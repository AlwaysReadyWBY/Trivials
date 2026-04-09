package top.alwaysready.trivials.module;

import org.bukkit.configuration.ConfigurationSection;
import top.alwaysready.trivials.module.armorstand.ArmorStandModule;
import top.alwaysready.trivials.module.casing.CasingModule;
import top.alwaysready.trivials.module.itemframe.ItemFrameModule;
import top.alwaysready.trivials.module.noteblock.NoteBlockModule;
import top.alwaysready.trivials.module.wax.WaxModule;

import java.util.Hashtable;
import java.util.Map;
import java.util.Optional;

public class ModuleManager {
    private final Map<String,Module> moduleMap = new Hashtable<>();
    private final Map<Class<?>,Module> typeModuleMap = new Hashtable<>();

    public ModuleManager(){
        registerModule(new ItemFrameModule());
        registerModule(new ArmorStandModule());
        registerModule(new CasingModule());
        registerModule(new WaxModule());
        registerModule(new NoteBlockModule());
    }
    
    public <T extends Module> Optional<T> getModule(Class<T> clazz){
        Module module = typeModuleMap.get(clazz);
        return clazz.isInstance(module)?Optional.of(clazz.cast(module)):Optional.empty();
    }

    public void registerModule(Module module){
        moduleMap.put(module.getKey(),module);
        typeModuleMap.put(module.getClass(),module);
    }

    public void loadModuleConfig(ConfigurationSection sec){
        if(sec == null) return;
        moduleMap.values().forEach(module -> module.loadConfig(sec.getConfigurationSection(module.getKey())));
    }
}
