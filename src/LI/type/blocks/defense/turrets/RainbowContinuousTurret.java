package LI.type.blocks.defense.turrets;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.struct.ObjectMap;
import arc.util.Time;
import arc.util.io.*;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.UnitSorts;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.ContinuousBulletType;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.blocks.defense.turrets.ContinuousTurret;
import LI.type.bullets.RainbowPointLaserBulletType;
import mindustry.world.meta.*;

public class RainbowContinuousTurret extends ContinuousTurret {
    public TextureRegion lightRegion, warmUpRegion, base;
    public float damageMin = 30/12f, damageMax = 3000/12f;
    public float beforeWarmUpTime = 5 * 60f, warmUpTime = 15 * 60f;;

    public RainbowContinuousTurret(String name) {
        super(name);
        rotate = emitLight = scaleDamageEfficiency = true;
        canOverdrive = playerControllable = false;
        shootSound = Sounds.none;
        loopSoundVolume = 1f;
        loopSound = Sounds.beamLustre;
        aimChangeSpeed = 1.5f;
        shootCone = 360f;
        shootY = 0f;
        aimChangeSpeed = 0.9f;
        rotateSpeed = 0.9f;
        shootWarmupSpeed = 0.01f;
        unitSort = UnitSorts.strongest;
        shootType = new RainbowPointLaserBulletType(){{
            pierceArmor = true;
            damage = damageMin;
            buildingDamageMultiplier = 0.01f;
            lifetime = 10;
        }};
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(new Stat("maxdamage", StatCat.function), damageMax * 60 / ((RainbowPointLaserBulletType)shootType).damageInterval , StatUnit.perSecond);
    }

    @Override
    public void setBars() {
        super.setBars();
        removeBar("reload");
        addBar("damageprogress", entity -> new Bar(
            Core.bundle.get("bar.damageprogress"),
            Pal.accent,
            () -> ((RainbowContinuousTurretBuild)entity).getDamageProgress()
        ));
    }

    @Override
    public void load() {
        super.load();
        lightRegion = Core.atlas.find(name + "-light");
        warmUpRegion = Core.atlas.find(name + "-warmup");
        base = Core.atlas.find("block-4");
    }

    public class RainbowContinuousTurretBuild extends ContinuousTurretBuild{
        public Color lightColor = Color.red;
        public float shootTime, damageProgress = 0f;
        public RainbowPointLaserBulletType realShootType = new RainbowPointLaserBulletType(){{
            pierceArmor = true;
            sprite = "液体工艺-white-point-laser";
            hitEffect = Fx.hitBulletColor;
            damage = damageMin;
            buildingDamageMultiplier = 0.01f;
            lifetime = 10;
        }};

        @Override
        public void created() {
            super.created();
            realShootType.set(this);
            realShootType.load();
        }

        public float getDamageProgress(){
            return damageProgress;
        }

        @Override
        public float estimateDps(){
            return damageMax * 60f / (shootType instanceof ContinuousBulletType c ? c.damageInterval : 5f);
        }

        public void rainbow(){
            var time = Time.time * 0.001;
            var cycleTime = 1; //完整循环秒
            var phase = (time % cycleTime) / cycleTime;

            if(phase < 1/3f) {
                float progress = (float) (phase * 3);
                lightColor = Color.red.cpy().lerp(Color.green.cpy(), progress);
            } else if(phase < 2/3f) {
                float progress = (float) ((phase - 1/3f) * 3);
                lightColor = Color.green.cpy().lerp(Color.blue.cpy(), progress);
            } else {
                float progress = (float) ((phase - 2/3f) * 3);
                lightColor = Color.blue.cpy().lerp(Color.red.cpy(), progress);
            }
        }

        public Color realColor(){
            return lightColor.cpy().lerp(Color.white, damageProgress);
        }

        @Override
        public void updateTile() {
            super.updateTile();
            rainbow();

            if(isShooting() && efficiency > 0){
                shootTime += Time.delta;
                if(shootTime > beforeWarmUpTime){
                    if(shootTime > beforeWarmUpTime + warmUpTime) shootTime = beforeWarmUpTime + warmUpTime;
                    damageProgress = (shootTime - beforeWarmUpTime)/warmUpTime;
                }
            }
            else{
                damageProgress = 0f;
                shootTime = 0f;
            }
        }

        @Override
        public void draw() {
            super.draw();
            Draw.z(Layer.turret + 0.1f);
            Draw.color(realColor());
            Draw.rect(lightRegion, x, y);
            if(damageProgress > 0){
                float sin = Vars.tilesize * size * damageProgress * (float)(0.15f * Math.sin(Time.time * 0.05f) + 0.85f);
                Draw.z(Layer.turret + 0.2f);
                Draw.rect(warmUpRegion, x, y, sin, sin, Time.time * 1);
            }
            Draw.reset();
        }

        @Override
        public void drawLight() {
            Vars.renderer.lights.add(x, y, 20 * Vars.tilesize, realColor(), 1f);
        }

        @Override
        public BulletType peekAmmo(){ return realShootType; }

        @Override
        public BulletType useAmmo(){ return realShootType; }

        @Override
        protected void updateBullet(BulletEntry entry) {}

        @Override
        protected void handleBullet(Bullet bullet, float offsetX, float offsetY, float angleOffset) {
            if(bullet != null){
                bullet.damage = (damageMin + (damageMax - damageMin) * damageProgress) * 1/6f * Math.min(efficiency, 1f);

                var shootLength = Mathf.dst(unit.x(), unit.y(), targetPos.x, targetPos.y);

                if(shootLength > range){
                    bullet.aimX = unit.x() + (targetPos.x - unit.x()) * (range / shootLength);
                    bullet.aimY = unit.y() + (targetPos.y - unit.y()) * (range / shootLength);
                }

                bullet.time = bullet.lifetime * bullet.type.optimalLifeFract * Math.min(shootWarmup, efficiency);
                bullet.keepAlive = true;

                wasShooting = true;
                heat = 1;
            }
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(shootTime);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            shootTime = read.f();
        }
    }
}
