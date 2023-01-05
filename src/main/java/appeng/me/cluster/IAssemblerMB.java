package appeng.me.cluster;

import appeng.api.util.WorldCoord;

public interface IAssemblerMB extends IAEMultiBlock {
   WorldCoord getLocation();

   IAssemblerCluster getCluster();

   void updateStatus(IAssemblerCluster var1);

   boolean isComplete();

   void calculateMultiblock();
}
