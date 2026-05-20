package LI.type.bullets;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.entities.Fires;
import mindustry.entities.Puddles;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.*;

import static LI.type.blocks.distribution.liquid.LiquidMassDriver.*;

public class LiquidMassDriverBolt extends BulletType{
    float orbSize, boilTime = 5f;
    public LiquidMassDriverBolt(float orbSize){
        super(1f, 75);
        collidesTiles = false;
        hitEffect = despawnEffect = Fx.hitLiquid;

        this.orbSize = orbSize;
    }

    @Override
    public void update(Bullet b){
        super.update(b);

        //data MUST be an instance of LiquidDriverBulletData
        if(!(b.data() instanceof LiquidDriverBulletData data)){
            hit(b);
            return;
        }

        float hitDst = 7f;
        if(data.to.dead()){
            return;
        }
        float baseDst = data.from.dst(data.to);
        float dst1 = b.dst(data.from);
        float dst2 = b.dst(data.to);

        boolean intersect = false;

        //bullet has gone past the destination point: but did it intersect it?
        if(dst1 > baseDst){
            float angleTo = b.angleTo(data.to);
            float baseAngle = data.to.angleTo(data.from);

            //if angles are nearby, then yes, it did
            if(Angles.near(angleTo, baseAngle, 2f)){
                intersect = true;
                //snap bullet position back; this is used for low-FPS situations
                b.set(data.to.x + Angles.trnsx(baseAngle, hitDst), data.to.y + Angles.trnsy(baseAngle, hitDst));
            }
        }

        //if on course and it's in range of the target
        if(Math.abs(dst1 + dst2 - baseDst) < 4f && dst2 <= hitDst){
            intersect = true;
        }
        if(intersect){
            data.to.handleLiquidPayload(b, data);
        }
    }

    @Override
    public void draw(Bullet b) {
        super.draw(b);
        var liquid = ((LiquidDriverBulletData)b.data).liquidType;
        if(liquid.willBoil()){
            Draw.color(liquid.color, Tmp.c3.set(liquid.gasColor), b.time / Mathf.randomSeed(b.id, boilTime));
            Fill.circle(b.x, b.y, orbSize * (b.fin() * 1.1f + 1f));
        }
        else{
            Draw.color(liquid.color, Color.white, b.fout() / 100);
            Fill.circle(b.x, b.y, orbSize);
        }
        Draw.reset();
    }

    @Override
    public void despawned(Bullet b) {
        super.despawned(b);
        var data = ((LiquidDriverBulletData)b.data);
        if(!data.liquidType.willBoil()){
            despawnEffect.at(b.x, b.y, b.rotation(), data.liquidType.color);
            if(!data.liquidType.incinerable){
                Puddles.deposit(Vars.world.tileWorld(b.x, b.y), data.liquidType, data.liquidAmount);
            }
        }
    }

    @Override
    public void hit(Bullet b, float x, float y) {
        var data = ((LiquidDriverBulletData)b.data);
        hitEffect.at(b.x, b.y, b.rotation(), data.liquidType.color);
        if(data.liquidAmount == 0) return;
        Puddles.deposit(Vars.world.tileWorld(b.x, b.y), data.liquidType, data.liquidAmount);
        if(data.liquidType.effect != null){
            Damage.status(b.team, b.x, b.y, 4 * Vars.tilesize, data.liquidType.effect, orbSize * 60, true, true);
        }
        if(data.liquidType.temperature >= 1 || data.liquidType.flammability >= 0.5){
            Fires.create(b.tileOn());
        }
        if(data.liquidType.explosiveness >= 0.5){
            Damage.damage(b.team, b.x, b.y, 4 * Vars.tilesize, damage * 2f, true);
        }
    }
}
