package appeng.api.networking.request;

import java.util.Set;

import appeng.api.config.Actionable;
import appeng.api.storage.data.IAEItemStack;

public interface IRequestProvider {
    
    Set<IAEItemStack> getRequestableItems();

    IAEItemStack requestStack(IAEItemStack stack, Actionable actionable);

    boolean isActive();

}
