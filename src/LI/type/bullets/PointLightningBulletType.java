package LI.type.bullets;

import arc.graphics.*;
import arc.math.*;
import mindustry.content.*;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.WaveEffect;
import mindustry.gen.Bullet;
import mindustry.graphics.*;
import LI.content.*;

public class PointLightningBulletType extends BulletType{
    WaveEffect wave;

    public PointLightningBulletType(float damage){
        this(damage, 80f);
    };
    public PointLightningBulletType(float Pdamage, float Prange){
        hittable = reflectable = keepVelocity = false;
        despawnHit = scaleLife = true;
        this.damage = splashDamage = Pdamage;
        splashDamageRadius = Prange;
        despawnEffect = shootEffect = smokeEffect = Fx.none;
        hitEffect = Fx.hitLancer;
        hitSound = LIaudio.lightning;

        chargeEffect = LIfx.pointLightningCharge(Prange);

        status = StatusEffects.shocked;
        lightningColor = Pal.lancerLaser;
        hitColor = Color.white;

        wave = new WaveEffect(){{
            lifetime = 15f;
            strokeFrom = 6f;
            sizeFrom = sizeTo = splashDamageRadius;
            colorFrom = colorTo = Color.white;
        }};

        fragBullet = new LightningBulletType(){{
            damage = Pdamage / 32f;
            lightningColor = Color.white;
            lightningLength = Mathf.ceil((Prange / 8f) * 5/4f);
            lightningLengthRand = Mathf.ceil(lightningLength / 4f);
        }};
        fragBullets = 16;
    }

    @Override
    public void init(Bullet b){
        super.init(b);
        float px = b.x + b.lifetime * b.vel.x,
                py = b.y + b.lifetime * b.vel.y;

        b.set(px, py);
        b.time = b.lifetime;
        LIfx.pointLightning.at(b.x, b.y, lightningColor);
        LIfx.pointLightning.at(b.x, b.y, lightningColor);
        LIfx.pointLightning.at(b.x, b.y, lightningColor);
        wave.at(b.x, b.y);
    }
}
