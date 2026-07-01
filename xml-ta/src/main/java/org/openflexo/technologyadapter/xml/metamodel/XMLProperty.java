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

package org.openflexo.technologyadapter.xml.metamodel;

import java.lang.reflect.Type;

import org.openflexo.foundation.InnerResourceData;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.Import;
import org.openflexo.pamela.annotations.Imports;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.technologyadapter.xml.XMLObject;

/**
 * 
 * Represents an XML property in an XMLModel, as a property of a {@link XMLComplexType}
 * 
 * Such a property has basically a name and a type, and is based on a {@link XMLSupport}
 * 
 * @author sylvain, xtof
 * 
 * @param <TT>
 *            {@link XMLType} reflected by this property
 * @param <T>
 *            actual run-time type beeing accessed though this property
 */
@ModelEntity(isAbstract = true)
@ImplementationClass(XMLProperty.XMLPropertyImpl.class)
@Imports({ @Import(XMLDataProperty.class), @Import(XMLObjectProperty.class) })
public interface XMLProperty<TT extends XMLType, T>
		extends XMLObject<XSDMetaModel>, Comparable<XMLProperty<TT, T>>, InnerResourceData<XSDMetaModel> {

	public static final String TYPE_KEY = "type";
	public static final String CONTAINER_KEY = "container";

	public static final String LOWER_BOUND_KEY = "lowerBound";
	public static final String UPPER_BOUND_KEY = "upperBound";

	public static final String XML_SUPPORT_KEY = "xmlSupport";
	public static final String XML_SUPPORT_NAME_KEY = "xmlSupportName";

	/**
	 * XML serialization support for a {@link XMLProperty} (element, attribute or CDATA)
	 */
	public static enum XMLSupport {
		ELEMENT, ATTRIBUTE, CDATA
	}

	@Getter(CONTAINER_KEY)
	public XMLComplexType getContainer();

	@Setter(CONTAINER_KEY)
	public void setContainer(XMLComplexType container);

	@Getter(value = TYPE_KEY, ignoreType = true)
	public TT getType();

	@Setter(TYPE_KEY)
	public void setType(TT aType);

	/**
	 * Return type beeing reflected by access of this property
	 * 
	 * @return
	 */
	public Type getAccessedType();

	@Getter(value = LOWER_BOUND_KEY, ignoreType = true)
	public Integer getLowerBound();

	@Setter(LOWER_BOUND_KEY)
	public void setLowerBound(Integer b);

	@Getter(value = UPPER_BOUND_KEY, ignoreType = true)
	public Integer getUpperBound();

	@Setter(UPPER_BOUND_KEY)
	public void setUpperBound(Integer b);

	/**
	 * Returns true if this property was created from an XML element and false if from an XMLAttribute
	 * 
	 * @return
	 */
	@Getter(XML_SUPPORT_KEY)
	public XMLSupport getXMLSupport();

	@Setter(XML_SUPPORT_KEY)
	public void setXMLSupport(XMLSupport xmlSupport);

	/**
	 * Return the name of the element or attribute used as XMLSupport<br>
	 * <ul>
	 * <li>If {@link XMLSupport} is ELEMENT : this is the name of the Element</li>
	 * <li>If {@link XMLSupport} is ATTRIBUTE : this is the name of the Attribute</li>
	 * </ul>
	 * 
	 * @return
	 */
	@Getter(XML_SUPPORT_NAME_KEY)
	public String getXMLSupportName();

	@Setter(XML_SUPPORT_NAME_KEY)
	public void setXMLSupportName(String aName);

	public boolean isRequired();

	public boolean isMultiple();

	/**
	 * Default implementation for {@link XMLProperty}
	 */
	public static abstract class XMLPropertyImpl<TT extends XMLType, T> extends XMLObjectImpl<XSDMetaModel> implements XMLProperty<TT, T> {

		@Override
		public int compareTo(XMLProperty<TT, T> arg0) {
			String thisName = getName();
			String otherName = arg0 != null ? arg0.getName() : null;

			if (thisName == null) {
				return otherName == null ? 0 : -1;
			}

			if (otherName == null) {
				return 1;
			}

			return thisName.compareTo(otherName);
		}

		@Override
		public XSDMetaModel getResourceData() {
			if (getContainer() != null) {
				return getContainer().getMetamodel();
			}
			return null;
		}

		@Override
		public boolean isRequired() {
			if (getLowerBound() != null && getLowerBound() >= 1) {
				return true;
			}
			return false;
		}

		@Override
		public boolean isMultiple() {
			if (getUpperBound() != null && (getUpperBound() > 1 || getUpperBound() == -1)) {
				return true;
			}
			return false;
		}
	}

}
