/**
 * 
 * Copyright (c) 2014, Openflexo
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

package org.openflexo.technologyadapter.xml.rm;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.OpenflexoRuntimeException;
import org.openflexo.foundation.resource.FileIODelegate;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.foundation.technologyadapter.FlexoMetaModelResource;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.technologyadapter.xml.XMLObject;
import org.openflexo.technologyadapter.xml.XMLTechnologyAdapter;
import org.openflexo.technologyadapter.xml.metamodel.XMLComplexType;
import org.openflexo.technologyadapter.xml.metamodel.XMLEnumerationType;
import org.openflexo.technologyadapter.xml.metamodel.XMLProperty;
import org.openflexo.technologyadapter.xml.metamodel.XMLProperty.XMLSupport;
import org.openflexo.technologyadapter.xml.metamodel.XMLSimpleType;
import org.openflexo.technologyadapter.xml.metamodel.XMLSimpleType.XMLSchemaPrimitiveType;
import org.openflexo.technologyadapter.xml.metamodel.XMLType;
import org.openflexo.technologyadapter.xml.metamodel.XSDMetaModel;
import org.openflexo.technologyadapter.xml.metamodel.XSDMetaModelFactory;
import org.openflexo.technologyadapter.xml.model.typed.XMLModel;
import org.openflexo.toolbox.JavaUtils;

import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSRestrictionSimpleType;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSType;

/**
 * 
 * A resource allowing access to an XSD document
 * 
 * @author sylvain, luka, xtof
 * 
 */
