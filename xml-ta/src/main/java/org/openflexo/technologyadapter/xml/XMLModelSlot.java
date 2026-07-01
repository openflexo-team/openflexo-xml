/**
 *
 * Copyright (c) 2014-2015, Openflexo
 *
 * This file is part of Xmlconnector, a component of the software infrastructure
 * developed at Openflexo.
 *
 *
 * Openflexo is dual-licensed under the European Union Public License (EUPL, either
 * version 1.1 of the License, or any later version ), which is available at
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * and the GNU General Public License (GPL, either version 3 of the License, or any
 * later version), which is available at http://www.gnu.org/licenses/gpl.html .
 *
 * You can redistribute it and/or modify under the terms of either of these licenses
 *
 * If you choose to redistribute it and/or modify under the terms of the GNU GPL, you
 * must include the following additional permission.
 *
 *          Additional permission under GNU GPL version 3 section 7
 *
 *          If you modify this Program, or any covered work, by linking or
 *          combining it with software containing parts covered by the terms
 *          of EPL 1.0, the licensors of this Program grant you additional permission
 *          to convey the resulting work. *
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 *
 * See http://www.openflexo.org/license.html for details.
 *
 *
 * Please contact Openflexo (openflexo-contacts@openflexo.org)
 * or visit www.openflexo.org if you need additional information.
 *
 */

package org.openflexo.technologyadapter.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.fml.FMLCompilationUnit;
import org.openflexo.foundation.fml.FlexoRole;
import org.openflexo.foundation.fml.annotations.DeclareActorReferences;
import org.openflexo.foundation.fml.annotations.DeclareEditionActions;
import org.openflexo.foundation.fml.annotations.DeclareFetchRequests;
import org.openflexo.foundation.fml.annotations.DeclareFlexoRoles;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.annotations.FMLAttribute;
import org.openflexo.foundation.ontology.DuplicateURIException;
import org.openflexo.foundation.ontology.fml.editionaction.SelectUniqueIndividual;
import org.openflexo.foundation.resource.FileSystemBasedResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.RepositoryFolder;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.foundation.technologyadapter.FlexoMetaModelResource;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TypeAwareModelSlot;
import org.openflexo.pamela.annotations.*;
import org.openflexo.pamela.annotations.Getter.Cardinality;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.technologyadapter.xml.XMLURIProcessor.XMLURIProcessorImpl;
import org.openflexo.technologyadapter.xml.fml.XMLActorReference;
import org.openflexo.technologyadapter.xml.fml.XMLIndividualRole;
import org.openflexo.technologyadapter.xml.fml.editionaction.AddXMLIndividual;
import org.openflexo.technologyadapter.xml.fml.editionaction.CreateXMLFileResource;
import org.openflexo.technologyadapter.xml.fml.editionaction.GetXMLDocumentRoot;
import org.openflexo.technologyadapter.xml.fml.editionaction.SelectXMLIndividual;
import org.openflexo.technologyadapter.xml.fml.editionaction.SetXMLDocumentRoot;
import org.openflexo.technologyadapter.xml.metamodel.*;
import org.openflexo.technologyadapter.xml.model.typed.XMLIndividual;
import org.openflexo.technologyadapter.xml.model.typed.XMLModel;
import org.openflexo.technologyadapter.xml.rm.TypedXMLResource;
import org.openflexo.technologyadapter.xml.rm.TypedXMLResourceFactory;
import org.openflexo.technologyadapter.xml.rm.XMLModelRepository;
import org.openflexo.technologyadapter.xml.rm.XSDMetaModelResource;

/**
 *
 * An XML ModelSlot used to edit an XML document conformant to a (XSD) MetaModel
 *
 * @author xtof
 *
 */
