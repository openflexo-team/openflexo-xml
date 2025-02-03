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

/*
 * (c) Copyright 2013- Openflexo
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

package org.openflexo.technologyadapter.xml;

import org.openflexo.foundation.fml.FlexoRole;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.foundation.fml.rt.ReflectedFMLRTModelSlot;
import org.openflexo.foundation.fml.rt.VirtualModelInstance;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.technologyadapter.xml.fml.reflect.XMLVirtualModelInstance;

/**
 * An implementation of a {@link ModelSlot} providing basic access to the content of an XML file and reflected as FML instances objects<br>
 * 
 * This {@link ModelSlot} is contract-based, as it is configured with a {@link VirtualModel} modelling data beeing accessed through this
 * {@link ModelSlot}. It means that data stored in database is locally reflected as {@link FlexoConceptInstance}s in a
 * {@link VirtualModelInstance} (instance of contract {@link VirtualModel})
 * 
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@ImplementationClass(FMLXMLModelSlot.FMLXMLModelSlotImpl.class)
/*@DeclareFlexoRoles({ SEColumnRole.class, SEDataAreaRole.class, SEReferenceRole.class })
@DeclareEditionActions({ CreateSEResource.class, InsertSEObject.class, RemoveSEObject.class })
@DeclareFlexoBehaviours({ SEInitializer.class })
@DeclareActorReferences({ XMLObjectActorReference.class })*/
@FML("FMLXMLModelSlot")
public interface FMLXMLModelSlot extends ReflectedFMLRTModelSlot<XMLVirtualModelInstance, XMLTechnologyAdapter> {

	abstract class FMLXMLModelSlotImpl extends ReflectedFMLRTModelSlotImpl<XMLVirtualModelInstance, XMLTechnologyAdapter>
			implements FMLXMLModelSlot {

		// private VirtualModelInstanceType type;

		@Override
		public Class<XMLTechnologyAdapter> getTechnologyAdapterClass() {
			return XMLTechnologyAdapter.class;
		}

		@Override
		public <PR extends FlexoRole<?>> String defaultFlexoRoleName(Class<PR> flexoRoleClass) {
			return super.defaultFlexoRoleName(flexoRoleClass);
		}

		@Override
		public XMLTechnologyAdapter getModelSlotTechnologyAdapter() {
			return (XMLTechnologyAdapter) super.getModelSlotTechnologyAdapter();
		}

		/*@Override
		public VirtualModelInstanceType getType() {
			//if (type == null || type.getVirtualModel() != getAccessedVirtualModel()) {
			//	type = SEVirtualModelInstanceType.getVirtualModelInstanceType(getAccessedVirtualModel());
			//}
			return type;
		}*/

		/*@Override
		public void setType(Type type) {
			// TODO Auto-generated method stub
			super.setType(type);
		}*/

		/*@Override
		public void setAccessedVirtualModel(VirtualModel aVirtualModel) {
			if (aVirtualModel != getAccessedVirtualModel()) {
				super.setAccessedVirtualModel(aVirtualModel);
				type = SEVirtualModelInstanceType.getVirtualModelInstanceType(getAccessedVirtualModel());
			}
		}*/

	}

}
