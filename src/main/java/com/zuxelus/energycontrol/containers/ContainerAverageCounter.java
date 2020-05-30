package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityAverageCounter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.item.ItemStack;

public class ContainerAverageCounter extends ContainerBase<TileEntityAverageCounter>
{
	private double lastAverage = -1;

	public ContainerAverageCounter(EntityPlayer player, TileEntityAverageCounter averageCounter) {
		super(averageCounter);
		// transformer upgrades
		addSlotToContainer(new SlotFilter(averageCounter, 0, 8, 18));
		// inventory
		addPlayerInventorySlots(player, 166);
	}

	@Override
	public void addCraftingToCrafters(ICrafting listener) {
		super.addCraftingToCrafters(listener);
		NetworkHelper.updateClientTileEntity(listener, te.xCoord, te.yCoord, te.zCoord, 1, te.getClientAverage());
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		int average = te.getClientAverage();
		for (int i = 0; i < crafters.size(); i++)
			if (lastAverage != average)
				NetworkHelper.updateClientTileEntity((ICrafting)crafters.get(i), te.xCoord, te.yCoord, te.zCoord, 1, average);
		lastAverage = average;
	}

	@Override
	public ItemStack slotClick(int slotId, int dragType, int clickType, EntityPlayer player) {
		ItemStack stack = super.slotClick(slotId, dragType, clickType, player);
		te.markDirty();
		return stack;
	}
}