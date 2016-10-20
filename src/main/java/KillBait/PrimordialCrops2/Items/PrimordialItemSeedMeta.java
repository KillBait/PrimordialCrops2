package KillBait.PrimordialCrops2.Items;

import KillBait.PrimordialCrops2.Blocks.PrimordialCropBlock;
import KillBait.PrimordialCrops2.PrimordialCrops2;
import KillBait.PrimordialCrops2.Utils.ItemModelProvider;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Created by Jon on 14/10/2016.
 */
public class PrimordialItemSeedMeta extends Item implements net.minecraftforge.common.IPlantable, ItemModelProvider {

	private final PrimordialCropBlock crops;
	private final String name;
	/**
	 * BlockID of the block the seeds can be planted on.
	 */
	private final Block soilBlockID;

	public PrimordialItemSeedMeta(PrimordialCropBlock crops, Block soil, String regName) {
		this.name = regName;
		this.crops = crops;
		this.soilBlockID = soil;
		this.setUnlocalizedName(regName);
		this.setRegistryName(regName);

		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setNoRepair();

		this.setCreativeTab(PrimordialCrops2.PrimordialCrops2);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName(stack) + "_" + stack.getMetadata();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
		subItems.add(new ItemStack(itemIn, 1, 0));
		subItems.add(new ItemStack(itemIn, 1, 1));
		subItems.add(new ItemStack(itemIn, 1, 2));
		subItems.add(new ItemStack(itemIn, 1, 3));
	}

	/**
	 * Called when a Block is right-clicked with this Item
	 */
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		net.minecraft.block.state.IBlockState state = worldIn.getBlockState(pos);
		if (facing == EnumFacing.UP && playerIn.canPlayerEdit(pos.offset(facing), facing, stack) && state.getBlock().canSustainPlant(state, worldIn, pos, EnumFacing.UP, this) && worldIn.isAirBlock(pos.up())) {
			/*if (!stack.hasTagCompound()) {
				stack.setTagCompound(new NBTTagCompound());
				stack.getTagCompound().setInteger("Tier", 0);
			}
			if (stack.getTagCompound().hasKey("Tier")) {*/
			/*LogHelper.info("meta =" + getMetadata(stack));
				worldIn.setBlockState(pos.up(), this.crops.getDefaultState().withProperty(TIER, Integer.valueOf(this.getMetadata(stack))), 2);*/
			worldIn.setBlockState(pos.up(), this.crops.getDefaultState());
			/*}*/
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
	public void registerItemModel(Item itemBlock) {

		PrimordialCrops2.proxy.registerItemRenderer(itemBlock, 0, this.name);
		PrimordialCrops2.proxy.registerItemRenderer(itemBlock, 1, this.name);
		PrimordialCrops2.proxy.registerItemRenderer(itemBlock, 2, this.name);
		PrimordialCrops2.proxy.registerItemRenderer(itemBlock, 3, this.name);

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
