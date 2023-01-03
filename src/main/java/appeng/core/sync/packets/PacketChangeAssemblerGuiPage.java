package appeng.core.sync.packets;

import appeng.container.implementations.ContainerAssembler;
import appeng.core.sync.AppEngPacket;
import appeng.core.sync.network.INetworkInfo;
import appeng.core.sync.network.NetworkHandler;
import appeng.me.cluster.implementations.AssemblerCluster;
import appeng.tile.legacy.TileAssembler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;

public class PacketChangeAssemblerGuiPage extends AppEngPacket {
    int direction;

    // automatic.
    public PacketChangeAssemblerGuiPage(ByteBuf buf) {
        this.direction = buf.readInt();
    }

    // api
    public PacketChangeAssemblerGuiPage(int direction) {
        ByteBuf buf = Unpooled.buffer();

        buf.writeInt(this.getPacketID());
        buf.writeInt(this.direction = direction);

        this.configureWrite(buf);
    }

    @Override
    public void
    serverPacketData(INetworkInfo manager, AppEngPacket packet, EntityPlayer player) {
        EntityPlayerMP pmp = (EntityPlayerMP) player;
        Container c = pmp.openContainer;
        int offset;
        if (c != null && c instanceof ContainerAssembler) {
            ContainerAssembler ca = (ContainerAssembler) c;
            TileAssembler ta = (TileAssembler) ca.getTileEntity();
            if (ta != null) {
                AssemblerCluster ac = (AssemblerCluster) ta.getCluster();
                if (ac == null) {
                    return;
                }

                if (ac.assemblers == null) {
                    return;
                }

                offset = -1;

                for (int i = 0; i < ac.assemblers.size(); i++) {
                    if (ac.assemblers.get(i) == ta) {
                        offset = i;
                    }
                }

                offset += this.direction;
                if (offset < 0) {
                    offset = ac.assemblers.size() - 1;
                }

                if (offset >= ac.assemblers.size()) {
                    offset = 0;
                }

                ac.setLastOffset(offset);
                TileAssembler nta = ac.getAssembler(offset);
                ca.myinv.setInventory(nta);
                ca.setTileEntity(nta);

                NetworkHandler.instance.sendTo(
                    new PacketUpdateAssemblerGuiPageNum(offset, ac.assemblers.size()),
                    (EntityPlayerMP) player
                );
            }
        }
    }
}
