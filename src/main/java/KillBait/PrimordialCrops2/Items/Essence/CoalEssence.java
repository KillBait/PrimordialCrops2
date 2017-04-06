package KillBait.PrimordialCrops2.Items.Essence;

import KillBait.PrimordialCrops2.Items.Base.PrimordialItemBase;
import KillBait.PrimordialCrops2.PrimordialCrops2;
import KillBait.PrimordialCrops2.Utils.ItemModelProvider;
import net.minecraft.item.Item;

/**
 * Created by Jon on 06/04/2017.
 */
public class CoalEssence extends PrimordialItemBase implements ItemModelProvider {

	public CoalEssence() {
		super("essence_coal");
	}

	@Override
	public void registerItemModel(Item itemBlock) {
		PrimordialCrops2.proxy.registerItemRenderer(itemBlock, 0, "essence/essence_coal");
	}
}
