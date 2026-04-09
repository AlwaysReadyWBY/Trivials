package top.alwaysready.trivials;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import top.alwaysready.trivials.module.ModuleManager;
import top.alwaysready.trivials.module.noteblock.*;
import top.alwaysready.trivials.utils.ConfigUpdater;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public final class Trivials extends JavaPlugin {

    private static Trivials instance;

    public static Trivials getInstance() {
        return instance;
    }

    private ModuleManager moduleManager;

    @Override
    public void onEnable() {
        instance = this;
        loadConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void loadConfig() {
        getDataFolder().mkdirs();
        saveResource("config.yml", false);
        File file = getDataFolder().toPath().resolve("config.yml").toFile();
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        switch (ConfigUpdater.MAIN.update(cfg)) {
            case FAILED -> {
                saveResource("config.yml", true);
                cfg = YamlConfiguration.loadConfiguration(file);
            }
            case UPDATED -> {
                try {
                    cfg.save(file);
                } catch (IOException e) {
                    Trivials.getInstance().getLogger().log(Level.WARNING,"Failed to save config.yml",e);
                }
            }
        }
        getModuleManager().loadModuleConfig(cfg.getConfigurationSection("modules"));
        getLogger().info("Reloaded!");
    }

    public ModuleManager getModuleManager() {
        if (moduleManager == null) moduleManager = new ModuleManager();
        return moduleManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("trivials.admin")) return false;
        switch (label){
            case "trivialreload"->{
                loadConfig();
                sender.sendMessage("Reloaded");
            }
            case "trivialdebug"->{
                if(!(sender instanceof Player p)) return false;
                if(args.length==0) return false;
                NoteNode note = new NotesBuilder()
                        .parse(String.join(" ", args))
                        .build();
                NoteData data = new NoteData();
                data.setId("debug");
                data.setSound("minecraft:block.note_block.pling");
                data.setTitle("debug");
                data.setFirst(note);
                PlayingNotes playing = new PlayingNotes()
                        .setLocation(p.getLocation())
                        .setData(data)
                        .setSound(data.getSound());
                playing.start();
                getModuleManager().getModule(NoteBlockModule.class).ifPresent(module -> {
                    module.addPlayingNote(data.getId(),playing);
                });
            }
        }
        return true;
    }
}
