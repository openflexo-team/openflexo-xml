# openflexo-xml — XML technology adapter

**Status:** Active <!-- Active · Frozen · POC · Deprecated · Retired: same as TA-CATALOG.md -->

Connects FML to XML documents: free documents navigated element by element, and documents typed by an XSD metamodel whose types FML uses.

| | |
|---|---|
| Technology | XML and XML Schema (XSD), through XSOM |
| Libraries | `xsom` 4.0.9 |
| Family | `DOC` · `R/W` (see the classification of `openflexo-technology-adapters/TA-CATALOG.md`) |
| Saving | free documents are written back (without namespaces nor comments, `XML-D-6`); typed documents and XSD metamodels are not written: their save leaves the file unchanged with a warning (`XML-D-4`) |
| Resource factories | `XSDMetaModelResourceFactory` (.xsd), `TypedXMLResourceFactory` (.xml), `FreeXMLResourceFactory` |
| Test resource center | `http://openflexo.org/xml-test` |

## FML surface

| Model slot | FML name | Roles | Edition actions and behaviours | Fetch requests |
|---|---|---|---|---|
| `FreeXMLModelSlot` | `FreeXMLModelSlot` | — | `CreateXMLFileResource`, `GetXMLDocumentRoot`, `SetXMLDocumentRoot` | — |
| `XMLModelSlot` | `XMLModelSlot` | `XMLIndividualRole` | `CreateXMLFileResource`, `AddXMLIndividual`, `GetXMLDocumentRoot`, `SetXMLDocumentRoot` | `SelectUniqueIndividual`, `SelectXMLIndividual` |
| `XMLMetaModelSlot` | `XMLMetaModelSlot` | `XMLTypeRole` | `AddXMLType` | — |
| `FMLXMLModelSlot` | `FMLXMLModelSlot` | — | — | — |

## Example

From the test resource center (`TestResourceCenter/FML/reflect/TestFMLReflect1.fml/TestFMLReflect1.fml`):

```fml
use org.openflexo.technologyadapter.xml.FMLXMLModelSlot as XML;

import org.openflexo.technologyadapter.xml.model.free.FreeXMLDocument;

import ["http://openflexo.org/xml-test/FML/reflect/Library1"];

@URI("http://openflexo.org/xml-test/FML/reflect/TestFMLReflect1.fml")
public model TestFMLReflect1 {
	Library1 library with FMLXMLModelSlot();

   create(Resource<FreeXMLDocument> resource) {
      connect library using resource ;
   }
}
```

Scripts: `xml-ta-test-rc/…/TestResourceCenter/AutomatedTests/`, `xml-ta-test-rc/…/TestResourceCenter/XMLErrorKinds/`.
Save: `TestXMLRoundTrip` (xml-ta-test) makes the round trip of a free document in a resource center of its own.

## Modules

| Module | Content |
|---|---|
| `xml-ta` | adapter, model slots, resources, domain model, dictionaries |
| `xml-ta-ui` | controller, icons, module views, inspectors |
| `xml-ta-test` | tests |
| `xml-ta-test-rc` | test resource center |

## Build and test

From `openflexo-dev` (local sources of the whole platform):

    ./gradlew --continue :openflexo-xml:xml-ta-test:cleanTest :openflexo-xml:xml-ta-test:test

Tests run with `ignoreFailures = true`: read the `Results:` line, not the exit status.

## Known defects and backlog

- Known defects: [KNOWN_DEFECTS.md](KNOWN_DEFECTS.md) (`XML-D-<n>`), fixed ones in [FIXED_DEFECTS.md](FIXED_DEFECTS.md).
- Features, evolutions and user requests: [BACKLOG.md](BACKLOG.md) (`XML-F-<n>`).
- Issues: https://github.com/openflexo-team/openflexo-xml/issues

## License

Dual-licensed under the EUPL (1.1 or later) and the GNU GPL (3 or later), as stated in the header of each source file;
[LICENSE](LICENSE) holds the text of the GPL.
