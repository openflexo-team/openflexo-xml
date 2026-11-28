/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2012-2012, AgileBirds
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

package org.openflexo.technologyadapter.xml.rm;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.OpenflexoRuntimeException;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.foundation.technologyadapter.FlexoModelResource;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.technologyadapter.xml.XMLTechnologyAdapter;
import org.openflexo.technologyadapter.xml.metamodel.XSDMetaModel;
import org.openflexo.technologyadapter.xml.model.typed.XMLModel;
import org.openflexo.technologyadapter.xml.model.typed.XMLModelBuilder;
import org.openflexo.technologyadapter.xml.model.typed.XMLModelFactory;

/**
 * A resource allowing access to an XML document conform to an XSD grammar (its metamodel)
 * 
 * @author sylvain
 */
@ModelEntity
@ImplementationClass(TypedXMLResource.TypedXMLResourceImpl.class)
public interface TypedXMLResource extends XMLResource<XMLModel, XMLModelFactory>,
		FlexoModelResource<XMLModel, XSDMetaModel, XMLTechnologyAdapter, XMLTechnologyAdapter> {

	/**
	 * Default implementation for TypedXMLResource
	 */
	public static abstract class TypedXMLResourceImpl extends XMLResourceImpl<XMLModel, XMLModelFactory> implements TypedXMLResource {

		protected static final Logger logger = Logger.getLogger(TypedXMLResourceImpl.class.getPackage().getName());

		@Override
		public Class<XMLModel> getResourceDataClass() {
			return XMLModel.class;
		}

		@Override
		protected XMLModel performLoad() throws IOException, Exception {

			resourceData = getFactory().makeXMLModel();
			resourceData.setResource(this);
			// resourceData.setURI(this.getURI());

			getMetaModelResource().loadResourceData();
			resourceData.setMetaModel(getMetaModelResource().getMetaModelData());

			notifyResourceWillLoad();

			XMLModelBuilder builder = new XMLModelBuilder();
			builder.setModelContext(resourceData);
			builder.deserialize(getInputStream());
			builder.resetModelContext();

			notifyResourceLoaded();

			return resourceData;

		}

		@Override
		public XMLModel getModel() {
			return getModelData();
		}

		@Override
		public XMLModel getModelData() {

			try {
				return getResourceData();
			} catch (FileNotFoundException | ResourceLoadingCancelledException | OpenflexoRuntimeException e) {
				logger.log(Level.WARNING, "Cannot load " + this, e);
			}
			return null;

		}

		/**
		 * Saving a typed XML document is not supported yet (XML-D-4): {@link XMLWriter} predates the current model (single and multiple
		 * property values, sub-elements held both as children and as object property values) and the order of the elements required
		 * by the XSD is not kept. The file is left as it is and a warning says so; no exception is raised, so that saving a project
		 * goes on with its other resources. The change made in memory is lost when the resource is unloaded.
		 */
		@Override
		protected void performSave(boolean clearIsModified) throws SaveResourceException {
			if (resourceData == null || resourceData.getRoot() == null) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Nothing to write for " + this + ": the document has no root");
				}
				return;
			}
			logger.warning("Typed XML documents (conform to an XSD) cannot be saved yet: " + getIODelegate().getSerializationArtefact()
					+ " is left unchanged, the changes made in Openflexo are not written");
		}

		/**
		 * Not used while {@link #performSave(boolean)} does not write (XML-D-4).
		 */
		@Override
		protected void write(OutputStream out) throws Exception {
			throw new UnsupportedOperationException("Saving a typed XML document is not supported yet");
		}

	}

}
