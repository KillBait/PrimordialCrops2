package KillBait.PrimordialCrops2.Items.ItemBlocks;

import KillBait.PrimordialCrops2.PrimordialCrops2;
import KillBait.PrimordialCrops2.Info;
import KillBait.PrimordialCrops2.Utils.ItemModelProvider;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Created by Jon on 02/04/2017.
 */
public class InfusedDirtItemBlock extends ItemBlock implements ItemModelProvider {

	protected String name;

	public InfusedDirtItemBlock(Block block) {
		super(block);
		String blockname = String.valueOf(block.getRegistryName()).substring(Info.MODID.length() +1 );
		this.name = blockname;
		this.setUnlocalizedName(blockname);
		this.setRegistryName(blockname);
		this.setHasSubtypes(true);
		this.setCreativeTab(PrimordialCrops2.PrimordialCrops2);
	}


	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName(stack) + "_" + stack.getMetadata();
	}

	@Override
	public int getMetadata(int damage) {
		return damage;
	}


	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems) {
		subItems.add(new ItemStack(itemIn, 1, 0));
		subItems.add(new ItemStack(itemIn, 1, 1));
		subItems.add(new ItemStack(itemIn, 1, 2));
		subItems.add(new ItemStack(itemIn, 1, 3));
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		list.add("\u00A7aSeeds can be planted directly on it, no hoe needed.");
		list.add(" ");
		/*list.add("Ever since i successfully fused primordial essence with regular");
		list.add("dirt, it has only shown special properties for crops containing the same");
		list.add("primordial energy. In time hopefully i can refine the process so it works");
		list.add("with regular crops.");
		list.add(" ");*/
		list.add("Primordial Crops planted on this gives:");
		switch (stack.getMetadata()) {
			case 0:
				list.add("- 2 Essence per harvest");
				break;
			case 1:
				list.add("- 4 Essence per harvest");
				break;
			case 2:
				list.add("- 8 Essence per harvest");
				break;
			case 3:
				list.add("- 16 Essence per harvest");
				break;
		}
	}

	@Override
	public void registerItemModel(Item itemBlock) {

		PrimordialCrops2.proxy.registerItemRenderer(itemBlock, 0, "itemblocks/itemblock_acciodirt");
		PrimordialCrops2.proxy.registerItemRenderer(itemBlock, 1, "itemblocks/itemblock_cruciodirt");
		PrimordialCrops2.proxy.registerItemRenderer(itemBlock, 2, "itemblocks/itemblock_imperiodirt");
		PrimordialCrops2.proxy.registerItemRenderer(itemBlock, 3, "itemblocks/itemblock_ziviciodirt");
		//PrimordialCrops2.proxy.registerItemRenderer(itemBlock, 4, "ZivicioEssence");

	}
}