@DeclareFlexoRoles({ XMLIndividualRole.class })
@DeclareActorReferences({ XMLActorReference.class })
@DeclareEditionActions({ CreateXMLFileResource.class, AddXMLIndividual.class, GetXMLDocumentRoot.class, SetXMLDocumentRoot.class })
@DeclareFetchRequests({ SelectXMLIndividual.class, SelectUniqueIndividual.class })
@ModelEntity
@XMLElement
@ImplementationClass(XMLModelSlot.XMLModelSlotImpl.class)
@Imports({
		@Import(XMLURIProcessor.class),
		@Import(CombinedXMLURIProcessor.class),
		@Import(IdXMLURIProcessor.class),
		@Import(HashXMLURIProcessor.class),
		@Import(StructuralXMLURIProcessor.class),
		@Import(ValueXMLURIProcessor.class),
		@Import(FuzzyXMLURIProcessor.class)
})
@FML("XMLModelSlot")
public interface XMLModelSlot extends TypeAwareModelSlot<XMLModel, XSDMetaModel, TypedXMLResource>,
		AbstractXMLModelSlot<XMLModel, TypedXMLResource, XMLURIProcessor> {
	/*public enum URIProcessorType {
		DEFAULT,
		ID,
		HASH,
		STRUCTURAL,
		VALUE,
		FUZZY,
		COMBINED
	}*/
	@PropertyIdentifier(type = String.class)
	public static final String URI_PROCESSOR_TYPE_KEY = "uriProcessorType";

	@PropertyIdentifier(type = String.class)
	public static final String IDENTIFIER_PROPERTY_NAME_KEY = "identifierPropertyName";
	@Getter(value = URI_PROCESSOR_TYPE_KEY, defaultValue = "COMBINED")
	@XMLAttribute
	@FMLAttribute(value = URI_PROCESSOR_TYPE_KEY, required = false)
	public String getURIProcessorType();

	@Setter(URI_PROCESSOR_TYPE_KEY)
	public void setURIProcessorType(String processorType);

	@Getter(value = IDENTIFIER_PROPERTY_NAME_KEY)
	@XMLAttribute
	@FMLAttribute(value = IDENTIFIER_PROPERTY_NAME_KEY, required = false)
	public String getIdentifierPropertyName();

	@Setter(IDENTIFIER_PROPERTY_NAME_KEY)
	public void setIdentifierPropertyName(String propertyName);
	/*
    //not used yet
	@PropertyIdentifier(type = String.class, cardinality = Cardinality.LIST)
	public static final String IDENTIFIER_PROPERTY_NAMES_KEY = "identifierPropertyNames";

	@Getter(value = IDENTIFIER_PROPERTY_NAMES_KEY, cardinality = Cardinality.LIST)
	@XMLElement
	@FMLAttribute(value = IDENTIFIER_PROPERTY_NAMES_KEY, required = false)
	public List<String> getIdentifierPropertyNames();

	@Setter(IDENTIFIER_PROPERTY_NAMES_KEY)
	public void setIdentifierPropertyNames(List<String> propertyNames);

	@Adder(IDENTIFIER_PROPERTY_NAMES_KEY)
	public void addToIdentifierPropertyNames(String propertyName);

	@Remover(IDENTIFIER_PROPERTY_NAMES_KEY)
	public void removeFromIdentifierPropertyNames(String propertyName);
*/

	@PropertyIdentifier(type = XSDMetaModel.class)
	public static final String META_MODEL_KEY = "metaModel";

	@Override
	public XMLTechnologyAdapter getModelSlotTechnologyAdapter();

	@Getter(value = META_MODEL_KEY, ignoreType = true)
	@FMLAttribute(value = META_MODEL_KEY, required = true)
	public XSDMetaModel getMetaModel();

	@Setter(META_MODEL_KEY)
	public void setMetaModel(XSDMetaModel aMetaModel);

	// public static abstract class XMLModelSlotImpl extends AbstractXMLModelSlot.AbstractXMLModelSlotImpl<XMLURIProcessor> implements
	// XMLModelSlot {
	// TODO : check for multiple inheritance issues in PAMELA
	public static abstract class XMLModelSlotImpl extends TypeAwareModelSlotImpl<XMLModel, XSDMetaModel, TypedXMLResource>
			implements XMLModelSlot {

		private static final Logger logger = Logger.getLogger(XMLModelSlot.class.getPackage().getName());

		/* Used to process URIs for XML Objects */
		private List<XMLURIProcessor> uriProcessors;
		private Hashtable<String, XMLURIProcessor> uriProcessorsMap;

		public XMLModelSlotImpl() {
			super();
			if (uriProcessorsMap == null) {
				uriProcessorsMap = new Hashtable<>();
			}
			if (uriProcessors == null) {
				uriProcessors = new ArrayList<>();
			}

		}

		@Override
		public Class<? extends TechnologyAdapter> getTechnologyAdapterClass() {
			return XMLTechnologyAdapter.class;
		}

		@Override
		public XMLTechnologyAdapter getModelSlotTechnologyAdapter() {
			return (XMLTechnologyAdapter) super.getModelSlotTechnologyAdapter();
		}
		/*
		protected List<XMLDataProperty> getIdentifierProperties(XMLType xmlType) {
			List<XMLDataProperty> returned = new ArrayList<>();

			if (!(xmlType instanceof XMLComplexType)) {
				return returned;
			}

			XMLComplexType ct = (XMLComplexType) xmlType;
			List<String> propertyNames = getIdentifierPropertyNames();

			if (propertyNames == null) {
				return returned;
			}

			for (String propertyName : propertyNames) {
				XMLProperty prop = ct.getPropertyByName(propertyName);
				if (prop instanceof XMLDataProperty) {
					returned.add((XMLDataProperty) prop);
				}
			}

			return returned;
		}*/
		/*
		@Override
		public XMLURIProcessor createURIProcessor(XMLType aXmlType) {
			//Here creating a processor for each type, i want one for each individual, it's not an ontology
			logger.warning("URI --- "+aXmlType.getURI());
			XMLURIProcessor xsuriProc = getFMLModelFactory().newInstance(XMLURIProcessor.class);
			xsuriProc.setModelSlot(this);//First Model slot
			xsuriProc.setMappingStyle(AbstractXMLURIProcessor.MappingStyle.ATTRIBUTE_VALUE);
			xsuriProc.setMappedXMLType(aXmlType); //Map before the uri, it's used in the binding
			xsuriProc.setTypeURI(aXmlType.getURI());//Give type uri
			XMLComplexType ct = (XMLComplexType) aXmlType;
			//To be configured by type
			XMLProperty idProp = ct.getPropertyByName("Name");
			xsuriProc.setAttributeName("Name");
			xsuriProc.setBasePropertyForURI((XMLDataProperty) idProp);

			addToUriProcessors(xsuriProc);
			return xsuriProc;
		}*/
		@Override
		public XMLURIProcessor createURIProcessor(XMLType xmlType) {
			if (xmlType == null) {
				return null;
			}

			XMLURIProcessor processor = makeURIProcessor();

			processor.setModelSlot(this);
			processor.setMappedXMLType(xmlType);
			processor.setTypeURI(xmlType.getURI());

			configureURIProcessor(processor, xmlType);
			addToUriProcessors(processor);

			return processor;
		}

		/*=====================================================================================
		 * URI Accessors
		 */
		// TODO Manage the fact that URI May Change

		@Override
		public String getURIForObject(XMLModel model, Object o) {

			if (o instanceof XMLIndividual) {
				XMLType type = ((XMLIndividual) o).getType();
				logger.warning(type.toString());
				XMLURIProcessor p = ensureURIProcessorForType(type);
				if (p != null) {
					return p.getURIForObject(model, (XMLObject) o);
				}
				logger.warning("Unable to calculate URI as I have no XMLURIProcessor even after ensure");
			}
			else if (o instanceof XMLType) {
				return ((XMLType) o).getURI();
			}

			return null;
		}

		@Override
		public Object retrieveObjectWithURI(XMLModel model, String objectURI) {

			if (objectURI == null) {
				return null;
			}
			logger.warning("Map:" +uriProcessorsMap.toString());
			String typeUri = XMLURIProcessorImpl.retrieveTypeURI(model, objectURI);
			logger.warning("type uri:" +typeUri);
			XMLURIProcessor mapParams = uriProcessorsMap.get(XMLURIProcessorImpl.retrieveTypeURI(model, objectURI));
			if (mapParams == null) {
				// Look for a processor in superClasses
				XMLType aType = model.getMetaModel().getTypeFromURI(typeUri);
				//mapParams = retrieveURIProcessorForType(aType);
				mapParams = ensureURIProcessorForType(aType);
			}

			if (mapParams != null) {
				try {
					return mapParams.retrieveObjectWithURI(model, objectURI);
				} catch (DuplicateURIException e) {
					e.printStackTrace();
				}
			}

			return null;
		}

		@Override
		public XMLURIProcessor retrieveURIProcessorForType(XMLType aXmlType) {

			logger.info("SEARCHING for an uriProcessor for " + aXmlType.getURI());

			XMLURIProcessor mapParams = uriProcessorsMap.get(aXmlType.getURI());
			logger.warning(uriProcessors.toString());
			logger.warning(uriProcessorsMap.toString());
			if (mapParams == null && aXmlType.getSuperType() != null) {
				XMLType s = aXmlType.getSuperType();
				logger.info("SEARCHING for an uriProcessor for " + s.getURI());
				mapParams = retrieveURIProcessorForType(s);

				if (mapParams != null) {
					logger.info("UPDATING the MapUriProcessors for an uriProcessor for " + aXmlType.getURI());
					uriProcessorsMap.put(aXmlType.getURI(), mapParams);
				}
			}
			return mapParams;
		}
		/*
		private XMLURIProcessor makeURIProcessor() {
			URIProcessorType processorType = getURIProcessorType();

			if (processorType == null) {
				processorType = URIProcessorType.COMBINED;
			}

			switch (processorType) {
				case ID:
					return getFMLModelFactory().newInstance(IdXMLURIProcessor.class);

				case HASH:
					return getFMLModelFactory().newInstance(HashXMLURIProcessor.class);

				case STRUCTURAL:
					return getFMLModelFactory().newInstance(StructuralXMLURIProcessor.class);

				case VALUE:
					return getFMLModelFactory().newInstance(ValueXMLURIProcessor.class);

				case FUZZY:
					return getFMLModelFactory().newInstance(FuzzyXMLURIProcessor.class);

				case COMBINED:
					return getFMLModelFactory().newInstance(CombinedXMLURIProcessor.class);

				case DEFAULT:
				default:
					return getFMLModelFactory().newInstance(XMLURIProcessor.class);
			}
		}*/
		private XMLURIProcessor makeURIProcessor() {
			String processorType = getURIProcessorType();

			if (processorType == null || processorType.trim().isEmpty()) {
				processorType = "COMBINED";
			}

			processorType = processorType.trim().toUpperCase();

			switch (processorType) {
				case "ID":
					return getFMLModelFactory().newInstance(IdXMLURIProcessor.class);

				case "HASH":
					return getFMLModelFactory().newInstance(HashXMLURIProcessor.class);

				case "STRUCTURAL":
					return getFMLModelFactory().newInstance(StructuralXMLURIProcessor.class);

				case "VALUE":
					return getFMLModelFactory().newInstance(ValueXMLURIProcessor.class);

				case "FUZZY":
					return getFMLModelFactory().newInstance(FuzzyXMLURIProcessor.class);

				case "DEFAULT":
					return getFMLModelFactory().newInstance(XMLURIProcessor.class);

				case "COMBINED":
				default:
					return getFMLModelFactory().newInstance(CombinedXMLURIProcessor.class);
			}
		}
		private void configureURIProcessor(XMLURIProcessor processor, XMLType xmlType) {
			if (!(xmlType instanceof XMLComplexType)) {
				processor.setMappingStyle(AbstractXMLURIProcessor.MappingStyle.SINGLETON);
				return;
			}

			XMLComplexType complexType = (XMLComplexType) xmlType;
			XMLDataProperty identifierProperty = findIdentifierProperty(complexType);

			if (identifierProperty != null) {
				processor.setMappingStyle(AbstractXMLURIProcessor.MappingStyle.ATTRIBUTE_VALUE);
				processor.setAttributeName(identifierProperty.getName());
				processor.setBasePropertyForURI(identifierProperty);
			}
			else {
				processor.setMappingStyle(AbstractXMLURIProcessor.MappingStyle.SINGLETON);
			}
		}
		private XMLDataProperty findIdentifierProperty(XMLComplexType complexType) {
			if (complexType == null) {
				return null;
			}

			String configuredName = getIdentifierPropertyName();

			if (configuredName != null && !configuredName.trim().isEmpty()) {
				XMLProperty configuredProperty = complexType.getPropertyByName(configuredName);

				if (configuredProperty instanceof XMLDataProperty) {
					return (XMLDataProperty) configuredProperty;
				}
			}

			XMLProperty idProperty = complexType.getPropertyByName("id");

			if (idProperty instanceof XMLDataProperty) {
				return (XMLDataProperty) idProperty;
			}

			XMLProperty nameProperty = complexType.getPropertyByName("Name");

			if (nameProperty instanceof XMLDataProperty) {
				return (XMLDataProperty) nameProperty;
			}

			XMLProperty lowerNameProperty = complexType.getPropertyByName("name");

			if (lowerNameProperty instanceof XMLDataProperty) {
				return (XMLDataProperty) lowerNameProperty;
			}

			return null;
		}

		/*
		// add the attribute as parameter?
		public XMLURIProcessor ensureURIProcessorForType(XMLType aXmlType) {
			logger.warning("OK - "+aXmlType.getURI());
			if (aXmlType == null) return null;

			//exists
			XMLURIProcessor existing = retrieveURIProcessorForType(aXmlType);
			if (existing != null) {
				return existing;
			}
			if(existing!=null) logger.warning("existing - "+existing.toString());
			else logger.warning("Not existing"+aXmlType.getURI());

			// create processor automatically
			logger.info("No XMLURIProcessor for type " + aXmlType.getURI()
					+ " , creating one automatically");

			XMLURIProcessor proc = createURIProcessor(aXmlType);
			proc.setTypeURI(aXmlType.getURI());
			proc.setMappedXMLType(aXmlType);

			if (aXmlType instanceof XMLComplexType) {
				XMLComplexType ct = (XMLComplexType) aXmlType;

				//if there's an id attribute, use ATTRIBUTE_VALUE, else SINGLETON
				//To be configured by type
				XMLProperty idProp = ct.getPropertyByName("Name");
				if (idProp instanceof XMLDataProperty) {
					proc.setMappingStyle(AbstractXMLURIProcessor.MappingStyle.ATTRIBUTE_VALUE);
					proc.setAttributeName("Name");
					proc.setBasePropertyForURI((XMLDataProperty) idProp);
				} else {
					proc.setMappingStyle(AbstractXMLURIProcessor.MappingStyle.SINGLETON);
				}
			} else {
				proc.setMappingStyle(AbstractXMLURIProcessor.MappingStyle.SINGLETON);
			}

			// Make sure it's indexed in list + map
			addToUriProcessors(proc);

			return proc;
		}*/
		public XMLURIProcessor ensureURIProcessorForType(XMLType xmlType) {
			if (xmlType == null) {
				return null;
			}

			XMLURIProcessor existing = retrieveURIProcessorForType(xmlType);

			if (existing != null) {
				return existing;
			}

			logger.info("No XMLURIProcessor for type " + xmlType.getURI()
					+ ", creating one automatically");

			return createURIProcessor(xmlType);
		}
		// ==========================================================================
		// ============================== uriProcessors Map ===================
		// ==========================================================================

		public void setUriProcessors(List<XMLURIProcessor> uriProcessingParameters) {
			this.uriProcessors = uriProcessingParameters;
		}

		public void updateURIMapForProcessor(XMLURIProcessor xmluriProc) {
			String uri = xmluriProc.getTypeURI();
			if (uri != null) {
				for (String k : uriProcessorsMap.keySet()) {
					XMLURIProcessor p = uriProcessorsMap.get(k);
					if (p.equals(xmluriProc)) {
						uriProcessorsMap.remove(k);
					}
				}
				uriProcessorsMap.put(uri, xmluriProc);
			}
		}
		public void addToUriProcessors(XMLURIProcessor processor) {
			if (processor == null) {
				return;
			}

			processor.setModelSlot(this);

			if (!uriProcessors.contains(processor)) {
				uriProcessors.add(processor);
			}

			String uri = processor.getTypeURI();

			if (uri != null) {
				uriProcessorsMap.put(uri, processor);
			}
		}
		/*
		public void addToUriProcessors(XMLURIProcessor xmluriProc) {
			xmluriProc.setModelSlot(this);
			uriProcessors.add(xmluriProc);
			logger.warning("Here !" +xmluriProc.getTypeURI());
			uriProcessorsMap.put(xmluriProc.getTypeURI().toString(), xmluriProc);
			logger.warning("Map added "+uriProcessorsMap);
		}*/

		public void removeFromUriProcessors(XMLURIProcessor xmluriProc) {
			String uri = xmluriProc.getTypeURI();
			if (uri != null) {
				for (String k : uriProcessorsMap.keySet()) {
					XMLURIProcessor p = uriProcessorsMap.get(k);
					if (p.equals(xmluriProc)) {
						uriProcessorsMap.remove(k);
					}
				}
				uriProcessors.remove(xmluriProc);
				xmluriProc.reset();
			}
		}

		// Do not use this since not efficient, used in deserialization only
		@Override
		public List<XMLURIProcessor> getUriProcessorsList() {
			return uriProcessors;
		}

		@Override
		public void setUriProcessorsList(List<XMLURIProcessor> uriProcList) {
			this.uriProcessors = new ArrayList<>();
			this.uriProcessorsMap = new Hashtable<>();
			logger.info("setUriProcessorsList called with size=" + (uriProcList == null ? 0 : uriProcList.size()));
			if (uriProcList != null) {
				for (XMLURIProcessor p : uriProcList) {
					addToUriProcessors(p);   // sets modelSlot + updates the map
				}
			}
		}

		@Override
		public void addToUriProcessorsList(XMLURIProcessor xmluriProc) {
			addToUriProcessors(xmluriProc);
		}

		@Override
		public void removeFromUriProcessorsList(XMLURIProcessor xmluriProc) {
			removeFromUriProcessors(xmluriProc);
		}

		@Override
		public boolean isStrictMetaModelling() {
			return true;
		}

		@Override
		public XSDMetaModel getMetaModel() {
			if (getMetaModelResource() != null) {
				return getMetaModelResource().getMetaModelData();
			}
			return null;
		}

		@Override
		public void setMetaModel(XSDMetaModel aMetaModel) {
			setMetaModelResource(aMetaModel != null ? (FlexoMetaModelResource<XMLModel, XSDMetaModel, ?>) aMetaModel.getResource() : null);
		}

		@Override
		public void setMetaModelResource(FlexoMetaModelResource<XMLModel, XSDMetaModel, ?> metaModelResource) {
			super.setMetaModelResource(metaModelResource);
			try {
				if (metaModelResource != null) {
					getPropertyChangeSupport().firePropertyChange(META_MODEL_KEY, null, metaModelResource.getResourceData());
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ResourceLoadingCancelledException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FlexoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void handleRequiredImports(FMLCompilationUnit compilationUnit) {
			super.handleRequiredImports(compilationUnit);
			if (compilationUnit != null && getMetaModel() != null) {
				compilationUnit.ensureResourceImport(getMetaModel(), false);
			}
		}

		@Override
		public TypedXMLResource createProjectSpecificEmptyModel(FlexoResourceCenter<?> rc, String filename, String relativePath,
				String modelUri, FlexoMetaModelResource<XMLModel, XSDMetaModel, ?> metaModelResource) {

			XMLTechnologyAdapter xmlTA = getServiceManager().getTechnologyAdapterService().getTechnologyAdapter(XMLTechnologyAdapter.class);
			TypedXMLResourceFactory factory = getModelSlotTechnologyAdapter().getXMLFileResourceFactory();

			Object serializationArtefact = xmlTA.retrieveResourceSerializationArtefact(rc, filename, relativePath,
					TypedXMLResourceFactory.XML_EXTENSION);

			TypedXMLResource newXMLFileResource;
			try {
				newXMLFileResource = factory.makeResource(serializationArtefact, (FlexoResourceCenter) rc, filename, modelUri, true);
				newXMLFileResource.setMetaModelResource((FlexoMetaModelResource) metaModelResource);
				return newXMLFileResource;
			} catch (SaveResourceException e) {
				e.printStackTrace();
			} catch (ModelDefinitionException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public TypedXMLResource createSharedEmptyModel(FlexoResourceCenter<?> resourceCenter, String relativePath, String filename,
				String modelUri, FlexoMetaModelResource<XMLModel, XSDMetaModel, ?> metaModelResource) {

			// Unused XMLFileResource returned = null;

			if (resourceCenter instanceof FileSystemBasedResourceCenter) {
				File xmlFile = new File(((FileSystemBasedResourceCenter) resourceCenter).getRootDirectory(),
						relativePath + System.getProperty("file.separator") + filename);

				modelUri = xmlFile.toURI().toString();

				XMLModelRepository<File> modelRepository = (XMLModelRepository<File>) getModelSlotTechnologyAdapter()
						.getXMLModelRepository(resourceCenter);

				try {
					return createEmptyXMLFileResource(xmlFile, modelRepository, (XSDMetaModelResource) metaModelResource);
				} catch (SaveResourceException e) {
					e.printStackTrace();
				} catch (ModelDefinitionException e) {
					e.printStackTrace();
				}
			}
			return null;

		}

		private TypedXMLResource createEmptyXMLFileResource(File xmlFile, XMLModelRepository<File> modelRepository,
				XSDMetaModelResource metaModelResource) throws SaveResourceException, ModelDefinitionException {

			XMLTechnologyAdapter ta = getModelSlotTechnologyAdapter();
			TypedXMLResourceFactory xmlFileResourceFactory = ta.getXMLFileResourceFactory();

			TypedXMLResource returned = xmlFileResourceFactory.makeResource(xmlFile, modelRepository.getResourceCenter(), true);

			// XMLFileResource returned = XMLFileResourceImpl.makeXMLFileResource(xmlFile,
			// (XMLTechnologyContextManager) this.getModelSlotTechnologyAdapter().getTechnologyContextManager(),
			// modelRepository.getResourceCenter());

			RepositoryFolder<TypedXMLResource, File> folder;
			try {
				folder = modelRepository.getParentRepositoryFolder(xmlFile, true);
				if (folder != null) {
					modelRepository.registerResource(returned, folder);
				}
				else {
					modelRepository.registerResource(returned);
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			if (metaModelResource != null && returned != null) {
				returned.setMetaModelResource(metaModelResource);
				returned.getModel().setMetaModel(metaModelResource.getMetaModelData());
			}

			return returned;

		}

		@Override
		public Type getType() {
			return XMLModel.class;
		}

		@Override
		public <PR extends FlexoRole<?>> String defaultFlexoRoleName(Class<PR> flexoRoleClass) {
			return flexoRoleClass.getSimpleName();
		}

		@Override
		public String getTypeDescription() {
			return "xml";
		}
	}

}
