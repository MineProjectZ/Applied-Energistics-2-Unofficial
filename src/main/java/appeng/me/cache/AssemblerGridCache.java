package appeng.me.cache;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import appeng.api.networking.IAssemblerCache;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridStorage;
import appeng.me.cluster.implementations.AssemblerCluster;

public class AssemblerGridCache implements IAssemblerCache {
    Set<AssemblerCluster> assemblers = new HashSet<>();
    int cooldown = 0;

    public AssemblerGridCache(IGrid grid) {}

    @Override
    public void onUpdateTick() {
        if (--this.cooldown > 0)
            return;
        this.cooldown = 20;


        Iterator<AssemblerCluster> it = this.assemblers.iterator();
        while (it.hasNext()) {
            AssemblerCluster ac = it.next();
            if (ac.isDestroyed) {
                it.remove();
                continue;
            }
            ac.onOperation();
        }
    }

    @Override
    public void removeNode(IGridNode gridNode, IGridHost machine) {}

    @Override
    public void addNode(IGridNode gridNode, IGridHost machine) {}

    @Override
    public void onSplit(IGridStorage destinationStorage) {}

    @Override
    public void onJoin(IGridStorage sourceStorage) {}

    @Override
    public void populateGridStorage(IGridStorage destinationStorage) {}

    public void addCluster(AssemblerCluster cluster) {
        this.assemblers.add(cluster);
    }
}
