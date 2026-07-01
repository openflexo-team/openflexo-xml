package org.openflexo.technologyadapter.xml;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.openflexo.foundation.ontology.DuplicateURIException;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.technologyadapter.xml.model.typed.XMLIndividual;
import org.openflexo.technologyadapter.xml.model.typed.XMLModel;

/**
 * URI processor that combines id, hash, structure, value and fuzzy matching.
 *
 * This is the recommended processor when the XML source is not controlled by the
 * application. It tries cheap/exact methods first, then falls back to similarity.
 */
@ModelEntity
@XMLElement
@ImplementationClass(CombinedXMLURIProcessor.CombinedXMLURIProcessorImpl.class)
public interface CombinedXMLURIProcessor extends XMLURIProcessor {

    public static abstract class CombinedXMLURIProcessorImpl
            extends AbstractSignatureXMLURIProcessor
            implements CombinedXMLURIProcessor {

        //Maybe as parameters?
        private static final double STRUCTURE_THRESHOLD = 0.85;
        private static final double VALUE_THRESHOLD = 0.82;
        private static final double FUZZY_THRESHOLD = 0.85;

        /**
         * Builds a URI containing all available signature information.
         *
         * @param model XML model containing the object
         * @param object object to identify
         * @return generated URI, or {@code null} when the object cannot be processed
         */
        @Override
        public String getURIForObject(XMLModel model, XMLObject object) {
            if (!(object instanceof XMLIndividual)) {
                logger.warning("Cannot build combined URI: object is not an XMLIndividual");
                return null;
            }

            if (getMappedXMLType() == null) {
                bindtypeURIToMappedType();
            }

            if (getMappedXMLType() == null || typeURI == null) {
                logger.warning("Cannot build combined URI: processor is not initialized");
                return null;
            }

            Map<String, String> parameters = buildCombinedParameters((XMLIndividual) object);

            String uri = buildURI(parameters);
            cacheObject(uri, object);

            return uri;
        }

        /**
         * Resolves an object by trying id, exact hash, structure, values and fuzzy matching.
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

            if (getMappedXMLType() == null) {
                logger.warning("Cannot resolve combined URI: mapped XML type is null");
                return null;
            }

            Map<String, List<String>> parameters = parseQueryMulti(URI.create(objectURI).getQuery());

            XMLIndividual resolved = resolveById(model, parameters);

            if (resolved != null) {
                cacheObject(objectURI, resolved);
                return resolved;
            }

            resolved = resolveByCombinedHash(model, parameters);

            if (resolved != null) {
                cacheObject(objectURI, resolved);
                return resolved;
            }

            resolved = resolveByStructure(model, parameters, STRUCTURE_THRESHOLD);

            if (resolved != null) {
                cacheObject(objectURI, resolved);
                return resolved;
            }

            resolved = resolveByValue(model, parameters, VALUE_THRESHOLD);

            if (resolved != null) {
                cacheObject(objectURI, resolved);
                return resolved;
            }

            resolved = resolveByFuzzySignature(model, parameters, FUZZY_THRESHOLD);

            if (resolved != null) {
                cacheObject(objectURI, resolved);
                return resolved;
            }

            logger.warning("Cannot resolve combined XML URI: " + objectURI);
            return null;
        }
    }
}