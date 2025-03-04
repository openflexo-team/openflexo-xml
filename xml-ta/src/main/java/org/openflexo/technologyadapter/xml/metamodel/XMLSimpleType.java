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

package org.openflexo.technologyadapter.xml.metamodel;

import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.exceptions.InvalidDataException;
import org.openflexo.pamela.model.StringConverterLibrary;
import org.openflexo.pamela.model.StringConverterLibrary.DateConverter;
import org.openflexo.technologyadapter.xml.model.typed.XMLIndividual;
import org.openflexo.technologyadapter.xml.model.typed.XMLModel;

/**
 * Represents a simple XML type, reflected by a base java type
 */
@ModelEntity
@ImplementationClass(XMLSimpleType.XMLSimpleTypeImpl.class)
public interface XMLSimpleType extends XMLType {

	// static simple Types URI
	public static String STRING_URI = "string"; // xs:string
	public static String BOOLEAN_URI = "boolean";
	public static String BYTE_URI = "byte";
	public static String DATE_URI = "date";
	public static String DECIMAL_URI = "decimal";
	public static String DOUBLE_URI = "double";
	public static String FLOAT_URI = "float";
	public static String INT_URI = "int";
	public static String INTEGER_URI = "integer";
	public static String LONG_URI = "long";
	public static String SHORT_URI = "short";
	public static String ID_URI = "ID";
	public static String ID_REF_URI = "IDREF";
	public static String DATE_TIME_URI = "dateTime";
	public static String ANY_URI = "anyURI";

	public static XMLSchemaPrimitiveType getPrimitiveFromURI(String uri) {
		for (XMLSchemaPrimitiveType primitiveType : XMLSchemaPrimitiveType.values()) {
			if (primitiveType.getFullQualifiedURI().equals(uri)) {
				return primitiveType;
			}
			if (primitiveType.getAbbreviatedURI().equals(uri)) {
				return primitiveType;
			}
		}
		XMLSimpleTypeImpl.logger.warning("Unexpected type " + uri);
		return null;
	}

	public enum XMLSchemaPrimitiveType {

