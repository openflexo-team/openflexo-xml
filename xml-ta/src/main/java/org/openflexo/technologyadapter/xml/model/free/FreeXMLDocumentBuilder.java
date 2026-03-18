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

package org.openflexo.technologyadapter.xml.model.free;

import java.lang.reflect.Type;

import org.openflexo.technologyadapter.xml.XMLObject;
import org.openflexo.xml.SaxBasedObjectGraphFactory;
import org.openflexo.xml.XMLCst;
import org.openflexo.xml.XMLReaderSAXHandler.ParsedElement;

/**
 * A builder for a {@link FreeXMLDocument} (sax-based)
 */
public class FreeXMLDocumentBuilder extends SaxBasedObjectGraphFactory<FreeXMLDocument, XMLElement, XMLObject<FreeXMLDocument>, String> {

	@Override
	public RootNodeStrategy getRootNodeStrategy() {
		return RootNodeStrategy.SINGLE_ROOT_NODE;
	}

	private FreeXMLDocumentFactory getModelFactory() {
		if (getModelContext() != null) {
			return getModelContext().getModelFactory();
		}
		return null;
	}

	@Override
	public XMLElement createInstance(Type aType, String name, ParsedElement<XMLElement, XMLObject<FreeXMLDocument>, String> parsed) {

		// System.out.println("Called createInstance() with " + aType + " and " + name);

		if (aType == XMLElement.class) {
			// System.out.println("Creating Element " + name);
			return getModelFactory().makeXMLElement(name, getModelContext());
		}

		return null;
	}

	@Override
	public Type getType(String typeURI, String localName, XMLObject<FreeXMLDocument> container) {

		// System.out.println(
		// "Called getTypeForObject() with typeURI=" + typeURI + " and container=" + container + " and objectName=" + objectName);

		return XMLElement.class;
	}

	@Override
	public void setRootNode(XMLElement rootNode) {
		getModelContext().setRootElement(rootNode);
	}

	@Override
	public void addToRootNodes(XMLElement anObject) {
		// not applicable
	}

	@Override
	public void updateRootNode(ParsedElement<XMLElement, XMLObject<FreeXMLDocument>, String> parsed) {
		// not applicable
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setModelProperty(String propertyName, Object value) {

		// System.out.println("setContextProperty with " + propertyName + " and " + value);

	}

	@Override
	public void setModelContext(FreeXMLDocument document) {
		super.setModelContext(document);
	}

	@Override
	public void addOrSetDataPropertyValue(XMLObject<FreeXMLDocument> targetObject, String property, Object value) {

		if (targetObject instanceof XMLElement) {
			if (property.equals(XMLCst.CDATA_ATTR_NAME)) {
				((XMLElement) targetObject).setValue(value);
			}
			else {
				((XMLElement) targetObject).setAttributeValue(property, value);
			}
		}
	}

	@Override
	public void addOrSetObjectPropertyValue(XMLObject<FreeXMLDocument> targetObject, String property, XMLObject<FreeXMLDocument> value) {
		// Never used in this context
	}

	@Override
	public void addChildToObject(XMLElement currentObject, XMLObject<FreeXMLDocument> currentContainer) {

		// System.out.println("addChildToObject with " + currentObject + " and " + currentContainer);

		if (currentObject instanceof XMLElement && currentContainer instanceof XMLElement) {
			((XMLElement) currentContainer).addToChildElements(currentObject);
		}
	}

	@Override
	public String getPropertyNamed(XMLObject<FreeXMLDocument> object, String propertyName) {
		return null;
	}

	@Override
	public Type getTypeForProperty(String property) {
		return null;
	}

	@Override
	public String getPropertyForElementName(XMLObject<FreeXMLDocument> object, String elementName) {
		return null;
	}

	@Override
	public String getPropertyForAttributeName(XMLObject<FreeXMLDocument> object, String attributeName) {
		return attributeName;
	}

	@Override
	public void handleCData(XMLElement targetObject, String value) {
		targetObject.setValue(value);
	}
}
