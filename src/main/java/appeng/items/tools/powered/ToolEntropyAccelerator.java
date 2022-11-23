package appeng.items.tools.powered;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class ToolEntropyAccelerator extends ToolEntropyManipulator {

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

            if (this.canCool(blockID, metadata)) {
                if (this.cool(blockID, p, metadata, w, x, y, z)) {
                    this.extractAEPower(item, 1600);
                    return true;
                }
                return false;
            }
        }

        return false;
    }

}
