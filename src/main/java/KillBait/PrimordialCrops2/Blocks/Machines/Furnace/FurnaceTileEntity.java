package KillBait.PrimordialCrops2.Blocks.Machines.Furnace;

import KillBait.PrimordialCrops2.Items.Essence.CraftEssence;
import KillBait.PrimordialCrops2.Utils.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.*;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;


/**
 * Created by Jon on 20/10/2016.
 */

	// TODO switch to ItemHandler capabilities

public class FurnaceTileEntity extends TileEntity implements IInventory, ITickable{

	public static final int CATALYST_SLOT = 0;
	public static final int FUEL_SLOT = 1;
	public static final int INPUT_SLOT = 2;
	public static final int OUTPUT_SLOT = 3;
	public static final int TOTAL_SLOTS = 4;

	private NonNullList<ItemStack> furnaceItemStacks = NonNullList.<ItemStack>withSize(TOTAL_SLOTS, ItemStack.EMPTY);

	/** The number of burn ticks remaining on the curret piece of fuel */
	private int burnTimeRemaining;
	/** The initial fuel value of the currently burning fuel (in ticks of burn duration) */
	private int burnTimeInitialValue;

	// coal = 1600 fuel, 1 coal = 8 smelts of cobble, 200 fuel per craft at 1 fuel per tick
    //
	// to keep this 200 fuel to smelt 1 item ratio, we must increase the number of fuel consumed per tick.
	//
	// since we are using int's to keep the remaining fuel, we can only use round numbers
	//
	// using this bit of code
	//
	//	for (int i = 200; i >= 0; i--) {
	//	  if (i > 0) {
	//		LogHelper.info("Cook Time " + i + " - " + (double) 200 / i + " fuel per tick ");
	//	  }
	//  }
	//
	// we can see all the possible numbers down to 1 smelt per tick ( see SmeltTimes.txt )
	//
	//  only using the non fractioned numbers leave us with
	//
	//  Cook Time 200 ticks - 1.0 fuel per tick // VANILLA furnace speed
	//  Cook Time 100 ticks - 2.0 fuel per tick   // minicio
	//  Cook Time  50 ticks - 4.0 fuel per tick   // accio
	//  Cook Time  40 ticks - 5.0 fuel per tick
	//  Cook Time  25 ticks - 8.0 fuel per tick   // crucio
	//  Cook Time  20 ticks - 10.0 fuel per tick
	//  Cook Time  10 ticks - 20.0 fuel per tick  // imperio
	//  Cook Time   8 ticks - 25.0 fuel per tick
	//  Cook Time   5 ticks - 40.0 fuel per tick
	//  Cook Time   4 ticks - 50.0 fuel per tick
	//  Cook Time   2 ticks - 100.0 fuel per tick
	//  Cook Time   1 ticks - 200.0 fuel per tick // zivicio

	public int[] ticksPerCraftScale = {200,100,50,25,10,1};

	//public int[] maxUsesPerCatalyst = {0,8,8,8,8,8}; // Testing purposes only!!
	public int[] maxUsesPerCatalyst = {0,64,128,256,512,1024};



	private short currentCatalystRemaining;
	private short currentCatalystType; // 0 = none. 1= minicio, 2 = accio, 3 = crucio, 4 = imperio, 5 = zivicio

	/**
	 * The number of ticks the current item has been cooking
	 */
	private short cookTime;
	private short lastCookTime;
	private boolean emitParticles = false;

	/**
	 * The number of ticks required to cook an item
	 */
	private static short COOK_TIME_FOR_COMPLETION = 200;  // vanilla value is 200 = 10 seconds


	private ItemStack input;
	private ItemStack output;
	private ItemStack fuel;
	private ItemStack catalyst;

	protected ItemStackHandler inputSlot;
	protected ItemStackHandler outputSlot;
	protected ItemStackHandler fuelSlot;
	protected ItemStackHandler catalystSlot;

	private String customName;

	public FurnaceTileEntity() {
		clear();
	}

	@Override
	public void update() {

		// check if any catalyst available
		checkCatalyst();

		// Check if there's something in the input slot and fuel is available to smelt
		if (canSmelt()) {
			if (useFuel()) {
				cookTime += 1;
			}

			if (cookTime < 0) cookTime = 0;

			if (cookTime >= COOK_TIME_FOR_COMPLETION) {
				smeltItem();
				cookTime = 0;
			}
		/*} else {
			cookTime = 0;*/
		}

		// TODO add light when furnace on

		if (!world.isRemote) {
			if (cookTime != lastCookTime) {
				if (!emitParticles) {
					//LogHelper.info("Flame ON");
					PrimordialFurnace.setState(true, this.world, this.pos);
					emitParticles = true;
				}
			}else{
				if (emitParticles) {
					//LogHelper.info("Flame OFF");
					PrimordialFurnace.setState(false, this.world, this.pos);
					emitParticles = false;
				}
			}
			lastCookTime = cookTime;
		}
	}

