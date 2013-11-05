#Snomed Client Applications

###[Release Manifest Generator](/client/manifest-main)
Command line tool for creating a manifest file to go with a release bundle. For more information on the internals of this tool, have a look at the [Manifest library](/lib/manifest).
###[Canonical Generator](/client/canonical-main)
Command line tool for generating the canonical form of Snomed. See [algorithm details](/lib/canonical) for more information.
###[Canonical Maven MOJO Plugin](/client/canonical-plugin)
Maven MOJO build plugin for generating the canonical form of Snomed. See [algorithm details](/lib/canonical) for more information.
###[Closure Generator](/client/closure-main)
Command line tool for creating the transitive closure of isA relationships. See [algorithm details](/lib/closure) for more information.
###[Closure Maven MOJO Plugin](/client/closure-plugin)
Maven MOJO build plugin for creating the transitive closure of isA relationships. See [algorithm details](/lib/closure) for more information.
###[Snomed Diff Tool](/client/diff-main)
Command line tool for comparing two snomed releases.
###[Database Import Tool](/client/import-main)
Command line tool for importing Snomed to a relational database. See [algorithm details](/lib/importexport) for more information.
###[RDF Schema Generator](/client/rdfs-export-main)
Command line tool for converting Snomed to RDF Schema. See [algorithm details](/lib/importexport) for more information.