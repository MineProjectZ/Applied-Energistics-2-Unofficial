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

package appeng.integration.modules;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import appeng.api.config.Actionable;
import appeng.api.storage.data.IAEItemStack;
import appeng.helpers.Reflected;
import appeng.integration.IIntegrationModule;
import appeng.integration.IntegrationHelper;
import appeng.integration.abstraction.ILogisticsPipes;
import appeng.util.item.AEItemStack;
import logisticspipes.api.ILPPipeTile;
import logisticspipes.api.IRequestAPI;
import logisticspipes.api.IRequestAPI.SimulationResult;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

/**
 * @author Second_Fry
 * @version rv3 - 12.06.2015
 * @since rv3 12.06.2015
 */
@Reflected
public class LogisticsPipes implements ILogisticsPipes, IIntegrationModule {
    @Reflected
    public static LogisticsPipes instance;

    @Reflected
    public LogisticsPipes() {
        IntegrationHelper.testClassExistence(this, logisticspipes.api.ILPPipe.class);
        IntegrationHelper.testClassExistence(this, logisticspipes.api.ILPPipeTile.class);
        IntegrationHelper.testClassExistence(this, logisticspipes.api.IRequestAPI.class);
        IntegrationHelper
            .testClassExistence(this, logisticspipes.pipes.basic.CoreRoutedPipe.class);
        IntegrationHelper
            .testClassExistence(this, logisticspipes.proxy.SimpleServiceLocator.class);
        IntegrationHelper
            .testClassExistence(this, logisticspipes.routing.ExitRoute.class);
        IntegrationHelper
            .testClassExistence(this, logisticspipes.utils.AdjacentTile.class);
        IntegrationHelper
            .testClassExistence(this, logisticspipes.utils.item.ItemIdentifier.class);
    }

    @Override
    public void init() throws Throwable {}

    @Override
    public void postInit() {

    }

    @Override
    public boolean isRequestPipe(TileEntity te) {
        return te instanceof ILPPipeTile && ((ILPPipeTile)te).getLPPipe() instanceof IRequestAPI;
    }

    @Override
    public Set<IAEItemStack> getRequestableItems(TileEntity te) {
        if (isRequestPipe(te)) {
            IRequestAPI api = (IRequestAPI) ((ILPPipeTile)te).getLPPipe();
            Map<IAEItemStack, IAEItemStack> stacks = new HashMap<>();
            for(ItemStack s : api.getProvidedItems()) {
                IAEItemStack stack = AEItemStack.create(s);
                int stacksize = s.stackSize;
                stack.reset();
                stack.setCountRequestable(stacksize);
                if (stacks.containsKey(stack)) {
                    stacks.get(stack).incCountRequestable(stack.getCountRequestable());
                } else {
                    stacks.put(stack, stack);
                }
            }
            for(ItemStack s : api.getCraftedItems()) {
                IAEItemStack stack = AEItemStack.create(s);
                stack.reset();
                if (stacks.containsKey(stack)) {
                    stacks.get(stack).setCraftable(true);
                } else {
                    stack.setCraftable(true);
                    stacks.put(stack, stack);
                }
            }
            return stacks.keySet();
        }
        return new HashSet<>();
    }

    @Override
    public IAEItemStack requestStack(TileEntity te, IAEItemStack request, Actionable actionable) {
        if (isRequestPipe(te) && request.getStackSize() <= Integer.MAX_VALUE) {
            IRequestAPI api = (IRequestAPI) ((ILPPipeTile)te).getLPPipe();
            if (actionable == Actionable.SIMULATE) {
                SimulationResult res = api.simulateRequest(request.getItemStack());
                if (res.missing.isEmpty()) {
                    return null;
                } else {
                    ItemStack m = res.missing.get(0);
                    if (request.equals(m)) {
                        return AEItemStack.create(res.missing.get(0));
                    } else {
                        return request;
                    }
                }
            } else {
                List<ItemStack> returned = api.performRequest(request.getItemStack());
                if (returned.isEmpty()) {
                    return null;
                } else {
                    ItemStack m = returned.get(0);
                    int missing = m.stackSize;
                    if (missing > 0 && request.equals(m)) {
                        IAEItemStack newRequest = request.copy();
                        newRequest.decStackSize(missing);
                        // LP should still request the items, which are available
                        api.performRequest(newRequest.getItemStack());
                        IAEItemStack leftover = request.copy();
                        leftover.setStackSize(missing);
                        return leftover;
                    }
                }
            }
        }
        return request;
    }
}