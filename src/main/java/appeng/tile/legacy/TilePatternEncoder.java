package appeng.tile.legacy;

import java.util.List;

import appeng.api.AEApi;
import appeng.api.implementations.ICraftingPatternItem;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEItemStack;
import appeng.container.ContainerNull;
import appeng.container.implementations.ContainerPatternEncoder;
import appeng.tile.AEBaseInvTile;
import appeng.tile.TileEvent;
import appeng.tile.events.TileEventType;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.tile.inventory.InvOperation;
import appeng.util.Platform;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class TilePatternEncoder extends AEBaseInvTile {
    public AppEngInternalInventory storage = new AppEngInternalInventory(this, 12);
    public boolean lockChanges = false;

    public void invChanged(int slot, InvOperation op) {
        if (slot == 11) {
            this.lockChanges = true;
            ItemStack pi = this.storage.getStackInSlot(11);
            if (pi != null && pi.getItem() instanceof ICraftingPatternItem) {
                ICraftingPatternDetails ap = ((ICraftingPatternItem) pi.getItem())
                                                 .getPatternForItem(pi, this.worldObj);
                if (ap == null)
                    return;

                IAEItemStack[] s = ap.getInputs();

                for (int x = 0; x < 9; ++x) {
                    this.storage.setInventorySlotContents(
                        x, s[x] == null ? null : s[x].getItemStack()
                    );
                }

                this.storage.setInventorySlotContents(
                    9, ap.getCondensedOutputs()[0].getItemStack()
                );
            }

            this.lockChanges = false;
            this.triggerContainerUpdate();
        } else {
            if (slot >= 0 && slot <= 8 && !this.lockChanges) {
                InventoryCrafting ci = new InventoryCrafting(new ContainerNull(), 3, 3);

                for (int x = 0; x < 9; ++x) {
                    ci.setInventorySlotContents(x, this.storage.getStackInSlot(x));
                }

                ItemStack is = Platform.findMatchingRecipeOutput(ci, this.worldObj);
                if (is != null) {
                    this.storage.setInventorySlotContents(9, is);
                } else {
                    this.storage.setInventorySlotContents(9, null);
                }
            }

            this.triggerContainerUpdate();
        }
    }

    public void encodePattern() {
        ItemStack fish = this.storage.getStackInSlot(11);
        if (this.storage.getStackInSlot(9) != null && !this.craftingGridEmpty()) {
            if (fish == null) {
                fish = this.storage.decrStackSize(10, 1);
            }

            if (fish != null) {
                if (fish.getItem()
                    == AEApi.instance()
                           .definitions()
                           .materials()
                           .blankPattern()
                           .maybeItem()
                           .get()) {
                    fish = AEApi.instance()
                               .definitions()
                               .items()
                               .encodedPattern()
                               .maybeStack(1)
                               .get();
                    this.storage.setInventorySlotContents(11, fish);
                }

                ItemStack[] cm = new ItemStack[] {
                    this.storage.getStackInSlot(0), this.storage.getStackInSlot(1),
                    this.storage.getStackInSlot(2), this.storage.getStackInSlot(3),
                    this.storage.getStackInSlot(4), this.storage.getStackInSlot(5),
                    this.storage.getStackInSlot(6), this.storage.getStackInSlot(7),
                    this.storage.getStackInSlot(8)
                };

                NBTTagCompound fishTag = new NBTTagCompound();

                NBTTagList inputList = new NBTTagList();
                for (ItemStack inStack : cm) {
                    NBTTagCompound inputTag = new NBTTagCompound();

                    if (inStack != null) {
                        inStack.writeToNBT(inputTag);
                    }

                    inputList.appendTag(inputTag);
                }

                NBTTagCompound outputTag = new NBTTagCompound();
                if (this.storage.getStackInSlot(9) != null)
                    this.storage.getStackInSlot(9).writeToNBT(outputTag);

                NBTTagList outputList = new NBTTagList();
                outputList.appendTag(outputTag);

                fishTag.setTag("in", inputList);
                fishTag.setTag("out", outputList);

                InventoryCrafting ci = new InventoryCrafting(new ContainerNull(), 3, 3);

                for (int x = 0; x < 9; ++x) {
                    ci.setInventorySlotContents(x, this.storage.getStackInSlot(x));
                }

                fishTag.setBoolean(
                    "crafting",
                    ItemStack.areItemStacksEqual(
                        Platform.findMatchingRecipeOutput(ci, this.worldObj),
                        this.storage.getStackInSlot(9)
                    )
                );
                fishTag.setBoolean("substitute", false);

                fish.setTagCompound(fishTag);

                this.storage.setInventorySlotContents(11, fish);
                this.triggerContainerUpdate();
            }
        }
    }

    @TileEvent(TileEventType.WORLD_NBT_READ)
    public void readFromNbtTilePatternEncoder(NBTTagCompound tagCompound) {
        this.storage.readFromNBT(tagCompound, "storage");
        this.invChanged(-1, InvOperation.setInventorySlotContents);
    }

    @TileEvent(TileEventType.WORLD_NBT_WRITE)
    public void writeToNbtTilePatternEncoder(NBTTagCompound tagCompound) {
        this.storage.writeToNBT(tagCompound, "storage");
    }

    @Override
    public int getSizeInventory() {
        return this.storage.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int var1) {
        return this.storage.getStackInSlot(var1);
    }

    @Override
    public ItemStack decrStackSize(int var1, int var2) {
        return this.storage.decrStackSize(var1, var2);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int var1) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int var1, ItemStack var2) {
        this.storage.setInventorySlotContents(var1, var2);
    }

    @Override
    public String getInventoryName() {
        return "Pattern Encoder";
    }

    @Override
    public int getInventoryStackLimit() {
        return this.storage.getInventoryStackLimit();
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return i == 1 ? itemstack.getItem()
                == AEApi.instance()
                       .definitions()
                       .items()
                       .encodedPattern()
                       .maybeItem()
                       .get()
                      : itemstack.getItem()
                == AEApi.instance()
                       .definitions()
                       .materials()
                       .blankPattern()
                       .maybeItem()
                       .get();
    }

    @Override
    public boolean canInsertItem(int i, ItemStack itemstack, int j) {
        return this.isItemValidForSlot(i, itemstack);
    }

    @Override
    public boolean canExtractItem(int i, ItemStack itemstack, int j) {
        return true;
    }

    public void clearConfig() {
        for (int x = 0; x < 10; ++x) {
            this.storage.setInventorySlotContents(x, null);
        }

        this.triggerContainerUpdate();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer p) {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) != this
            ? false
            : p.getDistanceSq(
                  (double) this.xCoord + 0.5,
                  (double) this.yCoord + 0.5,
                  (double) this.zCoord + 0.5
              ) <= 32.0;
    }

    private void triggerContainerUpdate() {
        if (this.worldObj == null)
            return;

        List<EntityPlayerMP> players = this.worldObj.getEntitiesWithinAABB(
            EntityPlayerMP.class,
            AxisAlignedBB.getBoundingBox(
                this.xCoord - 8.0,
                this.yCoord - 8.0,
                this.zCoord - 8.0,
                this.xCoord + 8.0,
                this.yCoord + 8.0,
                this.zCoord + 8.0
            )
        );

        for (EntityPlayerMP pl : players) {
            if (!(pl.openContainer instanceof ContainerPatternEncoder))
                continue;

            pl.sendContainerToPlayer(pl.openContainer);
        }
    }

    @Override
    public IInventory getInternalInventory() {
        return this.storage;
    }

    @Override
    public void onChangeInventory(
        IInventory inv, int slot, InvOperation mc, ItemStack removed, ItemStack added
    ) {
        this.invChanged(slot, mc);
    }

    @Override
    public int[] getAccessibleSlotsBySide(ForgeDirection side) {
        return new int[] { side == ForgeDirection.DOWN ? 11 : 10 };
    }

    @Override
    public void getDrops(
        final World w, final int x, final int y, final int z, final List<ItemStack> drops
    ) {
        if (this instanceof IInventory) {
            final IInventory inv = (IInventory) this;

            for (int l = 10; l < inv.getSizeInventory(); l++) {
                final ItemStack is = inv.getStackInSlot(l);
                if (is != null) {
                    drops.add(is);
                }
            }
        }
    }

    public boolean craftingGridEmpty() {
        for(int i = 0; i < 9; i++) {
            ItemStack stack = this.storage.getStackInSlot(i);
            if (stack != null) {
                return false;
            }
        }
        return true;
    }
}
