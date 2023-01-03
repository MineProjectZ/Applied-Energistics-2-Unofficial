package appeng.block.legacy;

import appeng.core.sync.GuiBridge;
import appeng.tile.legacy.TileAssemblerMB;
import appeng.util.Platform;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * A base class that all assembler wall blocks inherit from
 */
public abstract class BlockAssemblerMB extends BlockAssemblerBase {
    public BlockAssemblerMB() {
        this(TileAssemblerMB.class);
    }

    public BlockAssemblerMB(Class<? extends TileEntity> te) {
        super(te);
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
        if (tileEntity != null) {
            TileAssemblerMB tamb = (TileAssemblerMB) tileEntity;
            if (tamb.isComplete()) {
                if (Platform.isServer()) {
                    Platform.openGUI(
                        p,
                        tamb.ac.assemblers.get(0),
                        ForgeDirection.getOrientation(side),
                        GuiBridge.GUI_ASSEMBLER_MB
                    );
                }
                return true;
            }
        }
        return false;
    }
}
