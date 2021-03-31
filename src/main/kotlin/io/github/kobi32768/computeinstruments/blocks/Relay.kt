package io.github.kobi32768.computeinstruments.blocks

import net.minecraft.block.*
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.entity.LivingEntity
import net.minecraft.item.BlockItemUseContext
import net.minecraft.item.ItemStack
import net.minecraft.state.BooleanProperty
import net.minecraft.state.StateContainer
import net.minecraft.state.properties.BlockStateProperties
import net.minecraft.util.Direction
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.shapes.ISelectionContext
import net.minecraft.util.math.shapes.VoxelShape
import net.minecraft.world.IBlockReader
import net.minecraft.world.IWorldReader
import net.minecraft.world.TickPriority
import net.minecraft.world.World
import net.minecraft.world.server.ServerWorld
import net.minecraftforge.common.ToolType
import java.util.*
import kotlin.math.*

object Relay : HorizontalBlock(Properties
    .create(Material.IRON)
    .setRequiresTool()
    .hardnessAndResistance(5.0f, 6.0f)
    .sound(SoundType.METAL)
    .harvestTool(ToolType.PICKAXE)
    .harvestLevel(1)
) {
    val POWERED = BlockStateProperties.POWERED!!
    val CONTACTING = BooleanProperty.create("contacting")

    override fun getShape(state: BlockState, worldIn: IBlockReader, pos: BlockPos, context: ISelectionContext
    ): VoxelShape {
        return Block.makeCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)
    }

    override fun tick(state: BlockState, worldIn: ServerWorld, pos: BlockPos, rand: Random) {
        if (!isContacting(worldIn, pos, state) && isInputActivated(worldIn, pos, state)) {
            worldIn.setBlockState(pos, state.with(POWERED, true), 2)
        } else {
            worldIn.setBlockState(pos, state.with(POWERED, false), 2)
        }
    }

    private fun isContacting(worldIn: IWorldReader, pos: BlockPos, state: BlockState): Boolean {
        return getPowerOnSides(worldIn, pos, state) > 0
    }

    private fun getPowerOnSides(worldIn: IWorldReader, pos: BlockPos, state: BlockState): Int {
        val direction = state.get(HORIZONTAL_FACING)
        val directionR = direction.rotateY()
        val directionL = direction.rotateYCCW()
        return max(
            getPowerOnSide(worldIn, pos.offset(directionR), directionR),
            getPowerOnSide(worldIn, pos.offset(directionL), directionL))
    }

    private fun getPowerOnSide(worldIn: IWorldReader, pos: BlockPos, side: Direction): Int {
        val blockstate = worldIn.getBlockState(pos)
        if (!isAlternateInput(blockstate)) return 0
        return when {
            blockstate.isIn(Blocks.REDSTONE_BLOCK) -> 15
            blockstate.isIn(Blocks.REDSTONE_WIRE) -> blockstate.get(RedstoneWireBlock.POWER)
            else -> worldIn.getStrongPower(pos, side)
        }
    }

    private fun calculateInputStrength(worldIn: World, pos: BlockPos, state: BlockState): Int {
        val direction = state.get(HORIZONTAL_FACING)
        val blockpos = pos.offset(direction)
        val i = worldIn.getRedstonePower(blockpos, direction)
        if (i >= 15) {
            return i
        } else {
            val blockstate = worldIn.getBlockState(blockpos)
            return max(i,
                if (blockstate.isIn(Blocks.REDSTONE_WIRE))
                    blockstate.get(RedstoneWireBlock.POWER)
                else 0 )
        }
    }

    private fun isInputActivated(worldIn: World, pos: BlockPos, state: BlockState): Boolean {
        return calculateInputStrength(worldIn, pos, state) > 0
    }

    override fun neighborChanged(state: BlockState, worldIn: World, pos: BlockPos, blockIn: Block, fromPo: BlockPos, isMoving: Boolean) {
        if (state.isValidPosition(worldIn, pos)) {
            updateState(worldIn, pos, state)
        } else {
            val tileEntity = if (state.hasTileEntity()) worldIn.getTileEntity(pos) else null
            spawnDrops(state, worldIn, pos, tileEntity)
            worldIn.removeBlock(pos, false)

            for (direction in Direction.values()) {
                worldIn.notifyNeighborsOfStateChange(pos.offset(direction), this)
            }
        }
    }

    private fun updateState(worldIn: World, pos: BlockPos, state: BlockState) {
        if (!isContacting(worldIn, pos, state)) {
            val isPowered = state.get(POWERED)
            val isInputActivated = isInputActivated(worldIn, pos, state)
            if (isPowered != isInputActivated && !worldIn.pendingBlockTicks.isTickPending(pos, this)) {
                var tickPriority = if (isPowered)
                    TickPriority.VERY_HIGH
                else
                    TickPriority.HIGH

                worldIn.pendingBlockTicks.scheduleTick(pos, this, /* delay = */ 0, tickPriority)
            }
        }
    }

    override fun canProvidePower(state: BlockState): Boolean { return true }

    private fun isAlternateInput(state: BlockState): Boolean {
        return state.canProvidePower()
    }

    override fun getStateForPlacement(context: BlockItemUseContext): BlockState? {
        return defaultState.with(HORIZONTAL_FACING, context.placementHorizontalFacing.opposite)
    }

    override fun onBlockPlacedBy(worldIn: World, pos: BlockPos, state: BlockState, placer: LivingEntity?, stack: ItemStack) {
        if (isInputActivated(worldIn, pos, state)) {
            worldIn.pendingBlockTicks.scheduleTick(pos, this, /* delay = */ 0)
        }
    }

    override fun onBlockAdded(state: BlockState, worldIn: World, pos: BlockPos, oldState: BlockState, isMoving: Boolean) {
        notifyNeighbors(worldIn, pos, state)
    }

    override fun onReplaced(state: BlockState, worldIn: World, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        if (!isMoving && state.isIn(newState.block)) {
            super.onReplaced(state, worldIn, pos, newState, isMoving)
            notifyNeighbors(worldIn, pos, state)
        }
    }

    private fun notifyNeighbors(worldIn: World, pos: BlockPos, state: BlockState) {
        val direction = state.get(HORIZONTAL_FACING)
        val blockpos = pos.offset(direction.opposite)
        if (net.minecraftforge.event.ForgeEventFactory
                .onNeighborNotify(worldIn, pos, worldIn.getBlockState(pos),
                EnumSet.of(direction.opposite), false).isCanceled) { return }

        worldIn.neighborChanged(blockpos, this, pos)
        worldIn.notifyNeighborsOfStateExcept(blockpos, this, direction)
    }

    override fun fillStateContainer(builder: StateContainer.Builder<Block, BlockState>) {
        builder.add(HORIZONTAL_FACING, CONTACTING, POWERED)
    }
}
