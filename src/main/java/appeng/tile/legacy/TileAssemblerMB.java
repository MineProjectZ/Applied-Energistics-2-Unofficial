package appeng.tile.legacy;

import java.util.List;

import appeng.api.implementations.ICraftingPatternItem;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.me.cluster.IAssemblerCluster;
import appeng.me.cluster.IAssemblerMB;
import appeng.me.cluster.implementations.AssemblerCluster;
import appeng.tile.TileEvent;
import appeng.tile.events.TileEventType;
import appeng.tile.grid.AENetworkTile;
import appeng.util.Platform;
import appeng.util.inv.WrapperChainedInventory;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class TileAssemblerMB extends AENetworkTile implements IAssemblerMB, IInventory {
    public boolean complete;
    public AssemblerCluster ac;
    private boolean isEdge;
    int state = -1;

    public TileAssemblerMB() {
        // TODO: WTF
        //super.updatesOnPower = false;
        this.getProxy().setIdlePowerUsage(0.0);
    }

    @Override
    public boolean canBeRotated() {
        return false;
    }

    // TODO: WTF
    //protected void terminate() {
    //    super.terminate();
    //    if (this.ac != null) {
    //        this.ac.destroy();
    //    }

    //    this.ac = null;
    //}

    public IAssemblerCluster getCluster() {
        return this.ac;
    }

    public boolean isComplete() {
        return this.complete || this.ac != null;
    }

    @Override
    public void calculateMultiblock() {
        TileAssembler.calculateMultiblockR(this);
    }

    @Override
    public void onReady() {
        super.onReady();
        // TODO: crafting accelerator
        //this.isEdge
        //    = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord)
        //    != AppEng.getInstance().registration.blkCraftingAccelerator.getMetaData();
        this.onNeighborBlockChange();
    }

    public int getState() {
        int o = 0;
        // TODO: WTF
        //if (AppEng.getInstance().GridManager.getEntityFromCoord(new DimentionalCoord(
        //        this.worldObj, this.xCoord - 1, this.yCoord, this.zCoord
        //    ))
        //    != null) {
        //    o |= 1;
        //}

        //if (AppEng.getInstance().GridManager.getEntityFromCoord(new DimentionalCoord(
        //        this.field_70331_k,
        //        this.field_70329_l + 1,
        //        this.field_70330_m,
        //        this.field_70327_n
        //    ))
        //    != null) {
        //    o |= 2;
        //}

        //if (AppEng.getInstance().GridManager.getEntityFromCoord(new DimentionalCoord(
        //        this.field_70331_k,
        //        this.field_70329_l,
        //        this.field_70330_m - 1,
        //        this.field_70327_n
        //    ))
        //    != null) {
        //    o |= 4;
        //}

        //if (AppEng.getInstance().GridManager.getEntityFromCoord(new DimentionalCoord(
        //        this.field_70331_k,
        //        this.field_70329_l,
        //        this.field_70330_m + 1,
        //        this.field_70327_n
        //    ))
        //    != null) {
        //    o |= 8;
        //}

        //if (AppEng.getInstance().GridManager.getEntityFromCoord(new DimentionalCoord(
        //        this.field_70331_k,
        //        this.field_70329_l,
        //        this.field_70330_m,
        //        this.field_70327_n - 1
        //    ))
        //    != null) {
        //    o |= 16;
        //}

        //if (AppEng.getInstance().GridManager.getEntityFromCoord(new DimentionalCoord(
        //        this.field_70331_k,
        //        this.field_70329_l,
        //        this.field_70330_m,
        //        this.field_70327_n + 1
        //    ))
        //    != null) {
        //    o |= 32;
        //}

        return o;
    }

    public void onNeighborBlockChange() {
        if (this.isEdge) {
            int newState = this.getState();
            int oldState = this.state;
            if (this.state != newState) {
                this.state = newState;
                // TODO: WTF
                //MinecraftForge.EVENT_BUS.post(new MultiBlockUpdateEvent(
                //    this, this.worldObj, this.getLocation()
                //));
            }
        }
    }

    // TODO: renderer
    //@SideOnly(Side.CLIENT)
    //public boolean renderWorldBlock(
    //    IBlockAccess world,
    //    int x,
    //    int y,
    //    int z,
    //    Block block,
    //    int modelId,
    //    RenderBlocks renderer
    //) {
    //    AppEngBlockRenderer appEngRenderer = AppEngBlockRenderer.instance;
    //    AppEngSubBlock sb = ((AppEngMultiBlock) block).getSubBlock(this.func_70322_n());
    //    if (this.complete) {
    //        if (sb == AppEng.getInstance().registration.blkHeatVent) {
    //            block.func_71905_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    //            appEngRenderer.setOverrideBlockTexture(
    //                block, AppEngTextureRegistry.Blocks.BlockHeatVentMerged.get()
    //            );
    //            renderer.func_83018_a(block);
    //            renderer.func_78570_q(block, x, y, z);
    //            appEngRenderer.setOverrideBlockTexture(block, (Icon) null);
    //            return true;
    //        }

    //        if (sb == AppEng.getInstance().registration.blkAssemblerFieldWall) {
    //            boolean XAxis = world.func_72796_p(x + 1, y, z) instanceof
    //            TileAssemblerMB
    //                && world.func_72796_p(x - 1, y, z) instanceof TileAssemblerMB;
    //            boolean YAxis = world.func_72796_p(x, y + 1, z) instanceof
    //            TileAssemblerMB
    //                && world.func_72796_p(x, y - 1, z) instanceof TileAssemblerMB;
    //            boolean ZAxis = world.func_72796_p(x, y, z + 1) instanceof
    //            TileAssemblerMB
    //                && world.func_72796_p(x, y, z - 1) instanceof TileAssemblerMB;
    //            if (XAxis) {
    //                block.func_71905_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    //                appEngRenderer.setOverrideBlockTexture(
    //                    block,
    //                    AppEngTextureRegistry.Blocks.BlockContainmentWallMerged.get()
    //                );
    //                renderer.func_83018_a(block);
    //                renderer.func_78570_q(block, x, y, z);
    //                appEngRenderer.setOverrideBlockTexture(block, (Icon) null);
    //                return true;
    //            }

    //            if (YAxis) {
    //                renderer.field_78683_h = 1;
    //                renderer.field_78662_g = 1;
    //                renderer.field_78679_j = 1;
    //                renderer.field_78685_i = 1;
    //                block.func_71905_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    //                appEngRenderer.setOverrideBlockTexture(
    //                    block,
    //                    AppEngTextureRegistry.Blocks.BlockContainmentWallMerged.get()
    //                );
    //                renderer.func_83018_a(block);
    //                renderer.func_78570_q(block, x, y, z);
    //                appEngRenderer.setOverrideBlockTexture(block, (Icon) null);
    //                renderer.field_78683_h = 0;
    //                renderer.field_78662_g = 0;
    //                renderer.field_78679_j = 0;
    //                renderer.field_78685_i = 0;
    //                return true;
    //            }

    //            if (ZAxis) {
    //                renderer.field_78681_k = 1;
    //                renderer.field_78675_l = 1;
    //                block.func_71905_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    //                appEngRenderer.setOverrideBlockTexture(
    //                    block,
    //                    AppEngTextureRegistry.Blocks.BlockContainmentWallMerged.get()
    //                );
    //                renderer.func_83018_a(block);
    //                renderer.func_78570_q(block, x, y, z);
    //                appEngRenderer.setOverrideBlockTexture(block, (Icon) null);
    //                renderer.field_78681_k = 0;
    //                renderer.field_78675_l = 0;
    //                return true;
    //            }
    //        }
    //    }

    //    return super.renderWorldBlock(world, x, y, z, block, modelId, renderer);
    //}

    public void updateStatus(IAssemblerCluster _ac) {
        this.ac = (AssemblerCluster) _ac;
        if (!Platform.isClient()) {
            this.markForUpdate();
        }
    }

    public boolean isEnabled() {
        if (Platform.isClient()) {
            return this.complete;
        } else {
            return this.ac != null;
        }
    }

    public IInventory getInventory() {
        if (this.ac == null) {
            return null;
        } else {
            if (this.ac.inv == null) {
                this.ac.inv = new WrapperChainedInventory(
                    this.ac.assemblers.toArray(new IInventory[0])
                );
            }

            return this.ac.inv;
        }
    }

    public int getSizeInventory() {
        IInventory inv = this.getInventory();
        return inv == null ? 0 : inv.getSizeInventory();
    }

    public ItemStack getStackInSlot(int var1) {
        IInventory inv = this.getInventory();
        return inv == null ? null : inv.getStackInSlot(var1);
    }

    public ItemStack decrStackSize(int var1, int var2) {
        IInventory inv = this.getInventory();
        return inv == null ? null : inv.decrStackSize(var1, var2);
    }

    public ItemStack getStackInSlotOnClosing(int var1) {
        return null;
    }

    public void setInventorySlotContents(int var1, ItemStack var2) {
        IInventory inv = this.getInventory();
        if (inv != null) {
            inv.setInventorySlotContents(var1, var2);
        }
    }

    @Override
    public String getInventoryName() {
        return "ME Assembler Chamber";
    }

    @Override
    public int getInventoryStackLimit() {
        IInventory inv = this.getInventory();
        return inv == null ? 0 : inv.getInventoryStackLimit();
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean hasCustomInventoryName() {
        return true;
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        if (itemstack.getItem() instanceof ICraftingPatternItem) {
            ICraftingPatternDetails pat
                = ((ICraftingPatternItem) itemstack.getItem())
                      .getPatternForItem(itemstack, this.worldObj);

            return pat != null;
        }
        return false;
    }

    // TODO: WTF
    //public void bulkUpdate(long inst, Player player) {
    //    if (this.ac != null && this.ac.inst != inst) {
    //        this.ac.inst = inst;
    //        this.sendUpdate(true, player);
    //    }
    //}

    public boolean requiresTickingUpdates() {
        return false;
    }

    public void sendUpdate(boolean newState, EntityPlayer player) {
        if (this.ac != null) {
            NBTTagCompound tc = new NBTTagCompound();
            tc.setBoolean("formed", newState);

            // TODO: WTF
            //try {
            //    Packet250CustomPayload p
            //        = (new PacketBulkMAC(
            //               Platform.createBulkUpdate(tc, this.ac.min, this.ac.max)
            //           ))
            //              .getPacket();
            //    if (p != null) {
            //        if (player == null) {
            //            double var10002 = (double) this.field_70329_l;
            //            double var10003 = (double) this.field_70330_m;
            //            double var10004 = (double) this.field_70327_n;
            //            AppEng.getInstance().SideProxy.sendToAllNearExcept(
            //                (EntityPlayer) null,
            //                var10002,
            //                var10003,
            //                var10004,
            //                512.0,
            //                this.field_70331_k,
            //                p
            //            );
            //        } else {
            //            PacketDispatcher.sendPacketToPlayer(p, player);
            //        }
            //    }
            //} catch (IOException var6) {
            //    var6.printStackTrace();
            //}
        }
    }

    public boolean isSeperated() {
        return !this.isEnabled();
    }

    public boolean isUseableByPlayer(EntityPlayer entityplayer) {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) != this
            ? false
            : entityplayer.getDistanceSq(
                  (double) this.xCoord + 0.5,
                  (double) this.yCoord + 0.5,
                  (double) this.zCoord + 0.5
              ) <= 32.0;
    }

    @TileEvent(TileEventType.NETWORK_WRITE)
    public boolean writeToStreamTileAssemblerMB(ByteBuf buf) {
        buf.writeBoolean(this.ac != null);
        return true;
    }

    @TileEvent(TileEventType.NETWORK_READ)
    public boolean readFromStreamTileAssemblerMB(ByteBuf buf) {
        this.complete = buf.readBoolean();
        this.worldObj.markBlockRangeForRenderUpdate(
            this.xCoord, this.yCoord, this.zCoord, this.xCoord, this.yCoord, this.zCoord
        );
        System.out.println("ALEC: " + this.complete);
        return true;
    }

    @Override
    public void getDrops(World w, int x, int y, int z, List<ItemStack> drops) {}
}
