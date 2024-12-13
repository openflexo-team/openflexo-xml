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

package org.openflexo.technologyadapter.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.connie.type.ParameterizedTypeImpl;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.test.OpenflexoTestCase;
import org.openflexo.technologyadapter.xml.metamodel.XMLComplexType;
import org.openflexo.technologyadapter.xml.metamodel.XMLDataProperty;
import org.openflexo.technologyadapter.xml.metamodel.XMLEnumerationType;
import org.openflexo.technologyadapter.xml.metamodel.XMLObjectProperty;
import org.openflexo.technologyadapter.xml.metamodel.XMLProperty.XMLSupport;
import org.openflexo.technologyadapter.xml.metamodel.XMLSimpleType;
import org.openflexo.technologyadapter.xml.metamodel.XSDMetaModel;
import org.openflexo.technologyadapter.xml.rm.TypedXMLResource;
import org.openflexo.technologyadapter.xml.rm.XMLModelRepository;
import org.openflexo.technologyadapter.xml.rm.XSDMetaModelRepository;
import org.openflexo.technologyadapter.xml.rm.XSDMetaModelResource;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

@RunWith(OrderedRunner.class)
public class TestXSD extends OpenflexoTestCase {

	protected static final Logger logger = Logger.getLogger(TestXSD.class.getPackage().getName());

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

	/**
	 * Test Library XSD
	 * 
	 * @throws FlexoException
	 * @throws ResourceLoadingCancelledException
	 * @throws FileNotFoundException
	 * 
	 */
	@Test
	@TestOrder(2)
	public void testLibraryMetamodel() throws FileNotFoundException, ResourceLoadingCancelledException, FlexoException {

		XSDMetaModelResource mmRes = mmRepository.getResource("http://www.example.org/Library");

		/*for (XSDMetaModelResource r : mmRepository.getAllResources()) {
			System.out.println("> Resource: " + r.getURI());
		}*/

		assertNotNull(mmRes);
		assertFalse(mmRes.isLoaded());
		mmRes.loadResourceData();
		assertTrue(mmRes.isLoaded());

		XSDMetaModel metaModel = mmRes.getMetaModelData();

		Helpers.dumpTypes(metaModel);

		assertEquals(8, metaModel.getTypes().size());

		XMLSimpleType anyURIType = metaModel.getSimpleTypeFromURI("http://www.w3.org/2001/XMLSchema#anyURI");
		assertNotNull(anyURIType);
		XMLSimpleType stringType = metaModel.getSimpleTypeFromURI("http://www.w3.org/2001/XMLSchema#string");
		assertNotNull(stringType);
		XMLSimpleType intType = metaModel.getSimpleTypeFromURI("http://www.w3.org/2001/XMLSchema#int");
		assertNotNull(intType);

		XMLEnumerationType categoryType = metaModel.getEnumerationTypeFromURI("http://www.example.org/Library#BookCategory");
		assertNotNull(categoryType);
		assertEquals(3, categoryType.getEnumValues().size());

		XMLComplexType anyType = metaModel.getComplexTypeFromURI("http://www.w3.org/2001/XMLSchema#anyType");
		assertNotNull(anyType);

		XMLComplexType writerType = metaModel.getComplexTypeFromURI("http://www.example.org/Library#Writer");
		assertNotNull(writerType);
		XMLDataProperty writerBookProperty = (XMLDataProperty) writerType.getPropertyByName("book");
		assertNotNull(writerBookProperty);
		assertEquals(XMLSupport.ELEMENT, writerBookProperty.getXMLSupport());
		assertEquals("Book", writerBookProperty.getXMLSupportName());
		assertSame(anyURIType, writerBookProperty.getType());
		XMLDataProperty writerNameProperty = (XMLDataProperty) writerType.getPropertyByName("name");
		assertNotNull(writerNameProperty);
		assertSame(stringType, writerNameProperty.getType());

		XMLComplexType bookType = metaModel.getComplexTypeFromURI("http://www.example.org/Library#Book");
		assertNotNull(bookType);
		XMLDataProperty bookAuthorProperty = (XMLDataProperty) bookType.getPropertyByName("author");
		assertNotNull(bookAuthorProperty);
		assertSame(anyURIType, bookAuthorProperty.getType());
		XMLDataProperty bookCategoryProperty = (XMLDataProperty) bookType.getPropertyByName("category");
		assertNotNull(bookCategoryProperty);
		assertSame(categoryType, bookCategoryProperty.getType());
		XMLDataProperty bookPagesProperty = (XMLDataProperty) bookType.getPropertyByName("pages");
		assertNotNull(bookPagesProperty);
		assertSame(intType, bookPagesProperty.getType());
		XMLDataProperty bookTitleProperty = (XMLDataProperty) bookType.getPropertyByName("title");
		assertNotNull(bookTitleProperty);
		assertSame(stringType, bookTitleProperty.getType());

		XMLComplexType libraryType = metaModel.getComplexTypeFromURI("http://www.example.org/Library#LibraryType");
		assertNotNull(libraryType);
		XMLDataProperty nameProperty = (XMLDataProperty) libraryType.getPropertyByName("name");
		assertNotNull(nameProperty);
		XMLObjectProperty writersProperty = (XMLObjectProperty) libraryType.getPropertyByName("writers");
		assertNotNull(writersProperty);
		assertEquals(XMLSupport.ELEMENT, writersProperty.getXMLSupport());
		assertEquals("Writer", writersProperty.getXMLSupportName());
		assertSame(writerType, writersProperty.getType());
		assertEquals(new ParameterizedTypeImpl(List.class, XMLIndividualType.getXMLIndividualOfType(writerType)),
				writersProperty.getAccessedType());
		XMLObjectProperty booksProperty = (XMLObjectProperty) libraryType.getPropertyByName("books");
		assertNotNull(booksProperty);
		assertEquals(XMLSupport.ELEMENT, booksProperty.getXMLSupport());
		assertEquals("Book", booksProperty.getXMLSupportName());
		assertSame(bookType, booksProperty.getType());
		assertEquals(new ParameterizedTypeImpl(List.class, XMLIndividualType.getXMLIndividualOfType(bookType)),
				booksProperty.getAccessedType());

	}

