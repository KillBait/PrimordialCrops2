package KillBait.PrimordialCrops2.Blocks.Machines.Furnace;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.awt.*;

/**
 * Created by Jon on 20/10/2016.
 */
public class FurnaceContainer extends Container {

	private FurnaceTileEntity furnaceTE;
	private int[] cachedFields;
	private Point slotcoord_input = new Point(56, 17);
	private Point slotcoord_fuel = new Point(56, 53);
	private Point slotcoord_output = new Point(116, 35);
	private Point slotcoord_catalyst = new Point(21, 35);

	//private Point[] slotindex = new Point[4];


	public FurnaceContainer(IInventory playerInventory, FurnaceTileEntity furnaceTE) {
		this.furnaceTE = furnaceTE;

		// This container references items out of our own inventory (the 9 slots we hold ourselves)
		// as well as the slots from the player inventory so that the user can transfer items between
		// both inventories. The two calls below make sure that slots are defined for both inventories.
		addOwnSlots();
		addPlayerSlots(playerInventory);
	}

	private void addOwnSlots() {
		//IItemHandler itemHandler = this.furnaceTE.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		//IItemHandler itemHandler = this.furnaceTE.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH);
		/*Point[] slotcoords = {
				new Point(56, 17), // input slot
				new Point(56, 53), // fuel slot
				new Point(116, 35), // output slot
				new Point(21, 35) // catalyst slot
		};*/

		// Add our own slots
		int slotIndex = 0;
		addSlotToContainer(new SlotCatalyst(this.furnaceTE, slotIndex++, slotcoord_catalyst.x, slotcoord_catalyst.y));
		//slotIndex++;
		//itemHandler = this.furnaceTE.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
		addSlotToContainer(new SlotFuel(this.furnaceTE, slotIndex++, slotcoord_fuel.x, slotcoord_fuel.y));
		//slotIndex++;
		//itemHandler = this.furnaceTE.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
		addSlotToContainer(new SlotInput(this.furnaceTE, slotIndex++, slotcoord_input.x, slotcoord_input.y));
		//slotIndex++;
		//itemHandler = this.furnaceTE.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.SOUTH);
		addSlotToContainer(new SlotOutput(this.furnaceTE, slotIndex++, slotcoord_output.x, slotcoord_output.y));
	}

	private void addPlayerSlots(IInventory playerInventory) {

		// Slots for the main inventory
		for (int row = 0; row < 3; ++row) {
			for (int col = 0; col < 9; ++col) {
				int x = 8 + col * 18;
				int y = row * 18 + 84;
				this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 10, x, y));
			}
		}

		// Slots for the hotbar
		for (int row = 0; row < 9; ++row) {
			int x = 8 + row * 18;
			int y = 72 + 70;
			this.addSlotToContainer(new Slot(playerInventory, row, x, y));
		}
	}

	@Nullable
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		ItemStack itemstack = null;
		Slot slot = this.inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (index < FurnaceTileEntity.TOTALSLOTS) {
				if (!this.mergeItemStack(itemstack1, FurnaceTileEntity.TOTALSLOTS, this.inventorySlots.size(), true)) {
					return null;
				}
			} else if (!this.mergeItemStack(itemstack1, 0, FurnaceTileEntity.TOTALSLOTS, false)) {
				return null;
			}

			if (itemstack1.stackSize == 0) {
				slot.putStack(null);
			} else {
				slot.onSlotChanged();
			}

			slot.onPickupFromSlot(playerIn, itemstack1);

		}


		return itemstack;
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
		return furnaceTE.canInteractWith(playerIn);
	}

	public class SlotFuel extends Slot {
		public SlotFuel(IInventory inventoryIn, int index, int xPosition, int yPosition) {
			super(inventoryIn, index, xPosition, yPosition);
		}

		// if false, player won't be able to insert the given item into this slot
		@Override
		public boolean isItemValid(ItemStack stack) {
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
			return FurnaceTileEntity.isItemValidForCatalystSlot(stack);
		}
	}
}
