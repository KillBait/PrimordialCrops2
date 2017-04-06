package KillBait.PrimordialCrops2.Blocks;

import KillBait.PrimordialCrops2.Blocks.Base.PrimordialBlockMeta;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Jon on 02/04/2017.
 */


public class InfusedDirtBlock extends PrimordialBlockMeta {

	public static final PropertyEnum<InfusedDirtBlock.DirtType> VARIANT = PropertyEnum.<InfusedDirtBlock.DirtType>create("type", InfusedDirtBlock.DirtType.class);

	public InfusedDirtBlock()
	{
		super(Material.GROUND, "infuseddirt");
		this.setHardness(0.5f);
		this.setResistance(5f);
		this.setHarvestLevel("shovel", 0);
		this.setSoundType(SoundType.GROUND);
		this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, InfusedDirtBlock.DirtType.ACCIO));
	}

	@Override
	public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable) {
		EnumPlantType plantType = plantable.getPlantType(world, pos.up());
		switch (plantType) {
			case Desert:
				return true;
			case Nether:
				return true;
			case Crop:
				return true;
			case Cave:
				return true;
			case Plains:
				return true;
			case Water:
				return false;
			case Beach:
				return true;
		}

		return false;
	}

	public boolean hasCrops(World worldIn, BlockPos pos) {
		Block block = worldIn.getBlockState(pos.up()).getBlock();
		return block instanceof net.minecraftforge.common.IPlantable && canSustainPlant(worldIn.getBlockState(pos), worldIn, pos, net.minecraft.util.EnumFacing.UP, (net.minecraftforge.common.IPlantable) block);
	}

	@Override
	public MapColor getMapColor(IBlockState state)
	{
		return ((InfusedDirtBlock.DirtType)state.getValue(VARIANT)).getColor();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list)
	{
		list.add(new ItemStack(this, 1, InfusedDirtBlock.DirtType.ACCIO.getMetadata()));
		list.add(new ItemStack(this, 1, InfusedDirtBlock.DirtType.CRUCIO.getMetadata()));
		list.add(new ItemStack(this, 1, InfusedDirtBlock.DirtType.IMPERIO.getMetadata()));
		list.add(new ItemStack(this, 1, InfusedDirtBlock.DirtType.ZIVICIO.getMetadata()));
	}

	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
	{
		return new ItemStack(this, 1, ((InfusedDirtBlock.DirtType)state.getValue(VARIANT)).getMetadata());
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(VARIANT, InfusedDirtBlock.DirtType.byMetadata(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return ((InfusedDirtBlock.DirtType)state.getValue(VARIANT)).getMetadata();
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {VARIANT});
	}

	@Override
	public int damageDropped(IBlockState state)
	{
		InfusedDirtBlock.DirtType InfusedDirt$dirttype = (InfusedDirtBlock.DirtType)state.getValue(VARIANT);
		return InfusedDirt$dirttype.getMetadata();
	}

	public static enum DirtType implements IStringSerializable
	{
		ACCIO(0, "accio", "default", MapColor.DIRT),
		CRUCIO(1, "crucio", MapColor.DIRT),
		IMPERIO(2, "imperio", MapColor.DIRT),
		ZIVICIO(3, "zivicio", MapColor.DIRT);

		private static final InfusedDirtBlock.DirtType[] METADATA_LOOKUP = new InfusedDirtBlock.DirtType[values().length];
		private final int metadata;
		private final String name;
		private final String unlocalizedName;
		private final MapColor color;

		private DirtType(int metadataIn, String nameIn, MapColor color)
		{
			this(metadataIn, nameIn, nameIn, color);
		}

		private DirtType(int metadataIn, String nameIn, String unlocalizedNameIn, MapColor color)
		{
			this.metadata = metadataIn;
			this.name = nameIn;
			this.unlocalizedName = unlocalizedNameIn;
			this.color = color;
		}

		public int getMetadata()
		{
			return this.metadata;
		}

		public String getUnlocalizedName()
		{
			return this.unlocalizedName;
		}

		public MapColor getColor()
		{
			return this.color;
		}

		public String toString()
		{
			return this.name;
		}

		public static InfusedDirtBlock.DirtType byMetadata(int metadata)
		{
			if (metadata < 0 || metadata >= METADATA_LOOKUP.length)
			{
				metadata = 0;
			}

			return METADATA_LOOKUP[metadata];
		}

		public String getName()
		{
			return this.name;
		}

		static
		{
			for (InfusedDirtBlock.DirtType InfusedDirt$dirttype : values())
			{
				METADATA_LOOKUP[InfusedDirt$dirttype.getMetadata()] = InfusedDirt$dirttype;
			}
		}
	}
}
