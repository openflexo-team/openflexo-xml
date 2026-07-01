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
 * URI processor that resolves objects using a global similarity score.
 *
 * This is a fallback mode. It should only accept matches above a conservative
 * threshold because similar XML elements may be confused.
 */
@ModelEntity
@XMLElement
@ImplementationClass(FuzzyXMLURIProcessor.FuzzyXMLURIProcessorImpl.class)
public interface FuzzyXMLURIProcessor extends XMLURIProcessor {

    public static abstract class FuzzyXMLURIProcessorImpl
            extends AbstractSignatureXMLURIProcessor
            implements FuzzyXMLURIProcessor {

        //Maybe as parameters?
        private static final double FUZZY_THRESHOLD = 0.85;

        /**
         * Builds a URI using the compact signature.
         *
         * @param model XML model containing the object
         * @param object object to identify
         * @return generated URI, or {@code null} when the object cannot be processed
         */
        @Override
        public String getURIForObject(XMLModel model, XMLObject object) {
            if (!(object instanceof XMLIndividual)) {
                logger.warning("Cannot build fuzzy URI: object is not an XMLIndividual");
                return null;
            }

            if (getMappedXMLType() == null) {
                bindtypeURIToMappedType();
            }

            if (getMappedXMLType() == null || typeURI == null) {
                logger.warning("Cannot build fuzzy URI: processor is not initialized");
                return null;
            }

            XMLSignature signature = XMLSignature.fromIndividual((XMLIndividual) object);

            Map<String, String> parameters = new LinkedHashMap<>();
            putIfNotEmpty(parameters, "sig", signature.toCompactSignature());

            String uri = buildURI(parameters);
            cacheObject(uri, object);

            return uri;
        }

        /**
         * Resolves an object by global signature similarity.
         *
         * @param model XML model to search
         * @param objectURI URI to resolve
         * @return resolved object, or {@code null} when no confident match is found
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

            XMLIndividual resolved = resolveByFuzzySignature(model, parameters, FUZZY_THRESHOLD);

            if (resolved != null) {
                cacheObject(objectURI, resolved);
            }

            return resolved;
        }
    }
}