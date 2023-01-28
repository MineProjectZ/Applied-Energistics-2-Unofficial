package appeng.block.legacy;

import java.util.EnumSet;

import appeng.block.AEBaseTileBlock;
import appeng.core.features.AEFeature;
import appeng.tile.legacy.TilePowerRelay;
import net.minecraft.block.material.Material;

public class BlockPowerRelay extends AEBaseTileBlock {

    public BlockPowerRelay() {
        super(Material.iron);
        this.setTileEntity(TilePowerRelay.class);
        this.setFeature(EnumSet.of(AEFeature.Legacy));
    }
    
}
