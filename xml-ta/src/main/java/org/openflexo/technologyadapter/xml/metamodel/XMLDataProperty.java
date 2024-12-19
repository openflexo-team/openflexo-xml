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
import java.util.List;

import org.openflexo.connie.type.ParameterizedTypeImpl;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.Initializer;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.Parameter;
import org.openflexo.pamela.annotations.Setter;

/**
 * An {@link XMLProperty} with a simple type {@link XMLSimpleType}
 * 
 * @author sylvain, xtof
 *
 */
@ModelEntity
@ImplementationClass(XMLDataProperty.XMLDataPropertyImpl.class)
public interface XMLDataProperty<T> extends XMLProperty<XMLSimpleType, T> {

	@Initializer
	public XMLDataProperty<T> init(@Parameter(NAME) String s, @Parameter(TYPE_KEY) XMLSimpleType type);

	public static final String DEFAULT_VALUE_KEY = "defaultValue";
	public static final String FIXED_VALUE_KEY = "fixedValue";

	public boolean hasDefaultValue();

	@Getter(value = DEFAULT_VALUE_KEY, ignoreType = true)
	public T getDefaultValue();

	@Setter(DEFAULT_VALUE_KEY)
	public void setDefaultValue(T value);

	public boolean hasFixedValue();

	@Getter(value = FIXED_VALUE_KEY, ignoreType = true)
	public T getFixedValue();

	@Setter(FIXED_VALUE_KEY)
	public void setFixedValue(T value);

	/**
	 * Default implementation for {@link XMLDataProperty}
	 */
	public static abstract class XMLDataPropertyImpl<T> extends XMLPropertyImpl<XMLSimpleType, T> implements XMLDataProperty<T> {

		// TODO .... get anything from there
		// private final XSAttributeUse attributeUse = null;

		@Override
		public boolean hasFixedValue() {
			return getFixedValue() != null;
		}

		@Override
		public boolean hasDefaultValue() {
			return getDefaultValue() != null;
		}

		@Override
		public T getDefaultValue() {
			/*	if (attributeUse != null) {
					if (attributeUse.getDefaultValue() != null) {
						return attributeUse.getDefaultValue().toString();
					}
				}
			 */
			return null;
		}

		@Override
		public T getFixedValue() {
			/*
			if (attributeUse != null) {
				if (attributeUse.getFixedValue() != null) {
					return attributeUse.getFixedValue().toString();
				}
			}*/
			return null;
		}

		@Override
		public String getDisplayableDescription() {
			StringBuffer buffer = new StringBuffer("XMLDataProperty ");
			buffer.append(getName());
			buffer.append(" lowerBound=" + getLowerBound());
			buffer.append(" upperBound" + getUpperBound());
			if (hasDefaultValue()) {
				buffer.append(", default: '").append(getDefaultValue()).append("'");
			}
			if (hasFixedValue()) {
				buffer.append(", fixed: '").append(getFixedValue()).append("'");
			}
			return buffer.toString();
		}

		@Override
		public Type getAccessedType() {
			if (isMultiple()) {
				if (getType() != null) {
					return new ParameterizedTypeImpl(List.class, getType().getJavaType());
				}
				return new ParameterizedTypeImpl(List.class, Object.class);
			}
			else {
				if (getType() != null) {
					return getType().getJavaType();
				}
				return Object.class;

			}
		}

	}

}
