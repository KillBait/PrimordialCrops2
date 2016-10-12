package KillBait.PrimordialCrops2.Registry;

import KillBait.PrimordialCrops2.Blocks.BlockBase.PrimordialBlockBase;
import KillBait.PrimordialCrops2.Blocks.BlockBase.PrimordialFarmland;
import KillBait.PrimordialCrops2.Blocks.BlockBase.PrimordialOreBase;
import KillBait.PrimordialCrops2.Blocks.Farmland.AccioInfusedFarmland;
import KillBait.PrimordialCrops2.Blocks.Farmland.CrucioInfusedFarmland;
import KillBait.PrimordialCrops2.Blocks.Farmland.ImperioInfusedFarmland;
import KillBait.PrimordialCrops2.Blocks.Farmland.ZivicioInfusedFarmland;
import KillBait.PrimordialCrops2.Blocks.Soil.AccioInfusedDirt;
import KillBait.PrimordialCrops2.Blocks.Soil.CrucioInfusedDirt;
import KillBait.PrimordialCrops2.Blocks.Soil.ImperioInfusedDirt;
import KillBait.PrimordialCrops2.Blocks.Soil.ZivicioInfusedDirt;
import KillBait.PrimordialCrops2.Utils.ItemModelProvider;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;

import static net.minecraftforge.fml.common.registry.GameRegistry.register;

/**
 * Created by Jon on 02/10/2016.
 */
public class ModBlocks {

	public static PrimordialOreBase oreMinicio;
	public static PrimordialBlockBase accioInfusedDirt;
	public static PrimordialFarmland accioFarmland;
	public static PrimordialBlockBase crucioInfusedDirt;
	public static PrimordialFarmland crucioFarmland;
	public static PrimordialBlockBase imperioInfusedDirt;
	public static PrimordialFarmland imperioFarmland;
	public static PrimordialBlockBase zivicioInfusedDirt;
	public static PrimordialFarmland zivicioFarmland;

	public static void init() {
		oreMinicio = register(new PrimordialOreBase("oreMinicio"));
		accioInfusedDirt = register(new AccioInfusedDirt());
		accioFarmland = register(new AccioInfusedFarmland(), null);
		crucioInfusedDirt = register(new CrucioInfusedDirt());
		crucioFarmland = register(new CrucioInfusedFarmland(), null);
		imperioInfusedDirt = register(new ImperioInfusedDirt());
		imperioFarmland = register(new ImperioInfusedFarmland(), null);
		zivicioInfusedDirt = register(new ZivicioInfusedDirt());
		zivicioFarmland = register(new ZivicioInfusedFarmland(), null);

	}

	private static <T extends Block> T register(T block) {
		ItemBlock itemBlock = new ItemBlock(block);
		itemBlock.setRegistryName(block.getRegistryName());
		return register(block, itemBlock);
	}

	private static <T extends Block> T register(T block, ItemBlock itemBlock) {
		GameRegistry.register(block);
		if (itemBlock != null) {
			GameRegistry.register(itemBlock);
		}

		if (block instanceof ItemModelProvider) {
			((ItemModelProvider) block).registerItemModel(itemBlock);
		}

		return block;
	}


}
