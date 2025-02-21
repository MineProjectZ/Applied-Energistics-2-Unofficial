/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2013 - 2014, AlgorithmX2, All rights reserved.
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

package appeng.items.storage;

import java.util.EnumSet;

import appeng.api.AEApi;
import appeng.api.config.FuzzyMode;
import appeng.api.config.Upgrades;
import appeng.api.implementations.items.IUpgradeModule;
import appeng.api.storage.ICellWorkbenchItem;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.core.features.AEFeature;
import appeng.items.AEBaseItem;
import appeng.items.contents.CellConfig;
import appeng.items.contents.CellUpgrades;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import appeng.util.prioitylist.FuzzyPriorityList;
import appeng.util.prioitylist.IPartitionList;
import appeng.util.prioitylist.MergedPriorityList;
import appeng.util.prioitylist.PrecisePriorityList;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class ItemViewCell extends AEBaseItem implements ICellWorkbenchItem {
    public ItemViewCell() {
        this.setFeature(EnumSet.of(AEFeature.Core));
        this.setMaxStackSize(1);
    }

    public static IPartitionList<IAEItemStack> createFilter(final ItemStack[] list) {
        IPartitionList<IAEItemStack> myPartitionList = null;

        final MergedPriorityList<IAEItemStack> myMergedList
            = new MergedPriorityList<IAEItemStack>();

        for (final ItemStack currentViewCell : list) {
            if (currentViewCell == null) {
                continue;
            }

            if ((currentViewCell.getItem() instanceof ItemViewCell)) {
                final IItemList<IAEItemStack> priorityList
                    = AEApi.instance().storage().createItemList();

                final ICellWorkbenchItem vc
                    = (ICellWorkbenchItem) currentViewCell.getItem();
                final IInventory upgrades = vc.getUpgradesInventory(currentViewCell);
                final IInventory config = vc.getConfigInventory(currentViewCell);
                final FuzzyMode fzMode = vc.getFuzzyMode(currentViewCell);

                boolean hasInverter = false;
                boolean hasFuzzy = false;

                for (int x = 0; x < upgrades.getSizeInventory(); x++) {
                    final ItemStack is = upgrades.getStackInSlot(x);
                    if (is != null && is.getItem() instanceof IUpgradeModule) {
                        final Upgrades u = ((IUpgradeModule) is.getItem()).getType(is);
                        if (u != null) {
                            switch (u) {
                                case FUZZY:
                                    hasFuzzy = true;
                                    break;
                                case INVERTER:
                                    hasInverter = true;
                                    break;
                                default:
                            }
                        }
                    }
                }

                for (int x = 0; x < config.getSizeInventory(); x++) {
                    final ItemStack is = config.getStackInSlot(x);
                    if (is != null) {
                        priorityList.add(AEItemStack.create(is));
                    }
                }

                if (!priorityList.isEmpty()) {
                    if (hasFuzzy) {
                        myMergedList.addNewList(
                            new FuzzyPriorityList<IAEItemStack>(priorityList, fzMode),
                            !hasInverter
                        );
                    } else {
                        myMergedList.addNewList(
                            new PrecisePriorityList<IAEItemStack>(priorityList),
                            !hasInverter
                        );
                    }

                    myPartitionList = myMergedList;
                }
            }
        }

        return myPartitionList;
    }

    @Override
    public boolean isEditable(final ItemStack is) {
        return true;
    }

    @Override
    public IInventory getUpgradesInventory(final ItemStack is) {
        return new CellUpgrades(is, 2);
    }

    @Override
    public IInventory getConfigInventory(final ItemStack is) {
        return new CellConfig(is);
    }

    @Override
    public FuzzyMode getFuzzyMode(final ItemStack is) {
        final String fz = Platform.openNbtData(is).getString("FuzzyMode");
        try {
            return FuzzyMode.valueOf(fz);
        } catch (final Throwable t) {
            return FuzzyMode.IGNORE_ALL;
        }
    }

    @Override
    public void setFuzzyMode(final ItemStack is, final FuzzyMode fzMode) {
        Platform.openNbtData(is).setString("FuzzyMode", fzMode.name());
    }
}
