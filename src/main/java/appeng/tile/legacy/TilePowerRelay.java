package appeng.tile.legacy;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.util.AECableType;
import appeng.api.util.DimensionalCoord;
import appeng.me.helpers.AENetworkProxy;
import appeng.me.helpers.IGridProxyable;
import appeng.tile.TileEvent;
import appeng.tile.events.TileEventType;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.tile.inventory.InvOperation;
import appeng.tile.powersink.AEBasePoweredTile;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public class TilePowerRelay extends AEBasePoweredTile implements IGridProxyable {
    public static final IInventory NULL_INVENTORY = new AppEngInternalInventory(null, 0);
    public static final int[] ACCESSIBLE_SLOTS_BY_SIDE = {};
    private Map<ForgeDirection, AENetworkProxy> proxies = new HashMap<>();

    public TilePowerRelay() {
        for(ForgeDirection dir : ForgeDirection.values()) {
            AENetworkProxy proxy = new AENetworkProxy(this, "proxy" + dir.name().toLowerCase(), this.getItemFromTile(this), true);
            proxy.setFlags(GridFlags.CANNOT_CARRY);
            if (dir == ForgeDirection.UNKNOWN) {
                proxy.setValidSides(EnumSet.noneOf(ForgeDirection.class));
            } else {
                proxy.setValidSides(EnumSet.of(dir));
            }
            proxies.put(dir, proxy);
        }
    }

    @TileEvent(TileEventType.WORLD_NBT_READ)
    public void readFromNBT_PowerRelay(final NBTTagCompound data) {
        for(ForgeDirection dir : ForgeDirection.values()) {
            proxies.get(dir).readFromNBT(data);
        }
    }

    @TileEvent(TileEventType.WORLD_NBT_WRITE)
    public void writeToNBT_PowerRelay(final NBTTagCompound data) {
        for(ForgeDirection dir : ForgeDirection.values()) {
            proxies.get(dir).writeToNBT(data);
        }
    }

    @Override
    public IInventory getInternalInventory() {
        return NULL_INVENTORY;
    }

    @Override
    public void onChangeInventory(IInventory inv, int slot, InvOperation mc, ItemStack removed, ItemStack added) {
        
    }

    @Override
    public int[] getAccessibleSlotsBySide(ForgeDirection whichSide) {
        return ACCESSIBLE_SLOTS_BY_SIDE;
    }

    @Override
    public IGridNode getGridNode(ForgeDirection dir) {
        return proxies.get(dir).getNode();
    }

    @Override
    public AECableType getCableConnectionType(ForgeDirection dir) {
        return AECableType.COVERED;
    }

    @Override
    public AENetworkProxy getProxy() {
        return proxies.get(ForgeDirection.UNKNOWN);
    }

    public AENetworkProxy getProxyForSide(ForgeDirection dir) {
        return proxies.get(dir);
    }

    @Override
    public DimensionalCoord getLocation() {
        return new DimensionalCoord(this);
    }

    @Override
    public void gridChanged() {
        
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        for(ForgeDirection dir : ForgeDirection.values()) {
            proxies.get(dir).onChunkUnload();
        }
    }

    @Override
    public void onReady() {
        super.onReady();
        for(ForgeDirection dir : ForgeDirection.values()) {
            proxies.get(dir).onReady();
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        for(ForgeDirection dir : ForgeDirection.values()) {
            proxies.get(dir).invalidate();
        }
    }

    @Override
    public void validate() {
        super.validate();
        for(ForgeDirection dir : ForgeDirection.values()) {
            proxies.get(dir).validate();
        }
    }
    
}
