val ktorVersion: String by project
val kotestVersion: String by project

plugins {
    kotlin("jvm") version "2.0.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0"
    id("maven-publish")
    id("jacoco")
}

group = "schwarz.it"
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

publishing {
   publications {
      create<MavenPublication>("mavenPublication") {
         from(components["java"])
      }
   }
   repositories {
      maven {
         name = "artifactory"
         credentials {
            username = project.findProperty("artifactoryUsername") as String? ?: System.getenv("ARTIFACTORY_USER")
            password =
               project.findProperty("artifactoryPassword") as String? ?: System.getenv("ARTIFACTORY_PASSWORD")
         }
         url = uri("https://schwarzit.jfrog.io/artifactory/xx-sit-odj-psftp-maven-release-local/")
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
