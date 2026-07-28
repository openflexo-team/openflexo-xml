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
import java.util.Collections;
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
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PastingPoint;
import org.openflexo.pamela.annotations.Remover;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.technologyadapter.xml.XMLIndividualType;
import org.openflexo.technologyadapter.xml.XMLObject;
import org.openflexo.technologyadapter.xml.metamodel.XMLComplexType;
import org.openflexo.technologyadapter.xml.metamodel.XMLProperty;
import org.openflexo.xml.XMLCst;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 
 * An {@link XMLIndividual} represents a single instance of XML Element in a {@link XMLModel} conform to an XSD file<br>
 * 
 * Such individual is typed with a {@link XMLComplexType}<br>
 * 
 * XML individuals are structured with a tree (see {@link #getParent()} and {@link #getChildren()} methods)
 * 
 * @author sylvain, xtof
 * 
 */
@ModelEntity
@ImplementationClass(XMLIndividual.XMLIndividualImpl.class)
public interface XMLIndividual extends XMLObject<XMLModel> {

	// TODO : manage the calculation of FQN

	public static final String UUID_KEY = "uuid";
	public static final String TYPE = "myType";
	public static final String MODEL = "containerModel";
	public static final String CHILD = "childrenByTypes";
	public static final String PARENT = "parent";
	public static final String PROPERTIES_VALUES = "propertiesValues";

	public static final String TEXT = "text";

	/**
	 * Property used to host XML's PCDATA textual content
	 */
	public static final String CONTENT = "contentDATA";

	// @Initializer
	// public XMLIndividual init(@Parameter(MODEL) XMLModel m, @Parameter(TYPE) XMLType t);

	@Getter(MODEL)
	public XMLModel getContainerModel();

	@Setter(MODEL)
	public void setContainerModel(XMLModel aModel);

	@Getter(value = TYPE, ignoreType = true)
	public XMLComplexType getType();

	@Setter(TYPE)
	public void setType(XMLComplexType aType);

	@Getter(UUID_KEY)
	public String getUUID();

	@Setter(UUID_KEY)
	public void setUUID(String uuid);

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

	/*
	@Getter(value = PROPERTIES_VALUES, cardinality = Cardinality.LIST)
	public List<? extends XMLPropertyValue<?, ?>> getPropertiesValues();
	
	@Adder(value = PROPERTIES_VALUES)
	public void addPropertyValue(XMLPropertyValue<?, ?> value);
	
	@Remover(value = PROPERTIES_VALUES)
	public void deletePropertyValue(XMLPropertyValue<?, ?> value);
	
	public XMLPropertyValue<?, ?> getPropertyValue(String propertyName);
	
	public <T> XMLPropertyValue<?, T> getSinglePropertyValue(XMLProperty<?, T> property);
	
	public <T> List<? extends XMLPropertyValue<?, T>> getMultiplePropertyValues(XMLProperty<?, T> property);*/

	public <T> T getPropertyValue(String propertyName);

	public <T> T getPropertyValue(XMLProperty<?, T> prop);

	public <T> List<T> getPropertyValues(String propertyName);

	public <T> List<T> getPropertyValues(XMLProperty<?, T> prop);

	public <T> void setPropertyValue(String propertyName, T value);

	public <T> void setPropertyValue(XMLProperty<?, T> prop, T value);

	public <T> void addPropertyValue(String propertyName, T value);

	public <T> void addPropertyValue(XMLProperty<?, T> prop, T value);

	public <T> void removePropertyValue(String propertyName, T value);

	public <T> void removePropertyValue(XMLProperty<?, T> prop, T value);

	// public String getPropertyStringValue(XMLProperty prop);

	@Getter(TEXT)
	@Deprecated // Not sure if this is a good idea
	public String getText();

	@Setter(TEXT)
	@Deprecated // Not sure if this is a good idea
	public void setText(String value);

	@Getter(CONTENT)
	@Deprecated // Not sure if this is a good idea
	public String getContentDATA();

	@Setter(CONTENT)
	@Deprecated // Not sure if this is a good idea
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

		private List<XMLIndividual> children;
		private Map<XMLComplexType, Set<XMLIndividualImpl>> childrenByTypes = null;
		// private Map<XMLProperty, XMLPropertyValue> propertiesValues = null;
		private final String uuid;

		private Map<XMLProperty<?, ?>, Object> singlePropertyValues;
		private Map<XMLProperty<?, ?>, List> multiplePropertyValues;

		/**
		 * Default Constructor
		 * 
		 * @param adapter
		 */
		public XMLIndividualImpl() {
			super();
			uuid = UUID.randomUUID().toString();
			children = new ArrayList();
			childrenByTypes = new HashMap<>();

			singlePropertyValues = new HashMap<>();
			multiplePropertyValues = new HashMap<>();

		}

		@Override
		public XMLModel getResourceData() {
			return getContainerModel();
		}

		/**
		 * Expose the {@link XMLIndividualType} reflecting this individual in FML type system.<br>
		 * Used to provide dynamic typing in FML-script (see {@code FMLUtils#inferType(Object)}), so that a variable assigned with a typed
		 * XML individual keeps its {@link XMLIndividualType} rather than falling back to its raw PAMELA proxy class.
		 */
		@Override
		public java.lang.reflect.Type getFMLType() {
			return XMLIndividualType.getXMLIndividualOfType(getType());
		}

		@Override
		public String getUUID() {
			String returned = (String) performSuperGetter(UUID_KEY);
			if (returned != null) {
				return returned;
			}
			return uuid;
		}

		@Override
		public void setUUID(String uuid) {
			String oldUUID = getUUID();
			performSuperSetter(UUID_KEY, uuid);
			if (getContainerModel() != null) {
				getContainerModel().changeUUID(this, oldUUID, uuid);
			}

		}

		@Override
		public String getContentDATA() {
			XMLProperty<?, String> attr = (XMLProperty<?, String>) this.getType().getPropertyByName(XMLCst.CDATA_ATTR_NAME);
			if (attr != null) {
				return getPropertyValue(attr);
			}
			return "";
		}

		@Override
		public void setContentDATA(String value) {
			XMLProperty attr = this.getType().getPropertyByName(XMLCst.CDATA_ATTR_NAME);
			if (attr != null) {
				setPropertyValue(XMLCst.CDATA_ATTR_NAME, value);
			}
		}

		@Override
		public String getName() {
			return getType().getName();
		}

		@Override
		public void removeChild(XMLIndividual anIndividual) {
			children.remove(anIndividual);
			childrenByTypes.get(anIndividual.getType()).remove(anIndividual);
		}

		@Override
		public void addChild(XMLIndividual anIndividual) {
			children.add(anIndividual);
			XMLComplexType aType = anIndividual.getType();
			Set<XMLIndividualImpl> typedSet = childrenByTypes.get(aType);

			if (typedSet == null) {
				typedSet = new HashSet<>();
				childrenByTypes.put(aType, typedSet);
			}
			typedSet.add((XMLIndividualImpl) anIndividual);
			anIndividual.setParent(this);
		}

		@Override
		public List<XMLIndividual> getChildren() {

			/*List<XMLIndividual> returned = new ArrayList<>();
			
			for (Set<XMLIndividualImpl> s : childrenByTypes.values()) {
				returned.addAll(s);
			}*/
			return children;
		}

		/*@Override
		public String getPropertyStringValue(XMLProperty prop) {
			XMLPropertyValue pv = propertiesValues.get(prop);
			if (pv != null) {
				return propertiesValues.get(prop).getStringValue();
			}
			return "";
		}*/

		@Override
		public <T> T getPropertyValue(String propertyName) {
			XMLProperty<?, T> property = (XMLProperty<?, T>) getType().getPropertyByName(propertyName);
			if (property == null) {
				logger.warning("Not found property: " + propertyName);
				return null;
			}
			return getPropertyValue(property);
		}

		@Override
		public <T> T getPropertyValue(XMLProperty<?, T> property) {
			if (property == null) {
				logger.warning("Null property");
				return null;
			}
			if (!property.isMultiple()) {
				return (T) singlePropertyValues.get(property);
			}
			else {
				logger.warning("Inconsistent data : called GET for a MULTIPLE property: " + property.getName());
				List<T> l = getPropertyValues(property);
				if (l != null && l.size() > 0) {
					return l.get(0);
				}
				return null;
			}
		}

		@Override
		public <T> List<T> getPropertyValues(String propertyName) {
			XMLProperty<?, T> property = (XMLProperty<?, T>) getType().getPropertyByName(propertyName);
			if (property == null) {
				logger.warning("Not found property: " + propertyName);
				return null;
			}
			return getPropertyValues(property);
		}

		@Override
		public <T> List<T> getPropertyValues(XMLProperty<?, T> property) {
			if (property == null) {
				logger.warning("Null property");
				return null;
			}
			if (property.isMultiple()) {
				return multiplePropertyValues.get(property);
			}
			else {
				logger.warning("Inconsistent data : called GET for a SINGLE property: " + property.getName());
				T item = getPropertyValue(property);
				if (item != null) {
					return Collections.singletonList(item);
				}
				return null;
			}
		}

		@Override
		public <T> void setPropertyValue(String propertyName, T value) {
			XMLProperty<?, T> property = (XMLProperty<?, T>) getType().getPropertyByName(propertyName);
			if (property == null) {
				logger.warning("Not found property: " + propertyName);
				return;
			}
			setPropertyValue(property, value);
		}

		@Override
		public <T> void setPropertyValue(XMLProperty<?, T> property, T value) {
			if (property == null) {
				logger.warning("Null property for value " + value);
				return;
			}
			if (!property.isMultiple()) {
				singlePropertyValues.put(property, value);
			}
			else {
				logger.warning("Inconsistent data : called SET for a MULTIPLE property: " + property.getName());
			}
		}

		@Override
		public <T> void addPropertyValue(String propertyName, T value) {
			XMLProperty<?, T> property = (XMLProperty<?, T>) getType().getPropertyByName(propertyName);
			if (property == null) {
				logger.warning("Not found property: " + propertyName);
				return;
			}
			addPropertyValue(property, value);
		}

		@Override
		public <T> void addPropertyValue(XMLProperty<?, T> property, T value) {
			if (property == null) {
				logger.warning("Null property for value " + value);
				return;
			}
			if (property.isMultiple()) {
				List<T> l = multiplePropertyValues.get(property);
				if (l == null) {
					l = new ArrayList<T>();
					multiplePropertyValues.put(property, l);
				}
				l.add(value);
			}
			else {
				logger.warning("Inconsistent data : called ADD for a SINGLE property: " + property.getName());
				setPropertyValue(property, value);
			}

		}

		@Override
		public <T> void removePropertyValue(String propertyName, T value) {
			XMLProperty<?, T> property = (XMLProperty<?, T>) getType().getPropertyByName(propertyName);
			if (property == null) {
				logger.warning("Not found property: " + propertyName);
				return;
			}
			removePropertyValue(property, value);
		}

		@Override
		public <T> void removePropertyValue(XMLProperty<?, T> property, T value) {
			if (property == null) {
				logger.warning("Null property for value " + value);
				return;
			}
			if (property.isMultiple()) {
				List<T> l = multiplePropertyValues.get(property);
				if (l != null) {
					l.remove(value);
				}
			}
			else {
				logger.warning("Inconsistent data : called ADD for a SINGLE property: " + property.getName());
				setPropertyValue(property, null);
			}

		}

		/*@Override
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
		public void addPropertyValue(XMLPropertyValue value) {
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
					// TODO Manage complex typesForURI and actual typesForURI for objects.
					prop = mm.getModelFactory().makeSingleDataProperty(name, (XMLSimpleType) mm.getTypeFromURI(XSDMetaModel.STRING_URI),
							false, XMLSupport.ELEMENT, name, getType());
		
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
		
		}*/

		/*@Override
		public <T> void addPropertyValue(XMLProperty<?, T> prop, T value) {
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
		
		}*/

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

		@Override
		public String toString() {
			return "XMLIndividual" + getFlexoID() + "[" + getType().getName() + "]";
		}

	}

}
