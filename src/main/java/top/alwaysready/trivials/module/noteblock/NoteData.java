package top.alwaysready.trivials.module.noteblock;

import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Marker;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import top.alwaysready.trivials.Trivials;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NoteData {
    public static NoteData getOrCreate(Block block){
        return get(block).orElseGet(()->{
            NoteData data = new NoteData();
            data.setId("block-"+block.getWorld().getUID()+"("+block.getX()+","+block.getY()+","+block.getZ()+")");
            data.createMarker(block);
            return data;
        });
    }

    public static void remove(Block block){
        block.getWorld().getNearbyEntities(block.getBoundingBox(), Marker.class::isInstance)
                .stream()
                .filter(marker -> marker.getScoreboardTags().contains("trivials:note_data"))
                .forEach(Entity::remove);
    }

    public static Optional<NoteData> get(Block block){
        return block.getWorld().getNearbyEntities(block.getBoundingBox(), Marker.class::isInstance)
                .stream()
                .filter(marker -> marker.getScoreboardTags().contains("trivials:note_data"))
                .map(Marker.class::cast)
                .map(NoteData::get)
                .findFirst();
    }

    public static NoteData get(Marker marker){
        return marker.getMetadata("note_data").stream()
                .map(MetadataValue::value)
                .filter(NoteData.class::isInstance)
                .map(NoteData.class::cast)
                .findFirst()
                .orElseGet(()->{
                    NoteData data = new NoteData();
                    data.load(marker.getPersistentDataContainer());
                    marker.setMetadata("note_data",new FixedMetadataValue(Trivials.getInstance(),data));
                    data.setMarker(marker);
                    return data;
                });
    }

    private static final NamespacedKey KEY_SOUND = NamespacedKey.fromString("trivials:sound");
    private static final NamespacedKey KEY_ID = NamespacedKey.fromString("trivials:id");
    private static final NamespacedKey KEY_TITLE = NamespacedKey.fromString("trivials:title");
    private static final NamespacedKey KEY_NODES = NamespacedKey.fromString("trivials:nodes");

    private Marker marker;
    //In millis
    private double length = 0;
    private String sound;
    private String id;
    private String title;
    private NoteNode first;

    public double getLength() {
        return length;
    }

    public String getSound() {
        return sound;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public NoteNode getFirst() {
        return first;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setFirst(NoteNode first) {
        this.first = first;
        NoteNode node = first;
        if(node == null) {
            length = 0;
            return;
        }
        while(node.hasNext()){
            node = node.getNext();
        }
        length = node.getEnd();
    }

    public Marker createMarker(Block block){
        Marker marker = (Marker) block.getWorld().spawnEntity(
                block.getLocation().clone().add(0.5,0.5,0.5),
                EntityType.MARKER);
        marker.getScoreboardTags().add("trivials:note_data");
        marker.setMetadata("note_data",new FixedMetadataValue(Trivials.getInstance(),this));
        save(marker.getPersistentDataContainer());
        setMarker(marker);
        return marker;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public void save(PersistentDataContainer pdc){
        if(sound== null){
            pdc.remove(KEY_SOUND);
        } else {
            pdc.set(KEY_SOUND, PersistentDataType.STRING, sound);
        }
        pdc.set(KEY_ID, PersistentDataType.STRING, id);
        if(title == null){
            pdc.remove(KEY_TITLE);
        } else {
            pdc.set(KEY_TITLE, PersistentDataType.STRING, title);
        }
        NoteNode node = getFirst();
        List<PersistentDataContainer> containers = new ArrayList<>();
        while(node!=null){
            PersistentDataContainer container = pdc.getAdapterContext().newPersistentDataContainer();
            node.save(container);
            node = node.getNext();
        }
        pdc.set(KEY_NODES, PersistentDataType.LIST.dataContainers(),containers);
    }

    public void load(PersistentDataContainer pdc){
        setSound(pdc.get(KEY_SOUND, PersistentDataType.STRING));
        setId(pdc.get(KEY_ID, PersistentDataType.STRING));
        setTitle(pdc.get(KEY_TITLE, PersistentDataType.STRING));
        List<PersistentDataContainer> containers = pdc.get(KEY_NODES, PersistentDataType.LIST.dataContainers());
        NoteNode first = null;
        NoteNode node = null;
        double start = 0;
        for(PersistentDataContainer container : containers){
            NoteNode newNode = new NoteNode().load(start,container);
            if(node != null){
                node.setNext(newNode);
            } else {
                first = newNode;
            }
            start += newNode.getLength();
            node = newNode;
        }
        setFirst(first);
    }
}
