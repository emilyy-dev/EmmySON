package io.github.emilyydev.emmyson.conventions.extension.impl

import io.github.emilyydev.emmyson.conventions.extension.MultiReleaseConfig
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.tasks.Jar
import java.util.jar.Attributes

class MultiReleaseConfigImpl(
    private val configurations: ConfigurationContainer,
    private val tasks: TaskContainer,
    private val sourceSets: SourceSetContainer,
    private val dependencies: DependencyHandler,
    private val objects: ObjectFactory
) : MultiReleaseConfig {

    private val addedVersions: MutableSet<Int> = mutableSetOf()

    override fun addLanguageVersion(version: Int) {
        // don't do anything if it was already added
        if (!addedVersions.add(version)) {
            return
        }

        val name = "java$version"
        val capitalizedName = "Java$version"

        // create new source set for the desired version
        val newSourceSet = sourceSets.create(name) {
            java.srcDir("src/main/$name")
        }

        // add main classes to the compile classpath of the new source set
        val mainClasses = objects.fileCollection().from(sourceSets.getByName("main").output.classesDirs)
        dependencies.add("${name}CompileOnly", mainClasses)
        // add everything from the main compile classpath to this'
        configurations.getByName("${name}CompileClasspath").extendsFrom(configurations.getByName("compileClasspath"))

        with(tasks) {
            // add license check for new source set
            named("check") {
                dependsOn("license$capitalizedName")
            }

            // set language version for the generated class files
            named("compile${capitalizedName}Java", JavaCompile::class.java) {
                options.release.set(version)
            }

            // mark jar as multi-release and add new source set output to the corresponding version location
            withType(Jar::class.java) {
                metaInf {
                    manifest.attributes[Attributes.Name.MULTI_RELEASE.toString()] = true
                    from(newSourceSet.output) {
                        into("versions/$version")
                    }
                }
            }
        }
    }
}