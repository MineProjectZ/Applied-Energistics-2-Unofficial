package appeng.block.legacy;

import java.util.EnumSet;

import appeng.api.AEApi;
import appeng.api.storage.ICellHandler;
import appeng.block.AEBaseBlock;
import appeng.block.AEBaseTileBlock;
import appeng.client.render.BaseBlockRender;
import appeng.client.render.blocks.RenderBlockLegacyChest;
import appeng.core.features.AEFeature;
import appeng.core.localization.PlayerMessages;
import appeng.core.sync.GuiBridge;
import appeng.tile.AEBaseTile;
import appeng.tile.legacy.TileLegacyChest;
import appeng.tile.storage.TileChest;
import appeng.util.Platform;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockLegacyChest extends AEBaseTileBlock {

    public BlockLegacyChest() {
        super(Material.iron);
        this.isFullSize = this.isOpaque = false;
        this.setTileEntity(TileChest.class);
        this.setFeature(EnumSet.of(AEFeature.Legacy));
    }
    
    @Override
    protected BaseBlockRender<? extends AEBaseBlock, ? extends AEBaseTile> getRenderer() {
        return new RenderBlockLegacyChest();
    }

    @Override
    public boolean onActivated(
        final World w,
        final int x,
        final int y,
        final int z,
        final EntityPlayer p,
        final int side,
        final float hitX,
        final float hitY,
        final float hitZ
    ) {
        final TileChest tg = this.getTileEntity(w, x, y, z);
        if (tg != null && !p.isSneaking()) {
            if (Platform.isClient()) {
                return true;
            }

            if (side != tg.getUp().ordinal()) {
                Platform.openGUI(
                    p, tg, ForgeDirection.getOrientation(side), GuiBridge.GUI_CHEST
                );
            } else {
                final ItemStack cell = tg.getStackInSlot(1);
                if (cell != null) {
                    final ICellHandler ch
                        = AEApi.instance().registries().cell().getHandler(cell);

                    tg.openGui(p, ch, cell, side);
                } else {
                    p.addChatMessage(PlayerMessages.ChestCannotReadStorageCell.get());
                }
            }

            return true;
        }

        return false;
    }

}
