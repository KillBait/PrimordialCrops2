package KillBait.PrimordialCrops2.Proxy;

import KillBait.PrimordialCrops2.Compat.CompatHandler;
import KillBait.PrimordialCrops2.Handlers.EventHandler;
import KillBait.PrimordialCrops2.Handlers.GuiHandler;
import KillBait.PrimordialCrops2.Registry.ModBlocks;
import KillBait.PrimordialCrops2.Registry.ModCrops;
import KillBait.PrimordialCrops2.Registry.ModItems;
import KillBait.PrimordialCrops2.Registry.ModRecipes;
import KillBait.PrimordialCrops2.WorldGen.PrimordialWorldGen;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

import static KillBait.PrimordialCrops2.PrimordialCrops2.instance;

public class CommonProxy {

	public void preInit(FMLPreInitializationEvent e) {

		ModBlocks.init();
		ModCrops.init();
		ModItems.init();

		//OreDictonaryRegistry.regOreDic();

		CompatHandler.registerTOP();
		CompatHandler.registerWaila();

		//RecipeSorter.register("primordialcrops2:seedtier", PrimordialShapedRecipe.class, SHAPED, "after:minecraft:shaped before:forge:shapedore");

		MinecraftForge.EVENT_BUS.register(new EventHandler());


	}

	public void init(FMLInitializationEvent e) {
		GameRegistry.registerWorldGenerator(new PrimordialWorldGen(), 0);
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
		//FMLInterModComms.sendMessage("EnderCore", "addRightClickCrop", "primordialcrops2:CoalSeed|primordialcrops2:cropCoal|15|13");
		ModRecipes.init();
	}

	public void postInit(FMLPostInitializationEvent e) {
	}

	public void registerItemRenderer(Item item, int meta, String id) {
	}
}
