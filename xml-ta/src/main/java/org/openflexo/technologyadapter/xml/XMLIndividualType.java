/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2012-2012, AgileBirds
 * 
 * This file is part of Flexo-foundation, a component of the software infrastructure 
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

import java.lang.reflect.Type;
import java.util.Objects;

import org.openflexo.connie.type.ProxyType;
import org.openflexo.foundation.fml.TechnologyAdapterTypeFactory;
import org.openflexo.foundation.fml.TechnologySpecificType;
import org.openflexo.foundation.technologyadapter.SpecificTypeInfo;
import org.openflexo.foundation.utils.FlexoObjectReference;
import org.openflexo.foundation.utils.FlexoObjectReference.ReferenceOwner;
import org.openflexo.technologyadapter.xml.metamodel.XMLType;
import org.openflexo.technologyadapter.xml.model.XMLIndividual;
import org.openflexo.toolbox.PropertyChangedSupportDefaultImplementation;
import org.openflexo.toolbox.StringUtils;

/**
 * An type defined as an {@link XMLIndividual} of a given {@link XMLType}
 *
 * @author sylvain
 *
 */
public class XMLIndividualType extends PropertyChangedSupportDefaultImplementation implements TechnologySpecificType<XMLTechnologyAdapter> {

	// extends IndividualOfClass<XMLTechnologyAdapter, XMLIndividual, XMLType> {

	private final XMLType xmlType;

	public XMLIndividualType(XMLType xmlType) {
		this.xmlType = xmlType;
	}

	public XMLType getXMLType() {
		return xmlType;
	}

	@Override
	public String getSerializationRepresentation() {
		return new FlexoObjectReference<>(xmlType).getStringRepresentation();
	}

	@Override
	public XMLTechnologyAdapter getSpecificTechnologyAdapter() {
		if (getXMLType() != null) {
			return getXMLType().getTechnologyAdapter();
		}
		return null;
	}

	@Override
	public boolean isResolved() {
		return getXMLType() != null;
	}

	@Override
	public void resolve() {
	}

	@Override
	public void registerSpecificTypeInfo(SpecificTypeInfo<XMLTechnologyAdapter> typeInfo) {
		this.typeInfo = typeInfo;
	}

	public SpecificTypeInfo<XMLTechnologyAdapter> getSpecificTypeInfo() {
		return typeInfo;
	}

	private SpecificTypeInfo<XMLTechnologyAdapter> typeInfo;

	public static XMLIndividualType getXMLIndividualOfType(XMLType aXMLType) {
		if (aXMLType == null) {
			return null;
		}
		return aXMLType.getTechnologyAdapter().getTechnologyContextManager().getIndividualOfType(aXMLType);
	}

	public static XMLIndividualType UNDEFINED_XML_INDIVIDUAL_TYPE = new XMLIndividualType((XMLType) null);

	/**
	 * Factory for {@link XMLIndividualType} instances
	 * 
	 * @author sylvain
	 * 
	 */
	public static class XMLIndividualTypeFactory

			extends TechnologyAdapterTypeFactory<XMLIndividualType, XMLTechnologyAdapter> implements ReferenceOwner {

		// IndividualOfClassTypeFactory<XMLTechnologyAdapter, OWLIndividual, OWLClass, XMLIndividualType> implements ReferenceOwner {

		public XMLIndividualTypeFactory(XMLTechnologyAdapter technologyAdapter) {
			super(technologyAdapter);
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public Class<XMLIndividualType> getCustomType() {
			return XMLIndividualType.class;
		}

		public XMLIndividualType getIndividualOfType(XMLType type) {
			if (type == null) {
				return null;
			}
			return getTechnologyAdapter().getTechnologyContextManager().getIndividualOfType(type);
		}

		@Override
		public XMLIndividualType makeCustomType(String configuration) {

			FlexoObjectReference<XMLType> reference = new FlexoObjectReference<>(configuration, this);

			XMLType xmlType = reference.getObject();

			if (xmlType != null) {
				return getIndividualOfType(xmlType);
			}
			return null;
		}

		@Override
		public void configureFactory(XMLIndividualType type) {
		}

		@Override
		public void notifyObjectLoaded(FlexoObjectReference<?> reference) {
		}

		@Override
		public void objectCantBeFound(FlexoObjectReference<?> reference) {
		}

		@Override
		public void objectDeleted(FlexoObjectReference<?> reference) {
		}

		@Override
		public void objectSerializationIdChanged(FlexoObjectReference<?> reference) {
		}

	}

	@Override
	public Class<?> getBaseClass() {
		return XMLIndividual.class;
	}

	@Override
	public boolean isTypeAssignableFrom(Type aType, boolean permissive) {
		// System.out.println("isTypeAssignableFrom " + aType + " (i am a " + this + ")");
		if (aType instanceof ProxyType) {
			return isTypeAssignableFrom(((ProxyType) aType).getReferencedType(), permissive);
		}
		if (aType instanceof XMLIndividualType) {
			if (getXMLType() == null) {
				return ((XMLIndividualType) aType).getXMLType() == null;
			}
			return getXMLType().equals(((XMLIndividualType) aType).getXMLType());
		}
		System.out.println(
				"TODO !!!!! isTypeAssignableFrom() in XMLIndividualType " + getXMLType() + " for " + aType + " of " + aType.getClass());
		if (aType instanceof XMLIndividual) {
			// TODO: something better to do here !!!
			return true;
		}
		return false;
	}

	@Override
	public boolean isOfType(Object object, boolean permissive) {
		if (!(object instanceof XMLIndividual)) {
			return false;
		}
		// TODO please implement me
		return true;
	}

	@Override
	public String simpleRepresentation() {
		if (getSpecificTypeInfo() != null && StringUtils.isNotEmpty(getSpecificTypeInfo().getSerializationForm())) {
			return getSpecificTypeInfo().getSerializationForm();
		}
		return getXMLType() != null ? "XMLIndividualType(type=" + getXMLType().getName() + ")" : "XMLIndividual";
	}

	@Override
	public String fullQualifiedRepresentation() {
		if (getSpecificTypeInfo() != null && StringUtils.isNotEmpty(getSpecificTypeInfo().getSerializationForm())) {
			return getSpecificTypeInfo().getSerializationForm();
		}
		// return getClass().getName() + "(" + getSerializationRepresentation() + ")";
		return getClass().getName() + "(type=" + getXMLType().getName() + ")";
	}

	@Override
	public String toString() {
		return simpleRepresentation();
	}

	@Override
	public int hashCode() {
		return Objects.hash(xmlType);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		XMLIndividualType other = (XMLIndividualType) obj;
		return Objects.equals(xmlType, other.xmlType);
	}

}
