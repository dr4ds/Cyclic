package com.lothrazar.cyclicmagic;

import org.apache.logging.log4j.Logger;

import com.lothrazar.cyclicmagic.gui.GuiHandlerUncrafting;
import com.lothrazar.cyclicmagic.gui.GuiHandlerWand;
import com.lothrazar.cyclicmagic.proxy.CommonProxy;
import com.lothrazar.cyclicmagic.registry.*;
import com.lothrazar.cyclicmagic.util.Const;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(modid = Const.MODID, useMetadata = true, canBeDeactivated = false, updateJSON = "https://raw.githubusercontent.com/PrinceOfAmber/CyclicMagic/master/update.json", guiFactory = "com.lothrazar." + Const.MODID + ".config.IngameConfigHandler")
public class ModMain{

	@Instance(value = Const.MODID)
	public static ModMain instance;
	@SidedProxy(clientSide = "com.lothrazar." + Const.MODID + ".proxy.ClientProxy", serverSide = "com.lothrazar." + Const.MODID + ".proxy.CommonProxy")
	public static CommonProxy proxy;
	public static Logger logger;
	private static Configuration config;
	public static SimpleNetworkWrapper network;
	public final static CreativeTabs TAB = new CreativeTabs(Const.MODID) {
		@Override
		public Item getTabIconItem(){
			return ItemRegistry.chest_sack;
		}
	};
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent event){

		logger = event.getModLog();

		config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		syncConfig();

		network = NetworkRegistry.INSTANCE.newSimpleChannel(Const.MODID);

		EventRegistry.register();
		
		ReflectionRegistry.register();
		
		PacketRegistry.register(network);
	}

	@EventHandler
	public void onInit(FMLInitializationEvent event){

		ItemRegistry.register();
		BlockRegistry.register();
		SpellRegistry.register();
		PotionRegistry.register();
		MobSpawningRegistry.register();
		WorldGenRegistry.register();
		FuelRegistry.register();
		
		if(StackSizeRegistry.enabled){
			StackSizeRegistry.register();
		}
		if(RecipeAlterRegistry.enabled){
			RecipeAlterRegistry.register();
		}
		if(RecipeNewRegistry.enabled){
			RecipeNewRegistry.register();
		}
		
		proxy.register();

		TileEntityRegistry.register();

		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandlerWand());
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandlerUncrafting());
		
		ProjectileRegistry.register(event);
	}

	@EventHandler
	public void onPostInit(FMLPostInitializationEvent event){
	
		//registers all plantable crops. the plan is to work with non vanilla data also
		DispenserBehaviorRegistry.register();
		
	}
	
	@EventHandler
	public void onServerStarting(FMLServerStartingEvent event){
		CommandRegistry.register(event);
	}

	public static Configuration getConfig(){
		return config;
	}

	public static void syncConfig(){
		//hit on startup and on change event from 
		Configuration c = getConfig();
		WorldGenRegistry.syncConfig(c);
		PotionRegistry.syncConfig(c);
		EventRegistry.syncConfig(c);
		BlockRegistry.syncConfig(c);
		ItemRegistry.syncConfig(c);
		MobSpawningRegistry.syncConfig(c);
		RecipeAlterRegistry.syncConfig(c);
		RecipeNewRegistry.syncConfig(c);
		DispenserBehaviorRegistry.syncConfig(c);
		StackSizeRegistry.syncConfig(c);
		SpellRegistry.syncConfig(c);
		
		c.save();
	}
	
/* 
 * 
 * 
 //BUG: spells get casted even if you have zero mana 
  * 
  * BUG: nether ore does not drop eXP
  * 
  * ender book - addInformation about waypoints - count of them?
 * 
 * 
 * SPELL: bring back ghost - let it put you in new location but only if air blocks
 * 
 *disable entire wand in config
 *OR
 * --- COST of each spell in config !!! 
 * 
1. text message if we use a build spell but invo is empty
- max and regen in nbt, not config
 
4. chest give failure message text (only useable on a container)

regen space in text
set maximum function/button
  
//IDEA: make boats float
 * https://www.reddit.com/r/minecraftsuggestions/comments/4d4ob1/make_boats_float_again/
 
 
 
	public String getTranslatedName();
 
 https://www.reddit.com/r/minecraftsuggestions/comments/4chlpo/add_a_control_option_for_elytra_automatically/
 

 //do we need custom ItemBlocks for these?
		//top logs recipe

		//smoothstone block
		 //mushroomies?
 
 
 idea: make ladders faster
 
 */
}
