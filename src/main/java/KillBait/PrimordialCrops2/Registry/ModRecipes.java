package KillBait.PrimordialCrops2.Registry;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Created by Jon on 04/10/2016.
 */
public class ModRecipes {


	public static void init() {

		//GameRegistry.addRecipe(new PrimordialShapedRecipe());
		GameRegistry.addRecipe(new ItemStack(ModItems.minicioSeed, 1), "mm ", "mm ", "   ", 'm', Items.WHEAT_SEEDS);
		//GameRegistry.addRecipe(new PrimordialShapedRecipe(1,2,new ItemStack(ModItems.minicioSeed, 1), " m ", "msm", " m ", 's', Items.WHEAT_SEEDS, 'm', new ItemStack(ModItems.minicioSeed, 1)));
	}
}
