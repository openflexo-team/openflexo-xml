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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openflexo.foundation.FlexoObject.FlexoObjectImpl;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.pamela.PamelaMetaModelLibrary;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.pamela.factory.PamelaModelFactory;
import org.openflexo.technologyadapter.xml.XMLTechnologyAdapter;
import org.openflexo.technologyadapter.xml.model.XMLModel;

/**
 * A simple MetaModeling Structure that is not backed up in an XSD file and where you can create new types freely
 * 
 * @author xtof
 *
 */

public abstract class XMLMetaModelImpl<MM extends XMLMetaModel<MM>> extends FlexoObjectImpl implements XMLMetaModel<MM> {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger
			.getLogger(XMLMetaModelImpl.class.getPackage().getName());

	protected Map<String, XMLType> types = null;

	private static PamelaModelFactory MF;

	public XMLMetaModelImpl() {
		super();
		types = new HashMap<>();
	}

	static {
		try {
			MF = new PamelaModelFactory(PamelaMetaModelLibrary.retrieveMetaModel(XMLModel.class, XMLType.class, XMLComplexType.class,
					XMLSimpleType.class, XMLProperty.class, XMLDataProperty.class, XMLObjectProperty.class));
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
	}

	public static PamelaModelFactory getModelFactory() {
		return MF;
	}

	@Override
	public XMLType getTypeFromURI(String uri) {

		XMLType t = types.get(uri);

		if (t == null) {
			return createNewType(uri, uri, true);
		}

		return t;

	}

	@Override
	public void addType(XMLType aType) {
		types.put(aType.getURI(), aType);
	}

	@Override
	public void removeType(XMLType aType) {
		types.remove(aType);
	}

	@Override
	public List<? extends XMLType> getTypes() {
		// TODO: perf issue
		return new ArrayList<>(types.values());
	}

	@Override
	public XMLType createNewType(String uri, String localName, boolean simpleType) {

		XMLType aType = null;
		if (simpleType) {
			aType = XMLMetaModelImpl.getModelFactory().newInstance(XMLSimpleType.class, this);
		}
		else {
			aType = XMLMetaModelImpl.getModelFactory().newInstance(XMLComplexType.class, this);
		}
		aType.setIsAbstract(false);
		aType.setURI(uri);
		aType.setName(localName);

		addType(aType);

		return aType;
	}

	/**
	 * 
	 * creates a new empty MetaModel
	 * 
	 * @return
	 */
	public static XSDMetaModel createEmptyMetaModel(String uri) {

		XSDMetaModel metamodel = MF.newInstance(XSDMetaModel.class);
		metamodel.setReadOnly(false);

		metamodel.setURI(uri);

		return metamodel;

	}

	@Override
	public String getDisplayableDescription() {
		return null;
	}

	@Override
	public XMLTechnologyAdapter getTechnologyAdapter() {
		return null;
	}

	@Override
	public Object getObject(String objectURI) {
		return null;
	}

	@Override
	public void setIsReadOnly(boolean b) {

	}

	@Override
	public FlexoResource<MM> getResource() {
		return null;
	}

	@Override
	public void setResource(FlexoResource<MM> resource) {

	}

}
