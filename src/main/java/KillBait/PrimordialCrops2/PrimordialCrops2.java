package KillBait.PrimordialCrops2;

import KillBait.PrimordialCrops2.Registry.ModItems;
import KillBait.PrimordialCrops2.Utils.PrimordialShapedRecipe;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;

import static net.minecraftforge.oredict.RecipeSorter.Category.SHAPED;
import static net.minecraftforge.oredict.RecipeSorter.register;

/**
 * Created by Jon on 30/09/2016.
 */

@Mod(modid = Info.MODID, version = Info.VERSION, name = Info.MODNAME)

public class PrimordialCrops2 {

	@Mod.Instance(Info.MODID)
	public static PrimordialCrops2 instance;

	@SidedProxy(clientSide = "KillBait.PrimordialCrops2.Proxy.ClientProxy", serverSide = "KillBait.PrimordialCrops2.Proxy.ServerProxy")

	public static KillBait.PrimordialCrops2.Proxy.CommonProxy proxy;

	public static CreativeTabs PrimordialCrops2 = new CreativeTabs("PrimordialCrops2") {
		@Override
		public Item getTabIconItem() {
			return ModItems.minicioEssence;
		}

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
