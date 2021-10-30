package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.init.ModContainerTypes;
import com.zuxelus.energycontrol.tileentities.TileEntityFluidControlValve;
import com.zuxelus.zlib.containers.ContainerBase;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;

public class ContainerFluidControlValve extends Container {
	public TileEntityFluidControlValve te;

	public ContainerFluidControlValve(int windowId, PlayerInventory inventory, PacketBuffer data) {
		this(windowId, inventory, (TileEntityFluidControlValve) ContainerBase.getTileEntity(inventory, data));
	}

	public ContainerFluidControlValve(int windowId, PlayerInventory inventory, TileEntityFluidControlValve te) {
		super(ModContainerTypes.fluid_control_valve.get(), windowId);
		this.te = te;
		addPlayerInventorySlots(inventory, 166);
	}

	private void addPlayerInventorySlots(PlayerInventory inventory, int height) {
		addPlayerInventorySlots(inventory, 178, height);
	}

	private void addPlayerInventorySlots(PlayerInventory inventory, int width, int height) {
		int xStart = (width - 162) / 2;
		for (int row = 0; row < 3; row++)
			for (int i = 0; i < 9; i++)
				addSlot(new Slot(inventory, i + row * 9 + 9, xStart + i * 18, height - 82 + row * 18));

		addPlayerInventoryTopSlots(inventory, xStart, height);
	}

	private void addPlayerInventoryTopSlots(PlayerInventory inventory, int width, int height) {
		for (int col = 0; col < 9; col++)
			addSlot(new Slot(inventory, col, width + col * 18, height - 24));
	}

	@Override
	public boolean stillValid(PlayerEntity player) {
		return player.distanceToSqr(te.getBlockPos().getX() + 0.5D, te.getBlockPos().getY() + 0.5D, te.getBlockPos().getZ() + 0.5D) <= 64.0D;
	}
}
