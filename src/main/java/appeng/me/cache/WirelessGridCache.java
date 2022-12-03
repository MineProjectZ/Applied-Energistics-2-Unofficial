package appeng.me.cache;

import java.util.HashSet;
import java.util.Set;

import appeng.api.implementations.tiles.IWirelessAccessPoint;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridStorage;
import appeng.api.networking.IWirelessCache;

public class WirelessGridCache implements IWirelessCache {

    Set<IWirelessAccessPoint> accessPoints = new HashSet<>();

    public WirelessGridCache(final IGrid grid) {
        
    }

    @Override
    public void onUpdateTick() {}

    @Override
    public void removeNode(IGridNode gridNode, IGridHost machine) {
        if (machine instanceof IWirelessAccessPoint) {
            accessPoints.remove(machine);
        }
    }

    @Override
    public void addNode(IGridNode gridNode, IGridHost machine) {
        if (machine instanceof IWirelessAccessPoint) {
            accessPoints.add((IWirelessAccessPoint) machine);
        }
    }

    @Override
    public void onSplit(IGridStorage destinationStorage) {}

    @Override
    public void onJoin(IGridStorage sourceStorage) {}

    @Override
    public void populateGridStorage(IGridStorage destinationStorage) {}

    @Override
    public Set<IWirelessAccessPoint> getAccessPoints() {
        return new HashSet<>(accessPoints);
    }
    
}
