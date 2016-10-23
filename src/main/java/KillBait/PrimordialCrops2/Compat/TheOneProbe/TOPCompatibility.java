package KillBait.PrimordialCrops2.Compat.TheOneProbe;

import KillBait.PrimordialCrops2.Utils.LogHelper;
import mcjty.theoneprobe.api.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInterModComms;

import javax.annotation.Nullable;

/**
 * Created by Jon on 04/10/2016.
 */
public class TOPCompatibility {

	private static boolean registered;

	public static void register() {
		if (registered)
			return;
		registered = true;
		FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", "KillBait.PrimordialCrops2.Compat.TheOneProbe.TOPCompatibility$GetTheOneProbe");
	}


	public static class GetTheOneProbe implements com.google.common.base.Function<ITheOneProbe, Void> {

		public static ITheOneProbe probe;

		@Nullable
		@Override
		public Void apply(ITheOneProbe theOneProbe) {
			probe = theOneProbe;
			LogHelper.info("Registering support for The One Probe");
			probe.registerProvider(new IProbeInfoProvider() {
				@Override
				public String getID() {
					return "primordialcrops2:default";
				}

				@Override
				public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
					if (blockState.getBlock() instanceof TOPInfoProvider) {
						TOPInfoProvider provider = (TOPInfoProvider) blockState.getBlock();
						provider.addProbeInfo(mode, probeInfo, player, world, blockState, data);
					}

				}
			});
			return null;
		}
	}

}
