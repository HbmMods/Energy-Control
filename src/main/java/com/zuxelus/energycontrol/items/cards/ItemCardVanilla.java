package com.zuxelus.energycontrol.items.cards;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCardVanilla extends ItemCardBase {
	public static final int DISPLAY_BURNING = 1;
	public static final int DISPLAY_TIME = 2;
	public static final int DISPLAY_SLOT_1 = 4;
	public static final int DISPLAY_SLOT_2 = 8;
	public static final int DISPLAY_SLOT_3 = 16;

	public ItemCardVanilla() {
		super(ItemCardType.CARD_VANILLA, "card_vanilla");
	}

	@Override
	public CardState update(World world, ICardReader reader, int range, BlockPos pos) {
		BlockPos target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		TileEntity te = world.getTileEntity(target);
		if (te instanceof TileEntityFurnace) {
			TileEntityFurnace furnace = (TileEntityFurnace) te;
			reader.setInt("type", 1);
			reader.setString("entity", "furnace");
			reader.setBoolean("burning", furnace.isBurning());
			reader.setInt("burnTime", furnace.getField(0));
			NBTTagCompound tag = new NBTTagCompound();
			if (!furnace.getStackInSlot(0).isEmpty()) {
				tag.setString("Cooking", furnace.getStackInSlot(0).getDisplayName());
				tag.setInteger("Csize", furnace.getStackInSlot(0).getCount());
			}
			if (!furnace.getStackInSlot(1).isEmpty()) {
				tag.setString("Fuel", furnace.getStackInSlot(1).getDisplayName());
				tag.setInteger("Fsize", furnace.getStackInSlot(1).getCount());
			}
			if (!furnace.getStackInSlot(2).isEmpty()) {
				tag.setString("Output", furnace.getStackInSlot(2).getDisplayName());
				tag.setInteger("Osize", furnace.getStackInSlot(2).getCount());
			}
			reader.setTag("Info", tag);
			return CardState.OK;
		}
		return CardState.NO_TARGET;
	}

	@Override
	public List<PanelString> getStringData(int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = new LinkedList<>();
		int burnTime = reader.getInt("burnTime");
		NBTTagCompound tag = reader.getTag("Info");
		if (tag != null) {
			if ((settings & DISPLAY_TIME) > 0)
				result.add(new PanelString("msg.burnTime", burnTime, showLabels));
			if ((settings & DISPLAY_SLOT_1) > 0) {
				String slot1pre = isServer ? "N/A" : I18n.format("msg.ec.None");
				if (tag.hasKey("Cooking"))
					slot1pre = tag.getString("Cooking");
				if (showLabels)
					result.add(PanelString.create("msg.cooking", tag.getInteger("Csize"), slot1pre));
				else
					result.add(new PanelString(String.format("%sx - %s", tag.getInteger("Csize"), slot1pre)));
			}
			if ((settings & DISPLAY_SLOT_2) > 0) {
				String slot2pre = isServer ? "N/A" : I18n.format("msg.ec.None");
				if (tag.hasKey("Fuel"))
					slot2pre = tag.getString("Fuel");
				if (showLabels)
					result.add(PanelString.create("msg.fuel", tag.getInteger("Fsize"), slot2pre));
				else
					result.add(new PanelString(String.format("%sx - %s", tag.getInteger("Fsize"), slot2pre)));
			}
			if ((settings & DISPLAY_SLOT_3) > 0) {
				String slot3pre = isServer ? "N/A" : I18n.format("msg.ec.None");
				if (tag.hasKey("Output"))
					slot3pre = tag.getString("Output");
				if (showLabels)
					result.add(PanelString.create("msg.output", tag.getInteger("Osize"), slot3pre));
				else
					result.add(new PanelString(String.format("%sx - %s", tag.getInteger("Osize"), slot3pre)));
			}
		}
		if ((settings & DISPLAY_BURNING) > 0)
			addOnOff(result, isServer, reader.getBoolean("burning"));
		return result;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public List<PanelSetting> getSettingsList(ItemStack stack) {
		List<PanelSetting> result = new ArrayList<>();
		result.add(new PanelSetting(I18n.format("msg.ec.cbInfoPanelOnOff"), DISPLAY_BURNING));
		result.add(new PanelSetting(I18n.format("msg.burnTimeText"), DISPLAY_TIME));
		result.add(new PanelSetting(I18n.format("msg.cookingText"), DISPLAY_SLOT_1));
		result.add(new PanelSetting(I18n.format("msg.fuelText"), DISPLAY_SLOT_2));
		result.add(new PanelSetting(I18n.format("msg.outputText"), DISPLAY_SLOT_3));
		return result;
	}
}
