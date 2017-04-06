package KillBait.PrimordialCrops2.Items.Essence;

import KillBait.PrimordialCrops2.Items.Base.PrimordialItemBase;
import KillBait.PrimordialCrops2.PrimordialCrops2;
import KillBait.PrimordialCrops2.Utils.ItemModelProvider;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Created by Jon on 23/10/2016.
 */
public class CraftEssence extends PrimordialItemBase implements ItemModelProvider {

	public CraftEssence() {
		super("CraftEssence", true);
	}


	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName(stack) + "_" + stack.getMetadata();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems) {
		subItems.add(new ItemStack(itemIn, 1, 0));
		subItems.add(new ItemStack(itemIn, 1, 1));
		subItems.add(new ItemStack(itemIn, 1, 2));
		subItems.add(new ItemStack(itemIn, 1, 3));
		subItems.add(new ItemStack(itemIn, 1, 4));
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

		PrimordialCrops2.proxy.registerItemRenderer(itemBlock, 0, "essence/essence_minicio");
		PrimordialCrops2.proxy.registerItemRenderer(itemBlock, 1, "essence/essence_accio");
		PrimordialCrops2.proxy.registerItemRenderer(itemBlock, 2, "essence/essence_crucio");
		PrimordialCrops2.proxy.registerItemRenderer(itemBlock, 3, "essence/essence_imperio");
		PrimordialCrops2.proxy.registerItemRenderer(itemBlock, 4, "essence/essence_zivicio");

	}

}
