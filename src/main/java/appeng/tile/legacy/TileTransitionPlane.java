package appeng.tile.legacy;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGrid;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.security.MachineSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.data.IAEItemStack;
import appeng.core.sync.packets.PacketTransitionEffect;
import appeng.me.GridAccessException;
import appeng.server.ServerHelper;
import appeng.tile.TileEvent;
import appeng.tile.events.TileEventType;
import appeng.tile.grid.AENetworkTile;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.common.util.ForgeDirection;

public class TileTransitionPlane extends AENetworkTile {
    List<IAEItemStack> buffer = new ArrayList<>();
    int delay = 0;
    int trys = 0;
    boolean wasPulse = false;
    static Set<Block> unbreakables = new HashSet<>();
    private final BaseActionSource mySrc = new MachineSource(this);

    public TileTransitionPlane() {
        // TODO: WTF
        //super.hasPower = false;
        this.getProxy().setValidSides(EnumSet.allOf(ForgeDirection.class));
        this.getProxy().setFlags(GridFlags.REQUIRE_CHANNEL);
        this.getProxy().setIdlePowerUsage(1.0);
    }

    // TODO: WTF
    //public void setOrientationBySide(
    //    EntityPlayer player, int side, float hitX, float hitY, float hitZ
    //) {
    //    super.setOrientationBySide(player, side, hitX, hitY, hitZ);
    //    ForgeDirection getForward() = ForgeDirection.getOrientation(side);
    //    WorldCoord pos
    //        = new WorldCoord(this.xCoord, this.yCoord, this.zCoord);
    //    pos.add(getForward(), -1);
    //    this.worldObj.getTileEntity(pos.x, pos.y, pos.z);
    //    MinecraftForge.EVENT_BUS.post(
    //        new GridTileConnectivityEvent(this, this.worldObj, this.getLocation())
    //    );
    //}

    // TODO: WTF
    //public boolean syncStyle(IAppEngNetworkTile.SyncTime st) {
    //    return true;
    //}

    @TileEvent(TileEventType.TICK)
    public void updateTileEntity() {
        if (this.getProxy().isActive() && this.delay++ > 320) {
            if (this.eatBlock()) {
                this.delay = 0;
            }

            try {
                IGrid gi = this.getProxy().getGrid();

                if (gi != null) {
                    while (!this.buffer.isEmpty()) {
                        IAEItemStack aeitem = this.buffer.get(0);
                        this.buffer.remove(0);
                        IStorageGrid storage = this.getProxy().getStorage();
                        IEnergyGrid energy = this.getProxy().getEnergy();
                        IAEItemStack overflow = Platform.poweredInsert(
                            energy, storage.getItemInventory(), aeitem, this.mySrc
                        );
                        if (overflow != null) {
                            this.buffer.add(overflow);
                            break;
                        }

                        if (this.buffer.isEmpty()) {
                            this.markForUpdate();
                        }
                    }
                    if (this.buffer.isEmpty()) {
                        this.reqEat();
                    }
                }
            } catch (GridAccessException e) {
                // :P
            }
        }
    }

    // TODO: WTF
    //public void ddItem(EntityItem entitiy) {
    //    if (this.isAccepting()) {
    //        ItemStack is = entitiy.getEntityItem();
    //        if (this.Buffer.isEmpty() && this.getProxy().isActive() &&
    //        Platform.isServer()
    //            && is != null) {
    //            IGrid gi = this.getProxy().getGrid();
    //            if (gi != null && !entitiy.isDead) {
    //                int howMany = gi.usePowerForAddition(is.stackSize, 1);
    //                if (howMany > 0) {
    //                    IMEInventory ca = gi.getCellArray();
    //                    if (ca != null) {
    //                        ItemStack toAdd = is.splitStack(howMany);
    //                        toAdd = Platform.refundEnergy(
    //                            gi, Platform.addItems(ca, toAdd), "Transiton Plane"
    //                        );
    //                        if (toAdd != null) {
    //                            is.stackSize += toAdd.stackSize;
    //                        }

