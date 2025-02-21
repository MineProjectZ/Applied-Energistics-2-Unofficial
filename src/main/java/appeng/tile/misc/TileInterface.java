/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2013 - 2015, AlgorithmX2, All rights reserved.
 *
 * Applied Energistics 2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Applied Energistics 2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Applied Energistics 2.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */

package appeng.tile.misc;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.config.Upgrades;
import appeng.api.implementations.tiles.ITileStorageMonitorable;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.crafting.ICraftingProviderHelper;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.events.MENetworkChannelsChanged;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.events.MENetworkPowerStatusChange;
import appeng.api.networking.events.MENetworkRequestProviderChange;
import appeng.api.networking.request.IRequestProvider;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IStorageMonitorable;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.AECableType;
import appeng.api.util.DimensionalCoord;
import appeng.api.util.IConfigManager;
import appeng.helpers.DualityInterface;
import appeng.helpers.IInterfaceHost;
import appeng.helpers.IPriorityHost;
import appeng.integration.IntegrationRegistry;
import appeng.integration.IntegrationType;
import appeng.integration.abstraction.ILogisticsPipes;
import appeng.me.GridAccessException;
import appeng.tile.TileEvent;
import appeng.tile.events.TileEventType;
import appeng.tile.grid.AENetworkInvTile;
import appeng.tile.inventory.InvOperation;
import appeng.util.Platform;
import appeng.util.inv.IInventoryDestination;
import appeng.transformer.annotations.Integration.Interface;
import logisticspipes.api.ILogisticsPowerProvider;

import com.google.common.collect.ImmutableSet;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

