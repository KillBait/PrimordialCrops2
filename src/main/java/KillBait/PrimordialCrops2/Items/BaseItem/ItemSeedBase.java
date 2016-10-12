package KillBait.PrimordialCrops2.Items.BaseItem;

import KillBait.PrimordialCrops2.Blocks.BlockBase.TierCropBlock;
import KillBait.PrimordialCrops2.PrimordialCrops2;
import KillBait.PrimordialCrops2.Utils.ItemModelProvider;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

import static KillBait.PrimordialCrops2.Blocks.BlockBase.TierCropBlock.TIER;

/**
 * Created by Jon on 03/10/2016.
 */
public class ItemSeedBase extends Item implements net.minecraftforge.common.IPlantable, ItemModelProvider {

	private final TierCropBlock crops;
	private final String name;
	/**
	 * BlockID of the block the seeds can be planted on.
	 */
	private final Block soilBlockID;

	public ItemSeedBase(TierCropBlock crops, Block soil, String regName) {
		this.name = regName;
		this.crops = crops;
		this.soilBlockID = soil;
		this.setUnlocalizedName(regName);
		this.setRegistryName(regName);

		/*this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setNoRepair();*/

		this.setCreativeTab(PrimordialCrops2.PrimordialCrops2);
	}

	/**
	 * Called when a Block is right-clicked with this Item
	 */
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		net.minecraft.block.state.IBlockState state = worldIn.getBlockState(pos);
		if (facing == EnumFacing.UP && playerIn.canPlayerEdit(pos.offset(facing), facing, stack) && state.getBlock().canSustainPlant(state, worldIn, pos, EnumFacing.UP, this) && worldIn.isAirBlock(pos.up())) {
			if (!stack.hasTagCompound()) {
				stack.setTagCompound(new NBTTagCompound());
				stack.getTagCompound().setInteger("Tier", 0);
			}
			if (stack.getTagCompound().hasKey("Tier")) {
				worldIn.setBlockState(pos.up(), this.crops.getDefaultState().withProperty(TIER, Integer.valueOf(stack.getTagCompound().getInteger("Tier"))), 2);
			}
			--stack.stackSize;
			return EnumActionResult.SUCCESS;
		} else {
			return EnumActionResult.FAIL;
		}
	}

	@Override
	public net.minecraftforge.common.EnumPlantType getPlantType(net.minecraft.world.IBlockAccess world, BlockPos pos) {
		return this.crops == net.minecraft.init.Blocks.NETHER_WART ? net.minecraftforge.common.EnumPlantType.Nether : net.minecraftforge.common.EnumPlantType.Crop;
	}

	@Override
	public net.minecraft.block.state.IBlockState getPlant(net.minecraft.world.IBlockAccess world, BlockPos pos) {
		return this.crops.getDefaultState();
	}

	@Override
	public void registerItemModel(Item item) {
		//for(int i = 0; i < 4; ++i) {
		PrimordialCrops2.proxy.registerItemRenderer(item, 0, this.name);
		//}
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		if (stack.hasTagCompound()) {
			if (!stack.getTagCompound().hasKey("Tier")) {
				stack.getTagCompound().setInteger("Tier", 0);
			}
		} else {
			stack.setTagCompound(new NBTTagCompound());
			stack.getTagCompound().setInteger("Tier", 0);
		}
		list.add("Tier " + ((stack.getTagCompound().getInteger("Tier") + 1)));
	}
}
