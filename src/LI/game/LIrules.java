package LI.game;

import arc.Events;
import mindustry.Vars;
import mindustry.game.Rules;
import LI.content.*;
import LI.content.tech.ATD;

import static mindustry.Vars.*;
import static mindustry.game.EventType.*;

public class LIrules {
    public LIrules(){
        Events.on(SectorLaunchEvent.class, (e) -> { setRules(); });
        Events.on(SaveLoadEvent.class, (e) -> { setRules(); });
    }

    public void setRules(){
        if(state.isCampaign() && state.rules.sector != null){
            if(state.rules.planet == LIplanets.NT){
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
            else{
                if(ATD.ATDplanets.contains(state.rules.planet) && state.rules.bannedBlocks.size > 0){
                    for(var b : Vars.content.blocks()){
                        if(b.shownPlanets.contains(LIplanets.NT)){
                            if(state.rules.blockWhitelist){
                                if(!state.rules.bannedBlocks.contains(b)){
                                    state.rules.bannedBlocks.add(b);
                                }
                            }
                            else{
                                if(state.rules.bannedBlocks.contains(b)){
                                    state.rules.bannedBlocks.remove(b);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
