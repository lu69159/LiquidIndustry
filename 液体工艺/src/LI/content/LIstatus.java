package LI.content;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.*;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.Vars;
import mindustry.ctype.ContentType;
import mindustry.entities.effect.ParticleEffect;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.type.StatusEffect;
import mindustry.game.EventType.*;

import LI.type.status.ImmuneStatusEffect;

import static mindustry.content.StatusEffects.*;

public class LIstatus {
    public static StatusEffect SY,GZ,XM,BF,JD;

    public static void load(){
        GZ = new StatusEffect("管制"){{
            color = Color.valueOf("FF5050");
            healthMultiplier = 0.6f;
            reloadMultiplier = 0.5f;
            speedMultiplier = 0.4f;
            effectChance = 0.2f;
            effect = new ParticleEffect(){{
                particles = 1;
                baseLength = 15;
                length = -9;
                lifetime = 45;
                spin = 3;
                region = "液体工艺-square-k";
                interp = Interp.pow3Out;
                sizeInterp = Interp.pow5In;
                sizeFrom = 2;
                sizeTo = 0;
                lightColor = Color.valueOf("FF5050");
                colorFrom = Color.valueOf("FF5050FF");
                colorTo = Color.valueOf("FF505070");
            }};
        }};
        XM = new StatusEffect("雪盲"){{
            color = Color.white;
            healthMultiplier = 1.5f;
            speedMultiplier = 0.6f;
            dragMultiplier = 0.1f;
            init(() -> {
                opposite(melting, burning);
            });
        }};
        BF = new StatusEffect("冰封"){{
            color = Color.valueOf("C0ECFF");
            reloadMultiplier = 0.25f;
            speedMultiplier = 0f;
            transitionDamage = 40f;
            effectChance = 0.1f;
            effect = new ParticleEffect(){{
                particles = 1;
                baseLength = 10;
                length = -5;
                lifetime = 60;
                spin = 3;
                region = "液体工艺-snow";
                interp = Interp.pow3Out;
                sizeInterp = Interp.pow5In;
                sizeFrom = 3;
                sizeTo = 0;
                lightColor = Color.valueOf("D8F3FF");
                colorFrom = LIcolor.CJLDYcolor.cpy();
                colorTo = LIcolor.CJLDYcolor.cpy().a(70/255f);
            }};

            init(() -> {
                opposite(melting, burning);

                affinity(blasted, (unit, result, time) -> {
                    unit.damagePierce(transitionDamage);
                    if(unit.team == Vars.state.rules.waveTeam){
                        Events.fire(Trigger.blastFreeze);
                    }
                });
            });
        }};
        JD = new ImmuneStatusEffect("解冻", Seq.with(freezing, XM)){{
            color = Color.valueOf("6ECDEC");
            permanent = true;
        }};
        SY = new ImmuneStatusEffect("神佑"){
        {
            color = Color.valueOf("EFEFEF");
            applyEffect = LIfx.blessApply;
            healthMultiplier = 12f;
            damageMultiplier = 6f;
            reloadMultiplier = 4f;
            speedMultiplier = 3f;
            buildSpeedMultiplier = 2f;
            damage = -5f;
        }
            @Override
            public void setStats(){}
            @Override
            public void draw(Unit unit){
                Draw.z(Layer.effect);
                Draw.color(Color.valueOf("FFFFFF80"));
                Draw.rect(Core.atlas.find("液体工艺-神佑光环"),
                        unit.x + Angles.trnsx(unit.rotation - 90, 0, 0),
                        unit.y + Angles.trnsy(unit.rotation - 90, 0, 0),
                        128 * unit.hitSize / 26,
                        128 * unit.hitSize / 26,
                        Time.time * 1);
            }
        };

        //确保添加所有MOD的负面效果
        Events.on(ContentInitEvent.class, e ->{
            ((ImmuneStatusEffect)SY).immuneEffects = addBadEffects();
            SY.init();
        });
    }

    private static Seq<StatusEffect> addBadEffects(){
        Seq<StatusEffect> badEffects = new Seq<>();
        for(var status : Vars.content.getBy(ContentType.status)){
            StatusEffect s = (StatusEffect)status;
            var x = s.damageMultiplier * s.healthMultiplier * s.reloadMultiplier * s.speedMultiplier * s.buildSpeedMultiplier;
            if(x < 1f || s.damage > 0 || s.intervalDamage > 0 || s.transitionDamage > 0 || s.disarm){
                badEffects.add(s);
            }
        }
        return badEffects;
    }
}
