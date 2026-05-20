package LI.type.ai;

import mindustry.ai.UnitCommand;

public class LIcommands{
    public static UnitCommand healCommand;

    public static void load(){
        healCommand = new UnitCommand("heal", "add", u -> new HealAI());
    }
}
