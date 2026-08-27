pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            name = "planetcolorpicker"
            url = uri(file("${rootDir}/repository"))
        }
    }
}
rootProject.name = "PlanetColorPicker"
include(":app", ":planetcolorpicker")