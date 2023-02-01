package appeng.block.legacy;

import java.util.EnumSet;

import appeng.api.util.WorldCoord;
import appeng.block.AEBaseBlock;
import appeng.block.AEBaseTileBlock;
import appeng.client.render.BaseBlockRender;
import appeng.client.render.blocks.RenderBlockTransitionPlane;
import appeng.core.features.AEFeature;
import appeng.tile.AEBaseTile;
import appeng.tile.legacy.TileTransitionPlane;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockTransitionPlane extends AEBaseTileBlock {
    public BlockTransitionPlane() {
        super(Material.iron);
        this.isOpaque = true;
        this.setTileEntity(TileTransitionPlane.class);
        this.setFeature(EnumSet.of(AEFeature.Legacy, AEFeature.Experimental));
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected BaseBlockRender<? extends AEBaseBlock, ? extends AEBaseTile> getRenderer() {
        return new RenderBlockTransitionPlane();
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
    public void onBlockPlacedBy(World w, int x, int y, int z, EntityLivingBase player, ItemStack is) {
        super.onBlockPlacedBy(w, x, y, z, player, is);
        this.calcMB(w, x, y, z);
    }

    @Override
    public void onPostBlockPlaced(World world, int x, int y, int z, int md) {
        super.onPostBlockPlaced(world, x, y, z, md);
        this.calcMB(world, x, y, z);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
        this.calcMB(world, x, y, z);
        ((TileTransitionPlane) world.getTileEntity(x, y, z)).reqEat();
    }

    private void calcMB(World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileTransitionPlane) {
            ((TileTransitionPlane) te)
                .calc.calculateMultiblock(world, new WorldCoord(x, y, z));
        }
    }
}
