package LI.content.tech;

import arc.func.Boolf;
import mindustry.Vars;
import mindustry.ctype.UnlockableContent;
import mindustry.type.*;
import mindustry.content.*;
import LI.content.LIplanets;

public class ATD {
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
            if(shouldATD) AddToDatabase(planet, (u) -> u.shownPlanets.contains(LIplanets.NT));
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
