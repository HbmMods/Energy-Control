package com.zuxelus.energycontrol.items.cards;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.ICardReader;
import com.zuxelus.energycontrol.api.ITouchAction;
import com.zuxelus.energycontrol.api.PanelSetting;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.utils.StringUtils;

import appeng.api.AEApi;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.parts.IPart;
import appeng.api.storage.ICellInventory;
import appeng.api.storage.ICellInventoryHandler;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.MEMonitorHandler;
import appeng.api.storage.StorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.util.IReadOnlyCollection;
import appeng.me.helpers.AENetworkProxy;
import appeng.parts.CableBusContainer;
import appeng.parts.reporting.PartStorageMonitor;
import appeng.tile.networking.TileCableBus;
import appeng.tile.storage.TileChest;
import appeng.tile.storage.TileDrive;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemCardAppEngInv extends ItemCardBase implements ITouchAction {

	public ItemCardAppEngInv() {
		super(ItemCardType.CARD_APPENG_INV, "card_app_eng_inv");
	}

	@Override
	public CardState update(World world, ICardReader reader, int range, int x, int y, int z) {
		ChunkCoordinates target = reader.getTarget();
		if (target == null)
			return CardState.NO_TARGET;

		ArrayList<ItemStack> stacks = reader.getItemStackList(true);
		if (stacks.size() < 1)
			return CardState.OK;

		IReadOnlyCollection<IGridNode> gridList;

		TileEntity te = world.getTileEntity(target.posX, target.posY, target.posZ);
		if (te instanceof TileCableBus) {
			CableBusContainer cb = ((TileCableBus) te).getCableBus();
			if (cb != null)
				for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
					IPart part = cb.getPart(side);
					if (part instanceof PartStorageMonitor) {
						PartStorageMonitor monitor = (PartStorageMonitor) part;
						AENetworkProxy proxy = monitor.getProxy();
						if (proxy != null && proxy.getNode() != null && proxy.getNode().getGrid() != null) {
							gridList = proxy.getNode().getGrid().getNodes();
							if (gridList != null)
								for (IGridNode node : gridList) {
									IGridHost host = node.getMachine();
									if (host instanceof TileChest) {
										ItemStack stack = ((TileChest) host).getInternalInventory().getStackInSlot(1);
										updateValues(stack, stacks);
									} else if (host instanceof TileDrive) {
										for (int i = 0; i < ((TileDrive) host).getInternalInventory().getSizeInventory(); i++) {
											ItemStack stack = ((TileDrive) host).getInternalInventory().getStackInSlot(i);
											updateValues(stack, stacks);
										}
									}
								}
						}
						reader.setItemStackList(stacks);
						return CardState.OK;
					}
				}
		}
		reader.setItemStackList(stacks);
		return CardState.NO_TARGET;
	}

	private void updateValues(ItemStack cell, ArrayList<ItemStack> stacks) {
		if (cell == null)
			return;
		for (StorageChannel channel : StorageChannel.values()) {
			IMEInventory handler = AEApi.instance().registries().cell().getCellInventory(cell, null, channel);
			if (handler instanceof IMEInventoryHandler) {
				MEMonitorHandler<?> monitor = new MEMonitorHandler((IMEInventoryHandler) handler);
				for (IAEStack st : monitor.getStorageList())
					if (st instanceof IAEItemStack)
						for (ItemStack stack : stacks)
							if (stack.isItemEqual(((IAEItemStack) st).getItemStack()))
								stack.stackSize = stack.stackSize + (int) st.getStackSize();
			}
		}
	}

	@Override
	public List<PanelString> getStringData(int settings, ICardReader reader, boolean isServer, boolean showLabels) {
		List<PanelString> result = reader.getTitleList();
		NBTTagList list = reader.getTagList("Items", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound stackTag = list.getCompoundTagAt(i);
			ItemStack stack = ItemStack.loadItemStackFromNBT(stackTag);
			result.add(new PanelString(String.format("%s %d", StringUtils.getItemName(stack), stack.stackSize - 1)));
		}
		return result;
	}

	@Override
	public List<PanelSetting> getSettingsList() {
		return null;
	}

	@Override
	public boolean enableTouch(ItemStack stack) {
		return true;
	}

	@Override
	public boolean runTouchAction(World world, ICardReader reader, ItemStack current) {
		NBTTagList list = reader.getTagList("Items", Constants.NBT.TAG_COMPOUND);
		if (current == null) {
			if (list.tagCount() > 0) {
				list.removeTag(list.tagCount() - 1);
				reader.setTag("Items", list);
				return true;
			}
			return false;
		}
		ArrayList<ItemStack> stacks = reader.getItemStackList(true);
		for (ItemStack stack : stacks)
			if (stack.isItemEqual(current))
				return false;
		ItemStack item = current.copy();
		item.stackSize = 1;
		list.appendTag(item.writeToNBT(new NBTTagCompound()));
		reader.setTag("Items", list);
		return true;
	}

	@Override
	public void renderImage(TextureManager manager, ICardReader reader) { }
}
