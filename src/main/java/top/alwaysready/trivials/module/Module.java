package top.alwaysready.trivials.module;

import org.bukkit.configuration.ConfigurationSection;

public abstract class Module {

    private final String key;
    private boolean enabled;

    protected Module(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void enable(){
        if(!isEnabled()) onEnable();
        enabled = true;
    }

    public void disable(){
        if(isEnabled()) onDisable();
        enabled = false;
    }

    public abstract void onEnable();
    public abstract void onDisable();

    public boolean loadConfig(ConfigurationSection sec) {
        return sec != null && sec.getBoolean("enabled", false);
    }
}
