package appeng.block.legacy;

import java.util.EnumSet;

import appeng.block.AEBaseTileBlock;
import appeng.core.features.AEFeature;
import appeng.core.sync.GuiBridge;
import appeng.tile.networking.TileWireless;
import appeng.util.Platform;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockWirelessAccessPoint extends AEBaseTileBlock {
    public BlockWirelessAccessPoint() {
        super(Material.iron);
        this.isOpaque = true;
        this.setTileEntity(TileWireless.class);
        this.setFeature(EnumSet.of(AEFeature.Legacy));
    }

    @Override
    public boolean onBlockActivated(
        World w,
        int x,
        int y,
        int z,
        EntityPlayer p,
        int side,
        // useless parameters
        float alec1,
        float alec2,
        float alec3
    ) {
        TileEntity tileEntity = w.getTileEntity(x, y, z);
        if (tileEntity != null && !p.isSneaking()) {
            Platform.openGUI(
                p, tileEntity, ForgeDirection.getOrientation(side), GuiBridge.GUI_WIRELESS
            );
            return true;
        } else {
            return false;
        }
    }
}
