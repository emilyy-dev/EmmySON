plugins {
    id("emmyson.licensed")
    jacoco
    id("me.champeau.jmh")
}

tasks {
    compileTestJava {
        options.release.set(JavaVersion.current().ordinal)
    }

    compileJmhJava {
        options.release.set(JavaVersion.current().ordinal)
    }

    test {
        finalizedBy(jacocoTestReport)
    }
}

jmh {
    resultFormat.set("CSV")
}

testing {
    suites {
        named<JvmTestSuite>("test") {
            useJUnitJupiter()
        }
    }
}
