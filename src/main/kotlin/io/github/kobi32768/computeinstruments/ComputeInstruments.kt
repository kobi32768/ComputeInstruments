package io.github.kobi32768.computeinstruments

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext

@Mod(ComputeInstruments.MOD_ID)
class ComputeInstruments {
    companion object {
        const val MOD_ID = "computeinstruments"
        val TAB = CIGroup(MOD_ID)
    }

    init {
        ComputeInstruments()
    }

    fun ComputeInstruments() {
        val modEventBus = FMLJavaModLoadingContext.get().modEventBus
        Blocks.register(modEventBus)
        Items.register(modEventBus)
    }
}
