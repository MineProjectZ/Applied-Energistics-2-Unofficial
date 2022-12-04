package appeng.client.render.blocks;

import appeng.block.AEBaseTileBlock;
import appeng.client.render.BaseBlockRender;
import appeng.tile.legacy.TileLegacyDisplay;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;

public class RenderBlockLegacyDisplay
    extends BaseBlockRender<AEBaseTileBlock, TileLegacyDisplay> {
    public RenderBlockLegacyDisplay() {
        super(false, 20);
    }

    @Override
    public boolean renderInWorld(
        AEBaseTileBlock b, IBlockAccess world, int x, int y, int z, RenderBlocks renderer
    ) {
        TileLegacyDisplay tile = (TileLegacyDisplay) world.getTileEntity(x, y, z);
        renderer.setRenderBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        if (tile.isDisplayPowered()) {
            //b.dontrender = tile.noScreen();
            renderer.renderStandardBlock(b, x, y, z);
            //b.dontrender = tile.screenOnly();
            int bn = 15;
            Tessellator.instance.setColorOpaque_F(1.0F, 1.0F, 1.0F);
            Tessellator.instance.setBrightness(bn << 20 | bn << 4);
            this.renderFace(
                x,
                y,
                z,
                b,
                //b.getIcon(tile.getForward().ordinal(), tile.getBlockMetadata()),
                b.getIcon(world, x, y, z, tile.getForward().ordinal()),
                renderer,
                tile.getForward()
            );
        } else {
            renderer.renderStandardBlock(b, x, y, z);
        }

        return true;
    }
}
