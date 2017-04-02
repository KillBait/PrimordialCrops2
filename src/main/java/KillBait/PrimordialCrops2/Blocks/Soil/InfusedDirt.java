package KillBait.PrimordialCrops2.Blocks.Soil;

import KillBait.PrimordialCrops2.Blocks.PrimordialBlockMeta;
import KillBait.PrimordialCrops2.PrimordialCrops2;
import KillBait.PrimordialCrops2.Utils.ItemModelProvider;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Jon on 02/04/2017.
 */
public class InfusedDirt extends PrimordialBlockMeta{

	public static final PropertyEnum<InfusedDirt.DirtType> VARIANT = PropertyEnum.<InfusedDirt.DirtType>create("type", InfusedDirt.DirtType.class);
	//public static final PropertyBool SNOWY = PropertyBool.create("snowy");

	public InfusedDirt()
	{
		super(Material.GROUND, "infuseddirt");
		this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, InfusedDirt.DirtType.ACCIO));
		//this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
	}

	/*@Override
	public void registerItemModel(Item itemBlock) {
		PrimordialCrops2.proxy.registerItemRenderer(null, 0, name);
	}*/

	/**
	 * Get the MapColor for this Block and the given BlockState
	 */
	public MapColor getMapColor(IBlockState state)
	{
		return ((InfusedDirt.DirtType)state.getValue(VARIANT)).getColor();
	}

	/**
	 * Get the actual Block state of this Block at the given position. This applies properties not visible in the
	 * metadata, such as fence connections.
	 */
	/*public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
	{
		if (state.getValue(VARIANT) == InfusedDirt.DirtType.PODZOL)
		{
			Block block = worldIn.getBlockState(pos.up()).getBlock();
			state = state.withProperty(SNOWY, Boolean.valueOf(block == Blocks.SNOW || block == Blocks.SNOW_LAYER));
		}

		return state;
	}*/

	/**
	 * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
	 */
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list)
	{
		list.add(new ItemStack(this, 1, InfusedDirt.DirtType.ACCIO.getMetadata()));
		list.add(new ItemStack(this, 1, InfusedDirt.DirtType.CRUCIO.getMetadata()));
		list.add(new ItemStack(this, 1, InfusedDirt.DirtType.IMPERIO.getMetadata()));
		list.add(new ItemStack(this, 1, InfusedDirt.DirtType.ZIVICIO.getMetadata()));
	}

	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
	{
		return new ItemStack(this, 1, ((InfusedDirt.DirtType)state.getValue(VARIANT)).getMetadata());
	}

	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(VARIANT, InfusedDirt.DirtType.byMetadata(meta));
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	public int getMetaFromState(IBlockState state)
	{
		return ((InfusedDirt.DirtType)state.getValue(VARIANT)).getMetadata();
	}

	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {VARIANT});
	}

	/**
	 * Gets the metadata of the item this Block can drop. This method is called when the block gets destroyed. It
	 * returns the metadata of the dropped item based on the old metadata of the block.
	 */
	public int damageDropped(IBlockState state)
	{
		InfusedDirt.DirtType InfusedDirt$dirttype = (InfusedDirt.DirtType)state.getValue(VARIANT);

		/*if (InfusedDirt$dirttype == InfusedDirt.DirtType.PODZOL)
		{
			InfusedDirt$dirttype = InfusedDirt.DirtType.DIRT;
		}*/

		return InfusedDirt$dirttype.getMetadata();
	}

	public static enum DirtType implements IStringSerializable
	{
		ACCIO(0, "accio", "default", MapColor.DIRT),
		CRUCIO(1, "crucio", MapColor.DIRT),
		IMPERIO(2, "imperio", MapColor.OBSIDIAN),
		ZIVICIO(3, "zivicio", MapColor.OBSIDIAN);

		private static final InfusedDirt.DirtType[] METADATA_LOOKUP = new InfusedDirt.DirtType[values().length];
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

		public static InfusedDirt.DirtType byMetadata(int metadata)
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
			for (InfusedDirt.DirtType InfusedDirt$dirttype : values())
			{
				METADATA_LOOKUP[InfusedDirt$dirttype.getMetadata()] = InfusedDirt$dirttype;
			}
		}
	}
}
