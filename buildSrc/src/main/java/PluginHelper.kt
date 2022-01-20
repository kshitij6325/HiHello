package main.java

import org.gradle.kotlin.dsl.PluginDependenciesSpecScope

fun PluginDependenciesSpecScope.addBasicPlugins() {
    id("kotlin-android")
    id("kotlinx-serialization")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

fun PluginDependenciesSpecScope.addAppPlugins() {
    id("com.android.application")
    id("androidx.navigation.safeargs.kotlin")
    addBasicPlugins()
}

fun PluginDependenciesSpecScope.addDataLibBasicPlugins() {
    id("com.android.library")
    addBasicPlugins()
}

fun PluginDependenciesSpecScope.addFeatureLibBasicPlugins() {
    addDataLibBasicPlugins()
    id("androidx.navigation.safeargs.kotlin")
}