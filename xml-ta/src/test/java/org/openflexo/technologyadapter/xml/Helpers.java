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

package org.openflexo.technologyadapter.xml;

import java.util.List;

import org.openflexo.technologyadapter.xml.metamodel.XMLComplexType;
import org.openflexo.technologyadapter.xml.metamodel.XMLDataProperty;
import org.openflexo.technologyadapter.xml.metamodel.XMLEnumValue;
import org.openflexo.technologyadapter.xml.metamodel.XMLEnumerationType;
import org.openflexo.technologyadapter.xml.metamodel.XMLObjectProperty;
import org.openflexo.technologyadapter.xml.metamodel.XMLProperty;
import org.openflexo.technologyadapter.xml.metamodel.XMLSimpleType;
import org.openflexo.technologyadapter.xml.metamodel.XMLType;
import org.openflexo.technologyadapter.xml.metamodel.XSDMetaModel;
import org.openflexo.technologyadapter.xml.model.typed.XMLIndividual;

public class Helpers {

	/**
	 * Prints all typesForURI...
	 * 
	 * @param metamodel
	 */
	public static final void dumpTypes(XSDMetaModel metamodel) {

		System.out.println("\n\n");

		System.out.println("Complex typesForURI : ");
		for (XMLType t : metamodel.getTypes()) {
			if (t instanceof XMLComplexType) {
				System.out.println(" - " + t.getName() + (t.getSuperType() != null ? " extends " + t.getSuperType().getName() : "") + " ["
						+ t.getURI() + "]" + (t.isAbstract() ? "[Abstract]" : ""));
				for (XMLProperty<?, ?> x : ((XMLComplexType) t).getProperties()) {
					XMLType pt = x.getType();
					if (pt instanceof XMLSimpleType) {
						System.out.println("    -- data: " + x.getName() + " :: " + pt.getName() + " [" + pt.getURI() + "]" + " ["
								+ x.getXMLSupport() + "/" + x.getXMLSupportName() + "]");
					}
					else {
						String card = "(" + x.getLowerBound() + "-" + (x.getUpperBound() == -1 ? "*" : x.getUpperBound()) + ")";
						System.out.println("    -- obj:  " + card + " " + x.getName() + " :: " + pt.getName() + " [" + pt.getURI() + "]"
								+ " [" + x.getXMLSupport() + "/" + x.getXMLSupportName() + "]");
					}
				}
			}
		}

		System.out.println("Simple typesForURI : ");
		for (XMLType t : metamodel.getTypes()) {
			if (t instanceof XMLSimpleType) {
				System.out.println(" - " + t.getName() + (t instanceof XMLEnumerationType ? enumValues((XMLEnumerationType) t) : "") + " ["
						+ t.getURI() + "]" + (t.isAbstract() ? "[Abstract]" : ""));
			}
		}

	}

	private static String enumValues(XMLEnumerationType t) {
		StringBuffer sb = new StringBuffer();
		sb.append("(");
		boolean isFirst = true;
		for (XMLEnumValue xmlEnumValue : t.getEnumValues()) {
			sb.append((isFirst ? "" : ",") + xmlEnumValue.getName());
			isFirst = false;
		}
		sb.append(")");
		return sb.toString();
	}

	/**
	 * Prints all the property values of an individual
	 */

	public static final void dumpProperties(XMLIndividual indiv, XMLType aType, String prefix) {
		if (aType == null) {
			dumpProperties(indiv, indiv.getType(), prefix);
		}
		else {
			if (aType instanceof XMLComplexType) {
				for (XMLProperty<?, ?> prop : ((XMLComplexType) aType).getProperties()) {
					if (prop instanceof XMLDataProperty) {
						Object val = indiv.getPropertyValue(prop);
						if (val != null) {
							System.out.println(prefix + "    * attr: " + prop.getName() + " = " + indiv.getPropertyValue(prop).toString());
						}
						else {
							System.out.println(prefix + "    ! attr: " + prop.getName() + " n'est pas valuée");
						}
					}
					else if (prop instanceof XMLObjectProperty) {
						System.out.println(prefix + "    * obj: " + prop.getName());
						List<XMLIndividual> vals = indiv.getPropertyValues((XMLObjectProperty) prop);
						if (vals != null) {
							for (XMLIndividual v : vals) {
								dumpIndividual(v, prefix + "          + ");
							}
						}
						else {
							System.out.println(" !! Etrange, la propriete " + prop.getName() + " ne contient rien ?!?");
						}
					}
				}

			}
			if (aType.getSuperType() != null) {
				dumpProperties(indiv, aType.getSuperType(), prefix);
			}
		}
	}

	/**
	 * Prints all individuals
	 * 
	 */

	public static final void dumpIndividual(XMLIndividual indiv, String prefix) {

		System.out.println(prefix + "Indiv : " + indiv.getName());
		dumpProperties(indiv, null, prefix);

		for (XMLIndividual x : indiv.getChildren())
			dumpIndividual(x, prefix + "    [C]");

		System.out.flush();

	}

}
