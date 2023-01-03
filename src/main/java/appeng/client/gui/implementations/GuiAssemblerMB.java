package appeng.client.gui.implementations;

import appeng.client.gui.AEBaseGui;
import appeng.container.implementations.ContainerAssemblerMB;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.PacketChangeAssemblerGuiPage;
import appeng.core.sync.packets.PacketUpdateAssemblerGuiPageNum;
import appeng.tile.legacy.TileAssembler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

public class GuiAssemblerMB extends AEBaseGui {
    public int pageNumber = 0;
    public int maxPages = 0;
    public GuiButton next;
    public GuiButton prev;

    public GuiAssemblerMB(InventoryPlayer inventoryPlayer, TileAssembler tileEntity) {
        super(new ContainerAssemblerMB(inventoryPlayer, tileEntity));
        this.ySize = 222;
        this.xSize = 223;
    }

    @Override
    public void drawFG(int offsetX, int offsetY, int mouseX, int mouseY) {
        this.fontRendererObj.drawString(
            StatCollector.translateToLocal("gui.appliedenergistics2.MAC"), 8, 6, 4210752
        );
        this.fontRendererObj.drawString(
            StatCollector.translateToLocal("container.inventory"),
            8,
            this.ySize - 96 + 3,
            4210752
        );
        if (this.maxPages == 0) {
            this.fontRendererObj.drawString("WTF", 178, 56, 4210752);
        } else {
            this.fontRendererObj.drawString(
                1 + this.pageNumber + " / " + this.maxPages, 178, 56, 4210752
            );
        }
    }

    @Override
    public void drawBG(int offsetX, int offsetY, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.bindTexture("guis/mulitiassembler.png");
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.next.xPosition = x + this.xSize - 48;
        this.next.yPosition = y + 6;
        this.prev.xPosition = x + this.xSize - 48;
        this.prev.yPosition = y + 29;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.add(
            this.next = new GuiButton(
                1,
                238,
                94,
                42,
                20,
                StatCollector.translateToLocal("gui.appliedenergistics2.Next")
            )
        );
        this.buttonList.add(
            this.prev = new GuiButton(
                1,
                238,
                94,
                42,
                20,
                StatCollector.translateToLocal("gui.appliedenergistics2.Prev")
            )
        );
        NetworkHandler.instance.sendToServer(new PacketUpdateAssemblerGuiPageNum(0, 0));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == this.next) {
            NetworkHandler.instance.sendToServer(new PacketChangeAssemblerGuiPage(1));
        } else if (button == this.prev) {
            NetworkHandler.instance.sendToServer(new PacketChangeAssemblerGuiPage(-1));
        }
    }
}
