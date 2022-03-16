package com.zuxelus.energycontrol.renderers;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.tileentities.Screen;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class TileEntityInfoPanelRenderer extends TileEntityRenderer<TileEntityInfoPanel> {
	private static int[][] sides = new int[][] { { 3, 2, 1, 0, 5, 4 }, { 2, 3, 1, 0, 4, 5 }, { 4, 5, 1, 0, 3, 2 },
		{ 5 ,4, 1, 0, 2, 3 }, { 1, 0, 3, 2, 4, 5 }, { 0, 1, 2, 3, 4, 5 } };
	private static final ResourceLocation[] TEXTUREOFF;
	private static final ResourceLocation[] TEXTUREON;
	private static final CubeRenderer[] model;

	static {
		TEXTUREOFF = new ResourceLocation[16];
		TEXTUREON = new ResourceLocation[16];
		for (int i = 0; i < 16; i++) {
			TEXTUREOFF[i] = new ResourceLocation(
					EnergyControl.MODID + String.format(":textures/block/info_panel/off/all%d.png", i));
			TEXTUREON[i] = new ResourceLocation(
					EnergyControl.MODID + String.format(":textures/block/info_panel/on/all%d.png", i));
		}
		model = new CubeRenderer[16];
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				model[i * 4 + j] = new CubeRenderer(i * 32 + 64, j * 32 + 64);
	}

	private static String implodeArray(String[] inputArray, String glueString) {
		String output = "";
		if (inputArray.length > 0) {
			StringBuilder sb = new StringBuilder();
			for (String s : inputArray) {
				if (s == null || s.isEmpty())
					continue;
				sb.append(glueString);
				sb.append(s);
			}
			output = sb.toString();
			if (output.length() > 1)
				output = output.substring(1);
		}
		return output;
	}

	public static int[] getBlockLight(TileEntityFacing te) {
		int[] light = new int[6];
		light[sides[te.getFacing().get3DDataValue()][0]] = WorldRenderer.getLightColor(te.getLevel(), te.getBlockPos().relative(Direction.DOWN));
		light[sides[te.getFacing().get3DDataValue()][1]] = WorldRenderer.getLightColor(te.getLevel(), te.getBlockPos().relative(Direction.UP));
		light[sides[te.getFacing().get3DDataValue()][2]] = WorldRenderer.getLightColor(te.getLevel(), te.getBlockPos().relative(Direction.WEST));
		light[sides[te.getFacing().get3DDataValue()][3]] = WorldRenderer.getLightColor(te.getLevel(), te.getBlockPos().relative(Direction.EAST));
		light[sides[te.getFacing().get3DDataValue()][4]] = WorldRenderer.getLightColor(te.getLevel(), te.getBlockPos().relative(Direction.NORTH));
		light[sides[te.getFacing().get3DDataValue()][5]] = WorldRenderer.getLightColor(te.getLevel(), te.getBlockPos().relative(Direction.SOUTH));
		return light;
	}

	public TileEntityInfoPanelRenderer(TileEntityRendererDispatcher te) {
		super(te);
	}

	@Override
	public void render(TileEntityInfoPanel te, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		matrixStack.pushPose();
		int[] light = getBlockLight(te);
		switch (te.getFacing()) {
		case UP:
			break;
		case NORTH:
			matrixStack.mulPose(Vector3f.XP.rotationDegrees(-90));
			matrixStack.translate(0.0F, -1.0F, 0.0F);
			break;
		case SOUTH:
			matrixStack.mulPose(Vector3f.XP.rotationDegrees(90));
			matrixStack.translate(0.0F, 0.0F, -1.0F);
			break;
		case DOWN:
			matrixStack.mulPose(Vector3f.XP.rotationDegrees(180));
			matrixStack.translate(0.0F, -1.0F, -1.0F);
			break;
		case WEST:
			matrixStack.mulPose(Vector3f.ZP.rotationDegrees(90));
			matrixStack.translate(0.0F, -1.0F, 0.0F);
			break;
		case EAST:
			matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-90));
			matrixStack.translate(-1.0F, 0.0F, 0.0F);
			break;
		}

		int color = 2;
		if (te.getColored()) {
			color = te.getColorBackground();
			if (color > 15 || color < 0)
				color = 2;
		}
		IVertexBuilder vertexBuilder;
		if (te.getPowered())
			vertexBuilder = buffer.getBuffer(RenderType.entitySolid(TEXTUREON[color]));
		else
			vertexBuilder = buffer.getBuffer(RenderType.entitySolid(TEXTUREOFF[color]));
		model[te.findTexture()].render(matrixStack, vertexBuilder, light, combinedOverlay);
		if (te.getPowered()) {
			List<PanelString> joinedData = te.getPanelStringList(false, te.getShowLabels());
			drawText(te, joinedData, matrixStack, buffer, combinedLight);
		}
		matrixStack.popPose();
	}

	private void drawText(TileEntityInfoPanel panel, List<PanelString> joinedData, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight) {
		Screen screen = panel.getScreen();
		BlockPos pos = panel.getBlockPos();
		float displayWidth = 1 - 2F / 16;
		float displayHeight = 1 - 2F / 16;
		float dx = 0; float dy = 0; float dz = 0;
		if (screen != null) {
			switch (panel.getFacing()) {
			case UP:
				switch (panel.getRotation()) {
				case NORTH:
					dz = (pos.getZ() - screen.maxZ - screen.minZ + pos.getZ());
					dy = pos.getX() - screen.maxX - screen.minX + pos.getX();
					displayWidth += screen.maxX - screen.minX;
					displayHeight += screen.maxZ - screen.minZ;
					break;
				case SOUTH:
					dz = (pos.getZ() - screen.maxZ - screen.minZ + pos.getZ());
					dy = pos.getX() - screen.maxX - screen.minX + pos.getX();
					displayWidth += screen.maxX - screen.minX;
					displayHeight += screen.maxZ - screen.minZ;
					break;
				case EAST:
					dz = (pos.getZ() - screen.maxZ - screen.minZ + pos.getZ());
					dy = pos.getX() - screen.maxX - screen.minX + pos.getX();
					displayWidth += screen.maxZ - screen.minZ;
					displayHeight += screen.maxX - screen.minX;
					break;
				case WEST:
					dz = (pos.getZ() - screen.maxZ - screen.minZ + pos.getZ());
					dy = pos.getX() - screen.maxX - screen.minX + pos.getX();
					displayWidth += screen.maxZ - screen.minZ;
					displayHeight += screen.maxX - screen.minX;
					break;
				case DOWN:
					break;
				case UP:
					break;
				}
				break;
			case NORTH:
				dz = (pos.getY() - screen.maxY - screen.minY + pos.getY());
				dy = pos.getX() - screen.maxX - screen.minX + pos.getX();
				displayWidth += screen.maxX - screen.minX;
				displayHeight += screen.maxY - screen.minY;
				break;
			case SOUTH:
				dz = - (pos.getY() - screen.maxY - screen.minY + pos.getY());
				dy = pos.getX() - screen.maxX - screen.minX + pos.getX();
				displayWidth += screen.maxX - screen.minX;
				displayHeight += screen.maxY - screen.minY;
				break;
			case DOWN:
 				break;
			case WEST:
				dz = pos.getZ() - screen.maxZ + pos.getZ() - screen.minZ;
				dy = (pos.getY() - screen.maxY - screen.minY + pos.getY());
				displayWidth += screen.maxZ - screen.minZ;
				displayHeight += screen.maxY - screen.minY;
				break;
			case EAST:
				dz = pos.getZ() - screen.maxZ + pos.getZ() - screen.minZ;
				dy = - (pos.getY() - screen.maxY - screen.minY + pos.getY());
				displayWidth += screen.maxZ - screen.minZ;
				displayHeight += screen.maxY - screen.minY;
				break;
			}
		}

		matrixStack.translate(0.5F - dy / 2, 1.01F - dx / 2 , 0.5F - dz / 2);
		matrixStack.mulPose(Vector3f.XP.rotationDegrees(-90));
		switch(panel.getRotation())
		{
		case UP:
			break;
		case NORTH:
			matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180));
			break;
		case SOUTH:
			break;
		case DOWN:
			break;
		case WEST:
			matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-90));
			break;
		case EAST:
			matrixStack.mulPose(Vector3f.ZP.rotationDegrees(90));
			break;
		}

		if (panel.isTouchCard() || panel.hasBars()) {
			matrixStack.mulPose(Vector3f.YP.rotationDegrees(180));
			panel.renderImage(renderer.textureManager, displayWidth, displayHeight, matrixStack);
			matrixStack.mulPose(Vector3f.YP.rotationDegrees(180));
		}
		if (joinedData != null) {
			matrixStack.translate(0, 0, 0.0002F);
			int colorHex = 0x000000;
			if (panel.getColored())
				colorHex = panel.getColorText();
			renderText(joinedData, displayWidth, displayHeight, colorHex, matrixStack, renderer.getFont());
		}
	}

	public static void renderText(List<PanelString> joinedData, float displayWidth, float displayHeight, int colorHex, MatrixStack matrixStack, FontRenderer fontRenderer) {
		int maxWidth = 1;
		for (PanelString panelString : joinedData) {
			String currentString = implodeArray(new String[] { panelString.textLeft, panelString.textCenter, panelString.textRight }, " ");
			maxWidth = Math.max(fontRenderer.width(currentString), maxWidth);
		}
		maxWidth += 4;

		int lineHeight = fontRenderer.lineHeight + 2;
		int requiredHeight = lineHeight * joinedData.size();
		float scaleX = displayWidth / maxWidth;
		float scaleY = displayHeight / requiredHeight;
		float scale = Math.min(scaleX, scaleY);
		matrixStack.scale(scale, -scale, scale);
		int realHeight = (int) Math.floor(displayHeight / scale);
		int realWidth = (int) Math.floor(displayWidth / scale);
		int offsetX;
		int offsetY;
		if (scaleX < scaleY) {
			offsetX = 2;
			offsetY = (realHeight - requiredHeight) / 2;
		} else {
			offsetX = (realWidth - maxWidth) / 2 + 2;
			offsetY = 0;
		}

		int row = 0;
		for (PanelString panelString : joinedData) {
			if (panelString.textLeft != null)
				fontRenderer.draw(matrixStack, panelString.textLeft, offsetX - realWidth / 2,
					offsetY - realHeight / 2 + row * lineHeight, panelString.colorLeft != 0 ? panelString.colorLeft : colorHex);
			if (panelString.textCenter != null)
				fontRenderer.draw(matrixStack, panelString.textCenter, -fontRenderer.width(panelString.textCenter) / 2,
					offsetY - realHeight / 2 + row * lineHeight, panelString.colorCenter != 0 ? panelString.colorCenter : colorHex);
			if (panelString.textRight != null)
				fontRenderer.draw(matrixStack, panelString.textRight, realWidth / 2 - fontRenderer.width(panelString.textRight),
					offsetY - realHeight / 2 + row * lineHeight, panelString.colorRight != 0 ? panelString.colorRight : colorHex);
			row++;
		}
	}
}
