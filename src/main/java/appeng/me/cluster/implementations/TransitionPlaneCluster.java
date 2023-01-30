package appeng.me.cluster.implementations;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import appeng.api.networking.IGridHost;
import appeng.api.util.WorldCoord;
import appeng.me.cluster.IAECluster;
import appeng.tile.legacy.TileTransitionPlane;

public class TransitionPlaneCluster implements IAECluster {
    public WorldCoord min;
    public WorldCoord max;

    public Set<TileTransitionPlane> tiles = new HashSet<>();

    public boolean isDestroyed = false;

    public TransitionPlaneCluster(WorldCoord min, WorldCoord max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public void updateStatus(boolean updateGrid) {}

    @Override
    public void destroy() {
        if (this.isDestroyed)
            return;

        this.isDestroyed = true;

        for (TileTransitionPlane pl : this.tiles) {
            pl.updateStatus(null);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator<IGridHost> getTiles() {
        return (Iterator<IGridHost>)(Object)this.tiles.iterator();
    }
}