		STRING {
			@Override
			public String getLocalURI() {
				return STRING_URI;
			}

			@Override
			public Type getJavaType() {
				return String.class;
			}

			@Override
			public String valueFromString(String stringValue, XMLModel model) {
				return stringValue;
			}

		},
		BOOLEAN {
			@Override
			public String getLocalURI() {
				return BOOLEAN_URI;
			}

			@Override
			public Type getJavaType() {
				return Boolean.class;
			}

			@Override
			public Boolean valueFromString(String stringValue, XMLModel model) throws InvalidDataException {
				return StringConverterLibrary.getInstance().getConverter(Boolean.class).convertFromString(stringValue, null);
			}
		},
		BYTE {
			@Override
			public String getLocalURI() {
				return BYTE_URI;
			}

			@Override
			public Type getJavaType() {
				return Byte.class;
			}

			@Override
			public Byte valueFromString(String stringValue, XMLModel model) throws InvalidDataException {
				return StringConverterLibrary.getInstance().getConverter(Byte.class).convertFromString(stringValue, null);
			}
		},
		DATE {
			@Override
			public String getLocalURI() {
				return DATE_URI;
			}

			@Override
			public Type getJavaType() {
				return Date.class;
			}

			@Override
			public Date valueFromString(String stringValue, XMLModel model) throws InvalidDataException {
				return StringConverterLibrary.getInstance().getConverter(Date.class).convertFromString(stringValue, null);
			}
		},
		DECIMAL {
			@Override
			public String getLocalURI() {
				return DECIMAL_URI;
			}

			@Override
			public Type getJavaType() {
				return Number.class;
			}

			@Override
			public Number valueFromString(String stringValue, XMLModel model) throws InvalidDataException {
				return StringConverterLibrary.getInstance().getConverter(Number.class).convertFromString(stringValue, null);
			}
		},
		DOUBLE {
			@Override
			public String getLocalURI() {
				return DOUBLE_URI;
			}

			@Override
			public Type getJavaType() {
				return Double.class;
			}

			@Override
			public Double valueFromString(String stringValue, XMLModel model) throws InvalidDataException {
				return StringConverterLibrary.getInstance().getConverter(Double.class).convertFromString(stringValue, null);
			}
		},
		FLOAT {
			@Override
			public String getLocalURI() {
				return FLOAT_URI;
			}

			@Override
			public Type getJavaType() {
				return Float.class;
			}

			@Override
			public Float valueFromString(String stringValue, XMLModel model) throws InvalidDataException {
				return StringConverterLibrary.getInstance().getConverter(Float.class).convertFromString(stringValue, null);
			}
		},
		INT {
			@Override
			public String getLocalURI() {
				return INT_URI;
			}

			@Override
			public Type getJavaType() {
				return Integer.TYPE;
			}

			@Override
			public Integer valueFromString(String stringValue, XMLModel model) throws InvalidDataException {
				return StringConverterLibrary.getInstance().getConverter(Integer.class).convertFromString(stringValue, null);
			}
		},
		INTEGER {
			@Override
			public String getLocalURI() {
				return INTEGER_URI;
			}

			@Override
			public Type getJavaType() {
				return Integer.class;
			}

			@Override
			public Integer valueFromString(String stringValue, XMLModel model) throws InvalidDataException {
				return StringConverterLibrary.getInstance().getConverter(Integer.class).convertFromString(stringValue, null);
			}
		},
		LONG {
			@Override
			public String getLocalURI() {
				return LONG_URI;
			}

			@Override
			public Type getJavaType() {
				return Long.class;
			}

			@Override
			public Long valueFromString(String stringValue, XMLModel model) throws InvalidDataException {
				return StringConverterLibrary.getInstance().getConverter(Long.class).convertFromString(stringValue, null);
			}
		},
		SHORT {
			@Override
			public String getLocalURI() {
				return SHORT_URI;
			}

			@Override
			public Type getJavaType() {
				return Short.class;
			}

			@Override
			public Short valueFromString(String stringValue, XMLModel model) throws InvalidDataException {
				return StringConverterLibrary.getInstance().getConverter(Short.class).convertFromString(stringValue, null);
			}
		},
		ID {
			@Override
			public String getLocalURI() {
				return ID_URI;
			}

			@Override
			public Type getJavaType() {
				return String.class;
			}

			@Override
			public String valueFromString(String stringValue, XMLModel model) throws InvalidDataException {
				return stringValue;
			}
		},
		ID_REF {
			@Override
			public String getLocalURI() {
				return ID_REF_URI;
			}

			@Override
			public Type getJavaType() {
				return String.class;
			}

			@Override
			public XMLIndividual valueFromString(String stringValue, XMLModel model) throws InvalidDataException {
				// Search the XMLIndividual with supplied UUID
				XMLIndividual returned = model.getIndividualWithUUID(stringValue);
				if (returned != null) {
					return returned;
				}
				else {
					XMLSimpleTypeImpl.logger.warning("Cannot find XMLIndividual with UUID " + stringValue);
					return null;
				}
			}
		},
		DATE_TIME {
			@Override
			public String getLocalURI() {
				return DATE_TIME_URI;
			}

			@Override
			public Type getJavaType() {
				return Date.class;
			}

			@Override
			public Date valueFromString(String stringValue, XMLModel model) throws InvalidDataException {

				System.out.println("Hop, je dois convertir : [" + stringValue + "]");
				System.out.println("Je retourne: " + XMLSimpleTypeImpl.dateConverter.convertFromString(stringValue, null));
				// System.exit(-1);

				return XMLSimpleTypeImpl.dateConverter.convertFromString(stringValue, null);
				// return StringConverterLibrary.getInstance().getConverter(Date.class).convertFromString(stringValue, null);
			}

			// 2017-04-25T15:44:26.000+02:00
			// 2025-03-03T10:03:04.491+01:00
		},
		ANY {
			@Override
			public String getLocalURI() {
				return ANY_URI;
			}

			@Override
			public Type getJavaType() {
				return java.net.URI.class;
			}

			@Override
			public Object valueFromString(String stringValue, XMLModel model) throws InvalidDataException {
				try {
					return new java.net.URI(stringValue);
				} catch (URISyntaxException e) {
					throw new InvalidDataException("Invalid URI : " + stringValue);
				}
			}
		};

		public abstract String getLocalURI();

		public String getFullQualifiedURI() {
			return XSDMetaModel.XML_SCHEMA_URI + "#" + getLocalURI();
		}

		public String getAbbreviatedURI() {
			return "xs:" + getLocalURI();
		}

		public abstract Type getJavaType();

		public abstract Object valueFromString(String stringValue, XMLModel model) throws InvalidDataException;
	}

	public final String PRIMITIVE_TYPE = "primitiveType";

	@Getter(PRIMITIVE_TYPE)
	public XMLSchemaPrimitiveType getPrimitiveType();

	@Setter(PRIMITIVE_TYPE)
	public void setPrimitiveType(XMLSchemaPrimitiveType primitiveType);

	public Type getJavaType();

	public static abstract class XMLSimpleTypeImpl extends XMLTypeImpl implements XMLSimpleType {

		private static final Logger logger = Logger.getLogger(XMLSimpleTypeImpl.class.getPackage().getName());

		private static StringConverterLibrary.DateConverter dateConverter = new DateConverter("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

		@Override
		public String getDisplayableDescription() {
			return "Simple XML Type named : " + this.getName();
		}

		@Override
		public String getURI() {
			if (getPrimitiveType() != null) {
				return getPrimitiveType().getFullQualifiedURI();
			}
			return (String) performSuperGetter(URI);
		}

		@Override
		public Type getJavaType() {
			if (getPrimitiveType() != null) {
				return getPrimitiveType().getJavaType();
			}
			logger.warning("Unexpected " + getURI());
			return Object.class;
		}

		@Override
		public String toString() {
			return "[SimpleType: " + getName() + " uri=" + getURI() + "]";
		}

	}

}
