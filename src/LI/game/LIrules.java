package LI.game;

import arc.Events;
import mindustry.game.Rules;
import LI.content.*;

import static mindustry.Vars.*;
import static mindustry.game.EventType.*;

public class LIrules {
    public LIrules(){
        Events.on(SectorLaunchEvent.class, (e) -> { setRules(); });
        Events.on(SaveLoadEvent.class, (e) -> { setRules(); });
    }

    public void setRules(){
        if(state.isCampaign() && state.rules.planet == LIplanets.NT && state.rules.sector != null){
            Rules rule = new Rules();
            if(LImaps.allMaps.contains(state.rules.sector.preset)){
                state.rules.sector.preset.rules.get(rule);

                state.rules.fog = rule.fog;
                state.rules.staticFog = rule.staticFog;
                state.rules.lighting = rule.lighting;
                if(!state.rules.sector.isCaptured()) state.rules.attackMode = rule.attackMode;
                state.rules.ambientLight = rule.ambientLight;
                state.rules.staticColor = rule.staticColor;
                state.rules.dynamicColor = rule.dynamicColor;
            }
        }
    }
}
