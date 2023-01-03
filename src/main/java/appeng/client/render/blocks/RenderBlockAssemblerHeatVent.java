package appeng.client.render.blocks;

import appeng.block.legacy.BlockAssemblerHeatVent;
import appeng.client.render.BaseBlockRender;
import appeng.client.texture.ExtraBlockTextures;
import appeng.tile.legacy.TileAssemblerMB;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

public class RenderBlockAssemblerHeatVent
    extends BaseBlockRender<BlockAssemblerHeatVent, TileAssemblerMB> {
    @Override
    public boolean renderInWorld(
        BlockAssemblerHeatVent block,
        IBlockAccess world,
        int x,
        int y,
        int z,
        RenderBlocks renderer
    ) {
        TileAssemblerMB tile = (TileAssemblerMB) world.getTileEntity(x, y, z);
        if (tile != null && tile.complete) {
            block.getRendererInstance().setTemporaryRenderIcon(
                ExtraBlockTextures.BlockAssemblerHeatVentMerged.getIcon()
            );
            renderer.setRenderBoundsFromBlock(block);
            renderer.renderStandardBlock(block, x, y, z);
            block.getRendererInstance().setTemporaryRenderIcon(null);
            return true;
        }
        return super.renderInWorld(block, world, x, y, z, renderer);
    }
}
