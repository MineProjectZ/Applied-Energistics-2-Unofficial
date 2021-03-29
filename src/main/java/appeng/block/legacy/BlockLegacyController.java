package appeng.block.legacy;

import appeng.block.AEBaseTileBlock;
import appeng.core.features.AEFeature;
import appeng.core.sync.GuiBridge;
import appeng.tile.legacy.TileLegacyController;
import appeng.util.Platform;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.EnumSet;

public class BlockLegacyController extends AEBaseTileBlock {

    public BlockLegacyController() {
        super(Material.iron);
        this.setTileEntity(TileLegacyController.class);
        this.setFeature( EnumSet.of(AEFeature.Legacy) );
    }

    @Override
    public boolean onActivated(World w, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        Platform.openGUI(player, w.getTileEntity(x, y, z), ForgeDirection.getOrientation(side), GuiBridge.GUI_NETWORK_STATUS_BLOCK);
        return true;
    }

}
