package appeng.me.cluster.implementations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import appeng.api.config.Actionable;
import appeng.api.networking.IGridHost;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.security.MachineSource;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.WorldCoord;
import appeng.me.GridAccessException;
import appeng.me.cluster.IAssemblerCluster;
import appeng.tile.legacy.TileAssembler;
import appeng.tile.legacy.TileAssemblerMB;
import appeng.util.item.AEItemStack;
import com.google.common.collect.Iterators;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;

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
    public Job[] jobs;

    public AssemblerCluster(WorldCoord _min, WorldCoord _max) {
        this.min = _min;
        this.max = _max;
    }

    public void onOperation() {
        for (int i = 0; i < this.jobs.length; i++) {
            if (this.jobs[i] == null)
                continue;

            ItemStack out = this.jobs[i].det.getOutput(
                this.jobs[i].inv, this.assemblers.get(0).getWorldObj()
            );

            if (out != null) {
                try {
                    IMEMonitor<IAEItemStack> inv
                        = this.assemblers.get(0).getProxy().getStorage().getItemInventory(
                        );

                    inv.injectItems(
                        AEItemStack.create(out),
                        Actionable.MODULATE,
                        new MachineSource(this.assemblers.get(0))
                    );

                } catch (GridAccessException kek) {}
            }

            this.jobs[i] = null;
        }
    }

    public TileAssembler getAssembler(int i) {
        return this.assemblers.get(i);
    }

    public boolean addCraft(Job job) {
        for (int i = 0; i < this.jobs.length; i++) {
            if (this.jobs[i] == null) {
                this.jobs[i] = job;
                return true;
            }
        }

        return false;
    }

    public boolean canCraft() {
        for (Job j : this.jobs)
            if (j == null)
                return true;

        return false;
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

    public static class Job {
        public ICraftingPatternDetails det;
        public InventoryCrafting inv;

        public Job(ICraftingPatternDetails det, InventoryCrafting inv) {
            this.det = det;
            this.inv = inv;
        }
    }
}
