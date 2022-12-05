package appeng.tile.legacy;

import java.io.IOException;
import java.util.EnumSet;

import appeng.api.networking.GridFlags;
import appeng.api.storage.data.IAEItemStack;
import appeng.tile.TileEvent;
import appeng.tile.events.TileEventType;
import appeng.util.item.AEItemStack;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public class TileStorageMonitor extends TileLegacyDisplay {
    public boolean isLocked;
    public boolean upgraded;
    public IAEItemStack myItem;
    public boolean updateDisplayList;

    public TileStorageMonitor() {
        this.getProxy().setFlags(GridFlags.REQUIRE_CHANNEL);
        this.getProxy().setIdlePowerUsage(0.5);
        this.getProxy().setValidSides(EnumSet.allOf(ForgeDirection.class));
    }

    @Override
    public boolean requiresTESR() {
        return true;
    }

    @TileEvent(TileEventType.NETWORK_WRITE)
    public void writeToStreamTileStorageMonitor(ByteBuf data) {
        try {
            // TODO: this is a hack because the stupid AE2 reflection BS doesn't work
            super.writeToStreamTileLegacyDisplay(data);
            int flags = (this.isLocked ? 1 : 0) | (this.upgraded ? 0b10 : 0)
                | (this.myItem != null ? 0b100 : 0);

            data.writeByte(flags);
            if (this.myItem != null)
                ;
            this.myItem.writeToPacket(data);
        } catch (IOException kek) {
            throw new RuntimeException(kek);
        }
    }

    @TileEvent(TileEventType.NETWORK_READ)
    public void readFromStreamTileStorageMonitor(ByteBuf data) {
        try {
            // TODO: this is a hack because the stupid AE2 reflection BS doesn't work
            super.readFromStreamTileLegacyDisplay(data);
            byte flags = data.readByte();
            this.isLocked = (flags & 0b1) > 0;
            this.upgraded = (flags & 0b10) > 0;

            // 3rd flag means that there's an item set
            if ((flags & 0b100) > 0) {
                this.myItem = AEItemStack.loadItemStackFromPacket(data);
            }
        } catch (IOException kek) {
            throw new RuntimeException(kek);
        }

        this.updateDisplayList = true;
    }

    @TileEvent(TileEventType.WORLD_NBT_WRITE)
    public void writeNbt(NBTTagCompound nbt) {
        nbt.setBoolean("isLocked", this.isLocked);
        nbt.setBoolean("upgraded", this.upgraded);
        if (this.myItem != null) {
            NBTTagCompound item = new NBTTagCompound();
            this.myItem.writeToNBT(item);
            nbt.setTag("item", item);
        }
    }

    @TileEvent(TileEventType.WORLD_NBT_READ)
    public void readNbt(NBTTagCompound nbt) {
        this.isLocked = nbt.getBoolean("isLocked");
        this.upgraded = nbt.getBoolean("upgraded");
        if (nbt.hasKey("item")) {
            this.myItem = AEItemStack.loadItemStackFromNBT(nbt.getCompoundTag("item"));
        } else {
            this.myItem = null;
        }
    }
}
