const {NT} = require("planets/Nepture");
const maps = require("map/maps");
const attackMaps = maps.attackMaps;

function setRules(){ 
    var rules = Vars.state.rules;
    if(rules.planet == NT && !(rules.sector == null || rules.editor)){
        Log.info("Fucking BUG");
        rules.showSpawns = true;
        rules.solarMultiplier = 0.05;

        if(rules.sector == maps["暴雪前哨"].sector){
            rules.fog = true;
            rules.lighting = true;
            rules.staticFog = false;
            rules.ambientLight = rules.staticColor = rules.dynamicColor = Color.valueOf("DFDFDFC0");
        }
        else if(rules.sector == maps["测试区"].sector || rules.sector == maps["狭长冰谷"].sector){
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
        rules.attackMode = AM;
    }
}

//Events.on(Packages.mindustry.game.EventType.PlayEvent, cons(e => {
Events.on(SectorLaunchEvent, cons(e => {
    setRules();
}));
Events.on(SaveLoadEvent, cons(e => {
    setRules();
}));