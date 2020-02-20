package com.lothrazar.cyclic.setup;

import java.nio.file.Path;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.lothrazar.cyclic.ModCyclic;
import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigHandler {

  private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
  public static ForgeConfigSpec COMMON_CONFIG;
  public static ForgeConfigSpec.BooleanValue TOOLTIPS;
  static {
    initConfig();
  }

  private static void initConfig() {
    COMMON_BUILDER.comment("General settings").push(ModCyclic.MODID);
    TOOLTIPS = COMMON_BUILDER.comment("Testing only, this does nothing").define("itemTooltip", true);
    COMMON_BUILDER.pop();
    COMMON_CONFIG = COMMON_BUILDER.build();
  }

  public static boolean tooltipsEnabled() {
    return TOOLTIPS.get();
  }

  public static void loadConfig(Path path) {
    final CommentedFileConfig configData = CommentedFileConfig.builder(path)
        .sync()
        .autosave()
        .writingMode(WritingMode.REPLACE)
        .build();
    configData.load();
    COMMON_CONFIG.setConfig(configData);
  }
}