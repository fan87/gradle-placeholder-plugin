import net.linguica.gradle.maven.settings.MavenSettingsPlugin
import java.net.URI

plugins {
    kotlin("jvm") version "1.6.20"
    `java-gradle-plugin`
    `maven-publish`
    id("net.linguica.maven-settings") version "0.5"
}

group = "me.fan87"
version = "1.3-SNAPSHOT"

repositories {
    mavenCentral()
}

apply<MavenSettingsPlugin>()

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.ow2.asm:asm-util:9.3")
    implementation("org.ow2.asm:asm:9.3")

}

publishing {
    repositories {
        maven {
            name = "github"
            url = URI("https://maven.pkg.github.com/fan87/Public-Maven-Repository")
        }
        mavenLocal()
    }
}

gradlePlugin {
    plugins {
        create("placeholderPlugin") {
            id = "me.fan87.placeholder-plugin"
            implementationClass = "me.fan87.placeholderplugin.PlaceHolderPlugin"
        }
    }
}
