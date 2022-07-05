package io.github.emilyydev.emmyson.conventions.extension.impl

import io.github.emilyydev.emmyson.conventions.extension.CrossdocConfig
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.provider.MapProperty
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.external.javadoc.JavadocOfflineLink
import org.gradle.kotlin.dsl.project
import javax.inject.Inject

private const val CROSSDOC_CONFIGURATION_NAME = "crossdoc"

private fun javadocIo(project: Project) = with(project) { "https://javadoc.io/static/$group/$name/$version/" }

abstract class AbstractCrossdocConfig
@Inject constructor(
    private val dependencies: DependencyHandler
) : CrossdocConfig {

    abstract val offlineLinks: MapProperty<String, JavadocOfflineLink>

    override fun linkJavadocFor(project: Project) {
        with(project.configurations.maybeCreate(CROSSDOC_CONFIGURATION_NAME)) {
            isCanBeConsumed = true
            isCanBeResolved = false
        }

        val javadocTask = project.tasks.named(JavaPlugin.JAVADOC_TASK_NAME, Javadoc::class.java)
        project.artifacts.add(CROSSDOC_CONFIGURATION_NAME, javadocTask.map { it.destinationDir!! }) {
            builtBy(javadocTask)
        }

        with(dependencies) {
            add(JavaPlugin.JAVADOC_ELEMENTS_CONFIGURATION_NAME, project(project.path, CROSSDOC_CONFIGURATION_NAME))
        }

        val javadocIoUrl = javadocIo(project)
        offlineLinks.put(
            project.path,
            javadocTask.map { JavadocOfflineLink(javadocIoUrl, it.destinationDir.toString()) }
        )
    }
}
