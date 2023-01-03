package appeng.me.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import appeng.api.networking.IAssemblerCache;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridStorage;
import appeng.me.cluster.IAssemblerMB;
import appeng.me.cluster.implementations.AssemblerCluster;

public class AssemblerGridCache implements IAssemblerCache {
    // This needs to be a ConcurrentHashMap to prevent errors when a cluster is
    // invalidated while another is added.
    //
    // Java has no ConcurrentHashSet
    Map<AssemblerCluster, Object> assemblers = new ConcurrentHashMap<>();
    static Object PRESENT = new Object();

    int cooldown = 0;

    public AssemblerGridCache(IGrid grid) {}

    @Override
    public void onUpdateTick() {
        if (--this.cooldown > 0)
            return;
        this.cooldown = 20;

        for (AssemblerCluster ac : assemblers.keySet()) {
            if (ac.isDestroyed) {
                assemblers.remove(ac);
                continue;
            }
            ac.onOperation();
        }
    }

    @Override
    public void removeNode(IGridNode gridNode, IGridHost machine) {
        if (machine instanceof IAssemblerMB) {
            AssemblerCluster cluster
                = (AssemblerCluster) ((IAssemblerMB) machine).getCluster();

            if (cluster != null) {
                // We remove the cluster of the machine here, because we need to remove
                // old clusters on network split.
                this.assemblers.remove(cluster);
            }
        }
    }

    @Override
    public void addNode(IGridNode gridNode, IGridHost machine) {
        if (machine instanceof IAssemblerMB) {
            AssemblerCluster cluster
                = (AssemblerCluster) ((IAssemblerMB) machine).getCluster();

            if (cluster != null) {
                // Although a forming assembler multiblock will add its own cluster
                // itself, we still need to do it here too for network joins.
                this.addCluster(cluster);
            }
        }
    }

    @Override
    public void onSplit(IGridStorage destinationStorage) {}

    @Override
    public void onJoin(IGridStorage sourceStorage) {}

    @Override
    public void populateGridStorage(IGridStorage destinationStorage) {}

    public void addCluster(AssemblerCluster cluster) {
        this.assemblers.put(cluster, PRESENT);
    }
}
