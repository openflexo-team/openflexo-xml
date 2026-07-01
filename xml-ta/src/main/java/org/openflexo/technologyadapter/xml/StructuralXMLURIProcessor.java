package org.openflexo.technologyadapter.xml;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openflexo.foundation.ontology.DuplicateURIException;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.technologyadapter.xml.model.typed.XMLIndividual;
import org.openflexo.technologyadapter.xml.model.typed.XMLModel;

/**
 * URI processor that resolves objects using type and property structure.
 *
 * This avoids relying on XPath. It is useful when the element moved or values
 * changed, but the general shape of the object stayed similar.
 */
@ModelEntity
@XMLElement
@ImplementationClass(StructuralXMLURIProcessor.StructuralXMLURIProcessorImpl.class)
public interface StructuralXMLURIProcessor extends XMLURIProcessor {

    public static abstract class StructuralXMLURIProcessorImpl
            extends AbstractSignatureXMLURIProcessor
            implements StructuralXMLURIProcessor {

        private static final double STRUCTURE_THRESHOLD = 0.85;

        /**
         * Builds a URI using type, structure and compact signature data.
         *
         * @param model XML model containing the object
         * @param object object to identify
         * @return generated URI, or {@code null} when the object cannot be processed
         */
        @Override
        public String getURIForObject(XMLModel model, XMLObject object) {
            if (!(object instanceof XMLIndividual)) {
                logger.warning("Cannot build structural URI: object is not an XMLIndividual");
                return null;
            }

            if (getMappedXMLType() == null) {
                bindtypeURIToMappedType();
            }

            if (getMappedXMLType() == null || typeURI == null) {
                logger.warning("Cannot build structural URI: processor is not initialized");
                return null;
            }

            XMLSignature signature = XMLSignature.fromIndividual((XMLIndividual) object);

            Map<String, String> parameters = new LinkedHashMap<>();
            putIfNotEmpty(parameters, "type", signature.getTypeURI());
            putIfNotEmpty(parameters, "struct", signature.getStructureSignature());
            putIfNotEmpty(parameters, "sh", signature.getStructureHash());
            putIfNotEmpty(parameters, "sig", signature.toCompactSignature());

            String uri = buildURI(parameters);
            cacheObject(uri, object);

            return uri;
        }

        /**
         * Resolves an object by structure similarity.
         *
         * @param model XML model to search
         * @param objectURI URI to resolve
         * @return resolved object, or {@code null} when no match is found
         */
        @Override
        public Object retrieveObjectWithURI(XMLModel model, String objectURI)
                throws DuplicateURIException {

            XMLObject cached = getCachedObject(objectURI);

            if (cached != null) {
                return cached;
            }

            if (getMappedXMLType() == null) {
                bindtypeURIToMappedType();
            }

            Map<String, List<String>> parameters = parseQueryMulti(URI.create(objectURI).getQuery());

            XMLIndividual resolved = resolveByStructure(model, parameters, STRUCTURE_THRESHOLD);

            if (resolved != null) {
                cacheObject(objectURI, resolved);
            }

            return resolved;
        }
    }
}