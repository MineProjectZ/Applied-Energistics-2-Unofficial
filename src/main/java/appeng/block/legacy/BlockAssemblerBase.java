package appeng.block.legacy;

import java.util.EnumSet;

import appeng.block.AEBaseTileBlock;
import appeng.core.features.AEFeature;
import appeng.me.cluster.IAssemblerMB;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

public abstract class BlockAssemblerBase extends AEBaseTileBlock {
    public BlockAssemblerBase(Class<? extends TileEntity> tile) {
        super(Material.iron);
        this.setTileEntity(tile);
        this.setFeature(EnumSet.of(AEFeature.Legacy));
    }

    @Override
    public void onNeighborChange(
        IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ
    ) {
        super.onNeighborChange(world, x, y, z, tileX, tileY, tileZ);
        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof IAssemblerMB))
            return;

        ((IAssemblerMB) te).calculateMultiblock();
    }
}
