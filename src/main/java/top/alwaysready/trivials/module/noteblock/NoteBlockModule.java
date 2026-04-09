package top.alwaysready.trivials.module.noteblock;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.WritableBookMeta;
import org.bukkit.scheduler.BukkitTask;
import top.alwaysready.trivials.Trivials;
import top.alwaysready.trivials.module.Module;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NoteBlockModule extends Module implements Listener {

    private long lastTick;
    private BukkitTask task;
    private final Map<String,PlayingNotes> playingNotesMap = new ConcurrentHashMap<>();

    public NoteBlockModule() {
        super("note_block");
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, Trivials.getInstance());
        lastTick = System.currentTimeMillis();
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(Trivials.getInstance(),this::tick,1,1);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        if(task!=null){
            task.cancel();
            task = null;
        }
    }

    @Override
    public boolean loadConfig(ConfigurationSection sec) {
        if(!super.loadConfig(sec)){
            disable();
            return false;
        }
        //TODO load configuration
        enable();
        return true;
    }

    @EventHandler(ignoreCancelled = true)
    public void onNotePlay(NotePlayEvent ev){
        Block block = ev.getBlock();
        if(!(block.getBlockData() instanceof NoteBlock)) return;
        NoteData.get(block).ifPresent(data -> {
            ev.setCancelled(true);
            PlayingNotes notes = new PlayingNotes();
            notes.setData(data);
            notes.setSound(data.getSound()==null?
                    String.valueOf(ev.getInstrument().getSound().getKey())
                    :data.getSound());
            notes.setLocation(block.getLocation().clone().add(0.5,0.5,0.5));
            notes.start();
            addPlayingNote(data.getId(),notes);
        });
    }

    @EventHandler(priority = EventPriority.MONITOR,ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent ev){
        onBlockBreak(ev.getBlock(),ev.getBlock().getType());
    }

    @EventHandler(priority = EventPriority.MONITOR,ignoreCancelled = true)
    public void onPiston(BlockPistonExtendEvent ev){
        ev.getBlocks().forEach(block -> onBlockBreak(block,block.getType()));
    }

    @EventHandler(priority = EventPriority.MONITOR,ignoreCancelled = true)
    public void onPiston(BlockPistonRetractEvent ev){
        ev.getBlocks().forEach(block -> onBlockBreak(block,block.getType()));
    }

    @EventHandler(priority = EventPriority.LOW,ignoreCancelled = true)
    public void onPlayerInteractBlock(PlayerInteractEvent ev){
        if(ev.getAction()!= Action.LEFT_CLICK_BLOCK) return;
        if(!(ev.getClickedBlock().getBlockData() instanceof NoteBlock)) return;
        ItemStack item = ev.getItem();
        if(item == null) return;
        switch (item.getType()){
            case NAME_TAG->{
                setTitle(ev.getClickedBlock(),item.hasItemMeta()?item.getItemMeta().getDisplayName():null);
                ev.setCancelled(true);
                inform(ev.getPlayer());
                return;
            }
            case PAPER->{
                setSound(ev.getClickedBlock(),item.hasItemMeta()?item.getItemMeta().getDisplayName():null);
                ev.setCancelled(true);
                inform(ev.getPlayer());
                return;
            }
        }
        if(!(item.getItemMeta() instanceof WritableBookMeta book)) return;
        ev.setCancelled(true);
        setNotes(ev.getClickedBlock(), String.join("", book.getPages()));
        inform(ev.getPlayer());
    }

    public void onBlockBreak(Block block, Material type){
        if(!(block.getBlockData() instanceof NoteBlock)) return;
        NoteData.remove(block);
    }

    private void tick(){
        long now = System.currentTimeMillis();
        List<Runnable> post = new LinkedList<>();
        playingNotesMap.values().forEach(notes -> notes.elapse(now-lastTick,post));
        playingNotesMap.values().removeIf(PlayingNotes::isStopped);
        Bukkit.getScheduler().runTask(Trivials.getInstance(),()->post.forEach(Runnable::run));
        lastTick = now;
    }

    public void addPlayingNote(String id,PlayingNotes notes){
        playingNotesMap.put(id, notes);
    }

    public void setNotes(Block block,String notes){
        NoteData data = NoteData.getOrCreate(block);
        data.setFirst(new NotesBuilder().parse(notes).build());
        data.save(data.getMarker().getPersistentDataContainer());
    }

    public void setTitle(Block block,String title){
        NoteData data = NoteData.getOrCreate(block);
        data.setTitle(title);
        data.save(data.getMarker().getPersistentDataContainer());
    }

    public void setSound(Block block,String sound){
        NoteData data = NoteData.getOrCreate(block);
        data.setSound(sound!=null && sound.indexOf(':')<0? "minecraft:"+sound:sound);
        data.save(data.getMarker().getPersistentDataContainer());
    }

    private void inform(Player player){
        player.playSound(player, "minecraft:block.note_block.pling",1f,1f);
    }
}
