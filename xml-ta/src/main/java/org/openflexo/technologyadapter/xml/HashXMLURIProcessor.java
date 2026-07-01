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
 * URI processor that resolves objects through exact signature hashes.
 *
 * This is useful when the XML element did not change. If values or structure are
 * modified, the hash will no longer match.
 */
@ModelEntity
@XMLElement
@ImplementationClass(HashXMLURIProcessor.HashXMLURIProcessorImpl.class)
public interface HashXMLURIProcessor extends XMLURIProcessor {

    public static abstract class HashXMLURIProcessorImpl
            extends AbstractSignatureXMLURIProcessor
            implements HashXMLURIProcessor {

        /**
         * Builds a URI using exact structure, value and combined hashes.
         *
         * @param model XML model containing the object
         * @param object object to identify
         * @return generated URI, or {@code null} when the object cannot be processed
         */
        @Override
        public String getURIForObject(XMLModel model, XMLObject object) {
            if (!(object instanceof XMLIndividual)) {
                logger.warning("Cannot build hash URI: object is not an XMLIndividual");
                return null;
            }

            if (getMappedXMLType() == null) {
                bindtypeURIToMappedType();
            }

            if (getMappedXMLType() == null || typeURI == null) {
                logger.warning("Cannot build hash URI: processor is not initialized");
                return null;
            }

            XMLSignature signature = XMLSignature.fromIndividual((XMLIndividual) object);

            Map<String, String> parameters = new LinkedHashMap<>();
            putIfNotEmpty(parameters, "sh", signature.getStructureHash());
            putIfNotEmpty(parameters, "vh", signature.getValueHash());
            putIfNotEmpty(parameters, "ch", signature.getCombinedHash());

            String uri = buildURI(parameters);
            cacheObject(uri, object);

            return uri;
        }

        /**
         * Resolves an object by comparing exact combined signature hashes.
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

            XMLIndividual resolved = resolveByCombinedHash(model, parameters);

            if (resolved != null) {
                cacheObject(objectURI, resolved);
            }

            return resolved;
        }
    }
}