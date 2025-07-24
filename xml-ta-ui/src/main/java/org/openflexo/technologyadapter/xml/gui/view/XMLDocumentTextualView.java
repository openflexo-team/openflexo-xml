/**
 * 
 * Copyright (c) 2018, Openflexo
 * 
 * This file is part of OpenflexoTechnologyAdapter, a component of the software infrastructure 
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

package org.openflexo.technologyadapter.xml.gui.view;

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.resource.StreamIODelegate;
import org.openflexo.selection.SelectionListener;
import org.openflexo.selection.SelectionManager;
import org.openflexo.technologyadapter.xml.model.AbstractXMLDocument;
import org.openflexo.technologyadapter.xml.rm.XMLResource;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.view.SelectionSynchronizedModuleView;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.model.FlexoPerspective;

/**
 * This class represent the module view for a {@link JSONDocument}<br>
 * 
 * @author sylvain
 * 
 */
@SuppressWarnings("serial")
public class XMLDocumentTextualView extends JPanel implements SelectionSynchronizedModuleView<AbstractXMLDocument<?>> {

	private final AbstractXMLDocument<?> document;
	private final FlexoPerspective declaredPerspective;

	private final FlexoController controller;
	private RSyntaxTextArea textArea;

	public XMLDocumentTextualView(AbstractXMLDocument<?> document, FlexoController controller, FlexoPerspective perspective) {
		super(new BorderLayout());
		this.controller = controller;
		declaredPerspective = perspective;
		this.document = document;
		textArea = new RSyntaxTextArea();
		textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
		textArea.setText(retrieveXMLContentsFromArtefact(document));
		add(new JScrollPane(textArea), BorderLayout.CENTER);
	}

	public String retrieveXMLContentsFromArtefact(AbstractXMLDocument<?> xmlDocument) {
		XMLResource<?, ?> resource = xmlDocument.getResource();
		if (resource != null) {
			if (xmlDocument.getResource().getIODelegate() instanceof StreamIODelegate) {
				try {
					return FileUtils.fileContents(((StreamIODelegate) resource.getIODelegate()).getInputStream(), "UTF-8");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return "Contents not accessible";
	}

	public FlexoController getFlexoController() {
		return controller;
	}

	@Override
	public FlexoPerspective getPerspective() {
		return declaredPerspective;
	}

	@Override
	public void deleteModuleView() {
		if (getFlexoController() != null) {
			controller.removeModuleView(this);
		}
	}

	@Override
	public List<SelectionListener> getSelectionListeners() {
		Vector<SelectionListener> reply = new Vector<>();
		reply.add(this);
		return reply;
	}

	@Override
	public void willHide() {
	}

	@Override
	public void willShow() {
	}

	@Override
	public void show(FlexoController controller, FlexoPerspective perspective) {
	}

	@Override
	public AbstractXMLDocument<?> getRepresentedObject() {
		return document;
	}

	@Override
	public boolean isAutoscrolled() {
		return false;
	}

	@Override
	public void fireObjectSelected(FlexoObject object) {

	}

	@Override
	public void fireObjectDeselected(FlexoObject object) {

	}

	@Override
	public void fireResetSelection() {

	}

	@Override
	public void fireBeginMultipleSelection() {

	}

	@Override
	public void fireEndMultipleSelection() {

	}

	@Override
	public SelectionManager getSelectionManager() {
		if (getFlexoController() != null) {
			return getFlexoController().getSelectionManager();
		}
		return null;
	}

	@Override
	public Vector<FlexoObject> getSelection() {
		return getSelectionManager().getSelection();
	}

	@Override
	public void resetSelection() {
		getSelectionManager().resetSelection();
	}

	@Override
	public void addToSelected(FlexoObject object) {
		getSelectionManager().addToSelected(object);
	}

	@Override
	public void removeFromSelected(FlexoObject object) {
		getSelectionManager().removeFromSelected(object);
	}

	@Override
	public void addToSelected(Vector<? extends FlexoObject> objects) {
		getSelectionManager().addToSelected(objects);
	}

	@Override
	public void removeFromSelected(Vector<? extends FlexoObject> objects) {
		getSelectionManager().removeFromSelected(objects);
	}

	@Override
	public void setSelectedObjects(Vector<? extends FlexoObject> objects) {
		getSelectionManager().setSelectedObjects(objects);
	}

	@Override
	public FlexoObject getFocusedObject() {
		return getSelectionManager().getFocusedObject();
	}

	@Override
	public boolean mayRepresents(FlexoObject anObject) {
		return false;
	}

}
