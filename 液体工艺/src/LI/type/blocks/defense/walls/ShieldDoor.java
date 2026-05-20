package LI.type.blocks.defense.walls;

import arc.func.Cons;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.util.*;
import mindustry.content.Fx;
import mindustry.entities.Units;
import mindustry.gen.Unit;
import mindustry.graphics.*;
import mindustry.world.blocks.defense.ShieldWall;

import static mindustry.Vars.*;

public class ShieldDoor extends ShieldWall {
    public Color shieldColor = Color.cyan.cpy();

    public ShieldDoor(String name){
        super(name);
        absorbLasers = hasPower = conductivePower = true;
        solid = outputsPower = false; //hasShadow ?
        health = 2500;
        armor = 10;
        shieldHealth = 5000;
        size = 2;
        regenSpeed = 5f;
        breakCooldown = 180f;
        glowColor = Color.white.cpy().a(192/255f);
    }

    public class ShieldDoorBuild extends ShieldWallBuild{
        private float heat = 0f;

        private final Cons<Unit> unitConsumer = unit -> {
            float dst = unit.hitSize/2 + shieldRadius + 10f - unit.dst(this);
            if(dst > 0){
                unit.vel.setZero();
                unit.move(Tmp.v1.set(unit).sub(this).setLength(dst + 0.01f));
                if(Mathf.chanceDelta(0.12 * Time.delta)){
                    Fx.circleColorSpark.at(unit.x, unit.y, shieldColor);
                }
            }
        };

        @Override
        public void updateTile() {
            heat = Mathf.lerpDelta(heat, efficiency > 0 ? 1f : 0f, 0.1f);
            if(power.status > 0){
                if(!broken()){
                    Units.nearbyEnemies(team, x, y, shieldRadius + 10, unitConsumer);
                }
                if(breakTimer > 0){
                    breakTimer -= Time.delta * efficiency;
                }
                else{
                    shield = Mathf.clamp(shield + regenSpeed * efficiency * edelta(), 0, shieldHealth * efficiency);
                }
            }

            if(hit > 0){
                hit -= Time.delta / 10f;
                hit = Math.max(hit, 0f);
            }

            shieldRadius = Mathf.lerpDelta(shieldRadius, broken() ? 0f : 1f, 0.12f);
        }

        @Override
        public void draw() {
            Draw.rect(region, x, y);
            Draw.color(team.color);
            if(teamRegion != null){
                Draw.alpha(heat * ((1 - glowMag + Mathf.absin(glowScl, glowMag)) * shieldRadius));
                Draw.rect(teamRegion, x, y);
                Draw.reset();
            }
            if(shieldRadius > 0){
                float radius = shieldRadius * tilesize * size / 2f;
                Draw.z(Layer.shields + 0.01f);
                Draw.color(shieldColor, Color.white, Mathf.clamp(hit));
                drawShield(radius);
                Draw.reset();
                Drawf.additive(glowRegion, glowColor, (1 - glowMag + Mathf.absin(glowScl, glowMag)) * shieldRadius, x, y, 0, Layer.blockAdditive);
            }
        }

        public void drawShield(float radius){
            if(renderer.animateShields){
                Fill.square(this.x, this.y, radius);
            }else{
                Lines.stroke(1.5f);
                Draw.alpha(0.09f + Mathf.clamp(0.08f * hit));
                Fill.square(this.x, this.y, radius);
                Draw.alpha(1);
                Lines.poly(this.x, this.y, 4, radius, 45);
                Draw.reset();
            }
        }

        @Override
        public boolean absorbLasers() {
            return absorbLasers && !broken();
        }
    }
}
