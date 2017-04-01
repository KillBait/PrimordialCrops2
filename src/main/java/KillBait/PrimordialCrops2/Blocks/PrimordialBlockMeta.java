package KillBait.PrimordialCrops2.Blocks;

import KillBait.PrimordialCrops2.PrimordialCrops2;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

/**
 * Created by Jon on 13/02/2017.
 */
public class PrimordialBlockMeta extends PrimordialBlockBase {

	protected String name;

	public PrimordialBlockMeta(Material material, String unlocalizedName) {
		super(material, unlocalizedName);
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
	public PrimordialBlockMeta setCreativeTab(CreativeTabs tab) {
		super.setCreativeTab(tab);
		return this;
	}
}
