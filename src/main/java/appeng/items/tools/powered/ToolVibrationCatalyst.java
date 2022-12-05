package appeng.items.tools.powered;

import java.util.ArrayList;
import java.util.List;

import appeng.block.misc.BlockTinyTNT;
import appeng.util.InWorldToolOperationResult;
import appeng.util.Platform;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTNT;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class ToolVibrationCatalyst extends ToolEntropyManipulator {

    @Override
    public boolean onItemUse(final ItemStack item, final EntityPlayer p, final World w, int x, int y, int z,
            final int side, final float hitX, final float hitY, final float hitZ) {
        if (this.getAECurrentPower(item) > 1600) {
            if (!p.canPlayerEdit(x, y, z, side, item)) {
                return false;
            }

            final Block blockID = w.getBlock(x, y, z);
            final int metadata = w.getBlockMetadata(x, y, z);

            if (blockID == null || ForgeEventFactory.onPlayerInteract(p,
                    blockID.isAir(w, x, y, z) ? PlayerInteractEvent.Action.RIGHT_CLICK_AIR
                            : PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK,
                    x, y, z, side, w).isCanceled())
                return false;

            if (blockID instanceof BlockTNT) {
                if (!breakBlockWithCheck(w, p, x, y, z))
                    return false;
                ((BlockTNT) blockID).func_150114_a(w, x, y, z, 1, p);
                return true;
            }

            if (blockID instanceof BlockTinyTNT) {
                if (!breakBlockWithCheck(w, p, x, y, z))
                    return false;
                ((BlockTinyTNT) blockID).startFuse(w, x, y, z, p);
                return true;
            }

            if (this.canHeat(blockID, metadata)) {
                if (this.heat(blockID, p, metadata, w, x, y, z)) {
                    this.extractAEPower(item, 1600);
                    return true;
                }
                return false;
            }

            final ItemStack[] stack = Platform.getBlockDrops(w, x, y, z);
            final List<ItemStack> out = new ArrayList<ItemStack>();
            boolean hasFurnaceable = false;
            boolean canFurnaceable = true;

            for (final ItemStack i : stack) {
                final ItemStack result = FurnaceRecipes.smelting().getSmeltingResult(i);

                if (result != null) {
                    if (result.getItem() instanceof ItemBlock) {
                        if (Block.getBlockFromItem(result.getItem()) == blockID
                                && result.getItem().getDamage(result) == metadata) {
                            canFurnaceable = false;
                        }
                    }
                    hasFurnaceable = true;
                    out.add(result);
                } else {
                    canFurnaceable = false;
                    out.add(i);
                }
            }

            if (hasFurnaceable && canFurnaceable) {
                if (!breakBlockWithCheck(w, p, x, y, z))
                    return false;

                this.extractAEPower(item, 1600);
                final InWorldToolOperationResult or = InWorldToolOperationResult
                        .getBlockOperationResult(out.toArray(new ItemStack[out.size()]));
                w.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "fire.ignite", 1.0F,
                        itemRand.nextFloat() * 0.4F + 0.8F);

                if (or.getBlockItem() != null) {
                    w.setBlock(x, y, z, Block.getBlockFromItem(or.getBlockItem().getItem()),
                            or.getBlockItem().getItemDamage(), 3);
                }

                if (or.getDrops() != null) {
                    Platform.spawnDrops(w, x, y, z, or.getDrops());
                }

                return true;
            } else {
                final ForgeDirection dir = ForgeDirection.getOrientation(side);
                x += dir.offsetX;
                y += dir.offsetY;
                z += dir.offsetZ;

                if (!p.canPlayerEdit(x, y, z, side, item)) {
                    return false;
                }

                if (w.isAirBlock(x, y, z)) {
                    this.extractAEPower(item, 1600);
                    w.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "fire.ignite", 1.0F,
                            itemRand.nextFloat() * 0.4F + 0.8F);
                    w.setBlock(x, y, z, Blocks.fire);
                }

                return true;
            }
        }

        return false;
    }

}
