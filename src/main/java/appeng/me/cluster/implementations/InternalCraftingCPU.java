package appeng.me.cluster.implementations;

import appeng.api.networking.IGrid;
import appeng.api.networking.crafting.ICraftingGrid;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.security.MachineSource;
import appeng.me.GridAccessException;
import appeng.me.cache.CraftingGridCache;
import appeng.tile.legacy.TileLegacyController;
import net.minecraft.world.World;

public class InternalCraftingCPU extends AbstractCraftingCPU {

    private TileLegacyController controller;
    private MachineSource actionSource;
    private boolean isDestroyed = false;
    private final int[] usedOps = new int[3];
    private int cpuNum;

    public InternalCraftingCPU(TileLegacyController controller, int cpuNum) {
        this.controller = controller;
        this.actionSource = new MachineSource(controller);
        this.cpuNum = cpuNum;
    }

    @Override
    public BaseActionSource getActionSource() {
        return this.actionSource;
    }

    @Override
    public long getAvailableStorage() {
        return Long.MAX_VALUE;
    }

    @Override
    public int getCoProcessors() {
        return 0;
    }

    @Override
    public String getName() {
        return "Controller #" + this.cpuNum;
    }

    public void updateCPUNum(int num) {
        this.cpuNum = num;
    }

    @Override
    public void updateCraftingLogic(IGrid grid, IEnergyGrid eg, ICraftingGrid cc) {
        if (!this.isActive()) {
            return;
        }

        if (this.myLastLink != null) {
            if (this.myLastLink.isCanceled()) {
                this.myLastLink = null;
                this.cancel();
            }
        }

        if (this.isComplete) {
            if (this.inventory.getItemList().isEmpty()) {
                return;
            }

            this.storeItems();
            return;
        }

        this.waiting = false;
        if (this.waiting || this.tasks.isEmpty()) // nothing to do here...
        {
            return;
        }

        this.remainingOperations = 1
            - (this.usedOps[0] + this.usedOps[1] + this.usedOps[2]);
        final int started = this.remainingOperations;

        if (this.remainingOperations > 0) {
            do {
                this.somethingChanged = false;
                this.executeCrafting(eg, (CraftingGridCache)cc);
            } while (this.somethingChanged && this.remainingOperations > 0);
        }
        this.usedOps[2] = this.usedOps[1];
        this.usedOps[1] = this.usedOps[0];
        this.usedOps[0] = started - this.remainingOperations;

        if (this.remainingOperations > 0 && !this.somethingChanged) {
            this.waiting = true;
        }
    }

    @Override
    public boolean isActive() {
        return this.controller.getProxy().isActive();
    }

    @Override
    public boolean isDestroyed() {
        return this.isDestroyed;
    }

    public void destroy() {
        this.isDestroyed = true;
    }

    @Override
    protected IGrid getGrid() {
        try {
            return controller.getProxy().getGrid();
        } catch (GridAccessException e) {
            return null;
        }
    }

    @Override
    protected void markDirty() {
        controller.markDirty();
        
    }

    @Override
    protected void updateCPU() {

    }

    @Override
    protected World getWorld() {
        return controller.getWorldObj();
    }
    
}
