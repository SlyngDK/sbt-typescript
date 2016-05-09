package dk.slyng.sbt

import com.typesafe.sbt.jse.SbtJsTask
import dk.slyng.sbt.SbtTypescript.Module.Module
import dk.slyng.sbt.SbtTypescript.ModuleResolution.ModuleResolution
import dk.slyng.sbt.SbtTypescript.Target.Target
import sbt.Keys._
import sbt.{File, _}
import spray.json.{JsBoolean, JsObject, JsString}

object Import {


  object TypescriptKeys {
    val typescript = TaskKey[Seq[File]]("typescript", "Invoke the typescript compiler.")

    val tscPath = SettingKey[String]("typescript-tscPath", "Path to the tsc command.")
    val sourceMap = SettingKey[Boolean]("typescript-sourceMap", "Outputs a sourcemap.")
    val noEmitOnError = SettingKey[Boolean]("typescript-noEmitOnError", "Do not emit outputs if any type checking errors were reported.")
    val debug = SettingKey[Boolean]("Enable debug output.")
    val experimentalDecorators = SettingKey[Boolean]("Enables experimental support for ES7 decorators.")
    val emitDecoratorMetadata = SettingKey[Boolean]("")
    val module = SettingKey[Module]("Specify module code generation.")
    val target = SettingKey[Target]("Specify ECMAScript target version.")
    val moduleResolution = SettingKey[ModuleResolution]("Specifies module resolution strategy: 'node' (Node.js) or 'classic' (TypeScript pre-1.6).")
  }

}

object SbtTypescript extends AutoPlugin {

  object Module extends Enumeration {
    type Module = Value
    val commonjs = Value("commonjs")
    val amd = Value("amd")
    val system = Value("system")
    val umd = Value("umd")
    val es2015 = Value("es2015")
  }

  object Target extends Enumeration {
    type Target = Value
    val ES3 = Value("es3")
    val ES5 = Value("es5")
    val ES2015 = Value("es2015")
  }

  object ModuleResolution extends Enumeration {
    type ModuleResolution = Value
    val node = Value("node")
    val classic = Value("classic")
  }

  override def requires = SbtJsTask

  override def trigger = AllRequirements

  val autoImport = Import

  import autoImport.TypescriptKeys._
  import com.typesafe.sbt.jse.SbtJsTask.autoImport.JsTaskKeys._
  import com.typesafe.sbt.web.Import.WebKeys._
  import com.typesafe.sbt.web.SbtWeb.autoImport._

  val typescriptUnscopedSettings = Seq(
    includeFilter := "*.ts",
    excludeFilter := "*.d.ts",

    jsOptions := JsObject(
      "tscPath" -> JsString(tscPath.value),
      "sourceMap" -> JsBoolean(sourceMap.value),
      "noEmitOnError" -> JsBoolean(noEmitOnError.value),
      "debug" -> JsBoolean(debug.value),
      "module" -> JsString(module.value.toString),
      "target" -> JsString(target.value.toString),
      "experimentalDecorators" -> JsBoolean(experimentalDecorators.value),
      "moduleResolution" -> JsString(moduleResolution.value.toString),
      "emitDecoratorMetadata" -> JsBoolean(emitDecoratorMetadata.value)
    ).toString()
  )

  override def projectSettings = Seq(
    tscPath := "",
    sourceMap := true,
    noEmitOnError := false,
    debug := false,
    module := Module.system,
    target := Target.ES5,
    experimentalDecorators := false,
    moduleResolution := ModuleResolution.node,
    emitDecoratorMetadata := false

  ) ++ inTask(typescript)(
    SbtJsTask.jsTaskSpecificUnscopedSettings ++
      inConfig(Assets)(typescriptUnscopedSettings) ++
      inConfig(TestAssets)(typescriptUnscopedSettings) ++
      Seq(
        moduleName := "javascripts",
        shellFile := getClass.getClassLoader.getResource("typescript.js"),

        taskMessage in Assets := "TypeScript compiling",
        taskMessage in TestAssets := "TypeScript test compiling"
      )
  ) ++ SbtJsTask.addJsSourceFileTasks(typescript) ++ Seq(
    typescript in Assets := (typescript in Assets).dependsOn(webModules in Assets).value,
    typescript in Assets := (typescript in Assets).dependsOn(nodeModules in Assets).value,
    typescript in TestAssets := (typescript in TestAssets).dependsOn(webModules in TestAssets).value,
    typescript in TestAssets := (typescript in TestAssets).dependsOn(nodeModules in TestAssets).value
  )

}
