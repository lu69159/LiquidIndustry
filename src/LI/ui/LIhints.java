package LI.ui;

import arc.*;
import arc.func.*;
import arc.struct.ObjectSet;
import arc.util.*;

import static mindustry.Vars.*;
import static mindustry.game.EventType.*;
import static mindustry.ui.fragments.HintsFragment.*;

//TODO
public class LIhints{
    ObjectSet<String> events = new ObjectSet<>();

    public LIhints(){
        Events.run(ClientLoadEvent.class, () -> {
            Core.app.post(() -> {
                ui.hints.hints.add(LIHint.values());
            });
        });
    }

    public enum LIHint implements Hint {
        ; //TODO: enum hint

        @Nullable
        String text;
        int visibility = visibleAll;
        Hint[] dependencies = {};
        boolean finished, cached;
        Boolp complete, shown = () -> true;

        LIHint(Boolp complete){
            this.complete = complete;
        }

        @Override
        public boolean finished(){
            if(!cached){
                cached = true;
                finished = Core.settings.getBool(name() + "-hint-done", false);
            }
            return finished;
        }

        @Override
        public void finish(){
            Core.settings.put(name() + "-hint-done", finished = true);
        }

        @Override
        public String text(){
            if(text == null){
                text = mobile && Core.bundle.has("hint." + name() + ".mobile") ? Core.bundle.get("hint." + name() + ".mobile") : Core.bundle.get("hint." + name());
                if(!mobile) text = text.replace("tap", "click").replace("Tap", "Click");
            }
            return text;
        }

        @Override
        public boolean complete(){
            return complete.get();
        }

        @Override
        public boolean show(){
            return shown.get() && (dependencies.length == 0 || !Structs.contains(dependencies, d -> !d.finished()));
        }

        @Override
        public int order(){
            return ordinal();
        }

        @Override
        public boolean valid(){
            return (mobile && (visibility & visibleMobile) != 0) || (!mobile && (visibility & visibleDesktop) != 0);
        }
    }
}
