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

import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.technologyadapter.xml.model.AbstractXMLDocument;
import org.openflexo.technologyadapter.xml.model.free.FreeXMLDocument.FreeXMLDocumentImpl;
import org.openflexo.technologyadapter.xml.rm.FreeXMLResource;

/**
 * Represents a plain XML document without any conformance nor typing
 * 
 * @author sylvain
 */
@ModelEntity
@ImplementationClass(FreeXMLDocumentImpl.class)
public interface FreeXMLDocument extends AbstractXMLDocument<FreeXMLDocument> {

	@PropertyIdentifier(type = XMLElement.class)
	public static final String ROOT_ELEMENT_KEY = "rootElement";

	@Getter(value = ROOT_ELEMENT_KEY)
	public XMLElement getRootElement();

	@Setter(ROOT_ELEMENT_KEY)
	public void setRootElement(XMLElement rootElement);

	@Override
	public FreeXMLResource getResource();

	@Override
	public FreeXMLDocumentFactory getModelFactory();

	public String getXMLRepresentation();

	/**
	 * Default implementation for {@link FreeXMLDocument}
	 * 
	 * @author sylvain
	 */

	public static abstract class FreeXMLDocumentImpl extends AbstractXMLDocumentImpl<FreeXMLDocument> implements FreeXMLDocument {

		// Can be safely cast to FreeXMLResource
		@Override
		public FreeXMLResource getResource() {
			return (FreeXMLResource) super.getResource();
		}

		// Can be safely cast to FreeXMLDocumentFactory
		@Override
		public FreeXMLDocumentFactory getModelFactory() {
			return (FreeXMLDocumentFactory) super.getModelFactory();
		}

		@Override
		public String getXMLRepresentation() {
			if (getRootElement() != null) {
				return getRootElement().getXMLRepresentation();
			}
			return null;
		}

		@Override
		public String getName() {
			if (getResource() != null) {
				return getResource().getName();
			}
			else
				return "";
		}

	}

}
