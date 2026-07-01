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
 * URI processor that uses only the source XML id.
 *
 * This is the fastest and simplest mode, but it only works while the source id
 * remains stable.
 */
@ModelEntity
@XMLElement
@ImplementationClass(IdXMLURIProcessor.IdXMLURIProcessorImpl.class)
public interface IdXMLURIProcessor extends XMLURIProcessor {

    public static abstract class IdXMLURIProcessorImpl
            extends AbstractSignatureXMLURIProcessor
            implements IdXMLURIProcessor {

        /**
         * Builds a URI for an XML object using the source id.
         *
         * @param model XML model containing the object
         * @param object object to identify
         * @return generated URI, or {@code null} when the object cannot be processed
         */
        @Override
        public String getURIForObject(XMLModel model, XMLObject object) {
            if (!(object instanceof XMLIndividual)) {
                logger.warning("Cannot build id URI: object is not an XMLIndividual");
                return null;
            }

            if (getMappedXMLType() == null) {
                bindtypeURIToMappedType();
            }

            if (getMappedXMLType() == null || typeURI == null) {
                logger.warning("Cannot build id URI: processor is not initialized");
                return null;
            }

            XMLSignature signature = XMLSignature.fromIndividual((XMLIndividual) object);

            Map<String, String> parameters = new LinkedHashMap<>();
            putIfNotEmpty(parameters, "id", signature.getIdValue());

            String uri = buildURI(parameters);
            cacheObject(uri, object);

            return uri;
        }

        /**
         * Resolves an object from a URI created by this processor.
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

            XMLIndividual resolved = resolveById(model, parameters);

            if (resolved != null) {
                cacheObject(objectURI, resolved);
            }

            return resolved;
        }
    }
}