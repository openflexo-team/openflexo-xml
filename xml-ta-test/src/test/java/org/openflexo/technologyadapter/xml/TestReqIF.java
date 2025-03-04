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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.test.OpenflexoTestCase;
import org.openflexo.technologyadapter.xml.metamodel.XMLComplexType;
import org.openflexo.technologyadapter.xml.metamodel.XSDMetaModel;
import org.openflexo.technologyadapter.xml.model.typed.XMLIndividual;
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
public class TestReqIF extends OpenflexoTestCase {

	protected static final Logger logger = Logger.getLogger(TestReqIF.class.getPackage().getName());

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
	public void testReqIFMetamodel() throws FileNotFoundException, ResourceLoadingCancelledException, FlexoException {

		XSDMetaModelResource mmRes = mmRepository.getResource("http://www.omg.org/spec/ReqIF/20110401/reqif.xsd");

		/*for (XSDMetaModelResource r : mmRepository.getAllResources()) {
			System.out.println("> Resource: " + r.getURI());
		}*/

		assertNotNull(mmRes);
		assertFalse(mmRes.isLoaded());
		mmRes.loadResourceData();
		assertTrue(mmRes.isLoaded());

		XSDMetaModel metaModel = mmRes.getMetaModelData();

		Helpers.dumpTypes(metaModel);

	}

	@Test
	@TestOrder(3)
	public void testSample() throws FileNotFoundException, ResourceLoadingCancelledException, FlexoException, URISyntaxException {

		log("testSample()");

		XSDMetaModelResource libraryXSDResource = mmRepository.getResource("http://www.omg.org/spec/ReqIF/20110401/reqif.xsd");
		assertNotNull(libraryXSDResource);
		assertTrue(libraryXSDResource.isLoaded());

		TypedXMLResource sampleResource = modelRepository.getResource(baseUrl + "/TestResourceCenter/ReqIF/Sample.reqif");
		assertNotNull(sampleResource);
		assertFalse(sampleResource.isLoaded());

		assertSame(libraryXSDResource, sampleResource.getMetaModelResource());
		assertTrue(sampleResource.getDependencies().contains(libraryXSDResource));

		sampleResource.loadResourceData();
		assertTrue(sampleResource.isLoaded());
		assertTrue(libraryXSDResource.isLoaded());

		XSDMetaModel metaModel = libraryXSDResource.getMetaModelData();
		// Helpers.dumpTypes(metaModel);

		Helpers.dumpIndividual(sampleResource.getModelData().getRoot(), "");

		XMLComplexType reqIfType = metaModel.getComplexTypeFromURI("http://www.omg.org/spec/ReqIF/20110401/reqif.xsd#REQ-IF");
		assertNotNull(reqIfType);
		XMLComplexType headerType = metaModel.getComplexTypeFromURI("http://www.omg.org/spec/ReqIF/20110401/reqif.xsd#REQ-IF#THE-HEADER");
		assertNotNull(headerType);
		XMLComplexType coreContentType = metaModel
				.getComplexTypeFromURI("http://www.omg.org/spec/ReqIF/20110401/reqif.xsd#REQ-IF#CORE-CONTENT");
		assertNotNull(coreContentType);

		XMLIndividual reqIf = sampleResource.getModelData().getRoot();
		XMLIndividual header = reqIf.getChildren().get(0);
		XMLIndividual coreContent = reqIf.getChildren().get(1);

		assertSame(reqIfType, reqIf.getType());
		assertSame(headerType, header.getType());
		assertSame(coreContentType, coreContent.getType());

		assertSame(header, reqIf.getPropertyValue("theheader"));
		assertSame(coreContent, reqIf.getPropertyValue("corecontent"));

		// Check references ok

		XMLIndividual reqIfContent = coreContent.getPropertyValue("reqifcontent");
		XMLIndividual specifications = reqIfContent.getPropertyValue("specifications");
		XMLIndividual specification = specifications.getPropertyValue("specification");
		XMLIndividual specificationType = specification.getPropertyValue("type");
		XMLIndividual specificationTypeRef = specificationType.getPropertyValue("specificationtyperef");

		XMLIndividual spectypes = reqIfContent.getPropertyValue("spectypes");
		XMLIndividual specificationtype = spectypes.getPropertyValue("specificationtype");

		assertSame(specificationTypeRef, specificationtype);
	}

}