    //                        try {
    //                            float var10009 = (float) entitiy.posX;
    //                            AppEng.getInstance().SideProxy.sendToAllNearExcept(
    //                                (EntityPlayer) null,
    //                                entitiy.posX,
    //                                entitiy.posY,
    //                                entitiy.posZ,
    //                                64.0,
    //                                this.field_70331_k,
    //                                (new PacketTransitionPlane(
    //                                     var10009,
    //                                     (float) entitiy.posY,
    //                                     (float) entitiy.posZ,
    //                                     this.getForward(),
    //                                     false
    //                                 ))
    //                                    .getPacket()
    //                            );
    //                        } catch (IOException var8) {}

    //                        if (is.stackSize > 0) {
    //                            this.Buffer.add(is.copy());
    //                            this.markForUpdate();
    //                        }

    //                        entitiy.setDead();
    //                    }
    //                }
    //            }
    //        }
    //    }
    //}

    public boolean isAccepting() {
        this.wasPulse = false;
        return true;
    }

    // TODO: WTF
    //public static void blacklist(int itemID, int damageValue) {
    //    unbreakables.add(damageValue << 16 | itemID);
    //}

    public boolean notUnbreakable(Block block, int damageValue) {
        if (block != Blocks.air && block.getMaterial() != Material.air
            && block.getMaterial() != Material.lava
            && block.getMaterial() != Material.water) {
            return !unbreakables.contains(block);
        } else {
            return false;
        }
    }

    public boolean eatBlock() {
        if (this.isAccepting() && this.getProxy().isActive() && Platform.isServer()
            // TODO: WTF
            /*&& this.isValid()*/) {
            int x = this.xCoord + this.getForward().offsetX;
            int y = this.yCoord + this.getForward().offsetY;
            int z = this.zCoord + this.getForward().offsetZ;
            Block bid = this.worldObj.getBlock(x, y, z);
            int meta = this.worldObj.getBlockMetadata(x, y, z);
            if (!this.worldObj.isAirBlock(x, y, z) && this.worldObj.blockExists(x, y, z)
                && this.buffer.isEmpty() && bid != Blocks.air
                && this.notUnbreakable(bid, meta) && bid != Blocks.bedrock
                && this.worldObj.canMineBlock(
                    FakePlayerFactory.getMinecraft((WorldServer) this.worldObj), x, y, z
                )) {
                float hardness = bid.getBlockHardness(this.worldObj, x, y, z);
                if ((double) hardness >= 0.0) {
                    ItemStack[] out = Platform.getBlockDrops(this.worldObj, x, y, z);
                    float total = 1.0F + hardness;
                    ItemStack[] arr$ = out;
                    int len$ = out.length;
                    for (len$ = 0; len$ < len$; ++len$) {
                        ItemStack is = arr$[len$];
                        total += (float) is.stackSize;
                    }

                    boolean hasPower = false;
                    try {
                        hasPower = this.getProxy().getEnergy().extractAEPower(
                                       total, Actionable.MODULATE, PowerMultiplier.CONFIG
                                   )
                            > total - 0.1;
                    } catch (GridAccessException e1) {
                        // :P
                    }
                    if (hasPower) {
                        this.worldObj.setBlockToAir(x, y, z);
                        this.trys = 0;

                        ServerHelper.proxy.sendToAllNearExcept(
                            null,
                            this.xCoord,
                            this.yCoord,
                            this.zCoord,
                            64,
                            this.getTile().getWorldObj(),
                            new PacketTransitionEffect(
                                this.xCoord + this.getForward().offsetX + 0.5,
                                this.yCoord + this.getForward().offsetY + 0.5,
                                this.zCoord + this.getForward().offsetZ + 0.5,
                                this.getForward(),
                                false
                            )
                        );

                        arr$ = out;
                        len$ = out.length;

                        for (int i$ = 0; i$ < len$; ++i$) {
                            //ItemStack is = arr$[i$];
                            //is = Platform.addItems(ca, is);
                            //if (is != null) {
                            //    this.Buffer.add(is);
                            //    this.markForUpdate();
                            //}
                            if (arr$[i$] == null)
                                continue;

                            this.buffer.add(AEItemStack.create(arr$[i$]));
                        }

                        try {
                            IGrid gi = this.getProxy().getGrid();

                            if (gi != null) {
                                while (!this.buffer.isEmpty()) {
                                    IAEItemStack aeitem = this.buffer.get(0);
                                    this.buffer.remove(0);
                                    IStorageGrid storage = this.getProxy().getStorage();
                                    IEnergyGrid energy = this.getProxy().getEnergy();
                                    IAEItemStack overflow = Platform.poweredInsert(
                                        energy,
                                        storage.getItemInventory(),
                                        aeitem,
                                        this.mySrc
                                    );
                                    if (overflow != null) {
                                        this.buffer.add(overflow);
                                        break;
                                    }

                                    if (this.buffer.isEmpty()) {
                                        this.markForUpdate();
                                    }
                                }
                                if (this.buffer.isEmpty()) {
                                    this.reqEat();
                                }
                            }
                        } catch (GridAccessException e) {
                            // :P
                        }
                    }
                }
            }
        }

        return true;
    }

