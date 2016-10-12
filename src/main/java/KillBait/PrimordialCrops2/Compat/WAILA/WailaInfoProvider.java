package KillBait.PrimordialCrops2.Compat.WAILA;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Created by Jon on 04/10/2016.
 */
public interface WailaInfoProvider {
	List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config);
}
