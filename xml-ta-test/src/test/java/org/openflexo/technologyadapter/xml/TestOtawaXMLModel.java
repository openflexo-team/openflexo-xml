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
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.test.OpenflexoProjectAtRunTimeTestCase;
import org.openflexo.technologyadapter.xml.metamodel.XSDMetaModel;
import org.openflexo.technologyadapter.xml.model.typed.XMLIndividual;
import org.openflexo.technologyadapter.xml.rm.FreeXMLDocumentRepository;
import org.openflexo.technologyadapter.xml.rm.FreeXMLResource;
import org.openflexo.technologyadapter.xml.rm.TypedXMLResource;
import org.openflexo.technologyadapter.xml.rm.XMLModelRepository;
import org.openflexo.technologyadapter.xml.rm.XSDMetaModelRepository;
import org.openflexo.technologyadapter.xml.rm.XSDMetaModelResource;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * Perform some tests in the context of {@link XSDModel} management (an XML file conform to a XSD)<br>
 * Test Otawa files
 */
@RunWith(OrderedRunner.class)
public class TestOtawaXMLModel extends OpenflexoProjectAtRunTimeTestCase {

	protected static final Logger logger = Logger.getLogger(TestOtawaXMLModel.class.getPackage().getName());

	private static XMLTechnologyAdapter xmlAdapter;
	private static FreeXMLDocumentRepository<?> freeDocumentRepository;
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

		freeDocumentRepository = xmlAdapter.getFreeXMLDocumentRepository(resourceCenter);
		assertNotNull(freeDocumentRepository);
		for (FreeXMLResource r : freeDocumentRepository.getAllResources()) {
			System.out.println("FreeXMLResource: " + r.getURI() + " : " + r);
		}

