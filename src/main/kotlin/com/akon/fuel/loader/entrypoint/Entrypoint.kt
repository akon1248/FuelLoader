package com.akon.fuel.loader.entrypoint

import space.vectrix.ignite.mod.ModContainer

interface Entrypoint {
    fun initialize(container: ModContainer)
}
