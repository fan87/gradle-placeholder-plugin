package me.fan87.placeholderplugin

import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project

class PlaceHolderPlugin: Plugin<Project> {

    override fun apply(target: Project) {
        val extension = target.extensions.create("placeholders", PlaceHolderExtension::class.java)
    }

    fun Project.placeholders(action: Action<PlaceHolderExtension>) {
        project.extensions.getByType(PlaceHolderExtension::class.java).apply {
            action.execute(this)
        }
    }

}