@Interface(iname = IntegrationType.LogisticsPipes, iface = "logisticspipes.api.ILogisticsPowerProvider")
public class TileInterface extends AENetworkInvTile
    implements IGridTickable, ITileStorageMonitorable, IStorageMonitorable,
               IInventoryDestination, IInterfaceHost, IPriorityHost, IRequestProvider, ILogisticsPowerProvider {
    private final DualityInterface duality = new DualityInterface(this.getProxy(), this);
    private ForgeDirection pointAt = ForgeDirection.UNKNOWN;
    private ILogisticsPipes logisticsPipes = null;
    private TileEntity requestPipe = null;


    @MENetworkEventSubscribe
    public void stateChange(final MENetworkChannelsChanged c) {
        this.duality.notifyNeighbors();
    }

    @MENetworkEventSubscribe
    public void stateChange(final MENetworkPowerStatusChange c) {
        this.duality.notifyNeighbors();
    }

    public void setSide(final ForgeDirection axis) {
        if (Platform.isClient()) {
            return;
        }

        if (this.pointAt == axis.getOpposite()) {
            this.pointAt = axis;
        } else if (this.pointAt == axis || this.pointAt == axis.getOpposite()) {
            this.pointAt = ForgeDirection.UNKNOWN;
        } else if (this.pointAt == ForgeDirection.UNKNOWN) {
            this.pointAt = axis.getOpposite();
        } else {
            this.pointAt = Platform.rotateAround(this.pointAt, axis);
        }

        if (ForgeDirection.UNKNOWN == this.pointAt) {
            this.setOrientation(this.pointAt, this.pointAt);
        } else {
            this.setOrientation(
                this.pointAt.offsetY != 0 ? ForgeDirection.SOUTH : ForgeDirection.UP,
                this.pointAt.getOpposite()
            );
        }

        this.getProxy().setValidSides(EnumSet.complementOf(EnumSet.of(this.pointAt)));
        this.markForUpdate();
        this.markDirty();
    }

    @Override
    public void markDirty() {
        this.duality.markDirty();
    }

    @Override
    public void getDrops(
        final World w, final int x, final int y, final int z, final List<ItemStack> drops
    ) {
        this.duality.addDrops(drops);
    }

    @Override
    public void gridChanged() {
        this.duality.gridChanged();
    }

    @Override
    public void onReady() {
        this.getProxy().setValidSides(EnumSet.complementOf(EnumSet.of(this.pointAt)));
        super.onReady();
        this.duality.initialize();
        if (IntegrationRegistry.INSTANCE.isEnabled(IntegrationType.LogisticsPipes)) {
            logisticsPipes = (ILogisticsPipes) IntegrationRegistry.INSTANCE.getInstance(IntegrationType.LogisticsPipes);
            refreshRequestPipe();
        }
    }

    @TileEvent(TileEventType.WORLD_NBT_WRITE)
    public void writeToNBT_TileInterface(final NBTTagCompound data) {
        data.setInteger("pointAt", this.pointAt.ordinal());
        this.duality.writeToNBT(data);
    }

    @TileEvent(TileEventType.WORLD_NBT_READ)
    public void readFromNBT_TileInterface(final NBTTagCompound data) {
        final int val = data.getInteger("pointAt");

        if (val >= 0 && val < ForgeDirection.values().length) {
            this.pointAt = ForgeDirection.values()[val];
        } else {
            this.pointAt = ForgeDirection.UNKNOWN;
        }

        this.duality.readFromNBT(data);
    }

    @Override
    public AECableType getCableConnectionType(final ForgeDirection dir) {
        return this.duality.getCableConnectionType(dir);
    }

    @Override
    public DimensionalCoord getLocation() {
        return this.duality.getLocation();
    }

    @Override
    public boolean canInsert(final ItemStack stack) {
        return this.duality.canInsert(stack);
    }

    @Override
    public IMEMonitor<IAEItemStack> getItemInventory() {
        return this.duality.getItemInventory();
    }

    @Override
    public IMEMonitor<IAEFluidStack> getFluidInventory() {
        return this.duality.getFluidInventory();
    }

    @Override
    public IInventory getInventoryByName(final String name) {
        return this.duality.getInventoryByName(name);
    }

    @Override
    public TickingRequest getTickingRequest(final IGridNode node) {
        return this.duality.getTickingRequest(node);
    }

    @Override
    public TickRateModulation
    tickingRequest(final IGridNode node, final int ticksSinceLastCall) {
        return this.duality.tickingRequest(node, ticksSinceLastCall);
    }

    @Override
    public IInventory getInternalInventory() {
        return this.duality.getInternalInventory();
    }

    @Override
    public void onChangeInventory(
        final IInventory inv,
        final int slot,
        final InvOperation mc,
        final ItemStack removed,
        final ItemStack added
    ) {
        this.duality.onChangeInventory(inv, slot, mc, removed, added);
    }

    @Override
    public int[] getAccessibleSlotsBySide(final ForgeDirection side) {
        return this.duality.getAccessibleSlotsFromSide(side.ordinal());
    }

    @Override
    public DualityInterface getInterfaceDuality() {
        return this.duality;
    }

    @Override
    public EnumSet<ForgeDirection> getTargets() {
        if (this.pointAt == null || this.pointAt == ForgeDirection.UNKNOWN) {
            return EnumSet.complementOf(EnumSet.of(ForgeDirection.UNKNOWN));
        }
        return EnumSet.of(this.pointAt);
    }

    @Override
    public TileEntity getTileEntity() {
        return this;
    }

    @Override
    public IStorageMonitorable
    getMonitorable(final ForgeDirection side, final BaseActionSource src) {
        return this.duality.getMonitorable(side, src, this);
    }

    @Override
    public IConfigManager getConfigManager() {
        return this.duality.getConfigManager();
    }

    @Override
    public boolean pushPattern(
        final ICraftingPatternDetails patternDetails, final InventoryCrafting table
    ) {
        return this.duality.pushPattern(patternDetails, table);
    }

    @Override
    public boolean isBusy() {
        return this.duality.isBusy();
    }

    @Override
    public void provideCrafting(final ICraftingProviderHelper craftingTracker) {
        this.duality.provideCrafting(craftingTracker);
    }

    @Override
    public int getInstalledUpgrades(final Upgrades u) {
        return this.duality.getInstalledUpgrades(u);
    }

    @Override
    public ImmutableSet<ICraftingLink> getRequestedJobs() {
        return this.duality.getRequestedJobs();
    }

    @Override
    public IAEItemStack injectCraftedItems(
        final ICraftingLink link, final IAEItemStack items, final Actionable mode
    ) {
        return this.duality.injectCraftedItems(link, items, mode);
    }

    @Override
    public void jobStateChange(final ICraftingLink link) {
        this.duality.jobStateChange(link);
    }

    @Override
    public int getPriority() {
        return this.duality.getPriority();
    }

    @Override
    public void setPriority(final int newValue) {
        this.duality.setPriority(newValue);
    }

    public void refreshRequestPipe() {
        if (logisticsPipes != null) {
            for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                TileEntity te = worldObj.getTileEntity(this.xCoord + dir.offsetX, this.yCoord + dir.offsetY, this.zCoord + dir.offsetZ);
                if (logisticsPipes.isRequestPipe(te)) {
                    if (requestPipe != te) {
                        requestPipe = te;
                        try {
                            this.getProxy().getGrid().postEvent(new MENetworkRequestProviderChange(this));
                        } catch (GridAccessException e) {
                            // :P
                        }
                    }
                    return;
                }
            }
            if (requestPipe != null) {
                requestPipe = null;
                try {
                    this.getProxy().getGrid().postEvent(new MENetworkRequestProviderChange(this));
                } catch (GridAccessException e) {
                    // :P
                }
            }
        }
    }

    @Override
    public Set<IAEItemStack> getRequestableItems() {
        return logisticsPipes.getRequestableItems(requestPipe);
    }

    @Override
    public IAEItemStack requestStack(IAEItemStack stack, Actionable actionable) {
        if (logisticsPipes != null && requestPipe != null) {
            return logisticsPipes.requestStack(requestPipe, stack, actionable);
        }
        return stack;
    }

    @Override
    public boolean canUseEnergy(int arg0) {
        return canUseEnergy(arg0, null);
    }

    @Override
    public boolean canUseEnergy(int amount, List<Object> list) {
        if (list != null && list.contains(this)) {
            return false;
        } else {
            try {
                IEnergyGrid energy = this.getProxy().getEnergy();
                double extracted = energy.extractAEPower(amount, Actionable.SIMULATE, PowerMultiplier.ONE);
                return extracted >= amount;
            } catch (GridAccessException e) {
                return false;
            }
        }
    }

    @Override
    public int getX() {
        return this.xCoord;
    }

    @Override
    public int getY() {
        return this.yCoord;
    }

    @Override
    public int getZ() {
        return this.zCoord;
    }

    @Override
    public boolean useEnergy(int arg0) {
        return useEnergy(arg0, null);
    }

    @Override
    public boolean useEnergy(int amount, List<Object> list) {
        if (list != null && list.contains(this)) {
            return false;
        } else if (canUseEnergy(amount, list)) {
            try {
                IEnergyGrid energy = this.getProxy().getEnergy();
                energy.extractAEPower(amount, Actionable.MODULATE, PowerMultiplier.ONE);
                return true;
            } catch (GridAccessException e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public int getPowerLevel() {
        try {
            return (int) Math.min(this.getProxy().getEnergy().getStoredPower(), 2000000);
        } catch (GridAccessException e) {
            return 0;
        }
    }

    @Override
    public boolean isActive() {
        return this.requestPipe != null;
    }
}