    public void reqEat() {
        this.delay += 9999;
    }

    // TODO: WTF
    //public void onNeighborBlockChange() {
    //    this.reqEat();
    //    this.trys = 0;
    //}

    //public void setPowerStatus(boolean _hasPower) {
    //    super.setPowerStatus(_hasPower);
    //    this.reqEat();
    //}

    //public void onUpdateRedstone() {
    //    super.onUpdateRedstone();
    //    this.reqEat();
    //}

    //public void pulseRedStone() {
    //    super.pulseRedStone();
    //    this.wasPulse = true;
    //    this.reqEat();
    //}

    // TODO: multiblock
    //boolean linesUp(int x, int y, int z) {
    //    TileEntity te = this.worldObj.getTileEntity(x, y, z);
    //    if (te instanceof TileTransitionPlane) {
    //        return ((TileTransitionPlane) te).getForward() == this.getForward();
    //    } else {
    //        return false;
    //    }
    //}

    // TODO: WTF
    //public void placedBy(EntityLivingBase entityliving) {
    //    byte rotation = (byte
    //    ) (MathHelper.floor_double(
    //           (double) (entityliving.rotationYaw * 4.0F / 360.0F) + 2.5
    //       )
    //       & 3);
    //    if (entityliving.rotationPitch > 65.0F) {
    //        rotation = 4;
    //    } else if (entityliving.rotationPitch < -65.0F) {
    //        rotation = 5;
    //    }

    //    //this.getForward() = this.getDirectionFromAERotation(rotation);
    //    this.getForward() = ForgeDirection.getOrientation(rotation);
    //}

    // TODO: WTF
    //private Icon defTextures(ForgeDirection side) {
    //    if (side == this.getForward().getOpposite()) {
    //        return AppEngTextureRegistry.Blocks.GenericBottom.get();
    //    } else if (side == ForgeDirection.DOWN) {
    //        return AppEngTextureRegistry.Blocks.GenericTop.get();
    //    } else {
    //        return side == ForgeDirection.UP
    //            ? AppEngTextureRegistry.Blocks.GenericTop.get()
    //            : AppEngTextureRegistry.Blocks.GenericSide.get();
    //    }
    //}

    //public Icon getBlockTextureFromSide(ForgeDirection side) {
    //    Icon frontFace = this.isMachineActive()
    //        ? AppEngTextureRegistry.Blocks.BlockTransPlane.get()
    //        : AppEngTextureRegistry.Blocks.BlockTransPlaneOff.get();
    //    return this.getForward() == side ? frontFace : this.defTextures(side);
    //}

    @Override
    public void writeToNBT(NBTTagCompound par1nbtTagCompound) {
        super.writeToNBT(par1nbtTagCompound);
        NBTTagList items = new NBTTagList();

        for (int x = 0; x < this.buffer.size(); ++x) {
            NBTTagCompound item = new NBTTagCompound();
            this.buffer.get(x).writeToNBT(item);
            items.appendTag(item);
        }

        par1nbtTagCompound.setTag("items", items);
    }

    @Override
    public void readFromNBT(NBTTagCompound par1nbtTagCompound) {
        super.readFromNBT(par1nbtTagCompound);
        NBTTagList items = par1nbtTagCompound.getTagList("items", 10);

        for (int x = 0; x < items.tagCount(); ++x) {
            this.buffer.add(AEItemStack.loadItemStackFromNBT(items.getCompoundTagAt(x)));
        }
    }

