package KillBait.PrimordialCrops2.Blocks.Machines.Furnace;

import KillBait.PrimordialCrops2.Utils.LogHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.awt.*;

/**
 * Created by Jon on 20/10/2016.
 */

// TODO switch to ItemHandler capabilities

public class FurnaceContainer extends Container{

	private FurnaceTileEntity furnaceTE;
	private int[] cachedFields;

	private Point slotcoord_input = new Point(56, 17);
	private Point slotcoord_fuel = new Point(56, 53);
	private Point slotcoord_output = new Point(116, 35);
	private Point slotcoord_catalyst = new Point(21, 35);

	public static final int TOTAL_CATALYST_SLOTS = 1;
	public static final int TOTAL_FUEL_SLOTS = 1;
	public static final int TOTAL_INPUT_SLOTS = 1;
	public static final int TOTAL_OUTPUT_SLOTS = 1;

	public static final int FURNACE_SLOT_INDEX_START = 0;
	public static final int FURNACE_SLOT_TOTAL = TOTAL_CATALYST_SLOTS + TOTAL_FUEL_SLOTS + TOTAL_INPUT_SLOTS + TOTAL_OUTPUT_SLOTS;

	public static final int PLAYER_SLOT_INDEX_START = FURNACE_SLOT_TOTAL;
	public static final int PLAYER_SLOT_TOTAL = 36;

	public static final int CATALYST_SLOT_INDEX_START = 0;
	public static final int FUEL_SLOT_INDEX_START = CATALYST_SLOT_INDEX_START + TOTAL_CATALYST_SLOTS;
	public static final int INPUT_SLOT_INDEX_START = FUEL_SLOT_INDEX_START + TOTAL_FUEL_SLOTS;
	public static final int OUTPT_SLOT_INDEX_START = INPUT_SLOT_INDEX_START + TOTAL_OUTPUT_SLOTS;

	public FurnaceContainer(IInventory playerInventory, FurnaceTileEntity furnaceTile) {

		this.furnaceTE = furnaceTile;
		addMySlots(playerInventory);
		addPlayerSlots(playerInventory);
	}

	private void addMySlots(IInventory playerInventory) {

		// Add our own slots
		int slotIndex = 0;
		addSlotToContainer(new SlotCatalyst(furnaceTE, slotIndex++, slotcoord_catalyst.x, slotcoord_catalyst.y));
		addSlotToContainer(new SlotFuel(furnaceTE, slotIndex++, slotcoord_fuel.x, slotcoord_fuel.y));
		addSlotToContainer(new SlotInput(furnaceTE, slotIndex++, slotcoord_input.x, slotcoord_input.y));
		addSlotToContainer(new SlotOutput(furnaceTE, slotIndex, slotcoord_output.x, slotcoord_output.y));
	}

	private void addPlayerSlots(IInventory playerInventory) {

		// Slots for the hotbar
		for (int row = 0; row < 9; ++row) {
			int slot = row;
			int x = 8 + row * 18;
			int y = 72 + 70;
			//LogHelper.info("hotbar slot" + slot);
			this.addSlotToContainer(new Slot(playerInventory, slot, x, y));
		}

		// Slots for the main inventory
		for (int row = 0; row < 3; ++row) {
			for (int col = 0; col < 9; ++col) {
				int slot = 9 + row * 9 + col;
				int x = 8 + col * 18;
				int y = row * 18 + 84;
				//LogHelper.info("player slot " + slot);
				this.addSlotToContainer(new Slot(playerInventory, slot, x, y));
			}
		}


	}

	@Nullable
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {

		Slot sourceSlot = (Slot)inventorySlots.get(index);
		if (sourceSlot == null || !sourceSlot.getHasStack()) return ItemStack.EMPTY;
		ItemStack sourceStack = sourceSlot.getStack();
		ItemStack sourceStackCopy = sourceStack.copy();

		if (index >= FURNACE_SLOT_TOTAL && index < FURNACE_SLOT_TOTAL + PLAYER_SLOT_TOTAL) {
			//LogHelper.info("Transfer from player slot " + index);
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
			//LogHelper.info("Transfer from furnace slot " + index);
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
				//LogHelper.info("fieldchanged " + cachedFields[i]);
				fieldHasChanged[i] = true;
			}
		}

		// go through the list of listeners (players using this container) and update them if necessary
		for (IContainerListener listener : this.listeners) {
			for (int fieldID = 0; fieldID < this.furnaceTE.getFieldCount(); ++fieldID) {
				if (fieldHasChanged[fieldID]) {
					// Note that although sendProgressBarUpdate takes 2 ints on a server these are truncated to shorts
					//LogHelper.info("Sending changes");
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
		//LogHelper.info("id = " + id + " data " + data);
		this.furnaceTE.setField(id, data);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return this.furnaceTE.canInteractWith(playerIn);
	}

	public class SlotFuel extends Slot {
		public SlotFuel(IInventory inventoryIn, int index, int xPosition, int yPosition) {
			super(inventoryIn, index, xPosition, yPosition);
		}

		// if false, player won't be able to insert the given item into this slot
		@Override
		public boolean isItemValid(ItemStack stack) {
			//LogHelper.info("is valid fuel");
			return FurnaceTileEntity.isItemValidForFuelSlot(stack);
		}
	}

	// SlotSmeltableInput is a slot for input items
	public class SlotInput extends Slot {
		public SlotInput(IInventory inventoryIn, int index, int xPosition, int yPosition) {
			super(inventoryIn, index, xPosition, yPosition);
		}

		// if false, player won't be able to insert the given item into this slot
		@Override
		public boolean isItemValid(ItemStack stack) {
			//LogHelper.info("is valid input");
			return FurnaceTileEntity.isItemValidForInputSlot(stack);
		}
	}

	// SlotOutput is a slot that will not accept any items
	public class SlotOutput extends Slot {
		public SlotOutput(IInventory inventoryIn, int index, int xPosition, int yPosition) {
			super(inventoryIn, index, xPosition, yPosition);
		}

		// if false, player won't be able to insert the given item into this slot
		@Override
		public boolean isItemValid(ItemStack stack) {
			//LogHelper.info("is valid output");
			return FurnaceTileEntity.isItemValidForOutputSlot(stack);
		}
	}

	// SlotCatalyst is a slot for catalyst items
	public class SlotCatalyst extends Slot {
		public SlotCatalyst(IInventory inventoryIn, int index, int xPosition, int yPosition) {
			super(inventoryIn, index, xPosition, yPosition);
		}

		// if false, player won't be able to insert the given item into this slot
		@Override
		public boolean isItemValid(ItemStack stack) {
			//LogHelper.info("is valid catalyst");
			return FurnaceTileEntity.isItemValidForCatalystSlot(stack);
		}
	}
}
