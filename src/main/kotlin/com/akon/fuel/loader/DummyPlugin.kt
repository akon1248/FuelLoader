package com.akon.fuel.loader

interface DummyPlugin {
    companion object {
        const val RELOADING_DISABLED_MESSAGE =
            "[Fuel] Plugins/Mods loaded by Fuel Loader do not support reloading. " +
                "Please restart the server to apply changes."
    }
}
