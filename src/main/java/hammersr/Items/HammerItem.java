package hammersr.Items;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.EntityLiving;
import net.minecraft.core.entity.player.EntityPlayer;
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

public class HammerItem extends ItemTool {
	public HammerItem(String name, int id, ToolMaterial enumtoolmaterial) {
		super(name, id, 4, enumtoolmaterial, BlockTags.MINEABLE_BY_PICKAXE);
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
	@Override
	public boolean canHarvestBlock(EntityLiving entityLiving, ItemStack itemStack, Block block) {
		Integer mininglevel = ItemToolPickaxe.miningLevels.get(block);
		if (mininglevel != null) {
			return this.material.getMiningLevel() >= mininglevel;
		} else {
			return block.hasTag(BlockTags.MINEABLE_BY_PICKAXE);
		}
	}

	protected void MineBlock(int x, int y, int z, World world, EntityLiving player) {
	    if (world.isClientSide) return;
		Item GoldItem = HAEItems.hammerGold;
		Item heldItem = player.getHeldItem().getItem();
		if (!world.isClientSide) {
			if (world.getBlock(x, y, z) != null && !isBlockMatchToBlacklist(world.getBlock(x, y, z).id, new BlockLists().BlockHammersBlacklist))
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
	}

	private static boolean isBlockMatchToBlacklist(int BlockId, List<Integer> list) {
		boolean BlockMatchToBlacklist;
		BlockMatchToBlacklist = list.contains(BlockId);
		return BlockMatchToBlacklist;
	}

	@Override
	public boolean onBlockDestroyed(World world, ItemStack itemstack, int i, int j, int k, int l, Side side, EntityLiving entityliving) {
		super.onBlockDestroyed(world, itemstack, i, j, k, l, side, entityliving);
		if (Block.blocksList[i] == null || !Block.blocksList[i].hasTag(BlockTags.MINEABLE_BY_PICKAXE)) return true;
		int x, y, z;
		int Squ = 0;
		int Cu = 0 ;
		Item heldItem = entityliving.getHeldItem().getItem();
		if(heldItem == HAEItems.hammerWooden ||heldItem == HAEItems.hammerStone||heldItem == HAEItems.hammerGold){
			Squ = 1;
		} else if (heldItem == HAEItems.hammerIron) {
			Squ = 1;
			Cu = 2;
		}else if(heldItem == HAEItems.hammerSteel){
			Squ = 2;
			Cu = 3;
		} else if (heldItem == HAEItems.hammerDiamond) {
			Squ = 2;
			Cu = 5;
		}
		switch(Direction.getDirection(entityliving)){
			case NORTH :
				for (x = -Squ; x <= Squ; x++) {
					for (y = -Squ; y <= Squ; y++) {
						for (z = 0; z <= Cu; z++) {
							this.MineBlock(j + x, k + y, l - z , entityliving.world,entityliving);
					}
				}
			}
				break;
			case SOUTH:
				for (x = -Squ; x <= Squ; x++) {
					for (y = -Squ; y <= Squ; y++) {
						for (z = 0; z <= Cu; z++) {
							this.MineBlock(j + x, k + y, l+ z , entityliving.world,entityliving);
						}
					}
				}
				break;
			case WEST:
				for (x = -Squ; x <= Squ; x++) {
					for (y = -Squ; y <= Squ; y++) {
						for (z = 0; z <= Cu; z++) {
							this.MineBlock(j - z, k + y, l + x, entityliving.world,entityliving);
						}
					}
				}
				break;
			case EAST:
				for (x = -Squ; x <= Squ; x++) {
					for (y = -Squ; y <= Squ; y++) {
						for (z = 0; z <= Cu; z++) {
							this.MineBlock(j + z, k + y, l + x, entityliving.world,entityliving);
						}
					}
				}
				break;
			case UP:
			for (x = -Squ; x <= Squ; x++) {
				for (y = -Squ; y <= Squ; y++) {
					for (z = 0; z <= Cu; z++) {
						this.MineBlock(j + x , k +z, l + y, entityliving.world,entityliving);
					}
				}
			}
				break;
			case DOWN:
			for (x = -Squ; x <= Squ; x++) {
				for (y = -Squ; y <= Squ; y++) {
					for (z = 0; z <= Cu; z++) {
						this.MineBlock(j + x, k -z, l + y, entityliving.world,entityliving);
					}
				}
			}
				break;
		}
		return true;
	}
}



