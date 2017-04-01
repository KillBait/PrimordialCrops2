package KillBait.PrimordialCrops2.Blocks.Machines.Furnace;

import KillBait.PrimordialCrops2.Items.Essence.CraftEssence;
import KillBait.PrimordialCrops2.Utils.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.*;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
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
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.Arrays;

import static KillBait.PrimordialCrops2.Blocks.Machines.Furnace.FurnaceContainer.*;


/**
 * Created by Jon on 20/10/2016.
 */


public class FurnaceTileEntity extends TileEntity implements IInventory, ITickable{

	public static final int CATALYST_SLOT = 0;
	public static final int FUEL_SLOT = 1;
	public static final int INPUT_SLOT = 2;
	public static final int OUTPUT_SLOT = 3;
	public static final int TOTAL_SLOTS = 4;

	private NonNullList<ItemStack> furnaceItemStacks = NonNullList.<ItemStack>withSize(TOTAL_SLOTS, ItemStack.EMPTY);

	/** The number of burn ticks remaining on the current piece of fuel */
	private int [] burnTimeRemaining = new int[FUEL_SLOT];
	/** The initial fuel value of the currently burning fuel (in ticks of burn duration) */
	private int [] burnTimeInitialValue = new int[FUEL_SLOT];

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
	// we can see all the possible numbers down to 1 smelt per tick
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



	private short currentCatalystRemaining;
	private short currentCatalystType; // 0 = none. 1= minicio, 2 = accio, 3 = crucio, 4 = imperio, 5 = zivicio

	/**
	 * The number of ticks the current item has been cooking
	 */
	private short cookTime;

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

	public double fractionOfCatalystRemaining(int catalystSlot)
	{
		if (currentCatalystRemaining <=0) return 0;
		//if (burnTimeInitialValue[catalystSlot] <= 0 ) return 0;
		double fraction = currentCatalystRemaining / (double)8; // TODO change for multiple slots
		//double fraction = burnTimeRemaining[catalystSlot] / (double)burnTimeInitialValue[fuelSlot];
		return MathHelper.clamp(fraction, 0.0, 1.0);
	}

	public double fractionOfFuelRemaining(int fuelSlot)
	{
		if (burnTimeInitialValue[fuelSlot] <= 0 ) return 0;
		double fraction = burnTimeRemaining[fuelSlot] / (double)burnTimeInitialValue[fuelSlot];
		return MathHelper.clamp(fraction, 0.0, 1.0);
	}

	public int secondsOfFuelRemaining(int fuelSlot)
	{
		if (burnTimeRemaining[fuelSlot] <= 0 ) return 0;
		return burnTimeRemaining[fuelSlot] / 20; // 20 ticks per second
	}

	/*public int numberOfBurningFuelSlots()
	{
		int burningCount = 0;
		for (int burnTime : burnTimeRemaining) {
			if (burnTime > 0) ++burningCount;
		}
		return burningCount;
	}*/

	public double fractionOfCookTimeComplete()
	{
		double fraction = cookTime / (double)COOK_TIME_FOR_COMPLETION;
		return MathHelper.clamp(fraction, 0.0, 1.0);
	}

	/*public boolean isBurning() {
		for (int i = 0; i < TOTAL_FUEL_SLOTS; i++) {
			if (burnTimeRemaining[i] > 0) return true;
		}
		return false;
	}*/

	@SideOnly(Side.CLIENT)
	public static boolean isBurning(IInventory inventory)
	{
		return inventory.getField(0) > 0;
	}

	@Override
	public void update() {
		// check Catalyst slots

		checkCatalyst();

		// Check if there's something in the input slot and fuel is available to smelt
		if (canSmelt()) {
			int numberOfFuelBurning = burnFuel();

			if (numberOfFuelBurning > 0) {
				cookTime += numberOfFuelBurning;
			}

			if (cookTime < 0) cookTime = 0;

			if (cookTime >= COOK_TIME_FOR_COMPLETION) {
				smeltItem();
				cookTime = 0;
			}

			if (isBurning(this)) {
				PrimordialFurnace.setState(true, this.world, this.pos);
				LogHelper.info("Effects activated");
			}else {
				PrimordialFurnace.setState(false, this.world, this.pos);
			}
		} else {
			cookTime = 0;
			/*if (!isBurning()) {
				LogHelper.info("Effects de-activated");
				PrimordialFurnace.setState(false, this.world, this.pos);
			}*/
		}



		// when the number of burning slots changes, we need to force the block to re-render, otherwise the change in
		//   state will not be visible.  Likewise, we need to force a lighting recalculation.
		// The block update (for renderer) is only required on client side, but the lighting is required on both, since
		//    the client needs it for rendering and the server needs it for crop growth etc
		/*int numberBurning = numberOfBurningFuelSlots();
		if (numberBurning != 0) {
			if (world.isRemote) {
				IBlockState iblockstate = this.world.getBlockState(pos);
				final int FLAGS = 3;  // I'm not sure what these flags do, exactly.
				world.notifyBlockUpdate(pos, iblockstate, iblockstate, FLAGS);
			}
			world.checkLightFor(EnumSkyBlock.BLOCK, pos);
		}*/
	}

