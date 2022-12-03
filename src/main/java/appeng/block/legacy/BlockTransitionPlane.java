package appeng.block.legacy;

import java.util.EnumSet;

import appeng.block.AEBaseTileBlock;
import appeng.core.features.AEFeature;
import appeng.tile.legacy.TileTransitionPlane;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockTransitionPlane extends AEBaseTileBlock {
    public BlockTransitionPlane() {
        super(Material.iron);
        this.isOpaque = true;
        this.setTileEntity(TileTransitionPlane.class);
        this.setFeature(EnumSet.of(AEFeature.Legacy));
    }

    @Override
    public void onEntityCollidedWithBlock(World w, int x, int y, int z, Entity entitiy) {
        if (entitiy instanceof EntityItem) {
            TileEntity te = w.getTileEntity(x, y, z);
            if (te instanceof TileTransitionPlane) {
                // TODO: WTF
                //((TileTransitionPlane) te).addItem((EntityItem) entitiy);
            }
        }
    }

    @Override
    public void onNeighborBlockChange(
        World world,
        int x,
        int y,
        int z,
        Block neighbor
    ) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileTransitionPlane) {
            ((TileTransitionPlane)te).reqEat();
        }
    }

    //@Override
    //public IIcon getIcon(int side, int meta) {
    //    if (side == 0) {
    //        return AppEngTextureRegistry.Blocks.GenericBottom.get();
    //    } else if (side == 1) {
    //        return AppEngTextureRegistry.Blocks.GenericTop.get();
    //    } else {
    //        return side == 2 ? AppEngTextureRegistry.Blocks.BlockTransPlane.get()
    //                         : AppEngTextureRegistry.Blocks.GenericSide.get();
    //    }
    //}
}
