package KillBait.PrimordialCrops2.Compat.WAILA;

import KillBait.PrimordialCrops2.Blocks.TierCropBlock;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInterModComms;

import java.util.List;

import static KillBait.PrimordialCrops2.Blocks.TierCropBlock.AGE;

/**
 * Created by Jon on 04/10/2016.
 */
public class WailaCompatibility implements IWailaDataProvider {

	public static final WailaCompatibility INSTANCE = new WailaCompatibility();

	private WailaCompatibility() {
	}

	private static boolean registered;
	private static boolean loaded;

	public static void load(IWailaRegistrar registrar) {
		System.out.println("WailaCompatibility.load");
		if (!registered) {
			throw new RuntimeException("Please register this handler using the provided method.");
		}
		if (!loaded) {
			registrar.registerHeadProvider(INSTANCE, TierCropBlock.class);
			registrar.registerBodyProvider(INSTANCE, TierCropBlock.class);
			registrar.registerTailProvider(INSTANCE, TierCropBlock.class);
			loaded = true;
		}
	}

	public static void register() {
		if (registered)
			return;
		registered = true;
		FMLInterModComms.sendMessage("Waila", "register", "KillBait.PrimordialCrops2.Compat.WAILA.WailaCompatibility.load");
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
		return tag;
	}

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		Block block = accessor.getBlock();
		if (block instanceof WailaInfoProvider) {
			if (accessor.getBlock() instanceof TierCropBlock) {
				IBlockState state = accessor.getBlockState();
				float age = ((Integer) state.getValue(AGE));
				if (age < 3)
					currenttip.add(String.format("%s : %.0f %%", "Growth", (age * 33)));
				else
					currenttip.add(String.format("%s : %s", "Growth", "Mature"));
			}
			return ((WailaInfoProvider) block).getWailaBody(itemStack, currenttip, accessor, config);
		}
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return currenttip;
	}

}
