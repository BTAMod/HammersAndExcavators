package hammersr.Items;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.entity.EntityLiving;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.enums.EnumBlockSoundEffectType;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.material.ToolMaterial;

import net.minecraft.core.item.tool.ItemTool;
import net.minecraft.core.item.tool.ItemToolPickaxe;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ExcavatorItem extends ItemTool {

	public static Map<Block, Integer> miningLevels;
	public ExcavatorItem(String name, int id, ToolMaterial toolMaterial) {
		super(name,id,2,toolMaterial,BlockTags.MINEABLE_BY_SHOVEL);
	}
	static {
		miningLevels = ItemToolPickaxe.miningLevels;
	}
	public void giveOrDrop(EntityPlayer player, ItemStack stack) {
        if (stack == null || stack.stackSize < 1) return;
        player.inventory.insertItem(stack, false);
	    // Inventory full, drop the item at their feet
        if (stack.stackSize > 0) {
            EntityItem itementity = new EntityItem(player.world, player.x, player.y, player.z, stack);
            player.world.entityJoinedWorld(itementity);
            if (!player.world.isClientSide) {
                itementity.clumpToNearbyStack();
            }
        }
	}

	public boolean canHarvestBlock(Block block) {
		Integer mininglevel = miningLevels.get(block);
		if (mininglevel != null) {
			return this.material.getMiningLevel() >= mininglevel;
		} else {
			return block.hasTag(BlockTags.MINEABLE_BY_SHOVEL);
		}
	}
	public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced) {
		int x, y;
		Item heldItem = entityplayer.getHeldItem().getItem();
		Block block = Block.pathDirt;
		int Squ;
		if(heldItem == HAEItems.excavatorSteel ||heldItem == HAEItems.excavatorDiamond) {
			Squ = 2;
		}else{
			Squ = 1;
		}
		for (x = -Squ; x <= Squ; x++) {
			for (y = -Squ; y <= Squ; y++){
				int i1 = world.getBlockId(blockX +x, blockY, blockZ + y);
				int j1 = world.getBlockId(blockX, blockY + 1, blockZ);
				if (side != Side.BOTTOM && j1 == 0 && (i1 == Block.grass.id || i1 == Block.dirt.id || i1 == Block.grassRetro.id || i1 == Block.farmlandDirt.id)) {
					world.playBlockSoundEffect(entityplayer, (float) blockX + 0.5F, (float) blockY + 0.5F, (float) blockZ + 0.5F, Block.blocksList[i1], EnumBlockSoundEffectType.PLACE);
					world.setBlockWithNotify(blockX + x, blockY, blockZ+y, block.id);

				}
			}
		}
		return true;
	}

	protected void MineBlock(int x, int y, int z, World world, EntityLiving player) {
		Item GoldItem = HAEItems.excavatorGold;
		Item heldItem = player.getHeldItem().getItem();
		if (world.getBlock(x, y, z) != null && isBlockMatchTolist(world.getBlock(x, y, z).id, new BlockLists().BlockExcavatorsWhiteList))
			if (heldItem != GoldItem) {
				ItemStack[] itemToDrop = world.getBlock(x, y, z).getBreakResult(world, EnumDropCause.PROPER_TOOL, x, y, z, world.getBlockMetadata(x, y, z), world.getBlockTileEntity(x, y, z));
				world.setBlockWithNotify(x, y, z, 0);
				if (itemToDrop != null) {
					Arrays.stream(itemToDrop).filter(Objects::nonNull).forEach(expDrop -> giveOrDrop((EntityPlayer) player, expDrop));
				}
			} else {
				ItemStack[] itemToDrop = world.getBlock(x, y, z).getBreakResult(world, EnumDropCause.SILK_TOUCH, x, y, z, world.getBlockMetadata(x, y, z), world.getBlockTileEntity(x, y, z));
				world.setBlockWithNotify(x, y, z, 0);
				if (itemToDrop != null) {
					Arrays.stream(itemToDrop).filter(Objects::nonNull).forEach(expDrop -> giveOrDrop((EntityPlayer) player, expDrop));
				}
			}
	}

	private static boolean isBlockMatchTolist(int BlockId, List<Integer> list) {
		boolean BlockMatchToBlacklist;
		BlockMatchToBlacklist = list.contains(BlockId);
		return BlockMatchToBlacklist;
	}
	public boolean onBlockDestroyed(World world, ItemStack itemstack, int i, int j, int k, int l, EntityLiving entityliving) {
		super.onBlockDestroyed(world, itemstack, i, j, k, l, entityliving);
		Item heldItem = entityliving.getHeldItem().getItem();
		int Off1, Off2;
		int Squ;
		if(heldItem == HAEItems.excavatorSteel ||heldItem == HAEItems.excavatorDiamond) {
			Squ = 2;
		}else{
			Squ = 1;
		}
		switch(Direction.getDirection(entityliving)){
			case NORTH :
				for (Off1 = -Squ; Off1 <= Squ; Off1++) {
					for (Off2 = -Squ; Off2 <= Squ; Off2++) {
							this.MineBlock(j + Off1, k + Off2, l, entityliving.world,entityliving);
					}
				}
				break;
			case SOUTH:
				for (Off1 = -Squ; Off1 <= Squ; Off1++) {
					for (Off2 = -Squ; Off2 <= Squ; Off2++) {
						this.MineBlock(j + Off1, k + Off2, l , entityliving.world,entityliving);
					}
				}
				break;
			case WEST:
				for (Off1 = -Squ; Off1 <= Squ; Off1++) {
					for (Off2 = -Squ; Off2 <= Squ; Off2++) {
						this.MineBlock(j  , k + Off2, l + Off1, entityliving.world,entityliving);
					}
				}
				break;
			case EAST:
				for (Off1 = -Squ; Off1 <= Squ; Off1++) {
					for (Off2 = -Squ; Off2 <= Squ; Off2++) {
						this.MineBlock(j, k + Off2, l + Off1, entityliving.world,entityliving);
					}
				}
				break;
			case UP:
				for (Off1 = -Squ; Off1 <= Squ; Off1++) {
					for (Off2 = -Squ; Off2 <= Squ; Off2++) {
						this.MineBlock(j + Off1, k  , l+ Off2, entityliving.world,entityliving);
					}
				}
				break;
			case DOWN:
				for (Off1 = -Squ; Off1 <= Squ; Off1++) {
					for (Off2 = -Squ; Off2 <= Squ; Off2++) {
						this.MineBlock(j + Off1, k , l+ Off2, entityliving.world,entityliving);
					}
				}
				break;
		}
		return true;
	}
}
