package com.zuxelus.energycontrol.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.containers.ContainerTimer;
import com.zuxelus.energycontrol.gui.controls.CompactButton;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityTimer;
import com.zuxelus.zlib.gui.GuiContainerBase;

import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiTimer extends GuiContainerBase<ContainerTimer> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyControl.MODID, "textures/gui/gui_timer.png");
	private TileEntityTimer timer;
	private TextFieldWidget textboxTimer = null;
	private boolean lastIsWorking;

	public GuiTimer(ContainerTimer container, PlayerInventory inventory, ITextComponent title) {
		super(container, inventory, title, TEXTURE);
		imageWidth = 100;
		imageHeight = 136;
		this.timer = container.te;
		lastIsWorking = timer.getIsWorking();
	}

	@Override
	public void init() {
		super.init();
		lastIsWorking = timer.getIsWorking();

		addButton(new CompactButton(0, leftPos + 14, topPos + 50, 34, 12, new StringTextComponent("+1"), (button) -> { actionPerformed(button); }));
		addButton(new CompactButton(1, leftPos + 14, topPos + 64, 34, 12, new StringTextComponent("+10"), (button) -> { actionPerformed(button); }));
		addButton(new CompactButton(2, leftPos + 50, topPos + 50, 34, 12, new StringTextComponent("+100"), (button) -> { actionPerformed(button); }));
		addButton(new CompactButton(3, leftPos + 50, topPos + 64, 34, 12, new StringTextComponent("+1000"), (button) -> { actionPerformed(button); }));
		addButton(new CompactButton(4, leftPos + 14, topPos + 78, 70, 12, new StringTextComponent("+10000"), (button) -> { actionPerformed(button); }));

		addButton(new CompactButton(5, leftPos + 14, topPos + 36, 34, 12, new StringTextComponent("Reset"), (button) -> { actionPerformed(button); }));
		addButton(new CompactButton(6, leftPos + 50, topPos + 36, 34, 12, new StringTextComponent("Ticks"), (button) -> { actionPerformed(button); }));
		addButton(new CompactButton(7, leftPos + 14, topPos + 92, 70, 12,
				new StringTextComponent(timer.getInvertRedstone() ? "No Redstone" : "Redstone"), (button) -> { actionPerformed(button); }));
		addButton(new CompactButton(8, leftPos + 14, topPos + 106, 70, 12,
				new StringTextComponent(lastIsWorking ? "Stop" : "Start"), (button) -> { actionPerformed(button); }));

		updateCaptions(timer.getIsTicks());

		textboxTimer = addTextFieldWidget(20, 20, 58, 12, !lastIsWorking, timer.getTimeString());
	}

	private void updateCaptions(boolean isTicks) {
		buttons.get(0).setMessage(new StringTextComponent(isTicks ? "+1" : "+1s"));
		buttons.get(1).setMessage(new StringTextComponent(isTicks ? "+10" : "+30s"));
		buttons.get(2).setMessage(new StringTextComponent(isTicks ? "+100" : "+1m"));
		buttons.get(3).setMessage(new StringTextComponent(isTicks ? "+1000" : "+30m"));
		buttons.get(4).setMessage(new StringTextComponent(isTicks ? "+10000" : "+1h"));
		buttons.get(6).setMessage(new StringTextComponent(isTicks ? "Ticks" : "Time"));
	}

	@Override
	protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		super.renderBg(matrixStack, partialTicks, mouseX, mouseY);
		textboxTimer.renderButton(matrixStack, mouseX, mouseY, partialTicks);
	}

	@Override
	protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
		drawCenteredText(matrixStack, title, imageWidth, 6);
	}

	@Override
	public void tick() {
		super.tick();
		textboxTimer.tick();
		boolean isWorking = timer.getIsWorking();
		if (isWorking != lastIsWorking) {
			textboxTimer.setEditable(!isWorking);
			textboxTimer.changeFocus(!isWorking);
			buttons.get(8).setMessage(new StringTextComponent(isWorking ? "Stop" : "Start"));
			lastIsWorking = isWorking;
		}
		if (isWorking)
			textboxTimer.setValue(timer.getTimeString());
	}

	@Override
	public void onClose() {
		updateTime(0);
		super.onClose();
	}

	private void updateTime(int delta) {
		if (textboxTimer == null)
			return;
		int time = 0;
		try {
			String value = textboxTimer.getValue();
			if (timer.getIsTicks()) {
				if (!"".equals(value))
					time = Integer.parseInt(value);
			} else {
				String[] times = value.split(":");
				if (times.length == 2)
					time = (Integer.parseInt(times[0]) * 60 + Integer.parseInt(times[1])) * 20;
				if (times.length == 3)
					time = (Integer.parseInt(times[0]) * 3600 + Integer.parseInt(times[1]) * 60 + Integer.parseInt(times[2])) * 20;
			}
		} catch (NumberFormatException e) {	}
		time += delta;
		if (time < 0)
			time = 0;
		if (time >= 1000000)
			time = 1000000;
		if (timer.getTime() != time) {
			NetworkHelper.updateSeverTileEntity(timer.getBlockPos(), 1, time);
			timer.setTime(time);
		}
		textboxTimer.setValue(timer.getTimeString());
	}

	protected void actionPerformed(Button button) {
		int id = ((CompactButton) button).getId();
		boolean isTicks = timer.getIsTicks();
		switch(id) {
		case 0:
			updateTime(isTicks ? 1 : 1 * 20);
			break;
		case 1:
			updateTime(isTicks ? 10 : 30 * 20);
			break;
		case 2:
			updateTime(isTicks ? 100 : 60 * 20);
			break;
		case 3:
			updateTime(isTicks ? 1000 : 30 * 60 * 20);
			break;
		case 4:
			updateTime(isTicks ? 10000 : 60 * 60 * 20);
			break;
		case 5:
			NetworkHelper.updateSeverTileEntity(timer.getBlockPos(), 1, 0);
			timer.setTime(0);
			textboxTimer.setValue(timer.getTimeString());
			break;
		case 6:
			NetworkHelper.updateSeverTileEntity(timer.getBlockPos(), 4, isTicks ? 0 : 1);
			timer.setIsTicks(!isTicks);
			textboxTimer.setValue(timer.getTimeString());
			updateCaptions(!isTicks);
			break;
		case 7:
			boolean invertRedstone = timer.getInvertRedstone();
			NetworkHelper.updateSeverTileEntity(timer.getBlockPos(), 2, invertRedstone ? 0 : 1);
			timer.setInvertRedstone(!invertRedstone);
			buttons.get(7).setMessage(new StringTextComponent(!invertRedstone ? "No Redstone" : "Redstone"));
			break;
		case 8:
			updateTime(0);
			boolean isWorking = timer.getIsWorking();
			NetworkHelper.updateSeverTileEntity(timer.getBlockPos(), 3, isWorking ? 0 : 1);
			timer.setIsWorking(!isWorking);
			buttons.get(8).setMessage(new StringTextComponent(!isWorking ? "Stop" : "Start"));
			textboxTimer.setEditable(isWorking);
			textboxTimer.changeFocus(isWorking);
			break;
		}
	}
}
