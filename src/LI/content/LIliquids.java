package LI.content;

import arc.util.Time;
import arc.graphics.Color;
import mindustry.Vars;
import mindustry.gen.Puddle;
import mindustry.type.Liquid;
import mindustry.entities.Fires;
import mindustry.content.StatusEffects;

public class LIliquids {
    public static Liquid ZS,CJLDY,SBRY,FY0,FY1,FY2,FY3,FY4,FY5,FY6;

    public static void load(){
        ZS = new Liquid("重水", Color.valueOf("3D6E70")){{
            lightColor = Color.valueOf("3CA370");
            effect = StatusEffects.electrified;
            temperature = 0.3f;
            heatCapacity = 0.6f;
            viscosity = 0.3f;
            boilPoint = 0.52f;
            coolant = false;
        }};
        CJLDY = new Liquid("超级冷冻液", Color.valueOf("C0ECFF")){{
            lightColor = Color.valueOf("D8F3FF");
            effect = LIstatus.BF;
            temperature = 0.01f;
            heatCapacity = 2.1f;
            viscosity = 0.5f;
            boilPoint = 0.6f;
        }};
        SBRY = new Liquid("衰变熔岩", Color.valueOf("FF0000")){{
            lightColor = Color.valueOf("FF2020");
            effect = StatusEffects.burning;
            flammability = 6f;
            explosiveness = 6f;
            temperature = 3f;
            heatCapacity = 0.01f;
            viscosity = 0.85f;
            boilPoint = 6f;
            incinerable = false;
        }
            float fireTimer = 0;
            @Override
            public void update(Puddle puddle){
                if(!Vars.state.rules.fire) return;
                this.fireTimer += Time.delta;
                if(this.fireTimer >= 60){
                    Fires.create(puddle.tile);
                    this.fireTimer = 0;
                }
                super.update(puddle);
            }

            @Override
            public boolean willBoil(){
                return false;
            }
        };
        FY0 = new Liquid("冰冷废液", Color.valueOf("9B928B")){{
            lightColor = Color.valueOf("E0B28D");
            temperature = 0.1f;
            heatCapacity = 0.5f;
            viscosity = 0.5f;
            boilPoint = 2f;
            coolant = false;
        }};
        FY1 = new Liquid("一级精炼废液", Color.valueOf("848695")){{
            lightColor = Color.valueOf("989BAD");
            temperature = 0.15f;
            heatCapacity = 0.4f;
            viscosity = 0.6f;
            boilPoint = 2f;
            coolant = false;
        }};
        FY2 = new Liquid("二级精炼废液", Color.valueOf("9A9DBF")){{
            lightColor = Color.valueOf("EDEDED");
            temperature = 0.25f;
            heatCapacity = 0.3f;
            viscosity = 0.7f;
            boilPoint = 2f;
            coolant = false;
        }};
        FY3 = new Liquid("三级精华废液", Color.valueOf("B7BADA")){{
            lightColor = Color.valueOf("EFEFEF");
            temperature = 0.5f;
            heatCapacity = 0.2f;
            viscosity = 0.75f;
            boilPoint = 2.1f;
            coolant = false;
        }};
        FY4 = new Liquid("四级浓缩精华液", Color.valueOf("C7C9DC")){{
            lightColor = Color.white.cpy();
            temperature = 0.75f;
            heatCapacity = 0.12f;
            viscosity = 0.85f;
            boilPoint = 2.3f;
            coolant = false;
        }};
        FY5 = new Liquid("超浓缩精华液", Color.valueOf("EFEFEF")){{
            lightColor = Color.white.cpy();
            effect = LIstatus.SY;
            temperature = 1.5f;
            heatCapacity = 0.05f;
            viscosity = 0.9f;
            boilPoint = 2.5f;
            coolant = false;
        }};
        FY6 = new Liquid("神能精华液", Color.white){{
            hideDatabase = true;
            coolant = false;
        }};
    }
}
