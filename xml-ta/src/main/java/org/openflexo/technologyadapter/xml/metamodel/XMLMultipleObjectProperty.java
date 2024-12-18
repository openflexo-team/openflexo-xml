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
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.Initializer;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.Parameter;
import org.openflexo.technologyadapter.xml.XMLIndividualType;
import org.openflexo.technologyadapter.xml.model.typed.XMLIndividual;

/**
 * An {@link XMLObjectProperty} with a multiple cardinality
 * 
 * @author sylvain
 *
 */
@ModelEntity
@ImplementationClass(XMLMultipleObjectProperty.XMLMultipleObjectPropertyImpl.class)
public interface XMLMultipleObjectProperty extends XMLObjectProperty, XMLMultipleProperty<XMLComplexType, XMLIndividual> {

	@Initializer
	public XMLMultipleObjectProperty init(@Parameter(NAME) String s, @Parameter(TYPE_KEY) XMLComplexType type);

	public static abstract class XMLMultipleObjectPropertyImpl extends XMLObjectPropertyImpl implements XMLMultipleObjectProperty {

		@Override
		public String getDisplayableDescription() {
			StringBuffer buffer = new StringBuffer("XMLMultipleObjectProperty ");
			buffer.append(getName());
			buffer.append(" lowerBound=" + getLowerBound());
			buffer.append(" upperBound" + getUpperBound());
			return buffer.toString();
		}

		@Override
		public Type getAccessedType() {
			if (getType() != null) {
				return new ParameterizedTypeImpl(List.class, XMLIndividualType.getXMLIndividualOfType(getType()));
			}
			return new ParameterizedTypeImpl(List.class, XMLIndividualType.class);
		}

	}

}
