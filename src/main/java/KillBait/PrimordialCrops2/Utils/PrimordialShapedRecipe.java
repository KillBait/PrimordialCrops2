package KillBait.PrimordialCrops2.Utils;

import KillBait.PrimordialCrops2.Items.Base.PrimordialItemSeed;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Jon on 06/10/2016.
 */
public class PrimordialShapedRecipe extends ShapedOreRecipe implements IRecipe {

	int matchedtier;

	public PrimordialShapedRecipe(Block result, Object... recipe) {
		super(result, recipe);
	}

	public PrimordialShapedRecipe(Item result, Object... recipe) {
		super(result, recipe);
	}

	public PrimordialShapedRecipe(ItemStack result, Object... recipe) {
		super(result, recipe);
	}


	@Override
	protected boolean checkMatch(InventoryCrafting inv, int startX, int startY, boolean mirror) {

		int lasttier = 0;

		for (int x = 0; x < MAX_CRAFT_GRID_WIDTH; x++) {
			for (int y = 0; y < MAX_CRAFT_GRID_HEIGHT; y++) {
				int subX = x - startX;
				int subY = y - startY;
				Object target = null;

				if (subX >= 0 && subY >= 0 && subX < width && subY < height) {
					if (mirror) {
						target = input[width - subX - 1 + subY * width];
					} else {
						target = input[subX + subY * width];
					}
				}

				ItemStack slot = inv.getStackInRowAndColumn(x, y);

				if (target instanceof ItemStack) {
					if (!OreDictionary.itemMatches((ItemStack) target, slot, false)) {
						return false;
					}
					// check if the value of the Tier NBT matches the previously found NBT Tier, if not then fail
					if (slot.getItem() instanceof PrimordialItemSeed) {
						if (slot.hasTagCompound()) {
							if (slot.getTagCompound().hasKey("Tier")) {
								if (lasttier == 0) {
									lasttier = slot.getTagCompound().getInteger("Tier");
								} else {
									if ((slot.getTagCompound().getInteger("Tier") != lasttier) || lasttier >= 3) {
										return false;
									}
								}
							}
						}
					}


				} else if (target instanceof List) {
					boolean matched = false;

					Iterator<ItemStack> itr = ((List<ItemStack>) target).iterator();
					while (itr.hasNext() && !matched) {
						matched = OreDictionary.itemMatches(itr.next(), slot, false);
					}

					if (!matched) {
						return false;
					}
				} else if (target == null && slot != null) {
					return false;
				}
			}
		}

		this.matchedtier = lasttier;

		return true;
	}

	@Nullable
	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		ItemStack stack = output.copy();
		if (stack.hasTagCompound()) {
			stack.getTagCompound().setInteger("Tier", this.matchedtier + 1);
		} else {
			// This should never happen, but just in case
			stack.setTagCompound(new NBTTagCompound());
			stack.getTagCompound().setInteger("Tier", this.matchedtier + 1);
		}
		return stack;
	}

	/*@Override
	public int getRecipeSize() {
		return 0;
	}*/

/*	@Nullable
	@Override
	public ItemStack getRecipeOutput() {
		ItemStack stack = output.copy();
		if (stack.hasTagCompound()) {
			if (!stack.getTagCompound().hasKey("Tier")) {
				stack.getTagCompound().setInteger("Tier", 2);
			}
		} else {
			stack.setTagCompound(new NBTTagCompound());
			stack.getTagCompound().setInteger("Tier", 2);
		}
		return stack;
	}*/

	/*@Override
	public ItemStack[] getRemainingItems(InventoryCrafting inv) {
		return new ItemStack[0];
	}*/
}
