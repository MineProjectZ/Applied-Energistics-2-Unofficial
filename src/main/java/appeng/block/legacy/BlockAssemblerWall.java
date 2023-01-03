package appeng.block.legacy;

import appeng.block.AEBaseBlock;
import appeng.client.render.BaseBlockRender;
import appeng.client.render.blocks.RenderBlockAssemblerWall;
import appeng.tile.AEBaseTile;

public class BlockAssemblerWall extends BlockAssemblerMB {
    @Override
    protected BaseBlockRender<? extends AEBaseBlock, ? extends AEBaseTile> getRenderer() {
        return new RenderBlockAssemblerWall();
    }
}
