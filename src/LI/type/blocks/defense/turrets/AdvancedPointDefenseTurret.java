package LI.type.blocks.defense.turrets;

import arc.Core;
import arc.graphics.Color;
import arc.math.*;
import arc.math.geom.Vec2;
import arc.util.Tmp;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.ui.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.meta.*;

public class AdvancedPointDefenseTurret extends PointDefenseTurret{
    public int defaultEnergy = 50;

    public AdvancedPointDefenseTurret(String name){
        super(name);
        bulletDamage = 0f;
        retargetTime = 0.1f;
        rotateSpeed = 30f;
        color = Color.valueOf("F25555");
        reload = 5*60f;
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.remove(Stat.reload);
        stats.remove(Stat.damage);
        stats.add(Stat.cooldownTime, reload / 60f, StatUnit.seconds);
        stats.add(new Stat("maxtarget", StatCat.function), defaultEnergy);
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("energy", entity -> new Bar(
                Core.bundle.get("bar.energy"),
                color,
                () -> ((AdvancedPointDefenseTurretBuild)entity).getEnergyPercent()
        ));
    }

    public class AdvancedPointDefenseTurretBuild extends PointDefenseBuild{
        boolean resuming = false;
        public int energy = defaultEnergy;

        @Override
        public void updateTile() {
            //retarget
            if(timer(timerTarget, retargetTime)){
                target = Groups.bullet.intersect(x - range, y - range, range*2, range*2).min(b -> b.team != team && b.type().hittable, b -> b.dst2(this));
            }
            //pooled bullets
            if(target != null && !target.isAdded()){
                target = null;
            }

            if(energy <= 0){
                if(reloadCounter <= 0){
                    if(resuming){
                        energy = defaultEnergy;
                        resuming = false;
                    }else{
                        reloadCounter = reload;
                        resuming = true;
                    }
                }
                else reloadCounter -= edelta();
            }

            if(target != null && target.within(this, range) && target.team != team && target.type() != null && target.type().hittable && energy > 0){
                float dest = angleTo(target);
                rotation = Angles.moveToward(rotation, dest, rotateSpeed * edelta());

                if(Angles.within(rotation, dest, shootCone)){
                    target.remove();
                    Tmp.v1.trns(rotation, shootLength);
                    beamEffect.at(x + Tmp.v1.x, y + Tmp.v1.y, rotation, color, new Vec2().set(target));
                    shootEffect.at(x + Tmp.v1.x, y + Tmp.v1.y, rotation, color);
                    hitEffect.at(target.x, target.y, color);
                    shootSound.at(x + Tmp.v1.x, y + Tmp.v1.y, Mathf.random(0.9f, 1.1f));
                    energy--;
                }
            }
        }

        @Override
        public BlockStatus status() {
            return super.status();
        }

        @Override
        public boolean shouldConsume() {
            return enabled && (target != null || energy <= 0);
        }

        public float getEnergyPercent(){
            if(energy > 0) return (float)energy / defaultEnergy;
            else return 1f - (reloadCounter / reload);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            energy = read.i();
            resuming = read.bool();
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.i(energy);
            write.bool(resuming);
        }
    }
}
