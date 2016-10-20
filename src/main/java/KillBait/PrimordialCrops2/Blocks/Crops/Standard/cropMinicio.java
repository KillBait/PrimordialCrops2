package KillBait.PrimordialCrops2.Blocks.Crops.Standard;

import KillBait.PrimordialCrops2.Blocks.PrimordialCropBlock;
import KillBait.PrimordialCrops2.Registry.ModItems;
import net.minecraft.item.Item;

/**
 * Created by Jon on 08/10/2016.
 */
public class cropMinicio extends PrimordialCropBlock {

	public cropMinicio() {
		super();
		this.setUnlocalizedName("cropMinicio");
		this.setRegistryName("cropMinicio");
	}

	@Override
	protected Item getSeed() {
		return ModItems.minicioSeed;
	}

	@Override
	protected Item getCrop() {
		return ModItems.minicioEssence;
	}

}
