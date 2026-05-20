package LI.type.bullets;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.util.Nullable;
import arc.util.Time;
import mindustry.entities.bullet.PointLaserBulletType;
import LI.type.blocks.defense.turrets.RainbowContinuousTurret;
import mindustry.gen.Bullet;
import mindustry.graphics.Drawf;

public class RainbowPointLaserBulletType extends PointLaserBulletType {
    public @Nullable RainbowContinuousTurret.RainbowContinuousTurretBuild building;

    public RainbowPointLaserBulletType(){
        super();
        sprite = "液体工艺-white-point-laser";
    }

    public RainbowPointLaserBulletType set(RainbowContinuousTurret.RainbowContinuousTurretBuild b){
        building = b;
        return this;
    }

    public Color getColor(){
        if(building != null){
            return building.realColor();
        }
        return Color.white.cpy();
    }

    @Override
    public void draw(Bullet b) {
        drawTrail(b);
        drawParts(b);

        hitColor = getColor();
        Draw.color(hitColor);
        Drawf.laser(laser, laserEnd, b.x, b.y, b.aimX, b.aimY, b.fslope() * (1f - oscMag + Mathf.absin(Time.time, oscScl, oscMag)));
        Draw.reset();
    }

    @Override
    public void drawTrail(Bullet b){
        if(trailLength > 0 && b.trail != null){
            //draw below bullets? TODO
            float z = Draw.z();
            Draw.z(z - 0.0001f);
            b.trail.draw(getColor(), trailWidth);
            Draw.z(z);
        }
    }

    @Override
    public void drawLight(Bullet b){
        if(lightOpacity <= 0f || lightRadius <= 0f) return;
        Drawf.light(b, lightRadius, getColor(), lightOpacity);
    }

    @Override
    public void updateTrailEffects(Bullet b){
        if(trailChance > 0){
            if(Mathf.chanceDelta(trailChance)){
                trailEffect.at(b.aimX, b.aimY, trailRotation ? b.angleTo(b.aimX, b.aimY) : (trailParam * b.fslope()), getColor());
            }
        }

        if(trailInterval > 0f){
            if(b.timer(0, trailInterval)){
                trailEffect.at(b.aimX, b.aimY, trailRotation ? b.angleTo(b.aimX, b.aimY) : (trailParam * b.fslope()), getColor());
            }
        }
    }
}
