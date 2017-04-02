package KillBait.PrimordialCrops2.Items.ItemBlocks;

import KillBait.PrimordialCrops2.Blocks.Soil.InfusedDirt;
import KillBait.PrimordialCrops2.Items.PrimordialItemBase;
import KillBait.PrimordialCrops2.PrimordialCrops2;
import KillBait.PrimordialCrops2.Utils.ItemModelProvider;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Created by Jon on 02/04/2017.
 */
public class ItemBlock_InfusedDirt extends ItemBlock implements ItemModelProvider {

	protected String name;

	public ItemBlock_InfusedDirt(Block block) {
		super(block);
		this.name = "infuseddirt";
		this.setUnlocalizedName("infuseddirt");
		this.setRegistryName("infuseddirt");
		this.setHasSubtypes(true);
		this.setCreativeTab(PrimordialCrops2.PrimordialCrops2);
	}


	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName(stack) + "_" + stack.getMetadata();
	}

	@Override
	public int getMetadata(int damage) {
		return damage;
	}


	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems) {
		subItems.add(new ItemStack(itemIn, 1, 0));
		subItems.add(new ItemStack(itemIn, 1, 1));
		subItems.add(new ItemStack(itemIn, 1, 2));
		subItems.add(new ItemStack(itemIn, 1, 3));
		//subItems.add(new ItemStack(itemIn, 1, 4));
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		list.add("\u00A7aUsed in crafting.");
		list.add(" ");
		list.add("As a catalyst in primordial furnace gives.");
		switch (stack.getMetadata()) {
			case 0:
				list.add("2x speed increase");
				break;
			case 1:
				list.add("4x speed increase");
				break;
			case 2:
				list.add("8x speed increase");
				break;
			case 3:
				list.add("20x speed increase");
				break;
			case 4:
				list.add("200x speed increase");
				break;
		}
	}

	@Override
	public void registerItemModel(Item itemBlock) {

		PrimordialCrops2.proxy.registerItemRenderer(itemBlock, 0, "AccioInfusedDirt");
		PrimordialCrops2.proxy.registerItemRenderer(itemBlock, 1, "CrucioInfusedDirt");
		PrimordialCrops2.proxy.registerItemRenderer(itemBlock, 2, "ImperioInfusedDirt");
		PrimordialCrops2.proxy.registerItemRenderer(itemBlock, 3, "ZivicioInfusedDirt");
		//PrimordialCrops2.proxy.registerItemRenderer(itemBlock, 4, "ZivicioEssence");

	}
}

