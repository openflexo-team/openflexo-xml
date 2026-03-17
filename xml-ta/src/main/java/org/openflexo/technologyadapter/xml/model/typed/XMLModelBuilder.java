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
import java.util.logging.Logger;

import org.openflexo.pamela.exceptions.InvalidDataException;
import org.openflexo.technologyadapter.xml.XMLObject;
import org.openflexo.technologyadapter.xml.metamodel.XMLComplexType;
import org.openflexo.technologyadapter.xml.metamodel.XMLEnumValue;
import org.openflexo.technologyadapter.xml.metamodel.XMLEnumerationType;
import org.openflexo.technologyadapter.xml.metamodel.XMLProperty;
import org.openflexo.technologyadapter.xml.metamodel.XMLReferenceType;
import org.openflexo.technologyadapter.xml.metamodel.XMLSimpleType;
import org.openflexo.technologyadapter.xml.metamodel.XMLSimpleType.XMLSchemaPrimitiveType;
import org.openflexo.technologyadapter.xml.metamodel.XMLType;
import org.openflexo.technologyadapter.xml.metamodel.XSDMetaModel;
import org.openflexo.xml.SaxBasedObjectGraphFactory;
import org.openflexo.xml.XMLReaderSAXHandler;
import org.openflexo.xml.XMLReaderSAXHandler.ParsedElement;
import org.xml.sax.SAXException;

/**
 * A builder for a {@link XMLModel} (sax-based)
 */
public class XMLModelBuilder extends SaxBasedObjectGraphFactory<XMLModel, XMLIndividual, XMLObject<XMLModel>> {

	private static final Logger logger = Logger.getLogger(XMLModelBuilder.class.getPackage().getName());

	private XMLModel model = null;

	@Override
	public XMLIndividual createInstance(Type aType, String name, ParsedElement<XMLIndividual, XMLObject<XMLModel>> parsed) {

		if (aType instanceof XMLComplexType) {

			XMLIndividual returned = model.addNewIndividual((XMLComplexType) aType);

			logger.fine("Make XMLIndividual for type" + aType + " return " + returned);

			return returned;
		}

		return null;
	}

