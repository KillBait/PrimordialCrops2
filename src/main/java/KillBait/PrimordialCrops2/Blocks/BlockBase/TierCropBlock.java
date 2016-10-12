package KillBait.PrimordialCrops2.Blocks.BlockBase;

import KillBait.PrimordialCrops2.Compat.TheOneProbe.TOPInfoProvider;
import KillBait.PrimordialCrops2.Compat.WAILA.WailaInfoProvider;
import KillBait.PrimordialCrops2.Items.BaseItem.PrimordialItemBase;
import KillBait.PrimordialCrops2.Registry.ModItems;
import KillBait.PrimordialCrops2.Utils.LogHelper;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.IGrowable;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

import java.util.List;
import java.util.Random;

/**
 * Created by Jon on 30/09/2016.
 */
public class TierCropBlock extends BlockCrops implements IGrowable, IPlantable, TOPInfoProvider, WailaInfoProvider {

	public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 3);
	public static final PropertyInteger TIER = PropertyInteger.create("tier", 0, 3);
	private static final AxisAlignedBB[] CROP_AABB = new AxisAlignedBB[]{new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.30D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.45D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.55D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.80D, 1.0D)};
	//public final String regName;

	public TierCropBlock() {
		//this.regName = regName;
		//this.setUnlocalizedName(regName);
		//this.setRegistryName(regName);
		this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, Integer.valueOf(0)).withProperty(TIER, Integer.valueOf(0)));
	}

	protected PropertyInteger getAgeProperty() {
		return AGE;
	}

	protected PropertyInteger getTierProperty() {
		return TIER;
	}

	public int getMaxAge() {
		return 3;
	}

	public int getMaxTier() {
		return 3;
	}

	protected Item getSeed() {
		return ModItems.minicioSeed;
	}

	protected Item getCrop() {
		return ModItems.minicioEssence;
	}

	@Override
	public void grow(World worldIn, BlockPos pos, IBlockState state) {
		int i = this.getAge(state) + this.getBonemealAgeIncrease(worldIn);
		int j = this.getMaxAge();

		if (i > j) {
			i = j;
		}

		worldIn.setBlockState(pos, this.withAge(i, state), 2);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		super.updateTick(worldIn, pos, state, rand);

		if (worldIn.getLightFromNeighbors(pos.up()) >= 9) {
			int i = this.getAge(state);

			if (i < this.getMaxAge()) {
				float f = getGrowthChance(this, worldIn, pos);

				if (rand.nextInt((int) (25.0F / f) + 1) == 0) {
					worldIn.setBlockState(pos, this.withAge(i + 1, state), 2);
				}
			}
		}
	}

	public IBlockState withAge(int age, IBlockState state) {
		IBlockState newstate = this.getStateFromMeta(newMetaWithAge(state, age));
		return newstate;
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		List<ItemStack> ret = new java.util.ArrayList<ItemStack>();

		Random rnd = world instanceof World ? ((World) world).rand : new Random();

		int age = getMetaFromState(state);
		int extraseed;

		// how many essence/seeds to give
		int count = quantityDropped(state, fortune, rnd);
		for (int i = 0; i < count; i++) {
			Item item = this.getItemDropped(state, rnd, fortune);
			if (item != null) {
				if (item instanceof PrimordialItemBase) {
					//plant fully grown, give essence
					ret.add(new ItemStack(item, calcdrop(state)));
				} else {
					//plant isnt fully grown, give back seed
					ItemStack stackNBT = new ItemStack(item, 1);
					stackNBT.setTagCompound(new NBTTagCompound());
					stackNBT.getTagCompound().setInteger("Tier", state.getValue(TIER).intValue());
					ret.add(stackNBT);
				}
			}
		}

		// do we give extra seed if fully grown
		if (age >= getMaxAge()) {
			if ((Math.random() * 100) <= 20/*PrimordialConfig.regularSeedExtraChance*/) {
				extraseed = 1 + (1 * fortune);
			} else {
				extraseed = 0;
			}
			ItemStack stackNBT = new ItemStack(this.getSeed(), 1 + extraseed);
			stackNBT.setTagCompound(new NBTTagCompound());
			stackNBT.getTagCompound().setInteger("Tier", state.getValue(TIER).intValue());
			ret.add(stackNBT);
		}

		return ret;
	}


	protected int getBonemealAgeIncrease(World worldIn) {
		return super.getBonemealAgeIncrease(worldIn) / 3;
	}

	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[]{AGE, TIER});
	}

	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return CROP_AABB[((Integer) state.getValue(this.getAgeProperty())).intValue()];
	}

	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(AGE, Integer.valueOf(meta & 3)).withProperty(TIER, Integer.valueOf((meta & 15) >> 2));
	}

	public int getMetaFromState(IBlockState state) {
		int i = 0;
		i = i | ((Integer) state.getValue(AGE)).intValue();
		i = i | ((Integer) state.getValue(TIER)).intValue() << 2;
		return i;
	}

	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
									ItemStack stack, EnumFacing side, float hitX, float hitY, float hitZ) {

		//LogHelper.info(getStateFromMeta(getMetaFromState(state)));
		//LogHelper.info(calcdrop(state));

		return false;
	}

	public int newMetaWithAge(IBlockState state, int newage) {
		int i = 0;
		i = i | newage;
		i = i | ((Integer) state.getValue(TIER)).intValue() << 2;
		return i;
	}

	public int newMetaWithTier(IBlockState state, int newtier) {
		int i = 0;
		i = i | ((Integer) state.getValue(AGE)).intValue();
		i = i | newtier << 2;
		return i;
	}

	//yes... this is bad programing and lazy, but worked during testing, to quote the proverb (if it ain't broke, don't fix it)
	public int calcdrop(IBlockState state) {
		switch ((Integer) state.getValue(TIER).intValue()) {
			case 0:
				return 1;
			case 1:
				return 2;
			case 2:
				return 4;
			case 3:
				return 8;
			default:
				return 1;
		}
	}

	@Override
	public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
		probeInfo.horizontal()
				//.item(new ItemStack(Items.CLOCK))
				.text(TextFormatting.GREEN + "Tier: " + ((Integer) blockState.getValue(TIER) + 1));
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		Block block = accessor.getBlock();
		if (block instanceof TierCropBlock) {
			IBlockState tbc = accessor.getBlockState();
			currenttip.add(TextFormatting.GRAY + "Tier: " + (tbc.getValue(TIER) + 1));
		}
		return currenttip;
	}

	/*public Item getDrop()

	public Item getItemDropped(IBlockState blockstate, Random random, int fortune) {
		return this.drop;
	}

	public int damageDropped(IBlockState blockstate) {
		return this.meta;
	}*/
}