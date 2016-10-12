package KillBait.PrimordialCrops2.Utils;

import KillBait.PrimordialCrops2.Registry.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static KillBait.PrimordialCrops2.Blocks.BlockBase.TierCropBlock.TIER;

/**
 * Created by Jon on 10/10/2016.
 */
public class PrimordialEventHandler {

	@SubscribeEvent
	public void useHoe(UseHoeEvent event) {
		Block checkblock = event.getWorld().getBlockState(event.getPos()).getBlock();
		if (checkblock == ModBlocks.accioInfusedDirt) {
			event.setResult(Event.Result.ALLOW);
			event.getWorld().setBlockState(event.getPos(), ModBlocks.accioFarmland.getDefaultState());
		}
		if (checkblock == ModBlocks.crucioInfusedDirt) {
			event.setResult(Event.Result.ALLOW);
			event.getWorld().setBlockState(event.getPos(), ModBlocks.crucioFarmland.getDefaultState());
		}
		if (checkblock == ModBlocks.imperioInfusedDirt) {
			event.setResult(Event.Result.ALLOW);
			event.getWorld().setBlockState(event.getPos(), ModBlocks.imperioFarmland.getDefaultState());
		}
		if (checkblock == ModBlocks.zivicioInfusedDirt) {
			event.setResult(Event.Result.ALLOW);
			event.getWorld().setBlockState(event.getPos(), ModBlocks.zivicioFarmland.getDefaultState());
		}
	}
}