	private void checkCatalyst() {
		// check all catalyst slots to see if there is any in the slots.
		for (int catalystSlot = CATALYST_SLOT; catalystSlot < CATALYST_SLOT + TOTAL_CATALYST_SLOTS; catalystSlot++)	{
			if (currentCatalystRemaining == 0) {
				if (furnaceItemStacks.get(catalystSlot).getItem() instanceof CraftEssence) {
					int meta = furnaceItemStacks.get(catalystSlot).getMetadata();
					meta++;
					switch (meta) {
						case 1: {
							LogHelper.info("Minicio Essence");
							currentCatalystRemaining = 8;
							currentCatalystType = (short) meta;
							furnaceItemStacks.get(catalystSlot).shrink(1);
							COOK_TIME_FOR_COMPLETION = 100;
							break;
						}
						case 2: {
							LogHelper.info("Accio Essence");
							currentCatalystRemaining = 8;
							currentCatalystType = (short) meta;
							furnaceItemStacks.get(catalystSlot).shrink(1);
							COOK_TIME_FOR_COMPLETION = 50;
							break;
						}
						case 3: {
							LogHelper.info("Crucio Essence");
							currentCatalystRemaining = 8;
							currentCatalystType = (short) meta;
							furnaceItemStacks.get(catalystSlot).shrink(1);
							COOK_TIME_FOR_COMPLETION = 25;
							break;
						}
						case 4: {
							LogHelper.info("Imperio Essence");
							currentCatalystRemaining = 8;
							currentCatalystType = (short) meta;
							furnaceItemStacks.get(catalystSlot).shrink(1);
							COOK_TIME_FOR_COMPLETION = 10;
							break;
						}
						case 5: {
							LogHelper.info("Zivicio Essence");
							currentCatalystRemaining = 8;
							currentCatalystType = (short) meta;
							furnaceItemStacks.get(catalystSlot).shrink(1);
							COOK_TIME_FOR_COMPLETION = 1;
							break;
						}
					}
				}else {
					//LogHelper.info("No essence");
					currentCatalystType = 0;
					COOK_TIME_FOR_COMPLETION = 200;
				}
				//LogHelper.info("type = " + currentCatalystType + " remain = " + currentCatalystRemaining);
			}
		}
	}

	private int burnFuel() {
		int burningCount = 0;
		boolean inventoryChanged = false;

		for (int i = 0; i < TOTAL_FUEL_SLOTS; i++) {
			int fuelSlotNumber = i + FUEL_SLOT;
			if (burnTimeRemaining[i] == 0) {
				if (!furnaceItemStacks.get(fuelSlotNumber).isEmpty() && getItemBurnTime(furnaceItemStacks.get(fuelSlotNumber)) > 0) {  // isEmpty()
					// If the stack in this slot is not null and is fuel, set burnTimeRemaining & burnTimeInitialValue to the
					// item's burn time and decrease the stack size.
					//LogHelper.info("Using new fuel for slot " + fuelSlotNumber);
					burnTimeRemaining[i] = burnTimeInitialValue[i] = getItemBurnTime(furnaceItemStacks.get(fuelSlotNumber));

					ItemStack stack = furnaceItemStacks.get(fuelSlotNumber).getItem().getContainerItem(furnaceItemStacks.get(fuelSlotNumber));
					if (!stack.isEmpty() && furnaceItemStacks.get(fuelSlotNumber).getCount() == 1 ) {
						furnaceItemStacks.get(fuelSlotNumber).shrink(1);  // decreaseStackSize()
						furnaceItemStacks.set(fuelSlotNumber, stack.copy());
					}else {
						furnaceItemStacks.get(fuelSlotNumber).shrink(1);  // decreaseStackSize()
					}
					//++burningCount;
					inventoryChanged = true;
					// If the stack size now equals 0 set the slot contents to the items container item. This is for fuel
					// items such as lava buckets so that the bucket is not consumed. If the item dose not have
					// a container item getContainerItem returns null which sets the slot contents to null

					/*if (furnaceItemStacks.get(fuelSlotNumber).getCount() == 0) {  //getStackSize()
						furnaceItemStacks.set(fuelSlotNumber,furnaceItemStacks.get(fuelSlotNumber).getItem().getContainerItem(furnaceItemStacks.get(fuelSlotNumber)));
					}*/
				}
			}else {
				if (burnTimeRemaining[i] > 0) {
					burnTimeRemaining[i] -= (200 / COOK_TIME_FOR_COMPLETION); // TODO change reduction to whatever essence
					++burningCount;
				}
			}
		}

		if (inventoryChanged) markDirty();
		return burningCount;
	}

