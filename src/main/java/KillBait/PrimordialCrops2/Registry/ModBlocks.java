package KillBait.PrimordialCrops2.Registry;

import KillBait.PrimordialCrops2.Blocks.BlockBase.PrimordialBlockBase;
import KillBait.PrimordialCrops2.Blocks.BlockBase.PrimordialFarmland;
import KillBait.PrimordialCrops2.Blocks.BlockBase.PrimordialOreBase;
import KillBait.PrimordialCrops2.Blocks.Farmland.InfusedFarmland;
import KillBait.PrimordialCrops2.Blocks.Soil.InfusedDirt;
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
	public static PrimordialBlockBase infusedDirt;
	public static ItemBlock infusedDirtIB;
	public static PrimordialFarmland accioFarmland;

	public static void init() {
		infusedDirt = register(new InfusedDirt());
		accioFarmland = register(new InfusedFarmland());
		oreMinicio = register(new PrimordialOreBase("oreMinicio"));
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
