package com.zuxelus.energycontrol;

import com.zuxelus.energycontrol.blocks.BlockDamages;
import com.zuxelus.energycontrol.config.ConfigHandler;
import com.zuxelus.energycontrol.containers.*;
import com.zuxelus.energycontrol.gui.*;
import com.zuxelus.energycontrol.items.ItemHelper;
import com.zuxelus.energycontrol.items.cards.ItemCardHolder;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.kits.ItemKitMain;
import com.zuxelus.energycontrol.renderers.*;
import com.zuxelus.energycontrol.tileentities.*;
import com.zuxelus.energycontrol.utils.SoundHelper;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends ServerProxy {
	@Override
	public void loadConfig(FMLPreInitializationEvent event) {
		EnergyControl.config = new ConfigHandler();
		MinecraftForge.EVENT_BUS.register(EnergyControl.config);
		EnergyControl.config.init(event.getSuggestedConfigurationFile());
	}

	@Override
	public void registerSpecialRenderers() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityThermo.class, new TEThermoRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRemoteThermo.class, new TERemoteThermoRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityInfoPanel.class, new TileEntityInfoPanelRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityInfoPanelExtender.class, new TEInfoPanelExtenderRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAdvancedInfoPanel.class, new TEAdvancedInfoPanelRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAdvancedInfoPanelExtender.class, new TEAdvancedInfoPanelExtenderRenderer());
		int modelId = RenderingRegistry.getNextAvailableRenderId();
		EnergyControl.instance.modelId = modelId;
		RenderingRegistry.registerBlockHandler(new MainBlockRenderer(modelId));
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch (ID)
		{
		case BlockDamages.GUI_PORTABLE_PANEL:
			return new GuiPortablePanel(new ContainerPortablePanel(player));
		case BlockDamages.GUI_CARD_HOLDER:
			if (player.getCurrentEquippedItem().getItem() instanceof ItemCardHolder)
				return new GuiCardHolder(player);
		}
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		switch (ID) {
		case BlockDamages.DAMAGE_THERMAL_MONITOR:
			if (tileEntity instanceof TileEntityThermo)
				return new GuiThermalMonitor((TileEntityThermo) tileEntity);
			break;
		case BlockDamages.DAMAGE_HOWLER_ALARM:
			if (tileEntity instanceof TileEntityHowlerAlarm)
				return new GuiHowlerAlarm((TileEntityHowlerAlarm) tileEntity);
		case BlockDamages.DAMAGE_INDUSTRIAL_ALARM:
			if (tileEntity instanceof TileEntityIndustrialAlarm)
				return new GuiIndustrialAlarm((TileEntityIndustrialAlarm) tileEntity);
			break;
		case BlockDamages.DAMAGE_INFO_PANEL:
			if (tileEntity instanceof TileEntityInfoPanel)
				return new GuiInfoPanel(new ContainerInfoPanel(player, (TileEntityInfoPanel) tileEntity));
			break;
		case BlockDamages.DAMAGE_ADVANCED_PANEL:
			if (tileEntity instanceof TileEntityAdvancedInfoPanel)
				return new GuiAdvancedInfoPanel(new ContainerAdvancedInfoPanel(player, (TileEntityAdvancedInfoPanel) tileEntity));
			break;
		case BlockDamages.DAMAGE_RANGE_TRIGGER:
			if (tileEntity instanceof TileEntityRangeTrigger)
				return new GuiRangeTrigger(new ContainerRangeTrigger(player, (TileEntityRangeTrigger) tileEntity));
			break;
		case BlockDamages.DAMAGE_REMOTE_THERMO:
			if (tileEntity instanceof TileEntityRemoteThermo)
				return new GuiRemoteThermo(new ContainerRemoteThermo(player, (TileEntityRemoteThermo) tileEntity));
			break;
		case BlockDamages.DAMAGE_AVERAGE_COUNTER:
			if (tileEntity instanceof TileEntityAverageCounter)
				return new GuiAverageCounter(new ContainerAverageCounter(player, (TileEntityAverageCounter) tileEntity));
			break;
		case BlockDamages.DAMAGE_ENERGY_COUNTER:
			if (tileEntity instanceof TileEntityEnergyCounter)
				return new GuiEnergyCounter(new ContainerEnergyCounter(player, (TileEntityEnergyCounter) tileEntity));
			break;
		case BlockDamages.GUI_KIT_ASSEMBER:
			if (tileEntity instanceof TileEntityKitAssembler)
				return new GuiKitAssembler(new ContainerKitAssembler(player, (TileEntityKitAssembler) tileEntity));
			break;
		}
		return null;
	}

	@Override
	public void importSound() {
		SoundHelper.importSound();
	}
}