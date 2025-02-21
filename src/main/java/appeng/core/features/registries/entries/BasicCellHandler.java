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

package appeng.core.features.registries.entries;

import appeng.api.implementations.tiles.IChestOrDrive;
import appeng.api.storage.*;
import appeng.client.texture.ExtraBlockTextures;
import appeng.core.sync.GuiBridge;
import appeng.me.storage.CellInventory;
import appeng.me.storage.CellInventoryHandler;
import appeng.util.Platform;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

public class BasicCellHandler implements ICellHandler {
    @Override
    public boolean isCell(final ItemStack is) {
        return CellInventory.isCell(is);
    }

    @Override
    public IMEInventoryHandler getCellInventory(
        final ItemStack is, final ISaveProvider container, final StorageChannel channel
    ) {
        if (channel == StorageChannel.ITEMS) {
            return CellInventory.getCell(is, container);
        }
        return null;
    }

    @Override
    public IIcon getTopTexture_Light() {
        return ExtraBlockTextures.BlockMEChestItems_Light.getIcon();
    }

    @Override
    public IIcon getTopTexture_Medium() {
        return ExtraBlockTextures.BlockMEChestItems_Medium.getIcon();
    }

    @Override
    public IIcon getTopTexture_Dark() {
        return ExtraBlockTextures.BlockMEChestItems_Dark.getIcon();
    }

    @Override
    public void openChestGui(
        final EntityPlayer player,
        final IChestOrDrive chest,
        final ICellHandler cellHandler,
        final IMEInventoryHandler inv,
        final ItemStack is,
        final StorageChannel chan
    ) {
        Platform.openGUI(player, (TileEntity) chest, chest.getUp(), GuiBridge.GUI_ME);
    }

    @Override
    public int getStatusForCell(final ItemStack is, final IMEInventory handler) {
        if (handler instanceof CellInventoryHandler) {
            final CellInventoryHandler ci = (CellInventoryHandler) handler;
            return ci.getStatusForCell();
        }
        return 0;
    }

    @Override
    public double cellIdleDrain(final ItemStack is, final IMEInventory handler) {
        final ICellInventory inv = ((ICellInventoryHandler) handler).getCellInv();
        return inv.getIdleDrain();
    }
}
