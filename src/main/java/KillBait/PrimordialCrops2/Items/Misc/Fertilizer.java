package KillBait.PrimordialCrops2.Items.Misc;

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
 * Created by Jon on 09/10/2016.
 */
public class Fertilizer extends PrimordialItemBase implements ItemModelProvider {


	public Fertilizer() {
		super("Fertilizer", true);
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
		//subItems.add(new ItemStack(itemIn, 1, 3));
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		list.add("\u00A7aUse on farmland, protects a planted crop with:");
		list.add(" ");
		switch (stack.getMetadata()) {
			case 0:
				list.add("- Fall Resistance");
				break;
			case 1:
				list.add("- Fall Resistance");
				list.add("- Explosion Resistance");
				break;
			case 2:
				list.add("- Fall Resistance");
				list.add("- Explosion Resistance");
				list.add("- Fertility (No Water Source Needed)");
				break;
			case 3:
				list.add("- Fall Resistance");
				list.add("- Explosion Resistance");
				list.add("- Fertility (No Water Source Needed)");
				list.add("- Double Tick Rate");


		}
	}

	@Override
	public void registerItemModel(Item itemBlock) {

		PrimordialCrops2.proxy.registerItemRenderer(itemBlock, 0, "Fertilizer");
		PrimordialCrops2.proxy.registerItemRenderer(itemBlock, 1, "CrucioFertilizer");
		PrimordialCrops2.proxy.registerItemRenderer(itemBlock, 2, "ImperioFertilizer");
		//PrimordialCrops2.proxy.registerItemRenderer(itemBlock, 3, "ZivicioFertilizer");

	}
}
