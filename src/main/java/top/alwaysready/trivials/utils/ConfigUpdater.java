package top.alwaysready.trivials.utils;

import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class ConfigUpdater {
    public static final ConfigUpdater MAIN = new ConfigUpdater(){
        @Override
        protected void init() {
            addVersion(sec -> {
                sec.createSection("modules").createSection("item_frame").set("enabled",true);
            });
            addVersion(sec -> sec.getConfigurationSection("modules").createSection("armor_stand").set("enabled",true));
            addVersion(sec -> {
                ConfigurationSection moduleSec = sec.getConfigurationSection("modules");
                moduleSec.createSection("casing").set("enabled",true);
                moduleSec.createSection("wax").set("enabled",true);
            });
            addVersion(sec -> {
                ConfigurationSection moduleSec = sec.getConfigurationSection("modules");
                moduleSec.createSection("note_block").set("enabled",true);
            });
        }
    };

    private final List<Consumer<ConfigurationSection>> update = new ArrayList<>();

    protected ConfigUpdater(){
        init();
    }

    protected abstract void init();

    protected void addVersion(Consumer<ConfigurationSection> updater){
        update.add(updater);
    }

    public Result update(ConfigurationSection sec){
        int version = sec.getInt("version",1);
        if(version>=update.size()) return Result.UP_TO_DATE;
        try {
            for (int i = version; i < update.size(); i++) {
                update.get(i).accept(sec);
            }
            sec.set("version",update.size());
            return Result.UPDATED;
        } catch (Exception e){
            return Result.FAILED;
        }
    }

    public enum Result{
        UP_TO_DATE,
        UPDATED,
        FAILED
    }
}
