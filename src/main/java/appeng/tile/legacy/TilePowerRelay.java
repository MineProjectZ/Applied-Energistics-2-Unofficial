package appeng.tile.legacy;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.implementations.items.IAEItemPowerStorage;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.energy.IEnergyGridProvider;
import appeng.api.util.AECableType;
import appeng.api.util.DimensionalCoord;
import appeng.me.GridAccessException;
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

public class TilePowerRelay
    extends AEBasePoweredTile implements IGridProxyable, IEnergyGridProvider {
    public static final int[] ACCESSIBLE_SLOTS_BY_SIDE = {};

    private Map<ForgeDirection, AENetworkProxy> proxies = new HashMap<>();
    private AppEngInternalInventory inv = new AppEngInternalInventory(this, 2);

    public TilePowerRelay() {
        for (ForgeDirection dir : ForgeDirection.values()) {
            AENetworkProxy proxy = new AENetworkProxy(
                this, "proxy" + dir.name().toLowerCase(), this.getItemFromTile(this), true
            );
            proxy.setFlags(GridFlags.CANNOT_CARRY);
            if (dir == ForgeDirection.UNKNOWN) {
                proxy.setValidSides(EnumSet.noneOf(ForgeDirection.class));
            } else {
                proxy.setValidSides(EnumSet.of(dir));
            }
            proxy.setIdlePowerUsage(0);
            proxies.put(dir, proxy);
        }
        this.setInternalMaxPower(8000.0);
    }

    @TileEvent(TileEventType.TICK)
    public void onTick() {
        ItemStack stack = this.inv.getStackInSlot(0);
        if (stack != null && stack.getItem() instanceof IAEItemPowerStorage) {
            if (this.getAECurrentPower() < 5.0) {
                for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                    try {
                        this.setInternalCurrentPower(
                            this.getAECurrentPower()
                            + this.getProxyForSide(dir).getEnergy().extractAEPower(
                                this.getAEMaxPower() - this.getAECurrentPower(),
                                Actionable.MODULATE,
                                PowerMultiplier.ONE
                            )
                        );
                    } catch (GridAccessException e) {
                        // :P
                    }
                }
            }

            IAEItemPowerStorage iaeips = (IAEItemPowerStorage) stack.getItem();

            if (this.inv.getStackInSlot(1) == null
                && iaeips.getAEMaxPower(stack) - iaeips.getAECurrentPower(stack)
                    < 0.001) {
                this.inv.setInventorySlotContents(1, stack);
                this.inv.setInventorySlotContents(0, null);
            } else {
                this.setInternalCurrentPower(
                    iaeips.injectAEPower(stack, this.getAECurrentPower())
                );
            }
        }

        if (this.getAECurrentPower() > 0.01) {
            this.onUpdatePower();
        }
    }

    public void onUpdatePower() {
        List<ForgeDirection> demanding = new ArrayList<>();
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            try {
                IEnergyGrid eg = this.getProxyForSide(dir).getEnergy();
                if (eg.getEnergyDemand(1000.0) > 1.0) {
                    demanding.add(dir);
                }
            } catch (GridAccessException e) {
                // :P
            }
        }
        if (demanding.isEmpty())
            return;
        final double split = this.getAECurrentPower() / demanding.size();
        double current = 0.0;
        for (ForgeDirection dir : demanding) {
            double leftover = split;
            try {
                IEnergyGrid eg = this.getProxyForSide(dir).getEnergy();
                double demand = eg.getEnergyDemand(leftover);
                leftover
                    = eg.injectPower(Math.min(leftover, demand), Actionable.MODULATE);
                current += leftover;
            } catch (GridAccessException e) {
                // :P
            }
        }
        this.setInternalCurrentPower(current);
    }

    @TileEvent(TileEventType.WORLD_NBT_READ)
    public void readFromNBT_PowerRelay(final NBTTagCompound data) {
        for (ForgeDirection dir : ForgeDirection.values()) {
            proxies.get(dir).readFromNBT(data);
        }
    }

    @TileEvent(TileEventType.WORLD_NBT_WRITE)
    public void writeToNBT_PowerRelay(final NBTTagCompound data) {
        for (ForgeDirection dir : ForgeDirection.values()) {
            proxies.get(dir).writeToNBT(data);
        }
    }

    @Override
    public IInventory getInternalInventory() {
        return this.inv;
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
    public void gridChanged() {}

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        for (ForgeDirection dir : ForgeDirection.values()) {
            proxies.get(dir).onChunkUnload();
        }
    }

    @Override
    public void onReady() {
        super.onReady();
        for (ForgeDirection dir : ForgeDirection.values()) {
            proxies.get(dir).onReady();
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        for (ForgeDirection dir : ForgeDirection.values()) {
            proxies.get(dir).invalidate();
        }
    }

    @Override
    public void validate() {
        super.validate();
        for (ForgeDirection dir : ForgeDirection.values()) {
            proxies.get(dir).validate();
        }
    }

    @Override
    public double extractAEPower(double amt, Actionable mode, Set<IEnergyGrid> seen) {
        double acquiredPower = 0;
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            try {
                final IEnergyGrid eg = this.getProxyForSide(dir).getEnergy();
                acquiredPower += eg.extractAEPower(amt - acquiredPower, mode, seen);
            } catch (final GridAccessException e) {
                // :P
            }
        }
        return acquiredPower;
    }

    @Override
    public double injectAEPower(double amt, Actionable mode, Set<IEnergyGrid> seen) {
        return amt;
    }

    @Override
    public double getEnergyDemand(double amt, Set<IEnergyGrid> seen) {
        return 0;
    }
}
