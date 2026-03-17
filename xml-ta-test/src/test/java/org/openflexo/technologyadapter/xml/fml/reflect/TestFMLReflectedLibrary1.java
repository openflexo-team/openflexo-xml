/**
 * 
 * Copyright (c) 2014-2015, Openflexo
 * 
 * This file is part of Flexodiagram, a component of the software infrastructure 
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

package org.openflexo.technologyadapter.xml.fml.reflect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.fml.CreationScheme;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.VirtualModelLibrary;
import org.openflexo.foundation.fml.rt.FMLRTVirtualModelInstance;
import org.openflexo.foundation.fml.rt.action.CreateBasicVirtualModelInstance;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.test.OpenflexoProjectAtRunTimeTestCase;
import org.openflexo.technologyadapter.xml.XMLTechnologyAdapter;
import org.openflexo.technologyadapter.xml.rm.FreeXMLResource;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;
import org.openflexo.test.UITest;

/**
 * Test the creation and some manipulations of a {@link VirtualModel} with {@link FMLControlledDocXVirtualModelNature}<br>
 * We basically test here the generation of a plain document from a template, and the adding of a fragment at the end of the document
 * 
 * @author sylvain
 * 
 */
@RunWith(OrderedRunner.class)
public class TestFMLReflectedLibrary1 extends OpenflexoProjectAtRunTimeTestCase {

	private static FlexoEditor editor;
	private static FlexoProject<File> project;

	private static VirtualModel virtualModel;

	private static FMLRTVirtualModelInstance vmi;
	private static FreeXMLResource libraryXMLResource;

	/**
	 * Retrieve the ViewPoint
	 * 
	 * @throws FlexoException
	 * @throws ResourceLoadingCancelledException
	 * @throws FileNotFoundException
	 */
	@Test
	@TestOrder(1)
	@Category(UITest.class)
	public void testLoadViewPoint() throws FileNotFoundException, ResourceLoadingCancelledException, FlexoException {
		instanciateTestServiceManager(XMLTechnologyAdapter.class);

		// XMLTechnologyAdapter xmlTA = serviceManager.getTechnologyAdapterService().getTechnologyAdapter(XMLTechnologyAdapter.class);
		// serviceManager.activateTechnologyAdapter(xmlTA, true);

		FlexoResourceCenter<?> resourceCenter = serviceManager.getResourceCenterService()
				.getFlexoResourceCenter("http://openflexo.org/xml-test");

		/*for (FlexoResource<?> flexoResource : serviceManager.getResourceManager().getRegisteredResources()) {
			System.out.println("> " + flexoResource + " loaded: " + flexoResource.isLoaded() + " uri=" + flexoResource.getURI());
		}*/

		/*CompilationUnitResource cur = (CompilationUnitResource) serviceManager.getResourceManager()
				.getResource("http://openflexo.org/xml-test/FML/reflect/TestFMLReflect.fml");
		System.out.println("Hop: " + cur);*/

		VirtualModelLibrary vpLib = serviceManager.getVirtualModelLibrary();
		assertNotNull(vpLib);
		virtualModel = vpLib.getVirtualModel("http://openflexo.org/xml-test/FML/reflect/TestFMLReflect1.fml");

		assertNotNull(virtualModel);

		System.out.println(virtualModel.getCompilationUnit().getFMLPrettyPrint());

		assertCompilationUnitIsValid(virtualModel.getCompilationUnit());

		libraryXMLResource = (FreeXMLResource) serviceManager.getResourceManager()
				.getResource("http://openflexo.org/xml-test/TestResourceCenter/XML/Library1.xml");
		assertNotNull(libraryXMLResource);

		System.out.println(virtualModel.getCompilationUnit().getNormalizedFML());

	}

	@Test
	@TestOrder(2)
	@Category(UITest.class)
	public void testCreateProject() {
		editor = createStandaloneProject("TestProject");
		project = (FlexoProject<File>) editor.getProject();
		System.out.println("Created project " + project.getProjectDirectory());
		assertTrue(project.getProjectDirectory().exists());
	}

	@Test
	@TestOrder(3)
	@Category(UITest.class)
	public void testCreateInstance() throws FileNotFoundException, ResourceLoadingCancelledException, FlexoException {

		System.err.println("testCreateInstance()");
		CreateBasicVirtualModelInstance action = CreateBasicVirtualModelInstance.actionType
				.makeNewAction(project.getVirtualModelInstanceRepository().getRootFolder(), null, editor);
		action.setNewVirtualModelInstanceName("TestFMLReflectedLibrary");
		action.setNewVirtualModelInstanceTitle("TestFMLReflectedLibrary");
		action.setVirtualModel(virtualModel);

		CreationScheme creationScheme = virtualModel.getCreationSchemes().get(0);
		action.setCreationScheme(creationScheme);

		action.setParameterValue(creationScheme.getParameters().get(0), libraryXMLResource);
		action.doAction();
		assertTrue(action.hasActionExecutionSucceeded());
		vmi = action.getNewVirtualModelInstance();
		assertNotNull(vmi);
		assertNotNull(vmi.getResource());

		System.err.println("Library: " + vmi + " in " + vmi.getResource().getIODelegate());

		XMLVirtualModelInstance<?> library = vmi.getFlexoPropertyValue("library");
		assertNotNull(library);
		assertEquals("A Library", library.getFlexoPropertyValue("name"));
		assertSame(library.getVirtualModel(),
				serviceManager.getVirtualModelLibrary().getVirtualModel("http://openflexo.org/xml-test/FML/reflect/Library1"));

		assertEquals(2, library.getFlexoConceptInstances().size());
		XMLFlexoConceptInstance book1 = (XMLFlexoConceptInstance) library.getFlexoConceptInstances().get(0);
		assertNotNull(book1);
		assertEquals("Book", book1.getFlexoConcept().getName());
		assertEquals("toto", book1.getFlexoPropertyValue("title"));
		assertEquals(35, (int) book1.getFlexoPropertyValue("pages"));
		assertEquals("Mystery", book1.getFlexoPropertyValue("category"));
		assertEquals("Le Lapin", book1.getFlexoPropertyValue("author"));

		XMLFlexoConceptInstance book2 = (XMLFlexoConceptInstance) library.getFlexoConceptInstances().get(1);
		assertNotNull(book2);

		assertEquals(2, ((List) library.getFlexoPropertyValue("books")).size());

	}

