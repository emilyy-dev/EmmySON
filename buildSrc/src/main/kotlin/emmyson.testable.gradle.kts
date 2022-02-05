plugins {
    id("emmyson.licensed")
}

tasks {
    compileTestJava {
        options.release.set(JavaVersion.current().ordinal)
    }
}

testing.suites.named<JvmTestSuite>("test") {
    useJUnitJupiter()
}
