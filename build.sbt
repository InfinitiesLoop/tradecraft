val json4sVersion = "3.5.3"

lazy val commonSettings = Seq(
  organization := "com.infinity88",
  version := "1.0.0-SNAPSHOT",
  scalaVersion := "2.12.4",
  libraryDependencies := Seq(
  )
)

lazy val core = (project in file("core")).
  settings(commonSettings: _*).
  settings(
    name := "tradecraft-core",
    moduleName := "core",
    libraryDependencies := Seq(
      "org.scala-lang"    %   "scala-compiler"      % scalaVersion.value,
      "org.scala-lang"    %   "scala-library"       % scalaVersion.value,
      "org.scala-lang"    %   "scala-reflect"       % scalaVersion.value,
      // json for processing of requests and responses
      "org.json4s" %% "json4s-native" % json4sVersion,
      // templating engine for views
      "org.freemarker" % "freemarker" % "2.3.27-incubating",
      // for loading mods
      "org.clapper" %% "classutil" % "1.1.2"
    )
  )

lazy val server = (project in file("server")).
  settings(commonSettings: _*).
  settings(
    name := "tradecraft-server",
    moduleName := "server"
  ).dependsOn(core)

lazy val mod_sectormap = (project in file("mod.sectormap")).
  settings(commonSettings: _*).
  settings(
    name := "tradecraft-mod-sectormap",
    moduleName := "mod.sectormap"
  ).dependsOn(core)

lazy val mod_netty = (project in file("mod.netty")).
  settings(commonSettings: _*).
  settings(
    name := "tradecraft-mod-netty",
    moduleName := "mod.netty",
    libraryDependencies := Seq(
      "io.netty" % "netty-all" % "4.1.18.Final",
      "io.netty" % "netty-codec" % "4.1.18.Final",
      "org.json4s" %% "json4s-native" % json4sVersion
    )
  ).dependsOn(core)

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(
    name := "tradecraft",
    moduleName := "tradecraft"
  )
  .dependsOn(core, server, mod_netty)
  .aggregate(core, server, mod_sectormap)

mainClass in (Compile,run) := Some("tradecraft.Main")