	private void checkCatalyst() {
		// check if there any catalyst left to use up.
		if (currentCatalystRemaining == 0) {
			// check if the catalyst slot has items in it and if they are essence
			if (!furnaceItemStacks.get(CATALYST_SLOT).isEmpty() && furnaceItemStacks.get(CATALYST_SLOT).getItem() instanceof CraftEssence) {

				int meta = furnaceItemStacks.get(CATALYST_SLOT).getMetadata();
				meta++;

				currentCatalystRemaining = (short) maxUsesPerCatalyst[meta];
				currentCatalystType = (short) meta;
				furnaceItemStacks.get(CATALYST_SLOT).shrink(1);
				COOK_TIME_FOR_COMPLETION = (short) ticksPerCraftScale[meta];
				this.markDirty();
			}
		}
		/*if (!world.isRemote) {
			LogHelper.info("type = " + currentCatalystType + " remain = " + currentCatalystRemaining + " ticks = " + COOK_TIME_FOR_COMPLETION);
		}*/
	}

	private boolean useFuel() {

		boolean fuelRemaining = false;
		boolean inventoryChanged = false;

		if (burnTimeRemaining <= 0) {
			if (!furnaceItemStacks.get(FUEL_SLOT).isEmpty() && getItemBurnTime(furnaceItemStacks.get(FUEL_SLOT)) > 0) {

				burnTimeInitialValue = getItemBurnTime(furnaceItemStacks.get(FUEL_SLOT));
				burnTimeRemaining = burnTimeInitialValue - (200 / COOK_TIME_FOR_COMPLETION);

				// check if fuel in slot is a container item, i.e.  if it's a lava bucket, return the empty bucket
				// otherwise reduce the fuel in slot by one.
				ItemStack stack = furnaceItemStacks.get(FUEL_SLOT).getItem().getContainerItem(furnaceItemStacks.get(FUEL_SLOT));
				if (!stack.isEmpty() && furnaceItemStacks.get(FUEL_SLOT).getCount() == 1 ) {
					furnaceItemStacks.get(FUEL_SLOT).shrink(1);  // remove the stack
					furnaceItemStacks.set(FUEL_SLOT, stack.copy()); // add the empty container
				}else {
					furnaceItemStacks.get(FUEL_SLOT).shrink(1);
				}
				// refresh the TileEntity
				inventoryChanged = true;
				fuelRemaining = true;
			}
		}else {
			burnTimeRemaining -= (200 / COOK_TIME_FOR_COMPLETION);
			fuelRemaining = true;
		}

		if (inventoryChanged) markDirty();

		return fuelRemaining;
	}

	private boolean canSmelt() {
		// is item in input slot?
		if (!furnaceItemStacks.get(INPUT_SLOT).isEmpty()) {
			// if there is fuel in the slot or there is fuel stored return true
			if (burnTimeRemaining > 0 || !furnaceItemStacks.get(FUEL_SLOT).isEmpty()) {
				// Cook using new fuel
				return true;
			}
		}
		return false;
	}

	/**
	 * checks that there is an item to be smelted in one of the input slots and that there is room for the result in the output slots
	 * If desired, performs the smelt
	 */

