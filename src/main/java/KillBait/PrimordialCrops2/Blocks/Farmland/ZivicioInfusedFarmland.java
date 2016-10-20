package KillBait.PrimordialCrops2.Blocks.Farmland;

import KillBait.PrimordialCrops2.Blocks.PrimordialFarmland;
import KillBait.PrimordialCrops2.Registry.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by Jon on 12/10/2016.
 */
public class ZivicioInfusedFarmland extends PrimordialFarmland {

	public ZivicioInfusedFarmland() {
		super("ZivicioInfusedFarmland");
		this.setResistance(30f);
		this.setDefaultState(this.blockState.getBaseState().withProperty(MOISTURE, Integer.valueOf(7)));
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		/*int i = ((Integer) state.getValue(MOISTURE)).intValue();

		if (!this.hasWater(worldIn, pos) && !worldIn.isRainingAt(pos.up())) {
			if (i > 0) {
				worldIn.setBlockState(pos, state.withProperty(MOISTURE, Integer.valueOf(i - 1)), 2);
			} else if (!this.hasCrops(worldIn, pos)) {
				worldIn.setBlockState(pos, ModBlocks.imperioInfusedDirt.getDefaultState());
			}
		} else if (i < 7) {
			worldIn.setBlockState(pos, state.withProperty(MOISTURE, Integer.valueOf(7)), 2);
		}*/
	}

	@Override
	public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
	}

	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		return new ItemStack(ModBlocks.zivicioInfusedDirt, 1, 0);
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(ModBlocks.zivicioInfusedDirt);

	}
}
