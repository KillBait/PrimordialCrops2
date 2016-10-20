package KillBait.PrimordialCrops2.Items;

import KillBait.PrimordialCrops2.PrimordialCrops2;
import KillBait.PrimordialCrops2.Utils.ItemModelProvider;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

/**
 * Created by Jon on 02/10/2016.
 */
public class PrimordialItemBase extends Item implements ItemModelProvider {

	protected String name;

	public PrimordialItemBase(String name) {
		this.name = name;
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
		this.setCreativeTab(PrimordialCrops2.PrimordialCrops2);
	}

	public PrimordialItemBase(String name, boolean hasSubTypes) {
		this.name = name;
		if (hasSubTypes) {
			this.setHasSubtypes(true);
			this.setMaxDamage(0);
		}
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
		this.setCreativeTab(PrimordialCrops2.PrimordialCrops2);
	}

	public PrimordialItemBase(String name, boolean hasDamage, int maxDamage, int stackNum) {
		this.name = name;
		if (hasDamage) {
			this.setHasSubtypes(true);
			this.setMaxDamage(maxDamage - 1);
			this.setNoRepair();
			this.maxStackSize = stackNum;
		}
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
		this.setCreativeTab(PrimordialCrops2.PrimordialCrops2);
	}

	@Override
	public PrimordialItemBase setCreativeTab(CreativeTabs tab) {
		super.setCreativeTab(tab);
		return this;
	}

	@Override
	public void registerItemModel(Item itemBlock) {
		PrimordialCrops2.proxy.registerItemRenderer(itemBlock, 0, name);
	}

	/*@Override
	public ItemStack getContainerItem(ItemStack itemStack) {
		ItemStack copiedStack = itemStack.copy();
		if (PrimordialConfig.infusionStoneDurability) {
			copiedStack.setItemDamage(copiedStack.getItemDamage() + 1);
		}
		copiedStack.stackSize = 1;
		return copiedStack;
	}*/
}
