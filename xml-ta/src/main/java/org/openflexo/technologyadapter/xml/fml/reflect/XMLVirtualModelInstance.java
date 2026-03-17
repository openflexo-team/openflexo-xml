/*
 * Copyright (c) 2013-2017, Openflexo
 *
 * This file is part of Flexo-foundation, a component of the software infrastructure
 * developed at Openflexo.
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
 *           Additional permission under GNU GPL version 3 section 7
 *           If you modify this Program, or any covered work, by linking or
 *           combining it with software containing parts covered by the terms
 *           of EPL 1.0, the licensors of this Program grant you additional permission
 *           to convey the resulting work.
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

package org.openflexo.technologyadapter.xml.fml.reflect;

import java.util.logging.Logger;

import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.rt.VirtualModelInstance;
import org.openflexo.foundation.fml.rt.reflect.ReflectedVirtualModelInstance;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.Import;
import org.openflexo.pamela.annotations.Imports;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.technologyadapter.xml.FMLXMLModelSlot;
import org.openflexo.technologyadapter.xml.XMLTechnologyAdapter;
import org.openflexo.technologyadapter.xml.model.AbstractXMLDocument;
import org.openflexo.technologyadapter.xml.rm.XMLResource;

/**
 * A XML-specific {@link VirtualModelInstance} reflecting XML resource accessible through a {@link FMLXMLModelSlot} configured with a
 * {@link VirtualModel} as FML instances<br>
 * 
 */
@ModelEntity
@ImplementationClass(XMLVirtualModelInstance.XMLVirtualModelInstanceImpl.class)
@Imports(@Import(XMLFlexoConceptInstance.class))
@XMLElement
public interface XMLVirtualModelInstance<RD extends AbstractXMLDocument<RD>>
		extends ReflectedVirtualModelInstance<XMLVirtualModelInstance<RD>, XMLResource<RD, ?>, RD, XMLTechnologyAdapter> {

	abstract class XMLVirtualModelInstanceImpl<RD extends AbstractXMLDocument<RD>>
			extends ReflectedVirtualModelInstanceImpl<XMLVirtualModelInstance<RD>, XMLResource<RD, ?>, RD, XMLTechnologyAdapter>
			implements XMLVirtualModelInstance<RD> {

		private static final Logger logger = FlexoLogger.getLogger(XMLVirtualModelInstance.class.getPackage().toString());

		@Override
		public Class<XMLVirtualModelInstance<RD>> getInferedImplementedInterface() {
			return (Class) XMLVirtualModelInstance.class;
		}

		/*@Override
		public AbstractVirtualModelInstanceModelFactory<?> getFactory() {
			AbstractVirtualModelInstanceModelFactory<?> returned = super.getFactory();
			System.err.println("Bon ici , je retourne " + returned);
			System.err.println("getReflectedModelFactory()=" + getReflectedModelFactory());
			return returned;
		}*/

	}
}
