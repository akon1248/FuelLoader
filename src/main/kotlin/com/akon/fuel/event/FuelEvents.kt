package com.akon.fuel.event

import net.neoforged.bus.api.BusBuilder
import net.neoforged.bus.api.IEventBus

object FuelEvents {

    @JvmField
    val BUS: IEventBus = BusBuilder.builder().build()
}
