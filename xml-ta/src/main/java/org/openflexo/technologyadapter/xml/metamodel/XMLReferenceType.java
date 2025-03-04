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

import java.util.logging.Logger;

import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.Setter;

@ModelEntity
@ImplementationClass(XMLReferenceType.XMLReferenceTypeImpl.class)
public interface XMLReferenceType extends XMLSimpleType {

	public final String REFERENCED_TYPE = "referencedType";

	@Getter(REFERENCED_TYPE)
	public XMLSimpleType getReferencedType();

	@Setter(REFERENCED_TYPE)
	public void setReferencedType(XMLSimpleType t);

	public static abstract class XMLReferenceTypeImpl extends XMLSimpleTypeImpl implements XMLReferenceType {

		private static final Logger logger = Logger.getLogger(XMLSimpleTypeImpl.class.getPackage().getName());

		@Override
		public String getDisplayableDescription() {
			return "Reference XML Type named : " + this.getName();
		}

		@Override
		public String toString() {
			return "[ReferenceType: " + getName() + " uri=" + getURI() + "->" + getReferencedType() + "]";
		}

	}

}
