/**
 * 
 * Copyright (c) 2014-2015, Openflexo
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
import java.util.Date;
import java.util.logging.Logger;

public abstract class XMLSimpleTypeImpl extends XMLTypeImpl implements XMLSimpleType {

	private static final Logger logger = Logger.getLogger(XMLSimpleTypeImpl.class.getPackage().getName());

	@Override
	public Class<?> getImplementedInterface() {
		return XMLSimpleType.class;
	}

	@Override
	public String getDisplayableDescription() {
		return "Simple XML Type named : " + this.getName();
	}

	@Override
	public Type getJavaType() {
		if (getURI().equals(XMLMetaModel.STRING_URI)) {
			return String.class;
		}
		if (getURI().equals(XMLMetaModel.BOOLEAN_URI)) {
			return Boolean.class;
		}
		if (getURI().equals(XMLMetaModel.BYTE_URI)) {
			return Byte.class;
		}
		if (getURI().equals(XMLMetaModel.DATE_URI)) {
			return Date.class;
		}
		if (getURI().equals(XMLMetaModel.DECIMAL_URI)) {
			return Number.class;
		}
		if (getURI().equals(XMLMetaModel.DOUBLE_URI)) {
			return Double.class;
		}
		if (getURI().equals(XMLMetaModel.FLOAT_URI)) {
			return Float.class;
		}
		if (getURI().equals(XMLMetaModel.INT_URI)) {
			return Integer.class;
		}
		if (getURI().equals(XMLMetaModel.INTEGER_URI)) {
			return Integer.class;
		}
		if (getURI().equals(XMLMetaModel.LONG_URI)) {
			return Long.class;
		}
		if (getURI().equals(XMLMetaModel.SHORT_URI)) {
			return Short.class;
		}
		logger.warning("Unexpected " + getURI());
		return Object.class;
	}

}
