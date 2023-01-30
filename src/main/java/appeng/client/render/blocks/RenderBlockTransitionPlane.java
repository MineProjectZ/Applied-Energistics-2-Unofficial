package appeng.client.render.blocks;

import appeng.block.legacy.BlockTransitionPlane;
import appeng.client.render.BaseBlockRender;
import appeng.client.texture.ExtraBlockTextures;
import appeng.tile.legacy.TileTransitionPlane;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class RenderBlockTransitionPlane
    extends BaseBlockRender<BlockTransitionPlane, TileTransitionPlane> {
    @Override
    public boolean hasTESR() {
        return true;
    }

    @Override
    public void renderTile(
        BlockTransitionPlane block,
        TileTransitionPlane tile,
        Tessellator tess,
        double x,
        double y,
        double z,
        float f,
        RenderBlocks renderer
    ) {
        IIcon frontIcon = null;
        if (!tile.getProxy().isPowered()) {
            frontIcon = ExtraBlockTextures.BlockTransitionPlaneNoPower.getIcon();
        } else if (!tile.getProxy().isActive()) {
            frontIcon = ExtraBlockTextures.BlockTransitionPlaneOff.getIcon();
        } else {
            frontIcon = ExtraBlockTextures.BlockTransitionPlaneNormal.getIcon();
        }

        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture
        );

        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);

        ForgeDirection d = tile.getForward();

        switch (d) {
            case UP:
                GL11.glScalef(1.0f, -1.0f, 1.0f);
                GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
                GL11.glRotatef((tile.getUp().ordinal() - 2) * 90.0F, 0, 0, 1);
                break;
            case DOWN:
                GL11.glScalef(1.0f, -1.0f, 1.0f);
                GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
                GL11.glRotatef((tile.getUp().ordinal() - 2) * -90.0F, 0, 0, 1);
                break;
            case EAST:
                GL11.glScalef(-1.0f, -1.0f, -1.0f);
                GL11.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f);
                break;
            case WEST:
                GL11.glScalef(-1.0f, -1.0f, -1.0f);
                GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
                break;
            case NORTH:
                GL11.glScalef(-1.0f, -1.0f, -1.0f);
                break;
            case SOUTH:
                GL11.glScalef(-1.0f, -1.0f, -1.0f);
                GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                break;

            default:
                break;
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);

        switch (tile.renderMode) {
            case INVALID:
                GL11.glScaled(-0.75, -0.75, 1.0);
                break;

            case SINGLE:
                GL11.glScaled(-0.9, -0.9, 1.0);
                break;

            default:
                break;
        }

        tess.startDrawingQuads();

        tess.addVertexWithUV(-0.5, 0.5, 0.51, frontIcon.getMinU(), frontIcon.getMaxV());
        tess.addVertexWithUV(0.5, 0.5, 0.51, frontIcon.getMaxU(), frontIcon.getMaxV());
        tess.addVertexWithUV(0.5, -0.5, 0.51, frontIcon.getMaxU(), frontIcon.getMinV());
        tess.addVertexWithUV(-0.5, -0.5, 0.51, frontIcon.getMinU(), frontIcon.getMinV());

        tess.draw();

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);

        GL11.glPopMatrix();
    }
}
