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

package org.openflexo.technologyadapter.xml.fml.reflect;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.openflexo.connie.type.TypeUtils;
import org.openflexo.foundation.fml.FlexoConcept;
import org.openflexo.foundation.fml.FlexoConceptInstanceType;
import org.openflexo.foundation.fml.FlexoProperty;
import org.openflexo.foundation.fml.FlexoRole;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.VirtualModelInstanceType;
import org.openflexo.foundation.fml.md.BasicMetaData;
import org.openflexo.foundation.fml.md.FMLMetaData;
import org.openflexo.foundation.fml.md.SingleMetaData;
import org.openflexo.foundation.fml.rt.FMLExecutionException;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.foundation.fml.visitor.FlexoConceptVisitor;
import org.openflexo.pamela.exceptions.InvalidDataException;
import org.openflexo.pamela.model.StringConverterLibrary;
import org.openflexo.technologyadapter.xml.model.typed.XMLModel;
import org.openflexo.xml.SaxBasedObjectGraphFactory;
import org.openflexo.xml.XMLReaderSAXHandler.ParsedElement;
import org.xml.sax.SAXException;

/**
 * A builder for a {@link XMLModel} (sax-based)
 */
public class FMLXMLModelBuilder
		extends SaxBasedObjectGraphFactory<XMLVirtualModelInstance<?>, XMLFlexoConceptInstance, FlexoConceptInstance> {

	private static final Logger logger = Logger.getLogger(FMLXMLModelBuilder.class.getPackage().getName());

	private XMLVirtualModelInstance<?> model = null;

	private XMLVirtualModelInstanceModelFactory<?> factory;
	private VirtualModel reflectedVM;

	private Map<String, List<FlexoConcept>> conceptsByXMLElementName;

	public FMLXMLModelBuilder(XMLVirtualModelInstanceModelFactory<?> factory, VirtualModel reflectedVM) {
		this.reflectedVM = reflectedVM;
		this.factory = factory;

		conceptsByXMLElementName = new HashMap<>();

		reflectedVM.accept(new FlexoConceptVisitor() {

			@Override
			public void visitVirtualModel(VirtualModel virtualModel) {
				// System.out.println("visitVirtualModel: " + virtualModel);
				registerConcept(virtualModel);
			}

			@Override
			public void visitFlexoConcept(FlexoConcept flexoConcept) {
				// System.out.println("visitFlexoConcept: " + flexoConcept);
				registerConcept(flexoConcept);
			}
		});

	}

	private void registerConcept(FlexoConcept concept) {
		FMLMetaData metaData = concept.getMetaData("XMLElement");
		if (metaData instanceof BasicMetaData) {
			// Basic @XMLElement where XML tag is not specified : use concept name
			List<FlexoConcept> l = conceptsByXMLElementName.get(concept.getName());
			if (l == null) {
				l = new ArrayList<FlexoConcept>();
				conceptsByXMLElementName.put(concept.getName(), l);
			}
			l.add(concept);
		}
		else if (metaData instanceof SingleMetaData) {
			// System.out.println("Found : " + metaData + " of " + metaData.getClass() + " for " + concept);
			String xmlElementName = ((SingleMetaData<String>) metaData).getValue(String.class);
			List<FlexoConcept> l = conceptsByXMLElementName.get(xmlElementName);
			if (l == null) {
				l = new ArrayList<FlexoConcept>();
				conceptsByXMLElementName.put(xmlElementName, l);
			}
			l.add(concept);
		}
	}

	@Override
	public XMLFlexoConceptInstance createInstance(Type aType, String name,
			ParsedElement<XMLFlexoConceptInstance, FlexoConceptInstance> parsed) {

		if (aType instanceof VirtualModelInstanceType) {
			// This is the root element
			model.setVirtualModel(((VirtualModelInstanceType) aType).getVirtualModel());
		}
		else if (aType instanceof FlexoConceptInstanceType) {
			try {
				return (XMLFlexoConceptInstance) factory.makeNewFlexoConceptInstance(((FlexoConceptInstanceType) aType).getFlexoConcept(),
						parsed, model, model, null);
			} catch (FMLExecutionException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	@Override
	public Type getTypeForObject(String typeURI, FlexoConceptInstance container, String objectName) {
		// System.out.println("getTypeForObject() ??? " + typeURI + " container: " + container + " objectName=" + objectName);

		List<FlexoConcept> matchingConcepts = conceptsByXMLElementName.get(typeURI);

		if (matchingConcepts == null) {
			// logger.warning("Cannot find concept matching " + typeURI);
			return null;
		}
		else if (matchingConcepts.size() == 1) {
			return matchingConcepts.get(0).getInstanceType();
		}
		else {
			logger.warning("Not implemented : multiple concept with same XML tag " + typeURI);
			return null;
			// TODO : remove ambiguity with container
		}
	}

	@Override
	public Object deserialize(String input) throws IOException {
		if (model != null) {

			try {
				saxParser.parse(input, handler);
			} catch (SAXException e) {
				LOGGER.warning("Cannot parse document: " + e.getMessage());
				throw new IOException(e.getMessage());
			}
			return this.model;
		}
		LOGGER.warning("Context is not set for parsing, aborting");
		return null;
	}

	@Override
	public Object deserialize(InputStream input) throws IOException {
		if (model != null) {

			try {
				saxParser.parse(input, handler);
			} catch (SAXException e) {
				LOGGER.warning("Cannot parse document: " + e.getMessage());
				throw new IOException(e.getMessage());
			}
			return this.model;

		}
		LOGGER.warning("Context is not set for parsing, aborting");
		return null;
	}

	@Override
	public void addToRootNodes(XMLFlexoConceptInstance anObject) {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("unchecked")
	@Override
	public void setModelProperty(String propertyName, Object value) {
		/*if (propertyName.equals(XMLReaderSAXHandler.NAMESPACE_Property)) {
			model.setNamespace(((List<String>) value).get(0), ((List<String>) value).get(1));
		}*/

	}

	@Override
	public void setModelContext(XMLVirtualModelInstance<?> objectGraph) {
		super.setModelContext(objectGraph);
		model = objectGraph;

	}

	@Override
	public void resetModelContext() {
		model = null;
	}

	@Override
	public boolean modelHasPropertyNamed(String propertyName) {
		return getFlexoProperty(model, propertyName) != null;
	}

	@Override
	public String getPropertyName(FlexoConceptInstance object, String elementName) {

		FlexoProperty<?> property = getFlexoProperty(object, elementName);

		if (property != null) {
			return property.getName();
		}

		return null;
	}

	@Override
	public boolean objectHasPropertyNamed(FlexoConceptInstance object, String propertyName) {

		if (object == null) {
			// This is a root element : consider the model itself
			object = model;
		}

		FlexoProperty<?> property = getFlexoProperty(object, propertyName);
		if (property != null) {
			return true;
		}

		return false;
	}

	private FlexoProperty<?> getFlexoProperty(FlexoConceptInstance object, String propertyName) {
		if (object == null) {
			return null;
		}

		FlexoConcept concept = object.getFlexoConcept();

		if (concept == null) {
			return null;
		}

		for (FlexoProperty<?> p : concept.getAccessibleProperties()) {
			FMLMetaData metaData = p.getMetaData("XMLElement");
			// System.err.println("Property: " + p + " metaData=" + metaData);
			if (metaData instanceof BasicMetaData) {
				// Basic @XMLElement where XML tag is not specified : use property name
				if (propertyName.equals(p.getName())) {
					return p;
				}
				if (p.getType() instanceof FlexoConceptInstanceType) {
					FlexoConcept targetType = ((FlexoConceptInstanceType) p.getType()).getFlexoConcept();
					FMLMetaData conceptMetaData = targetType.getMetaData("XMLElement");
					if (conceptMetaData instanceof BasicMetaData) {
						if (propertyName.equals(targetType.getName())) {
							return p;
						}
					}
					else if (conceptMetaData instanceof SingleMetaData) {
						if (propertyName.equals(((SingleMetaData<String>) conceptMetaData).getValue(String.class))) {
							return p;
						}
					}
				}
			}
			else if (metaData instanceof SingleMetaData) {
				String xmlElementName = ((SingleMetaData<String>) metaData).getValue(String.class);
				if (propertyName.equals(xmlElementName)) {
					return p;
				}
			}
		}
		return null;
	}

	@Override
	public void addPropertyValueForObject(FlexoConceptInstance object, String propertyName, Object value) {
		FlexoProperty<?> property = getFlexoProperty(object, propertyName);
		if (property != null) {
			addPropertyValue(object, property, value);
		}
		else {
			logger.warning("Cannot find property " + propertyName + " for " + object);
		}
	}

	@Override
	public void addPropertyValueForModel(String propertyName, Object value) {
		FlexoProperty<?> property = getFlexoProperty(model, propertyName);
		if (property != null) {
			addPropertyValue(model, property, value);
		}
		else {
			logger.warning("Cannot find property " + propertyName + " for " + model);
		}
	}

	public <T> void addPropertyValue(FlexoConceptInstance object, FlexoProperty<T> property, Object value) {
		if (property != null) {
			// System.err.println(
			// "addPropertyValue for " + object + " will set " + property + " with " + value + " type=" + property.getType());

			Class<?> typeClass = TypeUtils.getBaseClass(property.getType());

			if (StringConverterLibrary.getInstance().hasConverter(typeClass)) {
				try {
					T val = (T) StringConverterLibrary.getInstance().getConverter(typeClass).convertFromString((String) value, null);
					object.setFlexoPropertyValue(property, val);
				} catch (InvalidDataException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public <T> void addPropertyObject(FlexoConceptInstance object, String propertyName, FlexoConceptInstance value) {
		FlexoRole property = object.getFlexoConcept().getAccessibleRole(propertyName);
		if (property != null) {
			object.addToFlexoActors(value, property);
		}
	}

	@Override
	public void addChildToObject(XMLFlexoConceptInstance child, FlexoConceptInstance container) {
		if (container != null && container != model) {
			container.addToEmbeddedFlexoConceptInstances(child);
		}

	}

	@Override
	public Type getTypeForProperty(FlexoConceptInstance currentContainer, String localName) {
		if (currentContainer == null) {
			// This is a root element : consider the model itself
			currentContainer = model;
		}

		FlexoProperty<?> property = getFlexoProperty(currentContainer, localName);
		if (property != null) {
			return property.getType();
		}
		return null;

	}
}
