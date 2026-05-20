package LI.content;

import arc.audio.*;
import arc.util.Log;
import mindustry.Vars;

import java.lang.reflect.Field;

public class LIaudio {
    public static Sound
        wind,laser,FFF,villager1,villager2,villager3;
    public static Music
        NTlaunch;

    public static void load() {
        try {
            for(Field field : LIaudio.class.getFields()) {
                if (field.getType().equals(Sound.class)) {
                    field.set((Object)null, Vars.tree.loadSound(field.getName()));
                }
                else if (field.getType().equals(Music.class)) {
                    field.set((Object)null, Vars.tree.loadMusic(field.getName()));
                }
            }
        } catch (IllegalAccessException e) {
            Log.err(e);
        }
    }
}
