package io.github.kobi32768.computeinstruments

import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack

class CIGroup(label: String) : ItemGroup(label) {
    override fun createIcon(): ItemStack {
        return ItemStack(Items.RELAY.get())
    }
}
