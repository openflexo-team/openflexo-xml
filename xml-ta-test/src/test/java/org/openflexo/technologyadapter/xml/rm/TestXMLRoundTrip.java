/**
 * 
 * Copyright (c) 2026, Openflexo
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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.junit.Test;
import org.openflexo.foundation.resource.DirectoryResourceCenter;
import org.openflexo.foundation.test.OpenflexoTestCase;
import org.openflexo.technologyadapter.xml.XMLTechnologyAdapter;
import org.openflexo.technologyadapter.xml.model.free.FreeXMLDocument;
import org.openflexo.technologyadapter.xml.model.free.XMLElement;
import org.openflexo.technologyadapter.xml.model.typed.XMLIndividual;
import org.openflexo.technologyadapter.xml.model.typed.XMLModel;

/**
 * Save of the two kinds of XML document. A free document makes the round trip: load, change, save, unload, reload from the file,
 * compare. A typed document cannot be saved yet (XML-D-4): its save writes nothing. Each test copies a document of the test
 * resource center into a new resource center of its own, so that no file of the test resource center is ever written. The metamodel of
 * the typed document (library.xsd) is the one of the test resource center.
 */
public class TestXMLRoundTrip extends OpenflexoTestCase {

	private static final String TEST_RC_XML = "/TestResourceCenter/XML/";

	private static File copyToNewResourceCenter(String documentName, DirectoryResourceCenter rc) throws Exception {
		File copy = new File(rc.getRootDirectory(), documentName);
		try (InputStream in = TestXMLRoundTrip.class.getResourceAsStream(TEST_RC_XML + documentName)) {
			assertNotNull("No " + documentName + " in the test resource center", in);
			Files.copy(in, copy.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
		return copy;
	}

	/**
	 * Saving a typed document is not supported yet (XML-D-4): the save writes nothing and raises nothing (a warning is logged), so that
	 * a project save goes on with its other resources. Replace this test by a round trip when the typed writer exists.
	 */
	@Test
	public void testTypedDocumentIsNotWritten() throws Exception {
		instanciateTestServiceManager(XMLTechnologyAdapter.class);
		XMLTechnologyAdapter ta = serviceManager.getTechnologyAdapterService().getTechnologyAdapter(XMLTechnologyAdapter.class);
		DirectoryResourceCenter rc = makeNewDirectoryResourceCenter();
		File file = copyToNewResourceCenter("example_library_1.xml", rc);
		byte[] original = Files.readAllBytes(file.toPath());

		TypedXMLResource resource = ta.getXMLFileResourceFactory().retrieveResource(file, rc);
		assertNotNull(resource);
		assertNotNull("The metamodel of the copy is not found", resource.getMetaModelResource());
		XMLModel model = resource.getResourceData();
		assertFalse(resource.isModified());
		XMLIndividual writer1 = model.getRoot().getChildren().get(0);
		assertEquals("Hector", writer1.getPropertyValue("name"));

		// XML-D-5: a change of a property value marks the resource modified
		writer1.setPropertyValue("name", "Achille");
		assertTrue(resource.isModified());

		resource.save();
		assertArrayEquals(original, Files.readAllBytes(file.toPath()));
		assertEquals("Achille", writer1.getPropertyValue("name"));
	}

	@Test
	public void testFreeDocument() throws Exception {
		instanciateTestServiceManager(XMLTechnologyAdapter.class);
		XMLTechnologyAdapter ta = serviceManager.getTechnologyAdapterService().getTechnologyAdapter(XMLTechnologyAdapter.class);
		DirectoryResourceCenter rc = makeNewDirectoryResourceCenter();
		File file = copyToNewResourceCenter("example_library_0.xml", rc);

		FreeXMLResource resource = ta.getResourceFactory(FreeXMLResourceFactory.class).retrieveResource(file, rc);
		assertNotNull(resource);
		FreeXMLDocument document = resource.getResourceData();
		XMLElement nameElement = document.getRootElement().getChildElements().get(0);
		assertEquals("A Library", nameElement.getValue());

		nameElement.setValue("Another Library");
		assertTrue(resource.isModified());
		resource.save();
		assertFalse(resource.isModified());
		String saved = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
		assertTrue("The saved file does not hold the new value:\n" + saved, saved.contains("Another Library"));

		resource.unloadResourceData(false);
		FreeXMLDocument reloaded = resource.loadResourceData();
		assertNotSame(document, reloaded);
		assertEquals("Library", reloaded.getRootElement().getName());
		assertEquals(3, reloaded.getRootElement().getChildElements().size());
		assertEquals("Another Library", reloaded.getRootElement().getChildElements().get(0).getValue());
		assertEquals(4, reloaded.getRootElement().getChildElements().get(1).getChildElements().size());
		assertEquals("truc", reloaded.getRootElement().getChildElements().get(1).getChildElements().get(0).getAttributeValue("encoding"));

		// A loaded document is not modified; changing an attribute (kept outside PAMELA) marks it modified
		assertFalse(resource.isModified());
		reloaded.getRootElement().getChildElements().get(1).getChildElements().get(0).setAttributeValue("encoding", "utf8");
		assertTrue(resource.isModified());
	}

}
