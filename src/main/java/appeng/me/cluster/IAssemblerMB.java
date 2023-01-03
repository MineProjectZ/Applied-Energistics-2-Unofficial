package appeng.me.cluster;

import appeng.api.util.WorldCoord;

public interface IAssemblerMB {
   WorldCoord getLocation();

   IAssemblerCluster getCluster();

   void updateStatus(IAssemblerCluster var1);

   boolean isComplete();

   void calculateMultiblock();
}
