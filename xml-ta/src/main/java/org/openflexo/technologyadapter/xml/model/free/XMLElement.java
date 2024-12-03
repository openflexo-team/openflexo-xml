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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openflexo.foundation.fml.FlexoProperty;
import org.openflexo.pamela.annotations.Adder;
import org.openflexo.pamela.annotations.CloningStrategy;
import org.openflexo.pamela.annotations.CloningStrategy.StrategyType;
import org.openflexo.pamela.annotations.Embedded;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.Getter.Cardinality;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PastingPoint;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Remover;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.technologyadapter.xml.XMLObject;
import org.openflexo.technologyadapter.xml.rm.FreeXMLResource;

/**
 * Represents a plain XML element
 * 
 * @author sylvain
 */
@ModelEntity
@ImplementationClass(XMLElement.XMLElementImpl.class)
public interface XMLElement extends XMLObject<FreeXMLDocument> {

	@PropertyIdentifier(type = String.class)
	public static final String NAME_KEY = "name";
	@PropertyIdentifier(type = Object.class)
	public static final String VALUE_KEY = "value";
	@PropertyIdentifier(type = FreeXMLDocument.class)
	public static final String DOCUMENT_KEY = "document";
	@PropertyIdentifier(type = XMLElement.class)
	public static final String PARENT_ELEMENT_KEY = "parentElement";
	@PropertyIdentifier(type = FlexoProperty.class, cardinality = Cardinality.LIST)
	public static final String CHILD_ELEMENTS_KEY = "childElements";

	@Override
	@Getter(NAME_KEY)
	public String getName();

	@Setter(NAME_KEY)
	public void setName(String name);

	@Getter(value = VALUE_KEY, ignoreType = true)
	public Object getValue();

	@Setter(VALUE_KEY)
	public void setValue(Object value);

	@Getter(DOCUMENT_KEY)
	public FreeXMLDocument getDocument();

	@Setter(DOCUMENT_KEY)
	public void setDocument(FreeXMLDocument document);

	@Getter(PARENT_ELEMENT_KEY)
	public XMLElement getParentElement();

	@Setter(PARENT_ELEMENT_KEY)
	public void setParentElement(XMLElement parentElement);

	@Getter(value = CHILD_ELEMENTS_KEY, cardinality = Cardinality.LIST, inverse = PARENT_ELEMENT_KEY)
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	public List<XMLElement> getChildElements();

	@Setter(CHILD_ELEMENTS_KEY)
	public void setChildElements(List<XMLElement> properties);

	@Adder(CHILD_ELEMENTS_KEY)
	@PastingPoint
	public void addToChildElements(XMLElement anElement);

	@Remover(CHILD_ELEMENTS_KEY)
	public void removeFromChildElements(XMLElement anElement);

	public void setAttributeValue(String attributeName, Object value);

	public Object getAttributeValue(String attributeName);

	public Collection<String> getAttributes();

	public String getXMLRepresentation();

	/**
	 * Default implementation for {@link XMLElement}
	 * 
	 * @author sylvain
	 */

	public static abstract class XMLElementImpl extends XMLObjectImpl<FreeXMLDocument> implements XMLElement {

		private Map<String, Object> attributeValues;

		public XMLElementImpl() {
			attributeValues = new HashMap<String, Object>();
		}

		// Can be safely cast to FreeXMLResource
		@Override
		public FreeXMLResource getResource() {
			return (FreeXMLResource) super.getResource();
		}

		@Override
		public FreeXMLDocument getResourceData() {
			return getDocument();
		}

		// Can be safely cast to FreeXMLDocumentFactory
		@Override
		public FreeXMLDocumentFactory getModelFactory() {
			return (FreeXMLDocumentFactory) super.getModelFactory();
		}

		@Override
		public void setAttributeValue(String attributeName, Object value) {
			attributeValues.put(attributeName, value);
		}

		@Override
		public Object getAttributeValue(String attributeName) {
			return attributeValues.get(attributeName);
		}

		@Override
		public Collection<String> getAttributes() {
			return attributeValues.keySet();
		}

		@Override
		public String toString() {
			return "<" + getName() + " id=" + getFlexoID() + "/>";
		}

		@Override
		public String getXMLRepresentation() {
			StringBuilder sb = new StringBuilder();
			sb.append("<" + getName());
			for (String att : getAttributes()) {
				sb.append(" " + att + "=\"" + getAttributeValue(att) + "\"");
			}
			if (getChildElements().size() > 0) {
				sb.append(">");
				for (XMLElement child : getChildElements()) {
					sb.append("\n" + child.getXMLRepresentation());
				}
				sb.append("\n</" + getName() + ">");
			}
			else if (getValue() != null) {
				sb.append(">");
				sb.append(getValue());
				sb.append("</" + getName() + ">");
			}
			else {
				sb.append("/>");
			}
			return sb.toString();
		}
	}

}
