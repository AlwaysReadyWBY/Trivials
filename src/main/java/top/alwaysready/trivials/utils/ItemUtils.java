package top.alwaysready.trivials.utils;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public interface ItemUtils {
    static boolean consumeItem(Player player, EquipmentSlot slot, int count){
        if(player.getGameMode()== GameMode.CREATIVE) return true;
        ItemStack stack = player.getInventory().getItem(slot);
        int amount = stack.getAmount()-count;
        if(amount < 0){
            return false;
        }
        if(amount == 0){
            player.getInventory().setItem(slot,null);
            return true;
        }
        stack.setAmount(amount);
        player.getInventory().setItem(slot,stack);
        return true;
    }

    static boolean isAir(ItemStack hand) {
        return hand == null || hand.getType().isAir() || hand.getAmount()<=0;
    }
}
