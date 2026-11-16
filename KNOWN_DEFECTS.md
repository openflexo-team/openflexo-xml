# openflexo-xml — Known defects

Defects of openflexo-xml that are known and not fixed yet: symptom, what is verified, how to reproduce. Identifiers are stable and never
reused: `XML-D-<n>`. Fixed defects go to `FIXED_DEFECTS.md`.

Status legend: `TODO` · `IN PROGRESS` · `BLOCKED` · `DONE` · `DEFERRED`.

---

## Edition actions

### XML-D-2 — `AddXMLIndividual` with a model slot always fails its URI check  ·  `TODO`

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
  `DuplicateURIException` that `XMLModelSlot.getURIForObject` prints and turns into null.

`AddXMLIndividual` has no container attribute: the action does not say where the individual goes in the tree, so the positional URI
cannot be computed before it is placed. Also: `typeURI` has no `@FMLAttribute`, so the action cannot be written in textual FML (its type is
resolved through the inferred model slot).

**To fix.** Decide where the action places the individual (a container), compute the URI after placing it, and exclude the individual
itself from the duplicate lookup; fix SINGLETON. Recorded, not fixed (decided with the user).

**Workaround.** None through the action; create the element through the XML model API from Java.
