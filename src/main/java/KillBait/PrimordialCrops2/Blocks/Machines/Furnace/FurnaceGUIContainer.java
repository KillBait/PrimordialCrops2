package KillBait.PrimordialCrops2.Blocks.Machines.Furnace;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;

import static KillBait.PrimordialCrops2.Info.MODID;

/**
 * Created by Jon on 20/10/2016.
 */

public class FurnaceGUIContainer extends GuiContainer {

	private static final int WIDTH = 176;
	private static final int HEIGHT = 166;

	private static final ResourceLocation background = new ResourceLocation(MODID, "textures/blocks/gui/Furnace.png");

	public FurnaceGUIContainer(FurnaceTileEntity tileEntity, FurnaceContainer container) {
		super(container);

		xSize = WIDTH;
		ySize = HEIGHT;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		mc.getTextureManager().bindTexture(background);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		String s = "aaaaaaaa";//PrimordialFurnace..getDisplayName().getUnformattedText();
		this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
		this.fontRendererObj.drawString("bbbbbbbbb"/*this.playerInventory.getDisplayName().getUnformattedText()*/, 8, this.ySize - 96 + 2, 4210752);
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