	private void smeltItem()
	{
		// check if something in input slot
		if (!furnaceItemStacks.get(INPUT_SLOT).isEmpty()) {
			// get the result of smelting the input slot item
			ItemStack smeltResult = FurnaceRecipes.instance().getSmeltingResult(furnaceItemStacks.get(INPUT_SLOT));
			// check if smeltResult is a valid item ( will be empty if not )
			if (!smeltResult.isEmpty()) {
				// get the contents of output slot			}
				ItemStack outputStack = furnaceItemStacks.get(OUTPUT_SLOT);
				// check if output slot is empty
				if (outputStack.isEmpty()) {
					// set output slot to smeltResult, reduce the input slot amount
					furnaceItemStacks.set(OUTPUT_SLOT, smeltResult.copy());
					furnaceItemStacks.get(INPUT_SLOT).shrink(1);
					// not sure if needed, but, check if input stack is used up and set it to an empty stack if it is
					if (furnaceItemStacks.get(INPUT_SLOT).getCount() <= 0) {
						furnaceItemStacks.set(INPUT_SLOT, ItemStack.EMPTY);
					}
					//LogHelper.info("Empty Slot");
					// check if any catalyst is left, if so, remove 1
					if (currentCatalystRemaining > 0) {
						currentCatalystRemaining--;
					}
					//LogHelper.info("burnTimeRemaing = " + burnTimeRemaining);
					markDirty();
				} else {
					if (outputStack.getItem() == smeltResult.getItem() && (!outputStack.getHasSubtypes() || outputStack.getMetadata() == outputStack.getMetadata())
							&& ItemStack.areItemStackTagsEqual(outputStack, smeltResult)) {

						int combinedSize = furnaceItemStacks.get(OUTPUT_SLOT).getCount() + smeltResult.getCount();

						if (combinedSize <= getInventoryStackLimit() && combinedSize <= furnaceItemStacks.get(OUTPUT_SLOT).getMaxStackSize()) {
							// remove 1 from input stack
							furnaceItemStacks.get(INPUT_SLOT).shrink(1);
							// combine the number of items in input and output slots
							int newStackSize = furnaceItemStacks.get(OUTPUT_SLOT).getCount() + smeltResult.getCount();
							// set output slot to new stacksize
							furnaceItemStacks.get(OUTPUT_SLOT).setCount(newStackSize);
							// not sure if needed, but, check if input stack is used up and set it to an empty stack if it is
							if (furnaceItemStacks.get(INPUT_SLOT).getCount() <= 0) {
								furnaceItemStacks.set(INPUT_SLOT, ItemStack.EMPTY);  //getStackSize(), EmptyItem
							}
							//LogHelper.info("Merge Stacks");
							// check if any catalyst is left, if so, remove 1 from total
							if (currentCatalystRemaining > 0) {
								currentCatalystRemaining--;
							}
							//LogHelper.info("burnTimeRemaing = " + burnTimeRemaining);
							markDirty();
						}
					}
				}
			}
		}
	}

	@Override
	public String getName() { return "container.primordialfurnace.name";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Nullable
	@Override
	public ITextComponent getDisplayName() {
		if (this.hasCustomName()) {
			return new TextComponentString(this.getName());
		}else {
			return new TextComponentTranslation(this.getName(), new Object [0]);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);

		final byte NBT_TYPE_COMPOUND = 10;       // See NBTBase.createNewByType()
		NBTTagList dataForAllSlots = compound.getTagList("Items", NBT_TYPE_COMPOUND);

		this.furnaceItemStacks = NonNullList.<ItemStack>withSize(this.getSizeInventory(), ItemStack.EMPTY);  // set all slots to empty
		for (int i = 0; i < dataForAllSlots.tagCount(); ++i) {
			NBTTagCompound dataForOneSlot = dataForAllSlots.getCompoundTagAt(i);
			byte slotNumber = dataForOneSlot.getByte("Slot");
			if (slotNumber >= 0 && slotNumber < this.furnaceItemStacks.size()) {
				this.furnaceItemStacks.set(slotNumber,new ItemStack(dataForOneSlot));
			}
		}

		cookTime = compound.getShort("CookTime");
		currentCatalystRemaining = compound.getShort("catalystRemaining");
		currentCatalystType = compound.getShort("catalystType");
		burnTimeRemaining = compound.getInteger("burnTimeRemaining");
		burnTimeInitialValue = compound.getInteger("burnTimeInitial");

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);

		NBTTagList dataForAllSlots = new NBTTagList();
		for (int i = 0; i < this.furnaceItemStacks.size(); ++i) {
			if (!this.furnaceItemStacks.get(i).isEmpty()) {
				NBTTagCompound dataForThisSlot = new NBTTagCompound();
				dataForThisSlot.setByte("Slot", (byte) i);
				this.furnaceItemStacks.get(i).writeToNBT(dataForThisSlot);
				dataForAllSlots.appendTag(dataForThisSlot);
			}
		}

		compound.setTag("Items", dataForAllSlots);

		compound.setShort("CookTime", cookTime);
		compound.setShort("catalystRemaining", currentCatalystRemaining);
		compound.setShort("catalystType", currentCatalystType);
		compound.setInteger("burnTimeRemaining", burnTimeRemaining);
		compound.setInteger("burnTimeInitial", burnTimeInitialValue);

		return compound;
	}

	public boolean canInteractWith(EntityPlayer playerIn) {
		return !isInvalid() && playerIn.getDistanceSq(pos.add(0.5D, 0.5D, 0.5D)) <= 64D;
	}

	/*
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
	}*/

	public static ItemStack getSmeltingResult(ItemStack stack) { return FurnaceRecipes.instance().getSmeltingResult(stack); }

