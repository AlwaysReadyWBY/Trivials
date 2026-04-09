package top.alwaysready.trivials.module.itemframe;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;
import top.alwaysready.trivials.module.wax.WaxAttachable;
import top.alwaysready.trivials.utils.ItemUtils;

import java.util.Optional;
import java.util.UUID;

import static top.alwaysready.trivials.module.itemframe.FrameOffset.*;

public class DilemmaItemFrame implements WaxAttachable {
    public static final NamespacedKey KEY_CONTENT = NamespacedKey.fromString("trivials:content");
    public static final NamespacedKey KEY_BACKGROUND = NamespacedKey.fromString("trivials:background");
    public static final NamespacedKey KEY_ROTATION = NamespacedKey.fromString("trivials:rotation");

    private ItemStack content;
    private boolean glowing;
    private boolean invisible;
    private FrameOffset frameOffset = HANGING_SIGN_NORTH;
    private UUID contentDisplayId;
    private UUID backgroundDisplayId;
    private UUID waxOwner;
    private float rotation = 0;

    public DilemmaItemFrame() {
        glowing = false;
        invisible = false;
    }

    public void setRotation(Interaction interaction, float rotation) {
        this.rotation = rotation;
        interaction.getPersistentDataContainer().set(KEY_ROTATION, PersistentDataType.FLOAT,rotation);
    }

    public float getRotation() {
        return rotation;
    }

    public void setGlowing(boolean glowing) {
        this.glowing = glowing;
    }

