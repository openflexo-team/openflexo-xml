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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.openflexo.pamela.annotations.Adder;
import org.openflexo.pamela.annotations.CloningStrategy;
import org.openflexo.pamela.annotations.CloningStrategy.StrategyType;
import org.openflexo.pamela.annotations.Embedded;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.Getter.Cardinality;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.Initializer;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.Parameter;
import org.openflexo.pamela.annotations.PastingPoint;
import org.openflexo.pamela.annotations.Remover;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.technologyadapter.xml.XMLObject;
import org.openflexo.technologyadapter.xml.metamodel.XMLComplexType;
import org.openflexo.technologyadapter.xml.metamodel.XMLDataProperty;
import org.openflexo.technologyadapter.xml.metamodel.XMLObjectProperty;
import org.openflexo.technologyadapter.xml.metamodel.XMLProperty;
import org.openflexo.technologyadapter.xml.metamodel.XMLProperty.XMLSupport;
import org.openflexo.technologyadapter.xml.metamodel.XMLType;
import org.openflexo.technologyadapter.xml.metamodel.XSDMetaModel;
import org.openflexo.xml.XMLCst;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 
 * an XMLIndividual represents a single instance of XML Element in a XMLModel
 * 
 * 
 * @author xtof
 * 
 */
@ModelEntity
@ImplementationClass(XMLIndividual.XMLIndividualImpl.class)
public interface XMLIndividual extends XMLObject<XMLModel> {

	// TODO : manage the calculation of FQN

	public static final String _UUID = "uuid";
	public static final String TYPE = "myType";
	public static final String MODEL = "containerModel";
	public static final String CHILD = "children";
	public static final String PARENT = "parent";
	public static final String PROPERTIES_VALUES = "propertiesValues";

	public static final String TEXT = "text";

	/**
	 * Property used to host XML's PCDATA textual content
	 */
	public static final String CONTENT = "contentDATA";

	@Initializer
	public XMLIndividual init(@Parameter(MODEL) XMLModel m, @Parameter(TYPE) XMLType t);

	@Getter(MODEL)
	public XMLModel getContainerModel();

	@Getter(TYPE)
	public XMLComplexType getType();

	@Setter(TYPE)
	public void setType(XMLComplexType aType);

	@Getter(_UUID)
	public String getUUID();

	@Getter(PARENT)
	public XMLIndividual getParent();

	@Setter(PARENT)
	public void setParent(XMLIndividual xmlind);

	@Getter(value = CHILD, cardinality = Cardinality.LIST, inverse = PARENT)
	@CloningStrategy(StrategyType.IGNORE)
	@Embedded
	public List<XMLIndividual> getChildren();

	@Remover(CHILD)
	public void removeChild(XMLIndividual ind);

	@Adder(CHILD)
	@PastingPoint
	public void addChild(XMLIndividual ind);

	@Getter(value = PROPERTIES_VALUES, cardinality = Cardinality.LIST)
	public List<? extends XMLPropertyValue> getPropertiesValues();

	public XMLPropertyValue getPropertyValue(String pname);

	public XMLPropertyValue getPropertyValue(XMLProperty prop);

	public String getPropertyStringValue(XMLProperty prop);

	@Adder(value = PROPERTIES_VALUES)
	public void addPropertyValue(/*XMLProperty prop,*/ XMLPropertyValue value);

	public void addPropertyValue(String name, Object value);

	public void addPropertyValue(XMLProperty prop, Object value);

	@Remover(value = PROPERTIES_VALUES)
	// public void deletePropertyValues(XMLProperty attr);
	public void deletePropertyValues(XMLPropertyValue value);

	@Getter(TEXT)
	public String getText();

	@Setter(TEXT)
	public void setText(String value);

	@Getter(CONTENT)
	public String getContentDATA();

	@Setter(CONTENT)
	public void setContentDATA(String value);

	// TODO : refactor to get rid of any JDOM reference
	public Element toXML(Document doc);

	/**
	 * 
	 * Default implementation for {@link XMLIndividual}
	 * 
	 * @author sylvain, xtof
	 * 
	 */
	public static abstract class XMLIndividualImpl extends XMLObjectImpl<XMLModel> implements XMLIndividual {

		private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger
				.getLogger(XMLIndividualImpl.class.getPackage().getName());

		/* Properties */

		private Map<XMLComplexType, Set<XMLIndividualImpl>> children = null;
		private Map<XMLProperty, XMLPropertyValue> propertiesValues = null;
		private final String uuid;

		/**
		 * Default Constructor
		 * 
		 * @param adapter
		 */
		public XMLIndividualImpl() {
			super();
			uuid = UUID.randomUUID().toString();
			propertiesValues = new HashMap<>();
			children = new HashMap<>();
		}

		@Override
		public String getUUID() {
			return uuid;
		}

		@Override
		public String getContentDATA() {
			XMLProperty attr = this.getType().getPropertyByName(XMLCst.CDATA_ATTR_NAME);
			if (attr != null) {
				return this.getPropertyStringValue(attr);
			}
			return "";
		}

