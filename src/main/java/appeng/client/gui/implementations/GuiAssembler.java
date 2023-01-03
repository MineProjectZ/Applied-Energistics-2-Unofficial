package appeng.client.gui.implementations;

import appeng.client.gui.AEBaseGui;
import appeng.container.implementations.ContainerAssembler;
import appeng.tile.legacy.TileAssembler;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

public class GuiAssembler extends AEBaseGui {
    public GuiAssembler(InventoryPlayer inventoryPlayer, TileAssembler tileEntity) {
        super(new ContainerAssembler(inventoryPlayer, tileEntity));
        this.ySize = 222;
    }

    @Override
    public void drawFG(int offsetX, int offsetY, int mouseX, int mouseY) {
        this.fontRendererObj.drawString(
            StatCollector.translateToLocal("gui.appliedenergistics2.Assembler"),
            8,
            6,
            4210752
        );
        this.fontRendererObj.drawString(
            StatCollector.translateToLocal("container.inventory"),
            8,
            this.ySize - 96 + 3,
            4210752
        );
    }

    @Override
    public void drawBG(int offsetX, int offsetY, int mouseX, int mouseY) {
        this.bindTexture("guis/assembler.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
    }
}
