package top.alwaysready.trivials.module;

import org.bukkit.configuration.ConfigurationSection;
import top.alwaysready.trivials.module.armorstand.ArmorStandModule;
import top.alwaysready.trivials.module.casing.CasingModule;
import top.alwaysready.trivials.module.itemframe.ItemFrameModule;
import top.alwaysready.trivials.module.wax.WaxModule;

import java.util.Hashtable;
import java.util.Map;

public class ModuleManager {
    private final Map<String,Module> moduleMap = new Hashtable<>();

    public ModuleManager(){
        registerModule(new ItemFrameModule());
        registerModule(new ArmorStandModule());
        registerModule(new CasingModule());
        registerModule(new WaxModule());
    }

    public void registerModule(Module module){
        moduleMap.put(module.getKey(),module);
    }

    public void loadModuleConfig(ConfigurationSection sec){
        if(sec == null) return;
        moduleMap.values().forEach(module -> module.loadConfig(sec.getConfigurationSection(module.getKey())));
    }
}
