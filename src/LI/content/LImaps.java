package LI.content;

import arc.graphics.Color;
import arc.struct.Seq;
import mindustry.type.SectorPreset;

import static LI.content.LIplanets.NT;

public class LImaps {
    public static SectorPreset
    //主线
        map1,map2,map3,map4,map5,map6,

    //支线
        ZXmap1,ZXmap2,ZXmap3,

    //教程
        JCmap1,JCmap2
    ;

    public static final Seq<SectorPreset> tutorialMaps = new Seq<>();
    public static final Seq<SectorPreset> windMaps = new Seq<>();
    public static final Seq<SectorPreset> allMaps = new Seq<>();

    public static void load(){
        map1 = new SectorPreset("测试区", NT, 1){{
            difficulty = 1;
            alwaysUnlocked = false;
            addStartingItems = true;
            captureWave = 2;
            rules = (rule) -> {
                rule.fog = false;
                rule.lighting = false;
            };
        }};

        map2 = new SectorPreset("狭长冰谷", NT, 38){{
            difficulty = 4;
            alwaysUnlocked = false;
            addStartingItems = true;
            captureWave = 55;
            rules = (rule) -> {
                rule.fog = false;
                rule.lighting = false;
            };
        }};

        map3 = new SectorPreset("极冰溶洞", NT, 56){{
            difficulty = 7;
            alwaysUnlocked = false;
            addStartingItems = true;
            rules = (rule) -> {
                rule.fog = false;
                rule.lighting = true;
                rule.ambientLight = Color.valueOf("000000F0");
                rule.attackMode = true;
            };
        }};

        map4 = new SectorPreset("永夜荒地", NT, 4){{
            difficulty = 8;
            alwaysUnlocked = false;
            addStartingItems = true;
            captureWave = 25;
            rules = (rule) -> {
                rule.fog = false;
                rule.lighting = true;
                rule.ambientLight = Color.valueOf("000000F0");
            };
        }};

        map5 = new SectorPreset("极光壁垒", NT, 54){{
            difficulty = 8;
            alwaysUnlocked = false;
            addStartingItems = true;
            rules = (rule) -> {
                rule.fog = false;
                rule.lighting = true;
                rule.ambientLight = Color.valueOf("000000F0");
                rule.attackMode = true;
            };
        }};

        map6 = new SectorPreset("暗潮冻港", NT, 61){{
            difficulty = 11;
            alwaysUnlocked = false;
            addStartingItems = true;
            captureWave = 41;
            rules = (rule) -> {
                rule.fog = false;
                rule.lighting = true;
                rule.ambientLight = Color.valueOf("000000F0");
            };
        }};

        ZXmap1 = new SectorPreset("蛇行道", NT, 53){{
            difficulty = 9;
            alwaysUnlocked = false;
            addStartingItems = true;
            captureWave = 31;
            rules = (rule) -> {
                rule.fog = false;
                rule.lighting = true;
                rule.ambientLight = Color.valueOf("000000F0");
            };
        }};

        ZXmap2 = new SectorPreset("地火", NT, 80){{
            difficulty = 11;
            alwaysUnlocked = false;
            addStartingItems = true;
            captureWave = 13;
            rules = (rule) -> {
                rule.fog = false;
                rule.lighting = true;
                rule.ambientLight = Color.valueOf("000000F0");
                rule.attackMode = false;
            };
        }};

        ZXmap3 = new SectorPreset("暴雪前哨", NT, 9){{
            difficulty = 11;
            alwaysUnlocked = false;
            addStartingItems = true;
            captureWave = 36;
            rules = (rule) -> {
                rule.fog = true;
                rule.lighting = true;
                rule.staticFog = false;
                rule.ambientLight = rule.staticColor = rule.dynamicColor = Color.valueOf("DFDFDFC0");
            };
        }};

        JCmap1 = new SectorPreset("教程：获取钛", NT, 39){{
            difficulty = 1;
            alwaysUnlocked = false;
            addStartingItems = true;
            rules = (rule) -> {
                rule.fog = false;
                rule.lighting = false;
                rule.attackMode = true;
            };
        }};

        JCmap2 = new SectorPreset("教程：获取钍", NT, 76){{
            difficulty = 1;
            alwaysUnlocked = false;
            addStartingItems = true;
            rules = (rule) -> {
                rule.fog = false;
                rule.lighting = false;
                rule.attackMode = true;
            };
        }};

        tutorialMaps.addAll(JCmap1, JCmap2);
        windMaps.addAll(ZXmap3);
        allMaps.addAll(map1, map2, map3, map4, map5, map6, ZXmap1, ZXmap2, ZXmap3, JCmap1, JCmap2);
    }
}
