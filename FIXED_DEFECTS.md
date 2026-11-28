# openflexo-xml — Fixed defects

Defects of openflexo-xml that are fixed, with what was verified, the cause and the fix. Identifiers are stable and never reused:
`XML-D-<n>`. Known defects not fixed yet go to `KNOWN_DEFECTS.md` (created with its first entry).

Status legend: `TODO` · `IN PROGRESS` · `BLOCKED` · `DONE` · `DEFERRED`.

---

### XML-D-1 — Deleting an instance that plays an XML role throws a `NullPointerException`  ·  `DONE`

**GitHub:** [openflexo-team/openflexo-xml#4](https://github.com/openflexo-team/openflexo-xml/issues/4)

**Symptom (verified 2026-11-14 by execution).** In openflexo-integration-tests, `ExternalWorkbookEditTest.externalEditIsPickedUpByRefresh`
(digital-twin-demo): `delete eq` in `ProductionLine.refresh()` failed with an NPE in `XMLActorReference.getObjectURI()` ("msInstance is null").
PAMELA reads that getter when it collects the embedded objects of the instance being deleted, after its model slot instance is gone, and
`getObjectURI()` did not check it (unlike `setModellingElement`). Until `CORE-F-11` (openflexo-core), `DeleteAction` printed and swallowed the
NPE, so the test was green and the failure invisible.

**Fixed (2026-11-14).** Without a model slot instance, `getObjectURI()` returns the URI last computed. The same code was fixed in
openflexo-pptx (`PPTX-D-6`).

## Saving

### XML-D-3 — Saving an XML resource silently does nothing  ·  `DONE`

**Symptom (verified 2026-11-27 by `TestXMLRoundTrip`, xml-ta-test).** `save()` of a free or a typed XML resource returned without error and
left the file unchanged.

**Cause.** `PamelaResourceImpl.saveResourceData` calls `performSave(boolean)`, which no class of the adapter implemented. The writing code of
`TypedXMLResource` sat in `_saveResourceData`, a method nothing calls any more (left from an older API of the core); `FreeXMLResource` had
none.

**Fixed (2026-11-27).** `XMLResourceImpl.performSave` writes through a temporary file renamed at the end (or the output stream of a non-file
delegate) and raises `SaveResourceException` on failure. A free document is written back (`FreeXMLResource.write`, the order of the
attributes kept); its limits are `XML-D-6`. A document without root element (a new, empty one) is not written, as a file without root
could not be loaded again. A typed document and an XSD metamodel are not written either, but a warning now says so (`XML-D-4`). The
temporary file of the former typed save was named `.pdf` (copied from another adapter); it is `.xml`.

### XML-D-5 — Changing a property of a typed XML individual does not mark the resource modified  ·  `DONE`

**Symptom (verified 2026-11-27 by `TestXMLRoundTrip`).** After `XMLIndividual.setPropertyValue`, `isModified()` of the resource stayed false,
so the change was not offered for saving.

**Cause.** The values of the properties are kept in plain maps of `XMLIndividualImpl`, outside PAMELA, which does not see them change.

**Fixed (2026-11-27).** `setPropertyValue`, `addPropertyValue` and `removePropertyValue` mark the model modified; loading clears the flag at
its end, so a loaded document is not modified. The same was done for the attributes of a free element (`XMLElement.setAttributeValue`),
also kept outside PAMELA. Since a typed document cannot be saved yet (`XML-D-4`), the flag only makes its save warn.

