package appeng.client.render.blocks;

import appeng.block.legacy.BlockAssemblerWall;
import appeng.client.render.BaseBlockRender;
import appeng.client.texture.ExtraBlockTextures;
import appeng.tile.legacy.TileAssemblerMB;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

public class RenderBlockAssemblerWall
    extends BaseBlockRender<BlockAssemblerWall, TileAssemblerMB> {
    @Override
    public boolean renderInWorld(
        BlockAssemblerWall block,
        IBlockAccess world,
        int x,
        int y,
        int z,
        RenderBlocks renderer
    ) {
        TileAssemblerMB tile = (TileAssemblerMB) world.getTileEntity(x, y, z);
        if (tile != null && tile.complete) {
            boolean XAxis = world.getTileEntity(x + 1, y, z) instanceof TileAssemblerMB
                && world.getTileEntity(x - 1, y, z) instanceof TileAssemblerMB;
            boolean YAxis = world.getTileEntity(x, y + 1, z) instanceof TileAssemblerMB
                && world.getTileEntity(x, y - 1, z) instanceof TileAssemblerMB;
            boolean ZAxis = world.getTileEntity(x, y, z + 1) instanceof TileAssemblerMB
                && world.getTileEntity(x, y, z - 1) instanceof TileAssemblerMB;
            if (XAxis) {
                renderer.setRenderBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                block.getRendererInstance().setTemporaryRenderIcon(
                    ExtraBlockTextures.BlockAssemblerWallMerged.getIcon()
                );
                renderer.setRenderBoundsFromBlock(block);
                renderer.renderStandardBlock(block, x, y, z);
                block.getRendererInstance().setTemporaryRenderIcon(null);
                return true;
            }

            if (YAxis) {
                renderer.uvRotateWest = 1;
                renderer.uvRotateEast = 1;
                renderer.uvRotateNorth = 1;
                renderer.uvRotateSouth = 1;
                renderer.setRenderBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                block.getRendererInstance().setTemporaryRenderIcon(
                    ExtraBlockTextures.BlockAssemblerWallMerged.getIcon()
                );
                renderer.setRenderBoundsFromBlock(block);
                renderer.renderStandardBlock(block, x, y, z);
                block.getRendererInstance().setTemporaryRenderIcon(null);
                renderer.uvRotateWest = 0;
                renderer.uvRotateEast = 0;
                renderer.uvRotateNorth = 0;
                renderer.uvRotateSouth = 0;
                return true;
            }

            if (ZAxis) {
                renderer.uvRotateTop = 1;
                renderer.uvRotateBottom = 1;
                renderer.setRenderBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                block.getRendererInstance().setTemporaryRenderIcon(
                    ExtraBlockTextures.BlockAssemblerWallMerged.getIcon()
                );
                renderer.setRenderBoundsFromBlock(block);
                renderer.renderStandardBlock(block, x, y, z);
                block.getRendererInstance().setTemporaryRenderIcon(null);
                renderer.uvRotateTop = 0;
                renderer.uvRotateBottom = 0;
                return true;
            }
        }

        return super.renderInWorld(block, world, x, y, z, renderer);
    }
}
