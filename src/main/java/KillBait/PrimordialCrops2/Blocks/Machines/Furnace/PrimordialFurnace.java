package KillBait.PrimordialCrops2.Blocks.Machines.Furnace;

import KillBait.PrimordialCrops2.Blocks.Base.PrimordialBlockContainer;
import KillBait.PrimordialCrops2.Compat.WAILA.WailaInfoProvider;
import KillBait.PrimordialCrops2.PrimordialCrops2;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.SpecialChars;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;

import static KillBait.PrimordialCrops2.Info.MODID;

	// TODO New Model and Texture ?????

public class PrimordialFurnace extends PrimordialBlockContainer {

	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyBool WORKING = PropertyBool.create("enabled");
	//public static String furnaceName;
	public static final int GUI_ID = 1; // ID 0 reserved for in-game book


	public PrimordialFurnace() {
		super(Material.ROCK, "primordialfurnace");
		this.setHardness(4f);
		this.setSoundType(SoundType.STONE);
		GameRegistry.registerTileEntity(FurnaceTileEntity.class, MODID + "_tileentity");
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new FurnaceTileEntity();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side,float hitX, float hitY, float hitZ) {
		// Only execute on the server
		if (!world.isRemote) {
			player.openGui(PrimordialCrops2.instance, GUI_ID, world, pos.getX(), pos.getY(), pos.getZ());
		}
		return true;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		world.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntity tileEntity = worldIn.getTileEntity(pos);
		if (tileEntity instanceof IInventory) {
			InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory) tileEntity);
		}
		// super call removes the tileentity
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState()
				.withProperty(FACING, EnumFacing.getFront((meta & 3) + 2))
				.withProperty(WORKING, (meta & 8) != 0);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return (state.getValue(FACING).getIndex() - 2) + (state.getValue(WORKING) ? 8 : 0);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, WORKING);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState iBlockState) {
		return EnumBlockRenderType.MODEL;
	}

	@SideOnly(Side.CLIENT)
	@SuppressWarnings("incomplete-switch")
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if (stateIn.getValue(WORKING)) {
			EnumFacing enumfacing = (EnumFacing) stateIn.getValue(FACING);
			double d0 = (double) pos.getX() + 0.5D;
			double d1 = (double) pos.getY() + rand.nextDouble() * 6.0D / 16.0D;
			double d2 = (double) pos.getZ() + 0.5D;
			double d3 = 0.52D;
			double d4 = rand.nextDouble() * 0.6D - 0.3D;

			if (rand.nextDouble() < 0.1D) {
				worldIn.playSound((double) pos.getX() + 0.5D, (double) pos.getY(), (double) pos.getZ() + 0.5D, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
			}

			switch (enumfacing) {
				case WEST:
					worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 - 0.52D, d1, d2 + d4, 0.0D, 0.0D, 0.0D, new int[0]);
					worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 - 0.52D, d1, d2 + d4, 0.0D, 0.0D, 0.0D, new int[0]);
					break;
				case EAST:
					worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + 0.52D, d1, d2 + d4, 0.0D, 0.0D, 0.0D, new int[0]);
					worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 + 0.52D, d1, d2 + d4, 0.0D, 0.0D, 0.0D, new int[0]);
					break;
				case NORTH:
					worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d4, d1, d2 - 0.52D, 0.0D, 0.0D, 0.0D, new int[0]);
					worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 + d4, d1, d2 - 0.52D, 0.0D, 0.0D, 0.0D, new int[0]);
					break;
				case SOUTH:
					worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d4, d1, d2 + 0.52D, 0.0D, 0.0D, 0.0D, new int[0]);
					worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 + d4, d1, d2 + 0.52D, 0.0D, 0.0D, 0.0D, new int[0]);
			}
		}
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		if (state.getValue(WORKING)) {
			lightValue = 15;
		}else{
			lightValue = 0;
		}
		return lightValue;
	}


	public static void setState(boolean active, World worldIn, BlockPos pos)
	{
		IBlockState iblockstate = worldIn.getBlockState(pos);
		TileEntity tileentity = worldIn.getTileEntity(pos);

		worldIn.setBlockState(pos, iblockstate.withProperty(WORKING, active), 3);

		if (tileentity != null)
		{
			tileentity.validate();
			worldIn.setTileEntity(pos, tileentity);
		}
	}
}
