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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.pamela.annotations.Adder;
import org.openflexo.pamela.annotations.CloningStrategy;
import org.openflexo.pamela.annotations.CloningStrategy.StrategyType;
import org.openflexo.pamela.annotations.Embedded;
import org.openflexo.pamela.annotations.Finder;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.Getter.Cardinality;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PastingPoint;
import org.openflexo.pamela.annotations.Remover;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.technologyadapter.xml.model.AbstractXMLDocument;

@ModelEntity
@ImplementationClass(XSDMetaModel.XSDMetaModelImpl.class)
public interface XSDMetaModel extends AbstractXMLDocument<XSDMetaModel>, FlexoMetaModel<XSDMetaModel> {

	// extends AbstractXMLDocument<XMLModel>, FlexoModel<XMLModel, XSDMetaModel> {

	/*public static final String RSC = "resource";
	
	@Override
	@Getter(RSC)
	public FlexoResource<XSDMetaModel> getResource();
	
	@Override
	@Setter(RSC)
	public void setResource(FlexoResource<XSDMetaModel> resource);*/

	/*
	 * 
	public IFlexoOntologyDataProperty getDataProperty(String propertyURI);
	*/

	public static String TYPES = "types";
	public static String READ_ONLY = "readOnly";

	// static simple Types URI
	public static String ANY_TYPE_URI = "xs:anyType";
	public static String STRING_URI = "xs:string";
	public static String BOOLEAN_URI = "xs:boolean";
	public static String BYTE_URI = "xs:byte";
	public static String DATE_URI = "xs:date";
	public static String DECIMAL_URI = "xs:decimal";
	public static String DOUBLE_URI = "xs:double";
	public static String FLOAT_URI = "xs:float";
	public static String INT_URI = "xs:int";
	public static String INTEGER_URI = "xs:integer";
	public static String LONG_URI = "xs:long";
	public static String SHORT_URI = "xs:short";

	@Getter(value = TYPES, cardinality = Cardinality.LIST, inverse = XMLType.MM)
	@CloningStrategy(StrategyType.IGNORE)
	@Embedded
	public List<? extends XMLType> getTypes();

	@Finder(attribute = XMLType.URI, collection = TYPES, isMultiValued = true)
	public XMLType getTypeFromURI(String string);

	// public XMLType getTypeFromURI(String string, boolean createsWhenNonExistant);

	/**
	 * Creates a new type in this MetaModel, simple or complex, depending on the parameters
	 * 
	 * @param uri
	 * @param localName
	 * @param simpleType
	 * @return
	 */
	// public XMLType createNewType(String uri, String localName, boolean simpleType);

	@Adder(TYPES)
	@PastingPoint
	public void addToTypes(XMLType aType);

	@Remover(TYPES)
	public void removeFromTypes(XMLType aType);

	@Override
	@Getter(value = READ_ONLY, defaultValue = "true")
	public boolean isReadOnly();

	@Setter(READ_ONLY)
	public void setReadOnly(boolean value);

	@Override
	public XSDMetaModelFactory getModelFactory();

	public static abstract class XSDMetaModelImpl extends AbstractXMLDocumentImpl<XSDMetaModel> implements XSDMetaModel {

		private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger
				.getLogger(XSDMetaModelImpl.class.getPackage().getName());

		protected Map<String, XMLType> types = null;

		public XSDMetaModelImpl() {
			types = new HashMap<>();
		}

		@Override
		public XSDMetaModelFactory getModelFactory() {
			return (XSDMetaModelFactory) super.getModelFactory();
		}

		// private XSDMetaModelResource xsdResource;

		// private static PamelaModelFactory MF;

		/*static {
			try {
				MF = new PamelaModelFactory(XSDMetaModel.class);
			} catch (ModelDefinitionException e) {
				e.printStackTrace();
			}
		}
		
		public static PamelaModelFactory getModelFactory() {
			return MF;
		}*/

