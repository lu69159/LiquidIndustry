const {NT}= require("planets/Nepture");
exports.attackMaps = new Seq();
function addMap(name, difficulty, sector, wave){
    const map = new SectorPreset(name, NT, sector);
    map.difficulty = difficulty;
    map.alwaysUnlocked = false;
    map.addStartingItems = true;
    map.captureWave = wave;
    map.localizedName = name;
    exports[name] = map;
}
function addAttackMap(name, difficulty, sector){
    const map = new SectorPreset(name, NT, sector);
    map.difficulty = difficulty;
    map.alwaysUnlocked = false;
    map.addStartingItems = true;
    map.localizedName = name;
    if(!exports.attackMaps.contains(map)){
        exports.attackMaps.add(map);
    }
    exports[name] = map;
}

//主线
addMap("测试区", 1, 1, 2);
addMap("狭长冰谷", 4, 38, 55);
addAttackMap("极冰溶洞", 7, 56);
addMap("永夜荒地", 8, 4, 25);
addAttackMap("极光壁垒", 8, 54);
addMap("暴雪前哨", 10, 61, 36);

//教程
addAttackMap("教程：获取钛", 1, 39);
addAttackMap("教程：获取钍", 1, 76);

//支线
addMap("蛇行道", 10, 53, 31);
addMap("地火", 12, 80, 13);
