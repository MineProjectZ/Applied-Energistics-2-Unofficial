package appeng.client.render.blocks;

import appeng.block.legacy.BlockLegacyChest;
import appeng.client.render.BaseBlockRender;
import appeng.client.texture.ExtraBlockTextures;
import appeng.tile.legacy.TileLegacyChest;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class RenderBlockLegacyChest extends BaseBlockRender<BlockLegacyChest, TileLegacyChest> {

    @Override
    public boolean renderInWorld(BlockLegacyChest block, IBlockAccess world, int x, int y, int z, RenderBlocks renderer) {
        renderer.setRenderBounds(0.02, 0.0, 0.02, 0.98, 0.98, 0.98);
        renderer.renderAllFaces = true;
        renderer.renderStandardBlock(block, x, y, z);
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileLegacyChest && ((TileLegacyChest)tile).getCellStatus(0) > 0) {
            TileLegacyChest te = (TileLegacyChest)tile;
            final int bn = 9;
            float u = 6.0f;
            float v = 1.0f;
            int cheststatus = te.getCellStatus(0);
            if (te.isPowered()) {
                if (cheststatus == 1) {
                    u = 1.0f;
                    v = 9.0f;
                }
                else if (cheststatus == 2) {
                    u = 1.0f;
                    v = 5.0f;
                }
                else {
                    u = 1.0f;
                    v = 1.0f;
                }
            }
            final IIcon parts = ExtraBlockTextures.HDChestTopParts.getIcon();
            final float offsetPerPixel = 0.0625f;
            final float[] p1 = new float[5];
            final float[] p2 = new float[5];
            final float[] p3 = new float[5];
            final float[] p4 = new float[5];
            p1[0] = 4.0f * offsetPerPixel;
            p1[1] = 0.981f;
            p1[2] = 4.0f * offsetPerPixel;
            p1[3] = u;
            p1[4] = v;
            p2[0] = p1[0] + offsetPerPixel * 4.0f;
            p2[1] = p1[1];
            p2[2] = p1[2];
            p2[3] = p1[3] + 4.0f;
            p2[4] = p1[4];
            p3[0] = p1[0] + offsetPerPixel * 4.0f;
            p3[1] = p1[1];
            p3[2] = p1[2] + offsetPerPixel * 3.0f;
            p3[3] = p1[3] + 4.0f;
            p3[4] = p1[4] + 3.0f;
            p4[0] = p1[0];
            p4[1] = p1[1];
            p4[2] = p1[2] + offsetPerPixel * 3.0f;
            p4[3] = p1[3];
            p4[4] = p1[4] + 3.0f;
            final Tessellator tess = Tessellator.instance;
            if (te.isPowered()) {
                tess.setBrightness(bn << 20 | bn << 4 | bn << 3);
            }
            else {
                tess.setBrightness(block.getMixedBrightnessForBlock(world, x, y + 1, z));
            }
            tess.addVertexWithUV((double)(x + p4[0]), (double)(y + p4[1]), (double)(z + p4[2]), (double)parts.getInterpolatedU((double)p4[3]), (double)parts.getInterpolatedV((double)p4[4]));
            tess.addVertexWithUV((double)(x + p3[0]), (double)(y + p3[1]), (double)(z + p3[2]), (double)parts.getInterpolatedU((double)p3[3]), (double)parts.getInterpolatedV((double)p3[4]));
            tess.addVertexWithUV((double)(x + p2[0]), (double)(y + p2[1]), (double)(z + p2[2]), (double)parts.getInterpolatedU((double)p2[3]), (double)parts.getInterpolatedV((double)p2[4]));
            tess.addVertexWithUV((double)(x + p1[0]), (double)(y + p1[1]), (double)(z + p1[2]), (double)parts.getInterpolatedU((double)p1[3]), (double)parts.getInterpolatedV((double)p1[4]));
        }
        renderer.renderAllFaces = false;
        return true;
    }

}
