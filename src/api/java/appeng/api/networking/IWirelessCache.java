package appeng.api.networking;

import java.util.Set;

import appeng.api.implementations.tiles.IWirelessAccessPoint;

public interface IWirelessCache extends IGridCache {
    
    Set<IWirelessAccessPoint> getAccessPoints();

}
