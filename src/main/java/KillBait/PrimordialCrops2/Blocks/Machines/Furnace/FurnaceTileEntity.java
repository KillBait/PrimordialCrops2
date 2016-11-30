package KillBait.PrimordialCrops2.Blocks.Machines.Furnace;

import KillBait.PrimordialCrops2.Items.Essence.CraftEssence;
import KillBait.PrimordialCrops2.Utils.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;
import java.util.Arrays;


/**
 * Created by Jon on 20/10/2016.
 */
public class FurnaceTileEntity extends TileEntity implements IInventory, ITickable, ISidedInventory {


	public static final int CATALYST_SLOT = 0;
	public static final int FUEL_SLOT = 1;
	public static final int INPUT_SLOT = 2;
	public static final int OUTPUT_SLOT = 3;
	public static final int TOTALSLOTS = 4;
	private ItemStack[] itemStacks = new ItemStack[TOTALSLOTS];

	/**
	 * The number of ticks the current item has been cooking
	 */
	private short cookTime;

	/**
	 * The number of ticks required to cook an item
	 */
	private static final short COOK_TIME_BASE_MAX = 200;  // vanilla value is 200 = 10 seconds


	/*private ItemStack input;
	private ItemStack output;
	private ItemStack fuel;
	private ItemStack catalyst;

	protected ItemStackHandler inputSlot;
	protected ItemStackHandler outputSlot;
	protected ItemStackHandler fuelSlot;
	protected ItemStackHandler catalystSlot;*/

	/*private String customName;*/

	/*public FurnaceTileEntity() {
		inputSlot = new ItemStackHandler();
		outputSlot = new ItemStackHandler();
		fuelSlot = new ItemStackHandler();
		catalystSlot = new ItemStackHandler();
	}

	public ItemStackHandler itemStackHandler = new ItemStackHandler(TOTALSLOTS) {
		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
		{
			LogHelper.info("slot " + slot);
			return super.insertItem(slot,stack,simulate);
		}

		@Override
		protected void onContentsChanged(int slot) {
			// We need to tell the tile entity that something has changed so
			// that the chest contents is persisted
			FurnaceTileEntity.this.markDirty();
		}
	};

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			//LogHelper.info("capability trigger");
			//return (T) itemStackHandler;
			if (facing == null) {
				LogHelper.info("null facing");
				return (T) itemStackHandler;
			}

			if (facing == EnumFacing.UP) {
				LogHelper.info("capability trigger UP");
				return (T) inputSlot;
			}
			if (facing == EnumFacing.DOWN) {
				LogHelper.info("capability trigger DOWN");
				return (T) fuelSlot;
			}
			if (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH || facing == EnumFacing.EAST || facing == EnumFacing.WEST) {
				LogHelper.info("capability trigger SIDES");
				return (T)catalystSlot;
			}


		}
		return super.getCapability(capability, facing);
	}*/

	@Override
	public String getName() {
		return "container.primordialfurnace.name";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Nullable
	@Override
	public ITextComponent getDisplayName() {
		return this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName());
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		LogHelper.info("readFromNBT");
		final byte NBT_TYPE_COMPOUND = 10;       // See NBTBase.createNewByType() for a listing
		NBTTagList dataForAllSlots = compound.getTagList("Items", NBT_TYPE_COMPOUND);

		Arrays.fill(itemStacks, null);           // set all slots to empty
		for (int i = 0; i < dataForAllSlots.tagCount(); ++i) {
			NBTTagCompound dataForOneSlot = dataForAllSlots.getCompoundTagAt(i);
			byte slotNumber = dataForOneSlot.getByte("Slot");
			if (slotNumber >= 0 && slotNumber < this.itemStacks.length) {
				this.itemStacks[slotNumber] = ItemStack.loadItemStackFromNBT(dataForOneSlot);
			}
		}

		/*if (compound.hasKey("CustomName", 8)) {
			LogHelper.info("Reading Name");
			this.customName = compound.getString("CustomName");
		}*/
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		LogHelper.info("writeToNBT");
		NBTTagList dataForAllSlots = new NBTTagList();
		for (int i = 0; i < this.itemStacks.length; ++i) {
			if (this.itemStacks[i] != null) {
				NBTTagCompound dataForThisSlot = new NBTTagCompound();
				dataForThisSlot.setByte("Slot", (byte) i);
				this.itemStacks[i].writeToNBT(dataForThisSlot);
				dataForAllSlots.appendTag(dataForThisSlot);
			}
		}
		// the array of hashmaps is then inserted into the parent hashmap for the container
		compound.setTag("Items", dataForAllSlots);

		/*if (this.hasCustomName()) {
			LogHelper.info("saving name");
			compound.setString("CustomName", this.customName);
		}*/
		return compound;
	}

	public boolean canInteractWith(EntityPlayer playerIn) {
		// If we are too far away from this tile entity you cannot use it
		return !isInvalid() && playerIn.getDistanceSq(pos.add(0.5D, 0.5D, 0.5D)) <= 64D;
	}

