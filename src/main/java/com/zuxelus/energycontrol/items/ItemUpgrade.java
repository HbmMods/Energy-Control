package com.zuxelus.energycontrol.items;

import java.util.List;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.init.ModItems;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemUpgrade extends Item {
	public static final int DAMAGE_RANGE = 0;
	public static final int DAMAGE_COLOR = 1;
	public static final int DAMAGE_TOUCH = 2;
	public static final int DAMAGE_WEB = 3;

	public ItemUpgrade() {
		super();
		setMaxDamage(0);
		setHasSubtypes(true);
		setCreativeTab(EnergyControl.creativeTab);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int damage = stack.getItemDamage();
		switch (damage) {
		case DAMAGE_RANGE:
			return "item.upgrade_range";
		case DAMAGE_COLOR:
			return "item.upgrade_color";
		case DAMAGE_TOUCH:
			return "item.upgrade_touch";
		case DAMAGE_WEB:
			return "item.upgrade_web";
		default:
			return "";
		}
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> items) {
		items.add(new ItemStack(ModItems.itemUpgrade, 1, DAMAGE_RANGE));
		items.add(new ItemStack(ModItems.itemUpgrade, 1, DAMAGE_COLOR));
		items.add(new ItemStack(ModItems.itemUpgrade, 1, DAMAGE_TOUCH));
		items.add(new ItemStack(ModItems.itemUpgrade, 1, DAMAGE_WEB));
	}
}
