@file:Suppress("UnstableApiUsage")

import org.gradle.kotlin.dsl.named
import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask
import org.springframework.boot.gradle.tasks.run.BootRun
import java.util.Properties
import org.gradle.api.plugins.jvm.JvmTestSuite
import kotlin.collections.putAll

plugins {
    java
    alias(libs.plugins.jvm.test.suite)
    alias(libs.plugins.springframework.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.openapi.generator)
}

group = "io.manurasahs.deltavault"
version = "0.0.1"

repositories {
    mavenCentral()
}

val jvmTargetVersion = strProperty("jvmTargetVersion")
val additionalJavaOpts = strProperty("additionalJavaOpts")

testing {
    suites {
        withType<JvmTestSuite> {
            useJUnitJupiter()

            dependencies {
                implementation.add(libs.spring.boot.starter.test)
                implementation.add(libs.junit.jupiter)
                implementation.add(libs.archunit.junit5)
            }

            targets {
                all {
                    testTask.configure {
                        environment.putAll(customizedEnvironment)
                    }
                }
            }
        }

        val test by getting(JvmTestSuite::class) {}

        @Suppress("unused")
        val integTest by registering(JvmTestSuite::class) {

            dependencies {
                implementation.add(project())
                implementation.add(libs.testcontainers)
                implementation.add(libs.testcontainers.junit.jupiter)

                implementation.add(libs.spring.boot.starter.test)
                implementation.add(libs.junit.jupiter)
            }

            targets {
                all {
                    testTask.configure {
                        shouldRunAfter(test)
                    }
                }
            }
        }
    }
}

configurations["integTestImplementation"].extendsFrom(configurations["testImplementation"])
configurations["integTestRuntimeOnly"].extendsFrom(configurations["testRuntimeOnly"])

dependencies {
    implementation(platform(libs.spring.boot.bom))
    implementation(platform(libs.amazon.awssdk.bom))
    implementation(platform(libs.testcontainers.bom))

    implementation(libs.spring.boot.starter.web)
    implementation(libs.jakarta.annotation.api)
    implementation(libs.jakarta.validation.api)
    implementation(libs.jackson.databind.nullable)
    implementation(libs.software.amazon.awssdk.s3)
    implementation(libs.software.amazon.awssdk.dynamo)
    implementation(libs.com.flipkart.zjsonpatch)
    implementation(libs.apache.commons.codec)
}

springBoot {
    mainClass.set("io.manurasahs.deltavault.DeltaVaultApplication")
}

fun strProperty(name: String): String = property(name).toString()

val props = Properties()
file("$rootDir/.env").takeIf { it.canRead() }?.inputStream()?.use { stream ->
    props.load(stream)
}

val customizedEnvironment = mutableMapOf<String, Any>()
customizedEnvironment.putAll(props.map { it.key.toString() to it.value.toString() })
customizedEnvironment.putAll(System.getenv())

tasks.named<BootRun>("bootRun") {
    environment.putAll(customizedEnvironment)
}

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

    named<Copy>("processIntegTestResources") {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    named<Copy>("processTestResources") {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    compileJava {
        options.compilerArgs.add(additionalJavaOpts)
        dependsOn(generatePublicApi)
    }

    processResources {
        dependsOn(generatePublicApi)
    }
    processTestResources {
        dependsOn(generatePublicApi)
    }
    named("processIntegTestResources") {
        dependsOn(generatePublicApi)
    }

    withType(JavaExec::class) {
        jvmArgs = listOf(additionalJavaOpts)
    }
}

fun setupGenerateTaskDefaultFields(task: GenerateTask) {
    task.group = "openapi"
    task.generatorName.set("spring")
    task.generateApiDocumentation.set(false)
    task.generateApiTests.set(false)
    task.generateModelDocumentation.set(false)
    task.generateModelTests.set(false)
    task.skipOverwrite.set(false)

    task.configOptions.put("library", "spring-boot")
    task.configOptions.put("dateLibrary", "java8")
    task.configOptions.put("delegatePattern", "false")
    task.configOptions.put("interfaceOnly", "true")
    task.configOptions.put("hideGenerationTimestamp", "true")
    task.configOptions.put("sourceFolder", "src/main/java")
    task.configOptions.put("useTags", "true")
    task.configOptions.put("useBeanValidation", "true")
    task.configOptions.put("performBeanValidation", "false")
    task.configOptions.put("openApiNullable", "true")
    task.configOptions.put("legacyDiscriminatorBehavior", "false")
    task.configOptions.put("documentationProvider", "none")
    task.configOptions.put("annotationLibrary", "none")
    task.configOptions.put("useSpringBoot3", "true")
    task.configOptions.put("useOneOfInterfaces", "true")
    task.configOptions.put("skipDefaultInterface", "true")
    task.typeMappings.put("OffsetDateTime", "java.time.LocalDateTime")
    task.typeMappings.put("Double", "java.math.BigDecimal")
    task.typeMappings.put("UUID", "java.lang.String")
}

val generatePublicApi by tasks.registering(GenerateTask::class) {
    setupGenerateTaskDefaultFields(this)
    inputSpec.set("$rootDir/../specification/openapi/client-rest-api.yml")
    outputDir.set("$rootDir")
    apiPackage.set("io.manurasahs.deltavault.port.adapter.clientrest.resources.api")
    invokerPackage.set("io.manurasahs.deltavault.port.adapter.clientrest.resources.invoker")
    modelPackage.set("io.manurasahs.deltavault.port.adapter.clientrest.resources.model")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(jvmTargetVersion))
    }
}
