package KillBait.PrimordialCrops2;

import KillBait.PrimordialCrops2.Registry.ModItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Jon on 30/09/2016.
 */

@Mod(modid = Info.MODID, version = Info.VERSION, name = Info.MODNAME)
//, dependencies = "after:endercore;after:Waila;after:TheOneProbe;after:JEI@[3.12.3.292,)")

public class PrimordialCrops2 {

	@Mod.Instance(Info.MODID)
	public static PrimordialCrops2 instance;

	@SidedProxy(clientSide = "KillBait.PrimordialCrops2.Proxy.ClientProxy", serverSide = "KillBait.PrimordialCrops2.Proxy.ServerProxy")

	public static KillBait.PrimordialCrops2.Proxy.CommonProxy proxy;

	public static CreativeTabs PrimordialCrops2 = new CreativeTabs("PrimordialCrops2") {
		@Override
		@SideOnly(Side.CLIENT)
		public ItemStack getTabIconItem() { return new ItemStack(ModItems.minicioSeed);	}
	};


	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		//PrimordialConfig.init(e);
		proxy.preInit(e);

	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent e) {
		proxy.init(e);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent e) {
		proxy.postInit(e);

	}

}
