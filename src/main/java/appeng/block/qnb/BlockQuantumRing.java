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

package appeng.block.qnb;

import java.util.Collections;
import java.util.List;

import appeng.tile.qnb.TileQuantumBridge;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class BlockQuantumRing extends BlockQuantumBase {
    public BlockQuantumRing() {
        super(Material.iron);
    }

    @Override
    public Iterable<AxisAlignedBB> getSelectedBoundingBoxesFromPool(
        final World w,
        final int x,
        final int y,
        final int z,
        final Entity e,
        final boolean isVisual
    ) {
        double onePixel = 2.0 / 16.0;
        final TileQuantumBridge bridge = this.getTileEntity(w, x, y, z);
        if (bridge != null && bridge.isCorner()) {
            onePixel = 4.0 / 16.0;
        } else if (bridge != null && bridge.isFormed()) {
            onePixel = 1.0 / 16.0;
        }
        return Collections.singletonList(AxisAlignedBB.getBoundingBox(
            onePixel, onePixel, onePixel, 1.0 - onePixel, 1.0 - onePixel, 1.0 - onePixel
        ));
    }

    @Override
    public void addCollidingBlockToList(
        final World w,
        final int x,
        final int y,
        final int z,
        final AxisAlignedBB bb,
        final List<AxisAlignedBB> out,
        final Entity e
    ) {
        double onePixel = 2.0 / 16.0;
        final TileQuantumBridge bridge = this.getTileEntity(w, x, y, z);
        if (bridge != null && bridge.isCorner()) {
            onePixel = 4.0 / 16.0;
        } else if (bridge != null && bridge.isFormed()) {
            onePixel = 1.0 / 16.0;
        }
        out.add(AxisAlignedBB.getBoundingBox(
            onePixel, onePixel, onePixel, 1.0 - onePixel, 1.0 - onePixel, 1.0 - onePixel
        ));
    }
}
