package KillBait.PrimordialCrops2.Registry;

import KillBait.PrimordialCrops2.Blocks.BlockBase.TierCropBlock;
import KillBait.PrimordialCrops2.Crops.Standard.cropCoal;
import KillBait.PrimordialCrops2.Crops.Standard.cropMinicio;
import KillBait.PrimordialCrops2.Utils.ItemModelProvider;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Created by Jon on 09/10/2016.
 */
public class ModCrops {

	public static TierCropBlock minicioCrop;
	public static TierCropBlock coalCrop;


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
