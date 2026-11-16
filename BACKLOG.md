# openflexo-xml — Backlog

Features and evolutions to develop in the XML technology adapter. Each entry says what problem it solves and when it is done.
Bugs that are known and not fixed go to `KNOWN_DEFECTS.md`, not here.

Identifiers are stable and never reused: `XML-F-<n>`. Reference them from commits and from other entries.

Status legend: `TODO` · `IN PROGRESS` · `BLOCKED` · `DONE` · `DEFERRED`.

---

## FML runtime errors

### XML-F-1 — The XML adapter raises typed FML errors instead of returning null after a warning or a printed stack  ·  `DONE`

**Problem.** Part of `CORE-F-11` phase 6 (`openflexo-core/BACKLOG.md`): the edition actions of the XML adapter caught the failures of
their evaluations four at a time, printed the stack and went on; a missing metamodel, a value that is not an individual, a URI that cannot
be computed gave a warning and null; a duplicate URI was a printed `DuplicateURIException` and null. `AddXMLIndividual` without a model
slot added the individual but returned null. A select from a null model gave an empty result. The builder of reflected instances swallowed
a failure that cannot happen.

**Done (decided with the user, 2026-11-16).** Scope: the paths that FML execution goes through.
- Edition actions (`AddXMLIndividual`, `AddXMLType`, `SetXMLDocumentRoot`, `GetXMLDocumentRoot`, `CreateXMLFileResource`) evaluate through
  `FMLEvaluation`. A null model, super type, type name, metamodel or root is an `FMLNullReferenceError`; a root that is not an XML individual
  an `FMLTypeError`; `AddXMLIndividual` without a type an `FMLResolutionError`; a URI that cannot be computed, or a duplicate URI, an
  `FMLTechnologyError`, the new individual being removed (also when an assertion fails). Without a model slot the individual is returned
  (no URI check possible). A typed failure of `AbstractCreateResource` is rethrown unchanged; a `ModelDefinitionException` is an
  `FMLInternalError`, a cancelled loading an `FMLCancellation`.
- The data assertions (`XMLDataPropertyAssertion.getValue`) and, in the core (`flexo-ontology`, shared with emf and owl), the conditions and
  values of the ontology assertions (`AbstractAssertion.evaluateCondition`, `DataPropertyAssertion.getValue`,
  `ObjectPropertyAssertion.getValue` / `getAssertionObject`) are strict: a condition that is invalid, fails or is null is an error, no longer
  taken as true; an unset condition still applies the assertion; an object that is not an ontology concept still gives null (a literal
  value of a data-range property is read through `getValue`).
- `SelectXMLIndividual` and its unique form: a null model is an `FMLNullReferenceError` naming the select.
- The factory of reflected instances (`XMLVirtualModelInstanceModelFactory`) declares no exception (it runs no creation scheme, `CORE-D-73`):
  the silent catch of `FMLXMLModelBuilder` is gone.
- After review: an unchecked failure in `AddXMLIndividual` goes through `FMLExecution.convertUnchecked` after the individual is removed.
  The review proved that `AddXMLIndividual` with a model slot never succeeds (its URI check cannot work on an individual not yet placed in
  the tree): it was a warning and null, it is now always an `FMLTechnologyError`; recorded as `XML-D-2`, not fixed (decided with the user).
- Chosen without an explicit decision, kept: a null super type, name or metamodel of `AddXMLType`, and a null root of `SetXMLDocumentRoot`,
  are `FMLNullReferenceError` (a null super type was a `NullPointerException`); a root that is not an individual is an `FMLTypeError`; a null
  receiver is an `FMLNullReferenceError`; a model without root gives null from `GetXMLDocumentRoot`.

**Left as they are (scope).** About 90 lines of `printStackTrace` / `System.out` in loading, parsing, XSD and model slot code. The
commented-out `getFlexoActor` of `XMLFlexoConceptInstance` (copied from the Excel adapter) is left commented.

**Tests.** `TestXMLErrorKinds` (xml-ta-test, scripts of `TestResourceCenter/XMLErrorKinds`, fixture `XMLErrorKinds.fml`):
`GetXMLDocumentRoot` on a null model, a select from a null model; each asserts the type, the behaviour frame and the line, and that nothing
is printed to stderr. Not covered by a test: `AddXMLIndividual`, `AddXMLType`, `SetXMLDocumentRoot` (their properties are not written in
textual FML: no `@FMLAttribute`), the strict ontology assertions.

**Verified (2026-11-16).** Green through the openflexo-dev composite: xml-ta-test 33, flexo-ontology, owl-ta-test 40, emf-ta-test 57,
openflexo-integration-tests (4 projects), formod (b-ta-test, formod-test), fml-cli-test 153. ECJ compiles flexo-ontology and xml-ta.
Reviewed by `openflexo-reviewer` (its findings fixed or recorded here and in `XML-D-2`). Not checked in the running application.
