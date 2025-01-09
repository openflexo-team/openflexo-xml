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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.FileIODelegate;
import org.openflexo.foundation.resource.FileWritingLock;
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
import org.openflexo.toolbox.FileUtils;

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
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (ResourceLoadingCancelledException e) {
				e.printStackTrace();
			} catch (FlexoException e) {
				e.printStackTrace();
			}
			return null;

		}

		protected void _saveResourceData(boolean clearIsModified) throws SaveResourceException {

			if (getFlexoIOStreamDelegate() == null) {
				throw new SaveResourceException(getIODelegate());
			}

			FileWritingLock lock = getFlexoIOStreamDelegate().willWriteOnDisk();

			if (logger.isLoggable(Level.INFO)) {
				logger.info("Saving resource " + this + " : " + getIODelegate().getSerializationArtefact());
			}

			if (getFlexoIOStreamDelegate() instanceof FileIODelegate) {
				File temporaryFile = null;
				try {
					File fileToSave = ((FileIODelegate) getFlexoIOStreamDelegate()).getFile();
					// Make local copy
					makeLocalCopy(fileToSave);
					// Using temporary file
					temporaryFile = ((FileIODelegate) getIODelegate()).createTemporaryArtefact(".pdf");
					if (logger.isLoggable(Level.FINE)) {
						logger.finer("Creating temp file " + temporaryFile.getAbsolutePath());
					}
					try (FileOutputStream fos = new FileOutputStream(temporaryFile)) {
						write(fos);
					}
					System.out.println("Renamed " + temporaryFile + " to " + fileToSave);
					FileUtils.rename(temporaryFile, fileToSave);
				} catch (Exception e) {
					e.printStackTrace();
					if (temporaryFile != null) {
						temporaryFile.delete();
					}
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Failed to save resource " + this);
					}
					getFlexoIOStreamDelegate().hasWrittenOnDisk(lock);
					throw new SaveResourceException(getIODelegate(), e);
				}
			}
			else {
				try {
					write(getOutputStream());
				} catch (Exception e) {
					e.printStackTrace();
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Failed to save resource " + this);
					}
					getFlexoIOStreamDelegate().hasWrittenOnDisk(lock);
					throw new SaveResourceException(getIODelegate(), e);
				}
			}

			getFlexoIOStreamDelegate().hasWrittenOnDisk(lock);
			if (clearIsModified) {
				notifyResourceStatusChanged();
			}
		}

		private void write(OutputStream out) throws IOException, XMLStreamException, ResourceLoadingCancelledException, FlexoException {
			System.out.println("Writing xml file in : " + getIODelegate().getSerializationArtefact());
			try (OutputStreamWriter outSW = new OutputStreamWriter(out, "UTF-8")) {
				XMLWriter<TypedXMLResource, XMLModel> writer = new XMLWriter<>(this, outSW);
				writer.writeDocument();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				throw new SaveResourceException(getIODelegate());
			} finally {
				out.close();
			}
			System.out.println("Wrote : " + getIODelegate().getSerializationArtefact());
		}

	}

}
