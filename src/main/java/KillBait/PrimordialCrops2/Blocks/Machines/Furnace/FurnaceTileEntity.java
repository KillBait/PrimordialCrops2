package KillBait.PrimordialCrops2.Blocks.Machines.Furnace;

import KillBait.PrimordialCrops2.Items.Essence.CraftEssence;
import KillBait.PrimordialCrops2.Utils.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FurnaceTileEntity extends TileEntity implements ITickable{

	private static final int CATALYST_SLOT = 0;
	private static final int FUEL_SLOT = 1;
	private static final int INPUT_SLOT = 2;
	private static final int OUTPUT_SLOT = 3;
	private static final int TOTAL_SLOTS = 4;

	/** The number of burn ticks remaining on the curret piece of fuel */
	private int burnTimeRemaining;
	/** The initial fuel value of the currently burning fuel (in ticks of burn duration) */
	private int burnTimeInitialValue;

	private int[] ticksPerCraftScale = {200,100,50,25,10,1};

	private int[] maxUsesPerCatalyst = {0,8,8,8,8,8}; // For Testing purposes only!!
	//public int[] maxUsesPerCatalyst = {0,64,128,256,512,1024};

	public short currentCatalystRemaining;
	public short currentCatalystType; // 0 = none. 1= minicio, 2 = accio, 3 = crucio, 4 = imperio, 5 = zivicio

	// The number of ticks the current item has been cooking
	private short cookTime;

	private short lastCookTime;

	private boolean emitParticles = false;

	// ---------------------------------------
	// meh.. ISidedInventorys are much easier to do than capabilites
	//
	// i decided i wanted none sided extract/insert because having to use a certain side limits the end users choices
	//
	// with ISided you have a completely seperate set of functions to handle automation from the GUI/TileEntity stuff
	// its pretty simple to code something like hoppers can only insert in slot 0, job done...
	//
	// How do i get around this, the hacky way using 2 ItemStackHandlers
	//
	// one private ItemStackHandler used by the TileEntity/Container/GUI with almost no restrictions
	// (except for inserting into the output slot)
	//
	// a second ItemStackHandler that capabilites exposes to automation, add your insert/extract logic to it
	// and then pass the function back back to the first private ItemStackHandler so it can access the inventory
	//
	// it's not good coding by a long shot, but works, good enough for me!!!

	public ItemStackHandler furnaceItemStackHandler = new ItemStackHandler(TOTAL_SLOTS) {
		@Override
		protected void onContentsChanged(int slot) {
			// We need to tell the tile entity that something has changed so that the furnace contents is persisted
			FurnaceTileEntity.this.markDirty();
		}

		@Override
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
			if (slot == 0 && isItemValidForCatalystSlot(stack)) {
				//LogHelper.info("Valid catalyst");
				return super.insertItem(slot, stack, simulate);
			}

			if (slot == 1 && isItemValidForFuelSlot(stack)) {
				//LogHelper.info("Valid fuel");
				return super.insertItem(slot, stack, simulate);
			}

			if (slot == 2 && isItemValidForInputSlot(stack)) {
				//LogHelper.info("Valid Smeltable Item");
				return super.insertItem(slot, stack, simulate);
			}

			return stack;
		}
	};

	private ItemStackHandler outputItemStackHandler = new ItemStackHandler(TOTAL_SLOTS) {
		@Override
		protected void onContentsChanged(int slot) {
			// We need to tell the tile entity that something has changed so that the furnace contents is persisted
			FurnaceTileEntity.this.markDirty();
		}

		@Override
		public void setStackInSlot(int slot, @Nonnull ItemStack stack)
		{
			furnaceItemStackHandler.setStackInSlot(slot, stack);
		}

		@Override
		public int getSlots()
		{
			return furnaceItemStackHandler.getSlots();
		}

		@Override
		@Nonnull
		public ItemStack getStackInSlot(int slot) {
			return furnaceItemStackHandler.getStackInSlot(slot);
		}

		@Override
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
			if (slot != OUTPUT_SLOT) {
				//LogHelper.info("Allowed insert into slot " + slot + " in outputItemstackHandler");
				return furnaceItemStackHandler.insertItem(slot, stack, simulate);
			}
			return stack;
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			if (slot == OUTPUT_SLOT) {
				//LogHelper.info("Allowed extract from slot " + slot + " in outputItemstackHandler");
				return furnaceItemStackHandler.extractItem(slot, amount, simulate);
			}
			//LogHelper.info("Denied extract from slot " + slot + " in outputItemstackHandler");
			return ItemStack.EMPTY;
		}
	};

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return (T) outputItemStackHandler;
		}
		return super.getCapability(capability, facing);
	}

	//
	// -----------------------------------------
	//

	public FurnaceTileEntity() {}

	@Override
	public void update() {

		// Check if there's something in the input slot and fuel is available to smelt
		if (canSmelt()) {
			// if cooktime = 0 we are starting a new smelt cycle, check if any catalyst to use.
			if (cookTime <= 0) checkCatalyst();

			if (useFuel()) {
				cookTime += 1;
			}

			if (cookTime < 0) cookTime = 0;

			if (cookTime >= ticksPerCraftScale[currentCatalystType]) {
				smeltItem();
				cookTime = 0;
			}
		}

		if (!world.isRemote) {
			if (cookTime != lastCookTime) {
				if (!emitParticles) {
					PrimordialFurnace.setState(true, this.world, this.pos);
					emitParticles = true;
				}
			}else{
				if (emitParticles) {
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
			if (!furnaceItemStackHandler.getStackInSlot(CATALYST_SLOT).isEmpty() && furnaceItemStackHandler.getStackInSlot(CATALYST_SLOT).getItem() instanceof CraftEssence) {
				int catalystMeta = furnaceItemStackHandler.getStackInSlot(CATALYST_SLOT).getMetadata();
				//meta++;
				currentCatalystType = (short) (catalystMeta + 1);
				currentCatalystRemaining = (short) maxUsesPerCatalyst[currentCatalystType];

				furnaceItemStackHandler.getStackInSlot(CATALYST_SLOT).shrink(1);
				this.markDirty();
			} else {
				currentCatalystType = 0;
			}
		}
	}

	private boolean useFuel() {

		boolean fuelRemaining = false;
		boolean inventoryChanged = false;

		// TODO handle burnable items that have a value less than 200 fuel (i.e. sticks) on per tick crafting

		if (burnTimeRemaining <= 0) {
			if (!furnaceItemStackHandler.getStackInSlot(FUEL_SLOT).isEmpty() && getItemBurnTime(furnaceItemStackHandler.getStackInSlot(FUEL_SLOT)) > 0) {

				burnTimeInitialValue = getItemBurnTime(furnaceItemStackHandler.getStackInSlot(FUEL_SLOT));
				burnTimeRemaining = burnTimeInitialValue - (200 / ticksPerCraftScale[currentCatalystType]);

				// check if fuel in slot is a container item, i.e.  if it's a lava bucket, return the empty bucket
				// if not a container item just reduce the fuel in slot by one.
				ItemStack stack = furnaceItemStackHandler.getStackInSlot(FUEL_SLOT).getItem().getContainerItem(furnaceItemStackHandler.getStackInSlot(FUEL_SLOT));
				if (!stack.isEmpty() && furnaceItemStackHandler.getStackInSlot(FUEL_SLOT).getCount() == 1 ) {
					furnaceItemStackHandler.getStackInSlot(FUEL_SLOT).shrink(1);  // remove the stack
					furnaceItemStackHandler.setStackInSlot(FUEL_SLOT, stack.copy()); // add the empty container
				}else {
					furnaceItemStackHandler.getStackInSlot(FUEL_SLOT).shrink(1);
				}
				inventoryChanged = true;
				fuelRemaining = true;
			}
		}else {
			burnTimeRemaining -= (200 / ticksPerCraftScale[currentCatalystType]);
			fuelRemaining = true;
		}

		if (inventoryChanged) markDirty();

		return fuelRemaining;
	}

	private boolean canSmelt() {
		// is item in input slot?
		if (!furnaceItemStackHandler.getStackInSlot(INPUT_SLOT).isEmpty() ) {
			// check if output slot has anything in it and how many, if 1 less than max stack size carry on
			if (furnaceItemStackHandler.getStackInSlot(OUTPUT_SLOT).getCount() < furnaceItemStackHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize()) {
				// if there is fuel in the slot or there is fuel stored return true
				if (burnTimeRemaining > 0 || !furnaceItemStackHandler.getStackInSlot(FUEL_SLOT).isEmpty()) {
					// Cook using new fuel
					return true;
				}
			}
		}
		return false;
	}

	// check if an item to be smelted is in the input slot and that there is room for the result in the output slots

	private void smeltItem()
	{
		// check if something in input slot
		if (!furnaceItemStackHandler.getStackInSlot(INPUT_SLOT).isEmpty()) {
			// get the result of smelting the input slot item
			ItemStack smeltResult = FurnaceRecipes.instance().getSmeltingResult(furnaceItemStackHandler.getStackInSlot(INPUT_SLOT));
			// check if smeltResult is a valid item ( will be empty if not )
			if (!smeltResult.isEmpty()) {
				// get the contents of output slot			}
				ItemStack outputStack = furnaceItemStackHandler.getStackInSlot(OUTPUT_SLOT);
				// check if output slot is empty
				if (outputStack.isEmpty()) {
					// set output slot to smeltResult, reduce the input slot amount
					furnaceItemStackHandler.setStackInSlot(OUTPUT_SLOT, smeltResult.copy());
					furnaceItemStackHandler.getStackInSlot(INPUT_SLOT).shrink(1);
					// not sure if needed, but, check if input stack is used up and set it to an empty stack if it is
					if (furnaceItemStackHandler.getStackInSlot(INPUT_SLOT).getCount() <= 0) {
						furnaceItemStackHandler.setStackInSlot(INPUT_SLOT, ItemStack.EMPTY);
					}
					// check if any catalyst is left, if so, remove 1
					if (currentCatalystRemaining > 0) {
						currentCatalystRemaining--;
					}
					markDirty();
				} else {
					if (outputStack.getItem() == smeltResult.getItem() && (!outputStack.getHasSubtypes() || outputStack.getMetadata() == outputStack.getMetadata())
							&& ItemStack.areItemStackTagsEqual(outputStack, smeltResult)) {

						int combinedSize = furnaceItemStackHandler.getStackInSlot(OUTPUT_SLOT).getCount() + smeltResult.getCount();
						if (combinedSize <= furnaceItemStackHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize()) {
							// remove 1 from input stack
							furnaceItemStackHandler.getStackInSlot(INPUT_SLOT).shrink(1);
							// combine the number of items in input and output slots
							int newStackSize = furnaceItemStackHandler.getStackInSlot(OUTPUT_SLOT).getCount() + smeltResult.getCount();
							// set output slot to new stacksize
							furnaceItemStackHandler.getStackInSlot(OUTPUT_SLOT).setCount(newStackSize);
							// not sure if needed, but, check if input stack is used up and set it to an empty stack if it is
							if (furnaceItemStackHandler.getStackInSlot(INPUT_SLOT).getCount() <= 0) {
								furnaceItemStackHandler.setStackInSlot(INPUT_SLOT, ItemStack.EMPTY);
							}
							// check if any catalyst is left, if so, remove 1 from total
							if (currentCatalystRemaining > 0) {
								currentCatalystRemaining--;
							}
							markDirty();
						}
					}
				}
			}
		}
	}

	private String getName() { return "container.primordialfurnace.name"; }

	private boolean hasCustomName() {
		return false;
	}

	@Nullable
	@Override
	public ITextComponent getDisplayName() {
		if (this.hasCustomName()) {
			return new TextComponentString(this.getName());
		}else {
			return new TextComponentTranslation(this.getName());
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {

		super.readFromNBT(compound);

		if (compound.hasKey("items")) {
			furnaceItemStackHandler.deserializeNBT(compound.getCompoundTag("items"));
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

		compound.setTag("items",furnaceItemStackHandler.serializeNBT());

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

	public static ItemStack getSmeltingResult(ItemStack stack) { return FurnaceRecipes.instance().getSmeltingResult(stack); }

	public static boolean isItemFuel(ItemStack stack) {
		// returns the number of ticks the supplied fuel will keep the furnace burning, or 0 if the item isn't fuel
		return getItemBurnTime(stack) > 0;
	}

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

	// Return true if the given stack is allowed to be inserted in the given slot
	private static boolean isItemValidForFuelSlot(ItemStack itemStack) {
		return isItemFuel(itemStack);
	}

	// Return true if the given stack is allowed to be inserted in the given slot
	private static boolean isItemValidForInputSlot(ItemStack itemStack) {
		return !getSmeltingResult(itemStack).isEmpty();
	}

	// Return true if the given stack is allowed to be inserted in the given slot
	/*public static boolean isItemValidForOutputSlot(ItemStack itemStack) {
		return false;
	}*/

	// Return true if the given stack is allowed to be inserted in the given slot
	public static boolean isItemValidForCatalystSlot(ItemStack itemStack) {
		return itemStack.getItem() instanceof CraftEssence;
	}

	@Override
	@Nullable
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound updateTagDescribingTileEntityState = getUpdateTag();
		final int METADATA = 0;
		return new SPacketUpdateTileEntity(this.pos, METADATA, updateTagDescribingTileEntityState);
	}

	// Client
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

	// Client
	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		this.readFromNBT(tag);
	}

	private static final byte COOKTIME_ID = 0;
	private static final byte BURN_TIME_INITIAL_ID = 1;
	private static final byte BURN_TIME_REMAINING_ID = 2;
	private static final byte CATALYST_TYPE_ID = 3;
	private static final byte CATALYST_REMAINING_ID = 4;
	private static final byte NUMBER_OF_FIELDS = 5;

	//@Override
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

	//@Override
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

	//@Override
	public int getFieldCount() {
		return NUMBER_OF_FIELDS;
	}

	public double fractionOfCatalystRemaining()
	{
		if (currentCatalystRemaining <=0) return 0;
		double fraction = currentCatalystRemaining / (double)maxUsesPerCatalyst[currentCatalystType];
		return MathHelper.clamp(fraction, 0.0, 1.0);
	}

	public double fractionOfFuelRemaining()
	{
		if (burnTimeInitialValue <= 0 ) return 0;
		double fraction = burnTimeRemaining / (double)burnTimeInitialValue;
		return MathHelper.clamp(fraction, 0.0, 1.0);
	}

	/*public int secondsOfFuelRemaining(int fuelSlot)
	{
		if (burnTimeRemaining <= 0 ) {
			return 0;
		}else {
			return burnTimeRemaining / 20; // 20 ticks per second
		}

	}*/

	public double fractionOfCookTimeComplete()
	{
		double fraction = cookTime / (double)ticksPerCraftScale[currentCatalystType];
		return MathHelper.clamp(fraction, 0.0, 1.0);
	}
}
