package KillBait.PrimordialCrops2.Handlers;

import KillBait.PrimordialCrops2.Blocks.Machines.Furnace.FurnaceContainer;
import KillBait.PrimordialCrops2.Blocks.Machines.Furnace.FurnaceGUIContainer;
import KillBait.PrimordialCrops2.Blocks.Machines.Furnace.FurnaceTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

/**
 * Created by Jon on 20/10/2016.
 */
public class GuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		//LogHelper.info("server ID = " + ID);
		switch (ID) {
			case 0:
				//reserved for in-game manual ?? is it even called as book is client side?
				break;
			case 1:
				BlockPos pos = new BlockPos(x, y, z);
				TileEntity te = world.getTileEntity(pos);
				if (te instanceof FurnaceTileEntity) {
					return new FurnaceContainer(player.inventory, (FurnaceTileEntity) te);
				}
				break;
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		//LogHelper.info("client ID = " + ID);
		switch (ID) {
			case 0:
				//reserved for in-game manual
				break;
			case 1:
				BlockPos pos = new BlockPos(x, y, z);
				TileEntity te = world.getTileEntity(pos);
				if (te instanceof FurnaceTileEntity) {
					FurnaceTileEntity furnaceTileEntity = (FurnaceTileEntity) te;
					return new FurnaceGUIContainer(player.inventory, furnaceTileEntity);
				}
				break;
		}
		return null;
	}
}
