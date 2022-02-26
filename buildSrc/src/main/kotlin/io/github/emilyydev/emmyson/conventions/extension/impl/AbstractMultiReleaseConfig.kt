package io.github.emilyydev.emmyson.conventions.extension.impl

import io.github.emilyydev.emmyson.conventions.extension.MultiReleaseConfig
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.tasks.Jar
import java.util.jar.Attributes
import javax.inject.Inject

abstract class AbstractMultiReleaseConfig
@Inject constructor(
    private val configurations: ConfigurationContainer,
    private val tasks: TaskContainer,
    private val sourceSets: SourceSetContainer,
    private val dependencies: DependencyHandler,
    private val objects: ObjectFactory
) : MultiReleaseConfig {

    private val addedVersions = mutableSetOf<Int>()

    override fun addLanguageVersion(version: Int) {
        // don't do anything if it was already added
        if (!addedVersions.add(version)) {
            return
        }

        // create new source set for the desired version
        val newSourceSet = sourceSets.create("java$version") {
            java.setSrcDirs(listOf("src/${SourceSet.MAIN_SOURCE_SET_NAME}/$name"))
        }

        // add main classes to the compile classpath of the new source set
        dependencies.add(
            newSourceSet.compileOnlyConfigurationName,
            objects.fileCollection().from(sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME).output.classesDirs)
        )
        // add everything from the main compile classpath to this'
        configurations.getByName(newSourceSet.compileClasspathConfigurationName).extendsFrom(
            configurations.getByName(JavaPlugin.COMPILE_CLASSPATH_CONFIGURATION_NAME)
        )

        with(tasks) {
            // add license check for new source set
            named("check") {
                dependsOn("licenseJava$version")
            }

            // set language version for the generated class files
            named(newSourceSet.compileJavaTaskName, JavaCompile::class.java) {
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
