/**
 * 
 * Copyright (c) 2014-2015, Openflexo
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

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.Normalizer;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.apache.xml.security.Init;
import org.apache.xml.security.c14n.Canonicalizer;
import org.openflexo.foundation.ontology.DuplicateURIException;
import org.openflexo.pamela.annotations.*;
import org.openflexo.technologyadapter.xml.metamodel.XMLComplexType;
import org.openflexo.technologyadapter.xml.metamodel.XMLDataProperty;
import org.openflexo.technologyadapter.xml.metamodel.XMLProperty;
import org.openflexo.technologyadapter.xml.metamodel.XMLType;
import org.openflexo.technologyadapter.xml.model.typed.XMLIndividual;
import org.openflexo.technologyadapter.xml.model.typed.XMLModel;
import org.openflexo.technologyadapter.xml.rm.XSDMetaModelResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/* Correct processing of XML Objects URIs needs to add an internal class to store
 * for each XMLComplexType wich are the XML Elements (attributes or CDATA, or...) that will be 
 * used to calculate URIs
 */

// TODO Manage the fact that URI May Change

@ModelEntity
@XMLElement
@ImplementationClass(XMLURIProcessor.XMLURIProcessorImpl.class)
public interface XMLURIProcessor extends AbstractXMLURIProcessor {

	/**
	 * XMLURIProcessor interface implementation
	 * 
	 * @author xtof
	 *
	 */

	public static abstract class XMLURIProcessorImpl extends AbstractXMLURIProcessorImpl implements XMLURIProcessor {

		// Initialize xmlsec once (canonicalization used for stable hashing)
		static {
			try {
				Init.init();
			} catch (Throwable t) {
				// Keep running; we will fall back to a weaker hash path if canonicalization is unavailable
				System.err.println("WARNING: Cannot initialize Apache Santuario (xmlsec). Canonical hashing will be degraded.");
			}
		}
		static final Logger logger = Logger.getLogger(XMLURIProcessor.class.getPackage().getName());

		// Cache des URis Pour aller plus vite ??
		// TODO some optimization required
		private final Map<String, XMLObject> uriCache = new HashMap<>();
		/** Optional: redirect oldUri -> newUri (in-memory here; persist externally if you wish) */
		private final Map<String, String> redirects = new HashMap<>();

		/** Optional: tombstones oldUri -> reason */
		private final Map<String, String> tombstones = new HashMap<>();

		private JaroWinklerSimilarity jw = new JaroWinklerSimilarity();
		/**
		 * initialises an URIProcessor with the given URI
		 * 
		 * @param typeURI
		 */
		public XMLURIProcessorImpl() {
			super();
		}

		/**
		 * initialises an URIProcessor with the given URI
		 * 
		 * @param typeURI
		 */
		public XMLURIProcessorImpl(String typeURI) {
			super();
			if (typeURI != null) {
				this.typeURI = URI.create(typeURI);
			}
		}

		// Lifecycle management methods
		@Override
		public void reset() {
			setModelSlot(null);
			setMappedXMLType(null);
			setMappingStyle(null);
			setBasePropertyForURI(null);
		}

		@Override
		public void setTypeURI(String name) {
			if (name != null) {
				typeURI = URI.create(name);
				bindtypeURIToMappedType();
			}
			else
				typeURI = null;
		}

		@Override
		public String getTypeURI() {
			if (mappedXMLType != null) {
				String _uri = mappedXMLType.getURI();
				return _uri;
			}
			else if (typeURI != null) {
				return typeURI.toString();
			}

			return null;

		}

		@Override
		public XMLType getMappedXMLType() {
			if (mappedXMLType == null && typeURI != null) {
				bindtypeURIToMappedType();
			}
			return mappedXMLType;
		}

		@Override
		public void setMappedXMLType(XMLType aType) {
			mappedXMLType = aType;
		}

