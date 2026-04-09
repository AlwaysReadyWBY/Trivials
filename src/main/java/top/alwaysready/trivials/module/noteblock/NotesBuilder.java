package top.alwaysready.trivials.module.noteblock;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NotesBuilder {
    private static final Map<String,Float> PITCH_MAP;
    private static final double mul = Math.pow(2,1/12f);

    static{
        PITCH_MAP = new Hashtable<>();
        double p = 1d;
        PITCH_MAP.put("C4", (float) p);
        p*=mul;
        PITCH_MAP.put("C#4", (float) p);
        p*=mul;
        PITCH_MAP.put("D4", (float) p);
        p*=mul;
        PITCH_MAP.put("D#4", (float) p);
        p*=mul;
        PITCH_MAP.put("E4", (float) p);
        p*=mul;
        PITCH_MAP.put("F4", (float) p);
        p*=mul;
        PITCH_MAP.put("F#4", (float) p);
        p*=mul;
        PITCH_MAP.put("G4", (float) p);
        p*=mul;
        PITCH_MAP.put("G#4", (float) p);
        p*=mul;
        PITCH_MAP.put("A4", (float) p);
        p*=mul;
        PITCH_MAP.put("A#4", (float) p);
        p*=mul;
        PITCH_MAP.put("B4", (float) p);
        PITCH_MAP.put("C5", 2f);
        p=1/mul;
        PITCH_MAP.put("B3", (float) p);
        p/=mul;
        PITCH_MAP.put("A#3", (float) p);
        p/=mul;
        PITCH_MAP.put("A3", (float) p);
        p/=mul;
        PITCH_MAP.put("G#3", (float) p);
        p/=mul;
        PITCH_MAP.put("G3", (float) p);
        p/=mul;
        PITCH_MAP.put("F#3", (float) p);
        p/=mul;
        PITCH_MAP.put("F3", (float) p);
        p/=mul;
        PITCH_MAP.put("E3", (float) p);
        p/=mul;
        PITCH_MAP.put("D#3", (float) p);
        p/=mul;
        PITCH_MAP.put("D3", (float) p);
        p/=mul;
        PITCH_MAP.put("C#3", (float) p);
        PITCH_MAP.put("C3", 0.5f);
        PITCH_MAP.put("P",-1f);
    }

    private double bpm = 150; // 8ticks/beat
    private final List<NoteNode> nodes = new LinkedList<>();

    public NotesBuilder setBpm(double bpm) {
        this.bpm = Math.min(1200,Math.max(30,bpm));
        return this;
    }

    public double getBpm() {
        return bpm;
    }

    public NotesBuilder parse(String str){
        char[] chars = str.toCharArray();
        for(int i=0;i<chars.length;){
            switch (chars[i]){
                case '/' -> i = readCommand(chars,i+1);
                case 'A','B','C','D','E','F','G','P' -> i = readNote(chars,i);
                default -> i++;
            }
        }
        return this;
    }

    private int readCommand(char[] chars,int cmdStart){
        StringBuilder builder = new StringBuilder();
        int i;
        for(i=cmdStart;i<chars.length;i++){
            if(chars[i]==';') break;
            builder.append(chars[i]);
        }
        String[] args = builder.toString().split(" ");
        if(args.length==0) return i+1;
        switch (args[0]){
            case "bpm" -> {
                if(args.length>1) {
                    setBpm(Double.parseDouble(args[1]));
                }
            }
        }
        return i+1;
    }

    private int readNote(char[] chars,int noteStart){
        StringBuilder builder = new StringBuilder();
        int i;
        for(i=noteStart;i<chars.length;i++){
            if(chars[i]=='.' || chars[i]=='_' || chars[i]=='*') break;
            builder.append(chars[i]);
        }

        if(i>=chars.length) return i+1;
        char typeChar = chars[i];

        Float pitch = PITCH_MAP.get(builder.toString());
        if(pitch == null) return i+1;

        builder = new StringBuilder();
        for(i++;i<chars.length;i++){
            if(chars[i]<'0' || chars[i]>'9') break;
            builder.append(chars[i]);
        }

        String str = builder.toString();
        double length;

        if(typeChar == '*'){
            length = Double.parseDouble(str);
        } else {
            double fullNotePerBeat = typeChar=='_'? 4:6;
            double div = str.isEmpty()?4:Double.parseDouble(str);
            length = 60000/getBpm()*fullNotePerBeat/div;
        }
        NoteNode node = new NoteNode()
                .setPitch(pitch)
                .setLength(length);
        if(!nodes.isEmpty()){
            NoteNode last = nodes.get(nodes.size() - 1);
            last.setNext(node);
            node.setEnd(last.getEnd()+node.getLength());
        } else {
            node.setEnd(node.getLength());
        }
        nodes.add(node);
        return i;
    }

    public NoteNode build(){
        return nodes.isEmpty()?null:nodes.get(0);
    }
}
