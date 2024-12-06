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
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.FileIODelegate;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.technologyadapter.FlexoMetaModelResource;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.technologyadapter.xml.XMLObject;
import org.openflexo.technologyadapter.xml.XMLTechnologyAdapter;
import org.openflexo.technologyadapter.xml.metamodel.XMLComplexType;
import org.openflexo.technologyadapter.xml.metamodel.XMLEnumerationType;
import org.openflexo.technologyadapter.xml.metamodel.XMLProperty;
import org.openflexo.technologyadapter.xml.metamodel.XMLType;
import org.openflexo.technologyadapter.xml.metamodel.XSDMetaModel;
import org.openflexo.technologyadapter.xml.metamodel.XSDMetaModelFactory;
import org.openflexo.technologyadapter.xml.model.typed.XMLModel;
import org.openflexo.toolbox.JavaUtils;

import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSRestrictionSimpleType;
import com.sun.xml.xsom.XSSchemaSet;
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

		private XSSchemaSet schemaSet;
		private XSDeclarationsFetcher fetcher;

		@Override
		public XSDMetaModel getMetaModelData() {
			try {
				return getResourceData();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (ResourceLoadingCancelledException e) {
				e.printStackTrace();
			} catch (FlexoException e) {
				e.printStackTrace();
			}
			return null;
		}

		private void loadTypes() {
			// TODO if a declaration (base) type is derived, get the correct
			if (resourceData != null) {

				/*System.out.println("------> ce qu'on obtient");
				for (XSComplexType complexType : fetcher.getComplexTypes()) {
					System.out.println(" > complexType : " + complexType + " uri=" + fetcher.getUri(complexType));
				}
				for (XSSimpleType simpleType : fetcher.getSimpleTypes()) {
					System.out.println(" > simpleType : " + simpleType + " uri=" + fetcher.getUri(simpleType));
				}
				for (XSElementDecl element : fetcher.getElementDecls()) {
					System.out.println(" > element : " + element);
				}
				System.out.println("<------ DONE");*/

				for (XSComplexType complexType : fetcher.getComplexTypes()) {
					ensureTypeExists(complexType);
				}

				for (XSElementDecl element : fetcher.getElementDecls()) {
					if (element.getType().isSimpleType()) {
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
				XSType btype = type.getBaseType();
				XMLType superType = ensureTypeExists(btype);
				if (superType != null && superType != returned) {
					returned.setSuperType(superType);
					superType.setIsAbstract(true); // TODO : should we really do this ???
				}
				return returned;
			}
			else if (type.isSimpleType()) {

				// System.out.println("SimpleType: " + type.getClass().getSimpleName() + " " + type.getName() + " uri=" + uri);

				if (type instanceof XSRestrictionSimpleType) {
					XSRestrictionSimpleType restrictionType = (XSRestrictionSimpleType) type;
					List<XSFacet> facets = restrictionType.getDeclaredFacets("enumeration");
					if (facets.size() > 0) {
						// This is an enumeration
						XMLEnumerationType returned = getFactory().makeEnumerationType(uri, type.getName(), resourceData);
						for (XSFacet xsFacet : facets) {
							System.out.println(" > " + xsFacet.getName() + "=" + xsFacet.getValue() + " fixed:" + xsFacet.isFixed());
							getFactory().makeEnumValue(xsFacet.getValue().toString(), returned);
						}
						return returned;
					}
				}

				return getFactory().makeSimpleType(uri, type.getName(), resourceData);

			}
			return null;
		}

		private void loadDataProperties() {

			for (XSElementDecl element : fetcher.getElementDecls()) {
				XSType elementType = element.getType();
				if (!elementType.isComplexType()) {
					String uri = fetcher.getUri(element);
					String ownerUri = fetcher.getOwnerURI(uri);
					if (ownerUri != null) {
						XMLType owner = resourceData.getTypeFromURI(ownerUri);
						if (owner != null && owner instanceof XMLComplexType) {
							((XMLComplexType) owner).createProperty(element.getName(),
									resourceData.getTypeFromURI(fetcher.getUri(elementType)));
						}
						else {

							System.out.println("Pour l'element " + element);

							logger.warning("unable to find an owner type for attribute: " + uri);
							logger.warning("ownerUri=" + ownerUri);
							logger.warning("owner=" + owner);

							System.out.println("tous les types que je connais :");
							for (XMLType xmlType : resourceData.getTypes()) {
								if (xmlType instanceof XMLComplexType) {
									System.out.println(" > " + xmlType.getURI());
								}
							}
							// System.exit(-1);
						}
					}
					else {
						logger.warning("unable to find an owner for : " + uri);
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
							type = resourceData.getTypeFromURI("xs:" + rType.getName());
						}
						else {
							type = resourceData.getTypeFromURI(XSDMetaModel.ANY_TYPE_URI);
						}
						// TODO: better manage types
						((XMLComplexType) owner).createProperty(attribute.getName(), type);
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

		private void loadObjectProperties() {

			for (XSElementDecl element : fetcher.getElementDecls()) {

				XSType elementType = element.getType();

				if (elementType.isComplexType()) {
					String uri = fetcher.getUri(element);
					XMLType t = resourceData.getTypeFromURI(fetcher.getUri(elementType));
					String name = element.getName();

					String ownerUri = fetcher.getOwnerURI(uri);

					if (ownerUri != null) {
						XMLType owner = resourceData.getTypeFromURI(ownerUri);
						if (owner != null && owner instanceof XMLComplexType) {

							String propertyName = JavaUtils.getVariableName(name);
							XSParticle particle = fetcher.getParticle(element);
							if (particle != null && particle.isRepeated()) {
								propertyName = propertyName + "s";
							}
							XMLProperty newProperty = ((XMLComplexType) owner).createProperty(propertyName, t);
							if (particle != null) {
								if (particle.getMinOccurs() != null)
									newProperty.setLowerBound(particle.getMinOccurs().intValue());
								if (particle.getMaxOccurs() != null)
									newProperty.setUpperBound(particle.getMaxOccurs().intValue());
								// System.out.println("" + particle + " " + particle.getTerm() + " of " + particle.getTerm().getClass()
								// + " minOccurs=" + particle.getMinOccurs() + " maxOccurs=" + particle.getMaxOccurs());
							}
						}
						else {
							logger.warning("unable to find an owner type for attribute: " + uri);
						}
					}
				}
			}

		}

		@Override
		protected XSDMetaModel performLoad() throws IOException, Exception {

			resourceData = getFactory().makeXSDMetaModel();
			resourceData.setResource(this);
			resourceData.setURI(this.getURI());

			notifyResourceWillLoad();

			schemaSet = XSOMUtils.read(getInputStream());

			if (schemaSet != null) {
				fetcher = new XSDeclarationsFetcher();
				fetcher.fetch(schemaSet);
				loadTypes();
				loadDataProperties();
				loadObjectProperties();
				// isLoaded = true;
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
				// System.out.println("Je cherche " + uri);
				XMLType returned = metaModel.getTypeFromURI(uri);
				// System.out.println("Je trouve " + returned);

				/*if (returned instanceof XMLComplexType) {
					XMLComplexType cType = (XMLComplexType) returned;
					for (XMLProperty xmlProperty : cType.getProperties()) {
						System.out.println(" > " + xmlProperty + " name=" + xmlProperty.getName());
					}
				}*/

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

			return null;
		}

	}

}
