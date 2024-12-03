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

package org.openflexo.technologyadapter.xml.model.typed;

import java.util.logging.Logger;

import org.openflexo.pamela.PamelaMetaModelLibrary;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.pamela.factory.EditingContext;
import org.openflexo.pamela.factory.PamelaModelFactory;
import org.openflexo.technologyadapter.xml.metamodel.XMLComplexType;
import org.openflexo.technologyadapter.xml.metamodel.XMLDataProperty;
import org.openflexo.technologyadapter.xml.metamodel.XMLObjectProperty;
import org.openflexo.technologyadapter.xml.model.AbstractXMLDocumentFactory;
import org.openflexo.technologyadapter.xml.rm.TypedXMLResource;

/**
 * A {@link PamelaModelFactory} used to manage {@link XMLModel}
 * 
 * One instance of this class should be used for each {@link TypedXMLResource}
 * 
 * @author sylvain
 * 
 */
public class XMLModelFactory extends AbstractXMLDocumentFactory<TypedXMLResource, XMLModel, XMLModelFactory> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(XMLModelFactory.class.getPackage().getName());

	public XMLModelFactory(TypedXMLResource resource, EditingContext editingContext) throws ModelDefinitionException {
		super(PamelaMetaModelLibrary.retrieveMetaModel(XMLModel.class, XMLIndividual.class, XMLDataPropertyValue.class,
				XMLObjectPropertyValue.class), resource, editingContext);
	}

	public XMLModel makeXMLModel() {
		return newInstance(XMLModel.class);
	}

	public XMLIndividual makeXMLIndividual(XMLModel model, XMLComplexType type) {
		XMLIndividual returned = newInstance(XMLIndividual.class);
		// TODO: handle model
		returned.setType(type);
		return returned;
	}

	public XMLObjectPropertyValue makeXMLObjectPropertyValue(XMLObjectProperty property, XMLIndividual... values) {
		XMLObjectPropertyValue returned = newInstance(XMLObjectPropertyValue.class);
		returned.setProperty(property);
		for (XMLIndividual v : values) {
			returned.addToValues(v);
		}
		return returned;
	}

	public XMLDataPropertyValue makeXMLDataPropertyValue(XMLDataProperty property, Object value) {
		XMLDataPropertyValue returned = newInstance(XMLDataPropertyValue.class);
		returned.setProperty(property);
		returned.setValue(value);
		return returned;
	}

}
