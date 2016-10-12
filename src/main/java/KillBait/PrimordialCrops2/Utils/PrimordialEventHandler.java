package KillBait.PrimordialCrops2.Utils;

import KillBait.PrimordialCrops2.Registry.ModBlocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static KillBait.PrimordialCrops2.Blocks.BlockBase.TierCropBlock.TIER;

/**
 * Created by Jon on 10/10/2016.
 */
public class PrimordialEventHandler {

	@SubscribeEvent
	public void useHoe(UseHoeEvent event) {
		if (event.getWorld().getBlockState(event.getPos()).getBlock() == ModBlocks.infusedDirt) {
			LogHelper.info("used Hoe");
			event.getWorld().setBlockState(event.getPos(), ModBlocks.accioFarmland.getDefaultState());
			//event.getWorld().playSound(event.getEntityPlayer(), event.getPos(), SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
		}
	}
}
