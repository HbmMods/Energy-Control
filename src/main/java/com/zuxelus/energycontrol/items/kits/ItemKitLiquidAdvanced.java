package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.LiquidCardHelper;
import com.zuxelus.energycontrol.items.ItemHelper;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidTankInfo;

public class ItemKitLiquidAdvanced extends ItemKitBase {
	public ItemKitLiquidAdvanced() {
		super(ItemCardType.KIT_LIQUID_ADVANCED, "kit_liquid_advanced");
	}

	@Override
	public ItemStack getSensorCard(ItemStack stack, Item card, EntityPlayer player, World world, int x, int y, int z) {
		FluidTankInfo tank = LiquidCardHelper.getStorageAt(world, x, y, z);
		if (tank != null) {
			ItemStack sensorLocationCard = new ItemStack(ItemHelper.itemCard, 1, ItemCardType.CARD_LIQUID_ADVANCED);
			ItemStackHelper.setCoordinates(sensorLocationCard, x, y, z);
			return sensorLocationCard;
		}
		return CrossModLoader.ic2.getLiquidAdvancedCard(world, x, y, z);
	}
}
