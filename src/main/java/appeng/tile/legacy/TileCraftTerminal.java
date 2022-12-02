package appeng.tile.legacy;

import appeng.core.api.ICraftingTerminal;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.tile.inventory.IAEAppEngInventory;
import appeng.tile.inventory.InvOperation;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileCraftTerminal
    extends TileTerminal implements ICraftingTerminal, IAEAppEngInventory {
    private final AppEngInternalInventory craftingGrid
        = new AppEngInternalInventory(this, 9);

    @Override
    public IInventory getInventoryByName(String name) {
        if (name.equals("crafting")) {
            return this.craftingGrid;
        }
        return null;
    }

    @Override
    public void onChangeInventory(
        IInventory inv,
        int slot,
        InvOperation mc,
        ItemStack removedStack,
        ItemStack newStack
    ) {
        this.markDirty();
    }

    @Override
    public void readFromNBT(final NBTTagCompound data) {
        super.readFromNBT(data);
        this.craftingGrid.readFromNBT(data, "craftingGrid");
    }

    @Override
    public void writeToNBT(final NBTTagCompound data) {
        super.writeToNBT(data);
        this.craftingGrid.writeToNBT(data, "craftingGrid");
    }
}
