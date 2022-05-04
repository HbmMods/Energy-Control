package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import appeng.me.helpers.IGridProxyable;
import appeng.parts.CableBusContainer;
import appeng.parts.reporting.PartStorageMonitor;
import appeng.tile.networking.TileCableBus;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemKitAppEng extends ItemKitBase {

	public ItemKitAppEng() {
		super(ItemCardType.KIT_APPENG, "kit_app_eng");
	}

	@Override
	public ItemStack getSensorCard(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileCableBus) {
			CableBusContainer cb = ((TileCableBus) te).getCableBus();
			if (cb != null && cb.getPart(ForgeDirection.getOrientation(side)) instanceof PartStorageMonitor) {
				ItemStack newCard = new ItemStack(ModItems.itemCard, 1, ItemCardType.CARD_APPENG_INV);
				ItemStackHelper.setCoordinates(newCard, x, y, z);
				return newCard;
			}
		}
		if (te instanceof IGridProxyable) {
			ItemStack newCard = new ItemStack(ModItems.itemCard, 1, ItemCardType.CARD_APPENG);
			ItemStackHelper.setCoordinates(newCard, x, y, z);
			return newCard;
		}
		return null;
	}
}
