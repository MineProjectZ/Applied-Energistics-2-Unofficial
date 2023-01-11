package appeng.tile.legacy;

import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.events.LocatableEventAnnounce;
import appeng.api.events.LocatableEventAnnounce.LocatableEvent;
import appeng.api.features.ILocatable;
import appeng.me.GridAccessException;
import appeng.tile.TileEvent;
import appeng.tile.events.TileEventType;
import appeng.tile.grid.AENetworkPowerTile;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.tile.inventory.InvOperation;
import appeng.util.Platform;
import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

public class TileLegacyController extends AENetworkPowerTile implements ILocatable {
    public static final IInventory NULL_INVENTORY = new AppEngInternalInventory(null, 0);
    public static final int[] ACCESSIBLE_SLOTS_BY_SIDE = {};
    public static int difference = 0;
    public int ticksSinceRefresh = 0;
    public long controllerKey;
    public int powerLevel;
    public int lastPowerLevel;

    public TileLegacyController() {
        this.setInternalMaxPower(10000);
        this.setInternalPublicPowerStorage(true);
        this.setInternalPowerFlow(AccessRestriction.READ_WRITE);
        this.getProxy().setIdlePowerUsage(6.0);
        difference++;
        this.controllerKey = System.currentTimeMillis() * 10 + difference;
        if (difference > 10) {
            difference = 0;
        }
    }

    @TileEvent(TileEventType.TICK)
    public void onTick() {
        if (!this.worldObj.isRemote) {
            ticksSinceRefresh++;
            if (ticksSinceRefresh % 20 == 0) {
                ticksSinceRefresh = 0;
                updatePowerLevel();
            }
        }
    }

    @Override
    public IInventory getInternalInventory() {
        return NULL_INVENTORY;
    }

    @Override
    public void onChangeInventory(
        IInventory inv, int slot, InvOperation mc, ItemStack removed, ItemStack added
    ) {}

    @Override
    public int[] getAccessibleSlotsBySide(ForgeDirection whichSide) {
        return ACCESSIBLE_SLOTS_BY_SIDE;
    }

    @Override
    protected double getFunnelPowerDemand( final double maxReceived )
    {
        try
        {
            return this.getProxy().getEnergy().getEnergyDemand( 10000 ) + super.getFunnelPowerDemand(maxReceived);
        }
        catch( final GridAccessException e )
        {
            // no grid? use local...
            return super.getFunnelPowerDemand(maxReceived);
        }
    }

    @Override
    protected double funnelPowerIntoStorage( final double power, final Actionable mode )
    {
        double ret = this.injectAEPower(power, mode);
        if (ret > 0) {
            try
            {
                ret = this.getProxy().getEnergy().injectPower( ret, mode );
            } catch (final GridAccessException e) {
                // :P
            }
        }
        return ret;
    }

    public void updatePowerLevel() {
        this.powerLevel = (int
        ) Math.ceil((5.0 * this.getInternalCurrentPower() / this.getInternalMaxPower()));
        if (this.powerLevel < 0) {
            this.powerLevel = 0;
        } else if (this.powerLevel > 5) {
            this.powerLevel = 5;
        }
        if (getProxy().isActive() && this.powerLevel == 0) {
            this.powerLevel = 6;
        } else if (!getProxy().isActive()) {
            this.powerLevel = 0;
        }

        if (this.powerLevel != this.lastPowerLevel) {
            this.markForUpdate();
            this.lastPowerLevel = this.powerLevel;
        }
    }

    @Override
    public long getLocatableSerial() {
        return controllerKey;
    }

    @TileEvent(TileEventType.WORLD_NBT_WRITE)
    public void writeNBTTileLegacyController(final NBTTagCompound data) {
        data.setLong("controllerKey", this.controllerKey);
    }

    @TileEvent(TileEventType.WORLD_NBT_READ)
    public void readNBTTileLegacyController(final NBTTagCompound data) {
        if (data.hasKey("controllerKey")) {
            this.controllerKey = data.getLong("controllerKey");
        }
    }

    @Override
    public void onReady() {
        super.onReady();
        if (Platform.isServer()) {
            MinecraftForge.EVENT_BUS.post(
                new LocatableEventAnnounce(this, LocatableEvent.Register)
            );
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        MinecraftForge.EVENT_BUS.post(
            new LocatableEventAnnounce(this, LocatableEvent.Unregister)
        );
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        MinecraftForge.EVENT_BUS.post(
            new LocatableEventAnnounce(this, LocatableEvent.Unregister)
        );
    }

    @TileEvent(TileEventType.NETWORK_WRITE)
    public void writeToStreamTileLegacyController(ByteBuf buf) {
        buf.writeByte(this.powerLevel);
    }

    @TileEvent(TileEventType.NETWORK_READ)
    public void readFromStreamTileLegacyController(ByteBuf buf) {
        this.powerLevel = buf.readByte();
        this.worldObj.markBlockRangeForRenderUpdate(
            this.xCoord, this.yCoord, this.zCoord, this.xCoord, this.yCoord, this.zCoord
        );
    }
}
