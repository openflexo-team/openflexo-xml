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

import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.Setter;

/**
 * 
 * Represents an XML property with a single cardinality
 * 
 * @author sylvain
 * 
 * @param <TT>
 *            {@link XMLType} reflected by this property
 * @param <T>
 *            actual run-time type beeing accessed though this property
 */
@ModelEntity(isAbstract = true)
public interface XMLSingleProperty<TT extends XMLType, T> extends XMLProperty<TT, T> {

	public static final String VALUE_KEY = "value";
	public static final String IS_REQUIRED_KEY = "isRequired";

	@Getter(value = VALUE_KEY, ignoreType = true)
	public T getValue();

	@Setter(VALUE_KEY)
	public void setValue(T aValue);

	@Getter(value = IS_REQUIRED_KEY, defaultValue = "false")
	public boolean isRequired();

	@Setter(IS_REQUIRED_KEY)
	public void setIsRequired(boolean required);
}
