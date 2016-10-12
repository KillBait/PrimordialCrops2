package KillBait.PrimordialCrops2.Blocks.Farmland;

import KillBait.PrimordialCrops2.Blocks.BlockBase.PrimordialFarmland;
import KillBait.PrimordialCrops2.Registry.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * Created by Jon on 12/10/2016.
 */
public class ImperioInfusedFarmland extends PrimordialFarmland {
	public ImperioInfusedFarmland() {
		super("ImperioInfusedFarmland");
		this.setHarvestLevel("shovel", 0);
	}

	@Override
	public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable) {
		EnumPlantType plantType = plantable.getPlantType(world, pos.up());
		switch (plantType) {
			case Desert:
				return true;
			case Nether:
				return true;
			case Crop:
				return true;
			case Cave:
				return true;
			case Plains:
				return true;
			case Water:
				return false;
			case Beach:
				return true;
		}

		return false;
	}

	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		return new ItemStack(ModBlocks.imperioInfusedDirt, 1, 0);
	}

	@Nullable
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(ModBlocks.imperioInfusedDirt);

	}
}
