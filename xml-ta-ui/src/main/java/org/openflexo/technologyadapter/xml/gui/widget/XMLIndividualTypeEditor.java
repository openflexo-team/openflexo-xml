/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Flexo-ui, a component of the software infrastructure 
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

package org.openflexo.technologyadapter.xml.gui.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import org.openflexo.components.widget.DefaultCustomTypeEditorImpl;
import org.openflexo.foundation.FlexoServiceManager;
import org.openflexo.gina.annotation.FIBPanel;
import org.openflexo.icon.UtilsIconLibrary;
import org.openflexo.technologyadapter.xml.XMLIndividualType;
import org.openflexo.technologyadapter.xml.XMLTechnologyAdapter;
import org.openflexo.technologyadapter.xml.metamodel.XMLComplexType;
import org.openflexo.technologyadapter.xml.metamodel.XSDMetaModel;
import org.openflexo.technologyadapter.xml.rm.XSDMetaModelResource;
import org.openflexo.toolbox.StringUtils;

/**
 * An editor to edit a {@link XMLIndividualType}
 * 
 * @author sylvain
 * 
 */
@FIBPanel("Fib/widgets/XMLIndividualTypeEditor.fib")
public class XMLIndividualTypeEditor extends DefaultCustomTypeEditorImpl<XMLIndividualType> {

	static final Logger logger = Logger.getLogger(XMLIndividualTypeEditor.class.getPackage().getName());

	private XMLComplexType selectedType = null;
	private XSDMetaModelResource metaModelResource;
	private String filteredClassName = "";
	private List<XMLComplexType> matchingValues = new ArrayList<>();
	private String presentationName;

	public XMLIndividualTypeEditor(FlexoServiceManager serviceManager, String presentationName) {
		super(serviceManager);
		this.presentationName = presentationName;
	}

	@Override
	public String getPresentationName() {
		return presentationName;
	}

	public XMLTechnologyAdapter getTechnologyAdapter() {
		return getServiceManager().getTechnologyAdapterService().getTechnologyAdapter(XMLTechnologyAdapter.class);
	}

	@Override
	public Class<XMLIndividualType> getCustomType() {
		return XMLIndividualType.class;
	}

	public XSDMetaModel getMetaModel() {
		if (getMetaModelResource() != null) {
			return getMetaModelResource().getMetaModelData();
		}
		return null;
	}

	public XSDMetaModelResource getMetaModelResource() {
		return metaModelResource;
	}

	public void setMetaModelResource(XSDMetaModelResource metaModelResource) {
		if ((metaModelResource == null && this.metaModelResource != null)
				|| (metaModelResource != null && !metaModelResource.equals(this.metaModelResource))) {
			XSDMetaModelResource oldValue = this.metaModelResource;
			this.metaModelResource = metaModelResource;
			getPropertyChangeSupport().firePropertyChange("metaModelResource", oldValue, metaModelResource);
			getPropertyChangeSupport().firePropertyChange("metaModel", oldValue != null ? oldValue.getLoadedResourceData() : null,
					getMetaModel());
		}
	}

	public XMLComplexType getSelectedType() {
		return selectedType;
	}

	public void setSelectedType(XMLComplexType selectedType) {

		System.out.println("   --> setSelectedType with " + selectedType);

		if ((selectedType == null && this.selectedType != null) || (selectedType != null && !selectedType.equals(this.selectedType))) {
			XMLComplexType oldValue = this.selectedType;
			this.selectedType = selectedType;
			getPropertyChangeSupport().firePropertyChange("selectedType", oldValue, selectedType);
			matchingValues.clear();
			getPropertyChangeSupport().firePropertyChange("matchingValues", null, matchingValues);
			getPropertyChangeSupport().firePropertyChange("searchLabel", null, getSearchLabel());
		}
	}

	@Override
	public XMLIndividualType getEditedType() {
		return XMLIndividualType.getXMLIndividualOfType(getSelectedType());
	}

	public List<XMLComplexType> getMatchingValues() {
		return matchingValues;
	}

	/*@Override
	public Resource getFIBComponentResource() {
		// TODO Auto-generated method stub
		Resource returned = super.getFIBComponentResource();
		System.out.println("Cool on retourne " + returned);
		return returned;
	}*/

	public ImageIcon getSearchIcon() {
		return UtilsIconLibrary.SEARCH_ICON;
	}

	public String getSearchLabel() {
		if (matchingValues.size() >= 1) {
			return "Found " + matchingValues.size() + " classes";
		}
		if (StringUtils.isNotEmpty(getFilteredClassName())) {
			return "No matches";
		}
		return "You might use wildcards (* = any string) and press 'Search'";

	}

	public String getFilteredClassName() {
		return filteredClassName;
	}

	public void setFilteredClassName(String filteredClassName) {
		if (filteredClassName == null || !filteredClassName.equals(this.filteredClassName)) {
			String oldValue = this.filteredClassName;
			this.filteredClassName = filteredClassName;
			updateMatchingClasses();
			getPropertyChangeSupport().firePropertyChange("filteredClassName", oldValue, filteredClassName);
			/*if (searchMode) {
				updateMatchingClasses();
				getPropertyChangeSupport().firePropertyChange("searchMode", !searchMode(), searchMode());
			}*/
		}
	}

	private void updateMatchingClasses() {

		// logger.info("*************** updateMatchingClasses() for " + filteredClassName);

		final List<XMLComplexType> oldMatchingValues = new ArrayList<>(getMatchingValues());
		// System.out.println("updateMatchingValues() with " + getFilteredName());
		matchingValues.clear();

		if (StringUtils.isNotEmpty(getFilteredClassName())) {
			if (getAllSelectableValues() != null && getFilteredClassName() != null) {
				for (XMLComplexType next : getAllSelectableValues()) {
					if (matches(next, getFilteredClassName())) {
						matchingValues.add(next);
					}
				}
			}
		}
		// logger.info("Objects matching with " + getFilteredClassName() + " found " + matchingValues.size() + " values");
		getPropertyChangeSupport().firePropertyChange("searchLabel", null, getSearchLabel());

		SwingUtilities.invokeLater(() -> {
			getPropertyChangeSupport().firePropertyChange("matchingValues", oldMatchingValues, getMatchingValues());
			if (matchingValues.size() == 1) {
				setSelectedType(matchingValues.get(0));
			}
		});

	}

	public List<XMLComplexType> getAllSelectableValues() {
		if (getMetaModelResource() != null) {
			return getMetaModelResource().getMetaModelData().getComplexTypes();
		}
		return null;
	}

	protected boolean matches(XMLComplexType o, String filteredName) {
		return o != null && StringUtils.isNotEmpty(o.getName()) && (o.getName()).toUpperCase().indexOf(filteredName.toUpperCase()) > -1;
	}

	@Override
	public void updateEditedType(XMLIndividualType type) {
		if (type != null && type.getXMLType() != null) {
			XMLComplexType t = type.getXMLType();
			setMetaModelResource((XSDMetaModelResource) t.getMetamodel().getResource());
			setSelectedType(t);
		}
	}

}
