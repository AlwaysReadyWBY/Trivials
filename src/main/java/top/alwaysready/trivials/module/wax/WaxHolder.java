package top.alwaysready.trivials.module.wax;

import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class WaxHolder implements WaxAttachable {
    private UUID waxOwner;

    public void onLoad(PersistentDataHolder holder){
        String str = holder.getPersistentDataContainer().get(KEY_WAX_OWNER, PersistentDataType.STRING);
        waxOwner = str == null? null:UUID.fromString(str);
    }

    @Override
    public UUID getWaxOwner() {
        return waxOwner;
    }

    public void setWaxOwner(PersistentDataHolder holder,UUID waxOwner) {
        this.waxOwner = waxOwner;
        if(waxOwner == null){
            holder.getPersistentDataContainer().remove(KEY_WAX_OWNER);
            return;
        }
        holder.getPersistentDataContainer().set(KEY_WAX_OWNER, PersistentDataType.STRING,waxOwner.toString());
    }
}