    // TODO: WTF
    //@SideOnly(Side.CLIENT)
    //public void renderTexturedPlate(
    //    float[] offset,
    //    float[] Left,
    //    float[] Up,
    //    float[] texOffset,
    //    float[] texDiff,
    //    Icon icon,
    //    int lightX,
    //    int lightY,
    //    int lightZ,
    //    boolean rev
    //) {
    //    Tessellator tess = Tessellator.instance;
    //    float[] p1 = new float[5];
    //    float[] p2 = new float[5];
    //    float[] p3 = new float[5];
    //    float[] p4 = new float[5];
    //    p1[0] = offset[0];
    //    p1[1] = offset[1];
    //    p1[2] = offset[2];
    //    p1[3] = icon.getInterpolatedU((double) texOffset[0]);
    //    p1[4] = icon.getInterpolatedV((double) texOffset[1]);
    //    p2[0] = offset[0] + Left[0];
    //    p2[1] = offset[1] + Left[1];
    //    p2[2] = offset[2] + Left[2];
    //    p2[3] = icon.getInterpolatedU((double) (texOffset[0] + texDiff[0]));
    //    p2[4] = icon.getInterpolatedV((double) texOffset[1]);
    //    p3[0] = offset[0] + Left[0] + Up[0];
    //    p3[1] = offset[1] + Left[1] + Up[1];
    //    p3[2] = offset[2] + Left[2] + Up[2];
    //    p3[3] = icon.getInterpolatedU((double) (texOffset[0] + texDiff[0]));
    //    p3[4] = icon.getInterpolatedV((double) (texOffset[1] + texDiff[1]));
    //    p4[0] = offset[0] + Up[0];
    //    p4[1] = offset[1] + Up[1];
    //    p4[2] = offset[2] + Up[2];
    //    p4[3] = icon.getInterpolatedU((double) texOffset[0]);
    //    p4[4] = icon.getInterpolatedV((double) (texOffset[1] + texDiff[1]));
    //    tess.setBrightness(
    //        this.getBlockType().getMixedBrightnessForBlock(this.worldObj, lightX,
    //        lightY, lightZ)
    //    );
    //    if (rev) {
    //        tess.addVertexWithUV(
    //            (double) p4[0],
    //            (double) p4[1],
    //            (double) p4[2],
    //            (double) p4[3],
    //            (double) p4[4]
    //        );
    //        tess.addVertexWithUV(
    //            (double) p3[0],
    //            (double) p3[1],
    //            (double) p3[2],
    //            (double) p3[3],
    //            (double) p3[4]
    //        );
    //        tess.addVertexWithUV(
    //            (double) p2[0],
    //            (double) p2[1],
    //            (double) p2[2],
    //            (double) p2[3],
    //            (double) p2[4]
    //        );
    //        tess.addVertexWithUV(
    //            (double) p1[0],
    //            (double) p1[1],
    //            (double) p1[2],
    //            (double) p1[3],
    //            (double) p1[4]
    //        );
    //    } else {
    //        tess.addVertexWithUV(
    //            (double) p1[0],
    //            (double) p1[1],
    //            (double) p1[2],
    //            (double) p1[3],
    //            (double) p1[4]
    //        );
    //        tess.addVertexWithUV(
    //            (double) p2[0],
    //            (double) p2[1],
    //            (double) p2[2],
    //            (double) p2[3],
    //            (double) p2[4]
    //        );
    //        tess.addVertexWithUV(
    //            (double) p3[0],
    //            (double) p3[1],
    //            (double) p3[2],
    //            (double) p3[3],
    //            (double) p3[4]
    //        );
    //        tess.addVertexWithUV(
    //            (double) p4[0],
    //            (double) p4[1],
    //            (double) p4[2],
    //            (double) p4[3],
    //            (double) p4[4]
    //        );
    //    }
    //}

    // TODO: WTF
    //public float[] p(float a, float b, float c) {
    //    float[] t = new float[] { a, b, c };
    //    return t;
    //}

    //public float[] t(float a, float b) {
    //    float[] t = new float[] { a, b };
    //    return t;
    //}

