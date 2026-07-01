package org.openflexo.technologyadapter.xml;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.openflexo.technologyadapter.xml.metamodel.XMLDataProperty;
import org.openflexo.technologyadapter.xml.metamodel.XMLProperty;
import org.openflexo.technologyadapter.xml.model.typed.XMLIndividual;

/**
 * Builds a compact signature for an XML individual.
 *
 * The signature keeps a few stable-ish clues about the individual: its type, id,
 * data property names, normalized values and hashes. It is meant to help resolve
 * an object later when the original XML id is not enough.
 */
public class XMLSignature {

    private String typeURI;
    private String typeName;
    private String idValue;

    private final List<String> propertyNames = new ArrayList<>();
    private final List<String> normalizedValues = new ArrayList<>();

    private String structureSignature;
    private String normalizedText;
    private String structureHash;
    private String valueHash;
    private String combinedHash;

    /**
     * Creates a signature from the current values of an XML individual.
     *
     * @param individual individual to describe
     * @return signature built from type, id, property names and normalized values
     */
    public static XMLSignature fromIndividual(XMLIndividual individual) {
        XMLSignature signature = new XMLSignature();

        if (individual == null || individual.getType() == null) {
            signature.computeDerivedValues();
            return signature;
        }

        signature.typeURI = individual.getType().getURI();
        signature.typeName = extractLocalName(signature.typeURI);

        for (XMLProperty property : getPropertiesSafely(individual)) {
            if (!(property instanceof XMLDataProperty)) {
                continue;
            }

            XMLDataProperty dataProperty = (XMLDataProperty) property;
            String propertyName = dataProperty.getName();

            if (propertyName == null) {
                continue;
            }

            signature.propertyNames.add(propertyName);

            Object value = individual.getPropertyValue(dataProperty);

            if ("id".equalsIgnoreCase(propertyName) && value != null) {
                signature.idValue = String.valueOf(value);
            }

            if (value != null) {
                String normalizedValue = normalize(String.valueOf(value));

                if (!normalizedValue.isEmpty()) {
                    signature.normalizedValues.add(propertyName + "=" + normalizedValue);
                }
            }
        }

        signature.computeDerivedValues();
        return signature;
    }

    private static List<XMLProperty> getPropertiesSafely(XMLIndividual individual) {
        List<XMLProperty> result = new ArrayList<>();

        if (individual == null || individual.getType() == null) {
            return result;
        }

        Object type = individual.getType();

        String[] methodNames = {
                "getProperties",
                "getAllProperties",
                "getAccessibleProperties"
        };

        for (String methodName : methodNames) {
            try {
                Method method = type.getClass().getMethod(methodName);
                Object value = method.invoke(type);

                if (!(value instanceof Collection<?>)) {
                    continue;
                }

                for (Object item : (Collection<?>) value) {
                    if (item instanceof XMLProperty) {
                        result.add((XMLProperty) item);
                    }
                }

                if (!result.isEmpty()) {
                    return result;
                }
            } catch (Exception ignored) {
                // Try the next known accessor.
            }
        }

        return result;
    }

    private void computeDerivedValues() {
        Collections.sort(propertyNames);
        Collections.sort(normalizedValues);

        structureSignature = safe(typeName) + "(" + String.join(",", propertyNames) + ")";
        normalizedText = String.join(" ", normalizedValues);

        structureHash = sha256(structureSignature);
        valueHash = sha256(normalizedText);
        combinedHash = sha256(safe(typeURI) + "|" + safe(structureSignature) + "|" + safe(normalizedText));
    }

    /**
     * Encodes the signature as a URI-safe compact value.
     *
     * @return compact Base64 URL-safe signature
     */
    public String toCompactSignature() {
        String raw = safe(typeURI) + "|"
                + safe(typeName) + "|"
                + safe(idValue) + "|"
                + safe(structureSignature) + "|"
                + safe(normalizedText);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Rebuilds a signature from a compact URI value.
     *
     * @param compactSignature compact signature value
     * @return decoded signature, or an empty signature when decoding fails
     */
    public static XMLSignature fromCompactSignature(String compactSignature) {
        XMLSignature signature = new XMLSignature();

        if (compactSignature == null || compactSignature.isEmpty()) {
            signature.computeDerivedValues();
            return signature;
        }

        try {
            String decoded = new String(
                    Base64.getUrlDecoder().decode(compactSignature),
                    StandardCharsets.UTF_8
            );

            String[] parts = decoded.split("\\|", -1);

            signature.typeURI = parts.length > 0 ? parts[0] : "";
            signature.typeName = parts.length > 1 ? parts[1] : "";
            signature.idValue = parts.length > 2 ? parts[2] : "";
            signature.structureSignature = parts.length > 3 ? parts[3] : "";
            signature.normalizedText = parts.length > 4 ? parts[4] : "";

            if (signature.structureSignature != null) {
                int start = signature.structureSignature.indexOf('(');
                int end = signature.structureSignature.lastIndexOf(')');

                if (start >= 0 && end > start) {
                    String props = signature.structureSignature.substring(start + 1, end);

                    if (!props.trim().isEmpty()) {
                        signature.propertyNames.addAll(Arrays.asList(props.split(",")));
                    }
                }
            }

            if (signature.normalizedText != null && !signature.normalizedText.trim().isEmpty()) {
                signature.normalizedValues.addAll(Arrays.asList(signature.normalizedText.split(" ")));
            }

            signature.structureHash = sha256(signature.structureSignature);
            signature.valueHash = sha256(signature.normalizedText);
            signature.combinedHash = sha256(
                    safe(signature.typeURI) + "|" + safe(signature.structureSignature) + "|" + safe(signature.normalizedText)
            );

        } catch (Exception e) {
            signature.computeDerivedValues();
        }

        return signature;
    }

    private static String extractLocalName(String uri) {
        if (uri == null) {
            return "";
        }

        int hashIndex = uri.lastIndexOf('#');
        int slashIndex = uri.lastIndexOf('/');
        int index = Math.max(hashIndex, slashIndex);

        return index >= 0 ? uri.substring(index + 1) : uri;
    }

    /**
     * Normalizes text before comparison.
     *
     * @param value raw value
     * @return normalized value
     */
    public static String normalize(String value) {
        if (value == null) {
            return "";
        }

        String normalized = value.trim().replaceAll("\\s+", " ");
        normalized = Normalizer.normalize(normalized, Normalizer.Form.NFKC);
        return normalized.toLowerCase(Locale.ROOT);
    }

    /**
     * Computes a SHA-256 hash.
     *
     * @param value value to hash
     * @return prefixed SHA-256 hash
     */
    public static String sha256(String value) {
        if (value == null) {
            value = "";
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));

            StringBuilder builder = new StringBuilder(hash.length * 2);

            for (byte b : hash) {
                builder.append(String.format("%02x", b));
            }

            return "sha256:" + builder;

        } catch (Exception e) {
            throw new IllegalStateException("Unable to compute SHA-256 hash", e);
        }
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }

    public String getTypeURI() {
        return typeURI;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getIdValue() {
        return idValue;
    }

    public List<String> getPropertyNames() {
        return propertyNames;
    }

    public List<String> getNormalizedValues() {
        return normalizedValues;
    }

    public String getStructureSignature() {
        return structureSignature;
    }

    public String getNormalizedText() {
        return normalizedText;
    }

    public String getStructureHash() {
        return structureHash;
    }

    public String getValueHash() {
        return valueHash;
    }

    public String getCombinedHash() {
        return combinedHash;
    }
}