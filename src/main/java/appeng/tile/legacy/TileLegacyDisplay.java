package appeng.tile.legacy;

import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.events.MENetworkPowerStatusChange;
import appeng.tile.TileEvent;
import appeng.tile.events.TileEventType;
import appeng.tile.grid.AENetworkTile;
import io.netty.buffer.ByteBuf;

public class TileLegacyDisplay extends AENetworkTile {
    private boolean displayPowered;

    public boolean isDisplayPowered() {
        return displayPowered;
    }

    @TileEvent(TileEventType.NETWORK_WRITE)
    public boolean writeToStreamTileLegacyDisplay(ByteBuf data) {
        data.writeBoolean(this.displayPowered);
        return true;
    }

    @TileEvent(TileEventType.NETWORK_READ)
    public boolean readFromStreamTileLegacyDisplay(ByteBuf data) {
        this.displayPowered = data.readBoolean();
        this.worldObj.func_147451_t(this.xCoord, this.yCoord, this.zCoord);
        this.worldObj.markBlockRangeForRenderUpdate(
            this.xCoord, this.yCoord, this.zCoord, this.xCoord, this.yCoord, this.zCoord
        );
        return true;
    }

    @MENetworkEventSubscribe
    public void powerUpdate(final MENetworkPowerStatusChange changed) {
        this.displayPowered = this.getProxy().isPowered();
        this.worldObj.func_147451_t(this.xCoord, this.yCoord, this.zCoord);
        this.markForUpdate();
    }
}