    // TODO: WTF
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
    //    renderer.setRenderBounds(
    //        this.getForward() == ForgeDirection.WEST ? 9.999999747378752E-5 : 0.0,
    //        this.getForward() == ForgeDirection.DOWN ? 9.999999747378752E-5 : 0.0,
    //        this.getForward() == ForgeDirection.NORTH ? 9.999999747378752E-5 : 0.0,
    //        this.getForward() == ForgeDirection.EAST ? 0.9998999834060669 : 1.0,
    //        this.getForward() == ForgeDirection.UP ? 0.9998999834060669 : 1.0,
    //        this.getForward() == ForgeDirection.SOUTH ? 0.9998999834060669 : 1.0
    //    );
    //    renderer.uvRotateTop = 0;
    //    renderer.uvRotateBottom = 3;
    //    renderer.uvRotateEast = 1;
    //    renderer.uvRotateNorth = 2;
    //    renderer.uvRotateSouth = 1;
    //    renderer.uvRotateWest = 2;
    //    renderer.renderStandardBlock(block, x, y, z);
    //    renderer.uvRotateBottom = renderer.uvRotateEast = renderer.uvRotateNorth
    //        = renderer.uvRotateSouth = renderer.uvRotateTop = renderer.uvRotateWest
    //        = 0;
    //    renderer.setRenderBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
    //    IIcon icon = AppEngTextureRegistry.Blocks.BlockFrame.get();
    //    float offsetPerPixel = 0.0625F;
    //    boolean rev;
    //    float zX;
    //    if (this.getForward() == ForgeDirection.NORTH
    //        || this.getForward() == ForgeDirection.SOUTH) {
    //        rev = this.getForward() == ForgeDirection.NORTH;
    //        zX = 1.0F;
    //        if (rev) {
    //            zX = 0.0F;
    //        }

    //        if (!this.linesUp(x - 1, y, z)) {
    //            this.renderTexturedPlate(
    //                this.p((float) x, (float) y, (float) z + zX),
    //                this.p(offsetPerPixel, 0.0F, 0.0F),
    //                this.p(0.0F, 1.0F, 0.0F),
    //                this.t(0.0F, 0.0F),
    //                this.t(1.0F, 16.0F),
    //                icon,
    //                x,
    //                y,
    //                z + (rev ? -1 : 1),
    //                rev
    //            );
    //        }

    //        if (!this.linesUp(x + 1, y, z)) {
    //            this.renderTexturedPlate(
    //                this.p((float) x + 15.0F * offsetPerPixel, (float) y, (float) z +
    //                zX), this.p(offsetPerPixel, 0.0F, 0.0F), this.p(0.0F, 1.0F, 0.0F),
    //                this.t(15.0F, 0.0F),
    //                this.t(1.0F, 16.0F),
    //                icon,
    //                x,
    //                y,
    //                z + (rev ? -1 : 1),
    //                rev
    //            );
    //        }

    //        if (!this.linesUp(x, y - 1, z)) {
    //            this.renderTexturedPlate(
    //                this.p((float) x, (float) y, (float) z + zX),
    //                this.p(1.0F, 0.0F, 0.0F),
    //                this.p(0.0F, offsetPerPixel, 0.0F),
    //                this.t(0.0F, 0.0F),
    //                this.t(16.0F, 1.0F),
    //                icon,
    //                x,
    //                y,
    //                z + (rev ? -1 : 1),
    //                rev
    //            );
    //        }

    //        if (!this.linesUp(x, y + 1, z)) {
    //            this.renderTexturedPlate(
    //                this.p((float) x, (float) y + 15.0F * offsetPerPixel, (float) z +
    //                zX), this.p(1.0F, 0.0F, 0.0F), this.p(0.0F, offsetPerPixel, 0.0F),
    //                this.t(0.0F, 15.0F),
    //                this.t(16.0F, 1.0F),
    //                icon,
    //                x,
    //                y,
    //                z + (rev ? -1 : 1),
    //                rev
    //            );
    //        }
    //    }

    //    if (this.getForward() == ForgeDirection.EAST
    //        || this.getForward() == ForgeDirection.WEST) {
    //        rev = this.getForward() == ForgeDirection.EAST;
    //        zX = 0.0F;
    //        if (rev) {
    //            zX = 1.0F;
    //        }

