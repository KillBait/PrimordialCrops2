package KillBait.PrimordialCrops2.Blocks.Machines.Furnace;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
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

	//private double test = 0;


	public FurnaceGUIContainer(InventoryPlayer pInv, FurnaceTileEntity furnaceTileEntity) {
		super(new FurnaceContainer(pInv, furnaceTileEntity));
		this.xSize = WIDTH;
		this.ySize = HEIGHT;
		this.furnaceTE = furnaceTileEntity;
		this.playerInv = pInv;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {

		mc.getTextureManager().bindTexture(background);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		//if (test >= 1) test = 0;
		//test = test + 0.01;

		// cook progress is a double between 0 (0%) and 1 (100%)
		// catalyst remaining is a double between 100 (full) to 0 (Empty)
		// fuel remaining is a double between 100 (full) to 0 (Empty)
		double cookProgress = furnaceTE.fractionOfCookTimeComplete();
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

	/*private static final ResourceLocation FURNACE_GUI_TEXTURES = new ResourceLocation("textures/gui/container/furnace.png");

	private final InventoryPlayer playerInventory;
	private final IInventory tileFurnace;

	public GuiFurnace(InventoryPlayer playerInv, IInventory furnaceInv)
	{
		super(new ContainerFurnace(playerInv, furnaceInv));
		this.playerInventory = playerInv;
		this.tileFurnace = furnaceInv;
	}*/

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	/*protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		String s = this.tileFurnace.getDisplayName().getUnformattedText();
		this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
		this.fontRendererObj.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
	}


	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(FURNACE_GUI_TEXTURES);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);

		if (TileEntityFurnace.isBurning(this.tileFurnace))
		{
			int k = this.getBurnLeftScaled(13);
			this.drawTexturedModalRect(i + 56, j + 36 + 12 - k, 176, 12 - k, 14, k + 1);
		}

		int l = this.getCookProgressScaled(24);
		this.drawTexturedModalRect(i + 79, j + 34, 176, 14, l + 1, 16);
	}

	private int getCookProgressScaled(int pixels)
	{
		int i = this.tileFurnace.getField(2);
		int j = this.tileFurnace.getField(3);
		return j != 0 && i != 0 ? i * pixels / j : 0;
	}

	private int getBurnLeftScaled(int pixels)
	{
		int i = this.tileFurnace.getField(1);

		if (i == 0)
		{
			i = 200;
		}

		return this.tileFurnace.getField(0) * pixels / i;
	}*/

}
