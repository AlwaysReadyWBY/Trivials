package top.alwaysready.trivials.module.armorstand;

import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import top.alwaysready.trivials.Trivials;
import top.alwaysready.trivials.module.wax.WaxHolder;
import top.alwaysready.trivials.module.Module;
import top.alwaysready.trivials.utils.DilemmaMeta;
import top.alwaysready.trivials.utils.ItemUtils;

public class ArmorStandModule extends Module implements Listener {
    public ArmorStandModule() {
        super("armor_stand");
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

    @EventHandler
    public void onPlayerInteract(PlayerInteractAtEntityEvent ev){
        Player player = ev.getPlayer();
        if(!(ev.getRightClicked() instanceof ArmorStand armorStand)) return;

        WaxHolder wax = DilemmaMeta.WAX_INFO.getOrCreate(armorStand);
        if(!wax.canModify(player)) {
            ev.setCancelled(true);
            return;
        }

        if(player.isSneaking()) return;
        ItemStack stack = player.getInventory().getItem(ev.getHand());
        if(stack == null) return;

        if(armorStand.hasArms() && stack.getType() == Material.SHEARS){
            ev.setCancelled(true);
            armorStand.setArms(false);
            player.getWorld().playSound(armorStand, Sound.ENTITY_SHEEP_SHEAR, SoundCategory.BLOCKS,1,1);
            return;
        }

        if(!armorStand.hasArms() && stack.getType() == Material.STICK
                && ItemUtils.consumeItem(player,ev.getHand(),2)){
            ev.setCancelled(true);
            armorStand.setArms(true);
            player.getWorld().playSound(armorStand, Sound.BLOCK_BAMBOO_PLACE, SoundCategory.BLOCKS,1,1);
            return;
        }

        if(armorStand.isSmall() && stack.getType() == Material.RED_MUSHROOM
                && ItemUtils.consumeItem(player,ev.getHand(),1)){
            ev.setCancelled(true);
            armorStand.setSmall(false);
            player.getWorld().playSound(armorStand, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.BLOCKS,1,1);
            return;
        }

        if(!armorStand.isSmall() && stack.getType() == Material.BROWN_MUSHROOM
                && ItemUtils.consumeItem(player,ev.getHand(),1)){
            ev.setCancelled(true);
            armorStand.setSmall(true);
            player.getWorld().playSound(armorStand, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.BLOCKS,1,1);
            return;
        }

        if(armorStand.hasBasePlate() && Tag.ITEMS_PICKAXES.isTagged(stack.getType())){
            ev.setCancelled(true);
            armorStand.setBasePlate(false);
            player.getWorld().playSound(armorStand, Sound.BLOCK_STONE_BREAK, SoundCategory.BLOCKS,1,1);
            return;
        }

        if(!armorStand.hasBasePlate() && stack.getType() == Material.SMOOTH_STONE_SLAB){
            ev.setCancelled(true);
            armorStand.setBasePlate(true);
            player.getWorld().playSound(armorStand, Sound.BLOCK_STONE_PLACE, SoundCategory.BLOCKS,1,1);
            return;
        }

        if(armorStand.isVisible() && stack.getType() == Material.PHANTOM_MEMBRANE
                && ItemUtils.consumeItem(player,ev.getHand(),1)){
            ev.setCancelled(true);
            armorStand.setVisible(false);
            player.getWorld().playSound(armorStand, Sound.ITEM_GLOW_INK_SAC_USE, SoundCategory.BLOCKS,1,1);
            return;
        }

        if(!armorStand.isVisible() && stack.getType() == Material.MILK_BUCKET){
            ev.setCancelled(true);
            armorStand.setVisible(true);
            player.getWorld().playSound(armorStand, Sound.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS,1,1);
            player.getInventory().setItem(ev.getHand(),new ItemStack(Material.BUCKET));
            return;
        }

        if(armorStand.hasGravity() && stack.getType() == Material.GHAST_TEAR
                && ItemUtils.consumeItem(player,ev.getHand(),1)){
            ev.setCancelled(true);
            armorStand.setGravity(false);
            player.getWorld().playSound(armorStand,Sound.BLOCK_ENCHANTMENT_TABLE_USE,SoundCategory.BLOCKS,1,1);
            return;
        }

        if(wax.getWaxOwner() == null && stack.getType() == Material.HONEYCOMB
                && ItemUtils.consumeItem(player,ev.getHand(),1)){
            ev.setCancelled(true);
            wax.setWaxOwner(armorStand, player.getUniqueId());
            armorStand.setInvulnerable(true);
            player.getWorld().playSound(armorStand,Sound.ITEM_HONEYCOMB_WAX_ON,SoundCategory.BLOCKS,1,1);
            return;
        }

        if(wax.getWaxOwner() != null && Tag.ITEMS_AXES.isTagged(stack.getType())){
            ev.setCancelled(true);
            wax.setWaxOwner(armorStand, null);
            armorStand.setInvulnerable(false);
            player.getWorld().playSound(armorStand, Sound.ITEM_AXE_WAX_OFF, SoundCategory.BLOCKS, 1, 1);
            return;
        }
    }
}
