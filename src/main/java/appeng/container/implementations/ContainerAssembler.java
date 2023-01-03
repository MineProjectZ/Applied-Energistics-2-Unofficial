package appeng.container.implementations;

import appeng.container.AEBaseContainer;
import appeng.container.slot.SlotPlayerHotBar;
import appeng.container.slot.SlotPlayerInv;
import appeng.container.slot.SlotRestrictedInput;
import appeng.container.slot.SlotRestrictedInput.PlacableItemType;
import appeng.tile.legacy.TileAssembler;
import appeng.util.inv.WrapperChainedInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;

public class ContainerAssembler extends AEBaseContainer {
    public WrapperChainedInventory myinv;

    public ContainerAssembler(InventoryPlayer ip, TileAssembler te) {
        this(ip, te.xCoord, te.yCoord, te.zCoord, te, te);
    }

    public ContainerAssembler(
        InventoryPlayer pi, int x, int y, int z, TileEntity te, IInventory inv
    ) {
        super(pi, te, null);
        int off = 0;
        this.myinv = new WrapperChainedInventory(inv);

        for (int i = 0; i < 6; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new SlotRestrictedInput(
                    PlacableItemType.ENCODED_CRAFTING_PATTERN,
                    this.myinv,
                    off++,
                    8 + j * 18,
                    i * 18 + 18,
                    pi
                ));
            }
        }

        this.bindPlayerInventory(pi);
    }

    protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
        int i;
        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new SlotPlayerInv(
                    inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 140 + i * 18
                ));
            }
        }

        for (i = 0; i < 9; ++i) {
            this.addSlotToContainer(
                new SlotPlayerHotBar(inventoryPlayer, i, 8 + i * 18, 198)
            );
        }
    }

    public boolean canInteractWith(EntityPlayer p) {
        return p.getDistanceSq(
                   (double) this.getTileEntity().xCoord + 0.5,
                   (double) this.getTileEntity().yCoord + 0.5,
                   (double) this.getTileEntity().zCoord + 0.5
               )
            <= 32.0;
    }
}
