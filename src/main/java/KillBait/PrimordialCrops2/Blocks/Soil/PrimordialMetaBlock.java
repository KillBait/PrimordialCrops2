package KillBait.PrimordialCrops2.Blocks.Soil;

import KillBait.PrimordialCrops2.PrimordialCrops2;
import KillBait.PrimordialCrops2.Utils.ItemModelProvider;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

/**
 * Created by Jon on 19/01/2017.
 */
public class PrimordialMetaBlock extends Block implements ItemModelProvider {

	protected String name;

	public PrimordialMetaBlock(Material material, String unlocalizedName) {
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
	public PrimordialMetaBlock setCreativeTab(CreativeTabs tab) {
		super.setCreativeTab(tab);
		return this;
	}
}