    //        if (!this.linesUp(x, y, z - 1)) {
    //            this.renderTexturedPlate(
    //                this.p((float) x + zX, (float) y, (float) z),
    //                this.p(0.0F, 0.0F, offsetPerPixel),
    //                this.p(0.0F, 1.0F, 0.0F),
    //                this.t(0.0F, 0.0F),
    //                this.t(1.0F, 16.0F),
    //                icon,
    //                x + (rev ? 1 : -1),
    //                y,
    //                z,
    //                rev
    //            );
    //        }

    //        if (!this.linesUp(x, y, z + 1)) {
    //            this.renderTexturedPlate(
    //                this.p((float) x + zX, (float) y, (float) z + 15.0F *
    //                offsetPerPixel), this.p(0.0F, 0.0F, offsetPerPixel),
    //                this.p(0.0F, 1.0F, 0.0F),
    //                this.t(15.0F, 0.0F),
    //                this.t(1.0F, 16.0F),
    //                icon,
    //                x + (rev ? 1 : -1),
    //                y,
    //                z,
    //                rev
    //            );
    //        }

    //        if (!this.linesUp(x, y - 1, z)) {
    //            this.renderTexturedPlate(
    //                this.p((float) x + zX, (float) y, (float) z),
    //                this.p(0.0F, 0.0F, 1.0F),
    //                this.p(0.0F, offsetPerPixel, 0.0F),
    //                this.t(0.0F, 0.0F),
    //                this.t(16.0F, 1.0F),
    //                icon,
    //                x + (rev ? 1 : -1),
    //                y,
    //                z,
    //                rev
    //            );
    //        }

    //        if (!this.linesUp(x, y + 1, z)) {
    //            this.renderTexturedPlate(
    //                this.p((float) x + zX, (float) y + 15.0F * offsetPerPixel, (float)
    //                z), this.p(0.0F, 0.0F, 1.0F), this.p(0.0F, offsetPerPixel, 0.0F),
    //                this.t(0.0F, 15.0F),
    //                this.t(16.0F, 1.0F),
    //                icon,
    //                x + (rev ? 1 : -1),
    //                y,
    //                z,
    //                rev
    //            );
    //        }
    //    }

    //    if (this.getForward() == ForgeDirection.UP
    //        || this.getForward() == ForgeDirection.DOWN) {
    //        rev = this.getForward() == ForgeDirection.UP;
    //        zX = 1.0F;
    //        if (!rev) {
    //            zX = 0.0F;
    //        }

    //        if (!this.linesUp(x - 1, y, z)) {
    //            this.renderTexturedPlate(
    //                this.p((float) x, (float) y + zX, (float) z),
    //                this.p(offsetPerPixel, 0.0F, 0.0F),
    //                this.p(0.0F, 0.0F, 1.0F),
    //                this.t(0.0F, 0.0F),
    //                this.t(1.0F, 16.0F),
    //                icon,
    //                x,
    //                y + (rev ? 1 : -1),
    //                z,
    //                rev
    //            );
    //        }

    //        if (!this.linesUp(x + 1, y, z)) {
    //            this.renderTexturedPlate(
    //                this.p((float) x + 15.0F * offsetPerPixel, (float) y + zX, (float)
    //                z), this.p(offsetPerPixel, 0.0F, 0.0F), this.p(0.0F, 0.0F, 1.0F),
    //                this.t(15.0F, 0.0F),
    //                this.t(1.0F, 16.0F),
    //                icon,
    //                x,
    //                y + (rev ? 1 : -1),
    //                z,
    //                rev
    //            );
    //        }

    //        if (!this.linesUp(x, y, z - 1)) {
    //            this.renderTexturedPlate(
    //                this.p((float) x, (float) y + zX, (float) z),
    //                this.p(1.0F, 0.0F, 0.0F),
    //                this.p(0.0F, 0.0F, offsetPerPixel),
    //                this.t(0.0F, 0.0F),
    //                this.t(16.0F, 1.0F),
    //                icon,
    //                x,
    //                y + (rev ? 1 : -1),
    //                z,
    //                rev
    //            );
    //        }

