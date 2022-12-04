package appeng.api.networking;

public interface IControllerCache extends IGridCache {

    boolean requiresChannels();

    boolean canRun();

    boolean hasConflict();
    
}
