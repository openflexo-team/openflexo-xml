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

import java.util.List;
import java.util.logging.Logger;

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

@ModelEntity
@ImplementationClass(XMLEnumerationType.XMLEnumerationTypeImpl.class)
public interface XMLEnumerationType extends XMLSimpleType {

	@PropertyIdentifier(type = XMLEnumValue.class, cardinality = Cardinality.LIST)
	public static final String ENUM_VALUES_KEY = "enumValues";

	@Getter(value = ENUM_VALUES_KEY, cardinality = Cardinality.LIST, inverse = XMLEnumValue.TYPE_KEY)
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	public List<XMLEnumValue> getEnumValues();

	@Adder(ENUM_VALUES_KEY)
	@PastingPoint
	public void addToEnumValues(XMLEnumValue aValue);

	@Remover(ENUM_VALUES_KEY)
	public void removeFromEnumValues(XMLEnumValue aValue);

	public static abstract class XMLEnumerationTypeImpl extends XMLSimpleTypeImpl implements XMLEnumerationType {

		private static final Logger logger = Logger.getLogger(XMLSimpleTypeImpl.class.getPackage().getName());

		@Override
		public String getDisplayableDescription() {
			return "Enumeration XML Type named : " + this.getName();
		}

		@Override
		public String toString() {
			return "[EnumerationType: " + getName() + " uri=" + getURI() + getEnumValues() + "]";
		}

	}

}
