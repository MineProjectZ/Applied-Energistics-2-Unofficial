package appeng.me.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.networking.IControllerCache;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridStorage;
import appeng.api.networking.crafting.ICraftingGrid;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.events.MENetworkPostCacheConstruction;
import appeng.api.networking.request.IRequestGrid;
import appeng.api.networking.request.IRequestProvider;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.ICellProvider;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.StorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.util.item.AEItemStack;

public class RequestGridCache implements IRequestGrid, IMEInventoryHandler<IAEItemStack>, ICellProvider {

    private IGrid grid;
    private IStorageGrid storageGrid;
    private IControllerCache controllerGrid;
    private Map<AEItemStack, Set<IRequestProvider>> requestable = new HashMap<>();
    private Set<IRequestProvider> requestProviders = new HashSet<>();

    public RequestGridCache(IGrid grid) {
        this.grid = grid;
    }

    @Override
    public void onUpdateTick() {
        
    }

    @Override
    public void removeNode(IGridNode gridNode, IGridHost machine) {
        if (machine instanceof IRequestProvider) {
            requestProviders.remove(machine);
        }
    }

    @Override
    public void addNode(IGridNode gridNode, IGridHost machine) {
        if (machine instanceof IRequestProvider) {
            requestProviders.add((IRequestProvider)machine);
        }
    }

    @Override
    public void onSplit(IGridStorage destinationStorage) {
        
    }

    @Override
    public void onJoin(IGridStorage sourceStorage) {
        
    }

    @Override
    public void populateGridStorage(IGridStorage destinationStorage) {
        
    }

    @Override
    public Set<IAEItemStack> getRequestableItems() {
        Set<IAEItemStack> list = new HashSet<>();
        for(AEItemStack stack : requestable.keySet()) {
            list.add(stack.copy());
        }
        return list;
    }

    @Override
    public IAEItemStack requestItems(IAEItemStack stack) {
        // TODO: Implement request mechanism
        return stack;
    }

    @Override
    public IAEItemStack injectItems(IAEItemStack input, Actionable type, BaseActionSource src) {
        return input;
    }

    @Override
    public IAEItemStack extractItems(IAEItemStack request, Actionable mode, BaseActionSource src) {
        return null;
    }

    @Override
    public IItemList<IAEItemStack> getAvailableItems(IItemList<IAEItemStack> out) {
        Set<AEItemStack> items = requestable.keySet();
        for (IAEItemStack s : items) {
            IAEItemStack stack = s.copy();
            stack.reset();
            stack.setCountRequestable(1); // TODO: use a value, that makes sense
            out.add(stack);
        }
        return out;
    }

    @Override
    public StorageChannel getChannel() {
        return StorageChannel.ITEMS;
    }

    @Override
    public AccessRestriction getAccess() {
        return AccessRestriction.WRITE;
    }

    @Override
    public boolean isPrioritized(IAEItemStack input) {
        return true;
    }

    @Override
    public boolean canAccept(IAEItemStack input) {
        return false;
    }

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getSlot() {
        return 0;
    }

    @Override
    public boolean validForPass(int i) {
        return i == 1;
    }

    @MENetworkEventSubscribe
    public void afterCacheConstruction(final MENetworkPostCacheConstruction cacheConstruction) {
        this.storageGrid = this.grid.getCache(IStorageGrid.class);
        this.controllerGrid = this.grid.getCache(IControllerCache.class);
        this.storageGrid.registerCellProvider(this);
    }

    @Override
    public List<IMEInventoryHandler> getCellArray(StorageChannel channel) {
        final List<IMEInventoryHandler> list = new ArrayList<>(1);

        if (channel == StorageChannel.ITEMS) {
            list.add(this);
        }

        return list;
    }

    public boolean useLegacyCrafting() {
        ICraftingGrid craftingGrid = grid.getCache(ICraftingGrid.class);
        return craftingGrid.getCpus().isEmpty() && controllerGrid.hasController();
    }
    
}
