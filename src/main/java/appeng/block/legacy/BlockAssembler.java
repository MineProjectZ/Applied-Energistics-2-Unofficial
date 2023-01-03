package appeng.block.legacy;

import appeng.core.sync.GuiBridge;
import appeng.tile.legacy.TileAssembler;
import appeng.util.Platform;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockAssembler extends BlockAssemblerBase {
    public BlockAssembler() {
        super(TileAssembler.class);
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
            Platform.openGUI(
                p,
                tileEntity,
                ForgeDirection.getOrientation(side),
                GuiBridge.GUI_ASSEMBLER
            );
            return true;
        } else {
            return false;
        }
    }
}
