package me.fan87.placeholderplugin

import java.util.function.Supplier

abstract class PlaceHolderExtension {

    val placeHolders = HashMap<String, Supplier<String>>()

    inline fun registerPlaceHolder(id: String, crossinline content: () -> String) {
        placeHolders[id] = Supplier<String> { content() }
    }

    inline fun registerLazyPlaceHolder(id: String, crossinline content: () -> String) {
        registerPlaceHolder(id, content())
    }

    fun registerPlaceHolder(id: String, content: String) {
        placeHolders[id] = Supplier<String> { content }
    }

}