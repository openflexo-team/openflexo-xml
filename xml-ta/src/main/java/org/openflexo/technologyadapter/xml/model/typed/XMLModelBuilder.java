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

package org.openflexo.technologyadapter.xml.model.typed;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

import org.openflexo.technologyadapter.xml.metamodel.XMLComplexType;
import org.openflexo.technologyadapter.xml.metamodel.XMLProperty;
import org.openflexo.technologyadapter.xml.metamodel.XMLType;
import org.openflexo.technologyadapter.xml.metamodel.XSDMetaModel;
import org.openflexo.xml.SaxBasedObjectGraphFactory;
import org.openflexo.xml.XMLReaderSAXHandler;
import org.xml.sax.SAXException;

/**
 * A builder for a {@link XMLModel} (sax-based)
 */
public class XMLModelBuilder extends SaxBasedObjectGraphFactory {

	private XMLModel model = null;

	@Override
	public Object getInstanceOf(Type aType, String name) {

		if (aType instanceof XMLComplexType) {

			System.out.println("Make XMLIndividual for type" + aType);

			XMLIndividual returned = model.addNewIndividual((XMLComplexType) aType);

			return returned;
		}

		return null;
	}

	@Override
	public XMLType getTypeForObject(String typeURI, Object container, String objectName) {

		XSDMetaModel mm = model.getMetaModel();
		XMLType returned = null;
		if (mm != null) {
			returned = mm.getTypeFromURI(typeURI);
		}

		if (returned == null) {
			System.out.println("Not found : " + typeURI);
			returned = mm.getTypeFromContextualURI(typeURI);
			if (returned == null) {
				System.out.println("Still not found : " + typeURI);
			}
		}

		// Try to match as local uri
		if (container instanceof XMLIndividual) {
			XMLType parentType = ((XMLIndividual) container).getType();
			if (returned == null && !typeURI.startsWith(parentType.getFullyQualifiedName())) {
				returned = mm.getTypeFromURI(parentType.getFullyQualifiedName() + "#" + typeURI);
			}
		}

		// Create the type if it does not exist and that we can!!
		if (!mm.isReadOnly() && returned == null) {
			if (container instanceof XMLIndividual) {
				XMLType parentType = ((XMLIndividual) container).getType();
				returned = mm.getModelFactory().makeComplexType(parentType.getFullyQualifiedName() + "#" + objectName, objectName, mm);
			}
			else {
				returned = mm.getModelFactory().makeComplexType(mm.getURI() + "#" + objectName, objectName, mm);
			}
		}

		System.out.println(
				"getTypeForObject() ??? " + typeURI + " container: " + container + " objectName=" + objectName + " returns " + returned);

		return returned;
	}

	@Override
	public Object deserialize(String input) throws IOException {
		if (model != null) {

			try {
				saxParser.parse(input, handler);
			} catch (SAXException e) {
				LOGGER.warning("Cannot parse document: " + e.getMessage());
				throw new IOException(e.getMessage());
			}
			return this.model;
		}
		LOGGER.warning("Context is not set for parsing, aborting");
		return null;
	}

	@Override
	public Object deserialize(InputStream input) throws IOException {
		if (model != null) {

			try {
				saxParser.parse(input, handler);
			} catch (SAXException e) {
				LOGGER.warning("Cannot parse document: " + e.getMessage());
				throw new IOException(e.getMessage());
			}
			return this.model;

		}
		LOGGER.warning("Context is not set for parsing, aborting");
		return null;
	}

	@Override
	public void addToRootNodes(Object anObject) {
		model.setRoot((XMLIndividual) anObject);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setContextProperty(String propertyName, Object value) {
		if (propertyName.equals(XMLReaderSAXHandler.NAMESPACE_Property)) {
			model.setNamespace(((List<String>) value).get(0), ((List<String>) value).get(1));
		}

	}

	@Override
	public void setContext(Object objectGraph) {
		model = (XMLModel) objectGraph;

	}

	@Override
	public void resetContext() {
		model = null;
	}

	@Override
	public boolean objectHasAttributeNamed(Object object, String propertyName) {

		// System.out.println("***** objectHasAttributeNamed??? object=" + object + " propertyName=" + propertyName);

		if (object instanceof XMLIndividual) {
			return getProperty((XMLIndividual) object, propertyName) != null;
		}
		return false;
	}

	private XMLProperty<?> getProperty(XMLIndividual object, String propertyName) {
		XMLProperty<?> prop = object.getType().getPropertyByName(propertyName);
		if (prop != null) {
			return prop;
		}
		for (XMLProperty<?> property : object.getType().getProperties()) {
			if (propertyName.equals(property.getXMLSupportName())) {
				return property;
			}
		}
		return null;
	}

	@Override
	public void addAttributeValueForObject(Object object, String propertyName, Object value) {

		// System.out.println("***** addAttributeValueForObject object=" + object + " name=" + name + " value=" + value);

		if (object instanceof XMLIndividual) {

			XMLProperty<?> prop = getProperty((XMLIndividual) object, propertyName);

			if (prop == null) {
				/*if (!mm.isReadOnly() || name.equals(XMLCst.CDATA_ATTR_NAME)) {
				
					prop = mm.getModelFactory().makeProperty(name, value.getClass(), XMLSupport.CDATA, null, t);
				
					if (prop != null) {
						((XMLIndividual) object).addPropertyValue(prop, value);
					}
					else {
						LOGGER.warning("UNABLE to create a new property named " + name);
					}
				}
				else {
					LOGGER.warning(
							"TRYING to give a value to a non existant property: " + name + " -- " + name.equals(XMLCst.CDATA_ATTR_NAME));
				}*/
				LOGGER.warning("Not found : property " + propertyName + " for " + object); // When still required ??? not sure (sylvain)
			}
			else {

				// System.out.println("On ajoute " + propertyName + "=" + value + " pour " + object);

				((XMLIndividual) object).addPropertyValue(prop, value);

			}
		}
	}

	@Override
	public void addChildToObject(Object currentObject, Object currentContainer) {

		//System.out.println("------> Et hop, on ajoute " + currentObject + " a " + currentContainer);

		if (currentContainer instanceof XMLIndividual) {
			((XMLIndividual) currentContainer).addChild((XMLIndividual) currentObject);
		}

	}

	@Override
	public Type getAttributeType(Object currentContainer, String localName) {
		XMLProperty prop = ((XMLIndividual) currentContainer).getType().getPropertyByName(localName);

		if (prop == null) {
			for (XMLProperty property : ((XMLIndividual) currentContainer).getType().getProperties()) {
				if (localName.equals(property.getXMLSupportName())) {
					prop = property;
				}
			}
		}

		if (prop != null) {
			return prop.getType();
		}
		return null;
	}
}
