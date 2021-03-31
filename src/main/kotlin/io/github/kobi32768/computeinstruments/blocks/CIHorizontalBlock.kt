package io.github.kobi32768.computeinstruments.blocks

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.HorizontalBlock
import net.minecraft.item.BlockItemUseContext
import net.minecraft.state.StateContainer

open class CIHorizontalBlock(builder: Properties) : HorizontalBlock(builder) {
    override fun getStateForPlacement(context: BlockItemUseContext): BlockState? {
        return defaultState.with(HORIZONTAL_FACING, context.placementHorizontalFacing.opposite)
    }

    override fun fillStateContainer(builder: StateContainer.Builder<Block?, BlockState?>) {
        builder.add(HORIZONTAL_FACING)
    }
}