    public boolean isGlowing() {
        return glowing;
    }

    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
    }

    public boolean isInvisible() {
        return invisible;
    }

    public ItemStack getContent() {
        return content;
    }

    public void setContent(ItemStack content) {
        this.content = content;
    }

    public void onCreate(Interaction interaction, Player placer, Block block, FrameOffset offset) {
        interaction.addScoreboardTag("trivials:item_frame");
        setFrameOffset(offset);
        interaction.teleport(getFrameOffset().getLocation(block.getLocation()));
        interaction.getWorld().spawn(interaction.getLocation().clone(), ItemDisplay.class,
                disp -> initBackground(interaction,disp));
        interaction.getWorld().playSound(interaction, Sound.ENTITY_ITEM_FRAME_PLACE, SoundCategory.BLOCKS,1,1);
    }

    public void onLoad(Interaction interaction) {
        PersistentDataContainer pdc = interaction.getPersistentDataContainer();
        String str = pdc.get(KEY_CONTENT, PersistentDataType.STRING);
        contentDisplayId = str == null? null:UUID.fromString(str);
        str = pdc.get(KEY_BACKGROUND, PersistentDataType.STRING);
        backgroundDisplayId = str == null? null:UUID.fromString(str);
        getContentDisplay().ifPresent(disp -> setContent(disp.getItemStack()));
        str = pdc.get(KEY_WAX_OWNER,PersistentDataType.STRING);
        waxOwner = str == null? null:UUID.fromString(str);
        rotation = Optional.ofNullable(pdc.get(KEY_ROTATION, PersistentDataType.FLOAT)).orElse(0f);
    }

    public void onRightClick(Interaction interaction, PlayerInteractEntityEvent ev) {
        ItemStack content = getContent();
        ItemStack hand = ev.getPlayer().getInventory().getItem(ev.getHand());

        if(content != null){
            if(hand!=null) {
                if (getWaxOwner() == null && hand.getType() == Material.HONEYCOMB
                        && ItemUtils.consumeItem(ev.getPlayer(), ev.getHand(), 1)) {
                    setWaxOwner(interaction,ev.getPlayer().getUniqueId());
                    return;
                }
                if (getWaxOwner() != null && Tag.ITEMS_AXES.isTagged(hand.getType())
                        && ItemUtils.consumeItem(ev.getPlayer(), ev.getHand(), 1)){
                    setWaxOwner(interaction,null);
                    return;
                }
            }
            setRotation(interaction,getRotation()+45f);
            rotateContent();
            return;
        }

        if (ItemUtils.isAir(hand)) return;
        content = hand.clone();
        content.setAmount(1);
        ItemUtils.consumeItem(ev.getPlayer(), ev.getHand(), 1);
        setContent(content);
        getBackgroundDisplay().ifPresent(disp -> disp.setItemStack(new ItemStack(Material.AIR)));
        interaction.getWorld().spawn(interaction.getLocation().clone(), ItemDisplay.class,
                disp -> initContent(interaction,disp));
        interaction.getWorld().playSound(interaction, Sound.ENTITY_ITEM_FRAME_ADD_ITEM, SoundCategory.BLOCKS,1,1);
    }

    public void onLeftClick(Interaction interaction, EntityDamageByEntityEvent ev) {
        Location loc = interaction.getLocation();
        ItemStack content = getContent();
        if (content != null) {
            setContent(null);
            getContentDisplay().ifPresent(Entity::remove);
            getBackgroundDisplay().ifPresent(bg ->
                    bg.setItemStack(new ItemStack(Material.ITEM_FRAME)));
            interaction.getWorld().dropItemNaturally(loc, content);
            interaction.getWorld().playSound(interaction, Sound.ENTITY_ITEM_FRAME_REMOVE_ITEM, SoundCategory.BLOCKS,1,1);
            return;
        }
        getBackgroundDisplay().ifPresent(Entity::remove);
        getContentDisplay().ifPresent(Entity::remove);
        interaction.getWorld().dropItemNaturally(loc, new ItemStack(Material.ITEM_FRAME));
        interaction.getWorld().playSound(interaction, Sound.ENTITY_ITEM_FRAME_BREAK, SoundCategory.BLOCKS,1,1);
        interaction.remove();
    }


    public Optional<ItemDisplay> getBackgroundDisplay(){
        if(backgroundDisplayId==null) return Optional.empty();
        return Optional.ofNullable(Bukkit.getEntity(backgroundDisplayId))
                .filter(ItemDisplay.class::isInstance)
                .map(ItemDisplay.class::cast);
    }

    public Optional<ItemDisplay> getContentDisplay(){
        if(contentDisplayId==null) return Optional.empty();
        return Optional.ofNullable(Bukkit.getEntity(contentDisplayId))
                .filter(ItemDisplay.class::isInstance)
                .map(ItemDisplay.class::cast);
    }

    private void initBackground(Interaction interaction,ItemDisplay disp){
        disp.addScoreboardTag("trivials:background");
        disp.setItemStack(new ItemStack(Material.ITEM_FRAME));
        Transformation trans = disp.getTransformation();
        FrameOffset offset = getFrameOffset();
        float scale = offset.getScale();
        disp.setTransformation(new Transformation(
                new Vector3f(offset.getTransX(), offset.getTransY(), offset.getTransZ()),
                trans.getLeftRotation(),
                new Vector3f(scale,scale,0.1f),
                trans.getRightRotation()
        ));
        Location loc = interaction.getLocation();
        disp.setRotation(loc.getYaw(), loc.getPitch());
        backgroundDisplayId = disp.getUniqueId();
        interaction.getPersistentDataContainer().set(KEY_BACKGROUND, PersistentDataType.STRING,
                backgroundDisplayId.toString());
    }

    private void initContent(Interaction interaction, ItemDisplay disp) {
        disp.getScoreboardTags().add("trivials:content");
        disp.setItemStack(getContent());
        Transformation trans = disp.getTransformation();
        FrameOffset offset = getFrameOffset();
        float scale = offset.getScale();
        disp.setTransformation(new Transformation(
                new Vector3f(offset.getTransX(), offset.getTransY(), offset.getTransZ()),
                trans.getLeftRotation().rotationAxis((float) (getRotation()*Math.PI/180),0,0,1),
                new Vector3f(scale,scale,0.1f),
                trans.getRightRotation()
        ));
        Location loc = interaction.getLocation();
        disp.setRotation(loc.getYaw(), loc.getPitch());
        disp.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.GUI);
        contentDisplayId = disp.getUniqueId();
        interaction.getPersistentDataContainer().set(KEY_CONTENT, PersistentDataType.STRING,
                contentDisplayId.toString());
    }

    private void rotateContent(){
        getContentDisplay().ifPresent(disp -> {
            Transformation trans = disp.getTransformation();
            disp.setTransformation(new Transformation(
                    trans.getTranslation(),
                    trans.getLeftRotation().rotationAxis((float) (getRotation()*Math.PI/180),0,0,1),
                    trans.getScale(),
                    trans.getRightRotation()
            ));
            disp.getWorld().playSound(disp, Sound.ENTITY_ITEM_FRAME_ROTATE_ITEM, SoundCategory.BLOCKS,1,1);
        });
    }

    public FrameOffset getFrameOffset() {
        return frameOffset;
    }

    public void setFrameOffset(FrameOffset frameOffset) {
        this.frameOffset = frameOffset;
    }

    public void setWaxOwner(Interaction interaction,UUID waxOwner) {
        this.waxOwner = waxOwner;
        if(waxOwner == null){
            interaction.getPersistentDataContainer().remove(KEY_WAX_OWNER);
            interaction.getWorld().playSound(interaction, Sound.ITEM_AXE_WAX_OFF, SoundCategory.BLOCKS,1,1);
            return;
        }
        interaction.getPersistentDataContainer().set(KEY_WAX_OWNER, PersistentDataType.STRING,waxOwner.toString());
        interaction.getWorld().playSound(interaction, Sound.ITEM_HONEYCOMB_WAX_ON, SoundCategory.BLOCKS,1,1);
    }

    @Override
    public UUID getWaxOwner() {
        return waxOwner;
    }
}
