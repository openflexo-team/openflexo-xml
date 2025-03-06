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

package org.openflexo.technologyadapter.xml.fml.reflect;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.foundation.fml.rt.VirtualModelInstanceObject;
import org.openflexo.technologyadapter.xml.model.typed.XMLModel;
import org.openflexo.xml.SaxBasedObjectGraphFactory;
import org.xml.sax.SAXException;

/**
 * A builder for a {@link XMLModel} (sax-based)
 */
public class FMLXMLModelBuilder
		extends SaxBasedObjectGraphFactory<XMLVirtualModelInstance<?>, XMLFlexoConceptInstance, VirtualModelInstanceObject> {

	private static final Logger logger = Logger.getLogger(FMLXMLModelBuilder.class.getPackage().getName());

	private XMLVirtualModelInstance<?> model = null;

	/*@Override
	public XMLFlexoConceptInstance createInstance(Type aType, String name) {
	
		if (aType instanceof XMLComplexType) {
	
			System.out.println("Make XMLIndividual for type" + aType);
	
			XMLIndividual returned = model.addNewIndividual((XMLComplexType) aType);
	
			return returned;
		}
	
		return null;
	}*/

	@Override
	public XMLFlexoConceptInstance createInstance(Type aType, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/*@Override
	public XMLType getTypeForObject(String typeURI, XMLObject<XMLModel> container, String objectName) {
	
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
	
		// System.out.println(
		// "getTypeForObject() ??? " + typeURI + " container: " + container + " objectName=" + objectName + " returns " + returned);
	
		return returned;
	}*/

	@Override
	public Type getTypeForObject(String typeURI, VirtualModelInstanceObject container, String objectName) {
		System.out.println("getTypeForObject() ??? " + typeURI + " container: " + container + " objectName=" + objectName);
		return null;
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

	/*@Override
	public void addToRootNodes(XMLIndividual anObject) {
	
		// System.out.println("-------> addToRootNodes " + anObject);
	
		model.setRoot(anObject);
	}*/

	@Override
	public void addToRootNodes(XMLFlexoConceptInstance anObject) {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("unchecked")
	@Override
	public void setModelProperty(String propertyName, Object value) {
		/*if (propertyName.equals(XMLReaderSAXHandler.NAMESPACE_Property)) {
			model.setNamespace(((List<String>) value).get(0), ((List<String>) value).get(1));
		}*/

	}

	@Override
	public void setModelContext(XMLVirtualModelInstance<?> objectGraph) {
		model = objectGraph;

	}

	@Override
	public void resetModelContext() {
		model = null;
	}

	/*@Override
	public boolean objectHasPropertyNamed(XMLObject<XMLModel> object, String propertyName) {
	
		// System.out.println("***** objectHasAttributeNamed??? object=" + object + " propertyName=" + propertyName);
	
		if (object instanceof XMLIndividual) {
			return getProperty((XMLIndividual) object, propertyName) != null;
		}
		return false;
	}*/

	@Override
	public boolean objectHasPropertyNamed(VirtualModelInstanceObject object, String propertyName) {
		// TODO Auto-generated method stub
		return false;
	}

	/*private XMLProperty<?, ?> getProperty(XMLIndividual object, String propertyName) {
		XMLProperty<?, ?> prop = object.getType().getPropertyByName(propertyName);
		if (prop != null) {
			return prop;
		}
		for (XMLProperty<?, ?> property : object.getType().getProperties()) {
			if (propertyName.equals(property.getXMLSupportName())) {
				return property;
			}
		}
		return null;
	}
	
	private <T> T valueForProperty(XMLProperty<?, T> property, Object value) {
		if (property.getType() instanceof XMLEnumerationType) {
			XMLEnumerationType enumeration = (XMLEnumerationType) property.getType();
			for (XMLEnumValue enumValue : enumeration.getEnumValues()) {
				if (enumValue.getName().equals(value)) {
					return (T) enumValue;
				}
			}
			logger.warning("Unexpected enum value " + value + " for " + property);
			return null;
		}
		else if (property.getType() instanceof XMLSimpleType) {
			XMLSimpleType t = (XMLSimpleType) property.getType();
			if (value instanceof String) {
				if (t.getPrimitiveType() != null) {
					try {
						return (T) t.getPrimitiveType().valueFromString((String) value);
					} catch (InvalidDataException e) {
						logger.warning("InvalidDataException : unexpected value " + value + " for " + property);
						return null;
					} catch (ClassCastException e) {
						logger.warning("ClassCastException : unexpected value " + value + " for " + property);
						return null;
					}
				}
				else {
					logger.warning("Not supported : type " + t.getURI() + " for value " + value + " for " + property);
					return null;
				}
			}
			else {
				logger.warning("Unexpected value " + value + " for " + property);
				return null;
			}
		}
		return (T) value;
	}*/

	/*@Override
	public void addPropertyValueForObject(XMLIndividual object, String propertyName, Object value) {
	
		// System.out.println("***** addAttributeValueForObject object=" + object + " name=" + name + " value=" + value);
	
		if (object instanceof XMLIndividual) {
	
			XMLProperty prop = getProperty(object, propertyName);
	
			if (prop == null) {
				logger.warning("Unsupported " + propertyName + " for " + object);
				return;
			}
	
			if (prop.isMultiple()) {
				object.addPropertyValue(prop, valueForProperty(prop, value));
			}
			else {
				object.setPropertyValue(prop, valueForProperty(prop, value));
			}
	
		}
	}*/

	@Override
	public void addPropertyValueForObject(XMLFlexoConceptInstance object, String propertyName, Object value) {
		// TODO Auto-generated method stub

	}

	/*@Override
	public void addChildToObject(XMLIndividual currentObject, XMLIndividual currentContainer) {
	
		// System.out.println("------> Et hop, on ajoute " + currentObject + " a " + currentContainer);
	
		if (currentContainer instanceof XMLIndividual) {
			currentContainer.addChild(currentObject);
		}
	
	}*/

	@Override
	public void addChildToObject(XMLFlexoConceptInstance child, XMLFlexoConceptInstance container) {
		// TODO Auto-generated method stub

	}

	/*@Override
	public Type getTypeForProperty(XMLIndividual currentContainer, String localName) {
		XMLProperty prop = currentContainer.getType().getPropertyByName(localName);
	
		if (prop == null) {
			for (XMLProperty property : currentContainer.getType().getProperties()) {
				if (localName.equals(property.getXMLSupportName())) {
					prop = property;
				}
			}
		}
	
		if (prop != null) {
			return prop.getType();
		}
		return null;
	}*/

	@Override
	public Type getTypeForProperty(XMLFlexoConceptInstance currentContainer, String localName) {
		// TODO Auto-generated method stub
		return null;
	}
}
