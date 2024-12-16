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

package org.openflexo.technologyadapter.xml.model.typed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.pamela.annotations.Adder;
import org.openflexo.pamela.annotations.CloningStrategy;
import org.openflexo.pamela.annotations.CloningStrategy.StrategyType;
import org.openflexo.pamela.annotations.Embedded;
import org.openflexo.pamela.annotations.Finder;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.Getter.Cardinality;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.Initializer;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.Parameter;
import org.openflexo.pamela.annotations.PastingPoint;
import org.openflexo.pamela.annotations.Remover;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.technologyadapter.xml.metamodel.XMLComplexType;
import org.openflexo.technologyadapter.xml.metamodel.XMLType;
import org.openflexo.technologyadapter.xml.metamodel.XSDMetaModel;
import org.openflexo.technologyadapter.xml.model.AbstractXMLDocument;
import org.openflexo.technologyadapter.xml.rm.TypedXMLResource;
import org.openflexo.xml.XMLCst;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Represents an XML document conform to an XSD grammar (its metamodel)
 * 
 * @author sylvain,xtof
 */
@ModelEntity
@ImplementationClass(XMLModel.XMLModelImpl.class)
public interface XMLModel extends AbstractXMLDocument<XMLModel>, FlexoModel<XMLModel, XSDMetaModel> {

	/**
	 * Reference to the {@link XMLMetaModel} that this document is conformant to
	 */
	public static final String MM = "metaModel";

	/**
	 * Link to the {@XMLResource} that manages the concrete serialization of this model
	 * 
	 */
	// public static final String RSC = "resource";

	/**
	 * Collection of {@link XMLIndividuals}
	 */
	public static final String IND = "individuals";

	/**
	 * Root individual of the XML Objects graph
	 */
	public static final String ROOT = "root";

	/**
	 * Properties that host uri and Namespace prefix for this Model
	 */
	public static final int NSPREFIX_INDEX = 0;
	public static final int NSURI_INDEX = 1;

	@Initializer
	public XMLModel init();

	@Initializer
	public XMLModel init(@Parameter(MM) XSDMetaModel mm);

	@Override
	public XMLModelFactory getModelFactory();

	@Override
	@Getter(MM)
	XSDMetaModel getMetaModel();

	@Setter(MM)
	void setMetaModel(XSDMetaModel mm);

	/*@Override
	@Getter(RSC)
	public FlexoResource<XMLModel> getResource();
	
	@Override
	@Setter(RSC)
	public void setResource(FlexoResource<XMLModel> resource);*/

	@Getter(ROOT)
	public XMLIndividual getRoot();

	@Setter(ROOT)
	public void setRoot(XMLIndividual indiv);

	@Getter(value = IND, cardinality = Cardinality.LIST, inverse = XMLIndividual.MODEL)
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	public List<? extends XMLIndividual> getIndividuals();

	// TODO ask Syl pourkoi on ne peut pas avoir +eurs adders...
	public XMLIndividual addNewIndividual(XMLComplexType aType);

	@Adder(IND)
	@PastingPoint
	public void addIndividual(XMLIndividual ind);

	@Remover(IND)
	public void removeFromIndividuals(XMLIndividual ind);

	@Finder(attribute = XMLIndividual.TYPE, collection = IND, isMultiValued = true)
	public List<? extends XMLIndividual> getIndividualsOfType(XMLType aType);

	/*
	 * Non-PAMELA-managed properties
	 */
	public List<String> getNamespace();

	public void setNamespace(String ns, String prefix);

	/**
	 * Default implementaion for {@link XMLModel}
	 * 
	 * @author sylvain,xtof
	 */

	public static abstract class XMLModelImpl extends AbstractXMLDocumentImpl<XMLModel> implements XMLModel {

		// Attributes

		protected static final Logger logger = Logger.getLogger(XMLModelImpl.class.getPackage().getName());
		private FlexoResource<?> xmlResource;

		private final Map<String, XMLIndividual> individuals;

		private final List<String> namespace = new ArrayList<>();

		/*private static PamelaModelFactory MF;
		
		static {
			try {
				MF = new PamelaModelFactory(PamelaMetaModelLibrary.retrieveMetaModel(XMLModel.class, XMLIndividual.class,
						XMLPropertyValue.class, XMLDataPropertyValue.class, XMLObjectPropertyValue.class));
			} catch (ModelDefinitionException e) {
				e.printStackTrace();
			}
		}
		
		public static PamelaModelFactory getModelFactory() {
			return MF;
		}*/

		public XMLModelImpl() {
			super();
			individuals = new HashMap<>();
		}

		// Can be safely cast to TypedXMLResource
		@Override
		public TypedXMLResource getResource() {
			return (TypedXMLResource) super.getResource();
		}

		@Override
		public XMLModelFactory getModelFactory() {
			return getResource().getFactory();
		}

		@Override
		public String getName() {
			if (xmlResource != null) {
				return xmlResource.getName();
			}
			else
				return "";
		}

		@Override
		public void setNamespace(String uri, String prefix) {
			namespace.add(XMLModel.NSPREFIX_INDEX, prefix);
			namespace.add(XMLModel.NSURI_INDEX, uri);
		}

		@Override
		public List<String> getNamespace() {

			if (namespace.size() < 2 && this.getMetaModel() != null) {
				namespace.add(XMLModel.NSPREFIX_INDEX, XMLCst.DEFAULT_NS);
				namespace.add(XMLModel.NSURI_INDEX, this.getMetaModel().getURI());
			}

			return namespace;

		}

		/*@Override
		public TypedXMLResource getResource() {
			return (TypedXMLResource) xmlResource;
		}
		
		@Override
		public void setResource(FlexoResource<XMLModel> resource) {
			this.xmlResource = resource;
		}*/

		@Override
		public String getURI() {
			return xmlResource.getURI();
		}

		@Override
		public Object getObject(String objectURI) {
			return null;
		}

		@Override
		public List<? extends XMLIndividual> getIndividuals() {
			return new ArrayList<>(individuals.values());
		}

		// TODO, TO BE OPTIMIZED
		@Override
		public List<XMLIndividual> getIndividualsOfType(XMLType aType) {
			ArrayList<XMLIndividual> returned = new ArrayList<>();
			for (XMLIndividual o : individuals.values()) {
				if (o.getType() == aType) {
					returned.add(o);
				}
			}
			return returned;
		}

		@Override
		public XMLIndividual addNewIndividual(XMLComplexType aType) {
			XMLIndividual anIndividual = getModelFactory().makeXMLIndividual(this, aType);
			addIndividual(anIndividual);
			anIndividual.setContainerModel(this);
			return anIndividual;
		}

		@Override
		public void addIndividual(XMLIndividual anIndividual) {
			individuals.put(anIndividual.getUUID(), anIndividual);
		}

		public Document toXML() throws ParserConfigurationException {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();

			XMLIndividual rootIndiv = getRoot();

			if (rootIndiv != null) {
				Element rootNode = rootIndiv.toXML(doc);
				doc.appendChild(rootNode);
			}

			return doc;
		}

	}

}
