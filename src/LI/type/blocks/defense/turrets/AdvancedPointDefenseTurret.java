package LI.type.blocks.defense.turrets;

import arc.Core;
import arc.graphics.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.ui.*;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.meta.*;

public class AdvancedPointDefenseTurret extends PointDefenseTurret{
    public int defaultEnergy = 50;

    public AdvancedPointDefenseTurret(String name){
        super(name);
        bulletDamage = 0f;
        retargetTime = 0.05f;
        shootCone = 60f;
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
        Seq<Bullet> targets = new Seq<>();
        public int energy = defaultEnergy;

        @Override
        public void updateTile() {
            //retarget
            if(timer(timerTarget, retargetTime)){
                targets = Groups.bullet.intersect(x - range, y - range, range*2, range*2).sort(b -> b.dst2(this));
                target = targets.min(b -> b.team != team && b.type().hittable && b.isAdded(), b -> b.dst2(this));
            }

            if(energy <= 0){
                if(resuming){
                    reloadCounter += edelta();
                    if(reloadCounter >= reload){
                        energy = defaultEnergy;
                        resuming = false;
                        reloadCounter = 0;
                    }
                }else{
                    resuming = true;
                }
            }

            if(target != null && !resuming){
                float dest = angleTo(target);
                rotation = Angles.moveToward(rotation, dest, rotateSpeed * edelta());
                if(efficiency <= 0) return;

                for(var t : targets){
                    if(energy <= 0) break;
                    if(t != null && t.isAdded() && t.team != team && t.type() != null && t.type().hittable && energy > 0){
                        if(Angles.within(rotation, dest, shootCone) && t.within(this, range)){
                            t.remove();
                            Tmp.v1.trns(rotation, shootLength);
                            beamEffect.at(x + Tmp.v1.x, y + Tmp.v1.y, rotation, color, new Vec2().set(t));
                            shootEffect.at(x + Tmp.v1.x, y + Tmp.v1.y, rotation, color);
                            hitEffect.at(t.x, t.y, color);
                            shootSound.at(x + Tmp.v1.x, y + Tmp.v1.y, Mathf.random(0.9f, 1.1f));
                            energy--;
                        }
                    }else{
                        targets.remove(t);
                    }
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
            else return reloadCounter / reload;
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
