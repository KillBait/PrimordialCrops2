package KillBait.PrimordialCrops2.Blocks.Machines.Furnace;

import KillBait.PrimordialCrops2.Utils.LogHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.SlotItemHandler;

import java.awt.*;

public class FurnaceContainer extends Container{

	private FurnaceTileEntity furnaceTE;
	private int[] cachedFields;

	private Point slotcoord_input = new Point(56, 17);
	private Point slotcoord_fuel = new Point(56, 53);
	private Point slotcoord_output = new Point(116, 35);
	private Point slotcoord_catalyst = new Point(21, 35);

	private static final int TOTAL_CATALYST_SLOTS = 1;
	private static final int TOTAL_FUEL_SLOTS = 1;
	private static final int TOTAL_INPUT_SLOTS = 1;
	private static final int TOTAL_OUTPUT_SLOTS = 1;

	private static final int FURNACE_SLOT_INDEX_START = 0;
	private static final int FURNACE_SLOT_TOTAL = TOTAL_CATALYST_SLOTS + TOTAL_FUEL_SLOTS + TOTAL_INPUT_SLOTS + TOTAL_OUTPUT_SLOTS;

	private static final int PLAYER_SLOT_INDEX_START = FURNACE_SLOT_TOTAL;
	private static final int PLAYER_SLOT_TOTAL = 36;

	private static final int CATALYST_SLOT_INDEX_START = 0;
	private static final int FUEL_SLOT_INDEX_START = CATALYST_SLOT_INDEX_START + TOTAL_CATALYST_SLOTS;
	private static final int INPUT_SLOT_INDEX_START = FUEL_SLOT_INDEX_START + TOTAL_FUEL_SLOTS;
	//public static final int OUTPT_SLOT_INDEX_START = INPUT_SLOT_INDEX_START + TOTAL_OUTPUT_SLOTS;

	public FurnaceContainer(IInventory playerInventory, FurnaceTileEntity furnaceTile) {

		this.furnaceTE = furnaceTile;
		addMySlots();
		addPlayerSlots(playerInventory);
	}

	private void addMySlots() {

		// Add our own slots
		int slotIndex = 0;

		// for the GUI we dont care about sided inventory or if a item is valid for a slot, the ItemHandler
		// Capability in the TileEntity will do that for us. we just need 4 generic slots.

		//meh... hacky way to get the furnace ItemStackHandler

		addSlotToContainer(new SlotItemHandler(this.furnaceTE.furnaceItemStackHandler, slotIndex++, slotcoord_catalyst.x, slotcoord_catalyst.y));
		addSlotToContainer(new SlotItemHandler(this.furnaceTE.furnaceItemStackHandler, slotIndex++, slotcoord_fuel.x, slotcoord_fuel.y));
		addSlotToContainer(new SlotItemHandler(this.furnaceTE.furnaceItemStackHandler, slotIndex++, slotcoord_input.x, slotcoord_input.y));
		addSlotToContainer(new SlotItemHandler(this.furnaceTE.furnaceItemStackHandler, slotIndex, slotcoord_output.x, slotcoord_output.y));
	}

