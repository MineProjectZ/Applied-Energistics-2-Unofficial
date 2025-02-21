package appeng.container.implementations;

import appeng.tile.legacy.TileAssembler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerAssemblerMB extends ContainerAssembler {
    public ContainerAssemblerMB(InventoryPlayer ip, TileAssembler te) {
        super(ip, te);
    }

    @Override
    public boolean canInteractWith(EntityPlayer p) {
        return true;
    }
}
