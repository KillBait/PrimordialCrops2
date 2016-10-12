package KillBait.PrimordialCrops2.Registry;

import KillBait.PrimordialCrops2.Utils.PrimordialShapedRecipe;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;

import static net.minecraftforge.oredict.RecipeSorter.Category.SHAPED;
import static net.minecraftforge.oredict.RecipeSorter.register;

/**
 * Created by Jon on 04/10/2016.
 */
public class ModRecipes {


	public static void init() {

		ItemStack resultstack = new ItemStack(ModItems.minicioSeed);
		resultstack.setTagCompound(new NBTTagCompound());
		ItemStack inputstack = new ItemStack(ModItems.minicioSeed);
		inputstack.setTagCompound(new NBTTagCompound());

		for (int x = 0; x < 3; x++) {
			inputstack.getTagCompound().setInteger("Tier", x);
			resultstack.getTagCompound().setInteger("Tier", x + 1);
			GameRegistry.addRecipe(new PrimordialShapedRecipe(resultstack, " m ", "msm", " m ", 's', Items.WHEAT_SEEDS, 'm', inputstack));

		}

		//GameRegistry.addRecipe(new PrimordialShapedRecipe());
		GameRegistry.addRecipe(new ItemStack(ModItems.minicioSeed, 1), "mm ", "mm ", "   ", 'm', Items.WHEAT_SEEDS);
		//GameRegistry.addRecipe(new PrimordialShapedRecipe(1,2,new ItemStack(ModItems.minicioSeed, 1), " m ", "msm", " m ", 's', Items.WHEAT_SEEDS, 'm', new ItemStack(ModItems.minicioSeed, 1)));
	}
}
