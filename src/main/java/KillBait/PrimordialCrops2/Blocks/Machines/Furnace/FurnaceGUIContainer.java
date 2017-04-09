package KillBait.PrimordialCrops2.Blocks.Machines.Furnace;

import KillBait.PrimordialCrops2.Utils.LogHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;

import static KillBait.PrimordialCrops2.Info.MODID;

/**
 * Created by Jon on 20/10/2016.
 */


	// TODO Change catalyst remaining bar to colour of essence instead of gradient?
	// TODO Add mouseover tooltips to GUI progress bars

@SideOnly(Side.CLIENT)
public class FurnaceGUIContainer extends GuiContainer {

	private static final int WIDTH = 176;
	private static final int HEIGHT = 166;
	private FurnaceTileEntity furnaceTE;
	private InventoryPlayer playerInv;
	private static final ResourceLocation background = new ResourceLocation(MODID, "textures/blocks/gui/Furnace.png");

	private Point progbar_cord = new Point(79, 35);
	private Point progbar_uv = new Point(176, 14);
	private Point progbar_size = new Point(24, 17);

	private Point flame_cord = new Point(57, 37);
	private Point flame_uv = new Point(176, 0);
	private Point flame_size = new Point(14, 14);

	private Point catalyst_cord = new Point(40, 35);
	private Point catalyst_uv = new Point(177, 32);
	private Point catalyst_size = new Point(2, 16);


	public FurnaceGUIContainer(InventoryPlayer pInv, FurnaceTileEntity furnaceTileEntity) {
		super(new FurnaceContainer(pInv, furnaceTileEntity));
		this.xSize = WIDTH;
		this.ySize = HEIGHT;
		this.furnaceTE = furnaceTileEntity;
		this.playerInv = pInv;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(background);

		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

		// cook progress is a double between 0 (0%) and 1 (100%)
		// catalyst remaining is a double between 100 (full) to 0 (Empty)
		// fuel remaining is a double between 100 (full) to 0 (Empty)
		double cookProgress = this.furnaceTE.fractionOfCookTimeComplete();
		// draw the cook progress bar
		drawTexturedModalRect(guiLeft + progbar_cord.x, guiTop + progbar_cord.y, progbar_uv.x, progbar_uv.y, (int) (cookProgress * progbar_size.x), progbar_size.y);

		//int yOffset = (int) (flame_size.y * cookProgress);

		double burnRemaining = this.furnaceTE.fractionOfFuelRemaining();
		int yOffset = (int)((1.0 - burnRemaining) * flame_size.y);

		drawTexturedModalRect(guiLeft + flame_cord.x, guiTop + flame_cord.y + yOffset, flame_uv.x, flame_uv.y + yOffset, flame_size.x, flame_size.y - yOffset);

		double catalystRemaining = this.furnaceTE.fractionOfCatalystRemaining();
		int cyOffset = (int) ((1.0 - catalystRemaining) * catalyst_size.y);

		drawTexturedModalRect(guiLeft + catalyst_cord.x, guiTop + catalyst_cord.y + cyOffset, catalyst_uv.x, catalyst_uv.y + cyOffset, catalyst_size.x, catalyst_size.y - cyOffset);



	}

	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String s = furnaceTE.getDisplayName().getUnformattedText();
		this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6,  Color.darkGray.getRGB());
		this.fontRenderer.drawString(this.playerInv.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, Color.darkGray.getRGB());

	}
}
