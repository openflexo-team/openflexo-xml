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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

import org.openflexo.technologyadapter.xml.XMLObject;
import org.openflexo.xml.SaxBasedObjectGraphFactory;
import org.openflexo.xml.XMLCst;
import org.xml.sax.SAXException;

/**
 * A builder for a {@link FreeXMLDocument} (sax-based)
 */
public class FreeXMLDocumentBuilder extends SaxBasedObjectGraphFactory<FreeXMLDocument, XMLElement, XMLObject<FreeXMLDocument>> {

	private FreeXMLDocument document = null;

	@Override
	public XMLElement createInstance(Type aType, String name) {

		// System.out.println("Called createInstance() with " + aType + " and " + name);

		if (aType == XMLElement.class) {
			// System.out.println("Creating Element " + name);
			return document.getModelFactory().makeXMLElement(name, document);
		}

		return null;
	}

	@Override
	public Type getTypeForObject(String typeURI, XMLObject<FreeXMLDocument> container, String objectName) {

		// System.out.println(
		// "Called getTypeForObject() with typeURI=" + typeURI + " and container=" + container + " and objectName=" + objectName);

		return XMLElement.class;
	}

	@Override
	public Object deserialize(String input) throws IOException {
		if (document != null) {

			try {
				saxParser.parse(input, handler);
			} catch (SAXException e) {
				LOGGER.warning("Cannot parse document: " + e.getMessage());
				throw new IOException(e.getMessage());
			}
			return this.document;
		}
		LOGGER.warning("Context is not set for parsing, aborting");
		return null;
	}

	@Override
	public Object deserialize(InputStream input) throws IOException {
		if (document != null) {

			try {
				saxParser.parse(input, handler);
			} catch (SAXException e) {
				LOGGER.warning("Cannot parse document: " + e.getMessage());
				throw new IOException(e.getMessage());
			}
			return this.document;

		}
		LOGGER.warning("Context is not set for parsing, aborting");
		return null;
	}

	@Override
	public void addToRootNodes(XMLElement anObject) {

		// System.out.println("addToRootNodes with " + anObject);

		document.setRootElement(anObject);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setModelProperty(String propertyName, Object value) {

		// System.out.println("setContextProperty with " + propertyName + " and " + value);

	}

	@Override
	public void setModelContext(FreeXMLDocument objectGraph) {
		document = objectGraph;
	}

	@Override
	public void resetModelContext() {
		document = null;
	}

	@Override
	public boolean objectHasPropertyNamed(XMLObject<FreeXMLDocument> object, String propertyName) {

		// System.out.println("Called objectHasAttributeNamed() with " + object + " and " + propertyName);

		return false;
	}

	@Override
	public void addPropertyValueForObject(XMLElement object, String name, Object value) {

		// System.out.println("Called addAttributeValueForObject() with " + object + " and " + name + " and " + value);

		if (object instanceof XMLElement) {
			if (name.equals(XMLCst.CDATA_ATTR_NAME)) {
				object.setValue(value);
			}
			else {
				object.setAttributeValue(name, value);
			}
		}
	}

	@Override
	public void addChildToObject(XMLElement currentObject, XMLElement currentContainer) {

		// System.out.println("addChildToObject with " + currentObject + " and " + currentContainer);

		if (currentObject instanceof XMLElement && currentContainer instanceof XMLElement) {
			currentContainer.addToChildElements(currentObject);
		}
	}

	@Override
	public Type getTypeForProperty(XMLElement currentContainer, String localName) {

		// System.out.println("getAttributeType with " + currentContainer + " and " + localName);

		return null;
	}
}