    //        if (!this.linesUp(x, y, z + 1)) {
    //            this.renderTexturedPlate(
    //                this.p((float) x, (float) y + zX, (float) z + 15.0F *
    //                offsetPerPixel), this.p(1.0F, 0.0F, 0.0F), this.p(0.0F, 0.0F,
    //                offsetPerPixel), this.t(0.0F, 15.0F), this.t(16.0F, 1.0F), icon, x,
    //                y + (rev ? 1 : -1),
    //                z,
    //                rev
    //            );
    //        }
    //    }

    //    return true;
    //}

    // TODO: WTF
    //public boolean isBlockNormalCube() {
    //    return true;
    //}

    //public boolean handleTilePacket(DataInputStream stream) throws IOException {
    //    ForgeDirection oldOrientation = this.getForward();
    //    boolean oldHasPower = super.hasPower;
    //    byte rotation = stream.readByte();
    //    this.getForward() = this.getDirectionFromAERotation(rotation);
    //    byte flags = stream.readByte();
    //    super.hasPower = (flags & 1) == 1;
    //    if ((flags & 2) == 2) {
    //        if (this.Buffer.isEmpty()) {
    //            this.Buffer.add(new ItemStack(Item.field_77669_D));
    //        }
    //    } else {
    //        this.Buffer.clear();
    //    }

    //    return oldHasPower != super.hasPower || oldOrientation != this.getForward();
    //}

    //public void configureTilePacket(DataOutputStream data) throws IOException {
    //    data.writeByte(this.getAERotationFromDirection(this.getForward()));
    //    byte flags = 0;
    //    byte flags = (byte) (flags | (super.hasPower ? 1 : 0));
    //    flags = (byte) (flags | (this.Buffer.isEmpty() ? 0 : 2));
    //    data.writeByte(flags);
    //}

    // TODO: WTF
    //public void addCollidingBlockToList(
    //    World world,
    //    int x,
    //    int y,
    //    int z,
    //    AxisAlignedBB axisalignedbb,
    //    List arraylist,
    //    Entity par7Entity
    //) {
    //    AppEngMultiBlock blk = (AppEngMultiBlock) this.getBlockType();
    //    if (par7Entity instanceof EntityItem && this.isMachineActive()
    //        && this.Buffer.isEmpty()) {
    //        blk.func_71905_a(
    //            this.getForward() == ForgeDirection.WEST ? 0.005F : 0.0F,
    //            this.getForward() == ForgeDirection.DOWN ? 0.005F : 0.0F,
    //            this.getForward() == ForgeDirection.NORTH ? 0.005F : 0.0F,
    //            this.getForward() == ForgeDirection.EAST ? 0.995F : 1.0F,
    //            this.getForward() == ForgeDirection.UP ? 0.995F : 1.0F,
    //            this.getForward() == ForgeDirection.SOUTH ? 0.995F : 1.0F
    //        );
    //        blk.configureCollidingBlockToList(
    //            world, x, y, z, axisalignedbb, arraylist, par7Entity
    //        );
    //        blk.func_71905_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    //    } else {
    //        blk.func_71905_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    //        blk.configureCollidingBlockToList(
    //            world, x, y, z, axisalignedbb, arraylist, par7Entity
    //        );
    //    }
    //}

    // TODO: this is to avoid Z-fighting. Put into renderer?
    //@Override
    //public AxisAlignedBB[] getSelectedBoundingBoxsFromPool(
    //    World world, int x, int y, int z
    //) {
    //    return new AxisAlignedBB[] { AxisAlignedBB.getBoundingBox(
    //        this.getForward() == ForgeDirection.WEST ? 0.004999999888241291 : 0.0,
    //        this.getForward() == ForgeDirection.DOWN ? 0.004999999888241291 : 0.0,
    //        this.getForward() == ForgeDirection.NORTH ? 0.004999999888241291 : 0.0,
    //        this.getForward() == ForgeDirection.EAST ? 0.9950000047683716 : 1.0,
    //        this.getForward() == ForgeDirection.UP ? 0.9950000047683716 : 1.0,
    //        this.getForward() == ForgeDirection.SOUTH ? 0.9950000047683716 : 1.0
    //    ) };
    //}

    //public void setPrimaryOrientation(ForgeDirection s) {
    //    this.orientation = s;
    //    MinecraftForge.EVENT_BUS.post(
    //        new GridTileConnectivityEvent(this, this.getWorld(), this.getLocation())
    //    );
    //}
}
