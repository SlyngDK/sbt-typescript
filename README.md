# sbt-typescript
Typescript asset compiler, using the tsc command.

To use this plugin, add this to plugins.sbt

```scala
resolvers += Resolver.url("SlyngDK snapshots", url("http://SlyngDK.github.com/snapshots/"))(Resolver.ivyStylePatterns)
addSbtPlugin("dk.slyng.sbt" % "sbt-typescript" % "0.2-SNAPSHOT")
```

You will also need to enable the SbtWeb plugin in your project.

Option              | Description
--------------------|------------
tscPath             | Path to the tsc command.
sourceMap           | Generates source maps for input files.
noEmitOnError	| Do not emit outputs if any type checking errors were reported.
debug	| Enable debug output.
experimentalDecorators	| Enables experimental support for ES7 decorators.
emitDecoratorMetadata	|
module	| Specify module code generation.
target	| Specify ECMAScript target version.
moduleResolution	| Specifies module resolution strategy: 'node' (Node.js) or 'classic' (TypeScript pre-1.6).

To change an option change the values on dk.slyng.sbt.Import.TypescriptKeys, in build.sbt.

All *.ts will be compiled, *.d.ts is excluded.


Working with Angular 2
------------------------------------

Add to build.sbt
```sbt
TypescriptKeys.debug := false
TypescriptKeys.target := Target.ES5
TypescriptKeys.module := Module.system
TypescriptKeys.experimentalDecorators := true
TypescriptKeys.moduleResolution := ModuleResolution.node
TypescriptKeys.sourceMap := true
TypescriptKeys.emitDecoratorMetadata := true
```

Add typescript into app/assets/typescript.

------------------------------------
Feel free to use and contribute.