	@Override
	public XMLType getTypeForObject(String typeURI, XMLObject<XMLModel> container, String objectName) {

		XSDMetaModel mm = model.getMetaModel();
		XMLType returned = null;
		if (mm != null) {
			returned = mm.getTypeFromURI(typeURI);
		}

		if (returned == null) {
			// System.out.println("Not found : " + typeURI);
			returned = mm.getTypeFromContextualURI(typeURI);
			/*if (returned == null) {
				System.out.println("Still not found : " + typeURI);
			}*/
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

		if (returned == null) {
			logger.warning("Cannot find type " + typeURI + " container: " + container + " objectName=" + objectName);
		}

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
	public void addToRootNodes(XMLIndividual anObject) {

		// System.out.println("-------> addToRootNodes " + anObject);

		if (model.getRoot() == null) {
			model.setRoot(anObject);
		}
		else {
			logger.warning("XMLModel already declares a root node : " + model.getRoot() + " while setting root node " + anObject);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setModelProperty(String propertyName, Object value) {
		if (propertyName.equals(XMLReaderSAXHandler.NAMESPACE_Property)) {
			model.setNamespace(((List<String>) value).get(0), ((List<String>) value).get(1));
		}

	}

	@Override
	public void setModelContext(XMLModel objectGraph) {
		super.setModelContext(objectGraph);
		model = objectGraph;

	}

	@Override
	public void resetModelContext() {
		model = null;
	}

	@Override
	public boolean modelHasPropertyNamed(String propertyName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean objectHasPropertyNamed(XMLObject<XMLModel> object, String propertyName) {

		// System.out.println("***** objectHasAttributeNamed??? object=" + object + " propertyName=" + propertyName);

		if (object instanceof XMLIndividual) {
			return getProperty(object, propertyName) != null;
		}
		return false;
	}

	private XMLProperty<?, ?> getProperty(XMLObject<XMLModel> object, String propertyName) {
		if (object instanceof XMLIndividual) {
			XMLIndividual individual = (XMLIndividual) object;
			XMLProperty<?, ?> prop = individual.getType().getPropertyByName(propertyName);
			if (prop != null) {
				return prop;
			}
			for (XMLProperty<?, ?> property : individual.getType().getProperties()) {
				if (propertyName.equals(property.getXMLSupportName())) {
					return property;
				}
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
		else if (property.getType() instanceof XMLReferenceType) {
			XMLReferenceType t = (XMLReferenceType) property.getType();
			if (value instanceof String) {
				if (t.getReferencedPrimitiveType() != null) {
					return decodeValue(t.getReferencedPrimitiveType(), (String) value);
				}
				else {
					logger.warning(
							"Not supported : type " + t.getURI() + " for value " + value + " of " + value.getClass() + " for " + property);
					return null;
				}
			}
			else {
				logger.warning("Unexpected value " + value + " for " + property);
				return null;
			}
		}
		else if (property.getType() instanceof XMLSimpleType) {
			XMLSimpleType t = (XMLSimpleType) property.getType();
			if (value instanceof String) {
				if (t.getPrimitiveType() != null) {
					return decodeValue(t.getPrimitiveType(), (String) value);
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
	}

	private <T> T decodeValue(XMLSchemaPrimitiveType primitiveType, String value) {
		try {
			return (T) primitiveType.valueFromString(value, model);
		} catch (InvalidDataException e) {
			logger.warning("InvalidDataException : unexpected value " + value);
			return null;
		} catch (ClassCastException e) {
			logger.warning("ClassCastException : unexpected value " + value);
			return null;
		}

	}

	@Override
	public void addPropertyValueForObject(XMLObject<XMLModel> object, String propertyName, Object value) {

		System.out.println("***** addAttributeValueForObject object=" + object + " propertyName=" + propertyName + " value=" + value
				+ " of " + value.getClass());

		if (propertyName.equals("latency") && value.equals("25")) {
			Thread.dumpStack();
		}

		if (object instanceof XMLIndividual) {

			XMLIndividual individual = (XMLIndividual) object;
			XMLProperty prop = getProperty(object, propertyName);

			if (prop == null) {
				logger.warning("Unsupported " + propertyName + " for " + object);
				return;
			}

			if (prop.isMultiple()) {
				individual.addPropertyValue(prop, valueForProperty(prop, value));
			}
			else {
				individual.setPropertyValue(prop, valueForProperty(prop, value));
				if (prop.getType() instanceof XMLSimpleType
						&& ((XMLSimpleType) prop.getType()).getPrimitiveType() == XMLSchemaPrimitiveType.ID) {
					// System.out.println(" ##### nouvel ID " + value + " of " + value.getClass() + " pour " + object);
					individual.setUUID((String) value);
				}

			}

			/*if (prop == null) {
				if (!mm.isReadOnly() || name.equals(XMLCst.CDATA_ATTR_NAME)) {
			
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
				}
				LOGGER.warning("Not found : property " + propertyName + " for " + object); // When still required ??? not sure (sylvain)
			}
			else {
			
				// System.out.println("On ajoute " + propertyName + "=" + value + " pour " + object);
			
				object.addPropertyValue(prop, value);
			
			}*/
		}
	}

	@Override
	public void addPropertyValueForModel(String propertyName, Object value) {
		logger.warning("Please implement me");
	}

	@Override
	public void addChildToObject(XMLIndividual currentObject, XMLObject<XMLModel> currentContainer) {

		// System.out.println("------> Et hop, on ajoute " + currentObject + " a " + currentContainer);

		if (currentContainer instanceof XMLIndividual) {
			((XMLIndividual) currentContainer).addChild(currentObject);
		}

	}

	@Override
	public Type getTypeForProperty(XMLObject<XMLModel> currentContainer, String localName) {
		if (currentContainer instanceof XMLIndividual) {
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
		}
		return null;
	}

	@Override
	public String getPropertyName(XMLObject<XMLModel> object, String propertyName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> void addPropertyObject(XMLObject<XMLModel> object, String propertyName, XMLObject<XMLModel> value) {
		// TODO Auto-generated method stub

	}

}