	private boolean canSmelt() {

		for (int inputSlot = INPUT_SLOT; inputSlot < INPUT_SLOT + TOTAL_INPUT_SLOTS; inputSlot++)	{
			if (!furnaceItemStacks.get(inputSlot).isEmpty()) {
				//item in input slot
				for (int i = 0; i < TOTAL_FUEL_SLOTS; i++) {
					if (burnTimeRemaining[i] > 0) {
						// LogHelper.info("Cook using stored fuel");
						return true;
					}
				}

				for (int fuelSlot = FUEL_SLOT; fuelSlot < FUEL_SLOT + TOTAL_FUEL_SLOTS; fuelSlot++) {
					if (!furnaceItemStacks.get(fuelSlot).isEmpty()) {
						// LogHelper.info("Cook using new fuel");
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Smelt an input item into an output slot, if possible
	 */
	private void smeltItem() {smeltItem(true);}

	/**
	 * checks that there is an item to be smelted in one of the input slots and that there is room for the result in the output slots
	 * If desired, performs the smelt
	 * @param performSmelt if true, perform the smelt.  if false, check whether smelting is possible, but don't change the inventory
	 * @return false if no items can be smelted, true otherwise
	 */
	private boolean smeltItem(boolean performSmelt)
	{
		Integer firstSuitableInputSlot = null;
		Integer firstSuitableOutputSlot = null;
		ItemStack result = ItemStack.EMPTY;  //EMPTY_ITEM

		// finds the first input slot which is smeltable and whose result fits into an output slot (stacking if possible)
		for (int inputSlot = INPUT_SLOT; inputSlot < INPUT_SLOT + TOTAL_INPUT_SLOTS; inputSlot++)	{
			if (!furnaceItemStacks.get(inputSlot).isEmpty()) {  //isEmpty()
				result = getSmeltingResultForItem(furnaceItemStacks.get(inputSlot));
				if (!result.isEmpty()) {  //isEmpty()
					// find the first suitable output slot- either empty, or with identical item that has enough space
					for (int outputSlot = OUTPUT_SLOT; outputSlot < OUTPUT_SLOT + TOTAL_OUTPUT_SLOTS; outputSlot++) {
						ItemStack outputStack = furnaceItemStacks.get(outputSlot);
						if (outputStack.isEmpty()) {  //isEmpty()
							firstSuitableInputSlot = inputSlot;
							firstSuitableOutputSlot = outputSlot;
							break;
						}

						if (outputStack.getItem() == result.getItem() && (!outputStack.getHasSubtypes() || outputStack.getMetadata() == outputStack.getMetadata())
								&& ItemStack.areItemStackTagsEqual(outputStack, result)) {
							int combinedSize = furnaceItemStacks.get(outputSlot).getCount() + result.getCount();  //getStackSize()
							if (combinedSize <= getInventoryStackLimit() && combinedSize <= furnaceItemStacks.get(outputSlot).getMaxStackSize()) {
								firstSuitableInputSlot = inputSlot;
								firstSuitableOutputSlot = outputSlot;
								break;
							}
						}
					}
					if (firstSuitableInputSlot != null) break;
				}
			}
		}

		if (firstSuitableInputSlot == null) return false;
		if (!performSmelt) return true;

		// alter input and output

		furnaceItemStacks.get(firstSuitableInputSlot).shrink(1);  // decreaseStackSize()
		if (furnaceItemStacks.get(firstSuitableInputSlot).getCount() <= 0) {
			furnaceItemStacks.set(firstSuitableInputSlot,ItemStack.EMPTY);  //getStackSize(), EmptyItem
		}
		if (furnaceItemStacks.get(firstSuitableOutputSlot).isEmpty()) {  // isEmpty()
			furnaceItemStacks.set(firstSuitableOutputSlot,result.copy()); // Use deep .copy() to avoid altering the recipe
		} else {
			int newStackSize = furnaceItemStacks.get(firstSuitableOutputSlot).getCount() + result.getCount();
			furnaceItemStacks.get(firstSuitableOutputSlot).setCount(newStackSize) ;  //setStackSize(), getStackSize()
		}
		if (currentCatalystRemaining >0) {
			currentCatalystRemaining--;
			LogHelper.info("Catalyst remaining = " + currentCatalystRemaining);
		}
		markDirty();
		return true;
	}

	// returns the smelting result for the given stack. Returns null if the given stack can not be smelted
	public static ItemStack getSmeltingResultForItem(ItemStack stack) { return FurnaceRecipes.instance().getSmeltingResult(stack); }

	// returns the number of ticks the given item will burn. Returns 0 if the given item is not a valid fuel

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
		burnTimeRemaining = Arrays.copyOf(compound.getIntArray("burnTimeRemaining"), 1/*FUEL_SLOTS_COUNT*/);
		burnTimeInitialValue = Arrays.copyOf(compound.getIntArray("burnTimeInitial"), 1/*FUEL_SLOTS_COUNT*/);

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
		compound.setTag("burnTimeRemaining", new NBTTagIntArray(burnTimeRemaining));
		compound.setTag("burnTimeInitial", new NBTTagIntArray(burnTimeInitialValue));

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

	@Nullable
	@Override
	public ItemStack removeStackFromSlot(int slotIndex) {
		ItemStack itemStack = getStackInSlot(slotIndex);
		if (!itemStack.isEmpty()) setInventorySlotContents(slotIndex, ItemStack.EMPTY);
		return itemStack;
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

	// TODO - Add IMC registration for custom fuels that dont use minecraftforge.fml.common.IFuelHandler?

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

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {

		LogHelper.info("Is Slot " + index + " Valid for stack " + stack);

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



	/*@Override
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
	}*/

	private static final byte COOK_FIELD_ID = 0;
	private static final byte FIRST_BURN_TIME_REMAINING_FIELD_ID = 1;
	private static final byte FIRST_BURN_TIME_INITIAL_FIELD_ID = FIRST_BURN_TIME_REMAINING_FIELD_ID + (byte)TOTAL_FUEL_SLOTS;
	private static final byte NUMBER_OF_FIELDS = FIRST_BURN_TIME_INITIAL_FIELD_ID + (byte)TOTAL_FUEL_SLOTS;

	@Override
	public int getField(int id) {
		if (id == COOK_FIELD_ID) return cookTime;
		if (id >= FIRST_BURN_TIME_REMAINING_FIELD_ID && id < FIRST_BURN_TIME_REMAINING_FIELD_ID + TOTAL_FUEL_SLOTS) {
			return burnTimeRemaining[id - FIRST_BURN_TIME_REMAINING_FIELD_ID];
		}
		if (id >= FIRST_BURN_TIME_INITIAL_FIELD_ID && id < FIRST_BURN_TIME_INITIAL_FIELD_ID + TOTAL_FUEL_SLOTS) {
			return burnTimeInitialValue[id - FIRST_BURN_TIME_INITIAL_FIELD_ID];
		}

		LogHelper.error("Invalid field ID in FurnaceTileEntity.getField:" + id);
		return 0;
	}

	@Override
	public void setField(int id, int value)
	{
		if (id == COOK_FIELD_ID) {
			cookTime = (short)value;
		} else if (id >= FIRST_BURN_TIME_REMAINING_FIELD_ID && id < FIRST_BURN_TIME_REMAINING_FIELD_ID + TOTAL_FUEL_SLOTS) {
			burnTimeRemaining[id - FIRST_BURN_TIME_REMAINING_FIELD_ID] = value;
		} else if (id >= FIRST_BURN_TIME_INITIAL_FIELD_ID && id < FIRST_BURN_TIME_INITIAL_FIELD_ID + TOTAL_FUEL_SLOTS) {
			burnTimeInitialValue[id - FIRST_BURN_TIME_INITIAL_FIELD_ID] = value;
		} else {
			LogHelper.error("Invalid field ID in FurnaceTileEntity.setField:" + id);
		}
	}

	@Override
	public int getFieldCount() {
		return NUMBER_OF_FIELDS;
	}
}
