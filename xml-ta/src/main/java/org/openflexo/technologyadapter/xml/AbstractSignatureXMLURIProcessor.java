package org.openflexo.technologyadapter.xml;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.openflexo.technologyadapter.xml.metamodel.XMLDataProperty;
import org.openflexo.technologyadapter.xml.metamodel.XMLType;
import org.openflexo.technologyadapter.xml.model.typed.XMLIndividual;
import org.openflexo.technologyadapter.xml.model.typed.XMLModel;

/**
 * Base implementation for URI processors that store and resolve XML signatures.
 *
 * Subclasses choose which part of the signature they use: id, hash, structure,
 * values, fuzzy matching, or a combination of all of them.
 */
public abstract class AbstractSignatureXMLURIProcessor
        extends XMLURIProcessor.XMLURIProcessorImpl {

    protected static final Logger logger =
            Logger.getLogger(XMLURIProcessor.class.getPackage().getName());

    private final Map<String, XMLObject> uriResolutionCache = new HashMap<>();

    protected final JaroWinklerSimilarity jaroWinkler = new JaroWinklerSimilarity();

    public AbstractSignatureXMLURIProcessor() {
        super();
    }

    public AbstractSignatureXMLURIProcessor(String typeURI) {
        super(typeURI);
    }

    /**
     * Returns a previously resolved object for the given URI.
     *
     * @param uri full object URI
     * @return cached object, or {@code null} when not cached
     */
    protected XMLObject getCachedObject(String uri) {
        return uri != null ? uriResolutionCache.get(uri) : null;
    }

    /**
     * Stores a resolved object in the local processor cache.
     *
     * @param uri full object URI
     * @param object object resolved from that URI
     */
    protected void cacheObject(String uri, XMLObject object) {
        if (uri != null && object != null) {
            uriResolutionCache.put(uri, object);
        }
    }

    /**
     * Builds the combined URI parameter set from an XML individual.
     *
     * @param individual object to describe
     * @return query parameters containing id, hashes, structure, values and compact signature
     */
    protected Map<String, String> buildCombinedParameters(XMLIndividual individual) {
        Map<String, String> parameters = new LinkedHashMap<>();
        XMLSignature signature = XMLSignature.fromIndividual(individual);

        putIfNotEmpty(parameters, "id", signature.getIdValue());
        putIfNotEmpty(parameters, "type", signature.getTypeURI());
        putIfNotEmpty(parameters, "struct", signature.getStructureSignature());
        putIfNotEmpty(parameters, "txt", signature.getNormalizedText());
        putIfNotEmpty(parameters, "sh", signature.getStructureHash());
        putIfNotEmpty(parameters, "vh", signature.getValueHash());
        putIfNotEmpty(parameters, "ch", signature.getCombinedHash());
        putIfNotEmpty(parameters, "sig", signature.toCompactSignature());

        return parameters;
    }

    /**
     * Builds the final URI using the processor type URI and query parameters.
     *
     * @param parameters query parameters to append
     * @return full URI, or {@code null} when it cannot be built
     */
    protected String buildURI(Map<String, String> parameters) {
        if (typeURI == null) {
            logger.warning("Cannot build XML URI: typeURI is null");
            return null;
        }

        try {
            return new URI(
                    typeURI.getScheme(),
                    typeURI.getAuthority(),
                    typeURI.getPath(),
                    buildQuery(parameters),
                    typeURI.getFragment()
            ).toString();

        } catch (URISyntaxException e) {
            logger.warning("Cannot build XML URI: " + e.getMessage());
            return null;
        }
    }

    /**
     * Resolves an individual by exact source id.
     *
     * @param model model to search
     * @param parameters parsed URI query parameters
     * @return matching individual, or {@code null}
     */
    protected XMLIndividual resolveById(XMLModel model, Map<String, List<String>> parameters) {
        String id = first(parameters.get("id"));

        if (id == null || id.isEmpty()) {
            return null;
        }

        XMLDataProperty property = getBasePropertyForURI();

        if (property == null) {
            return null;
        }

        String expected = normalize(id);

        for (XMLIndividual candidate : getCandidates(model)) {
            Object value = candidate.getPropertyValue(property);

            if (value != null && normalize(String.valueOf(value)).equals(expected)) {
                return candidate;
            }
        }

        return null;
    }

    /**
     * Resolves an individual by exact combined signature hash.
     *
     * @param model model to search
     * @param parameters parsed URI query parameters
     * @return matching individual, or {@code null}
     */
    protected XMLIndividual resolveByCombinedHash(XMLModel model, Map<String, List<String>> parameters) {
        String expectedHash = first(parameters.get("ch"));

        if (expectedHash == null || expectedHash.isEmpty()) {
            return null;
        }

        for (XMLIndividual candidate : getCandidates(model)) {
            XMLSignature signature = XMLSignature.fromIndividual(candidate);

            if (expectedHash.equals(signature.getCombinedHash())) {
                return candidate;
            }
        }

        return null;
    }

    /**
     * Resolves an individual by structure similarity.
     *
     * @param model model to search
     * @param parameters parsed URI query parameters
     * @param threshold minimum accepted similarity score
     * @return best matching individual, or {@code null}
     */
    protected XMLIndividual resolveByStructure(
            XMLModel model,
            Map<String, List<String>> parameters,
            double threshold
    ) {
        XMLSignature expected = signatureFromParameters(parameters);

        if (expected == null) {
            return null;
        }

        XMLIndividual best = null;
        double bestScore = 0.0;

        for (XMLIndividual candidate : getCandidates(model)) {
            XMLSignature candidateSignature = XMLSignature.fromIndividual(candidate);
            double score = structureSimilarity(expected, candidateSignature);

            if (score > bestScore) {
                bestScore = score;
                best = candidate;
            }
        }

        return bestScore >= threshold ? best : null;
    }

    /**
     * Resolves an individual by normalized value similarity.
     *
     * @param model model to search
     * @param parameters parsed URI query parameters
     * @param threshold minimum accepted similarity score
     * @return best matching individual, or {@code null}
     */
    protected XMLIndividual resolveByValue(
            XMLModel model,
            Map<String, List<String>> parameters,
            double threshold
    ) {
        XMLSignature expected = signatureFromParameters(parameters);

        if (expected == null) {
            return null;
        }

        XMLIndividual best = null;
        double bestScore = 0.0;

        for (XMLIndividual candidate : getCandidates(model)) {
            XMLSignature candidateSignature = XMLSignature.fromIndividual(candidate);
            double score = valueSimilarity(expected, candidateSignature);

            if (score > bestScore) {
                bestScore = score;
                best = candidate;
            }
        }

        return bestScore >= threshold ? best : null;
    }

    /**
     * Resolves an individual using the global signature similarity score.
     *
     * @param model model to search
     * @param parameters parsed URI query parameters
     * @param threshold minimum accepted similarity score
     * @return best matching individual, or {@code null}
     */
    protected XMLIndividual resolveByFuzzySignature(
            XMLModel model,
            Map<String, List<String>> parameters,
            double threshold
    ) {
        XMLSignature expected = signatureFromParameters(parameters);

        if (expected == null) {
            return null;
        }

        XMLIndividual best = null;
        double bestScore = 0.0;

        for (XMLIndividual candidate : getCandidates(model)) {
            XMLSignature candidateSignature = XMLSignature.fromIndividual(candidate);
            double score = globalSimilarity(expected, candidateSignature);

            if (score > bestScore) {
                bestScore = score;
                best = candidate;
            }
        }

        if (best != null && bestScore >= threshold) {
            logger.info("Resolved XML URI by fuzzy signature. Score=" + bestScore);
            return best;
        }

        return null;
    }

    protected XMLSignature signatureFromParameters(Map<String, List<String>> parameters) {
        String compactSignature = first(parameters.get("sig"));

        if (compactSignature == null || compactSignature.isEmpty()) {
            return null;
        }

        return XMLSignature.fromCompactSignature(compactSignature);
    }

    protected List<XMLIndividual> getCandidates(XMLModel model) {
        XMLType type = getMappedXMLType();

        if (model == null || type == null) {
            return Collections.emptyList();
        }

        return (List<XMLIndividual>) model.getIndividualsOfType(type);
    }

    protected String buildQuery(Map<String, String> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return null;
        }

        StringBuilder builder = new StringBuilder();

        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) {
                continue;
            }

            if (builder.length() > 0) {
                builder.append("&");
            }

            builder.append(entry.getKey())
                    .append("=")
                    .append(urlEncode(entry.getValue()));
        }

        return builder.length() == 0 ? null : builder.toString();
    }

    protected Map<String, List<String>> parseQueryMulti(String query) {
        Map<String, List<String>> result = new LinkedHashMap<>();

        if (query == null || query.isEmpty()) {
            return result;
        }

        for (String part : query.split("&")) {
            int index = part.indexOf('=');

            String key = index >= 0 ? part.substring(0, index) : part;
            String value = index >= 0 ? part.substring(index + 1) : "";

            result.computeIfAbsent(key, ignored -> new ArrayList<>()).add(urlDecode(value));
        }

        return result;
    }

    protected String first(List<String> values) {
        return values == null || values.isEmpty() ? null : values.get(0);
    }

    protected void putIfNotEmpty(Map<String, String> parameters, String key, String value) {
        if (value != null && !value.isEmpty()) {
            parameters.put(key, value);
        }
    }

    protected String normalize(String value) {
        if (value == null) {
            return "";
        }

        String normalized = value.trim().replaceAll("\\s+", " ");
        normalized = Normalizer.normalize(normalized, Normalizer.Form.NFKC);

        return normalized.toLowerCase(Locale.ROOT);
    }

    protected String urlEncode(String value) {
        if (value == null) {
            return "";
        }

        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }

    protected String urlDecode(String value) {
        if (value == null) {
            return "";
        }

        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }

    protected double exactSimilarity(String a, String b) {
        String x = normalize(a);
        String y = normalize(b);

        if (x.isEmpty() || y.isEmpty()) {
            return 0.0;
        }

        return x.equals(y) ? 1.0 : 0.0;
    }

    protected double textSimilarity(String a, String b) {
        String x = normalize(a);
        String y = normalize(b);

        if (x.isEmpty() || y.isEmpty()) {
            return 0.0;
        }

        return jaroWinkler.apply(x, y);
    }

    protected double jaccardSimilarity(Set<String> a, Set<String> b) {
        if (a == null || b == null || a.isEmpty() || b.isEmpty()) {
            return 0.0;
        }

        Set<String> intersection = new HashSet<>(a);
        intersection.retainAll(b);

        Set<String> union = new HashSet<>(a);
        union.addAll(b);

        return union.isEmpty() ? 0.0 : ((double) intersection.size()) / union.size();
    }

    protected double structureSimilarity(XMLSignature expected, XMLSignature candidate) {
        if (expected == null || candidate == null) {
            return 0.0;
        }

        double typeScore = exactSimilarity(expected.getTypeURI(), candidate.getTypeURI());

        double propertyScore = jaccardSimilarity(
                new HashSet<>(expected.getPropertyNames()),
                new HashSet<>(candidate.getPropertyNames())
        );

        return 0.50 * typeScore + 0.50 * propertyScore;
    }

    protected double valueSimilarity(XMLSignature expected, XMLSignature candidate) {
        if (expected == null || candidate == null) {
            return 0.0;
        }

        return textSimilarity(expected.getNormalizedText(), candidate.getNormalizedText());
    }

    protected double globalSimilarity(XMLSignature expected, XMLSignature candidate) {
        if (expected == null || candidate == null) {
            return 0.0;
        }

        double typeScore = exactSimilarity(expected.getTypeURI(), candidate.getTypeURI());
        double structureScore = structureSimilarity(expected, candidate);
        double valueScore = valueSimilarity(expected, candidate);
        double hashScore = exactSimilarity(expected.getCombinedHash(), candidate.getCombinedHash());

        return 0.15 * typeScore
                + 0.25 * structureScore
                + 0.45 * valueScore
                + 0.15 * hashScore;
    }
}