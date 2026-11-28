# openflexo-xml — Known defects

Defects of openflexo-xml that are known and not fixed yet: symptom, what is verified, how to reproduce. Identifiers are stable and never
reused: `XML-D-<n>`. Fixed defects go to `FIXED_DEFECTS.md`.

Status legend: `TODO` · `IN PROGRESS` · `BLOCKED` · `DONE` · `DEFERRED`.

---

## Edition actions

### XML-D-2 — `AddXMLIndividual` with a model slot always fails its URI check  ·  `TODO`

**GitHub:** [openflexo-team/openflexo-xml#3](https://github.com/openflexo-team/openflexo-xml/issues/3)

**Symptom.** An `AddXMLIndividual` run with an XML model slot never gives an individual. Before `CORE-F-11` phase 6 it logged
"Error while creating Individual of type …", removed the individual and returned null; since `XML-F-1` it raises an `FMLTechnologyError`
"Cannot compute the URI of the new individual" (or "Duplicate URI").

**What is verified (2026-11-16, probe of the review of `XML-F-1`, run once).** `XMLModel.addNewIndividual` creates an individual with no
parent, which is not the root: `XMLModelSlot.defaultURIForIndividual` gives null for it, so the positional URI cannot be computed.

**Read, not run.** With a URI processor:
- ATTRIBUTE_VALUE style: the new individual is already registered in the model, so `XMLURIProcessor.retrieveObjectWithURI` finds it and the
  action reports a duplicate of itself; an existing individual of the same type without the attribute makes `value.equals` throw a
  `NullPointerException`.
- SINGLETON style is inverted: the first creation finds itself (a list of one) and reports a duplicate; the second finds two, throws a
  `DuplicateURIException` that `XMLModelSlot.retrieveObjectWithURI` logs and turns into null.

`AddXMLIndividual` has no container attribute: the action does not say where the individual goes in the tree, so the positional URI
cannot be computed before it is placed. Also: `typeURI` has no `@FMLAttribute`, so the action cannot be written in textual FML (its type is
resolved through the inferred model slot).

**To fix.** Decide where the action places the individual (a container), compute the URI after placing it, and exclude the individual
itself from the duplicate lookup; fix SINGLETON. Recorded, not fixed (decided with the user).

**Workaround.** None through the action; create the element through the XML model API from Java.

## Saving

### XML-D-4 — A typed XML document cannot be saved  ·  `TODO`

**Symptom.** Saving a typed XML document (an XML file conform to an XSD) writes nothing: the file is left as it was and a warning says
"Typed XML documents (conform to an XSD) cannot be saved yet"; the resource is then marked saved, so the changes made in Openflexo are lost
when it is unloaded. No exception is raised, so that saving a project goes on with its other resources. Same for an XSD metamodel (changed
by `AddXMLType`, say): Openflexo has no XSD writer. Until `XML-D-3` both saves silently did nothing.

**What is verified (2026-11-27, `TestXMLRoundTrip.testTypedDocumentIsNotWritten`, xml-ta-test).** The unchanged file after a save.
Before this behaviour was put in, the writer was run once on `example_library_1.xml`: `XMLWriter.writeAttributes` threw a
`ClassCastException` (an `XMLIndividual` cast to `List`).

**Cause (read, not run beyond that first failure).** `XMLWriter` predates the current typed model:
- it reads every property value as a `List`, while the model keeps single values and lists apart (`setPropertyValue` / `addPropertyValue`);
- a complex sub-element is held twice by the model (as a child, `addChild`, and as the value of an object property): the writer walks both,
  so it would write it twice;
- the properties of a type are kept in a `HashMap`: the order of the elements required by the XSD (`xs:sequence`), and the position of the
  text elements among the children, are lost, so a written document may not conform to its XSD;
- namespaces: only the one of the root is written.

**To fix.** A new typed writer: keep the XSD order of the properties in the metamodel (`XSDMetaModelResource.loadTypes`), write each
sub-element once, in that order, with its namespace; then replace the refusal test by a round trip (create, change, save, unload, reload,
compare). Deferred to later work (decided with the user, 2026-11-27). An explicit refusal (a `SaveResourceException`) was tried and
dropped: `FlexoProjectImpl._saveModifiedResources` (openflexo-core) saves the modified resources in a loop without catch, so the first
refused document stopped the save of the project (its instances and VirtualModels were not saved).

**Workaround.** None in Openflexo; edit the file outside and reload it.

### XML-D-6 — Saving a free XML document loses what the free model does not keep  ·  `TODO`

**Symptom.** A free XML document (read without XSD) is saved with the local name, the attributes, the text and the children of each
element only. Lost or changed on save:
- the prefixes and namespace declarations, the comments, the processing instructions and the CDATA sections;
- prefixed attributes are written by their local name (`xml:lang` becomes `lang`), the `xsi:*` attributes of the root are dropped, and two
  attributes with the same local name merge;
- the text is trimmed by the reader;
- mixed content is reordered: the text of an element is written before its children.

**What is verified (2026-11-27, reading the code; reviewed).** `XMLReaderSAXHandler` (openflexo-utils) gives the free builder the local
name of each element and attribute, and `FreeXMLDocumentBuilder` keeps no namespace, comment, processing instruction, nor the position of
the text among the children: they are not in the model, so `FreeXMLResource.write` cannot write them. A document without them makes the
round trip (`TestXMLRoundTrip.testFreeDocument`). Each save also leaves a `<name>.xml~` copy of the previous file next to it (the platform's
`makeLocalCopy`).

**To fix.** Keep the qualified names, the namespace declarations and the order of text and children in `XMLElement` (and the comments, if
wanted), and write them back.

**Workaround.** Do not save, from Openflexo, a free document that uses namespaces, comments or mixed content.

## Tests

### XML-D-7 — `TestEditXMLModel` fails: its metamodel has no resource  ·  `TODO`

**Symptom.** `TestEditXMLModel.test0createXMLModel` (xml-ta-test), `@Ignore`d as "Not yet supported" until 2026-11-27, fails when run:
`NullPointerException` in `XMLObjectImpl.getModelFactory` (`XMLObject.java:116`), called by `XSDMetaModelImpl.getModelFactory`.

**What is verified (2026-11-27, run once).** The test builds an `XSDMetaModel` and an `XMLModel` with bare `PamelaModelFactory`s, outside any
resource; the model factory of an XML object is the one of its resource, so it is null.

**To fix.** Rewrite the test on resources (create the metamodel and the model through their resource factories, in a resource center of its
own), or drop it: building a typed model from Java is covered by nothing else. Its only output is a dump to stdout. Kept `@Ignore` naming
this entry.

