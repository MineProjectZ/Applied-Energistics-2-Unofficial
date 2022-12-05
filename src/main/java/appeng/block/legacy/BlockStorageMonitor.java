package appeng.block.legacy;

import java.util.EnumSet;

import appeng.block.AEBaseBlock;
import appeng.client.render.BaseBlockRender;
import appeng.client.render.blocks.RenderBlockStorageMonitor;
import appeng.core.Api;
import appeng.core.features.AEFeature;
import appeng.tile.AEBaseTile;
import appeng.tile.legacy.TileStorageMonitor;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BlockStorageMonitor extends BlockLegacyDisplay {
    public BlockStorageMonitor() {
        super(Material.iron);
        this.setTileEntity(TileStorageMonitor.class);
        this.setFeature(EnumSet.of(AEFeature.Legacy));
    }

    @Override
    protected BaseBlockRender<? extends AEBaseBlock, ? extends AEBaseTile> getRenderer() {
        return new RenderBlockStorageMonitor();
    }

    @Override
    public boolean onBlockActivated(
        World w,
        int x,
        int y,
        int z,
        EntityPlayer player,
        int side,
        float hitX,
        float hitY,
        float hitZ
    ) {
        TileStorageMonitor tile = (TileStorageMonitor) w.getTileEntity(x, y, z);

        if (player.getHeldItem() != null) {
            if (Platform.isWrench(player, player.getHeldItem(), x, y, z)
                && player.isSneaking()) {
                tile.isLocked = !tile.isLocked;
                tile.markForUpdate();
                return true;
            }
            if (player.getHeldItem().getItem()
                    == Api.INSTANCE.definitions()
                           .materials()
                           .conversionMatrix()
                           .maybeItem()
                           .get()
                && !tile.upgraded && player.isSneaking()) {
                if (!w.isRemote) {
                    player.inventory.decrStackSize(player.inventory.currentItem, 1);
                    tile.upgraded = true;
                    tile.markForUpdate();
                }
                return true;
            } else if (side == tile.getForward().ordinal() && !tile.isLocked) {
                if (!w.isRemote) {
                    tile.myItem = AEItemStack.create(new ItemStack(
                        player.getHeldItem().getItem(),
                        0,
                        player.getHeldItem().getItemDamage()
                    ));
                    tile.configureWatchers();
                    tile.markForUpdate();
                }
                return true;
            }
        }

        return super.onBlockActivated(w, x, y, z, player, side, hitX, hitY, hitZ);
    }
}
