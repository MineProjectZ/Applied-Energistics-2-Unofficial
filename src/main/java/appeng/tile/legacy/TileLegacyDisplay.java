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
    public void writeToStreamTileLegacyTerminal(ByteBuf data) {
        data.writeBoolean(this.displayPowered);
    }

    @TileEvent(TileEventType.NETWORK_READ)
    public void readFromStreamTileLegacyTerminal(ByteBuf data) {
        this.displayPowered = data.readBoolean();
        this.worldObj.func_147451_t(this.xCoord, this.yCoord, this.zCoord);
    }

    @MENetworkEventSubscribe
    public void powerUpdate(final MENetworkPowerStatusChange changed) {
        this.displayPowered = this.getProxy().isPowered();
        this.worldObj.func_147451_t(this.xCoord, this.yCoord, this.zCoord);
        this.markForUpdate();
    }
}
