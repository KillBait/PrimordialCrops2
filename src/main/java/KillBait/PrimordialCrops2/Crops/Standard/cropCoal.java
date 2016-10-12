package KillBait.PrimordialCrops2.Crops.Standard;

import KillBait.PrimordialCrops2.Blocks.BlockBase.TierCropBlock;
import KillBait.PrimordialCrops2.Registry.ModCrops;
import KillBait.PrimordialCrops2.Registry.ModItems;
import net.minecraft.item.Item;

/**
 * Created by Jon on 08/10/2016.
 */
public class cropCoal extends TierCropBlock {

	public cropCoal() {
		super();
		this.setUnlocalizedName("cropCoal");
		this.setRegistryName("cropCoal");
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