		public void bindtypeURIToMappedType() {
			XMLModelSlot modelSlot = (XMLModelSlot) getModelSlot();
			if (modelSlot != null) {
				String mmURI = modelSlot.getMetaModelURI();
				if (mmURI != null) {
					// FIXME : to be re-factored
					XSDMetaModelResource mmResource = (XSDMetaModelResource) modelSlot.getMetaModelResource();
					if (mmResource != null && typeURI != null) {
						mappedXMLType = mmResource.getMetaModelData().getTypeFromURI(typeURI.toString());
						String attrName = getAttributeName();
						if (getMappingStyle() == MappingStyle.ATTRIBUTE_VALUE && attrName != null) {
							setBasePropertyForURI((XMLDataProperty) ((XMLComplexType) getMappedXMLType()).getPropertyByName(attrName));
						}
					}
					else {
						logger.warning("unable to map typeURI to an OntClass, as metaModelResource or Type URI  is Null ");
					}
				}
				else
					setMappedXMLType(null);
			}
		}

		// URI Calculation
		// TODO : manage the fact that URI might change:
		@Override
		public String getURIForObject(XMLModel model, XMLObject xsO) {
			String builtURI = null;
			StringBuffer completeURIStr = new StringBuffer();

			if (!(xsO instanceof XMLIndividual)) {
				logger.warning("Cannot process URI: object is not an XMLIndividual");
				return null;
			}
			XMLIndividual ind = (XMLIndividual) xsO;

			// if processor not initialized
			if (getMappedXMLType() == null) {
				bindtypeURIToMappedType();
			}
			// processor should be initialized
			if (getMappedXMLType() == null) {
				logger.warning("Cannot process URI as URIProcessor is not initialized for that class: " + typeURI);
				return null;
			}
			//  query parts (id, hash, composite keys)
			List<String> q = new ArrayList<>();

			// when mapping by attribute and attribute present
			String idParam = null;
			String attrName = getAttributeName();
			//identify using a predetermined attribute
			XMLDataProperty keyProp = getBasePropertyForURI();
			//if (getMappingStyle() == MappingStyle.ATTRIBUTE_VALUE && attrName != null && getMappedXMLType() != null) {
			//for a specific attribute
			if (getMappingStyle() == MappingStyle.ATTRIBUTE_VALUE && keyProp != null) {
				//XMLProperty aProperty = ((XMLComplexType) getMappedXMLType()).getPropertyByName(attrName);
				//if (aProperty != null && xsO instanceof XMLIndividual) {
					//Object v = ((XMLIndividual) xsO).getPropertyValue(aProperty);
				Object v = ind.getPropertyValue(keyProp);
				if (v != null) idParam = "id=" + urlEncode(String.valueOf(v));
				//}
				//Object value = ((XMLIndividual) xsO).getPropertyValue(aProperty);
				/*try {
					// NPE protection
					if (value != null) {
						builtURI = URLEncoder.encode(value.toString(), "UTF-8");
					}
					else {
						logger.severe("XSURI: unable to compute an URI for given object");
					}
				} catch (UnsupportedEncodingException e) {
					logger.warning("Cannot process URI - Unexpected encoding error");
					e.printStackTrace();
				}*/
			}

			//Identify using the attribute id
			else if (idParam == null && getMappingStyle() == MappingStyle.SINGLETON) {
				// Uses the type, want the id of the individual
				//idParam = "id=" + urlEncode(((XMLIndividual) xsO).getType().getURI());
				/*try {
					builtURI = URLEncoder.encode(((XMLIndividual) xsO).getType().getURI(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					logger.warning("Cannot process URI - Unexpected encoding error");
					e.printStackTrace();
				}*/
				//uses the individual id
				XMLType type = getMappedXMLType();
				if (type instanceof XMLComplexType) {
					XMLComplexType ct = (XMLComplexType) type;
					XMLProperty idProp = ct.getPropertyByName("id");
					if (idProp instanceof XMLDataProperty) {
						XMLDataProperty idDataProp = (XMLDataProperty) idProp;

						// Read individual id value
						Object idValue = ind.getPropertyValue(idDataProp);
						if (idValue != null) {
							// cache it as basePropertyForURI
							setBasePropertyForURI(idDataProp);
							setAttributeName("id");

							idParam = "id=" + urlEncode(String.valueOf(idValue));
						}
					}
				}
			}
			// add id to query
			if (idParam != null) q.add(idParam);
			/*else {
				logger.warning("Cannot process URI - Unexpected or Unspecified mapping parameters");
			}*/

			// Canonical hash
			String hashParam = buildCanonicalHashParam((XMLIndividual) xsO);


			/*if (builtURI != null) {
				if (uriCache.get(builtURI) == null) {
					// TODO Manage the fact that URI May Change
					uriCache.put(builtURI, xsO);
				}
			}
			completeURIStr.append(typeURI.getScheme()).append("://").append(typeURI.getHost()).append(typeURI.getPath()).append("?")
					.append(builtURI).append("#").append(typeURI.getFragment());
			return completeURIStr.toString();*/
			if (hashParam != null) {
				//if (!q.isEmpty()) q.add("&");
				q.add(hashParam);
			}

			String query = q.isEmpty() ? null : String.join("&", q);

			String full;
			try {
				URI fullURI = new URI(typeURI.getScheme(), typeURI.getPath(),
						(query.isEmpty() ? null : query.toString()), typeURI.getFragment());
				full = fullURI.toString();
			} catch (URISyntaxException e) {
				logger.warning("Cannot process URI - Unexpected or Unspecified mapping parameters");
				return null;
			}

			// cache by FULL URI
			if (full != null) {
				// TODO Manage the fact that URI May Change
				uriCache.putIfAbsent(full, xsO);
			}
			return full;
		}
		private String buildCanonicalHashParam(XMLIndividual xsO) {
			try {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				dbf.setNamespaceAware(true);
				DocumentBuilder builder = dbf.newDocumentBuilder();
				Document doc = builder.newDocument();
                //bug
				Element root = null;
				//= xsO.toXML(doc);
				logger.warning("Root:"+root);
				if (root != null) {
					Canonicalizer c14n = Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
					byte[] canonical;
					try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
						c14n.canonicalizeSubtree( root, baos);
						canonical = baos.toByteArray();
					}
					return "h=sha256:" + sha256hex(canonical);
				}
			} catch (Throwable t) {
				logger.log(Level.WARNING,
						"Cannot process URI - error while building canonical hash", t);
			}
			try {
				String key = safeNormalize(getBaseKeyValueSafe(xsO));
				if (key == null || key.isEmpty()) {
					return null;
				}
				return "h=sha256:" + sha256hex(key.getBytes("UTF-8"));
			} catch (Throwable ignore) {
				return null;
			}
		}

