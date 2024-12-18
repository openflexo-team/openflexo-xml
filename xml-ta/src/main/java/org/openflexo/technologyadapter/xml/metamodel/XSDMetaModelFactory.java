/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Flexodiagram, a component of the software infrastructure 
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

import org.openflexo.pamela.PamelaMetaModelLibrary;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.pamela.factory.EditingContext;
import org.openflexo.pamela.factory.PamelaModelFactory;
import org.openflexo.technologyadapter.xml.metamodel.XMLProperty.XMLSupport;
import org.openflexo.technologyadapter.xml.model.AbstractXMLDocumentFactory;
import org.openflexo.technologyadapter.xml.model.free.FreeXMLDocument;
import org.openflexo.technologyadapter.xml.rm.FreeXMLResource;
import org.openflexo.technologyadapter.xml.rm.XSDMetaModelResource;

/**
 * A {@link PamelaModelFactory} used to manage {@link FreeXMLDocument}
 * 
 * One instance of this class should be used for each {@link FreeXMLResource}
 * 
 * @author sylvain
 * 
 */
public class XSDMetaModelFactory extends AbstractXMLDocumentFactory<XSDMetaModelResource, XSDMetaModel, XSDMetaModelFactory> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(XSDMetaModelFactory.class.getPackage().getName());

	public XSDMetaModelFactory(XSDMetaModelResource resource, EditingContext editingContext) throws ModelDefinitionException {
		super(PamelaMetaModelLibrary.retrieveMetaModel(XSDMetaModel.class), resource, editingContext);
	}

	public XSDMetaModel makeXSDMetaModel() {
		return newInstance(XSDMetaModel.class);
	}

	public XMLComplexType makeComplexType(String uri, String localName, XSDMetaModel metaModel) {
		// System.out.println("Creating a XMLComplexType " + localName + " " + uri);
		XMLComplexType returned = newInstance(XMLComplexType.class);
		returned.setIsAbstract(false);
		returned.setURI(uri);
		returned.setName(localName);
		metaModel.addToTypes(returned);
		return returned;
	}

	public XMLSimpleType makeSimpleType(String uri, String localName, XSDMetaModel metaModel) {
		// System.out.println("Creating a XMLSimpleType " + localName + " " + uri);
		XMLSimpleType returned = newInstance(XMLSimpleType.class);
		returned.setIsAbstract(false);
		returned.setURI(uri);
		returned.setName(localName);
		metaModel.addToTypes(returned);
		return returned;
	}

	public XMLEnumerationType makeEnumerationType(String uri, String localName, XSDMetaModel metaModel) {
		// System.out.println("Creating a XMLEnumerationType " + localName + " " + uri);
		XMLEnumerationType returned = newInstance(XMLEnumerationType.class);
		returned.setIsAbstract(false);
		returned.setURI(uri);
		returned.setName(localName);
		metaModel.addToTypes(returned);
		return returned;
	}

	public XMLEnumValue makeEnumValue(String name, XMLEnumerationType type) {
		XMLEnumValue returned = newInstance(XMLEnumValue.class);
		returned.setName(name);
		type.addToEnumValues(returned);
		return returned;
	}

	public XMLSingleObjectProperty makeSingleObjectProperty(String name, XMLComplexType aType, boolean isRequired, String elementName,
			XMLComplexType container) {
		XMLSingleObjectProperty returned = newInstance(XMLSingleObjectProperty.class, name, aType);
		container.addToProperties(returned);
		returned.setIsRequired(isRequired);
		returned.setXMLSupport(XMLSupport.ELEMENT);
		returned.setXMLSupportName(elementName);
		return returned;
	}

	/**
	 * 
	 * @param name
	 * @param aType
	 * @param elementName
	 * @param lowerBound
	 * @param upperBound
	 *            -1 for multiple cardinality
	 * @param container
	 * @return
	 */
	public XMLMultipleObjectProperty makeMultipleObjectProperty(String name, XMLComplexType aType, Integer lowerBound, Integer upperBound,
			String elementName, XMLComplexType container) {
		XMLMultipleObjectProperty returned = newInstance(XMLMultipleObjectProperty.class, name, aType);
		returned.setLowerBound(lowerBound);
		returned.setUpperBound(upperBound);
		container.addToProperties(returned);
		returned.setXMLSupport(XMLSupport.ELEMENT);
		returned.setXMLSupportName(elementName);
		return returned;
	}

	public XMLSingleDataProperty<?> makeSingleDataProperty(String name, XMLSimpleType aType, boolean isRequired, XMLSupport xmlSupport,
			String xmlSupportName, XMLComplexType container) {
		XMLSingleDataProperty<?> returned = newInstance(XMLSingleDataProperty.class, name, aType);
		container.addToProperties(returned);
		returned.setIsRequired(isRequired);
		returned.setXMLSupport(xmlSupport);
		returned.setXMLSupportName(xmlSupportName);
		return returned;
	}

	public XMLMultipleDataProperty<?> makeMultipleDataProperty(String name, XMLSimpleType aType, Integer lowerBound, Integer upperBound,
			XMLSupport xmlSupport, String xmlSupportName, XMLComplexType container) {
		XMLMultipleDataProperty<?> returned = newInstance(XMLMultipleDataProperty.class, name, aType);
		returned.setLowerBound(lowerBound);
		returned.setUpperBound(upperBound);
		container.addToProperties(returned);
		returned.setXMLSupport(xmlSupport);
		returned.setXMLSupportName(xmlSupportName);
		return returned;
	}

}
