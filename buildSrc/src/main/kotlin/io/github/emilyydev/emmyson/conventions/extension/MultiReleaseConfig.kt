package io.github.emilyydev.emmyson.conventions.extension

interface MultiReleaseConfig {

    fun addLanguageVersion(version: Int)
    fun addLanguageVersions(versions: Iterable<Int>)
    fun addLanguageVersions(versions: Sequence<Int>)
}
