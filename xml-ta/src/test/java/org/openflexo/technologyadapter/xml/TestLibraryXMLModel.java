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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.test.OpenflexoProjectAtRunTimeTestCase;
import org.openflexo.technologyadapter.xml.metamodel.XMLComplexType;
import org.openflexo.technologyadapter.xml.metamodel.XMLEnumerationType;
import org.openflexo.technologyadapter.xml.metamodel.XSDMetaModel;
import org.openflexo.technologyadapter.xml.model.typed.XMLIndividual;
import org.openflexo.technologyadapter.xml.rm.TypedXMLResource;
import org.openflexo.technologyadapter.xml.rm.XMLModelRepository;
import org.openflexo.technologyadapter.xml.rm.XSDMetaModelRepository;
import org.openflexo.technologyadapter.xml.rm.XSDMetaModelResource;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * Perform some tests in the context of {@link XSDModel} management (an XML file conform to a XSD)
 */
@RunWith(OrderedRunner.class)
public class TestLibraryXMLModel extends OpenflexoProjectAtRunTimeTestCase {

	protected static final Logger logger = Logger.getLogger(TestLibraryXMLModel.class.getPackage().getName());

	private static XMLTechnologyAdapter xmlAdapter;
	private static XMLModelRepository<?> modelRepository;
	private static XSDMetaModelRepository<?> mmRepository;
	private static String baseUrl;

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

		xmlAdapter = serviceManager.getTechnologyAdapterService().getTechnologyAdapter(XMLTechnologyAdapter.class);

		mmRepository = xmlAdapter.getXSDMetaModelRepository(resourceCenter);
		assertNotNull(mmRepository);
		assertTrue(mmRepository.getAllResources().size() > 2);
		for (XSDMetaModelResource r : mmRepository.getAllResources()) {
			System.out.println("XSDMetaModelResource: " + r.getURI() + " : " + r);
		}

		modelRepository = xmlAdapter.getXMLModelRepository(resourceCenter);
		assertNotNull(modelRepository);
		assertTrue(modelRepository.getAllResources().size() > 4);
		for (TypedXMLResource r : modelRepository.getAllResources()) {
			System.out.println("TypedXMLResource: " + r.getURI() + " : " + r);
		}

		baseUrl = resourceCenter.getDefaultBaseURI();

		/*
		 * Found an XSD with uri: http://www.example.org/Library(library.xsd)
		 * Found an XSD with uri:
		 * http://maven.apache.org/POM/4.0.0(maven-v4_0_0.xsd) Found an XSD with
		 * uri: http://www.taskcoach.org/TSK_XSD(taskcoach.xsd)
		 */
	}

	@Test
	@TestOrder(3)
	public void testExampleLibrary1() throws FileNotFoundException, ResourceLoadingCancelledException, FlexoException, URISyntaxException {

		log("testExampleLibrary1()");

		XSDMetaModelResource libraryXSDResource = mmRepository.getResource("http://www.example.org/Library");
		assertNotNull(libraryXSDResource);
		assertFalse(libraryXSDResource.isLoaded());

		TypedXMLResource library1Resource = modelRepository.getResource(baseUrl + "/TestResourceCenter/XML/example_library_1.xml");
		assertNotNull(library1Resource);
		assertFalse(library1Resource.isLoaded());

		assertSame(libraryXSDResource, library1Resource.getMetaModelResource());
		assertTrue(library1Resource.getDependencies().contains(libraryXSDResource));

		library1Resource.loadResourceData();
		assertTrue(library1Resource.isLoaded());
		assertTrue(libraryXSDResource.isLoaded());

		XSDMetaModel metaModel = libraryXSDResource.getMetaModelData();

		Helpers.dumpTypes(metaModel);

		XMLComplexType writerType = metaModel.getComplexTypeFromURI("http://www.example.org/Library#Writer");
		XMLComplexType bookType = metaModel.getComplexTypeFromURI("http://www.example.org/Library#Book");
		XMLComplexType libraryType = metaModel.getComplexTypeFromURI("http://www.example.org/Library#LibraryType");

		XMLEnumerationType categoryType = metaModel.getEnumerationTypeFromURI("http://www.example.org/Library#BookCategory");

		// assertNotNull(modelRes.getModel().getMetaModel().getTypeFromURI("http://www.example.org/Library#Library"));

		Helpers.dumpIndividual(library1Resource.getModelData().getRoot(), "");

		XMLIndividual library = library1Resource.getModelData().getRoot();

		XMLIndividual writer1 = library.getChildren().get(0);
		XMLIndividual writer2 = library.getChildren().get(1);
		XMLIndividual book1 = library.getChildren().get(2);
		XMLIndividual book2 = library.getChildren().get(3);

		assertSame(libraryType, library.getType());
		assertSame(writerType, writer1.getType());
		assertSame(writerType, writer2.getType());
		assertSame(bookType, book1.getType());
		assertSame(bookType, book2.getType());

		System.out.println("library: " + library);
		System.out.println("writer1: " + writer1);
		System.out.println("writer2: " + writer2);
		System.out.println("book1: " + book1);
		System.out.println("book2: " + book2);

		assertEquals("Hector", writer1.getPropertyValue("name"));
		assertEquals("Simeon", writer2.getPropertyValue("name"));
		assertEquals("toto", book1.getPropertyValue("title"));
		assertEquals(35, (int) book1.getPropertyValue("pages"));
		assertEquals(categoryType.getEnumValues().get(0), book1.getPropertyValue("category"));
		assertEquals(new URI("http://www.example.org/example_library_1.xml#Hector"), book1.getPropertyValue("author"));

	}

	@Test
	@TestOrder(4)
	public void testExampleLibrary2() throws FileNotFoundException, ResourceLoadingCancelledException, FlexoException, URISyntaxException {

		log("testExampleLibrary2()");

		TypedXMLResource library2Resource = modelRepository.getResource(baseUrl + "/TestResourceCenter/XML/example_library_2.xml");
		assertNotNull(library2Resource);
		assertFalse(library2Resource.isLoaded());
		library2Resource.loadResourceData();
		assertTrue(library2Resource.isLoaded());

		Helpers.dumpIndividual(library2Resource.getModelData().getRoot(), "");
	}

	@Test
	@TestOrder(5)
	public void testExampleLibrary3() throws FileNotFoundException, ResourceLoadingCancelledException, FlexoException, URISyntaxException {

		log("testExampleLibrary3()");

		TypedXMLResource library3Resource = modelRepository.getResource(baseUrl + "/TestResourceCenter/XML/example_library_3.xml");
		assertNotNull(library3Resource);
		assertFalse(library3Resource.isLoaded());
		library3Resource.loadResourceData();
		assertTrue(library3Resource.isLoaded());

		Helpers.dumpIndividual(library3Resource.getModelData().getRoot(), "");
	}

}
