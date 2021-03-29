package appeng.block.legacy;

import appeng.block.AEBaseTileBlock;
import appeng.core.features.AEFeature;
import appeng.tile.legacy.TileLegacyController;
import net.minecraft.block.material.Material;

import java.util.EnumSet;

public class BlockLegacyController extends AEBaseTileBlock {

    public BlockLegacyController() {
        super(Material.iron);
        this.setTileEntity(TileLegacyController.class);
        this.setFeature( EnumSet.of(AEFeature.Legacy) );
    }


}
