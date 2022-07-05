import io.github.emilyydev.emmyson.conventions.extension.CrossdocConfig
import io.github.emilyydev.emmyson.conventions.extension.impl.AbstractCrossdocConfig

plugins {
  id("emmyson.common")
}

val crossdocConfig = objects.newInstance(AbstractCrossdocConfig::class)
extensions.add(CrossdocConfig::class, "crossdocConfig", crossdocConfig)

tasks {
  withType<Javadoc> {
    inputs.property("crossdocConfig.offlineLinks", crossdocConfig.offlineLinks)
    doFirst {
      val standardOptions = options as? StandardJavadocDocletOptions ?: return@doFirst
      standardOptions.linksOffline?.addAll(crossdocConfig.offlineLinks.get().values)
    }
  }
}
