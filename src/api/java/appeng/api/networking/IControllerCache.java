package appeng.api.networking;

import java.util.Set;

import appeng.api.networking.crafting.ICraftingCPU;

public interface IControllerCache extends IGridCache {

    boolean requiresChannels();

    boolean canRun();

    boolean hasConflict();

    boolean hasController();

    IGridHost getController();

    Set<ICraftingCPU> getCPUs();
    
}
