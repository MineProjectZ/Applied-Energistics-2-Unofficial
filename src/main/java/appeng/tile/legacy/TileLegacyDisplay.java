package appeng.tile.legacy;

import appeng.api.implementations.tiles.IColorableTile;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.events.MENetworkPowerStatusChange;
import appeng.api.util.AEColor;
import appeng.tile.TileEvent;
import appeng.tile.events.TileEventType;
import appeng.tile.grid.AENetworkTile;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public class TileLegacyDisplay extends AENetworkTile implements IColorableTile {
    private boolean displayPowered;
    protected AEColor paintedColor = AEColor.Transparent;

    public boolean isDisplayPowered() {
        return displayPowered;
    }

    @TileEvent(TileEventType.NETWORK_WRITE)
    public void writeToStreamTileLegacyDisplay(ByteBuf data) {
        data.writeBoolean(this.displayPowered);
        data.writeByte(this.paintedColor.ordinal());
    }

    @TileEvent(TileEventType.NETWORK_READ)
    public void readFromStreamTileLegacyDisplay(ByteBuf data) {
        this.displayPowered = data.readBoolean();
        this.paintedColor = AEColor.values()[data.readByte()];
        this.worldObj.func_147451_t(this.xCoord, this.yCoord, this.zCoord);
        this.worldObj.markBlockRangeForRenderUpdate(
            this.xCoord, this.yCoord, this.zCoord, this.xCoord, this.yCoord, this.zCoord
        );
    }

    @TileEvent(TileEventType.WORLD_NBT_WRITE)
    public void writeToNBTTileLegacyDisplay(NBTTagCompound nbt) {
        nbt.setByte("color", (byte)this.paintedColor.ordinal());
    }

    @TileEvent(TileEventType.WORLD_NBT_READ)
    public void readFromNBTTileLegacyDisplay(NBTTagCompound nbt) {
        if (nbt.hasKey("color")) {
            this.paintedColor = AEColor.values()[nbt.getByte("color")];
        }
    }

    @MENetworkEventSubscribe
    public void powerUpdate(final MENetworkPowerStatusChange changed) {
        this.displayPowered = this.getProxy().isPowered();
        this.worldObj.func_147451_t(this.xCoord, this.yCoord, this.zCoord);
        this.markForUpdate();
    }

    @Override
    public AEColor getColor() {
        return this.paintedColor;
    }

    @Override
    public boolean recolourBlock(ForgeDirection side, AEColor newPaintedColor, EntityPlayer who) {
        if (this.paintedColor == newPaintedColor) {
            return false;
        }

        this.paintedColor = newPaintedColor;
        this.markDirty();
        this.markForUpdate();
        return true;
    }
}
