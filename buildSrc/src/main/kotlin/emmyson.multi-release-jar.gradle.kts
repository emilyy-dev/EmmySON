import io.github.emilyydev.emmyson.conventions.extension.MultiReleaseConfig
import io.github.emilyydev.emmyson.conventions.extension.impl.MultiReleaseConfigImpl

plugins {
    id("emmyson.licensed")
}

val multiReleaseConfig = MultiReleaseConfigImpl(configurations, tasks, sourceSets, dependencies, objects)
extensions.add(MultiReleaseConfig::class, "multiReleaseConfig", multiReleaseConfig)
