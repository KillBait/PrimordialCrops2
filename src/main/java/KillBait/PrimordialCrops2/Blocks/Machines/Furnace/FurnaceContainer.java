package KillBait.PrimordialCrops2.Blocks.Machines.Furnace;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nullable;

/**
 * Created by Jon on 20/10/2016.
 */
public class FurnaceContainer extends Container {

	private FurnaceTileEntity furnaceTE;

	public FurnaceContainer(IInventory playerInventory, FurnaceTileEntity furnaceTE) {
		this.furnaceTE = furnaceTE;

		// This container references items out of our own inventory (the 9 slots we hold ourselves)
		// as well as the slots from the player inventory so that the user can transfer items between
		// both inventories. The two calls below make sure that slots are defined for both inventories.
		addOwnSlots();
		addPlayerSlots(playerInventory);
	}

	private void addOwnSlots() {
		IItemHandler itemHandler = this.furnaceTE.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		int x = 9;
		int y = 6;

		// Add our own slots
		int slotIndex = 0;
		for (int i = 0; i < itemHandler.getSlots(); i++) {
			addSlotToContainer(new SlotItemHandler(itemHandler, slotIndex, x, y));
			slotIndex++;
			x += 18;
		}
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

			if (index < FurnaceTileEntity.SIZE) {
				if (!this.mergeItemStack(itemstack1, FurnaceTileEntity.SIZE, this.inventorySlots.size(), true)) {
					return null;
				}
			} else if (!this.mergeItemStack(itemstack1, 0, FurnaceTileEntity.SIZE, false)) {
				return null;
			}

			if (itemstack1.stackSize == 0) {
				slot.putStack(null);
			} else {
				slot.onSlotChanged();
			}
		}

		return itemstack;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return furnaceTE.canInteractWith(playerIn);
	}
}