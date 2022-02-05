rootProject.name = "emmyson"

sequenceOf(
    "api",
    "simple"
).forEach {
    include(it)
    project(":$it").name = "emmyson-$it"
}
