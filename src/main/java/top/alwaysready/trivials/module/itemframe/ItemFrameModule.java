package top.alwaysready.trivials.module.itemframe;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import top.alwaysready.trivials.Trivials;
import top.alwaysready.trivials.module.wax.WaxHolder;
import top.alwaysready.trivials.module.Module;
import top.alwaysready.trivials.utils.DilemmaMeta;
import top.alwaysready.trivials.utils.ItemUtils;

public class ItemFrameModule extends Module implements Listener {


    public ItemFrameModule() {
        super("item_frame");
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

    private void applyInvisibility(ItemFrame frame){
        frame.getWorld().playSound(frame, Sound.ITEM_GLOW_INK_SAC_USE, SoundCategory.BLOCKS,1,1);
        frame.setVisible(false);
    }

    private void onPlayerInteractItemFrame(ItemFrame frame,PlayerInteractEntityEvent ev){
        Player player = ev.getPlayer();
        WaxHolder wax = DilemmaMeta.WAX_INFO.getOrCreate(frame);
        if(!wax.canModify(player)){
            ev.setCancelled(true);
            return;
        }
        if(player.isSneaking()) return;
        ItemStack stack = player.getInventory().getItem(ev.getHand());
        if(stack == null) return;

        if(stack.getType() == Material.PHANTOM_MEMBRANE
                && frame.isVisible()
                && ItemUtils.consumeItem(player,ev.getHand(),1)){
            ev.setCancelled(true);
            applyInvisibility(frame);
            return;
        }

        if(stack.getType() == Material.MILK_BUCKET
                && (!frame.isVisible() || frame.isFixed())){
            ev.setCancelled(true);
            frame.setVisible(true);
            frame.setFixed(false);
            player.getWorld().playSound(frame, Sound.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS,1,1);
            player.getInventory().setItem(ev.getHand(),new ItemStack(Material.BUCKET));
            return;
        }

        if(stack.getType() == Material.GHAST_TEAR && !frame.isFixed()
                && ItemUtils.consumeItem(player,ev.getHand(),1)){
            ev.setCancelled(true);
            frame.setFixed(true);
            player.getWorld().playSound(frame,Sound.BLOCK_ENCHANTMENT_TABLE_USE,SoundCategory.BLOCKS,1,1);
            return;
        }

        if(wax.getWaxOwner() == null && stack.getType() == Material.HONEYCOMB && ItemUtils.consumeItem(player,ev.getHand(),1)) {
            ev.setCancelled(true);
            wax.setWaxOwner(frame, player.getUniqueId());
            player.getWorld().playSound(frame,Sound.ITEM_HONEYCOMB_WAX_ON,SoundCategory.BLOCKS,1,1);
            return;
        }

        if(wax.getWaxOwner() != null && Tag.ITEMS_AXES.isTagged(stack.getType())) {
            ev.setCancelled(true);
            wax.setWaxOwner(frame, null);
            player.getWorld().playSound(frame, Sound.ITEM_AXE_WAX_OFF, SoundCategory.BLOCKS, 1, 1);
            return;
        }
    }

    private void placeFrame(Player player, Block block, FrameOffset offset){
        Interaction interaction = block.getWorld().spawn(block.getLocation(), Interaction.class);
        DilemmaMeta.CUSTOM_ITEM_FRAME.getOrCreate(interaction).onCreate(interaction,player,block,offset);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent ev){
        if(ev.getRightClicked() instanceof ItemFrame frame) {
            onPlayerInteractItemFrame(frame,ev);
            return;
        }
        if(ev.getRightClicked() instanceof Interaction interaction){
            DilemmaMeta.CUSTOM_ITEM_FRAME.get(interaction)
                    .filter(frame -> frame.canModify(ev.getPlayer()))
                    .ifPresent(frame -> frame.onRightClick(interaction,ev));
        }
    }

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent ev){
        if(ev.getEntity() instanceof ItemFrame frame){
            if(!DilemmaMeta.WAX_INFO.getOrCreate(frame).canModify(ev.getDamager())){
                ev.setCancelled(true);
            }
            return;
        }
        if(ev.getEntity() instanceof Interaction interaction && ev.getDamager() instanceof Player player){
            DilemmaMeta.CUSTOM_ITEM_FRAME.get(interaction)
                    .filter(frame -> frame.canModify(player))
                    .ifPresent(frame -> frame.onLeftClick(interaction,ev));
        }
    }

    @EventHandler
    public void onPlayerInteractBlock(PlayerInteractEvent ev){
        if(ev.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if(ev.getPlayer().isSneaking()) return;
        ItemStack stack = ev.getItem();
        if(stack == null) return;
        Block block = ev.getClickedBlock();
        if(block == null) return;
        if(stack.getType() == Material.ITEM_FRAME){
            FrameOffset.getOffset(block,ev.getPlayer(),ev.getBlockFace()).ifPresent(offset ->{
                ev.setCancelled(true);
                ItemUtils.consumeItem(ev.getPlayer(),ev.getHand(),1);
                placeFrame(ev.getPlayer(),block,offset);
            });
        }
    }
}
