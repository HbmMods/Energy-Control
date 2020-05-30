package com.zuxelus.energycontrol;

import com.zuxelus.energycontrol.blocks.BlockDamages;
import com.zuxelus.energycontrol.config.ConfigHandler;
import com.zuxelus.energycontrol.containers.*;
import com.zuxelus.energycontrol.items.cards.ItemCardHolder;
import com.zuxelus.energycontrol.tileentities.*;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ServerProxy implements IGuiHandler {

	public void loadConfig(FMLPreInitializationEvent event) {
		EnergyControl.config = new ConfigHandler();
		EnergyControl.config.init(event.getSuggestedConfigurationFile());
	}

	public void registerSpecialRenderers() { }

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch (ID) {
		case BlockDamages.GUI_PORTABLE_PANEL:
			return new ContainerPortablePanel(player);
		case BlockDamages.GUI_CARD_HOLDER:
			if (player.getCurrentEquippedItem().getItem() instanceof ItemCardHolder)
				return new ContainerCardHolder(player);
		}
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		switch (ID) {
		case BlockDamages.DAMAGE_INFO_PANEL:
			return new ContainerInfoPanel(player, (TileEntityInfoPanel) tileEntity);
		case BlockDamages.DAMAGE_ADVANCED_PANEL:
			return new ContainerAdvancedInfoPanel(player, (TileEntityAdvancedInfoPanel) tileEntity);
		case BlockDamages.DAMAGE_RANGE_TRIGGER:
			return new ContainerRangeTrigger(player, (TileEntityRangeTrigger) tileEntity);
		case BlockDamages.DAMAGE_REMOTE_THERMO:
			return new ContainerRemoteThermo(player, (TileEntityRemoteThermo) tileEntity);
		case BlockDamages.DAMAGE_AVERAGE_COUNTER:
			return new ContainerAverageCounter(player, (TileEntityAverageCounter) tileEntity);
		case BlockDamages.DAMAGE_ENERGY_COUNTER:
			return new ContainerEnergyCounter(player, (TileEntityEnergyCounter) tileEntity);
		case BlockDamages.GUI_KIT_ASSEMBER:
			return new ContainerKitAssembler(player, (TileEntityKitAssembler) tileEntity);
		default:
			return null;
		}
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}

	public void registerEventHandlers() {
		FMLCommonHandler.instance().bus().register(ServerTickHandler.instance);
	}

	public void importSound() { }
}