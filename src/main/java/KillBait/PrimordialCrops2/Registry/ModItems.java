package KillBait.PrimordialCrops2.Registry;

import KillBait.PrimordialCrops2.Items.Essence.CoalEssence;
import KillBait.PrimordialCrops2.Items.Essence.CraftEssence;
import KillBait.PrimordialCrops2.Items.ItemBlocks.InfusedDirtItemBlock;
import KillBait.PrimordialCrops2.Items.Misc.Fertilizer;
import KillBait.PrimordialCrops2.Items.Base.PrimordialItemBase;
import KillBait.PrimordialCrops2.Items.Base.PrimordialItemSeed;
import KillBait.PrimordialCrops2.Items.Seeds.CoalSeed;
import KillBait.PrimordialCrops2.Items.Seeds.MinicioSeed;
import KillBait.PrimordialCrops2.Utils.ItemModelProvider;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSeeds;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Created by Jon on 02/10/2016.
 */
public class ModItems {

	public static PrimordialItemBase minicioEssence;
	public static PrimordialItemSeed minicioSeed;
	public static PrimordialItemBase coalEssence;
	public static PrimordialItemSeed coalSeed;
	public static Fertilizer essenceFertilizer;
	public static CraftEssence essenceCrafting;
	public static ItemBlock infusedDirtItem;



	public static void init() {

		infusedDirtItem = register(new InfusedDirtItemBlock(ModBlocks.infusedDirt));

		essenceFertilizer = register(new Fertilizer());
		essenceCrafting = register(new CraftEssence());
		//coalSeed = register(new PrimordialItemSeed(ModCrops.coalCrop, Blocks.FARMLAND, "CoalSeed"));

		minicioSeed = register(new MinicioSeed());
		coalSeed = register(new CoalSeed());

		coalEssence = register(new CoalEssence());
		//minicioEssence = register(new PrimordialItemBase("MinicioEssence"));


	}

	private static <T extends Item> T register(T item) {
		GameRegistry.register(item);
		if (item instanceof ItemModelProvider) {
			((ItemModelProvider) item).registerItemModel(item);
		}
		return item;
	}
}
