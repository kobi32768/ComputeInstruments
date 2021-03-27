package io.github.kobi32768.computeinstruments

import io.github.kobi32768.computeinstruments.blocks.Relay
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries

class Blocks {
    companion object {
        private val BLOCKS = DeferredRegister.create(
            ForgeRegistries.BLOCKS,
            ComputeInstruments.MOD_ID)

        val RELAY = BLOCKS.register("relay") { Relay }!!

        fun register(eventBus: IEventBus) {
            BLOCKS.register(eventBus)
        }
    }
}