		@Override
		public void setContentDATA(String value) {
			XMLProperty attr = this.getType().getPropertyByName(XMLCst.CDATA_ATTR_NAME);
			if (attr != null) {
				addPropertyValue(XMLCst.CDATA_ATTR_NAME, value);
			}
		}

		@Override
		public String getName() {
			return getType().getName();
		}

		@Override
		public void removeChild(XMLIndividual indiv) {
			children.get(indiv.getType()).remove(indiv);
		}

		@Override
		public void addChild(XMLIndividual anIndividual) {
			XMLComplexType aType = anIndividual.getType();
			Set<XMLIndividualImpl> typedSet = children.get(aType);

			if (typedSet == null) {
				typedSet = new HashSet<>();
				children.put(aType, typedSet);
			}
			typedSet.add((XMLIndividualImpl) anIndividual);
			((XMLIndividualImpl) anIndividual).setParent(this);
		}

		@Override
		public List<XMLIndividual> getChildren() {

			List<XMLIndividual> returned = new ArrayList<>();

			for (Set<XMLIndividualImpl> s : children.values()) {
				returned.addAll(s);
			}
			return returned;
		}

		@Override
		public String getPropertyStringValue(XMLProperty prop) {
			XMLPropertyValue pv = propertiesValues.get(prop);
			if (pv != null) {
				return propertiesValues.get(prop).getStringValue();
			}
			return "";
		}

		@Override
		public List<? extends XMLPropertyValue> getPropertiesValues() {
			return new ArrayList<XMLPropertyValue>(propertiesValues.values());
		}

		@Override
		public XMLPropertyValue getPropertyValue(String attributeName) {

			XMLProperty attr = getType().getPropertyByName(attributeName);

			if (attr != null) {
				return propertiesValues.get(attr);
			}
			return null;
		}

		@Override
		public XMLPropertyValue getPropertyValue(XMLProperty prop) {

			if (prop != null) {
				return propertiesValues.get(prop);
			}
			return null;

		}

		@Override
		public void addPropertyValue(/*XMLProperty attr,*/ XMLPropertyValue value) {
			// TODO
		}

		@Override
		public void deletePropertyValues(XMLPropertyValue value) {
			// TODO
		}

		@Override
		public void addPropertyValue(String name, Object value) {

			XMLProperty prop = getType().getPropertyByName(name);

			if (prop == null) {
				XSDMetaModel mm = getContainerModel().getMetaModel();
				if (!mm.isReadOnly()) {
					// TODO Manage complex types and actual types for objects.
					prop = this.getType().createProperty(name, mm.getTypeFromURI(XSDMetaModel.STRING_URI), XMLSupport.ELEMENT, name);
				}
				else {
					logger.warning("CANNOT give a value  for a non existant attribute :" + name);
				}
			}
			if (prop != null) {
				XMLPropertyValue vals = propertiesValues.get(prop);

				if (vals == null) {

					if (prop instanceof XMLDataProperty) {
						vals = getContainerModel().getModelFactory().makeXMLDataPropertyValue((XMLDataProperty) prop, value);
						propertiesValues.put(prop, vals);
					}
					else {
						// TODO..... complex attributes, collections
					}
				}

				else {
					// TODO..... manage this case also
				}
			}

		}

		@Override
		public void addPropertyValue(XMLProperty prop, Object value) {
			XMLPropertyValue val = propertiesValues.get(prop);

			if (val == null) {

				if (prop instanceof XMLDataProperty) {
					val = getContainerModel().getModelFactory().makeXMLDataPropertyValue((XMLDataProperty) prop, value);
					propertiesValues.put(prop, val);
				}
				else if (prop instanceof XMLObjectProperty) {
					val = getContainerModel().getModelFactory().makeXMLObjectPropertyValue((XMLObjectProperty) prop, (XMLIndividual) value);
					propertiesValues.put(prop, val);
				}
			}

			if (val != null) {
				if (prop instanceof XMLDataProperty) {
					((XMLDataPropertyValue) val).setValue(value);
				}
				else if (prop instanceof XMLObjectProperty) {
					((XMLObjectPropertyValue) val).addToValues((XMLIndividual) value);
				}
			}

		}

		/* (non-Javadoc)
		 * @see org.openflexo.technologyadapter.xml.model.IXMLIndividual#toXML(org.w3c.dom.Document)
		 */
		@Override
		public Element toXML(Document doc) {
			String nsURI = getType().getURI();
			Element element = null;
			if (nsURI != null) {
				element = doc.createElementNS(nsURI, getType().getFullyQualifiedName());
			}
			else {
				element = doc.createElement(getType().getName());
			}

			for (XMLIndividual i : getChildren()) {
				element.appendChild(i.toXML(doc));
			}

			// TODO dump attributes !!!

			return element;
		}

		@Override
		public String getDisplayableDescription() {
			return "XML Individual of type: " + getName();

		}

	}

}
