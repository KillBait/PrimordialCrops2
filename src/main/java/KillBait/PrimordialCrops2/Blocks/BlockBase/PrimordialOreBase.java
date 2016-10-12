package KillBait.PrimordialCrops2.Blocks.BlockBase;

import KillBait.PrimordialCrops2.PrimordialCrops2;
import net.minecraft.block.material.Material;

/**
 * Created by Jon on 02/10/2016.
 */
public class PrimordialOreBase extends PrimordialBlockBase {

	public PrimordialOreBase(String unlocalizedName) {
		super(Material.ROCK, unlocalizedName);
		this.setHardness(3f);
		this.setResistance(5f);
		this.setHarvestLevel("pickaxe", 0);
		this.setCreativeTab(PrimordialCrops2.PrimordialCrops2);
	}
}
