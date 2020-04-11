organization := "com.esri"

name := "euclid"

version := "0.5"

isSnapshot := true

publishMavenStyle := true

crossScalaVersions := Seq("2.10.6", "2.11.12")

resolvers += Resolver.mavenLocal

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"

pomExtra :=
  <url>https://github.com/mraad/euclid</url>
    <licenses>
      <license>
        <name>Apache License, Version 2.0</name>
        <url>http://www.apache.org/licenses/LICENSE-2.0.html</url>
        <distribution>repo</distribution>
      </license>
    </licenses>
    <scm>
      <url>https://github.com/mraad/euclid</url>
      <connection>scm:git:https://github.com/mraad/euclid.git</connection>
    </scm>
    <developers>
      <developer>
        <id>mraad</id>
        <name>Mansour Raad</name>
        <url>https://github.com/mraad</url>
        <email>mraad@esri.com</email>
      </developer>
    </developers>
