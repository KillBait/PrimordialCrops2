package KillBait.PrimordialCrops2.Items.Seeds;

import KillBait.PrimordialCrops2.Items.Base.PrimordialItemSeed;
import KillBait.PrimordialCrops2.PrimordialCrops2;
import KillBait.PrimordialCrops2.Registry.ModCrops;
import KillBait.PrimordialCrops2.Utils.ItemModelProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Created by Jon on 05/04/2017.
 */
public class MinicioSeed extends PrimordialItemSeed implements ItemModelProvider {

	public MinicioSeed() {
		super(ModCrops.minicioCrop, Blocks.FARMLAND, "seed_minicio");
	}

	@Override
	public void registerItemModel(Item item) {
		PrimordialCrops2.proxy.registerItemRenderer(item, 0, "seeds/seed_minicio");
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		list.add("Grow on Infused Farmland for increased growth/output");
	}
}
