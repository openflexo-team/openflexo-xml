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

import java.io.IOException;
import java.util.logging.Logger;

import org.openflexo.technologyadapter.xml.model.free.FreeXMLDocument;
import org.openflexo.technologyadapter.xml.model.free.FreeXMLDocumentBuilder;
import org.openflexo.technologyadapter.xml.model.free.FreeXMLDocumentFactory;

/**
 * @author xtof
 * 
 */
public abstract class FreeXMLResourceImpl extends XMLResourceImpl<FreeXMLDocument, FreeXMLDocumentFactory> implements FreeXMLResource {

	protected static final Logger logger = Logger.getLogger(FreeXMLResourceImpl.class.getPackage().getName());

	@Override
	protected FreeXMLDocument performLoad() throws IOException, Exception {

		System.out.println("Hop on lit le XML");

		resourceData = getFactory().makeFreeXMLDocument();
		resourceData.setResource(this);

		notifyResourceWillLoad();

		FreeXMLDocumentBuilder builder = new FreeXMLDocumentBuilder();
		builder.setContext(resourceData);
		builder.deserialize(getInputStream());
		builder.resetContext();

		notifyResourceLoaded();

		return resourceData;

		/*converter = new BasicExcelModelConverter(this);
		
		if (getFlexoIOStreamDelegate() == null) {
			throw new IOFlexoException("Cannot load Excel document with this IO/delegate: " + getIODelegate());
		}
		
		notifyResourceWillLoad();
		
		ExcelWorkbook returned = null;
		try {
			returned = createOrLoadExcelWorkbook(getFlexoIOStreamDelegate());
			getInputStream().close();
		} catch (OfficeXmlFileException e) {
			throw new IOFlexoException(e.getMessage());
		} catch (IOException e) {
			throw new IOFlexoException(e);
		}
		
		if (returned == null) {
			logger.warning("canno't retrieve resource data from serialization artifact " + getIODelegate().toString());
			return null;
		}
		
		notifyResourceLoaded();
		
		return returned;*/
	}

}
