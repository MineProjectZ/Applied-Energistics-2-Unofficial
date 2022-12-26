package appeng.client.gui.implementations;

import java.io.IOException;

import appeng.client.gui.AEBaseGui;
import appeng.container.implementations.ContainerPatternEncoder;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.PacketValueConfig;
import appeng.tile.legacy.TilePatternEncoder;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

public class GuiPatternEncoder extends AEBaseGui {
    GuiButton encodeBtn;
    GuiButton clearBtn;

    public GuiPatternEncoder(
        InventoryPlayer inventoryPlayer, TilePatternEncoder tileEntity
    ) {
        super(new ContainerPatternEncoder(inventoryPlayer, tileEntity));
        this.ySize = 172;
        this.xSize = 176;
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        try {
            if (guibutton == this.clearBtn) {
                NetworkHandler.instance.sendToServer(
                    (new PacketValueConfig("PatternEncoder.Clear", "1"))
                );
            } else {
                NetworkHandler.instance.sendToServer(
                    (new PacketValueConfig("PatternEncoder.Encode", "1"))
                );
            }
        } catch (IOException var4) {
            var4.printStackTrace();
        }
    }

    public void initGui() {
        super.initGui();
        this.buttonList.add(
            this.clearBtn = new GuiButton(
                1, 238, 94, 42, 20, StatCollector.translateToLocal("gui.appliedenergistics2.PatternEncoder.clear")
            )
        );
        this.buttonList.add(
            this.encodeBtn = new GuiButton(
                1, 238, 94, 42, 20, StatCollector.translateToLocal("gui.appliedenergistics2.PatternEncoder.encode")
            )
        );
    }

    @Override
    public void drawFG(int offsetX, int offsetY, int mouseX, int mouseY) {
        this.fontRendererObj.drawString(
            StatCollector.translateToLocal("gui.appliedenergistics2.PatternEncoder"), 8, 6, 4210752
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
        this.bindTexture("guis/me_pattern_encoder.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.encodeBtn.xPosition = x + 105;
        this.encodeBtn.yPosition = y + 57 + 6;
        this.clearBtn.xPosition = x + 105;
        this.clearBtn.yPosition = y + 9 + 6;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
    }
}
