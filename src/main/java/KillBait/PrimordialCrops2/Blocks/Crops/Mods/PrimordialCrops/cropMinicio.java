package KillBait.PrimordialCrops2.Blocks.Crops.Mods.PrimordialCrops;

import KillBait.PrimordialCrops2.Blocks.Base.PrimordialCropBlock;
import KillBait.PrimordialCrops2.Registry.ModItems;
import net.minecraft.item.Item;

/**
 * Created by Jon on 08/10/2016.
 */
public class cropMinicio extends PrimordialCropBlock {

	public cropMinicio() {
		super();
		this.setUnlocalizedName("crop_minicio");
		this.setRegistryName("crop_minicio");
	}

	@Override
	protected Item getSeed() {
		return ModItems.minicioSeed;
	}

	@Override
	protected Item getCrop() {
		return ModItems.essenceCrafting;
	}

}
