rootProject.name = "emmyson"

includeBuild("build-logic")

sequenceOf(
  "api",
  "simple"
).forEach {
  include(it)
  project(":$it").name = "emmyson-$it"
}
