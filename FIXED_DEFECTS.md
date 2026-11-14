# openflexo-xml — Fixed defects

Defects of openflexo-xml that are fixed, with what was verified, the cause and the fix. Identifiers are stable and never reused:
`XML-D-<n>`. Known defects not fixed yet go to `KNOWN_DEFECTS.md` (created with its first entry).

Status legend: `TODO` · `IN PROGRESS` · `BLOCKED` · `DONE` · `DEFERRED`.

---

### XML-D-1 — Deleting an instance that plays an XML role throws a `NullPointerException`  ·  `DONE`

**Symptom (verified 2026-11-14 by execution).** In openflexo-integration-tests, `ExternalWorkbookEditTest.externalEditIsPickedUpByRefresh`
(digital-twin-demo): `delete eq` in `ProductionLine.refresh()` failed with an NPE in `XMLActorReference.getObjectURI()` ("msInstance is null").
PAMELA reads that getter when it collects the embedded objects of the instance being deleted, after its model slot instance is gone, and
`getObjectURI()` did not check it (unlike `setModellingElement`). Until `CORE-F-11` (openflexo-core), `DeleteAction` printed and swallowed the
NPE, so the test was green and the failure invisible.

**Fixed (2026-11-14).** Without a model slot instance, `getObjectURI()` returns the URI last computed. The same code was fixed in
openflexo-pptx (`PPTX-D-6`).
