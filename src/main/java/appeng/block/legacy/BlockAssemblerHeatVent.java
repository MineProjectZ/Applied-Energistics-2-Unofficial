package appeng.block.legacy;

import appeng.block.AEBaseBlock;
import appeng.client.render.BaseBlockRender;
import appeng.client.render.blocks.RenderBlockAssemblerHeatVent;
import appeng.tile.AEBaseTile;

public class BlockAssemblerHeatVent extends BlockAssemblerMB {
    @Override
    protected BaseBlockRender<? extends AEBaseBlock, ? extends AEBaseTile> getRenderer() {
        return new RenderBlockAssemblerHeatVent();
    }
}
