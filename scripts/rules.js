const {NT} = require("planets/Nepture");
const maps = require("map/maps");
const attackMaps = Seq.with(maps["极冰溶洞"], maps["极光壁垒"], maps["教程：获取钛"], maps["教程：获取钍"]);

function setRules(){ 
    var rules = Vars.state.rules;
    if(Vars.state.isCampaign() && rules.planet == NT && !(rules.sector == null)){
        rules.showSpawns = true;
        rules.solarMultiplier = 0.05;
        if(rules.sector == maps["暴雪前哨"].sector){
            rules.fog = true;
            rules.lighting = true;
            rules.staticFog = false;
            rules.ambientLight = rules.staticColor = rules.dynamicColor = Color.valueOf("DFDFDFC0");
        }
        else if(rules.sector == maps["测试区"].sector || rules.sector == maps["狭长冰谷"].sector || rules.sector == maps["教程：获取钛"].sector || rules.sector == maps["教程：获取钍"].sector){
            rules.fog = false;
            rules.lighting = false;
        }
        else{
            rules.fog = false;
            rules.lighting = true;
            rules.ambientLight = Color.valueOf("000000F0");
        }

        let AM = false;
        attackMaps.each(map => {
            if(rules.sector == map.sector){
                AM = true;
            }
        });
        if(!rules.waves && rules.winWave <= 0) return;
        rules.attackMode = AM;
    }
}

Events.on(SectorLaunchEvent, cons(e => {
    setRules();
}));
Events.on(SaveLoadEvent, cons(e => {
    setRules();
}));