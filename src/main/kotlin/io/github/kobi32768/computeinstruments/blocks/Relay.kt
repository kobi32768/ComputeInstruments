package io.github.kobi32768.computeinstruments.blocks

import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraftforge.common.ToolType

object Relay : Block(Properties
    .create(Material.IRON)
    .setRequiresTool()
    .hardnessAndResistance(5.0f, 6.0f)
    .sound(SoundType.METAL)
    .harvestTool(ToolType.PICKAXE)
    .harvestLevel(1)
)
