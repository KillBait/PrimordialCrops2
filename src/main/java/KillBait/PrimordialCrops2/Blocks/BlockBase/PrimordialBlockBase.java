package KillBait.PrimordialCrops2.Blocks.BlockBase;

import KillBait.PrimordialCrops2.PrimordialCrops2;
import KillBait.PrimordialCrops2.Utils.ItemModelProvider;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;

import static net.minecraftforge.common.EnumPlantType.*;

/*
 * Created by Jon on 02/10/2016.
 */

public class PrimordialBlockBase extends Block implements ItemModelProvider {

	protected String name;

	public PrimordialBlockBase(Material material, String unlocalizedName) {
		super(material);
		this.name = unlocalizedName;
		this.setUnlocalizedName(unlocalizedName);
		this.setRegistryName(unlocalizedName);
		this.setCreativeTab(PrimordialCrops2.PrimordialCrops2);
	}

	@Override
	public void registerItemModel(Item itemBlock) {
		PrimordialCrops2.proxy.registerItemRenderer(itemBlock, 0, name);
	}

	@Override
	public PrimordialBlockBase setCreativeTab(CreativeTabs tab) {
		super.setCreativeTab(tab);
		return this;
	}
}
