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

package appeng.items.contents;

import appeng.api.config.*;
import appeng.api.implementations.guiobjects.IPortableCell;
import appeng.api.implementations.items.IAEItemPowerStorage;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.MEMonitorHandler;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.IConfigManager;
import appeng.container.interfaces.IInventorySlotAware;
import appeng.me.storage.CellInventory;
import appeng.util.ConfigManager;
import appeng.util.IConfigManagerHost;
import appeng.util.Platform;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class PortableCellViewer
    extends MEMonitorHandler<IAEItemStack> implements IPortableCell, IInventorySlotAware {
    private final ItemStack target;
    private final IAEItemPowerStorage ips;
    private final int inventorySlot;

    public PortableCellViewer(final ItemStack is, final int slot) {
        super(CellInventory.getCell(is, null));
        this.ips = (IAEItemPowerStorage) is.getItem();
        this.target = is;
        this.inventorySlot = slot;
    }

    @Override
    public int getInventorySlot() {
        return this.inventorySlot;
    }

    @Override
    public ItemStack getItemStack() {
        return this.target;
    }

    @Override
    public double extractAEPower(
        double amt, final Actionable mode, final PowerMultiplier usePowerMultiplier
    ) {
        amt = usePowerMultiplier.multiply(amt);

        if (mode == Actionable.SIMULATE) {
            return usePowerMultiplier.divide(
                Math.min(amt, this.ips.getAECurrentPower(this.target))
            );
        }

        return usePowerMultiplier.divide(this.ips.extractAEPower(this.target, amt));
    }

    @Override
    public IMEMonitor<IAEItemStack> getItemInventory() {
        return this;
    }

    @Override
    public IMEMonitor<IAEFluidStack> getFluidInventory() {
        return null;
    }

    @Override
    public IConfigManager getConfigManager() {
        final ConfigManager out = new ConfigManager(new IConfigManagerHost() {
            @Override
            public void updateSetting(
                final IConfigManager manager, final Enum settingName, final Enum newValue
            ) {
                final NBTTagCompound data
                    = Platform.openNbtData(PortableCellViewer.this.target);
                manager.writeToNBT(data);
            }
        });

        out.registerSetting(Settings.SORT_BY, SortOrder.NAME);
        out.registerSetting(Settings.VIEW_MODE, ViewItems.ALL);
        out.registerSetting(Settings.SORT_DIRECTION, SortDir.ASCENDING);

        out.readFromNBT((NBTTagCompound) Platform.openNbtData(this.target).copy());
        return out;
    }
}
