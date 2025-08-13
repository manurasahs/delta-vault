import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    java
    alias(libs.plugins.springframework.boot)
    alias(libs.plugins.spring.dependency.management)
}

group = "io.manurasahs.deltavault"
version = "0.0.1"

repositories {
    mavenCentral()
}

val jvmTargetVersion = strProperty("jvmTargetVersion")
val additionalJavaOpts = strProperty("additionalJavaOpts")

dependencies {
    implementation(platform(libs.spring.boot.bom))
    implementation(libs.spring.boot.starter)
}

springBoot {
    mainClass.set("io.manurasahs.deltavault.DeltaVaultApplication")
}

fun strProperty(name: String): String = property(name).toString()

val bootJarExplode by tasks.registering(Copy::class) {
    dependsOn(tasks.named("bootJar"))
    from(zipTree(tasks.named<BootJar>("bootJar").get().archiveFile))
    into(layout.buildDirectory.dir("exploded"))
}

tasks {
    withType(JacocoReport::class) {
        executionData(withType(Test::class))
        reports {
            xml.required.set(true)
        }
    }

    withType(Test::class) {
        useJUnitPlatform()
        this.jvmArgs = listOf(additionalJavaOpts)
        finalizedBy(withType(JacocoReport::class))
    }

    check {
        dependsOn(test)
    }

    assemble {
        dependsOn(bootJarExplode)
    }

    named<Copy>("processTestResources") {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    compileJava {
        options.compilerArgs.add(additionalJavaOpts)
    }

    withType(JavaExec::class) {
        jvmArgs = listOf(additionalJavaOpts)
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(jvmTargetVersion))
    }
}
