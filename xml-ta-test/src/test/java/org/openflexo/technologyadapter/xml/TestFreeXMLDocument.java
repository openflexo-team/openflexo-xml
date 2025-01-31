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

package org.openflexo.technologyadapter.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.test.OpenflexoProjectAtRunTimeTestCase;
import org.openflexo.technologyadapter.xml.model.free.FreeXMLDocument;
import org.openflexo.technologyadapter.xml.model.free.XMLElement;
import org.openflexo.technologyadapter.xml.rm.FreeXMLDocumentRepository;
import org.openflexo.technologyadapter.xml.rm.FreeXMLResource;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * Perform some tests in the context of {@link FreeXMLDocument} management
 */
@RunWith(OrderedRunner.class)
public class TestFreeXMLDocument extends OpenflexoProjectAtRunTimeTestCase {

	protected static final Logger logger = Logger.getLogger(TestFreeXMLDocument.class.getPackage().getName());

	private static XMLTechnologyAdapter xmlAdapter;
	private static FreeXMLDocumentRepository<?> freeDocumentRepository;
	private static String baseUrl;

	private static FreeXMLResource exampleLibrary0;
	private static FreeXMLResource mom2024acm;

	/**
	 * Instanciate test ResourceCenter
	 * 
	 * @throws IOException
	 */
	@Test
	@TestOrder(1)
	public void loadTestResourceCenter() throws IOException {
		log("loadTestResourceCenter()");

		instanciateTestServiceManager(XMLTechnologyAdapter.class);

		FlexoResourceCenter<?> resourceCenter = serviceManager.getResourceCenterService()
				.getFlexoResourceCenter("http://openflexo.org/xml-test");
		baseUrl = resourceCenter.getDefaultBaseURI();
		assertEquals("http://openflexo.org/xml-test", baseUrl);

		xmlAdapter = serviceManager.getTechnologyAdapterService().getTechnologyAdapter(XMLTechnologyAdapter.class);

		freeDocumentRepository = xmlAdapter.getFreeXMLDocumentRepository(resourceCenter);
		assertNotNull(freeDocumentRepository);
		for (FreeXMLResource r : freeDocumentRepository.getAllResources()) {
			System.out.println("FreeXMLResource: " + r.getURI() + " : " + r);
		}

		exampleLibrary0 = (FreeXMLResource) serviceManager.getResourceManager()
				.getResource("http://openflexo.org/xml-test/TestResourceCenter/XML/example_library_0.xml");
		assertNotNull(exampleLibrary0);
		assertTrue(freeDocumentRepository.getAllResources().contains(exampleLibrary0));

		mom2024acm = (FreeXMLResource) serviceManager.getResourceManager()
				.getResource("http://openflexo.org/xml-test/TestResourceCenter/XML/MoM'2024_ACM_data_2024-08-08.xml");
		assertNotNull(mom2024acm);
		assertTrue(freeDocumentRepository.getAllResources().contains(mom2024acm));

	}

	/**
	 * Test example_library_0.xml
	 * 
	 * @throws FileNotFoundException
	 * @throws ResourceLoadingCancelledException
	 * @throws FlexoException
	 */
	@Test
	@TestOrder(2)
	public void testExampleLibrary0() throws FileNotFoundException, ResourceLoadingCancelledException, FlexoException {

		log("testExampleLibrary0()");

		assertFalse(mom2024acm.isLoaded());
		assertNull(exampleLibrary0.getLoadedResourceData());
		assertNotNull(exampleLibrary0.loadResourceData());
		assertNotNull(exampleLibrary0.getResourceData());
		assertTrue(exampleLibrary0.isLoaded());

		FreeXMLDocument freeXMLDocument = exampleLibrary0.getResourceData();
		System.out.println(freeXMLDocument.getXMLRepresentation());

		assertNotNull(freeXMLDocument.getRootElement());
		XMLElement rootElement = freeXMLDocument.getRootElement();
		assertEquals("Library", rootElement.getName());
		assertEquals(3, rootElement.getChildElements().size());
		XMLElement nameElement = rootElement.getChildElements().get(0);
		assertEquals("A Library", nameElement.getValue());
		XMLElement book1Element = rootElement.getChildElements().get(1);
		assertEquals(4, book1Element.getChildElements().size());
		XMLElement book2Element = rootElement.getChildElements().get(2);
		assertEquals(4, book2Element.getChildElements().size());

		assertEquals("http://openflexo.org/xml-test/TestResourceCenter/XML/example_library_0.xml/Library", rootElement.getURI());
		assertEquals("http://openflexo.org/xml-test/TestResourceCenter/XML/example_library_0.xml/Library/name", nameElement.getURI());
		assertEquals("http://openflexo.org/xml-test/TestResourceCenter/XML/example_library_0.xml/Library/Book.0", book1Element.getURI());
		assertEquals("http://openflexo.org/xml-test/TestResourceCenter/XML/example_library_0.xml/Library/Book.1", book2Element.getURI());

	}

	/**
	 * Test MoM'2024_ACM_data_2024-08-08.xml
	 * 
	 * @throws FileNotFoundException
	 * @throws ResourceLoadingCancelledException
	 * @throws FlexoException
	 */
	@Test
	@TestOrder(3)
	public void testMom2024acm() throws FileNotFoundException, ResourceLoadingCancelledException, FlexoException {

		log("testMom2024acm()");

		assertFalse(mom2024acm.isLoaded());
		assertNull(mom2024acm.getLoadedResourceData());
		assertNotNull(mom2024acm.loadResourceData());
		assertNotNull(mom2024acm.getResourceData());
		assertTrue(mom2024acm.isLoaded());

		FreeXMLDocument freeXMLDocument = mom2024acm.getResourceData();
		System.out.println(freeXMLDocument.getXMLRepresentation());

		assertNotNull(freeXMLDocument.getRootElement());
		XMLElement rootElement = freeXMLDocument.getRootElement();
		assertEquals("erights_record", rootElement.getName());
		assertEquals(10, rootElement.getChildElements().size());
		XMLElement parentDataElement = rootElement.getChildElements().get(0);
		assertEquals("parent_data", parentDataElement.getName());
		XMLElement paper1Element = rootElement.getChildElements().get(1);
		assertEquals("paper", paper1Element.getName());
		assertEquals(8, paper1Element.getChildElements().size());
		XMLElement paper2Element = rootElement.getChildElements().get(2);
		assertEquals("paper", paper2Element.getName());
		assertEquals(8, paper2Element.getChildElements().size());

		assertEquals("http://openflexo.org/xml-test/TestResourceCenter/XML/MoM'2024_ACM_data_2024-08-08.xml/erights_record",
				rootElement.getURI());
		assertEquals("http://openflexo.org/xml-test/TestResourceCenter/XML/MoM'2024_ACM_data_2024-08-08.xml/erights_record/parent_data",
				parentDataElement.getURI());
		assertEquals("http://openflexo.org/xml-test/TestResourceCenter/XML/MoM'2024_ACM_data_2024-08-08.xml/erights_record/paper.0",
				paper1Element.getURI());
		assertEquals("http://openflexo.org/xml-test/TestResourceCenter/XML/MoM'2024_ACM_data_2024-08-08.xml/erights_record/paper.1",
				paper2Element.getURI());

	}

}
