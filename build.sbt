
lazy val commonSettings = Seq(
  organization := "com.infinity88",
  version := "1.0.0-SNAPSHOT",
  scalaVersion := "2.12.4"
)

lazy val core = (project in file("core")).
  settings(commonSettings: _*).
  settings(
    name := "tradecraft-core",
    moduleName := "core"
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
  )

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(
    name := "tradecraft",
    moduleName := "tradecraft"
  )
  .dependsOn(core, server)
  .aggregate(core, server, mod_sectormap)

// lol wut
mainClass in (Compile,run) := Some("tradecraft.Main")

