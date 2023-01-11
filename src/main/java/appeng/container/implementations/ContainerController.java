package appeng.container.implementations;

import java.io.IOException;

import appeng.api.AEApi;
import appeng.api.features.ILocatable;
import appeng.api.features.INetworkEncodable;
import appeng.api.features.IWirelessTermHandler;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridBlock;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.container.AEBaseContainer;
import appeng.container.guisync.GuiSync;
import appeng.container.slot.SlotOutput;
import appeng.container.slot.SlotPlayerHotBar;
import appeng.container.slot.SlotPlayerInv;
import appeng.container.slot.SlotRestrictedInput;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.PacketMEInventoryUpdate;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.tile.inventory.IAEAppEngInventory;
import appeng.tile.inventory.InvOperation;
import appeng.tile.legacy.TileLegacyController;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

public class ContainerController extends AEBaseContainer implements IAEAppEngInventory {
    @GuiSync(0)
    public long avgAddition;
    @GuiSync(1)
    public long powerUsage;
    @GuiSync(2)
    public long currentPower;
    @GuiSync(3)
    public long maxPower;
    private IGrid network;
    private IGridHost host;
    private int delay = 40;
    private final AppEngInternalInventory wirelessEncoder = new AppEngInternalInventory(this, 2);
    private final SlotRestrictedInput wirelessIn;
    private final SlotOutput wirelessOut;

    public ContainerController(final InventoryPlayer ip, final IGridHost te) {
        super(ip, null, null);
        host = te;

        if (host != null) {
            this.findNode(host, ForgeDirection.UNKNOWN);
            for (final ForgeDirection d : ForgeDirection.VALID_DIRECTIONS) {
                this.findNode(host, d);
            }
        }

        if (this.network == null && Platform.isServer()) {
            this.setValidContainer(false);
        }

        this.wirelessIn = new SlotRestrictedInput(SlotRestrictedInput.PlacableItemType.ENCODABLE_ITEM, this.wirelessEncoder, 0, 212, 10, ip);
        this.wirelessOut = new SlotOutput(this.wirelessEncoder, 1, 212, 68, -1);
        this.addSlotToContainer(wirelessIn);
        this.addSlotToContainer(wirelessOut);

        this.bindPlayerInventory(ip);
    }
    
    private void findNode(final IGridHost host, final ForgeDirection d) {
        if (this.network == null) {
            final IGridNode node = host.getGridNode(d);
            if (node != null) {
                this.network = node.getGrid();
            }
        }
    }

    @Override
    public void detectAndSendChanges() {
        this.delay++;
        if (Platform.isServer() && this.delay > 15 && this.network != null) {
            this.delay = 0;

            final IEnergyGrid eg = this.network.getCache(IEnergyGrid.class);
            if (eg != null) {
                this.setAverageAddition((long) (100.0 * eg.getAvgPowerInjection()));
                this.setPowerUsage((long) (100.0 * eg.getAvgPowerUsage()));
                if (host instanceof TileLegacyController) {
                    this.setCurrentPower((long)(100.0 * ((TileLegacyController)host).getAECurrentPower()));
                } else {
                    this.setCurrentPower((long) (100.0 * eg.getStoredPower()));
                }
                this.setMaxPower((long) (100.0 * eg.getMaxStoredPower()));
            }

            try {
                final PacketMEInventoryUpdate piu = new PacketMEInventoryUpdate();

                for (final Class<? extends IGridHost> machineClass :
                     this.network.getMachinesClasses()) {
                    final IItemList<IAEItemStack> list
                        = AEApi.instance().storage().createItemList();
                    for (final IGridNode machine :
                         this.network.getMachines(machineClass)) {
                        final IGridBlock blk = machine.getGridBlock();
                        final ItemStack is = blk.getMachineRepresentation();
                        if (is != null && is.getItem() != null) {
                            final IAEItemStack ais = AEItemStack.create(is);
                            ais.setStackSize(1);
                            ais.setCountRequestable((long
                            ) (blk.getIdlePowerUsage() * 100.0));
                            list.add(ais);
                        }
                    }

                    for (final IAEItemStack ais : list) {
                        piu.appendItem(ais);
                    }
                }

                for (final Object c : this.crafters) {
                    if (c instanceof EntityPlayer) {
                        NetworkHandler.instance.sendTo(piu, (EntityPlayerMP) c);
                    }
                }
            } catch (final IOException e) {
                // :P
            }
        }
        super.detectAndSendChanges();
    }

    public long getCurrentPower() {
        return this.currentPower;
    }

    private void setCurrentPower(final long currentPower) {
        this.currentPower = currentPower;
    }

    public long getMaxPower() {
        return this.maxPower;
    }

    private void setMaxPower(final long maxPower) {
        this.maxPower = maxPower;
    }

    public long getAverageAddition() {
        return this.avgAddition;
    }

    private void setAverageAddition(final long avgAddition) {
        this.avgAddition = avgAddition;
    }

    public long getPowerUsage() {
        return this.powerUsage;
    }

    private void setPowerUsage(final long powerUsage) {
        this.powerUsage = powerUsage;
    }

    protected void bindPlayerInventory(final InventoryPlayer inventoryPlayer) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new SlotPlayerInv(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 113 + i * 18 + 20));
            }
        }
        for (int i = 0; i < 9; ++i) {
            this.addSlotToContainer(new SlotPlayerHotBar(inventoryPlayer, i, 8 + i * 18, 191));
        }
    }

    @Override
    public void onContainerClosed(final EntityPlayer player) {
        super.onContainerClosed(player);

        if (this.wirelessIn.getHasStack()) {
            player.dropPlayerItemWithRandomChoice(this.wirelessIn.getStack(), false);
        }

        if (this.wirelessOut.getHasStack()) {
            player.dropPlayerItemWithRandomChoice(this.wirelessOut.getStack(), false);
        }
    }

    @Override
    public void saveChanges() {
        
    }

    @Override
    public void onChangeInventory(
        final IInventory inv,
        final int slot,
        final InvOperation mc,
        final ItemStack removedStack,
        final ItemStack newStack
    ) {
        if (!this.wirelessOut.getHasStack() && this.wirelessIn.getHasStack() && this.host instanceof ILocatable) {
                final ItemStack term = this.wirelessIn.getStack().copy();
                INetworkEncodable networkEncodable = null;

                if (term.getItem() instanceof INetworkEncodable) {
                    networkEncodable = (INetworkEncodable) term.getItem();
                }

                final IWirelessTermHandler wTermHandler
                    = AEApi.instance().registries().wireless().getWirelessTerminalHandler(
                        term
                    );
                if (wTermHandler != null) {
                    networkEncodable = wTermHandler;
                }

                if (networkEncodable != null) {
                    networkEncodable.setEncryptionKey(
                        term, String.valueOf(((ILocatable)host).getLocatableSerial()), ""
                    );

                    this.wirelessIn.putStack(null);
                    this.wirelessOut.putStack(term);

                    // update the two slots in question...
                    for (final Object crafter : this.crafters) {
                        final ICrafting icrafting = (ICrafting) crafter;
                        icrafting.sendSlotContents(
                            this, this.wirelessIn.slotNumber, this.wirelessIn.getStack()
                        );
                        icrafting.sendSlotContents(
                            this, this.wirelessOut.slotNumber, this.wirelessOut.getStack()
                        );
                    }
                }
            }
    }

}
