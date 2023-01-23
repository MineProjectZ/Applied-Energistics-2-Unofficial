package appeng.client.render.blocks;

import appeng.block.legacy.BlockLegacyChest;
import appeng.client.render.BaseBlockRender;
import appeng.tile.legacy.TileLegacyChest;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

public class RenderBlockLegacyChest extends BaseBlockRender<BlockLegacyChest, TileLegacyChest> {

    @Override
    public boolean renderInWorld(BlockLegacyChest block, IBlockAccess world, int x, int y, int z, RenderBlocks renderer) {
        renderer.setRenderBounds(0.02, 0.0, 0.02, 0.98, 0.98, 0.98);
        renderer.renderAllFaces = true;
        renderer.renderStandardBlock(block, x, y, z);
        renderer.renderAllFaces = false;
        return true;
    }

}
