package KillBait.PrimordialCrops2.Blocks.Crops.Standard;

import KillBait.PrimordialCrops2.Blocks.PrimordialCropBlock;
import KillBait.PrimordialCrops2.Registry.ModItems;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by Jon on 08/10/2016.
 */
public class cropCoal extends PrimordialCropBlock {

	public cropCoal() {
		super();
		this.setUnlocalizedName("cropCoal");
		this.setRegistryName("cropCoal");
	}

	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		return new ItemStack(this.getSeed());
	}

	@Override
	protected Item getSeed() {
		return ModItems.coalSeed;
	}

	@Override
	protected Item getCrop() {
		return ModItems.coalEssence;
	}
}
