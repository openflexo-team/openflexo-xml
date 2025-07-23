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

import org.openflexo.foundation.fml.FlexoConcept;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.foundation.fml.rt.reflect.ReflectedFlexoConceptInstance;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.Initializer;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.technologyadapter.xml.FMLXMLModelSlot;
import org.openflexo.xml.XMLReaderSAXHandler.ParsedElement;

/**
 * A XML-specific {@link FlexoConceptInstance} reflecting a distant object (represented by an XML element in a XML file) accessible in an
 * {@link XMLVirtualModelInstance} through a {@link FMLXMLModelSlot}<br>
 * 
 */
@ModelEntity
@ImplementationClass(XMLFlexoConceptInstance.XMLFlexoConceptInstanceImpl.class)
@XMLElement
public interface XMLFlexoConceptInstance extends ReflectedFlexoConceptInstance<ParsedElement<XMLFlexoConceptInstance>> {

	@Initializer
	void initialize(FlexoConcept concept, ParsedElement<XMLFlexoConceptInstance> supportObject);

	/**
	 * Default implementation for {@link XMLFlexoConceptInstance}
	 * 
	 * @author sylvain
	 *
	 */
	abstract class XMLFlexoConceptInstanceImpl extends FlexoConceptInstanceImpl implements XMLFlexoConceptInstance {

		private static final Logger logger = FlexoLogger.getLogger(XMLFlexoConceptInstance.class.getPackage().toString());

		/**
		 * Initialize this {@link XMLFlexoConceptInstance} with supplied Hibernate support object, and explicit concept (type)
		 * 
		 * @param concept
		 */
		@Override
		public void initialize(FlexoConcept concept, ParsedElement<XMLFlexoConceptInstance> supportObject) {
			setFlexoConcept(concept);
			setSupportObject(supportObject);
		}

		@Override
		public XMLVirtualModelInstance<?> getVirtualModelInstance() {
			return (XMLVirtualModelInstance<?>) super.getVirtualModelInstance();
		}

		/*@Override
		public Row getRowSupportObject() {
			return row;
		}
		
		@Override
		public void setRowSupportObject(Row row) {
		
			if ((row == null && this.row != null) || (row != null && !row.equals(this.row))) {
				Row oldValue = this.row;
				this.row = row;
				getPropertyChangeSupport().firePropertyChange("rowSupportObject", oldValue, row);
			}
		}
		
		@Override
		public <T> T getFlexoActor(FlexoRole<T> flexoRole) {
			if (flexoRole instanceof SEColumnRole) {
				SEColumnRole<T> columnRole = (SEColumnRole<T>) flexoRole;
				Cell cell = row.getCell(columnRole.getColumnIndex());
				// System.out.println("cell: " + cell);
				switch (columnRole.getPrimitiveType()) {
					case String:
						if (cell != null) {
							return (T) cell.getStringCellValue();
						}
						return null;
					case Long:
					case Integer:
					case Double:
					case Float:
						if (cell != null) {
							return (T) TypeUtils.castTo(cell.getNumericCellValue(), columnRole.getPrimitiveType().getType());
						}
						else {
							return (T) TypeUtils.castTo(0, columnRole.getPrimitiveType().getType());
						}
					case Date:
						if (cell != null) {
							return (T) cell.getDateCellValue();
						}
					case Boolean:
						if (cell != null) {
							return (T) (Boolean) cell.getBooleanCellValue();
						}
						return (T) Boolean.FALSE;
					default:
						logger.warning("Unexpected primitive type: " + columnRole.getPrimitiveType());
						return null;
				}
			}
			return super.getFlexoActor(flexoRole);
		}
		
		@Override
		public <T> void setFlexoActor(T object, FlexoRole<T> flexoRole) {
			if (flexoRole instanceof SEColumnRole) {
				SEColumnRole<T> columnRole = (SEColumnRole<T>) flexoRole;
				Cell cell = row.getCell(columnRole.getColumnIndex());
				// System.out.println("cell: " + cell);
				switch (columnRole.getPrimitiveType()) {
					case String:
						cell.setCellValue((String) object);
						break;
					case Long:
						cell.setCellValue((Long) object);
						break;
					case Integer:
						cell.setCellValue((Integer) object);
						break;
					case Double:
						cell.setCellValue((Double) object);
						break;
					case Float:
						cell.setCellValue((Float) object);
						break;
					case Date:
						cell.setCellValue((Date) object);
						break;
					case Boolean:
						cell.setCellValue((Boolean) object);
						break;
					default:
						logger.warning("Unexpected primitive type: " + columnRole.getPrimitiveType());
						break;
				}
			}
			else {
				super.setFlexoActor(object, flexoRole);
			}
		}
		
		@Override
		public XMLObjectActorReference makeActorReference(FlexoConceptInstanceRole role, FlexoConceptInstance fci) {
			AbstractVirtualModelInstanceModelFactory<?> factory = getFactory();
			XMLObjectActorReference returned = factory.newInstance(XMLObjectActorReference.class);
			returned.setFlexoRole(role);
			returned.setFlexoConceptInstance(fci);
			returned.setModellingElement(this);
			return returned;
		}
		*/
	}
}
