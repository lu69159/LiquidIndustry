package LI.type.blocks.defense.turrets;

import LI.type.bullets.PointLightningBulletType;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.pattern.ShootPattern;
import mindustry.world.blocks.defense.turrets.PowerTurret;

public class PointLightningTurret extends PowerTurret{
    public PointLightningTurret(String name) {
        super(name);
        predictTarget = moveWhileCharging = false;
        shoot.firstShotDelay = 40f;

        size = 4;
        reload = 240f;
        range = 320f;
        shootType = new PointLightningBulletType(1600f){{
            lifetime = 80f;
            speed = 2.5f;
        }};
    }

    public class PointLightningTurretBuld extends PowerTurretBuild{

        @Override
        protected void shoot(BulletType type){
            float lockX = targetPos.getX(), lockY = targetPos.getY();
            float len = Mathf.dst(x, y, lockX, lockY);
            if(len > range){
                lockX = x + (lockX - x) * range / len;
                lockY = y + (lockY - y) * range / len;
            }

            if(shoot.firstShotDelay > 0){
                chargeSound.at(x, y, Mathf.random(soundPitchMin, soundPitchMax));
                type.chargeEffect.at(lockX, lockY, rotation);
            }

            ShootPattern pattern = type.shootPattern != null ? type.shootPattern : shoot;

            float finalLockX = lockX, finalLockY = lockY;
            pattern.shoot(barrelCounter, (xOffset, yOffset, angle, delay, mover) -> {
                queuedBullets++;
                int barrel = barrelCounter;

                if(delay > 0f){
                    Time.run(delay, () -> {
                        int prev = barrelCounter;
                        barrelCounter = barrel;
                        targetPos.set(finalLockX, finalLockY);
                        bullet(type, xOffset, yOffset, angle, mover);
                        barrelCounter = prev;
                    });
                }else{
                    targetPos.set(finalLockX, finalLockY);
                    bullet(type, xOffset, yOffset, angle, mover);
                }
            }, () -> barrelCounter++);

            if(consumeAmmoOnce){
                useAmmo();
            }
        }
    }
}
