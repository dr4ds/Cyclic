package com.lothrazar.cyclicmagic.registry;
import com.lothrazar.cyclicmagic.enchantment.EnchantLaunch;
import com.lothrazar.cyclicmagic.enchantment.EnchantLifeLeech;
import com.lothrazar.cyclicmagic.enchantment.EnchantMagnet;
import com.lothrazar.cyclicmagic.enchantment.EnchantVenom;
import com.lothrazar.cyclicmagic.util.Const;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;

public class EnchantRegistry {
  public static EnchantLaunch launch;
  public static EnchantMagnet magnet;
  public static EnchantVenom venom;
  public static EnchantLifeLeech lifeleech;
  public static int launchid;
  public static int magnetid;
  public static int venomid;
  public static int lifeleechid=89;
  public static void register() {
    launch = new EnchantLaunch();
    magnet = new EnchantMagnet();
    venom = new EnchantVenom();
    Enchantment.REGISTRY.register(launchid, new ResourceLocation(launch.getName()), launch);
    Enchantment.REGISTRY.register(magnetid, new ResourceLocation(magnet.getName()), magnet);
    Enchantment.REGISTRY.register(venomid, new ResourceLocation(venom.getName()), venom);
    Enchantment.REGISTRY.register(lifeleechid, new ResourceLocation(lifeleech.getName()), lifeleech);
 }
  public static void syncConfig(Configuration c) {
    launchid = c.getInt("enchant.launch.id", Const.ConfigCategory.modpackMisc,
        86, 71, 999, "Id of the launch enchantment (double jump on boots).  Change this if you get id conflicts with other mods.");
    magnetid = c.getInt("enchant.magnet.id", Const.ConfigCategory.modpackMisc,
        87, 71, 999, "Id of the magnet enchantment.  Change this if you get id conflicts with other mods.");
    venomid = c.getInt("enchant.venom.id", Const.ConfigCategory.modpackMisc,
        88, 71, 999, "Id of the venom enchantment.  Change this if you get id conflicts with other mods.");
  }
}
