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
import java.util.logging.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.test.OpenflexoTestCase;
import org.openflexo.technologyadapter.xml.metamodel.XMLComplexType;
import org.openflexo.technologyadapter.xml.metamodel.XMLDataProperty;
import org.openflexo.technologyadapter.xml.metamodel.XMLObjectProperty;
import org.openflexo.technologyadapter.xml.metamodel.XMLProperty;
import org.openflexo.technologyadapter.xml.metamodel.XMLProperty.XMLSupport;
import org.openflexo.technologyadapter.xml.metamodel.XMLSimpleType;
import org.openflexo.technologyadapter.xml.metamodel.XMLType;
import org.openflexo.technologyadapter.xml.metamodel.XSDMetaModel;
import org.openflexo.technologyadapter.xml.rm.TypedXMLResource;
import org.openflexo.technologyadapter.xml.rm.XMLModelRepository;
import org.openflexo.technologyadapter.xml.rm.XSDMetaModelRepository;
import org.openflexo.technologyadapter.xml.rm.XSDMetaModelResource;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * Perform some tests in the context of {@link XSDMetaModel} management (XSD files)
 */
@RunWith(OrderedRunner.class)
public class TestOtawaXSD extends OpenflexoTestCase {

	protected static final Logger logger = Logger.getLogger(TestOtawaXSD.class.getPackage().getName());

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
	 * Test Otawa cache XSD (http://mem4csd.telecom-paris.fr/OtawaCache)
	 * 
	 * @throws FlexoException
	 * @throws ResourceLoadingCancelledException
	 * @throws FileNotFoundException
	 * 
	 */
	@Test
	@TestOrder(4)
	public void testOtawaCache() throws FileNotFoundException, ResourceLoadingCancelledException, FlexoException {

		XSDMetaModelResource mmRes = mmRepository.getResource("http://mem4csd.telecom-paris.fr/OtawaCache");

		/*for (XSDMetaModelResource r : mmRepository.getAllResources()) {
			System.out.println("> Resource: " + r.getURI());
		}*/

		assertNotNull(mmRes);
		assertFalse(mmRes.isLoaded());
		mmRes.loadResourceData();
		assertTrue(mmRes.isLoaded());

		XSDMetaModel metaModel = mmRes.getMetaModelData();

		Helpers.dumpTypes(metaModel);

		assertEquals(5, metaModel.getTypes().size());

		XMLSimpleType byteType = metaModel.getSimpleTypeFromURI("http://www.w3.org/2001/XMLSchema#byte");
		assertNotNull(byteType);

		XMLComplexType anyType = metaModel.getComplexTypeFromURI("http://www.w3.org/2001/XMLSchema#anyType");
		assertNotNull(anyType);

		XMLComplexType iCacheType = metaModel.getComplexTypeFromURI("http://mem4csd.telecom-paris.fr/OtawaCache#icache");
		assertNotNull(iCacheType);
		XMLDataProperty blockBitsProperty = (XMLDataProperty) iCacheType.getPropertyByName("blockBits");
		assertNotNull(blockBitsProperty);
		assertEquals(XMLSupport.ELEMENT, blockBitsProperty.getXMLSupport());
		assertEquals("block_bits", blockBitsProperty.getXMLSupportName());
		assertSame(byteType, blockBitsProperty.getType());
		XMLDataProperty missPenaltyProperty = (XMLDataProperty) iCacheType.getPropertyByName("missPenalty");
		assertNotNull(missPenaltyProperty);
		assertEquals(XMLSupport.ELEMENT, missPenaltyProperty.getXMLSupport());
		assertEquals("miss_penalty", missPenaltyProperty.getXMLSupportName());
		assertSame(byteType, missPenaltyProperty.getType());
		XMLDataProperty rowBitsProperty = (XMLDataProperty) iCacheType.getPropertyByName("rowBits");
		assertNotNull(rowBitsProperty);
		assertEquals(XMLSupport.ELEMENT, rowBitsProperty.getXMLSupport());
		assertEquals("row_bits", rowBitsProperty.getXMLSupportName());
		assertSame(byteType, rowBitsProperty.getType());
		XMLDataProperty wayBitsProperty = (XMLDataProperty) iCacheType.getPropertyByName("wayBits");
		assertNotNull(wayBitsProperty);
		assertEquals(XMLSupport.ELEMENT, wayBitsProperty.getXMLSupport());
		assertEquals("way_bits", wayBitsProperty.getXMLSupportName());
		assertSame(byteType, wayBitsProperty.getType());

		XMLComplexType dCacheType = metaModel.getComplexTypeFromURI("http://mem4csd.telecom-paris.fr/OtawaCache#dcache");
		assertNotNull(dCacheType);
		blockBitsProperty = (XMLDataProperty) dCacheType.getPropertyByName("blockBits");
		assertNotNull(blockBitsProperty);
		assertEquals(XMLSupport.ELEMENT, blockBitsProperty.getXMLSupport());
		assertEquals("block_bits", blockBitsProperty.getXMLSupportName());
		assertSame(byteType, blockBitsProperty.getType());
		missPenaltyProperty = (XMLDataProperty) dCacheType.getPropertyByName("missPenalty");
		assertNotNull(missPenaltyProperty);
		assertEquals(XMLSupport.ELEMENT, missPenaltyProperty.getXMLSupport());
		assertEquals("miss_penalty", missPenaltyProperty.getXMLSupportName());
		assertSame(byteType, missPenaltyProperty.getType());
		rowBitsProperty = (XMLDataProperty) dCacheType.getPropertyByName("rowBits");
		assertNotNull(rowBitsProperty);
		assertEquals(XMLSupport.ELEMENT, rowBitsProperty.getXMLSupport());
		assertEquals("row_bits", rowBitsProperty.getXMLSupportName());
		assertSame(byteType, rowBitsProperty.getType());
		wayBitsProperty = (XMLDataProperty) dCacheType.getPropertyByName("wayBits");
		assertNotNull(wayBitsProperty);
		assertEquals(XMLSupport.ELEMENT, wayBitsProperty.getXMLSupport());
		assertEquals("way_bits", wayBitsProperty.getXMLSupportName());
		assertSame(byteType, wayBitsProperty.getType());

		XMLComplexType cacheConfigType = metaModel.getComplexTypeFromURI("http://mem4csd.telecom-paris.fr/OtawaCache#cache-config");
		assertNotNull(cacheConfigType);
		XMLObjectProperty dcacheProperty = (XMLObjectProperty) cacheConfigType.getPropertyByName("dcache");
		assertNotNull(dcacheProperty);
		assertEquals(XMLSupport.ELEMENT, dcacheProperty.getXMLSupport());
		assertEquals("dcache", dcacheProperty.getXMLSupportName());
		assertSame(dCacheType, dcacheProperty.getType());
		XMLObjectProperty iCacheProperty = (XMLObjectProperty) cacheConfigType.getPropertyByName("icache");
		assertNotNull(iCacheProperty);
		assertEquals(XMLSupport.ELEMENT, iCacheProperty.getXMLSupport());
		assertEquals("icache", iCacheProperty.getXMLSupportName());
		assertSame(iCacheType, iCacheProperty.getType());

	}

	private XMLProperty<?, ?> assertProperty(String propertyName, XMLType propertyType, XMLComplexType ownerType) {
		XMLProperty<?, ?> p = ownerType.getPropertyByName(propertyName);
		assertNotNull(p);
		assertSame(propertyType, p.getType());
		return p;
	}

	/**
	 * Test Otawa memory XSD (http://mem4csd.telecom-paris.fr/OtawaMemory)
	 * 
	 * @throws FlexoException
	 * @throws ResourceLoadingCancelledException
	 * @throws FileNotFoundException
	 * 
	 */
	@Test
	@TestOrder(5)
	public void testOtawaMemory() throws FileNotFoundException, ResourceLoadingCancelledException, FlexoException {

		XSDMetaModelResource mmRes = mmRepository.getResource("http://mem4csd.telecom-paris.fr/OtawaMemory");

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

		XMLSimpleType byteType = metaModel.getSimpleTypeFromURI("http://www.w3.org/2001/XMLSchema#byte");
		assertNotNull(byteType);

		XMLSimpleType booleanType = metaModel.getSimpleTypeFromURI("http://www.w3.org/2001/XMLSchema#boolean");
		assertNotNull(booleanType);

		XMLSimpleType stringType = metaModel.getSimpleTypeFromURI("http://www.w3.org/2001/XMLSchema#string");
		assertNotNull(stringType);

		XMLComplexType anyType = metaModel.getComplexTypeFromURI("http://www.w3.org/2001/XMLSchema#anyType");
		assertNotNull(anyType);

		XMLComplexType addressType = metaModel.getComplexTypeFromURI("http://mem4csd.telecom-paris.fr/OtawaMemory#address");
		assertNotNull(addressType);
		XMLDataProperty offsetProperty = (XMLDataProperty) assertProperty("offset", stringType, addressType);

		XMLComplexType bankType = metaModel.getComplexTypeFromURI("http://mem4csd.telecom-paris.fr/OtawaMemory#bank");
		assertNotNull(bankType);
		XMLObjectProperty addressProperty = (XMLObjectProperty) assertProperty("address", addressType, bankType);
		XMLDataProperty cachableProperty = (XMLDataProperty) assertProperty("cachable", booleanType, bankType);
		XMLDataProperty latencyProperty = (XMLDataProperty) assertProperty("latency", byteType, bankType);
		XMLDataProperty nameProperty = (XMLDataProperty) assertProperty("name", stringType, bankType);
		XMLDataProperty sizeProperty = (XMLDataProperty) assertProperty("size", stringType, bankType);
		XMLDataProperty typeProperty = (XMLDataProperty) assertProperty("type", stringType, bankType);
		XMLDataProperty writableProperty = (XMLDataProperty) assertProperty("writable", booleanType, bankType);

		XMLComplexType banksType = metaModel.getComplexTypeFromURI("http://mem4csd.telecom-paris.fr/OtawaMemory#banks");
		assertNotNull(banksType);
		XMLObjectProperty banksProperty = (XMLObjectProperty) assertProperty("banks", bankType, banksType);

		XMLComplexType memoryType = metaModel.getComplexTypeFromURI("http://mem4csd.telecom-paris.fr/OtawaMemory#memory");
		assertNotNull(memoryType);
		XMLObjectProperty banks2Property = (XMLObjectProperty) assertProperty("banks", banksType, memoryType);

	}

	/**
	 * Test Otawa pipeline XSD (http://mem4csd.telecom-paris.fr/OtawaPipeline)
	 * 
	 * @throws FlexoException
	 * @throws ResourceLoadingCancelledException
	 * @throws FileNotFoundException
	 * 
	 */
	@Test
	@TestOrder(5)
	public void testOtawaPipeline() throws FileNotFoundException, ResourceLoadingCancelledException, FlexoException {

		XSDMetaModelResource mmRes = mmRepository.getResource("http://mem4csd.telecom-paris.fr/OtawaPipeline");

		/*for (XSDMetaModelResource r : mmRepository.getAllResources()) {
			System.out.println("> Resource: " + r.getURI());
		}*/

		assertNotNull(mmRes);
		assertFalse(mmRes.isLoaded());
		mmRes.loadResourceData();
		assertTrue(mmRes.isLoaded());

		XSDMetaModel metaModel = mmRes.getMetaModelData();

		Helpers.dumpTypes(metaModel);

		assertEquals(11, metaModel.getTypes().size());

		XMLSimpleType byteType = metaModel.getSimpleTypeFromURI("http://www.w3.org/2001/XMLSchema#byte");
		assertNotNull(byteType);

		XMLSimpleType booleanType = metaModel.getSimpleTypeFromURI("http://www.w3.org/2001/XMLSchema#boolean");
		assertNotNull(booleanType);

		XMLSimpleType stringType = metaModel.getSimpleTypeFromURI("http://www.w3.org/2001/XMLSchema#string");
		assertNotNull(stringType);

		XMLComplexType anyType = metaModel.getComplexTypeFromURI("http://www.w3.org/2001/XMLSchema#anyType");
		assertNotNull(anyType);

		XMLComplexType fuType = metaModel.getComplexTypeFromURI("http://mem4csd.telecom-paris.fr/OtawaPipeline#fu");
		assertNotNull(fuType);
		XMLDataProperty idProperty = (XMLDataProperty) assertProperty("id", stringType, fuType);
		XMLDataProperty latencyProperty = (XMLDataProperty) assertProperty("latency", byteType, fuType);
		XMLDataProperty nameProperty = (XMLDataProperty) assertProperty("name", stringType, fuType);
		XMLDataProperty refProperty = (XMLDataProperty) assertProperty("ref", stringType, fuType);
		XMLDataProperty widthProperty = (XMLDataProperty) assertProperty("width", byteType, fuType);

		XMLComplexType fusType = metaModel.getComplexTypeFromURI("http://mem4csd.telecom-paris.fr/OtawaPipeline#fus");
		assertNotNull(fusType);
		XMLObjectProperty fuProperty = (XMLObjectProperty) assertProperty("fus", fuType, fusType);

		XMLComplexType instType = metaModel.getComplexTypeFromURI("http://mem4csd.telecom-paris.fr/OtawaPipeline#inst");
		assertNotNull(instType);
		XMLObjectProperty fuProperty2 = (XMLObjectProperty) assertProperty("fus", fuType, instType);
		XMLDataProperty typeProperty = (XMLDataProperty) assertProperty("type", stringType, instType);

		XMLComplexType dispatchType = metaModel.getComplexTypeFromURI("http://mem4csd.telecom-paris.fr/OtawaPipeline#dispatch");
		assertNotNull(dispatchType);
		XMLObjectProperty instProperty = (XMLObjectProperty) assertProperty("inst", instType, dispatchType);

		XMLComplexType stageType = metaModel.getComplexTypeFromURI("http://mem4csd.telecom-paris.fr/OtawaPipeline#stage");
		assertNotNull(stageType);
		XMLObjectProperty dispatchProperty = (XMLObjectProperty) assertProperty("dispatch", dispatchType, stageType);
		XMLObjectProperty fusProperty = (XMLObjectProperty) assertProperty("fus", fusType, stageType);
		XMLDataProperty idProperty2 = (XMLDataProperty) assertProperty("id", stringType, stageType);
		XMLDataProperty nameProperty2 = (XMLDataProperty) assertProperty("name", stringType, stageType);
		XMLDataProperty orderedProperty = (XMLDataProperty) assertProperty("ordered", booleanType, stageType);
		XMLDataProperty typeProperty2 = (XMLDataProperty) assertProperty("type", stringType, stageType);
		XMLDataProperty writableProperty = (XMLDataProperty) assertProperty("width", byteType, stageType);

		XMLComplexType stagesType = metaModel.getComplexTypeFromURI("http://mem4csd.telecom-paris.fr/OtawaPipeline#stages");
		assertNotNull(stagesType);
		XMLObjectProperty stagesProperty = (XMLObjectProperty) assertProperty("stages", stageType, stagesType);

		XMLComplexType processorType = metaModel.getComplexTypeFromURI("http://mem4csd.telecom-paris.fr/OtawaPipeline#processor");
		assertNotNull(processorType);
		XMLDataProperty archProperty = (XMLDataProperty) assertProperty("arch", stringType, processorType);
		XMLDataProperty classProperty = (XMLDataProperty) assertProperty("class", stringType, processorType);
		XMLDataProperty modelProperty = (XMLDataProperty) assertProperty("model", stringType, processorType);
		XMLObjectProperty stagesProperty2 = (XMLObjectProperty) assertProperty("stages", stagesType, processorType);

	}

}
