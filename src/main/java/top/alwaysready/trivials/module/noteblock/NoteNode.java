package top.alwaysready.trivials.module.noteblock;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Optional;

public class NoteNode {

    private static final NamespacedKey KEY_LENGTH = NamespacedKey.fromString("trivials:note_length");
    private static final NamespacedKey KEY_PITCH = NamespacedKey.fromString("trivials:note_pitch");

    //In millis
    private double length;
    private double end;
    private float pitch;
    private NoteNode next;

    public boolean hasNext(){
        return next!=null;
    }

    public NoteNode setLength(double length) {
        this.length = length;
        return this;
    }

    public NoteNode setEnd(double end) {
        this.end = end;
        return this;
    }

    public NoteNode setPitch(float pitch) {
        this.pitch = pitch;
        return this;
    }

    public void setNext(NoteNode next) {
        this.next = next;
    }

    public NoteNode getNext() {
        return next;
    }

    public double getLength() {
        return length;
    }

    public double getEnd() {
        return end;
    }

    public float getPitch() {
        return pitch;
    }

    public void save(PersistentDataContainer container){
        container.set(KEY_LENGTH, PersistentDataType.DOUBLE, length);
        container.set(KEY_PITCH, PersistentDataType.FLOAT, pitch);
    }

    public NoteNode load(double start,PersistentDataContainer container){
        return setLength(Optional.ofNullable(container.get(KEY_LENGTH, PersistentDataType.DOUBLE)).orElse(0d))
                .setPitch(Optional.ofNullable(container.get(KEY_PITCH, PersistentDataType.FLOAT)).orElse(1f))
                .setEnd(start+getLength());
    }
}
