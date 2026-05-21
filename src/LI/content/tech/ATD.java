package LI.content.tech;

import arc.func.Boolf;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.ctype.UnlockableContent;
import mindustry.type.*;
import mindustry.content.*;
import LI.content.LIplanets;

import static mindustry.Vars.state;

public class ATD{
    public static Seq<Planet> ATDplanets = new Seq<>();

    public static void load(){
        AddToDatabase(LIplanets.NT, (u) -> {
            if(u instanceof Planet || u instanceof Weather) return false;
            return u.minfo.mod == Vars.mods.locateMod("液体工艺") ||
                    (u.shownPlanets.contains(Planets.serpulo) && !(u instanceof SectorPreset ||
                            u == Blocks.advancedLaunchPad || u == Blocks.landingPad || u == Blocks.interplanetaryAccelerator));
        });
        for(var planet : Vars.content.planets()){
            boolean shouldATD = true;
            for(var item : Items.serpuloItems){
                if(!item.shownPlanets.contains(planet)){
                    shouldATD = false;
                    break;
                }
            }
            if(shouldATD){
                if(planet != LIplanets.NT && planet != Planets.serpulo && planet != Planets.erekir) ATDplanets.add(planet);
                AddToDatabase(planet, (u) -> u.shownPlanets.contains(LIplanets.NT));
            }
        }
    }

    public static void ATDrule(){
        if(state.isCampaign() && ATDplanets.contains(state.rules.planet)){
            for(var b : state.rules.bannedBlocks){
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

    private static void AddToDatabase(Planet planet, Boolf<UnlockableContent> pred){
        for(var seq : Vars.content.getContentMap()){
            for(var thing : seq){
                if(thing instanceof UnlockableContent u && pred.get(u)){
                    if(!u.databaseTabs.contains(planet)) u.databaseTabs.add(planet);
                    if(!u.shownPlanets.contains(planet)) u.shownPlanets.add(planet);
                }
            }
        }
    }
}