	@Override
	public int getSizeInventory() {
		return furnaceItemStacks.size();
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemstack : furnaceItemStacks) {
			if (!itemstack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	//called evert tick
	@Nullable
	@Override
	public ItemStack getStackInSlot(int index) {
		return furnaceItemStacks.get(index);
	}

	@Nullable
	@Override
	public ItemStack decrStackSize(int index, int count) {
		ItemStack itemStackInSlot = getStackInSlot(index);
		if (itemStackInSlot.isEmpty()) return ItemStack.EMPTY;

		ItemStack itemStackRemoved;
		if (itemStackInSlot.getCount() <= count) {
			itemStackRemoved = itemStackInSlot;
			setInventorySlotContents(index, ItemStack.EMPTY);
		} else {
			itemStackRemoved = itemStackInSlot.splitStack(count);
			if (itemStackInSlot.getCount() == 0) {
				setInventorySlotContents(index, ItemStack.EMPTY);
			}
		}
		markDirty();
		return itemStackRemoved;
	}


	@Override
	public ItemStack removeStackFromSlot(int slotIndex) {

		if (getStackInSlot(slotIndex) != ItemStack.EMPTY) {
			setInventorySlotContents(slotIndex, ItemStack.EMPTY);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public void setInventorySlotContents(int slotIndex, ItemStack itemstack) {
		furnaceItemStacks.set(slotIndex,itemstack);
		if (!itemstack.isEmpty() && itemstack.getCount() > getInventoryStackLimit()) {
			itemstack.setCount(getInventoryStackLimit());
		}
		markDirty();
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	public static boolean isItemFuel(ItemStack stack) {
		// returns the number of ticks the supplied fuel will keep the furnace burning, or 0 if the item isn't fuel
		return getItemBurnTime(stack) > 0;
	}

	// TODO - Add IMC registration for custom fuels that dont use minecraftforge.fml.common.IFuelHandler?

	public static int getItemBurnTime(ItemStack stack) {
		if (stack.isEmpty()) {
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



	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {

		//LogHelper.info("Is Slot " + index + " Valid for stack " + stack);

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
		return !getSmeltingResult(itemStack).isEmpty();
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
		this.furnaceItemStacks.clear();
	}



	private static final byte COOKTIME_ID = 0;
	private static final byte BURN_TIME_INITIAL_ID = 1;
	private static final byte BURN_TIME_REMAINING_ID = 2;
	private static final byte CATALYST_TYPE_ID = 3;
	private static final byte CATALYST_REMAINING_ID = 4;
	private static final byte NUMBER_OF_FIELDS = 5;

	@Override
	public int getField(int id) {
		if (id == COOKTIME_ID) return cookTime;
		if (id == BURN_TIME_INITIAL_ID) return burnTimeInitialValue;
		if (id == BURN_TIME_REMAINING_ID) return burnTimeRemaining;
		if (id == CATALYST_TYPE_ID) return currentCatalystType;
		if (id == CATALYST_REMAINING_ID) return currentCatalystRemaining;

		if (id < 0 || id > (NUMBER_OF_FIELDS -1)) {
			LogHelper.error("Invalid field ID in FurnaceTileEntity.getField:" + id);
		}
		return 0;
	}

	@Override
	public void setField(int id, int value)
	{
		if (id == COOKTIME_ID) cookTime = (short)value;
		if (id == BURN_TIME_INITIAL_ID) burnTimeInitialValue = value;
		if (id == BURN_TIME_REMAINING_ID) burnTimeRemaining = value;
		if (id == CATALYST_TYPE_ID) currentCatalystType = (short)value;
		if (id == CATALYST_REMAINING_ID) currentCatalystRemaining = (short)value;

		if (id < 0 || id > (NUMBER_OF_FIELDS -1)) {
			LogHelper.error("Invalid field ID in FurnaceTileEntity.setField:" + id);
		}
	}

	@Override
	public int getFieldCount() {
		return NUMBER_OF_FIELDS;
	}

	public double fractionOfCatalystRemaining()
	{
		if (currentCatalystRemaining <=0) return 0;
		//if (burnTimeInitialValue[catalystSlot] <= 0 ) return 0;
		double fraction = currentCatalystRemaining / (double)8; // TODO change for multiple slots
		//double fraction = burnTimeRemaining[catalystSlot] / (double)burnTimeInitialValue[fuelSlot];
		return MathHelper.clamp(fraction, 0.0, 1.0);
	}

	public double fractionOfFuelRemaining()
	{
		if (burnTimeInitialValue <= 0 ) return 0;
		double fraction = burnTimeRemaining / (double)burnTimeInitialValue;
		return MathHelper.clamp(fraction, 0.0, 1.0);
	}

	// TODO remove if not used
	public int secondsOfFuelRemaining(int fuelSlot)
	{
		if (burnTimeRemaining <= 0 ) {
			return 0;
		}else {
			return burnTimeRemaining / 20; // 20 ticks per second
		}

	}

	public double fractionOfCookTimeComplete()
	{
		double fraction = cookTime / (double)COOK_TIME_FOR_COMPLETION;
		return MathHelper.clamp(fraction, 0.0, 1.0);
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
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
}
