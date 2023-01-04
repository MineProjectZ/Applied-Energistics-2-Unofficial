package appeng.api.networking.request;

import java.util.Set;

import appeng.api.networking.IGridCache;
import appeng.api.storage.data.IAEItemStack;

public interface IRequestGrid extends IGridCache {
    
    /**
     * @return A Set of all items, which can be requested
     */
    Set<IAEItemStack> getRequestableItems();

    /**
     * @param stack The stack of items, which should be requested
     * @return The stack of items, which coul not be requested, 
     * null if all were requested successfully
     */
    IAEItemStack requestItems(IAEItemStack stack);

}
