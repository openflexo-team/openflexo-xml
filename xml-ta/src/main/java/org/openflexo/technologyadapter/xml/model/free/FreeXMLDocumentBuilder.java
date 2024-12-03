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

import org.openflexo.xml.SaxBasedObjectGraphFactory;
import org.xml.sax.SAXException;

/**
 * A builder for a {@link FreeXMLDocument} (sax-based)
 */
public class FreeXMLDocumentBuilder extends SaxBasedObjectGraphFactory {

	private FreeXMLDocument document = null;

	@Override
	public Object getInstanceOf(Type aType, String name) {

		System.out.println("Called getInstanceOf() with " + aType + " and " + name);

		/*if (aType instanceof XMLComplexType) {
			XMLIndividual _inst = document.addNewIndividual((XMLComplexType) aType);
			return _inst;
		}*/

		return null;
	}

	@Override
	public Type getTypeForObject(String typeURI, Object container, String objectName) {

		System.out.println(
				"Called getTypeForObject() with typeURI=" + typeURI + " and container=" + container + " and objectName=" + objectName);

		/*XMLMetaModel mm = document.getMetaModel();
		XMLType tt = null;
		if (mm != null) {
			tt = mm.getTypeFromURI(typeURI);
		}
		
		// Try to match as local uri
		if (container instanceof XMLIndividual) {
			XMLType parentType = ((XMLIndividual) container).getType();
			if (tt == null && !typeURI.startsWith(parentType.getFullyQualifiedName())) {
				tt = mm.getTypeFromURI(parentType.getFullyQualifiedName() + "#" + typeURI);
			}
		}
		
		// Create the type if it does not exist and that we can!!
		if (!mm.isReadOnly() && tt == null) {
			if (container instanceof XMLIndividual) {
				XMLType parentType = ((XMLIndividual) container).getType();
				tt = mm.createNewType((parentType.getFullyQualifiedName() + "#" + objectName), objectName, false);
			}
			else {
				tt = mm.createNewType(mm.getURI() + "#" + objectName, objectName, false);
			}
		}
		
		return tt;*/

		return null;
	}

	@Override
	public Object deserialize(String input) throws IOException {
		if (document != null) {

			System.out.println("Prout ici");

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

			System.out.println("Prout la");

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
	public void addToRootNodes(Object anObject) {

		System.out.println("addToRootNodes with " + anObject);

		// document.setRoot((XMLIndividual) anObject);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setContextProperty(String propertyName, Object value) {

		System.out.println("setContextProperty with " + propertyName + " and " + value);

		/*if (propertyName.equals(XMLReaderSAXHandler.NAMESPACE_Property)) {
			document.setNamespace(((List<String>) value).get(0), ((List<String>) value).get(1));
		}*/

	}

	@Override
	public void setContext(Object objectGraph) {
		document = (FreeXMLDocument) objectGraph;

	}

	@Override
	public void resetContext() {
		document = null;
	}

	@Override
	public boolean objectHasAttributeNamed(Object object, String propertyName) {

		System.out.println("Called objectHasAttributeNamed() with " + object + " and " + propertyName);

		/*if (object instanceof XMLIndividual) {
		
			XMLProperty prop = ((XMLIndividual) object).getType().getPropertyByName(propertyName);
		
			return (prop != null);
		}
		return false;*/
		return false;
	}

	@Override
	public void addAttributeValueForObject(Object object, String name, Object value) {

		System.out.println("Called addAttributeValueForObject() with " + object + " and " + name + " and " + value);

		/*if (object instanceof XMLIndividual) {
			XMLComplexType t = ((XMLIndividual) object).getType();
		
			XMLProperty prop = t.getPropertyByName(name);
		
			XMLMetaModel mm = document.getMetaModel();
		
			if (prop == null) {
				if (!mm.isReadOnly() || name.equals(XMLCst.CDATA_ATTR_NAME)) {
		
					prop = t.createProperty(name, value.getClass());
		
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
			}
			else {
				((XMLIndividual) object).addPropertyValue(prop, value);
		
			}
		}*/
	}

	@Override
	public void addChildToObject(Object currentObject, Object currentContainer) {

		System.out.println("addChildToObject with " + currentObject + " and " + currentContainer);
		/*if (currentContainer instanceof XMLIndividual) {
			((XMLIndividual) currentContainer).addChild((XMLIndividual) currentObject);
		}*/

	}

	@Override
	public Type getAttributeType(Object currentContainer, String localName) {

		System.out.println("getAttributeType with " + currentContainer + " and " + localName);

		/*XMLProperty prop = ((XMLIndividual) currentContainer).getType().getPropertyByName(localName);
		if (prop != null) {
			return prop.getType();
		}*/
		return null;
	}
}
