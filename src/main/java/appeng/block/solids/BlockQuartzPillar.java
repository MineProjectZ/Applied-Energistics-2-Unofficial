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

package appeng.block.solids;

import java.util.EnumSet;

import appeng.api.util.IOrientable;
import appeng.api.util.IOrientableBlock;
import appeng.block.AEBaseBlock;
import appeng.core.features.AEFeature;
import appeng.helpers.MetaRotation;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;

public class BlockQuartzPillar extends AEBaseBlock implements IOrientableBlock {
    public BlockQuartzPillar() {
        super(Material.rock);
        this.setFeature(EnumSet.of(AEFeature.DecorativeQuartzBlocks));
    }

    @Override
    public boolean usesMetadata() {
        return true;
    }

    @Override
    public IOrientable
    getOrientable(final IBlockAccess w, final int x, final int y, final int z) {
        return new MetaRotation(w, x, y, z);
    }
}
