/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2012-2012, AgileBirds
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

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.technologyadapter.xml.model.free.FreeXMLDocument;
import org.openflexo.technologyadapter.xml.model.free.FreeXMLDocumentBuilder;
import org.openflexo.technologyadapter.xml.model.free.FreeXMLDocumentFactory;
import org.openflexo.technologyadapter.xml.model.free.XMLElement;

/**
 * A resource allowing access to a plain XML document without any conformance nor typing
 * 
 * @author sylvain
 */
@ModelEntity
@ImplementationClass(FreeXMLResource.FreeXMLResourceImpl.class)
public interface FreeXMLResource extends XMLResource<FreeXMLDocument, FreeXMLDocumentFactory> {

	/**
	 * Default implementation for FreeXMLResource
	 */
	public static abstract class FreeXMLResourceImpl extends XMLResourceImpl<FreeXMLDocument, FreeXMLDocumentFactory>
			implements FreeXMLResource {

		protected static final Logger logger = Logger.getLogger(FreeXMLResourceImpl.class.getPackage().getName());

		@Override
		public Class<FreeXMLDocument> getResourceDataClass() {
			return FreeXMLDocument.class;
		}

		@Override
		protected FreeXMLDocument performLoad() throws IOException, Exception {

			resourceData = getFactory().makeFreeXMLDocument();
			resourceData.setResource(this);
			resourceData.setURI(this.getURI());

			notifyResourceWillLoad();

			FreeXMLDocumentBuilder builder = new FreeXMLDocumentBuilder();
			builder.setModelContext(resourceData);
			builder.deserialize(getInputStream());
			builder.resetModelContext();

			notifyResourceLoaded();

			return resourceData;
		}

		/**
		 * Writes the document in UTF-8, one element per line, indented. A free document keeps the local name, the attributes, the text and
		 * the children of each element: namespaces, comments and processing instructions of the original file are not part of it, so
		 * they are not written.
		 */
		/**
		 * A document without root element (a new, empty one) is not written: a file without root would not be well-formed XML, and
		 * could not be loaded again.
		 */
		@Override
		protected void performSave(boolean clearIsModified) throws SaveResourceException {
			if (resourceData == null || resourceData.getRootElement() == null) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Nothing to write for " + this + ": the document has no root element");
				}
				return;
			}
			super.performSave(clearIsModified);
		}

		@Override
		protected void write(OutputStream out) throws IOException, XMLStreamException {
			OutputStreamWriter outSW = new OutputStreamWriter(out, StandardCharsets.UTF_8);
			XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(outSW);
			writer.writeStartDocument("UTF-8", "1.0");
			writeElement(writer, resourceData.getRootElement(), 0);
			writer.writeCharacters("\n");
			writer.writeEndDocument();
			writer.flush();
			writer.close();
			outSW.flush();
		}

		private static void writeElement(XMLStreamWriter writer, XMLElement element, int depth) throws XMLStreamException {
			writer.writeCharacters("\n" + "  ".repeat(depth));
			boolean empty = element.getChildElements().isEmpty() && element.getValue() == null;
			if (empty) {
				writer.writeEmptyElement(element.getName());
			}
			else {
				writer.writeStartElement(element.getName());
			}
			for (String attribute : element.getAttributes()) {
				Object value = element.getAttributeValue(attribute);
				writer.writeAttribute(attribute, value != null ? value.toString() : "");
			}
			if (empty) {
				return;
			}
			if (element.getValue() != null) {
				writer.writeCharacters(element.getValue().toString());
			}
			for (XMLElement child : element.getChildElements()) {
				writeElement(writer, child, depth + 1);
			}
			if (!element.getChildElements().isEmpty()) {
				writer.writeCharacters("\n" + "  ".repeat(depth));
			}
			writer.writeEndElement();
		}

	}

}
