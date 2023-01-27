package appeng.me.legacy;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;

public class CraftingJobPacket
{
    public ItemStack Target;
    public List<ItemStack> Dependencies;
    public List<ItemStack> Missing;
    
    public CraftingJobPacket() {
        this.Dependencies = new ArrayList<ItemStack>();
        this.Missing = new ArrayList<ItemStack>();
    }
}
