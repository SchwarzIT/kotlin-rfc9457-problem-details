import com.vanniktech.maven.publish.SonatypeHost.Companion.S01

val ktorVersion: String by project
val kotestVersion: String by project

plugins {
   kotlin("jvm") version "2.0.21"
   id("org.jetbrains.kotlin.plugin.serialization") version "2.0.20"
   id("jacoco")
   id("com.vanniktech.maven.publish") version "0.30.0"
}

group = "io.github.schwarzit"
version = "1.0.0"

repositories {
   mavenCentral()
}

dependencies {
   implementation(kotlin("reflect"))
   implementation("io.ktor:ktor-server-core:$ktorVersion")
   implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

   testImplementation(kotlin("test"))
   testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
   testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
}

tasks.test {
   useJUnitPlatform()
}
kotlin {
   jvmToolchain(21)
}

mavenPublishing {
   publishToMavenCentral(S01)
   coordinates(group.toString(), "kotlin-rfc9457-problem-details", version.toString())
   pom {
      name.set("Kotlin-RFC9457-Problem-Details")
      description.set("A Kotlin implementation of the RFC 9457 problem details format for handling HTTP API errors.")
      inceptionYear.set("2024")
      url.set("https://github.com/SchwarzIT/kotlin-rfc9457-problem-details")
      licenses {
         license {
            name.set("The Apache License, Version 2.0")
            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
         }
      }
      developers {
         developer {
            name.set("Johannes Hepp")
            url.set("https://github.com/johanneshepp")
         }
      }

      scm {
         url.set("https://github.com/SchwarzIT/kotlin-rfc9457-problem-details")
         connection.set("scm:git:git://github.com/SchwarzIT/kotlin-rfc9457-problem-details.git")
         developerConnection.set("scm:git:ssh://git@github.com/SchwarzIT/kotlin-rfc9457-problem-details.git")
      }
   }
}

jacoco {
   reportsDirectory.set(layout.buildDirectory.dir("reports/jacoco/"))
}

with(tasks) {
   test {
      useJUnitPlatform()
      finalizedBy(jacocoTestReport)
   }

   jacocoTestReport {
      dependsOn(test)
      reports {
         xml.required.set(true)
         csv.required.set(false)
         html.required.set(true)
         xml.outputLocation.set(layout.buildDirectory.file("reports/jacoco/report.xml"))
         html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/html"))
      }
   }
}
