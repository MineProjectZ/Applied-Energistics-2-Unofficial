package appeng.block.legacy;

import java.util.EnumSet;

import appeng.block.AEBaseTileBlock;
import appeng.client.texture.ExtraBlockTextures;
import appeng.core.features.AEFeature;
import appeng.core.sync.GuiBridge;
import appeng.tile.legacy.TileLegacyController;
import appeng.util.Platform;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockLegacyController extends AEBaseTileBlock {
    public BlockLegacyController() {
        super(Material.iron);
        this.setTileEntity(TileLegacyController.class);
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
        Platform.openGUI(
            player,
            w.getTileEntity(x, y, z),
            ForgeDirection.getOrientation(side),
            GuiBridge.GUI_NETWORK_STATUS_BLOCK
        );
        return true;
    }

    @Override
    public IIcon getIcon(int direction, int metadata) {
        if (direction == 3) {
            switch (metadata) {
                case 0:
                    return ExtraBlockTextures.Controller0.getIcon();
                case 1:
                    return ExtraBlockTextures.Controller1.getIcon();
                case 2:
                    return ExtraBlockTextures.Controller2.getIcon();
                case 3:
                    return ExtraBlockTextures.Controller3.getIcon();
                case 4:
                    return ExtraBlockTextures.Controller4.getIcon();
                case 5:
                    return ExtraBlockTextures.Controller5.getIcon();
                case 6:
                    return ExtraBlockTextures.ControllerLinked.getIcon();
            }
        }
        return super.getIcon(direction, metadata);
    }
}
