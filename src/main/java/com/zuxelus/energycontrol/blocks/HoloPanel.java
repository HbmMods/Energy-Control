package com.zuxelus.energycontrol.blocks;

import java.util.Random;

import com.zuxelus.energycontrol.tileentities.TileEntityHoloPanel;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class HoloPanel extends FacingHorizontalActiveEC {
	protected static final AxisAlignedBB AABB_NORTH = new AxisAlignedBB(0.0D, 0.0D, 0.25D, 1.0D, 0.0625D, 0.75D);
	protected static final AxisAlignedBB AABB_WEST = new AxisAlignedBB(0.25D, 0.0D, 0.0D, 0.75D, 0.0625D, 1.0D);

	@Override
	protected TileEntityFacing createTileEntity(int meta) {
		return new TileEntityHoloPanel();
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
		return side == EnumFacing.UP ? canPlaceBlockAt(world, pos) : false;
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos.down());
		return state.isSideSolid(world,  pos.down(), EnumFacing.UP); // 1.10.2
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, ItemStack stack) {
		return super.getStateForPlacement(world, pos, facing, hitZ, hitZ, hitZ, meta, placer, stack).withProperty(ACTIVE, world.isBlockPowered(pos));
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block) {
		if (!canPlaceBlockAt(world, pos)) {
			dropBlockAsItem(world, pos, state, 0);
			world.setBlockToAir(pos);
		} else 
			if (!world.isRemote) {
				TileEntity te = world.getTileEntity(pos);
				if (!(te instanceof TileEntityInfoPanel))
					return;

				((TileEntityInfoPanel) te).updateBlockState(state);
			}
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		world.setBlockState(pos, state.cycleProperty(ACTIVE), 2);
		TileEntity be = world.getTileEntity(pos);
		if (be instanceof TileEntityInfoPanel)
			((TileEntityInfoPanel) be).updateExtenders(!state.getValue(ACTIVE));
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		switch (state.getValue(FACING)) {
		case WEST:
		case EAST:
			return AABB_WEST;
		case NORTH:
		case SOUTH:
		default:
			return AABB_NORTH;
		}
	}

	@Override
	protected int getBlockGuiId() {
		return BlockDamages.DAMAGE_HOLO_PANEL;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}
}
