/*
 * (c) Copyright 2013 Openflexo
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.openflexo.technologyadapter.xml.rm;

import java.io.File;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.PamelaResourceModelFactory;
import org.openflexo.foundation.resource.FileSystemBasedResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.PamelaResource;
import org.openflexo.foundation.resource.ResourceData;
import org.openflexo.foundation.resource.TechnologySpecificPamelaResourceFactory;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterResource;
import org.openflexo.foundation.technologyadapter.TechnologyObject;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.pamela.factory.PamelaModelFactory;
import org.openflexo.technologyadapter.xml.XMLTechnologyAdapter;
import org.openflexo.toolbox.FileSystemMetaDataManager;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.xml.XMLRootElementInfo;

/**
 * Abstract partial implementation of ResourceFactory for {@link FreeXMLResource} and {@link TypedXMLResource}
 * 
 * @author sylvain
 *
 */
public abstract class AbstractXMLResourceFactory<R extends TechnologyAdapterResource<RD, XMLTechnologyAdapter> & PamelaResource<RD, F>, RD extends ResourceData<RD> & TechnologyObject<XMLTechnologyAdapter>, F extends PamelaModelFactory & PamelaResourceModelFactory>
		extends TechnologySpecificPamelaResourceFactory<R, RD, XMLTechnologyAdapter, F> {

	private static final Logger logger = Logger.getLogger(AbstractXMLResourceFactory.class.getPackage().getName());

	public static final String XML_EXTENSION = ".xml";
	public static final String XSD_SCHEMA_URI = "XSD_SCHEMA_URI";
	public static final String NONE_XSD_SCHEMA_URI = "none";
	public static final String IS_XML = "IS_XML";

	protected AbstractXMLResourceFactory(Class<R> resourceClass) throws ModelDefinitionException {
		super(resourceClass);
	}

	protected <I> String getSchemaURI(I serializationArtefact, FlexoResourceCenter<I> resourceCenter) {
		String returned = getCachedSchemaURI(serializationArtefact, resourceCenter);
		if (returned != null) {
			if (returned.equals(NONE_XSD_SCHEMA_URI)) {
				// already computed : NONE
				// System.out.println("already computed : NONE");
				return null;
			}
			else {
				// already computed
				// System.out.println("already computed : " + returned);
			}
			return returned;
		}
		// No value in cache, retrieve it now
		return retrieveSchemaURI(serializationArtefact, resourceCenter);
	}

	private <I> String retrieveSchemaURI(I serializationArtefact, FlexoResourceCenter<I> resourceCenter) {
		XMLRootElementInfo xmlRootElementInfo = resourceCenter.getXMLRootElementInfo(serializationArtefact);
		if (xmlRootElementInfo != null) {
			String returned = xmlRootElementInfo.getURI();
			if (StringUtils.isNotEmpty(returned)) {
				// System.out.println("Compute and save : " + returned);
				saveSchemaURI(returned, serializationArtefact, resourceCenter);
				return returned;
			}
			else {
				// System.out.println("Compute : NONE and save");
				saveSchemaURI(NONE_XSD_SCHEMA_URI, serializationArtefact, resourceCenter);
				return null;
			}
		}
		return null;
	}

	private <I> void saveSchemaURI(String schemaURI, I serializationArtefact, FlexoResourceCenter<I> resourceCenter) {
		if (resourceCenter instanceof FlexoProject) {
			resourceCenter = ((FlexoProject<I>) resourceCenter).getDelegateResourceCenter();
		}

		if (resourceCenter instanceof FileSystemBasedResourceCenter) {
			FileSystemMetaDataManager metaDataManager = ((FileSystemBasedResourceCenter) resourceCenter).getMetaDataManager();
			// We can safely cast serialization artefact to File
			File file = (File) serializationArtefact;
			metaDataManager.setProperty(XSD_SCHEMA_URI, schemaURI, file, true);
		}
	}

	private <I> String getCachedSchemaURI(I serializationArtefact, FlexoResourceCenter<I> resourceCenter) {
		if (resourceCenter instanceof FlexoProject) {
			resourceCenter = ((FlexoProject<I>) resourceCenter).getDelegateResourceCenter();
		}

		if (resourceCenter instanceof FileSystemBasedResourceCenter) {
			FileSystemMetaDataManager metaDataManager = ((FileSystemBasedResourceCenter) resourceCenter).getMetaDataManager();
			// We can safely cast serialization artefact to File
			File file = (File) serializationArtefact;

			if (file.lastModified() < metaDataManager.metaDataLastModified(file)) {
				// OK, in this case the metadata file is there and more recent than xml file
				// Attempt to retrieve metadata from cache
				return metaDataManager.getProperty(XSD_SCHEMA_URI, file);
			}
			else {
				// No way, metadata are either not present or older than file version, we should parse XML file, continuing...
				return null;
			}

		}

		// Cannot access any cache since this is not a file-system based resource center
		return null;
	}

	protected <I> boolean isXMLArtefact(I serializationArtefact, FlexoResourceCenter<I> resourceCenter) {
		Boolean returned = getCachedIsXMLArtefact(serializationArtefact, resourceCenter);
		if (returned != null) {
			return returned;
		}
		// No value in cache, retrieve it now
		return retrieveIsXMLArtefact(serializationArtefact, resourceCenter);
	}

	private <I> boolean retrieveIsXMLArtefact(I serializationArtefact, FlexoResourceCenter<I> resourceCenter) {
		if (resourceCenter.isDirectory(serializationArtefact)) {
			return false;
		}
		XMLRootElementInfo xmlRootElementInfo = resourceCenter.getXMLRootElementInfo(serializationArtefact);
		boolean isXML = xmlRootElementInfo != null;
		saveIsXMLArtefact(isXML, serializationArtefact, resourceCenter);
		return isXML;
	}

	private <I> void saveIsXMLArtefact(boolean isXML, I serializationArtefact, FlexoResourceCenter<I> resourceCenter) {
		if (resourceCenter instanceof FlexoProject) {
			resourceCenter = ((FlexoProject<I>) resourceCenter).getDelegateResourceCenter();
		}

		if (resourceCenter instanceof FileSystemBasedResourceCenter) {
			FileSystemMetaDataManager metaDataManager = ((FileSystemBasedResourceCenter) resourceCenter).getMetaDataManager();
			// We can safely cast serialization artefact to File
			File file = (File) serializationArtefact;
			metaDataManager.setProperty(IS_XML, isXML ? "true" : "false", file, true);
		}
	}

	private <I> Boolean getCachedIsXMLArtefact(I serializationArtefact, FlexoResourceCenter<I> resourceCenter) {
		if (resourceCenter instanceof FlexoProject) {
			resourceCenter = ((FlexoProject<I>) resourceCenter).getDelegateResourceCenter();
		}

		if (resourceCenter instanceof FileSystemBasedResourceCenter) {
			FileSystemMetaDataManager metaDataManager = ((FileSystemBasedResourceCenter) resourceCenter).getMetaDataManager();
			// We can safely cast serialization artefact to File
			File file = (File) serializationArtefact;

			if (file.lastModified() < metaDataManager.metaDataLastModified(file)) {
				// OK, in this case the metadata file is there and more recent than xml file
				// Attempt to retrieve metadata from cache
				String isXMLAsString = metaDataManager.getProperty(IS_XML, file);
				if (isXMLAsString != null) {
					return isXMLAsString.equals("true");
				}
			}
			else {
				// No way, metadata are either not present or older than file version, we should parse XML file, continuing...
				return null;
			}

		}

		// Cannot access any cache since this is not a file-system based resource center
		return null;
	}

}
