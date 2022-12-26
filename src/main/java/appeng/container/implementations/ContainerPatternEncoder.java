package appeng.container.implementations;

import appeng.container.AEBaseContainer;
import appeng.container.slot.SlotFake;
import appeng.container.slot.SlotPlayerHotBar;
import appeng.container.slot.SlotPlayerInv;
import appeng.container.slot.SlotRestrictedInput;
import appeng.container.slot.SlotRestrictedInput.PlacableItemType;
import appeng.tile.legacy.TilePatternEncoder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerPatternEncoder extends AEBaseContainer {
    public TilePatternEncoder patEnc;

    // TODO: WTF
    //public InventoryPaternEnc PatternEnc;

    // TODO: WTF
    //@Override
    public void updateClient() {
        this.patEnc.lockChanges = true;
        this.patEnc.lockChanges = false;
        //super.updateClient();
    }

    public ContainerPatternEncoder(
        InventoryPlayer inventoryPlayer, TilePatternEncoder te
    ) {
        super(inventoryPlayer, te, null);
        this.patEnc = te;
        //this.PatternEnc = new InventoryPaternEnc(this, te.field_70331_k);
        this.patEnc.lockChanges = true;

        this.patEnc.lockChanges = false;
        this.addSlotToContainer(new SlotRestrictedInput(
            PlacableItemType.BLANK_PATTERN,
            this.patEnc.storage,
            10,
            151,
            19,
            inventoryPlayer
        ));
        this.addSlotToContainer(new SlotRestrictedInput(
            PlacableItemType.ENCODED_PATTERN,
            this.patEnc.storage,
            11,
            151,
            63,
            inventoryPlayer
        ));
        this.addSlotToContainer(
            new SlotFake(((TilePatternEncoder) this.getTileEntity()).storage, 9, 119, 41)
        );

        int var6;
        int var7;
        for (var6 = 0; var6 < 3; ++var6) {
            for (var7 = 0; var7 < 3; ++var7) {
                this.addSlotToContainer(new SlotFake(
                    ((TilePatternEncoder) this.getTileEntity()).storage,
                    var7 + var6 * 3,
                    30 + var7 * 18,
                    17 + var6 * 18 + 6
                ));
            }
        }

        for (var6 = 0; var6 < 3; ++var6) {
            for (var7 = 0; var7 < 9; ++var7) {
                this.addSlotToContainer(new SlotPlayerInv(
                    inventoryPlayer,
                    var7 + var6 * 9 + 9,
                    8 + var7 * 18,
                    84 + var6 * 18 + 6
                ));
            }
        }

        for (var6 = 0; var6 < 9; ++var6) {
            this.addSlotToContainer(
                new SlotPlayerHotBar(inventoryPlayer, var6, 8 + var6 * 18, 148)
            );
        }
    }

    public void onContainerClosed(EntityPlayer par1EntityPlayer) {
        super.onContainerClosed(par1EntityPlayer);
    }

    public void clear() {
        ((TilePatternEncoder) this.getTileEntity()).clearConfig();
    }

    public void encode() {
        ((TilePatternEncoder) this.getTileEntity()).encodePattern();
    }
}
