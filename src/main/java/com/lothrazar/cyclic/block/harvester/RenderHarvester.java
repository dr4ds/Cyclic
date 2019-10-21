package com.lothrazar.cyclic.block.harvester;

import com.lothrazar.cyclic.util.RenderUtil;
import com.lothrazar.cyclic.util.RenderUtil.LaserConfig;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;

public class RenderHarvester extends TileEntityRenderer<TileHarvester> {

  static final float[] laserColor = new float[] { 0.99F, 0F, 0F };
  static final double rotationTime = 0;
  static final double beamWidth = 0.02;
  static final float alpha = 0.9F;

  @Override
  public void renderTileEntityFast(TileHarvester te, double x, double y, double z, float partialTicks, int destroyStage, net.minecraft.client.renderer.BufferBuilder buffer) {
    //test 
    RenderUtil.renderLaser(new LaserConfig(te.getPos().east(3), te.getPos(),
        rotationTime, alpha, beamWidth, laserColor));
  }
}