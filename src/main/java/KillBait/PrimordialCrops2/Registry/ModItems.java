package KillBait.PrimordialCrops2.Registry;

import KillBait.PrimordialCrops2.Items.BaseItem.ItemSeedBase;
import KillBait.PrimordialCrops2.Items.BaseItem.PrimordialItemBase;
import KillBait.PrimordialCrops2.Items.Misc.Fertilizer;
import KillBait.PrimordialCrops2.Utils.ItemModelProvider;
import KillBait.PrimordialCrops2.Utils.LogHelper;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Created by Jon on 02/10/2016.
 */
public class ModItems {

	public static PrimordialItemBase minicioEssence;
	public static ItemSeedBase minicioSeed;
	public static PrimordialItemBase coalEssence;
	public static ItemSeedBase coalSeed;
	public static Fertilizer essenceFertilizer;


	public static void init() {
		minicioSeed = register(new ItemSeedBase(ModCrops.minicioCrop, Blocks.FARMLAND, "MinicioSeed"));
		minicioEssence = register(new PrimordialItemBase("MinicioEssence"));
		coalSeed = register(new ItemSeedBase(ModCrops.coalCrop, Blocks.FARMLAND, "CoalSeed"));
		coalEssence = register(new PrimordialItemBase("CoalEssence"));
		essenceFertilizer = register(new Fertilizer());
	}

	private static <T extends Item> T register(T item) {
		GameRegistry.register(item);
		if (item instanceof ItemModelProvider) {
			LogHelper.info(item);
			((ItemModelProvider) item).registerItemModel(item);
		}
		return item;
	}
}
