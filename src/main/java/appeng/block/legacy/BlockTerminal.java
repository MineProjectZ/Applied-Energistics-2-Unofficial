package appeng.block.legacy;

import java.util.EnumSet;

import appeng.block.AEBaseTileBlock;
import appeng.core.features.AEFeature;
import appeng.core.sync.GuiBridge;
import appeng.tile.legacy.TileTerminal;
import appeng.util.Platform;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockTerminal extends AEBaseTileBlock {
    public BlockTerminal() {
        super(Material.iron);
        this.setTileEntity(TileTerminal.class);
        this.setFeature(EnumSet.of(AEFeature.Legacy));
    }

    @Override
    public boolean onActivated(
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
        final TileTerminal tile = this.getTileEntity(w, x, y, z);
        if (tile != null && !player.isSneaking()) {
            if (Platform.isClient()) {
                return true;
            }

            Platform.openGUI(
                player, tile, ForgeDirection.getOrientation(side), GuiBridge.GUI_ME
            );

            return true;
        }
        return false;
    }
}
