package appeng.container.implementations;

import appeng.container.AEBaseContainer;
import appeng.container.slot.SlotOutput;
import appeng.container.slot.SlotPlayerHotBar;
import appeng.container.slot.SlotPlayerInv;
import appeng.container.slot.SlotRestrictedInput;
import appeng.container.slot.SlotRestrictedInput.PlacableItemType;
import appeng.tile.legacy.TilePowerRelay;
import appeng.util.Platform;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;

public class ContainerPowerRelay extends AEBaseContainer {
    TilePowerRelay myte;

    public ContainerPowerRelay(InventoryPlayer ip, TilePowerRelay te) {
        super(ip, te);
        this.myte = te;
        this.addSlotToContainer(
            new SlotRestrictedInput(PlacableItemType.POWERED_TOOL, te, 0, 52, 53, ip)
        );
        this.addSlotToContainer(new SlotOutput(te, 1, 113, 53, -1));
        this.bindPlayerInventory(ip);
    }

    @Override
    public void onCraftMatrixChanged(IInventory par1iInventory) {
        super.onCraftMatrixChanged(par1iInventory);
        if (Platform.isServer()) {
            this.myte.markDirty();
        }
    }

    protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
        int i;
        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new SlotPlayerInv(
                    inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 115 + i * 18
                ));
            }
        }

        for (i = 0; i < 9; ++i) {
            this.addSlotToContainer(
                new SlotPlayerHotBar(inventoryPlayer, i, 8 + i * 18, 173)
            );
        }
    }
}