		private String getBaseKeyValueSafe(XMLObject xsO) {
			try {
				String attrName = getAttributeName();
				if (attrName == null || getMappedXMLType() == null) return null;
				XMLDataProperty p = getBasePropertyForURI();
				if (p == null) return null;
				Object v = ((XMLIndividual) xsO).getPropertyValue(p);
				return v != null ? v.toString() : null;
			} catch (Throwable t) {
				return null;
			}
		}


		private static String sha256hex(byte[] data) throws Exception {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] dig = md.digest(data);
			StringBuilder sb = new StringBuilder(dig.length * 2);
			for (byte b : dig) sb.append(String.format("%02x", b));
			return sb.toString();
		}
		// get the Object given the URI

		@Override
		public Object retrieveObjectWithURI(XMLModel model, String objectURI) throws DuplicateURIException {
			// redirect handled first
			if (objectURI != null && redirects.containsKey(objectURI)) {
				String newURI = redirects.get(objectURI);
				logger.info("XMLURI redirect: " + objectURI + " → " + newURI);
				objectURI = newURI;
			}

			// tombstone considered deleted
			if (objectURI != null && tombstones.containsKey(objectURI)) {
				logger.warning("XMLURI TOMBSTONE: " + objectURI + " → " + tombstones.get(objectURI));
				return null;
			}

			// object in cache
			XMLObject o = (objectURI != null) ? uriCache.get(objectURI) : null;
			if (o != null) {
				// cache hit (do NOT warn here; earlier version warned incorrectly)
				return o;
			}


			// if processor not initialized
			if (getMappedXMLType() == null) {
				bindtypeURIToMappedType();
			}
            // retrieve object not found in redirection, tombstone, or cache
			if (objectURI != null) {
				String attrName = getAttributeName();
				Map<String, List<String>> qp = parseQueryMulti(URI.create(objectURI).getQuery());
				String id = first(qp.get("id"));
				String hash = first(qp.get("h"));

				//  try hash match
				if (hash != null && hash.startsWith("sha256:")) {
					XMLIndividual byHash = findByHash(model, hash);
					if (byHash != null) {
						uriCache.put(objectURI, byHash);
						return byHash;
					}
				}

				//  try id using (ATTRIBUTE_VALUE) , must be normalized
				if (getMappingStyle() == MappingStyle.ATTRIBUTE_VALUE && attrName != null && id != null) {
					XMLIndividual byId = findByIdNormalized(model, id);
					if (byId != null) {
						uriCache.put(objectURI, byId);
						return byId;
					}
				}

				// ATTRIBUTE_VALUE path using entire query , normalized
				if (getMappingStyle() == MappingStyle.ATTRIBUTE_VALUE && attrName != null && getMappedXMLType() != null && id == null) {

					XMLProperty aProperty = ((XMLComplexType) getMappedXMLType()).getPropertyByName(attrName);
					String attrValue = URI.create(objectURI).getQuery();

					for (XMLIndividual obj : model.getIndividualsOfType(getMappedXMLType())) {

						Object value = obj.getPropertyValue(aProperty);
						try {
							String decoded = URLDecoder.decode(attrValue, "UTF-8");
							if (value != null && safeNormalize(value.toString()).equals(safeNormalize(decoded))) {
								uriCache.put(objectURI, obj);
								return obj;
							}
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}

				}
				//get the parameter "id" first,
				else if (getMappingStyle() == MappingStyle.SINGLETON) {
					if (id != null) {
						// the attribute defined by getAttributeName()?
						XMLIndividual byId = findByIdNormalized(model, id);
						if (byId != null) {
							uriCache.put(objectURI, byId);
							return byId;
						}

						//fuzzy resolution on the same attribute
						XMLIndividual fuzzy = fuzzyById(model, id);
						if (fuzzy != null) {
							logger.warning("XMLURI fuzzy match (SINGLETON) used for: " + objectURI);
							uriCache.put(objectURI, fuzzy);
							return fuzzy;
						}

						// We had an id, but no matching individual
						logger.warning("Cannot resolve SINGLETON by id=" + id + " for type " + this.getTypeURI());
						return null;
					}

					/*
					List<?> indivList = model.getIndividualsOfType(getMappedXMLType());
					if (indivList.size() > 1) {
						throw new DuplicateURIException(
								"Cannot process URI - Several individuals found for singleton of individual " + );
					}
					else if (indivList.size() == 0) {
						logger.warning("Cannot find Singleton for type : " + this.getTypeURI().toString());
					}
					else {
						o = (XMLObject) indivList.get(0);
						uriCache.put(objectURI, o);
						return o;
					}*/
				}

				// fuzzy fallback on id
				if (id != null) {
					XMLIndividual fuzzy = fuzzyById(model, id);
					if (fuzzy != null) {
						logger.warning("XMLURI fuzzy match used for: " + objectURI);
						uriCache.put(objectURI, fuzzy);
						return fuzzy;
					}
				}
			}


			logger.warning("Cannot process URI - Unexpected or Unspecified mapping parameters");
			return null;
			// should not be a preoccupation of XSURI
			// if (!resource.isLoaded()) {
			// resource.getModelData();
			// }

			// retrieve object
			/*if (o == null) {
				String attrName = getAttributeName();
				if (getMappingStyle() == MappingStyle.ATTRIBUTE_VALUE && attrName != null) {

					XMLProperty aProperty = ((XMLComplexType) getMappedXMLType()).getPropertyByName(attrName);
					String attrValue = URI.create(objectURI).getQuery();

					for (XMLIndividual obj : model.getIndividualsOfType(getMappedXMLType())) {

						Object value = obj.getPropertyValue(aProperty);
						try {
							if (value.equals(URLDecoder.decode(attrValue, "UTF-8"))) {
								return obj;
							}
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}

				}
				else if (getMappingStyle() == MappingStyle.SINGLETON) {
					List<?> indivList = model.getIndividualsOfType(getMappedXMLType());
					if (indivList.size() > 1) {
						throw new DuplicateURIException(
								"Cannot process URI - Several individuals found for singleton of type " + this.getTypeURI().toString());
					}
					else if (indivList.size() == 0) {
						logger.warning("Cannot find Singleton for type : " + this.getTypeURI().toString());
					}
					else {
						o = (XMLObject) indivList.get(0);
					}
				}
			}
			else {
				logger.warning("Cannot process URI - Unexpected or Unspecified mapping parameters");

			}

			return o;*/
		}

		// get the right URIProcessor for URI
		public static String retrieveTypeURI(XMLModel model, String objectURI) {

			URI fullURI;
			StringBuffer typeURIStr = new StringBuffer();

			fullURI = URI.create(objectURI);
			//typeURIStr.append(fullURI.getScheme()).append("://").append(fullURI.getHost()).append(fullURI.getPath()).append("#")
					//.append(fullURI.getFragment());
			typeURIStr.append(fullURI.getScheme()).append("://")
					.append(fullURI.getAuthority() != null ? fullURI.getAuthority() : "")
					.append(fullURI.getPath()).append("#")
					.append(fullURI.getFragment());

			return typeURIStr.toString();
		}
		private static String urlEncode(String s) {
			try { return URLEncoder.encode(s, "UTF-8"); }
			catch (UnsupportedEncodingException e) { return s; }
		}

		private static String urlDecode(String s) {
			try { return s == null ? null : URLDecoder.decode(s, "UTF-8"); }
			catch (UnsupportedEncodingException e) { return s; }
		}


		private Map<String, List<String>> parseQueryMulti(String q) {
			Map<String, List<String>> m = new HashMap<>();
			if (q == null || q.isEmpty()) return m;
			for (String part : q.split("&")) {
				int i = part.indexOf('=');
				String k = i >= 0 ? part.substring(0, i) : part;
				String v = i >= 0 ? part.substring(i + 1) : "";
				try {
					v = URLDecoder.decode(v, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					// keep raw
				}
				m.computeIfAbsent(k, __ -> new ArrayList<>()).add(v);
			}
			return m;
		}

		private String first(List<String> l) {
			return (l == null || l.isEmpty()) ? null : l.get(0);
		}

		private String safeNormalize(String s) {
			if (s == null) return "";
			String t = s.trim().replaceAll("\\s+", " ");
			return Normalizer.normalize(t, Normalizer.Form.NFKC).toLowerCase(Locale.ROOT);
		}

		private XMLIndividual findByHash(XMLModel model, String prefixedHash) {
			try {
				String hex = prefixedHash.startsWith("sha256:") ? prefixedHash.substring("sha256:".length()) : prefixedHash;
				for (XMLIndividual i : model.getIndividualsOfType(getMappedXMLType())) {
					String h = buildCanonicalHashParam(i);
					if (h != null) {
						String candidate = h.startsWith("h=") ? h.substring(2) : h; // strip "h=" if present
						String candHex = candidate.startsWith("sha256:") ? candidate.substring("sha256:".length()) : candidate;
						if (hex.equalsIgnoreCase(candHex)) return i;
					}
				}
			} catch (Throwable t) {
				logger.warning("Cannot process URI - Unexpected encoding error in Find");
			}
			return null;
		}

		private XMLIndividual findByIdNormalized(XMLModel model, String id) {
			if (getMappedXMLType() == null || getAttributeName() == null) return null;
			XMLProperty p = ((XMLComplexType) getMappedXMLType()).getPropertyByName(getAttributeName());
			if (p == null) return null;
			String target = safeNormalize(id);
			for (XMLIndividual i : model.getIndividualsOfType(getMappedXMLType())) {
				Object v = i.getPropertyValue(p);
				if (v != null && safeNormalize(String.valueOf(v)).equals(target)) return i;
			}
			return null;
		}

		private XMLIndividual fuzzyById(XMLModel model, String id) {
			if (getMappedXMLType() == null || getAttributeName() == null || id == null) return null;
			XMLProperty p = ((XMLComplexType) getMappedXMLType()).getPropertyByName(getAttributeName());
			if (p == null) return null;
			String target = safeNormalize(id);
			double best = 0.0; XMLIndividual winner = null;
			for (XMLIndividual i : model.getIndividualsOfType(getMappedXMLType())) {
				Object v = i.getPropertyValue(p);
				if (v == null) continue;
				double s = jw.apply(target, safeNormalize(String.valueOf(v)));
				if (s > best) { best = s; winner = i; }
			}
			// value?
			return (winner != null && best >= 0.88) ? winner : null;
		}


		// TODO Manage redirects and deleted individuals
		public void addRedirect(String oldFullUri, String newFullUri) {
			if (oldFullUri == null || newFullUri == null) return;
			redirects.put(oldFullUri, newFullUri);
			uriCache.remove(oldFullUri);
		}

		public void tombstone(String oldFullUri, String reason) {
			if (oldFullUri == null) return;
			tombstones.put(oldFullUri, reason != null ? reason : "deleted");
			uriCache.remove(oldFullUri);
		}
	}
}
