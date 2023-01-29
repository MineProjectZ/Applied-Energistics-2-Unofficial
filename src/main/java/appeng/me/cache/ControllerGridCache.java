package appeng.me.cache;

import java.util.HashSet;
import java.util.Set;

import appeng.api.config.Actionable;
import appeng.api.networking.IControllerCache;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridStorage;
import appeng.api.networking.crafting.ICraftingCPU;
import appeng.api.networking.energy.IAEPowerStorage;
import appeng.core.AEConfig;
import appeng.core.features.AEFeature;
import appeng.me.cluster.implementations.InternalCraftingCPU;
import appeng.tile.legacy.TileLegacyController;
import appeng.tile.networking.TileController;

public class ControllerGridCache implements IControllerCache {

    Set<TileLegacyController> controllers = new HashSet<>();
    Set<TileController> cores = new HashSet<>();

    public ControllerGridCache(final IGrid grid) {
        
    }

    @Override
    public void onUpdateTick() {}

    @Override
    public void removeNode(IGridNode gridNode, IGridHost machine) {
        if (machine instanceof TileLegacyController) {
            controllers.remove(machine);
        } else if (machine instanceof TileController) {
            cores.remove(machine);
        }
    }

    @Override
    public void addNode(IGridNode gridNode, IGridHost machine) {
        if (machine instanceof TileLegacyController) {
            controllers.add((TileLegacyController) machine);
        } else if (machine instanceof TileController) {
            cores.add((TileController) machine);
        }
    }

    @Override
    public void onSplit(IGridStorage destinationStorage) {}

    @Override
    public void onJoin(IGridStorage sourceStorage) {}

    @Override
    public void populateGridStorage(IGridStorage destinationStorage) {}

    @Override
    public boolean requiresChannels() {
        if (!AEConfig.instance.isFeatureEnabled(AEFeature.Channels)) {
            return false;
        }
        if (AEConfig.instance.HardLegacyController) {
            return controllers.isEmpty() || cores.size() < 68;
        } else {
            return controllers.isEmpty();
        }
    }

    @Override
    public boolean canRun() {
        return !hasConflict() && (!AEConfig.instance.NeedController || !controllers.isEmpty() || !cores.isEmpty());
    }

    @Override
    public boolean hasConflict() {
        return controllers.size() > 1;
    }

    @Override
    public boolean hasController() {
        return !controllers.isEmpty();
    }

    @Override
    public IGridHost getController() {
        for (TileLegacyController c : controllers) {
            return c;
        }
        return null;
    }

    @Override
    public Set<ICraftingCPU> getCPUs() {
        Set<ICraftingCPU> cpus = new HashSet<>();
        IGridHost h = getController();
        if (h instanceof TileLegacyController) {
            TileLegacyController controller = (TileLegacyController) h;
            for (InternalCraftingCPU cpu : controller.cpus) {
                cpus.add(cpu);
            }
        }
        return cpus;
    }

    @Override
    public double injectPower(double amt, Actionable mode) {
        IGridHost controller = getController();
        if (controller instanceof IAEPowerStorage) {
            return ((IAEPowerStorage)controller).injectAEPower(amt, mode);
        }
        return amt;
    }

    @Override
    public double getEnergyDemand() {
        IGridHost controller = getController();
        if (controller instanceof IAEPowerStorage) {
            return ((IAEPowerStorage)controller).getAEMaxPower() - ((IAEPowerStorage)controller).getAECurrentPower();
        }
        return 0;
    }
    
}
