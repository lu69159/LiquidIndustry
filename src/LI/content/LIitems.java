package LI.content;

import arc.Events;
import arc.graphics.*;
import arc.math.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import mindustry.game.EventType;
import mindustry.type.Item;

public class LIitems {
    public static Item QSZ,ZYZ,CDZ,TF,NRJT,SMWZ,SMSP,GTS,GTZS,GTLDY,GTSY,GTCJLDY,HWKZJT,HWSBJT;

    public static void load(){
        QSZ = new Item("亲水质", Color.valueOf("4DA6FF")){{
            cost = 1.4f;
        }};
        ZYZ = new Item("治愈质", Color.valueOf("6BC583")){{
            cost = 1.5f;
        }};
        CDZ = new Item("超导质", Color.valueOf("8EFFEA")){{
            cost = 1.4f;
            charge = 1.5f;
        }};
        TF = new Item("碳粉", Color.valueOf("272727")){{
            cost = 1f;
            flammability = 1.25f;
            explosiveness = 0.25f;
        }};
        NRJT = new Item("耐热晶体", Color.valueOf("EA8878")){{
            cost = 1.6f;
            flammability = 0.05f;
        }};
        SMWZ = new MysteryItem("神秘物质", Color.white){{
            cost = 5f;
            charge = 10f;
            radioactivity = 50f;

            stringLen = 18;
            frames = 19;
            transitionFrames = 1;
            frameTime = 3f;
        }};
        SMSP = new MysteryItem("神秘碎片", Color.white){{
            cost = 2.1f;
            charge = 1f;
            radioactivity = 5f;
        }};
        GTS = new Item("固态水", Color.valueOf("4DA6FF")){{
            cost = 1.5f;
        }};
        GTZS = new Item("固态重水", LIcolor.ZScolor){{
            cost = 1.5f;
            radioactivity = 1.5f;
        }};
        GTLDY = new Item("固态冷冻液", LIcolor.cryofluidColor){{
            cost = 1.5f;
        }};
        GTSY = new Item("固态石油", LIcolor.oilColor){{
            cost = 1.5f;
            flammability = 1.5f;
            explosiveness = 2.5f;
        }};
        GTCJLDY = new Item("固态超级冷冻液", LIcolor.CJLDYcolor){{
            cost = 1.5f;
        }};
        HWKZJT = new Item("恒温矿渣晶体", LIcolor.slagColor){{
            cost = 1.8f;
            flammability = 6f;
        }};
        HWSBJT = new Item("恒温衰变晶体", Color.red){{
            cost = 2f;
            flammability = 12f;
            explosiveness = 12f;
        }};
    }

    public static class MysteryItem extends Item{
        int stringLen = 12;
        public MysteryItem(String name, Color color){
            super(name, color);
        }

        @Override
        public void setStats(){}

        @Override
        public String displayDescription() {
            return "##MYSTERY##";
        }

        @Override
        public void displayExtra(Table table) {
            Label descLabel = null;
            for (Cell<?> cell : table.getCells()) {
                if (cell.get() instanceof Label l && l.getText().toString().contains("##MYSTERY##")){
                    descLabel = l;
                    break;
                }
            }
            if (descLabel != null) {
                descLabel.setText(chaosString(stringLen));
                Label finalDescLabel = descLabel;
                Events.run(EventType.Trigger.update, () -> finalDescLabel.setText(chaosString(stringLen)));
            }
        }

        public String chaosString(int length){
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < length; i++){
                int range = Mathf.random(8);
                switch(range){
                    case 0: sb.append((char) Mathf.random(0x4E00, 0xA000)); break; // 中日韩统一表意文字
                    case 1: sb.append((char) Mathf.random(0x0041, 0x005B)); break; // A-Z
                    case 2: sb.append((char) Mathf.random(0x0061, 0x007B)); break; // a-z
                    case 3: sb.append((char) Mathf.random(0x0030, 0x003A)); break; // 0-9
                    case 4: sb.append((char) Mathf.random(0x0080, 0x0100)); break; // Latin-1补充
                    case 5: sb.append((char) Mathf.random(0x0391, 0x03CA)); break; // 希腊字母
                    case 6: sb.append((char) Mathf.random(0x0400, 0x0500)); break; // 西里尔字母
                    default: sb.append((char) Mathf.random(0x2500, 0x2580)); break; // 制表符/方块
                }
            }
            return sb.toString();
        }
    }
}