	// ISidedInventory function
	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		if (side == EnumFacing.DOWN) {
			return new int[]{FUEL_SLOT};
		} else if (side == EnumFacing.UP) {
			return new int[]{INPUT_SLOT};
		} else
			// TODO may need adjusting if automation pulls catalyst out of slot
			return new int[]{CATALYST_SLOT, OUTPUT_SLOT};
	}

	// ISidedInventory function
	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return isItemValidForSlot(index, itemStackIn);
	}

	// ISidedInventory function
	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		LogHelper.info("can extract item");
		return false;
	}

	@Override
	public int getSizeInventory() {
		return itemStacks.length;
	}

	//called evert tick
	@Nullable
	@Override
	public ItemStack getStackInSlot(int index) {
		return itemStacks[index];
	}

	@Nullable
	@Override
	public ItemStack decrStackSize(int index, int count) {
		ItemStack itemStackInSlot = getStackInSlot(index);
		if (itemStackInSlot == null) return null;

		ItemStack itemStackRemoved;
		if (itemStackInSlot.stackSize <= count) {
			itemStackRemoved = itemStackInSlot;
			setInventorySlotContents(index, null);
		} else {
			itemStackRemoved = itemStackInSlot.splitStack(count);
			if (itemStackInSlot.stackSize == 0) {
				setInventorySlotContents(index, null);
			}
		}
		markDirty();
		return itemStackRemoved;
	}

	@Nullable
	@Override
	public ItemStack removeStackFromSlot(int slotIndex) {
		ItemStack itemStack = getStackInSlot(slotIndex);
		if (itemStack != null) setInventorySlotContents(slotIndex, null);
		return itemStack;
	}

	@Override
	public void setInventorySlotContents(int slotIndex, ItemStack itemstack) {
		itemStacks[slotIndex] = itemstack;
		if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
			itemstack.stackSize = getInventoryStackLimit();
		}
		markDirty();
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return false;
	}

	// should be blank
	@Override
	public void openInventory(EntityPlayer player) {

	}

	// should be blank
	@Override
	public void closeInventory(EntityPlayer player) {

	}

	// TODO - Add IMC registration for custom fuels that dont use minecraftforge.fml.common.IFuelHandler

	public static int getItemBurnTime(ItemStack stack) {
		if (stack == null) {
			return 0;
		} else {
			Item item = stack.getItem();

			if (item instanceof ItemBlock && Block.getBlockFromItem(item) != Blocks.AIR) {
				Block block = Block.getBlockFromItem(item);

				if (block == Blocks.WOODEN_SLAB) {
					return 150;
				}

				if (block.getDefaultState().getMaterial() == Material.WOOD) {
					return 300;
				}

				if (block == Blocks.COAL_BLOCK) {
					return 16000;
				}
			}

			if (item instanceof ItemTool && "WOOD".equals(((ItemTool) item).getToolMaterialName())) return 200;
			if (item instanceof ItemSword && "WOOD".equals(((ItemSword) item).getToolMaterialName())) return 200;
			if (item instanceof ItemHoe && "WOOD".equals(((ItemHoe) item).getMaterialName())) return 200;
			if (item == Items.STICK) return 100;
			if (item == Items.COAL) return 1600;
			if (item == Items.LAVA_BUCKET) return 20000;
			if (item == Item.getItemFromBlock(Blocks.SAPLING)) return 100;
			if (item == Items.BLAZE_ROD) return 2400;
			return net.minecraftforge.fml.common.registry.GameRegistry.getFuelValue(stack);
		}
	}

	public static boolean isItemFuel(ItemStack stack) {
		// returns the number of ticks the supplied fuel will keep the furnace burning, or 0 if the item isn't fuel
		return getItemBurnTime(stack) > 0;
	}

	//---------------------is this needed? ------------------------------------------
	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {

		if (index == CATALYST_SLOT) {
			return isItemValidForCatalystSlot(stack);
		} else if (index == FUEL_SLOT) {
			return isItemValidForFuelSlot(stack);
		} else if (index == INPUT_SLOT) {
			return isItemValidForInputSlot(stack);
		}
		return false;
	}

	// Return true if the given stack is allowed to be inserted in the given slot
	public static boolean isItemValidForFuelSlot(ItemStack itemStack) {
		return isItemFuel(itemStack);
	}

	// Return true if the given stack is allowed to be inserted in the given slot
	public static boolean isItemValidForInputSlot(ItemStack itemStack) {
		return true;
	}

	// Return true if the given stack is allowed to be inserted in the given slot
	public static boolean isItemValidForOutputSlot(ItemStack itemStack) {
		return false;
	}

	// Return true if the given stack is allowed to be inserted in the given slot
	public static boolean isItemValidForCatalystSlot(ItemStack itemStack) {
		if (itemStack.getItem() instanceof CraftEssence)
			return true;
		else
			return false;
	}

	@Override
	@Nullable
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound updateTagDescribingTileEntityState = getUpdateTag();
		final int METADATA = 0;
		return new SPacketUpdateTileEntity(this.pos, METADATA, updateTagDescribingTileEntityState);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		NBTTagCompound updateTagDescribingTileEntityState = pkt.getNbtCompound();
		handleUpdateTag(updateTagDescribingTileEntityState);
	}

	/* Creates a tag containing the TileEntity information, used by vanilla to transmit from server to client
	   Warning - although our getUpdatePacket() uses this method, vanilla also calls it directly, so don't remove it.
	 */
	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound nbtTagCompound = new NBTTagCompound();
		writeToNBT(nbtTagCompound);
		return nbtTagCompound;
	}

	/* Populates this TileEntity with information from the tag, used by vanilla to transmit from server to client
	 Warning - although our onDataPacket() uses this method, vanilla also calls it directly, so don't remove it.
   */
	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		this.readFromNBT(tag);
	}

	@Override
	public void clear() {
		Arrays.fill(itemStacks, null);
	}

	@Override
	public int getField(int id) {
		LogHelper.info("getField = " + id);
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		LogHelper.info("setField = " + id + " , Value = " + value);
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void update() {

	}
}
