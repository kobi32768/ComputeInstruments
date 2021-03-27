package io.github.kobi32768.computeinstruments

import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries

class Items {
    companion object {
        private val ITEMS = DeferredRegister.create(
            ForgeRegistries.ITEMS,
            ComputeInstruments.MOD_ID
        )

        val RELAY = ITEMS.register("relay") {
            BlockItem(Blocks.RELAY.get(), Item.Properties()
                .group(ItemGroup.REDSTONE))
        }!!

        fun register(eventBus: IEventBus) {
            ITEMS.register(eventBus)
        }
    }
}