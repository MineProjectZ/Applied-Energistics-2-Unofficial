package appeng.core.api;

import appeng.api.storage.ITerminalHost;
import net.minecraft.inventory.IInventory;

public interface ICraftingTerminal extends ITerminalHost {

    IInventory getInventoryByName(final String name);

}
