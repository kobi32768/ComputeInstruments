package io.github.kobi32768.computeinstruments

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraftforge.common.ToolType
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries

class Blocks {
    companion object {
        private val BLOCKS = DeferredRegister.create(
            ForgeRegistries.BLOCKS,
            ComputeInstruments.MOD_ID)

        val RELAY = BLOCKS.register("relay") {
            Block(AbstractBlock.Properties
                .create(Material.IRON)
                .setRequiresTool()
                .hardnessAndResistance(5.0f, 6.0f)
                .sound(SoundType.METAL)
                .harvestTool(ToolType.PICKAXE)
                .harvestLevel(1)
            )
        }!!

        fun register(eventBus: IEventBus) {
            BLOCKS.register(eventBus)
        }
    }
}
