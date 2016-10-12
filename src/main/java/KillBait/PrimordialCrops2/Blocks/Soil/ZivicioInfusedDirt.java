package KillBait.PrimordialCrops2.Blocks.Soil;

import KillBait.PrimordialCrops2.Blocks.BlockBase.PrimordialBlockBase;
import KillBait.PrimordialCrops2.PrimordialCrops2;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;

import java.util.List;

/**
 * Created by Jon on 12/10/2016.
 */
public class ZivicioInfusedDirt extends PrimordialBlockBase {

	public ZivicioInfusedDirt() {
		super(Material.GROUND, "ZivicioInfusedDirt");
		this.setHardness(0.5f);
		this.setResistance(5f);
		this.setHarvestLevel("shovel", 0);
		this.setSoundType(SoundType.GROUND);
	}

	@Override
	public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable) {
		EnumPlantType plantType = plantable.getPlantType(world, pos.up());
		switch (plantType) {
			case Desert:
				return true;
			case Nether:
				return false;
			case Crop:
				return false;
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
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		list.add("\u00A7aCan Be tilled into farmland, protects a planted crop with:");
		list.add(" ");
		list.add("- Fall Resistance");
		list.add("- Explosion Resistance");
		list.add("- Fertility (No Water Source Needed)");
		list.add("- Double Tick Rate");
	}

	@Override
	public void registerItemModel(Item itemBlock) {
		PrimordialCrops2.proxy.registerItemRenderer(itemBlock, 0, "ZivicioInfusedDirt");
	}
}
