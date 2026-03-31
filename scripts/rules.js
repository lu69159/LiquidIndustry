const {NT} = require("planets/Nepture");
const maps = require("map/maps");

var rules = Vars.state.rules;

Events.on(PlayEvent, cons(e => {
    if(rules.planet == NT && !(rules.sector == null || rules.sector.preset == null || rules.editor)){
        rules.showSpawns = true;
        rules.solarMultiplier = 0.05;
        var preset = rules.sector.preset;
        if(preset == maps["暴雪前哨"]){
            rules.fog = true;
            rules.staticFog = false;
            rules.ambientLight = rules.staticColor = rules.dynamicColor = Color.valueOf("DFDFDFC0");
        }
        else if(preset == maps["测试区"] || preset == maps["狭长冰谷"]){
            rules.fog = false;
            rules.lighting = false;
        }
        else{
            rules.fog = false;
            rules.lighting = true;
            rules.ambientLight = new Color(2/255 ,2/255 ,10/255 ,252/255);
        }
        //////
        if(maps.attackMaps.contains(preset)){
            rules.attackMode = true;
        }
        else{
            rules.attackMode = false;
        }
    }
}));