package LI;

import arc.Events;
import arc.util.*;
import mindustry.mod.*;
import LI.content.tech.LItechTree;
import LI.game.LIrules;
import LI.ui.dialogs.*;
import LI.type.ai.LIcommands;
import LI.content.*;
import LI.content.tech.ATD;

import static mindustry.game.EventType.*;
import static mindustry.Vars.*;

public class LIMod extends Mod{
    public LIstartDialog start;
    public LIrules rules;

    public LIMod(){
        Log.info("Loading: Liquid Industry Java Version");
        Events.on(ContentInitEvent.class, e -> {
            LIoverride.loadOverride();
            ATD.load();
        });
    }

    @Override
    public void init() {
        rules = new LIrules();
        start = new LIstartDialog();
        ui.research = new LIresearchDialog();
    }

    @Override
    public void loadContent(){
        LIaudio.load();
        LIcommands.load();
        LIweathers.load();
        LIplanets.load(); //未添加卫星
        LImaps.load();
        LIitems.load();
        LIliquids.load();
        LIstatus.load();
        LIunits.load();
        LIblocks.load(); //终能聚合器,精华提取源，神能凝聚仪未添加。钍反炮显示尚有问题 双传带液体角落显示有问题
        LItechTree.load(); //未添加的内容无法加入
    }
}
