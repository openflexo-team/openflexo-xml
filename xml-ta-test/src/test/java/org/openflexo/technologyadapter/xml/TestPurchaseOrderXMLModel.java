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
import java.util.logging.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.test.OpenflexoProjectAtRunTimeTestCase;
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
 * Perform some tests in the context of {@link XSDModel} management (an XML file conform to a XSD)
 */
@RunWith(OrderedRunner.class)
public class TestPurchaseOrderXMLModel extends OpenflexoProjectAtRunTimeTestCase {

	protected static final Logger logger = Logger.getLogger(TestPurchaseOrderXMLModel.class.getPackage().getName());

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

	@Test
	@TestOrder(3)
	public void testPO1() throws FileNotFoundException, ResourceLoadingCancelledException, FlexoException {

		log("testPO1()");

		XSDMetaModelResource poXSDResource = mmRepository.getResource("http://tempuri.org/PurchaseOrderSchema.xsd");
		assertNotNull(poXSDResource);
		assertFalse(poXSDResource.isLoaded());

		TypedXMLResource library1Resource = modelRepository.getResource(baseUrl + "/TestResourceCenter/XML/PurchaseOrder/po1.xml");
		assertNotNull(library1Resource);
		assertFalse(library1Resource.isLoaded());

		assertSame(poXSDResource, library1Resource.getMetaModelResource());
		assertTrue(library1Resource.getDependencies().contains(poXSDResource));

		library1Resource.loadResourceData();
		assertTrue(library1Resource.isLoaded());
		assertTrue(poXSDResource.isLoaded());

		XSDMetaModel metaModel = poXSDResource.getMetaModelData();

		Helpers.dumpTypes(metaModel);

		XMLComplexType addressType = metaModel.getComplexTypeFromURI("http://tempuri.org/PurchaseOrderSchema.xsd#USAddress");
		assertNotNull(addressType);

		XMLComplexType poType = metaModel.getComplexTypeFromURI("http://tempuri.org/PurchaseOrderSchema.xsd#PurchaseOrderType");
		assertNotNull(poType);

		Helpers.dumpIndividual(library1Resource.getModelData().getRoot(), "");

		XMLIndividual po = library1Resource.getModelData().getRoot();

		/*System.out.println("Hop le root: " + po);
		System.out.println("po.getType()=" + po.getType());
		System.out.println("poType=" + poType);
		System.exit(-1);*/

		assertSame(poType, po.getType());

		XMLIndividual shippingAddress = po.getChildren().get(0);
		assertSame(addressType, shippingAddress.getType());
		XMLIndividual billingAddress = po.getChildren().get(1);
		assertSame(addressType, billingAddress.getType());

		// assertEquals(billingAddress, ((XMLObjectPropertyValue) po.getPropertyValue("billTo")).getValues().get(0));
		assertEquals(billingAddress, po.getPropertyValues("billTo").get(0));
		assertEquals(shippingAddress, po.getPropertyValues("shipTos").get(0));
		// assertEquals(shippingAddress, po.getPropertyValue("shipTos"));

	}

}
