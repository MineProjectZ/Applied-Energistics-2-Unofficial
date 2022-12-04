package appeng.tile.legacy;

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
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

public class TileLegacyController extends AENetworkPowerTile implements ILocatable {
    private static final IInventory NULL_INVENTORY = new AppEngInternalInventory(null, 0);
    private static final int[] ACCESSIBLE_SLOTS_BY_SIDE = {};
    private static int difference = 0;
    private int ticksSinceRefresh = 0;
    private long controllerKey;
    
    public TileLegacyController() { //TODO Fix power storage
        this.setInternalMaxPower(10000);
        this.setInternalPublicPowerStorage(true);
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
                updateMeta();
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
            return this.getProxy().getEnergy().getEnergyDemand( 10000 );
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
        try
        {
            final double ret = this.getProxy().getEnergy().injectPower( power, mode );
            if( mode == Actionable.SIMULATE )
            {
                return ret;
            }
            return 0;
        } catch (final GridAccessException e) {
            // no grid? use local...
            return super.funnelPowerIntoStorage(power, mode);
        }
    }

    public void updateMeta() {
        int meta = (int
        ) Math.ceil((5.0 * this.getInternalCurrentPower() / this.getInternalMaxPower()));
        if (meta < 0) {
            meta = 0;
        } else if (meta > 5) {
            meta = 5;
        }
        if (getProxy().isActive() && meta == 0) {
            meta = 6;
        } else if (!getProxy().isActive()) {
            meta = 0;
        }
        this.worldObj.setBlockMetadataWithNotify(
            this.xCoord, this.yCoord, this.zCoord, meta, 2
        );
    }

    @Override
    public long getLocatableSerial() {
        return controllerKey;
    }

    @TileEvent(TileEventType.WORLD_NBT_WRITE)
    public void writeNBT(final NBTTagCompound data) {
        data.setLong("controllerKey", this.controllerKey);
    }

    @TileEvent(TileEventType.WORLD_NBT_READ)
    public void readNBT(final NBTTagCompound data) {
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

}
