# sbt-typescript
Typescript asset compiler, using the tsc command.

To use this plugin, checkout the sources and publishLocal using sbt.

```bash
git clone https://github.com/SlyngDK/sbt-typescript.git
cd sbt-typescript
sbt publishLocal
```

After that use the addSbtPlugin command within your project's plugins.sbt

```scala
addSbtPlugin("dk.slyng.sbt" % "sbt-typescript" % "0.1-SNAPSHOT")
```

You will also need to enable the SbtWeb plugin in your project.

Option              | Description
--------------------|------------
tscPath             | Path to the tsc command.
sourceMap           | Generates source maps for input files.

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