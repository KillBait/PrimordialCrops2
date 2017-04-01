package KillBait.PrimordialCrops2.Registry;

import KillBait.PrimordialCrops2.Blocks.Farmland.AccioInfusedFarmland;
import KillBait.PrimordialCrops2.Blocks.Farmland.CrucioInfusedFarmland;
import KillBait.PrimordialCrops2.Blocks.Farmland.ImperioInfusedFarmland;
import KillBait.PrimordialCrops2.Blocks.Farmland.ZivicioInfusedFarmland;
import KillBait.PrimordialCrops2.Blocks.Machines.Furnace.PrimordialFurnace;
import KillBait.PrimordialCrops2.Blocks.PrimordialBlockBase;
import KillBait.PrimordialCrops2.Blocks.PrimordialFarmland;
import KillBait.PrimordialCrops2.Blocks.PrimordialOre;
import KillBait.PrimordialCrops2.Blocks.Soil.AccioInfusedDirt;
import KillBait.PrimordialCrops2.Blocks.Soil.CrucioInfusedDirt;
import KillBait.PrimordialCrops2.Blocks.Soil.ImperioInfusedDirt;
import KillBait.PrimordialCrops2.Blocks.Soil.ZivicioInfusedDirt;
import KillBait.PrimordialCrops2.Utils.ItemModelProvider;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Created by Jon on 02/10/2016.
 */
public class ModBlocks {

	public static PrimordialOre oreMinicio;
	public static PrimordialOre oreAccio;
	public static PrimordialOre oreCrucio;

	public static PrimordialBlockBase accioInfusedDirt;
	public static PrimordialBlockBase crucioInfusedDirt;
	public static PrimordialBlockBase imperioInfusedDirt;
	public static PrimordialBlockBase zivicioInfusedDirt;

	public static PrimordialFarmland accioFarmland;
	public static PrimordialFarmland crucioFarmland;
	public static PrimordialFarmland imperioFarmland;
	public static PrimordialFarmland zivicioFarmland;

	public static PrimordialFurnace furnace;

	public static void init() {
		oreMinicio = register(new PrimordialOre("oreMinicio"));
		oreAccio = register(new PrimordialOre("oreAccio"));
		oreCrucio = register(new PrimordialOre("oreCrucio"));

		accioFarmland = register(new AccioInfusedFarmland(), null);
		crucioFarmland = register(new CrucioInfusedFarmland(), null);
		imperioFarmland = register(new ImperioInfusedFarmland(), null);
		zivicioFarmland = register(new ZivicioInfusedFarmland(), null);

		accioInfusedDirt = register(new AccioInfusedDirt());
		crucioInfusedDirt = register(new CrucioInfusedDirt());
		imperioInfusedDirt = register(new ImperioInfusedDirt());
		zivicioInfusedDirt = register(new ZivicioInfusedDirt());

		furnace = register(new PrimordialFurnace());
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
