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


------------------------------------

Feel free to used and contribute.