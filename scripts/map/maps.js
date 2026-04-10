const {NT}= require("planets/Nepture");

//主线
const map1 = new SectorPreset("测试区", NT, 1);
map1.difficulty = 1;
map1.alwaysUnlocked = false;
map1.addStartingItems = true;
map1.captureWave = 2;
map1.localizedName = "测试区";
map1.rules = cons((rule) => {
    rule.fog = false;
    rule.lighting = false;
});
exports["测试区"] = map1;

const map2 = new SectorPreset("狭长冰谷", NT, 38);
map2.difficulty = 4;
map2.alwaysUnlocked = false;
map2.addStartingItems = true;
map2.captureWave = 55;
map2.localizedName = "狭长冰谷";
map2.rules = cons((rule) => {
    rule.fog = false;
    rule.lighting = false;
});
exports["狭长冰谷"] = map2;

const map3 = new SectorPreset("极冰溶洞", NT, 56);
map3.difficulty = 7;
map3.alwaysUnlocked = false;
map3.addStartingItems = true;
map3.localizedName = "极冰溶洞";
map3.rules = cons((rule) => {
    rule.fog = false;
    rule.lighting = true;
    rule.ambientLight = Color.valueOf("000000F0");
    rule.attackMode = true;
});
exports["极冰溶洞"] = map3;

const map4 = new SectorPreset("永夜荒地", NT, 4);
map4.difficulty = 8;
map4.alwaysUnlocked = false;
map4.addStartingItems = true;
map4.captureWave = 25;
map4.localizedName = "永夜荒地";
map4.rules = cons((rule) => {
    rule.fog = false;
    rule.lighting = true;
    rule.ambientLight = Color.valueOf("000000F0");
});
exports["永夜荒地"] = map4;

const map5 = new SectorPreset("极光壁垒", NT, 54);
map5.difficulty = 8;
map5.alwaysUnlocked = false;
map5.addStartingItems = true;
map5.localizedName = "极光壁垒";
map5.rules = cons((rule) => {
    rule.fog = false;
    rule.lighting = true;
    rule.ambientLight = Color.valueOf("000000F0");
    rule.attackMode = true;
});
exports["极光壁垒"] = map5;

const map6 = new SectorPreset("暴雪前哨", NT, 61);
map6.difficulty = 10;
map6.alwaysUnlocked = false;
map6.addStartingItems = true;
map6.captureWave = 36;
map6.localizedName = "暴雪前哨";
map6.rules = cons((rule) => {
    rule.fog = true;
    rule.lighting = true;
    rule.staticFog = false;
    rule.ambientLight = rule.staticColor = rule.dynamicColor = Color.valueOf("DFDFDFC0");
});
exports["暴雪前哨"] = map6;

//教程
const JCmap1 = new SectorPreset("教程：获取钛", NT, 39);
JCmap1.difficulty = 1;
JCmap1.alwaysUnlocked = false;
JCmap1.addStartingItems = true;
JCmap1.localizedName = "教程：获取钛";
JCmap1.rules = cons((rule) => {
    rule.fog = false;
    rule.lighting = false;
    rule.attackMode = true;
});
exports["教程：获取钛"] = JCmap1;

const JCmap2 = new SectorPreset("教程：获取钍", NT, 76);
JCmap2.difficulty = 1;
JCmap2.alwaysUnlocked = false;
JCmap2.addStartingItems = true;
JCmap2.localizedName = "教程：获取钍";
JCmap2.rules = cons((rule) => {
    rule.fog = false;
    rule.lighting = false;
    rule.attackMode = true;
});
exports["教程：获取钍"] = JCmap2;

//支线
const ZXmap1 = new SectorPreset("蛇行道", NT, 53);
ZXmap1.difficulty = 10;
ZXmap1.alwaysUnlocked = false;
ZXmap1.addStartingItems = true;
ZXmap1.captureWave = 31;
ZXmap1.localizedName = "蛇行道";
ZXmap1.rules = cons((rule) => {
    rule.fog = false;
    rule.lighting = true;
    rule.ambientLight = Color.valueOf("000000F0");
});
exports["蛇行道"] = ZXmap1;

const ZXmap2 = new SectorPreset("地火", NT, 80);
ZXmap2.difficulty = 12;
ZXmap2.alwaysUnlocked = false;
ZXmap2.addStartingItems = true;
ZXmap2.captureWave = 13;
ZXmap2.localizedName = "地火";
ZXmap2.rules = cons((rule) => {
    rule.fog = false;
    rule.lighting = true;
    rule.ambientLight = Color.valueOf("000000F0");
    rule.attackMode = false;
});
exports["地火"] = ZXmap2;
