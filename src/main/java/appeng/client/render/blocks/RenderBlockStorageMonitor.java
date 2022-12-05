package appeng.client.render.blocks;

import appeng.api.storage.data.IAEItemStack;
import appeng.block.AEBaseTileBlock;
import appeng.client.ClientHelper;
import appeng.core.AELog;
import appeng.tile.legacy.TileLegacyDisplay;
import appeng.tile.legacy.TileStorageMonitor;
import appeng.util.ReadableNumberConverter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class RenderBlockStorageMonitor extends RenderBlockLegacyDisplay {
    private int dspList;

    public RenderBlockStorageMonitor() {
        this.dspList = GLAllocation.generateDisplayLists(1);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        GLAllocation.deleteDisplayLists(this.dspList);
    }

    @Override
    public boolean hasTESR() {
        return true;
    }

    @Override
    public void renderTile(
        AEBaseTileBlock b,
        TileLegacyDisplay tile_,
        Tessellator tess,
        double x,
        double y,
        double z,
        float f,
        RenderBlocks renderer
    ) {
        TileStorageMonitor tile = (TileStorageMonitor) tile_;
        if (tile.myItem != null && tile.isDisplayPowered()) {
            GL11.glPushMatrix();
            GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
            if (tile.updateDisplayList) {
                tile.updateDisplayList = false;
                GL11.glNewList(this.dspList, GL11.GL_COMPILE_AND_EXECUTE);
                this.tesrRenderScreen(tile, tess, tile.myItem);
                GL11.glEndList();
            } else {
                GL11.glCallList(this.dspList);
            }
            GL11.glPopMatrix();
        }
    }

    private void
    tesrRenderScreen(TileLegacyDisplay tile, Tessellator tess, IAEItemStack ais) {
        // GL11.glPushAttrib( GL11.GL_ALL_ATTRIB_BITS );

        final ForgeDirection d = tile.getForward();

        GL11.glTranslated(d.offsetX * 0.77, d.offsetY * 0.77, d.offsetZ * 0.77);

        switch (d) {
            case UP:
                GL11.glScalef(1.0f, -1.0f, 1.0f);
                GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
                GL11.glRotatef(tile.getUp().ordinal() * 90.0F, 0, 0, 1);
                break;
            case DOWN:
                GL11.glScalef(1.0f, -1.0f, 1.0f);
                GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
                GL11.glRotatef(tile.getUp().ordinal() * -90.0F, 0, 0, 1);
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

        try {
            final ItemStack sis = ais.getItemStack();
            sis.stackSize = 1;

            final int br = 16 << 20 | 16 << 4;
            final int var11 = br % 65536;
            final int var12 = br / 65536;
            OpenGlHelper.setLightmapTextureCoords(
                OpenGlHelper.lightmapTexUnit, var11 * 0.8F, var12 * 0.8F
            );

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            // RenderHelper.enableGUIStandardItemLighting();
            tess.setColorOpaque_F(1.0f, 1.0f, 1.0f);

            ClientHelper.proxy.doRenderItem(sis, tile.getWorldObj());
        } catch (final Exception e) {
            AELog.debug(e);
        } finally {
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        }

        GL11.glTranslatef(0.0f, 0.14f, -0.24f);
        GL11.glScalef(1.0f / 62.0f, 1.0f / 62.0f, 1.0f / 62.0f);

        final long stackSize = ais.getStackSize();
        final String renderedStackSize
            = ReadableNumberConverter.INSTANCE.toWideReadableForm(stackSize);

        final FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        final int width = fr.getStringWidth(renderedStackSize);
        GL11.glTranslatef(-0.5f * width, 0.0f, -1.0f);
        fr.drawString(renderedStackSize, 0, 0, 0);

        // GL11.glPopAttrib();
    }
}
