package KillBait.PrimordialCrops2.Blocks;

import KillBait.PrimordialCrops2.Compat.TheOneProbe.TOPInfoProvider;
import KillBait.PrimordialCrops2.Compat.WAILA.WailaInfoProvider;
import KillBait.PrimordialCrops2.Registry.ModBlocks;
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
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

import java.util.List;
import java.util.Random;

/**
 * Created by Jon on 18/10/2016.
 */
public class PrimordialCropBlock extends BlockCrops implements IGrowable, IPlantable, TOPInfoProvider, WailaInfoProvider {
	public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 3);
	private static final AxisAlignedBB[] CROP_AABB = new AxisAlignedBB[]{new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875, 0.30D, 0.875D), new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.45D, 0.875D), new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.55D, 0.875D), new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.80D, 0.875D)};
	//private static int tickcount = 0;

	public PrimordialCropBlock() {
		//this.regName = regName;
		//this.setUnlocalizedName(regName);
		//this.setRegistryName(regName);
		this.setResistance(30f);
		this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, Integer.valueOf(0)));
	}

	protected PropertyInteger getAgeProperty() {
		return AGE;
	}

	public int getMaxAge() {
		return 3;
	}

	@Override
	protected Item getSeed() {
		return ModItems.minicioSeed;
	}

	@Override
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
		worldIn.setBlockState(pos, this.getStateFromMeta(i), 2);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		super.updateTick(worldIn, pos, state, rand);

		if (worldIn.getLightFromNeighbors(pos.up()) >= 9) {
			int i = this.getAge(state);

			if (i < this.getMaxAge()) {
				float f = getGrowthChance(this, worldIn, pos);

				if (rand.nextInt((int) (25.0F / f) + 1) == 0) {
					worldIn.setBlockState(pos, this.getStateFromMeta(i + 1), 2);
				}
			}
		}
	}

	/*public IBlockState getNewStateWithAge(int age, IBlockState state) {
		IBlockState newstate = this.getStateFromMeta(newMetaWithAge(state, age));
		return newstate;
	}

	public IBlockState getNewStateWithTier(int tier, IBlockState state) {
		IBlockState newstate = this.getStateFromMeta(newMetaWithTier(state, tier));
		return newstate;
	}*/

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		List<ItemStack> ret = new java.util.ArrayList<ItemStack>();
		int extraseed = 0;


		//checkfarmland(world, pos);

		// check if crop fully grown
		if (getAge(state) >= 3) {
			// give drops
			//LogHelper.info("giving drops");
			ret.add(new ItemStack(this.getCrop(), getFarmlandBonus(world, pos)));
			// calc random chance of extra seed for crop being fully grown
			if ((Math.random() * 100) <= 20/*PrimordialConfig.regularSeedExtraChance*/) {
				extraseed = 1 + (1 * fortune);
			}
		}


		//LogHelper.info("getDrops Tier = " + state.getValue(TIER).intValue());
		ItemStack stack = new ItemStack(this.getSeed(), 1 + extraseed);//, state.getValue(TIER).intValue());
		//stackNBT.setTagCompound(new NBTTagCompound());
		//stackNBT.getTagCompound().setInteger("Tier", state.getValue(TIER).intValue());
		ret.add(stack);
		//tickcount = 0;

		return ret;
	}

	protected int getBonemealAgeIncrease(World worldIn) {
		return super.getBonemealAgeIncrease(worldIn) / 3;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[]{AGE});
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return CROP_AABB[((Integer) state.getValue(this.getAgeProperty())).intValue()];
	}

	/*@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(AGE, Integer.valueOf(meta & 3)).withProperty(TIER, Integer.valueOf((meta & 15) >> 2));
	}*/

	/*public int getMetaFromState(IBlockState state) {
		int i = 0;
		i = i | ((Integer) state.getValue(AGE)).intValue();
		i = i | ((Integer) state.getValue(TIER)).intValue() << 2;
		return i;
	}*/

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
									EnumFacing side, float hitX, float hitY, float hitZ) {
		//LogHelper.info("block activated with meta" + getMetaFromState(state));
		/*if (this.getAge(state) >= 3) {
			if(world.isRemote) {
				return true;
			}
			final ItemStack savedStack = new ItemStack(this.getCrop(), calcdrop(state));
			world.setBlockState(pos, state.withProperty(AGE, 0), 7);
			final EntityItem entItem = new EntityItem(world, player.posX, player.posY - 1D, player.posZ, savedStack);
			world.spawnEntityInWorld(entItem);
			entItem.onCollideWithPlayer(player);
			return true;
		}*/
		return false;
	}

	public int getFarmlandBonus(IBlockAccess world, BlockPos pos) {
		Block getblock = world.getBlockState(pos.down()).getBlock();
		if (getblock == ModBlocks.accioFarmland) {
			LogHelper.info("Accio infused farmland detected");
			return 2;
		}
		if (getblock == ModBlocks.crucioFarmland) {
			LogHelper.info("Crucio infused farmland detected");
			return 4;
		}
		if (getblock == ModBlocks.imperioFarmland) {
			LogHelper.info("Imperio infused farmland detected");
			return 6;
		}
		if (getblock == ModBlocks.zivicioFarmland) {
			LogHelper.info("Zivicio infused farmland detected");
			return 8;
		}

		return 0;
	}

	@Override
	public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
		probeInfo.horizontal()
				.item(new ItemStack(Items.CLOCK));
		//.text(TextFormatting.GREEN + "Tier: " + ((Integer) blockState.getValue(TIER) + 1));
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		Block block = accessor.getBlock();
		if (block instanceof TierCropBlock) {
			IBlockState tbc = accessor.getBlockState();
			//currenttip.add(TextFormatting.GRAY + "Tier: " + (tbc.getValue(TIER) + 1));
		}
		return currenttip;
	}
}
