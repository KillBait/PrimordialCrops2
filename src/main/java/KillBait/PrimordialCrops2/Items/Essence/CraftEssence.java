package KillBait.PrimordialCrops2.Items.Essence;

import KillBait.PrimordialCrops2.Items.PrimordialItemBase;
import KillBait.PrimordialCrops2.PrimordialCrops2;
import KillBait.PrimordialCrops2.Utils.ItemModelProvider;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
		subItems.add(new ItemStack(itemIn, 1, 0));
		subItems.add(new ItemStack(itemIn, 1, 1));
		subItems.add(new ItemStack(itemIn, 1, 2));
		subItems.add(new ItemStack(itemIn, 1, 3));
		subItems.add(new ItemStack(itemIn, 1, 4));
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		list.add("\u00A7aUsed in crafting, ");
		list.add(" ");
		list.add("\u00A7aAs a catalyst in primordial furnace gives ");
		switch (stack.getMetadata()) {
			case 0:
				list.add("100% speed increase, 50% chance of second output");
				break;
			case 1:
				list.add("200% speed increase, 100% chance of second output");
				break;
			case 2:
				list.add("300% speed increase, 150% chance of second output");
				break;
			case 3:
				list.add("400% speed increase, 200% chance of second output");
				break;
			case 4:
				list.add("500% speed increase, 300% chance of second output");
				break;
		}
	}

	@Override
	public void registerItemModel(Item itemBlock) {

		PrimordialCrops2.proxy.registerItemRenderer(itemBlock, 0, "MinicioEssence");
		PrimordialCrops2.proxy.registerItemRenderer(itemBlock, 1, "AccioEssence");
		PrimordialCrops2.proxy.registerItemRenderer(itemBlock, 2, "CrucioEssence");
		PrimordialCrops2.proxy.registerItemRenderer(itemBlock, 3, "ImperioEssence");
		PrimordialCrops2.proxy.registerItemRenderer(itemBlock, 4, "ZivicioEssence");

	}

}