		/*@Override
		public FlexoResource<XSDMetaModel> getResource() {
			return xsdResource;
		}
		
		@Override
		public void setResource(FlexoResource<XSDMetaModel> resource) {
			this.xsdResource = (XSDMetaModelResource) resource;
		}*/

		@Override
		public boolean isReadOnly() {
			return true;
		}

		@Override
		public void setIsReadOnly(boolean b) {
		}

		/*@Override
		public XMLTechnologyAdapter getTechnologyAdapter() {
			if (this.xsdResource != null)
				return xsdResource.getTechnologyAdapter();
			return null;
		}*/

		@Override
		public String getName() {
			return this.getURI();
		}

		/*@Override
		public Class<?> getImplementedInterface() {
			return XSDMetaModel.class;
		}*/

		// private static PamelaModelFactory MF;

		/*public XMLMetaModelImpl() {
			super();
			types = new HashMap<>();
		}*/

		/*static {
			try {
				MF = new PamelaModelFactory(PamelaMetaModelLibrary.retrieveMetaModel(XMLModel.class, XMLType.class, XMLComplexType.class,
						XMLSimpleType.class, XMLProperty.class, XMLDataProperty.class, XMLObjectProperty.class));
			} catch (ModelDefinitionException e) {
				e.printStackTrace();
			}
		}
		
		public static PamelaModelFactory getModelFactory() {
			return MF;
		}*/

		@Override
		public XMLType getTypeFromURI(String uri) {
			return types.get(uri);

			/*if (t == null) {
			System.out.println("Tous les types que je connais");
			for (XMLType xmlType : types.values()) {
				System.out.println(" > " + xmlType + " of " + xmlType.getClass() + " uri=" + xmlType.getURI());
			}
			}*/

			// System.out.println("On cherche un type " + uri);
			// return getTypeFromURI(uri, true);
		}

		/*@Override
		public XMLType getTypeFromURI(String uri, boolean createsWhenNonExistant) {
		
			XMLType t = types.get(uri);
		
			if (t == null && createsWhenNonExistant) {
				return createNewType(uri, uri, true);
			}
			return t;
		
		}*/

		@Override
		public void addToTypes(XMLType aType) {
			types.put(aType.getURI(), aType);
			aType.setMetamodel(this);
		}

		@Override
		public void removeFromTypes(XMLType aType) {
			types.remove(aType);
			aType.setMetamodel(null);
		}

		@Override
		public List<? extends XMLType> getTypes() {
			// TODO: perf issue
			return new ArrayList<>(types.values());
		}

		/*@Override
		public XMLType createNewType(String uri, String localName, boolean simpleType) {
		
			XMLType aType = null;
			if (simpleType) {
				System.out.println("On cree un XMLSimpleType pour " + uri);
				aType = getModelFactory().newInstance(XMLSimpleType.class, this);
			}
			else {
				aType = getModelFactory().newInstance(XMLComplexType.class, this);
			}
			aType.setIsAbstract(false);
			aType.setURI(uri);
			aType.setName(localName);
		
			addToTypes(aType);
		
			return aType;
		}*/

		/**
		 * 
		 * creates a new empty MetaModel
		 * 
		 * @return
		 */
		/*public static XSDMetaModel createEmptyMetaModel(String uri) {
		
			XSDMetaModel metamodel = MF.newInstance(XSDMetaModel.class);
			metamodel.setReadOnly(false);
		
			metamodel.setURI(uri);
		
			return metamodel;
		
		}*/

		@Override
		public String getDisplayableDescription() {
			return null;
		}

		/*@Override
		public XMLTechnologyAdapter getTechnologyAdapter() {
			return null;
		}*/

		@Override
		public Object getObject(String objectURI) {
			return null;
		}

		/*@Override
		public FlexoResource<MM> getResource() {
			return null;
		}
		
		@Override
		public void setResource(FlexoResource<MM> resource) {
		
		}*/

	}

}
