package top.alwaysready.trivials.module.casing;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import top.alwaysready.trivials.Trivials;
import top.alwaysready.trivials.module.Module;

import java.util.Collection;

public class CasingModule extends Module implements Listener {
    public CasingModule() {
        super("casing");
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, Trivials.getInstance());
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
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

    private boolean isGlass(Material type){
        return String.valueOf(type.getKey()).contains("glass");
    }

    private void encaseItem(Item item,Block block,double spread){
        Location loc = block.getLocation().clone()
                .add(0.5,0.5,0.5)
                .add(spread*(Math.random()-0.5),spread*(Math.random()-0.5),spread*(Math.random()-0.5));
        ItemDisplay disp = item.getWorld().spawn(loc, ItemDisplay.class);
        disp.setTransformation(new Transformation(
                new Vector3f(0,0,0),
                new AxisAngle4f((float) (Math.random()*Math.PI*2),0,1,0),
                new Vector3f(0.5f,0.5f,0.5f),
                new AxisAngle4f(0,0,1,0)
        ));
        disp.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.GUI);
        disp.setBillboard(Display.Billboard.CENTER);
        disp.setItemStack(item.getItemStack().clone());
        disp.getScoreboardTags().add("tdilemma:casing");
        item.remove();
    }

    private void releaseItem(ItemDisplay disp){
        disp.getWorld().dropItemNaturally(disp.getLocation(),disp.getItemStack().clone());
        disp.remove();
    }

    public void onBlockPlace(Block block,Material type){
        if(!isGlass(type)) return;
        Collection<Entity> items = block.getWorld().getNearbyEntities(block.getBoundingBox(), Item.class::isInstance);
        double spread = Math.min(0.25,(items.size()-1)*0.05);
        items.forEach(item -> encaseItem((Item) item,block,spread));
    }

    public void onBlockBreak(Block block,Material type){
        if(!isGlass(type)) return;
        block.getWorld().getNearbyEntities(block.getBoundingBox(), e -> e instanceof ItemDisplay
                && e.getScoreboardTags().contains("tdilemma:casing"))
                .forEach(entity -> releaseItem((ItemDisplay) entity));
    }

    @EventHandler(priority = EventPriority.MONITOR,ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent ev){
        onBlockPlace(ev.getBlock(),ev.getBlockPlaced().getType());
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
}
