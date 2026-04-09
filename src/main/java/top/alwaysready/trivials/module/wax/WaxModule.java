package top.alwaysready.trivials.module.wax;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import top.alwaysready.trivials.Trivials;
import top.alwaysready.trivials.module.Module;
import top.alwaysready.trivials.utils.DilemmaMeta;
import top.alwaysready.trivials.utils.ItemUtils;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

public class WaxModule extends Module implements Listener {
    private static final Set<EntityType> APPLICABLE_TYPES = EnumSet.noneOf(EntityType.class);

    static{
        APPLICABLE_TYPES.add(EntityType.PAINTING);
        APPLICABLE_TYPES.add(EntityType.LEASH_KNOT);
        APPLICABLE_TYPES.add(EntityType.END_CRYSTAL);
        APPLICABLE_TYPES.add(EntityType.BAMBOO_CHEST_RAFT);
        APPLICABLE_TYPES.add(EntityType.BAMBOO_RAFT);
        Arrays.stream(EntityType.values())
                .filter(type -> {
                    String key = type.name().toLowerCase();
                    return key.contains("minecart") || key.contains("boat");
                })
                .forEach(APPLICABLE_TYPES::add);
    }

    public WaxModule() {
        super("wax");
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

    private boolean isWaxApplicable(EntityType type){
        return APPLICABLE_TYPES.contains(type);
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent ev){
        Entity entity = ev.getRightClicked();
        if(!isWaxApplicable(entity.getType())) return;
        WaxHolder wax = DilemmaMeta.WAX_INFO.getOrCreate(entity);
        if(!wax.canModify(ev.getPlayer())) return;
        if(ev.getPlayer().isSneaking()) return;
        ItemStack stack = ev.getPlayer().getInventory().getItem(ev.getHand());
        if(stack == null) return;
        if(wax.getWaxOwner() == null && stack.getType() == Material.HONEYCOMB
                && ItemUtils.consumeItem(ev.getPlayer(),ev.getHand(),1)){
            ev.setCancelled(true);
            wax.setWaxOwner(entity,ev.getPlayer().getUniqueId());
            entity.setInvulnerable(true);
            return;
        }
        if(wax.getWaxOwner() != null && Tag.ITEMS_AXES.isTagged(stack.getType())) {
            ev.setCancelled(true);
            wax.setWaxOwner(entity, null);
            entity.setInvulnerable(false);
            return;
        }
    }
}
