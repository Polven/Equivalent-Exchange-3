package ee3.common;

import net.minecraft.src.CreativeTabs;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import ee3.common.block.ModBlocks;
import ee3.common.core.CommonProxy;
import ee3.common.core.CreativeTabEE3;
import ee3.common.core.handlers.AddonHandler;
import ee3.common.core.handlers.ConfigurationHandler;
import ee3.common.core.handlers.EntityLivingHandler;
import ee3.common.core.handlers.FuelHandler;
import ee3.common.core.handlers.ItemPickupHandler;
import ee3.common.core.handlers.LocalizationHandler;
import ee3.common.core.handlers.PacketHandler;
import ee3.common.core.handlers.PlayerDestroyItemHandler;
import ee3.common.core.handlers.VersionCheckTickHandler;
import ee3.common.core.helper.LogHelper;
import ee3.common.core.helper.VersionHelper;
import ee3.common.item.ModItems;
import ee3.common.lib.ConfigurationSettings;
import ee3.common.lib.Reference;
import ee3.common.recipe.RecipesTransmutationStone;

/**
 * EquivalentExchange3
 * 
 * Main mod class for the Minecraft mod Equivalent Exchange 3
 * 
 * @author pahimar
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 * 
 */
@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION)
@NetworkMod(channels = { Reference.CHANNEL_NAME }, clientSideRequired = true, serverSideRequired = false, packetHandler = PacketHandler.class)
public class EquivalentExchange3 {

    @Instance(Reference.MOD_ID)
    public static EquivalentExchange3 instance;

    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
    public static CommonProxy proxy;
    
    public static CreativeTabs tabsEE3 = new CreativeTabEE3(CreativeTabs.getNextID(), Reference.MOD_ID);

    @PreInit
    public void preInit(FMLPreInitializationEvent event) {

    	// Initialize the log helper
        LogHelper.init();
        
    	// Load the localization files into the LanguageRegistry
    	LocalizationHandler.loadLanguages();
    	
        // Initialize the configuration
        ConfigurationHandler.init(event.getSuggestedConfigurationFile());
        
        // Conduct the version check and log the result
        if (ConfigurationSettings.ENABLE_VERSION_CHECK) {
        	VersionHelper.checkVersion();
        }
    	VersionHelper.logResult();
        
        // Initialize the Version Check Tick Handler (Client only)
        TickRegistry.registerTickHandler(new VersionCheckTickHandler(), Side.CLIENT);
        
        // Register the KeyBinding Handler (Client only)
        proxy.registerKeyBindingHandler();

        // Register the Sound Handler (Client only)
        proxy.registerSoundHandler();
        
    }

    @Init
    public void load(FMLInitializationEvent event) {
    	
        // Initialize the custom item rarity types
        proxy.initCustomRarityTypes();

        // Register the GUI Handler
        NetworkRegistry.instance().registerGuiHandler(instance, proxy);

        // Register the PlayerDestroyItem Handler
        MinecraftForge.EVENT_BUS.register(new PlayerDestroyItemHandler());
        
        // Register the Item Pickup Handler
        MinecraftForge.EVENT_BUS.register(new ItemPickupHandler());

        // Register the EntityLiving Handler
        MinecraftForge.EVENT_BUS.register(new EntityLivingHandler());

        // Initialize mod blocks
        ModBlocks.init();
        
        // Initialize mod items
        ModItems.init();
        
        // Initialize mod tile entities
        proxy.initTileEntities();
        
        // Initialize custom rendering and pre-load textures (Client only)
        proxy.initRenderingAndTextures();
        
        // Load the Transmutation Stone recipes
        RecipesTransmutationStone.init();
        
        // Register the Fuel Handler
        GameRegistry.registerFuelHandler(new FuelHandler());

    }

    @PostInit
    public void modsLoaded(FMLPostInitializationEvent event) {

        // Initialize the Addon Handler
        AddonHandler.init(); 

    }
}
