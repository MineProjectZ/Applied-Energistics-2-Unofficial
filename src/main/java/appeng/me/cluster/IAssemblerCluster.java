package appeng.me.cluster;

import net.minecraft.tileentity.TileEntity;

public interface IAssemblerCluster extends IAECluster {
   boolean canCraft();

   TileEntity getAssembler(int var1);

   int getLastOffset();

   void setLastOffset(int var1);
}
