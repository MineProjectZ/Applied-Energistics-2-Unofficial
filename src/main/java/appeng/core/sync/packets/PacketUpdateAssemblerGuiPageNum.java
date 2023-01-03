package appeng.core.sync.packets;

import appeng.client.gui.implementations.GuiAssemblerMB;
import appeng.container.implementations.ContainerAssembler;
import appeng.core.sync.AppEngPacket;
import appeng.core.sync.network.INetworkInfo;
import appeng.core.sync.network.NetworkHandler;
import appeng.me.cluster.implementations.AssemblerCluster;
import appeng.tile.legacy.TileAssembler;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class PacketUpdateAssemblerGuiPageNum extends AppEngPacket {
    int offset;
    int size;

    // automatic.
    public PacketUpdateAssemblerGuiPageNum(ByteBuf buf) {
        this.offset = buf.readInt();
        this.size = buf.readInt();
    }

    // api
    public PacketUpdateAssemblerGuiPageNum(int offset, int size) {
        ByteBuf buf = Unpooled.buffer();

        buf.writeInt(this.getPacketID());
        buf.writeInt(this.offset = offset);
        buf.writeInt(this.size = size);

        this.configureWrite(buf);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void
    clientPacketData(INetworkInfo network, AppEngPacket packet, EntityPlayer player) {
        GuiScreen cs = FMLClientHandler.instance().getClient().currentScreen;

        if (cs instanceof GuiAssemblerMB) {
            GuiAssemblerMB ga = (GuiAssemblerMB) cs;

            ga.pageNumber = this.offset;
            ga.maxPages = this.size;
        }
    }

    @Override
    public void
    serverPacketData(INetworkInfo manager, AppEngPacket packet, EntityPlayer player) {
        if (player.openContainer instanceof ContainerAssembler) {
            ContainerAssembler ca = (ContainerAssembler) player.openContainer;
            TileAssembler ta = (TileAssembler) ca.getTileEntity();
            AssemblerCluster ac = (AssemblerCluster) ta.getCluster();

            int offset = -1;
            for (int i = 0; i < ac.assemblers.size(); i++) {
                if (ac.assemblers.get(i) == ta) {
                    offset = i;
                    break;
                }
            }

            NetworkHandler.instance.sendTo(
                new PacketUpdateAssemblerGuiPageNum(offset, ac.assemblers.size()),
                (EntityPlayerMP) player
            );
        }
    }
}
