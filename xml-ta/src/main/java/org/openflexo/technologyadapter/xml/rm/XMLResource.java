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

package org.openflexo.technologyadapter.xml.rm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.resource.FileIODelegate;
import org.openflexo.foundation.resource.FileWritingLock;
import org.openflexo.foundation.resource.PamelaResource;
import org.openflexo.foundation.resource.PamelaResourceImpl;
import org.openflexo.foundation.resource.ResourceData;
import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterResource;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.factory.PamelaModelFactory;
import org.openflexo.technologyadapter.xml.XMLTechnologyAdapter;
import org.openflexo.technologyadapter.xml.XMLTechnologyContextManager;
import org.openflexo.technologyadapter.xml.model.AbstractXMLDocument;
import org.openflexo.technologyadapter.xml.model.AbstractXMLDocumentFactory;
import org.openflexo.toolbox.FileUtils;

/**
 * An XML resource, typically represented as an .xml file<br>
 * Can be a either a free XML file, or an XML conform to a grammar or metamodel (such as a .xsd file)
 * 
 * @param <RD>
 *            type of {@link ResourceData}
 * @param <F>
 *            type of {@link PamelaModelFactory}
 */
@ModelEntity
@ImplementationClass(XMLResource.XMLResourceImpl.class)
public interface XMLResource<RD extends AbstractXMLDocument<RD>, F extends AbstractXMLDocumentFactory<?, RD, F>>
		extends TechnologyAdapterResource<RD, XMLTechnologyAdapter>, PamelaResource<RD, F> {

	public static final String TECHNOLOGY_CONTEXT_MANAGER = "XMLTechnologyContextManager";
	public static final String XML_FILE_EXTENSION = ".xml";

	@Override
	@Getter(value = TECHNOLOGY_CONTEXT_MANAGER, ignoreType = true)
	public XMLTechnologyContextManager getTechnologyContextManager();

	@Setter(TECHNOLOGY_CONTEXT_MANAGER)
	public void setTechnologyContextManager(XMLTechnologyContextManager technologyContextManager);

	/**
	 * Default implementation for {@link XMLResource}
	 * 
	 */
	public static abstract class XMLResourceImpl<RD extends AbstractXMLDocument<RD>, F extends AbstractXMLDocumentFactory<?, RD, F>>
			extends PamelaResourceImpl<RD, F> implements XMLResource<RD, F> {

		protected static final Logger logger = Logger.getLogger(XMLResourceImpl.class.getPackage().getName());

		/**
		 * Writes the resource data, as an XML document, to the supplied stream. The stream is closed by the caller.
		 * 
		 * @param out
		 *            the stream receiving the document
		 * @throws Exception
		 *             when the document cannot be written
		 */
		protected abstract void write(OutputStream out) throws Exception;

		/**
		 * Saves the document: in a file, through a temporary file renamed at the end (the file is never left half-written); otherwise
		 * in the output stream of the I/O delegate.
		 */
		@Override
		protected void performSave(boolean clearIsModified) throws SaveResourceException {
			if (getFlexoIOStreamDelegate() == null) {
				throw new SaveResourceException(getIODelegate());
			}

			FileWritingLock lock = getFlexoIOStreamDelegate().willWriteOnDisk();

			if (logger.isLoggable(Level.INFO)) {
				logger.info("Saving resource " + this + " : " + getIODelegate().getSerializationArtefact());
			}

			try {
				if (getFlexoIOStreamDelegate() instanceof FileIODelegate) {
					File temporaryFile = null;
					try {
						File fileToSave = ((FileIODelegate) getFlexoIOStreamDelegate()).getFile();
						makeLocalCopy(fileToSave);
						temporaryFile = ((FileIODelegate) getIODelegate()).createTemporaryArtefact(XML_FILE_EXTENSION);
						try (FileOutputStream fos = new FileOutputStream(temporaryFile)) {
							write(fos);
						}
						FileUtils.rename(temporaryFile, fileToSave);
						if (logger.isLoggable(Level.FINE)) {
							logger.fine("Renamed " + temporaryFile + " to " + fileToSave);
						}
					} catch (Exception e) {
						if (temporaryFile != null) {
							temporaryFile.delete();
						}
						logger.log(Level.WARNING, "Failed to save resource " + this, e);
						throw new SaveResourceException(getIODelegate(), e);
					}
				}
				else {
					try (OutputStream out = getOutputStream()) {
						write(out);
					} catch (Exception e) {
						logger.log(Level.WARNING, "Failed to save resource " + this, e);
						throw new SaveResourceException(getIODelegate(), e);
					}
				}
			} finally {
				getFlexoIOStreamDelegate().hasWrittenOnDisk(lock);
			}
		}

	}

}
