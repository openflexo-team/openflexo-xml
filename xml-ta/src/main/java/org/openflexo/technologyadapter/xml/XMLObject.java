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

import java.util.logging.Logger;

import org.openflexo.foundation.InnerResourceData;
import org.openflexo.foundation.technologyadapter.TechnologyObject;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.technologyadapter.xml.XMLObject.XMLObjectImpl;
import org.openflexo.technologyadapter.xml.model.AbstractXMLDocument;
import org.openflexo.technologyadapter.xml.model.AbstractXMLDocumentFactory;
import org.openflexo.technologyadapter.xml.rm.XMLResource;

/**
 * 
 * Abstract base class for all objects beeing part of a XML resource content
 * 
 * An {@link XMLObject} is contained in an {@link AbstractXMLDocument}, and is a {@link TechnologyObject} of XML technology adapter<br>
 * As such, a {@link XMLObject} "lives" in a {@link XMLResource}
 * 
 * @author sylvain
 * 
 */
@ModelEntity(isAbstract = true)
@ImplementationClass(XMLObjectImpl.class)
public interface XMLObject<RD extends AbstractXMLDocument<RD>> extends TechnologyObject<XMLTechnologyAdapter>, InnerResourceData<RD> {

	public static final String NAME = "name";

	public final String URI = "uri";

	@Getter(NAME)
	public String getName();

	@Getter(URI)
	public String getURI();

	@Setter(URI)
	public void setURI(String uri);

	public XMLResource<RD, ?> getResource();

	public String getDisplayableDescription();

	@Deprecated
	public String getSerializationIdentifier();

	public AbstractXMLDocumentFactory<?, ?, ?> getModelFactory();

	/**
	 * Default base implementation for {@link ExcelObject}
	 * 
	 * @author sylvain
	 *
	 */
	public static abstract class XMLObjectImpl<RD extends AbstractXMLDocument<RD>> extends FlexoObjectImpl implements XMLObject<RD> {

		@SuppressWarnings("unused")
		private static final Logger logger = Logger.getLogger(XMLObjectImpl.class.getPackage().getName());

		@Override
		public final XMLTechnologyAdapter getTechnologyAdapter() {
			if (getResourceData() != null && getResourceData().getResource() != null) {
				return getResourceData().getResource().getTechnologyAdapter();
			}
			return null;
		}

		@Override
		public XMLResource<RD, ?> getResource() {
			return getResourceData().getResource();
		}

		@Override
		public AbstractXMLDocumentFactory<?, ?, ?> getModelFactory() {
			return getResource().getFactory();
		}

		@Override
		@Deprecated
		public final String getSerializationIdentifier() {
			/*if (getResourceData() != null) {
				return getResourceData().getResource().getConverter().toSerializationIdentifier(this);
			}*/
			return "???";
		}

		@Override
		public String toString() {
			return getImplementedInterface().getSimpleName() + "-" + getSerializationIdentifier();
		}

	}

}
