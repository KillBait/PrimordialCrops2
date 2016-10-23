package KillBait.PrimordialCrops2.Handlers;

import KillBait.PrimordialCrops2.Registry.ModBlocks;
import net.minecraft.block.Block;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by Jon on 10/10/2016.
 */
public class EventHandler {

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

	@SubscribeEvent
	public void BlockPlace(BlockEvent.PlaceEvent event) {
		//LogHelper.info("block place event");
		event.setResult(Event.Result.ALLOW);

	}

	@SubscribeEvent
	public void handleCropRightClick(PlayerInteractEvent event) {
		//LogHelper.info("Crop Right Click event");
		event.setResult(Event.Result.ALLOW);
	}
}
