package com.akon.fuel.loader.entrypoint

import org.bukkit.plugin.Plugin

interface PluginLifecycleDelegate {

    fun onInstantiate(plugin: Plugin) {}

    fun onLoad(plugin: Plugin) {}

    fun onEnable(plugin: Plugin) {}

    fun onDisable(plugin: Plugin) {}
}
