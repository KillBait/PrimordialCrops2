package KillBait.PrimordialCrops2.Registry;

import KillBait.PrimordialCrops2.Blocks.Crops.Vanilla.cropCoal;
import KillBait.PrimordialCrops2.Blocks.Crops.Mods.PrimordialCrops.cropMinicio;
import KillBait.PrimordialCrops2.Blocks.Base.PrimordialCropBlock;
import KillBait.PrimordialCrops2.Utils.ItemModelProvider;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Created by Jon on 09/10/2016.
 */
public class ModCrops {

	public static PrimordialCropBlock minicioCrop;
	public static PrimordialCropBlock coalCrop;


	public static void init() {
		minicioCrop = register(new cropMinicio(), null);
		coalCrop = register(new cropCoal(), null);

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

	private static <T extends Block> T register(T block) {
		ItemBlock itemBlock = new ItemBlock(block);
		itemBlock.setRegistryName(block.getRegistryName());
		return register(block, itemBlock);
	}
}
