package KillBait.PrimordialCrops2.Blocks.Base;

import KillBait.PrimordialCrops2.PrimordialCrops2;
import KillBait.PrimordialCrops2.Utils.ItemModelProvider;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by Jon on 22/10/2016.
 */
public class PrimordialBlockContainer extends BlockContainer implements ItemModelProvider {

	protected String name;

	protected PrimordialBlockContainer(Material materialIn, String unlocalizedName) {
		this(materialIn, materialIn.getMaterialMapColor(), unlocalizedName);
	}

	protected PrimordialBlockContainer(Material materialIn, MapColor color, String unlocalizedName) {
		super(materialIn, color);
		this.name = unlocalizedName;
		this.isBlockContainer = true;
		this.setUnlocalizedName(unlocalizedName);
		this.setRegistryName(unlocalizedName);
		this.setCreativeTab(PrimordialCrops2.PrimordialCrops2);
	}

	// You MUST Override this in your class and create the tile entity there
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return null;
	}

	@Override
	public void registerItemModel(Item itemBlock) {
		PrimordialCrops2.proxy.registerItemRenderer(itemBlock, 0, name);
	}

	@Override
	public PrimordialBlockContainer setCreativeTab(CreativeTabs tab) {
		super.setCreativeTab(tab);
		return this;
	}


}
