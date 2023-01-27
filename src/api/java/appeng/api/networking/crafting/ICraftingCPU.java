/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 AlgorithmX2
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package appeng.api.networking.crafting;

import appeng.api.config.Actionable;
import appeng.api.networking.IGrid;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.storage.IBaseMonitor;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;

public interface ICraftingCPU extends IBaseMonitor<IAEItemStack> {
    /**
     * @return true if the CPU currently has a job.
     */
    boolean isBusy();

    /**
     * @return the action source for the CPU.
     */
    BaseActionSource getActionSource();

    /**
     * @return the available storage in bytes
     */
    long getAvailableStorage();

    /**
     * @return the number of co-processors in the CPU.
     */
    int getCoProcessors();

    /**
     * @return an empty string or the name of the cpu.
     */
    String getName();

    void getListOfItem(final IItemList<IAEItemStack> list, final CraftingItemList whichList);

    void cancel();

    IAEItemStack getItemStack(final IAEItemStack what, final CraftingItemList storage2);

    long getElapsedTime();

    long getRemainingItemCount();

    long getStartItemCount();

    void addCrafting(final ICraftingPatternDetails details, final long crafts);

    void addStorage(final IAEItemStack extractItems);

    void addEmitable(final IAEItemStack i);

    void updateCraftingLogic(final IGrid grid, final IEnergyGrid eg, final ICraftingGrid cc);

    ICraftingLink getLastCraftingLink();

    boolean canAccept(final IAEStack<?> input);

    IAEStack<?> injectItems(final IAEStack<?> input, final Actionable type, final BaseActionSource src);

    ICraftingLink submitJob(final IGrid g, final ICraftingJob job, final BaseActionSource src, final ICraftingRequester requestingMachine);

    boolean isMaking(final IAEItemStack what);

    boolean isActive();

    boolean isDestroyed();
}
