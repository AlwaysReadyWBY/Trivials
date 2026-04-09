package top.alwaysready.trivials.module.noteblock;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import top.alwaysready.trivials.Trivials;

import java.util.List;

public class PlayingNotes {
    //In millis
    private double time = 0;
    private NoteData data;
    private String sound;
    private Location location;
    private NoteNode currentNode;

    public PlayingNotes setData(NoteData data) {
        this.data = data;
        return this;
    }

    public PlayingNotes setLocation(Location location) {
        this.location = location;
        return this;
    }

    public PlayingNotes setSound(String sound) {
        this.sound = sound;
        return this;
    }

    public NoteData getData() {
        return data;
    }

    public double getTime() {
        return time;
    }

    public Location getLocation() {
        return location;
    }

    public NoteNode getCurrentNode() {
        return currentNode;
    }

    public String getSound() {
        return sound;
    }

    public void start(){
        time = 0;
        if(getSound() == null || getLocation() == null) return;
        currentNode = getData().getFirst();
        String title = getData().getTitle();
        if(title == null && currentNode == null) return;
        if(title !=null || currentNode.getPitch()>=0) {
            Bukkit.getScheduler().runTask(Trivials.getInstance(), () -> {
                getLocation().getWorld().playSound(getLocation(),
                        getSound(),
                        SoundCategory.BLOCKS,
                        3,
                        currentNode.getPitch());
                if(title==null) return;
                getLocation().getWorld().getPlayers().stream()
                        .filter(player -> player.getLocation().distance(getLocation())<48)
                        .forEach(player -> player.sendTitle(title,null,10,20,10));
            });
        }
    }

    public void elapse(double elapsed, List<Runnable> post){
        time+=elapsed;
        while(currentNode != null && currentNode.getEnd()-time <= 25){
            currentNode = currentNode.getNext();
            if(currentNode!=null && currentNode.getPitch()>=0){
                post.add(()->getLocation().getWorld().playSound(getLocation(),
                        getSound(),
                        SoundCategory.BLOCKS,
                        3,
                        currentNode.getPitch()));
            }
        }
    }

    public boolean isStopped(){
        return currentNode == null;
    }
}
