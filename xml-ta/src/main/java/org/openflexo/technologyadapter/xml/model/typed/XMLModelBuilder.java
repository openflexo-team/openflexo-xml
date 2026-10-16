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

import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.pamela.exceptions.InvalidDataException;
import org.openflexo.technologyadapter.xml.XMLObject;
import org.openflexo.technologyadapter.xml.metamodel.XMLComplexType;
import org.openflexo.technologyadapter.xml.metamodel.XMLDataProperty;
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

/**
 * A builder for a {@link XMLModel} (sax-based)
 */
public class XMLModelBuilder extends SaxBasedObjectGraphFactory<XMLModel, XMLIndividual, XMLObject<XMLModel>, XMLProperty<?, ?>> {

	private static final Logger logger = Logger.getLogger(XMLModelBuilder.class.getPackage().getName());

	@Override
	public RootNodeStrategy getRootNodeStrategy() {
		return RootNodeStrategy.SINGLE_ROOT_NODE;
	}

	@Override
	public XMLIndividual createInstance(Type aType, String name,
			ParsedElement<XMLIndividual, XMLObject<XMLModel>, XMLProperty<?, ?>> parsed) {

		if (aType instanceof XMLComplexType) {

			XMLIndividual returned = getModelContext().addNewIndividual((XMLComplexType) aType);

			logger.fine("Make XMLIndividual for type" + aType + " return " + returned);

			return returned;
		}

		return null;
	}

	@Override
	public Type getType(String typeURI, String localName, XMLObject<XMLModel> container) {

		XSDMetaModel mm = getModelContext().getMetaModel();
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
				returned = mm.getModelFactory().makeComplexType(parentType.getFullyQualifiedName() + "#" + localName, localName, mm);
			}
			else {
				returned = mm.getModelFactory().makeComplexType(mm.getURI() + "#" + localName, localName, mm);
			}
		}

		// System.out.println(
		// "getTypeForObject() ??? " + typeURI + " container: " + container + " objectName=" + objectName + " returns " + returned);

		if (returned == null) {
			logger.warning("Cannot find type " + typeURI + " container: " + container + " objectName=" + localName);
		}

		return returned;
	}

	@Override
	public void setRootNode(XMLIndividual rootNode) {
		if (getModelContext().getRoot() == null) {
			getModelContext().setRoot(rootNode);
		}
		else {
			logger.warning(
					"XMLModel already declares a root node : " + getModelContext().getRoot() + " while setting root node " + rootNode);
		}
	}

	@Override
	public void addToRootNodes(XMLIndividual anObject) {
		// not applicable
	}

	@Override
	public void updateRootNode(ParsedElement<XMLIndividual, XMLObject<XMLModel>, XMLProperty<?, ?>> parsed) {
		// not applicable
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setModelProperty(String propertyName, Object value) {
		if (propertyName.equals(XMLReaderSAXHandler.NAMESPACE_Property)) {
			getModelContext().setNamespace(((List<String>) value).get(0), ((List<String>) value).get(1));
		}

	}

	@Override
	public XMLProperty<?, ?> getPropertyNamed(XMLObject<XMLModel> object, String propertyName) {
		if (object instanceof XMLIndividual) {
			XMLIndividual individual = (XMLIndividual) object;
			return individual.getType().getPropertyByName(propertyName);
		}
		return null;
	}

	@Override
	public XMLProperty<?, ?> getPropertyForElementName(XMLObject<XMLModel> object, String elementName) {
		if (object instanceof XMLIndividual) {
			XMLIndividual individual = (XMLIndividual) object;
			for (XMLProperty<?, ?> property : individual.getType().getProperties()) {
				if (elementName.equals(property.getXMLSupportName())) {
					return property;
				}
			}
		}
		return null;
	}

	@Override
	public XMLDataProperty<?> getPropertyForAttributeName(XMLObject<XMLModel> object, String attributeName) {
		if (object instanceof XMLIndividual) {
			XMLIndividual individual = (XMLIndividual) object;
			for (XMLProperty<?, ?> property : individual.getType().getProperties()) {
				if (attributeName.equals(property.getXMLSupportName())) {
					return (XMLDataProperty) property;
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
			return (T) primitiveType.valueFromString(value, getModelContext());
		} catch (InvalidDataException e) {
			logger.warning("InvalidDataException : unexpected value " + value);
			return null;
		} catch (ClassCastException e) {
			logger.warning("ClassCastException : unexpected value " + value);
			return null;
		}

	}

	private <T> void addOrSetPropertyValue(XMLObject<XMLModel> targetObject, XMLProperty<?, T> property, Object value) {

		// System.err.println("***** addOrSetPropertyValue object=" + targetObject + " property=" + property + " value=" + value);

		if (targetObject instanceof XMLIndividual) {

			XMLIndividual individual = (XMLIndividual) targetObject;

			if (property == null) {
				logger.warning("Unsupported " + property + " for " + targetObject);
				return;
			}

			if (property.isMultiple()) {
				individual.addPropertyValue(property, valueForProperty(property, value));
			}
			else {
				individual.setPropertyValue(property, valueForProperty(property, value));
				if (property.getType() instanceof XMLSimpleType
						&& ((XMLSimpleType) property.getType()).getPrimitiveType() == XMLSchemaPrimitiveType.ID) {
					// System.out.println(" ##### nouvel ID " + value + " of " + value.getClass() + " pour " + object);
					individual.setUUID((String) value);
				}

			}
		}
	}

	@Override
	public void addOrSetDataPropertyValue(XMLObject<XMLModel> targetObject, XMLProperty<?, ?> property, Object value) {
		addOrSetPropertyValue(targetObject, property, value);
	}

	@Override
	public void addOrSetObjectPropertyValue(XMLObject<XMLModel> targetObject, XMLProperty<?, ?> property, XMLObject<XMLModel> value) {
		addOrSetPropertyValue(targetObject, property, value);
	}

	@Override
	public void addChildToObject(XMLIndividual currentObject, XMLObject<XMLModel> currentContainer) {
		if (currentContainer instanceof XMLIndividual) {
			((XMLIndividual) currentContainer).addChild(currentObject);
		}

	}

	@Override
	public Type getTypeForProperty(XMLProperty<?, ?> property) {
		if (property != null) {
			return property.getType();
		}
		return null;
	}

	@Override
	public void handleCData(XMLIndividual object, String value) {
		// TODO Auto-generated method stub
	}
}
