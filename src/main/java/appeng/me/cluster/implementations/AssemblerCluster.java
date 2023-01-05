package appeng.me.cluster.implementations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import appeng.api.networking.IGridHost;
import appeng.api.util.WorldCoord;
import appeng.me.cluster.IAssemblerCluster;
import appeng.tile.legacy.TileAssembler;
import appeng.tile.legacy.TileAssemblerMB;
import com.google.common.collect.Iterators;
import net.minecraft.inventory.IInventory;

public class AssemblerCluster implements IAssemblerCluster {
    public WorldCoord min;
    public WorldCoord max;
    public boolean isDestroyed = false;
    public int lastPage;
    public int accelerators;
    public IInventory inv;
    public List<TileAssemblerMB> mb = new ArrayList<>();
    public List<TileAssembler> assemblers = new ArrayList<>();
    public long inst;

    public AssemblerCluster(WorldCoord _min, WorldCoord _max) {
        this.min = _min;
        this.max = _max;
    }

    public void initMaster() {
        if (this.getMaster().jobs == null
            || this.getMaster().jobs.length != this.accelerators) {
            this.getMaster().jobs = new TileAssembler.Job[this.accelerators];
        }

        for (int i = 1; i < this.assemblers.size(); i++) {
            // slaveify old masters
            this.assemblers.get(i).jobs = null;
        }
    }

    public void onOperation() {
        this.getMaster().onOperation();
    }

    public TileAssembler getAssembler(int i) {
        return this.assemblers.get(i);
    }

    public boolean canCraft() {
        return this.getMaster().canCraft();
    }

    public void destroy() {
        if (!this.isDestroyed) {
            this.isDestroyed = true;
            if (!this.mb.isEmpty()) {
                // TODO: WTF
                //TileAssemblerMB mb = this.mb.get(0);
                //mb.sendUpdate(false, (Player) null);
            }

            for (TileAssembler as : this.assemblers) {
                as.updateStatus(null);
            }

            for (TileAssemblerMB r : this.mb) {
                r.updateStatus(null);
                // TODO: WTF
                //MinecraftForge.EVENT_BUS.post(new GridTileConnectivityEvent(
                //    (IGridTileEntity) r.getTile(),
                //    ((TileAssemblerMB) r.getTile()).getWorld(),
                //    ((TileAssemblerMB) r.getTile()).getLocation()
                //));
            }
        }
    }

    public int getLastOffset() {
        return this.lastPage <= 0
            ? 0
            : (this.lastPage >= this.assemblers.size() ? 0 : this.lastPage);
    }

    public void setLastOffset(int x) {
        this.lastPage = x;
    }

    @Override
    public void updateStatus(boolean updateGrid) {
        // TODO: WTF
    }

    @Override
    public Iterator<IGridHost> getTiles() {
        return Iterators.<IGridHost>concat(
            this.mb.iterator(), this.assemblers.iterator()
        );
    }

    public TileAssembler getMaster() {
        return this.getAssembler(0);
    }
}