	private void addPlayerSlots(IInventory playerInventory) {

		int slot = 0;

		// Slots for the hotbar
		for (int row = 0; row < 9; ++row) {
			//int slot = row;
			int x = 8 + (row * 18);
			int y = 72 + 70;
			this.addSlotToContainer(new Slot(playerInventory, slot++, x, y));
		}

		// Slots for the main inventory
		for (int row = 0; row < 3; ++row) {
			for (int col = 0; col < 9; ++col) {
				//int slot = 9 + row * 9 + col;
				int x = 8 + (col * 18);
				int y = (row * 18) + 84;
				this.addSlotToContainer(new Slot(playerInventory, slot++, x, y));
			}
		}


	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {

		Slot sourceSlot = inventorySlots.get(index);
		if (sourceSlot == null || !sourceSlot.getHasStack()) return ItemStack.EMPTY;
		ItemStack sourceStack = sourceSlot.getStack();
		ItemStack sourceStackCopy = sourceStack.copy();

		if (index >= FURNACE_SLOT_TOTAL && index < FURNACE_SLOT_TOTAL + PLAYER_SLOT_TOTAL) {
			//LogHelper.info("Transfer from player slot " + index + " type " + sourceSlot);
			if (FurnaceTileEntity.isItemValidForCatalystSlot(sourceStack)) {
				//LogHelper.info("Catalyst item");
				if (!mergeItemStack(sourceStack, FURNACE_SLOT_INDEX_START, FURNACE_SLOT_INDEX_START + TOTAL_CATALYST_SLOTS, false)) {
					//LogHelper.info("Unaable to move stack");
					return ItemStack.EMPTY;
				}
			}else if (FurnaceTileEntity.getItemBurnTime(sourceStack) > 0) {
				//LogHelper.info("Burable item");
				if (!mergeItemStack(sourceStack, FUEL_SLOT_INDEX_START, FUEL_SLOT_INDEX_START + TOTAL_FUEL_SLOTS, false)) {
					return ItemStack.EMPTY;
				}
			}else if (!FurnaceTileEntity.getSmeltingResult(sourceStack).isEmpty()) {
				//LogHelper.info("Smeltable item");
				if (!mergeItemStack(sourceStack, INPUT_SLOT_INDEX_START, INPUT_SLOT_INDEX_START + TOTAL_INPUT_SLOTS, false)) {
					return ItemStack.EMPTY;
				}
			}else{
				//LogHelper.info("none catalyst/burnable/smeltable");
				return ItemStack.EMPTY;
			}

		} else if (index >= FURNACE_SLOT_INDEX_START && index < FURNACE_SLOT_INDEX_START + FURNACE_SLOT_TOTAL) {
			//LogHelper.info("Transfer from furnace slot " + index  + " type " + sourceSlot);
			if (!mergeItemStack(sourceStack, PLAYER_SLOT_INDEX_START, PLAYER_SLOT_INDEX_START + PLAYER_SLOT_TOTAL, false)) {
				return ItemStack.EMPTY;
			}

		} else {
			LogHelper.error("Invalid Slot Index " + index + " in FurnaceContainer");
			return ItemStack.EMPTY;
		}

		if (sourceStack.isEmpty()) {
			sourceSlot.putStack(ItemStack.EMPTY);
		} else {
			sourceSlot.onSlotChanged();
		}

		return sourceStackCopy;
	}

	@Override
	public void detectAndSendChanges() {

		super.detectAndSendChanges();

		boolean allFieldsHaveChanged = false;
		boolean fieldHasChanged[] = new boolean[this.furnaceTE.getFieldCount()];
		if (cachedFields == null) {
			cachedFields = new int[this.furnaceTE.getFieldCount()];
			allFieldsHaveChanged = true;
		}
		for (int i = 0; i < cachedFields.length; ++i) {
			if (allFieldsHaveChanged || cachedFields[i] != this.furnaceTE.getField(i)) {
				cachedFields[i] = this.furnaceTE.getField(i);
				fieldHasChanged[i] = true;
			}
		}

		// go through the list of listeners (players using this container) and update them if necessary
		for (IContainerListener listener : this.listeners) {
			for (int fieldID = 0; fieldID < this.furnaceTE.getFieldCount(); ++fieldID) {
				if (fieldHasChanged[fieldID]) {
					// Note that although sendProgressBarUpdate takes 2 ints on a server these are truncated to shorts
					listener.sendProgressBarUpdate(this, fieldID, cachedFields[fieldID]);
				}
			}
		}
	}

	// Called when a progress bar update is received from the server. The two values (id and data) are the same two
	// values given to sendProgressBarUpdate.  In this case we are using fields so we just pass them to the tileEntity.
	@SideOnly(Side.CLIENT)
	@Override
	public void updateProgressBar(int id, int data) {
		this.furnaceTE.setField(id, data);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return this.furnaceTE.canInteractWith(playerIn);
	}
}