		baseUrl = resourceCenter.getDefaultBaseURI();

	}

	@Test
	@TestOrder(2)
	public void testOtawaCache() throws FileNotFoundException, ResourceLoadingCancelledException, FlexoException, URISyntaxException {

		log("testOtawaCache()");

		XSDMetaModelResource otawaCacheXSDResource = mmRepository.getResource("http://mem4csd.telecom-paris.fr/OtawaCache");
		assertNotNull(otawaCacheXSDResource);
		assertFalse(otawaCacheXSDResource.isLoaded());

		TypedXMLResource cacheResource = modelRepository.getResource(baseUrl + "/TestResourceCenter/CaoticWorkflow/otawa_caches.xml");
		assertNotNull(cacheResource);
		assertFalse(cacheResource.isLoaded());

		assertSame(otawaCacheXSDResource, cacheResource.getMetaModelResource());
		assertTrue(cacheResource.getDependencies().contains(otawaCacheXSDResource));

		cacheResource.loadResourceData();
		assertTrue(cacheResource.isLoaded());
		assertTrue(otawaCacheXSDResource.isLoaded());

		XSDMetaModel metaModel = otawaCacheXSDResource.getMetaModelData();

		Helpers.dumpTypes(metaModel);

		Helpers.dumpIndividual(cacheResource.getModelData().getRoot(), "");

		XMLIndividual cacheConfig = cacheResource.getModelData().getRoot();
		assertNotNull(cacheConfig);
		assertSame(cacheConfig.getType(), metaModel.getComplexTypeFromURI("http://mem4csd.telecom-paris.fr/OtawaCache#cache-config"));

		XMLIndividual dCache = cacheConfig.getPropertyValue("dcache");
		assertNotNull(dCache);
		assertSame(dCache.getType(), metaModel.getComplexTypeFromURI("http://mem4csd.telecom-paris.fr/OtawaCache#dcache"));
		assertEquals((byte) 5, (byte) dCache.getPropertyValue("blockBits"));
		assertEquals((byte) 2, (byte) dCache.getPropertyValue("wayBits"));
		assertEquals((byte) 9, (byte) dCache.getPropertyValue("rowBits"));
		assertEquals((byte) 25, (byte) dCache.getPropertyValue("missPenalty"));

		XMLIndividual iCache = cacheConfig.getPropertyValue("icache");
		assertNotNull(iCache);
		assertSame(iCache.getType(), metaModel.getComplexTypeFromURI("http://mem4csd.telecom-paris.fr/OtawaCache#icache"));
		assertEquals((byte) 5, (byte) iCache.getPropertyValue("blockBits"));
		assertEquals((byte) 2, (byte) iCache.getPropertyValue("wayBits"));
		assertEquals((byte) 9, (byte) iCache.getPropertyValue("rowBits"));
		assertEquals((byte) 25, (byte) iCache.getPropertyValue("missPenalty"));

	}

	@Test
	@TestOrder(3)
	public void testOtawaMemory() throws FileNotFoundException, ResourceLoadingCancelledException, FlexoException, URISyntaxException {

		log("testOtawaMemory()");

		XSDMetaModelResource otawaMemoryXSDResource = mmRepository.getResource("http://mem4csd.telecom-paris.fr/OtawaMemory");
		assertNotNull(otawaMemoryXSDResource);
		assertFalse(otawaMemoryXSDResource.isLoaded());

		TypedXMLResource memoryResource = modelRepository.getResource(baseUrl + "/TestResourceCenter/CaoticWorkflow/otawa_memory.xml");
		assertNotNull(memoryResource);
		assertFalse(memoryResource.isLoaded());

		assertSame(otawaMemoryXSDResource, memoryResource.getMetaModelResource());
		assertTrue(memoryResource.getDependencies().contains(otawaMemoryXSDResource));

		memoryResource.loadResourceData();
		assertTrue(memoryResource.isLoaded());
		assertTrue(otawaMemoryXSDResource.isLoaded());

		XSDMetaModel metaModel = otawaMemoryXSDResource.getMetaModelData();

		Helpers.dumpTypes(metaModel);

		Helpers.dumpIndividual(memoryResource.getModelData().getRoot(), "");

		XMLIndividual memory = memoryResource.getModelData().getRoot();
		assertNotNull(memory);
		assertSame(memory.getType(), metaModel.getComplexTypeFromURI("http://mem4csd.telecom-paris.fr/OtawaMemory#memory"));

		XMLIndividual banks = memory.getPropertyValue("banks");
		assertNotNull(banks);
		assertSame(banks.getType(), metaModel.getComplexTypeFromURI("http://mem4csd.telecom-paris.fr/OtawaMemory#banks"));

		XMLIndividual bank = (XMLIndividual) ((List) banks.getPropertyValues("banks")).get(0);
		assertNotNull(bank);
		assertSame(bank.getType(), metaModel.getComplexTypeFromURI("http://mem4csd.telecom-paris.fr/OtawaMemory#bank"));

		XMLIndividual address = bank.getPropertyValue("address");
		assertNotNull(address);
		assertSame(address.getType(), metaModel.getComplexTypeFromURI("http://mem4csd.telecom-paris.fr/OtawaMemory#address"));
		assertEquals("0x00000000", address.getPropertyValue("offset"));

		assertEquals("RAM", bank.getPropertyValue("name"));
		assertEquals(true, (boolean) bank.getPropertyValue("cachable"));
		assertEquals((byte) 25, (byte) bank.getPropertyValue("latency"));
		assertEquals("0x1fffffff", bank.getPropertyValue("size"));
		assertEquals("DRAM", bank.getPropertyValue("type"));
		assertEquals(true, (boolean) bank.getPropertyValue("writable"));

	}

	@Test
	@TestOrder(4)
	public void testOtawaPipeline() throws FileNotFoundException, ResourceLoadingCancelledException, FlexoException, URISyntaxException {

		log("testOtawaPipeline()");

		XSDMetaModelResource otawaPipelineXSDResource = mmRepository.getResource("http://mem4csd.telecom-paris.fr/OtawaPipeline");
		assertNotNull(otawaPipelineXSDResource);
		assertFalse(otawaPipelineXSDResource.isLoaded());

		TypedXMLResource pipelineResource = modelRepository.getResource(baseUrl + "/TestResourceCenter/CaoticWorkflow/otawa_pipeline.xml");
		assertNotNull(pipelineResource);
		assertFalse(pipelineResource.isLoaded());

		assertSame(otawaPipelineXSDResource, pipelineResource.getMetaModelResource());
		assertTrue(pipelineResource.getDependencies().contains(otawaPipelineXSDResource));

		pipelineResource.loadResourceData();
		assertTrue(pipelineResource.isLoaded());
		assertTrue(otawaPipelineXSDResource.isLoaded());

		XSDMetaModel metaModel = otawaPipelineXSDResource.getMetaModelData();

		Helpers.dumpTypes(metaModel);

		Helpers.dumpIndividual(pipelineResource.getModelData().getRoot(), "");

		XMLIndividual processor = pipelineResource.getModelData().getRoot();
		assertNotNull(processor);
		assertSame(processor.getType(), metaModel.getComplexTypeFromURI("http://mem4csd.telecom-paris.fr/OtawaPipeline#processor"));
		assertEquals("patmos", processor.getPropertyValue("arch"));
		assertEquals("otawa::hard::Processor", processor.getPropertyValue("class"));
		assertEquals("patmos", processor.getPropertyValue("model"));

		XMLIndividual stagesIndiv = processor.getPropertyValue("stages");
		assertNotNull(stagesIndiv);
		assertSame(stagesIndiv.getType(), metaModel.getComplexTypeFromURI("http://mem4csd.telecom-paris.fr/OtawaPipeline#stages"));

		List<XMLIndividual> stages = stagesIndiv.getPropertyValues("stages");
		assertEquals(5, stages.size());

		XMLIndividual stage1 = stages.get(0);
		assertEquals("FE", stage1.getPropertyValue("id"));
		assertEquals("FE", stage1.getPropertyValue("name"));
		assertEquals((byte) 2, (byte) stage1.getPropertyValue("width"));
		assertEquals("FETCH", stage1.getPropertyValue("type"));

		XMLIndividual stage2 = stages.get(1);
		assertEquals("DE", stage2.getPropertyValue("id"));
		assertEquals("DE", stage2.getPropertyValue("name"));
		assertEquals((byte) 2, (byte) stage2.getPropertyValue("width"));
		assertEquals("LAZY", stage2.getPropertyValue("type"));

		XMLIndividual stage3 = stages.get(2);
		assertEquals("EX", stage3.getPropertyValue("id"));
		assertEquals("EX", stage3.getPropertyValue("name"));
		assertEquals((byte) 2, (byte) stage3.getPropertyValue("width"));
		assertEquals(true, (boolean) stage3.getPropertyValue("ordered"));
		XMLIndividual dispatch = stage3.getPropertyValue("dispatch");
		assertNotNull(dispatch);
		assertSame(dispatch.getType(), metaModel.getComplexTypeFromURI("http://mem4csd.telecom-paris.fr/OtawaPipeline#dispatch"));
		XMLIndividual inst = dispatch.getPropertyValue("inst");
		assertNotNull(inst);
		assertSame(inst.getType(), metaModel.getComplexTypeFromURI("http://mem4csd.telecom-paris.fr/OtawaPipeline#inst"));
		List<XMLIndividual> fuList = inst.getPropertyValues("fus");
		assertNotNull(fuList);
		assertEquals(1, fuList.size());
		XMLIndividual fu = fuList.get(0);
		assertSame(fu.getType(), metaModel.getComplexTypeFromURI("http://mem4csd.telecom-paris.fr/OtawaPipeline#fu"));
		assertEquals("ALU", fu.getPropertyValue("ref"));
		XMLIndividual fus = stage3.getPropertyValue("fus");
		assertNotNull(fus);
		assertSame(fus.getType(), metaModel.getComplexTypeFromURI("http://mem4csd.telecom-paris.fr/OtawaPipeline#fus"));
		List<XMLIndividual> fuList2 = fus.getPropertyValues("fus");
		assertNotNull(fuList2);
		assertEquals(1, fuList2.size());
		XMLIndividual fu2 = fuList2.get(0);
		assertSame(fu2.getType(), metaModel.getComplexTypeFromURI("http://mem4csd.telecom-paris.fr/OtawaPipeline#fu"));
		assertEquals("ALU", fu2.getPropertyValue("id"));
		assertEquals("ALU", fu2.getPropertyValue("name"));
		assertEquals((byte) 2, (byte) fu2.getPropertyValue("width"));
		assertEquals((byte) 1, (byte) fu2.getPropertyValue("latency"));

		XMLIndividual stage4 = stages.get(3);
		assertEquals("MEM", stage4.getPropertyValue("id"));
		assertEquals("MEM", stage4.getPropertyValue("name"));
		assertEquals((byte) 2, (byte) stage4.getPropertyValue("width"));
		assertEquals("LAZY", stage4.getPropertyValue("type"));

		XMLIndividual stage5 = stages.get(4);
		assertEquals("CM", stage5.getPropertyValue("id"));
		assertEquals("CM", stage5.getPropertyValue("name"));
		assertEquals((byte) 2, (byte) stage5.getPropertyValue("width"));
		assertEquals("COMMIT", stage5.getPropertyValue("type"));

	}

}