@ModelEntity
@ImplementationClass(XSDMetaModelResource.XSDMetaModelResourceImpl.class)
@XMLElement
public interface XSDMetaModelResource
		extends XMLResource<XSDMetaModel, XSDMetaModelFactory>, FlexoMetaModelResource<XMLModel, XSDMetaModel, XMLTechnologyAdapter> {

	@Override
	public XSDMetaModel getMetaModelData();

	public FileIODelegate getFileFlexoIODelegate();

	public static abstract class XSDMetaModelResourceImpl extends XMLResourceImpl<XSDMetaModel, XSDMetaModelFactory>
			implements XSDMetaModelResource {

		private static final Logger logger = Logger.getLogger(XSDMetaModelResourceImpl.class.getPackage().getName());

		/**
		 * An XSD metamodel is never written: Openflexo reads XSD files, it has no XSD writer. A change made in memory (by
		 * {@code AddXMLType}, say) is not saved, and a warning says so; the file is left as it is.
		 */
		@Override
		protected void performSave(boolean clearIsModified) throws SaveResourceException {
			if (resourceData != null && resourceData.isModified()) {
				logger.warning("XSD metamodels cannot be saved: " + getIODelegate().getSerializationArtefact()
						+ " is left unchanged, the changes made in Openflexo are not written");
			}
		}

		/**
		 * Not used: {@link #performSave(boolean)} never writes an XSD.
		 */
		@Override
		protected void write(OutputStream out) throws Exception {
			throw new UnsupportedOperationException("Saving an XSD metamodel is not supported");
		}

		private XSSchemaSet schemaSet;
		private XSDeclarationsFetcher fetcher;

		@Override
		public XSDMetaModel getMetaModelData() {
			try {
				return getResourceData();
			} catch (FileNotFoundException | ResourceLoadingCancelledException | OpenflexoRuntimeException e) {
				logger.log(Level.WARNING, "Cannot load " + this, e);
			}
			return null;
		}

		private void loadTypes() {
			// TODO if a declaration (base) type is derived, get the correct
			if (resourceData != null) {


				for (XSComplexType complexType : fetcher.getComplexTypes()) {
					ensureTypeExists(complexType);
				}

				for (XSElementDecl element : fetcher.getElementDecls()) {

					if (element.getType().isComplexType()) {

						// We browse all the element
						// If we find anonymous typesForURI, we use the element as the base of a XMLComplexType
						if (!element.getType().isGlobal()) {


							XMLComplexType returned = getFactory().makeComplexType(fetcher.getUri(element), element.getName(),
									resourceData);
							XSType basetype = element.getType().getBaseType();
							XMLType superType = ensureTypeExists(basetype);
							if (superType != null && superType != returned) {
								returned.setSuperType(superType);
								superType.setIsAbstract(true); // TODO : should we really do this ???
							}
						}

						else {
							// Register name of element as alternative name
							String uri = fetcher.getUri(element.getType());
							XMLComplexType existingType = (XMLComplexType) resourceData.getTypeFromURI(uri);
							if (!existingType.getName().equals(element.getName())) {
								// type name is different from element type, also register element name as alternative name
								// TODO : may we have multiple alternative names ??? Check this
								existingType.addToElementOccurences(element.getName());
							}

						}

					}
					else if (element.getType().isSimpleType()) {
						ensureTypeExists(element.getType());
					}
				}
			}
			else {
				logger.warning("Cannot load Types as MetaModel (resourceData) is NULL");
			}
		}

		private XMLType ensureTypeExists(XSType type) {
			String uri = fetcher.getUri(type);
			XMLType returned = resourceData.getTypeFromURI(uri);
			if (returned != null) {
				return returned;
			}
			return buildType(type);
		}

		private XMLType buildType(XSType type) {

			String uri = fetcher.getUri(type);
			if (type.isComplexType()) {
				XMLType returned = getFactory().makeComplexType(uri, type.getName(), resourceData);
				XSType basetype = type.getBaseType();
				XMLType superType = ensureTypeExists(basetype);
				if (superType != null && superType != returned) {
					returned.setSuperType(superType);
					superType.setIsAbstract(true); // TODO : should we really do this ???
				}
				return returned;
			}
			else if (type.isSimpleType()) {


				if (type instanceof XSRestrictionSimpleType) {
					XSRestrictionSimpleType restrictionType = (XSRestrictionSimpleType) type;
					List<XSFacet> facets = restrictionType.getDeclaredFacets("enumeration");
					if (facets.size() > 0) {
						// This is an enumeration
						XMLEnumerationType returned = getFactory().makeEnumerationType(uri, type.getName(), resourceData);
						for (XSFacet xsFacet : facets) {
							getFactory().makeEnumValue(xsFacet.getValue().toString(), returned);
						}
						return returned;
					}
					else {
						XMLSchemaPrimitiveType primitiveType = XMLSimpleType.getPrimitiveFromURI(uri);
						// If this does not map a primitive, consider it as a reference type
						if (primitiveType == null) {
							return getFactory().makeReferencedType(uri, type.getName(), ensureTypeExists(restrictionType.getBaseType()),
									resourceData);
						}
					}
				}

				return getFactory().makeSimpleType(uri, type.getName(), resourceData);

			}
			return null;
		}

		/**
		 * Internally called to create a {@link XMLProperty} from an element given a {@link XMLComplexType} owner
		 * 
		 * @param element
		 * @param type
		 * @param owner
		 * @return
		 */
		private XMLProperty<?, ?> createProperty(XSElementDecl element, XMLComplexType owner) {
			XSType elementType = element.getType();
			String propertyName = JavaUtils.getVariableName(element.getName());

			XMLType propertyType;

			if (!elementType.isGlobal()) {
				propertyType = retrieveTypeWithURI(fetcher.getUri(element));
			}
			else {
				propertyType = retrieveTypeWithURI(fetcher.getUri(elementType));
			}

			if (elementType.isComplexType()) {
				XSParticle particle = fetcher.getParticle(element);
				if (particle != null && particle.isRepeated()) {
					propertyName = propertyName + "s";
				}

				if (particle != null) {
					Integer lowerBound = 0;
					Integer upperBound = 1;
					if (particle.getMinOccurs() != null)
						lowerBound = particle.getMinOccurs().intValue();
					if (particle.getMaxOccurs() != null)
						upperBound = particle.getMaxOccurs().intValue();
					return resourceData.getModelFactory().makeObjectProperty(propertyName, (XMLComplexType) propertyType, lowerBound,
							upperBound, element.getName(), owner);
				}
				else {
					return resourceData.getModelFactory().makeSingleObjectProperty(propertyName, (XMLComplexType) propertyType, false,
							element.getName(), owner);
				}

			}
			else {
				XSParticle particle = fetcher.getParticle(element);
				if (particle != null && particle.isRepeated()) {
					propertyName = propertyName + "s";
				}

				if (particle != null) {
					Integer lowerBound = 0;
					Integer upperBound = 1;
					if (particle.getMinOccurs() != null)
						lowerBound = particle.getMinOccurs().intValue();
					if (particle.getMaxOccurs() != null)
						upperBound = particle.getMaxOccurs().intValue();
					return resourceData.getModelFactory().makeDataProperty(propertyName, (XMLSimpleType) propertyType, lowerBound,
							upperBound, XMLSupport.ELEMENT, element.getName(), owner);
				}
				else {
					return resourceData.getModelFactory().makeSingleDataProperty(propertyName, (XMLSimpleType) propertyType, false,
							XMLSupport.ELEMENT, element.getName(), owner);
				}

			}

		}

		private void loadProperties() {

			// We first iterate on all element declarations
			for (XSElementDecl element : fetcher.getElementDecls()) {
				String uri = fetcher.getUri(element);
				String ownerUri = fetcher.getOwnerURI(uri);
				if (ownerUri != null) {
					// In this case the element has a declared owner which can be retrieved from owner uri
					XMLType owner = resourceData.getTypeFromURI(ownerUri);
					if (owner instanceof XMLComplexType) {
						createProperty(element, (XMLComplexType) owner);
					}
				}
				else {
					// Otherwise we have to retrieve it from element uri (and we have to iterate on model group)
					XMLType owner = resourceData.getTypeFromURI(uri);
					if (owner instanceof XMLComplexType) {
						if (element.getType().isComplexType()) {
							XSComplexType xsType = (XSComplexType) element.getType();
							XSParticle particle = xsType.getContentType().asParticle();
							if (particle != null) {
								if (particle.getTerm().isModelGroup()) {
									XSModelGroup mg = particle.getTerm().asModelGroup();
									for (XSParticle childParticle : mg.getChildren()) {
										if (childParticle.getTerm() != null && childParticle.getTerm().isElementDecl()) {
											XSElementDecl childElement = childParticle.getTerm().asElementDecl();
											createProperty(childElement, (XMLComplexType) owner);

										}
									}
								}
							}
						}

					}
				}
			}

			// Attributes defined on a complexType
			for (XSAttributeDecl attribute : fetcher.getAttributeDecls()) {
				String uri = fetcher.getUri(attribute);

				String ownerUri = fetcher.getOwnerURI(uri);

				if (ownerUri != null) {
					XMLType owner = resourceData.getTypeFromURI(ownerUri);
					if (owner != null && owner instanceof XMLComplexType) {
						XMLType type;
						if (attribute.getType() instanceof XSRestrictionSimpleType) {
							XSRestrictionSimpleType rType = (XSRestrictionSimpleType) attribute.getType();
							// type = retrieveTypeWithURI("xs:" + rType.getName());
							type = retrieveTypeWithURI(fetcher.getUri(rType));
						}
						else {
							type = resourceData.getTypeFromURI(XSDMetaModel.ANY_TYPE_URI);
						}

						// TODO: better manage typesForURI
						resourceData.getModelFactory().makeSingleDataProperty(attribute.getName(), (XMLSimpleType) type, false,
								XMLSupport.ATTRIBUTE, attribute.getName(), (XMLComplexType) owner);

					}
					else {
						logger.warning("unable to find an owner type for attribute: " + uri);
					}
				}
				else {
					logger.warning("unable to find an owner for : " + uri);
				}
			}

		}

		private XMLType retrieveTypeWithURI(String typeURI) {
			XMLType returned = resourceData.getTypeFromURI(typeURI);
			if (returned == null) {
				// Maybe the type is to be found in simple typesForURI found in the fetcher
				for (XSSimpleType simpleType : fetcher.getSimpleTypes()) {
					if (fetcher.getUri(simpleType).equals(typeURI)) {
						// Found new type to add
						return buildType(simpleType);
					}

				}
				logger.warning("Not found type: " + typeURI);
			}
			return returned;
		}

		@Override
		protected XSDMetaModel performLoad() throws IOException, Exception {

			resourceData = getFactory().makeXSDMetaModel();
			resourceData.setResource(this);
			resourceData.setURI(this.getURI());

			notifyResourceWillLoad();

			schemaSet = XSOMUtils.read(getInputStream(), getURI());

			if (schemaSet != null) {
				fetcher = new XSDeclarationsFetcher();
				fetcher.fetch(schemaSet);
				loadTypes();
				loadProperties();
			}
			else {
				throw new IOException("Cannot parse the XSD stream " + getInputStream());
			}

			notifyResourceLoaded();

			return resourceData;
		}

		public XSDeclarationsFetcher getFetcher() {
			return fetcher;
		}

		@Override
		public Class<XSDMetaModel> getResourceDataClass() {
			return XSDMetaModel.class;
		}

		@Override
		public XMLObject findObject(String objectIdentifier, String userIdentifier) {
			XSDMetaModel metaModel;
			try {
				metaModel = getResourceData();

				// Easyest way
				String uri = metaModel.getURI() + "#" + objectIdentifier;
				XMLType returned = metaModel.getTypeFromURI(uri);

				return returned;

				/*OWLConcept<?> object = ontology.getOntologyObject(uri);
				if (object != null) {
					return object;
				}
				for (OWLClass owlClass : ontology.getClasses()) {
					if (owlClass.getName().equals(objectIdentifier)) {
						return owlClass;
					}
				}
				for (OWLObjectProperty owlProperty : ontology.getObjectProperties()) {
					if (owlProperty.getName().equals(objectIdentifier)) {
						return owlProperty;
					}
				}
				for (OWLDataProperty owlProperty : ontology.getDataProperties()) {
					if (owlProperty.getName().equals(objectIdentifier)) {
						return owlProperty;
					}
				}
				for (OWLIndividual owlIndividual : ontology.getIndividuals()) {
					if (owlIndividual.getName().equals(objectIdentifier)) {
						return owlIndividual;
					}
				}*/
			} catch (FileNotFoundException | ResourceLoadingCancelledException | OpenflexoRuntimeException e) {
				logger.log(Level.WARNING, "Cannot load " + this + " to find " + objectIdentifier, e);
			}

			return null;
		}

	}

}