	/**
	 * Test PurchaseOrderSchema XSD (http://tempuri.org/PurchaseOrderSchema.xsd)
	 * 
	 * @throws FlexoException
	 * @throws ResourceLoadingCancelledException
	 * @throws FileNotFoundException
	 * 
	 */
	@Test
	@TestOrder(3)
	public void testPurchaseOrderSchemal() throws FileNotFoundException, ResourceLoadingCancelledException, FlexoException {

		XSDMetaModelResource mmRes = mmRepository.getResource("http://tempuri.org/PurchaseOrderSchema.xsd");

		/*for (XSDMetaModelResource r : mmRepository.getAllResources()) {
			System.out.println("> Resource: " + r.getURI());
		}*/

		assertNotNull(mmRes);
		assertFalse(mmRes.isLoaded());
		mmRes.loadResourceData();
		assertTrue(mmRes.isLoaded());

		XSDMetaModel metaModel = mmRes.getMetaModelData();

		Helpers.dumpTypes(metaModel);

		assertEquals(39, metaModel.getTypes().size());

	}

	/**
	 * Test Maven XSD (http://maven.apache.org/POM/4.0.0)
	 * 
	 * @throws FlexoException
	 * @throws ResourceLoadingCancelledException
	 * @throws FileNotFoundException
	 * 
	 */
	@Test
	@TestOrder(4)
	public void testMavenMetamodel() throws FileNotFoundException, ResourceLoadingCancelledException, FlexoException {

		System.exit(-1);

		XSDMetaModelResource mmRes = mmRepository.getResource("http://maven.apache.org/POM/4.0.0");

		/*for (XSDMetaModelResource r : mmRepository.getAllResources()) {
			System.out.println("> Resource: " + r.getURI());
		}*/

		assertNotNull(mmRes);
		assertFalse(mmRes.isLoaded());
		mmRes.loadResourceData();
		assertTrue(mmRes.isLoaded());

		XSDMetaModel metaModel = mmRes.getMetaModelData();

		Helpers.dumpTypes(metaModel);

		assertEquals(39, metaModel.getTypes().size());

	}

}
