package appeng.api.networking;

import appeng.api.config.Actionable;
import appeng.api.storage.data.IAEItemStack;

public interface IControllerCache extends IGridCache {

    boolean requiresChannels();

    boolean canRun();

    boolean hasConflict();

    boolean hasController();

    IGridHost getController();

    boolean requestCrafting(IAEItemStack stack, Actionable actionable);
    
}