	/*@Test
	@TestOrder(4)
	@Category(UITest.class)
	public void testListAllOWLClasses() throws TypeMismatchException, NullReferenceException, ReflectiveOperationException,
			InvalidBindingException, FileNotFoundException, ResourceLoadingCancelledException, FlexoException {
		System.err.println("testListAllOWLClasses()");
		List<OWLClass> allClasses = vmi.execute("this.listAllOWLClasses()");
		assertEquals(30, allClasses.size());
	}
	
	@Test
	@TestOrder(5)
	@Category(UITest.class)
	public void testListDeclaredOWLClasses() throws TypeMismatchException, NullReferenceException, ReflectiveOperationException,
			InvalidBindingException, FileNotFoundException, ResourceLoadingCancelledException, FlexoException {
		System.err.println("listDeclaredOWLClasses()");
		List<OWLClass> allClasses = vmi.execute("this.listDeclaredOWLClasses()");
		assertEquals(4, allClasses.size());
	}
	
	@Test
	@TestOrder(6)
	@Category(UITest.class)
	public void testListDeclaredOWLClasses2() throws TypeMismatchException, NullReferenceException, ReflectiveOperationException,
			InvalidBindingException, FileNotFoundException, ResourceLoadingCancelledException, FlexoException {
		System.err.println("listDeclaredOWLClasses2()");
		List<OWLClass> allClasses = vmi.execute("this.listDeclaredOWLClasses2()");
		assertEquals(4, allClasses.size());
	}
	
	@Test
	@TestOrder(7)
	@Category(UITest.class)
	public void testSelectUniqueOWLClass() throws TypeMismatchException, NullReferenceException, ReflectiveOperationException,
			InvalidBindingException, FileNotFoundException, ResourceLoadingCancelledException, FlexoException {
		System.err.println("selectUniqueOWLClass()");
		OWLClass theCat = vmi.execute("this.selectUniqueOWLClass()");
		System.err.println("theCat=" + theCat);
		assertNotNull(theCat);
	}
	
	@Test
	@TestOrder(8)
	@Category(UITest.class)
	public void testListAllOWLIndividuals() throws TypeMismatchException, NullReferenceException, ReflectiveOperationException,
			InvalidBindingException, FileNotFoundException, ResourceLoadingCancelledException, FlexoException {
		System.err.println("listAllOWLIndividuals()");
		List<OWLIndividual> allIndividuals = vmi.execute("this.listAllOWLIndividuals()");
		assertEquals(3, allIndividuals.size());
	}
	
	@Test
	@TestOrder(9)
	@Category(UITest.class)
	public void testListAllOWLIndividuals2() throws TypeMismatchException, NullReferenceException, ReflectiveOperationException,
			InvalidBindingException, FileNotFoundException, ResourceLoadingCancelledException, FlexoException {
		System.err.println("listAllOWLIndividuals2()");
		List<OWLIndividual> allIndividuals = vmi.execute("this.listAllOWLIndividuals2()");
		assertEquals(3, allIndividuals.size());
	}
	
	@Test
	@TestOrder(10)
	@Category(UITest.class)
	public void testSelectUniqueOWLIndividual() throws TypeMismatchException, NullReferenceException, ReflectiveOperationException,
			InvalidBindingException, FileNotFoundException, ResourceLoadingCancelledException, FlexoException {
		System.err.println("selectUniqueOWLIndividual()");
		OWLIndividual jerry = vmi.execute("this.selectUniqueOWLIndividual()");
		System.err.println("jerry=" + jerry);
		assertNotNull(jerry);
	}
	
	@Test
	@TestOrder(11)
	@Category(UITest.class)
	public void testListAllCats() throws TypeMismatchException, NullReferenceException, ReflectiveOperationException,
			InvalidBindingException, FileNotFoundException, ResourceLoadingCancelledException, FlexoException {
		System.err.println("listAllCats()");
		List<OWLIndividual> allCats = vmi.execute("this.listAllCats()");
		assertEquals(2, allCats.size());
	}
	
	@Test
	@TestOrder(12)
	@Category(UITest.class)
	public void testListAllCats2() throws TypeMismatchException, NullReferenceException, ReflectiveOperationException,
			InvalidBindingException, FileNotFoundException, ResourceLoadingCancelledException, FlexoException {
		System.err.println("listAllCats2()");
		List<OWLIndividual> allCats = vmi.execute("this.listAllCats2()");
		assertEquals(2, allCats.size());
	}*/

}
