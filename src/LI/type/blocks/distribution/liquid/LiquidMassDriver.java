package LI.type.blocks.distribution.liquid;

import arc.*;
import arc.audio.*;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import arc.util.pooling.Pool.*;
import arc.util.pooling.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.meta.*;
import LI.content.LIfx;
import LI.type.bullets.LiquidMassDriverBolt;

import static mindustry.Vars.*;

public class LiquidMassDriver extends Block{
    public float range = 864f;
    public float rotateSpeed = 5f;
    public float translation = 7f;
    public float minDistribute = 400f;
    public float knockback = 4f;
    public float reload = 180f;
    public float bulletOrbSize = 4f;
    public float bulletSpeed = 5.5f;
    public float bulletLifetime = 200f;
    public Effect shootEffect = Fx.shootLiquid;
    public Effect smokeEffect = LIfx.shootBigSmokeColor;
    public Effect receiveEffect = Fx.hitLiquid;
    public Sound shootSound = Sounds.massdriver;
    public Sound receiveSound = Sounds.massdriverReceive;
    public float shootSoundVolume = 0.5f;
    public float shake = 3f;
    public TextureRegion baseRegion, liquidRegion, topRegion;

    public LiquidMassDriver(String name){
        super(name);
        update = true;
        solid = true;
        configurable = true;
        hasLiquids = true;
        hasPower = true;
        sync = true;
        liquidCapacity = 4800f;

        //point2 is relative
        config(Point2.class, (LiquidMassDriverBuild tile, Point2 point) -> tile.link = Point2.pack(point.x + tile.tileX(), point.y + tile.tileY()));
        config(Integer.class, (LiquidMassDriverBuild tile, Integer point) -> tile.link = point);
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.add(Stat.shootRange, range / tilesize, StatUnit.blocks);
        stats.add(Stat.reload, table -> table.add(Strings.autoFixed(60f / reload, 2) + StatUnit.perSecond.localized()));
        //stats.add(Stat.receiveRate, 60f, StatUnit.itemsSecond);
    }

    @Override
    public void load() {
        super.load();
        baseRegion = Core.atlas.find(name + "-base");
        liquidRegion = Core.atlas.find(name + "-liquid");
        topRegion = Core.atlas.find(name + "-top");
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{baseRegion, region, topRegion};
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        Drawf.dashCircle(x * tilesize, y * tilesize, range, Pal.accent);

        //check if a mass driver is selected while placing this driver
        if(!control.input.config.isShown()) return;
        Building selected = control.input.config.getSelected();
        if(selected == null || selected.block != this || !selected.within(x * tilesize, y * tilesize, range)) return;

        //if so, draw a dotted line towards it while it is in range
        float sin = Mathf.absin(Time.time, 6f, 1f);
        Tmp.v1.set(x * tilesize + offset, y * tilesize + offset).sub(selected.x, selected.y).limit((size / 2f + 1) * tilesize + sin + 0.5f);
        float x2 = x * tilesize - Tmp.v1.x, y2 = y * tilesize - Tmp.v1.y,
                x1 = selected.x + Tmp.v1.x, y1 = selected.y + Tmp.v1.y;
        int segs = (int)(selected.dst(x * tilesize, y * tilesize) / tilesize);

        Lines.stroke(4f, Pal.gray);
        Lines.dashLine(x1, y1, x2, y2, segs);
        Lines.stroke(2f, Pal.placing);
        Lines.dashLine(x1, y1, x2, y2, segs);
        Draw.reset();
    }

    public static class LiquidDriverBulletData implements Poolable{
        public LiquidMassDriverBuild from, to;
        public Liquid liquidType;
        public float liquidAmount = 0f;

        @Override
        public void reset(){
            from = null;
            to = null;
            liquidType = null;
        }
    }

    public class LiquidMassDriverBuild extends Building implements RotBlock{
        public int link = -1;
        public float rotation = 90;
        public float reloadCounter = 0f;
        public LiquidDriverState state = LiquidDriverState.idle;
        public OrderedSet<Building> waitingShooters = new OrderedSet<>();

        @Override
        public float buildRotation(){
            return rotation;
        }

        public Building currentShooter(){
            return waitingShooters.isEmpty() ? null : waitingShooters.first();
        }

        public boolean canShoot(Building b){ //是否可以向b发射液体
            return liquids != null && liquids.currentAmount() >= minDistribute && b instanceof LiquidMassDriverBuild l &&
            (
                l.liquids == null ||
                (l.liquids.current() == liquids.current() && l.liquids.currentAmount() + minDistribute < liquidCapacity) ||
                l.liquids.currentAmount() < 0.2f
            );
        }

        @Override
        public void updateTile(){
            Building link = world.build(this.link);
            boolean hasLink = liquidLinkValid();

            if(hasLink){
                this.link = link.pos();
            }

            //reload regardless of state
            if(reloadCounter > 0f){
                reloadCounter = Mathf.clamp(reloadCounter - edelta() / reload);
            }

            var current = currentShooter();

            //cleanup waiting shooters that are not valid
            if(current != null && !liquidShooterValid(current)){
                waitingShooters.remove(current);
            }

            //switch states
            if(state == LiquidDriverState.idle){
                //start accepting when idle and there's space
                if(!waitingShooters.isEmpty() && (liquids == null || (liquidCapacity - liquids.currentAmount() >= minDistribute))){
                    state = LiquidDriverState.accepting;
                }else if(hasLink && liquids != null){ //有液体和连接
                    state = LiquidDriverState.shooting;
                }
            }
            if(state == LiquidDriverState.idle || state == LiquidDriverState.accepting){
                if(liquids != null) dumpLiquid(liquids.current(), 1);
            }
            if(efficiency <= 0f){
                return;
            }

            if(state == LiquidDriverState.accepting){
                if(currentShooter() == null || liquidCapacity - liquids.currentAmount() < minDistribute){
                    state = LiquidDriverState.idle;
                    return;
                }

                rotation = Angles.moveToward(rotation, angleTo(currentShooter()), rotateSpeed * efficiency);
            }else if(state == LiquidDriverState.shooting){
                //if there's nothing to shoot at OR someone wants to shoot at this thing, bail
                if(!hasLink){
                    state = LiquidDriverState.idle;
                    return;
                }
                if(!waitingShooters.isEmpty()){
                    for(var shooter: waitingShooters){
                        if(shooter instanceof LiquidMassDriverBuild l && l.canShoot(this)){
                            state = LiquidDriverState.idle;
                            return;
                        }
                    }
                }

                float targetRotation = angleTo(link);

                if(canShoot(link)){
                    LiquidMassDriverBuild other = (LiquidMassDriverBuild)link;
                    other.waitingShooters.add(this);

                    if(reloadCounter <= 0.0001f){

                        rotation = Angles.moveToward(rotation, targetRotation, rotateSpeed * efficiency);

                        //fire when it's the first in the queue and angles are ready.
                        if(other.currentShooter() == this &&
                                other.state == LiquidDriverState.accepting &&
                                Angles.near(rotation, targetRotation, 2f) && Angles.near(other.rotation, targetRotation + 180f, 2f)){
                            //actually fire
                            fireLiquid(other);
                            float timeToArrive = Math.min(bulletLifetime, dst(other) / bulletSpeed);
                            Time.run(timeToArrive, () -> {
                                //remove waiting shooters, it's done firing
                                other.waitingShooters.remove(this);
                                other.state = LiquidDriverState.idle;
                            });
                            //driver is immediately idle
                            state = LiquidDriverState.idle;
                        }
                    }
                }
            }
        }

        @Override
        public double sense(LAccess sensor){
            if(sensor == LAccess.progress) return Mathf.clamp(1f - reloadCounter);
            return super.sense(sensor);
        }

        @Override
        public void draw(){
            Draw.rect(baseRegion, x, y);
            Draw.z(Layer.turret);

            Drawf.shadow(region,
                    x + Angles.trnsx(rotation + 180f, reloadCounter * knockback) - (size / 2f),
                    y + Angles.trnsy(rotation + 180f, reloadCounter * knockback) - (size / 2f), rotation - 90);
            Draw.rect(region,
                    x + Angles.trnsx(rotation + 180f, reloadCounter * knockback),
                    y + Angles.trnsy(rotation + 180f, reloadCounter * knockback), rotation - 90);
            if(liquids != null){
                Draw.color(Color.black.cpy().lerp(liquids.current().color, liquids.currentAmount()/liquidCapacity));
                Draw.rect(liquidRegion,
                        x + Angles.trnsx(rotation + 180f, reloadCounter * knockback),
                        y + Angles.trnsy(rotation + 180f, reloadCounter * knockback), rotation - 90);
            }
            Draw.color();
            Draw.rect(topRegion,
                    x + Angles.trnsx(rotation + 180f, reloadCounter * knockback),
                    y + Angles.trnsy(rotation + 180f, reloadCounter * knockback), rotation - 90);
        }

        @Override
        public void drawConfigure(){
            float sin = Mathf.absin(Time.time, 6f, 1f);

            Draw.color(Pal.accent);
            Lines.stroke(1f);
            Drawf.circles(x, y, (tile.block().size / 2f + 1) * tilesize + sin - 2f, Pal.accent);

            for(var shooter : waitingShooters){
                Drawf.circles(shooter.x, shooter.y, (tile.block().size / 2f + 1) * tilesize + sin - 2f, Pal.place);
                Drawf.arrow(shooter.x, shooter.y, x, y, size * tilesize + sin, 4f + sin, Pal.place);
            }

            if(liquidLinkValid()){
                Building target = world.build(link);
                Drawf.circles(target.x, target.y, (target.block.size / 2f + 1) * tilesize + sin - 2f, Pal.place);
                Drawf.arrow(x, y, target.x, target.y, size * tilesize + sin, 4f + sin);
            }

            Drawf.dashCircle(x, y, range, Pal.accent);
        }

        @Override
        public boolean onConfigureBuildTapped(Building other){
            if(this == other){
                if(link == -1) deselect();
                configure(-1);
                return false;
            }

            if(link == other.pos()){
                configure(-1);
                return false;
            }else if(other.block == block && other.dst(tile) <= range && other.team == team){
                configure(other.pos());
                return false;
            }

            return true;
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            return liquidLinkValid() && state == LiquidDriverState.shooting && !liquid.gas &&
                    liquids == null || (liquids.current() == liquid && liquids.currentAmount() < liquidCapacity) || liquids.currentAmount() < 0.2f;
        }

        protected void fireLiquid(LiquidMassDriverBuild target){
            //reset reload, use power.
            reloadCounter = 1f;

            LiquidDriverBulletData data = Pools.obtain(LiquidDriverBulletData.class, LiquidDriverBulletData::new);
            data.from = this;
            data.to = target;
            data.liquidType = liquids.current();
            data.liquidAmount = Math.min(liquids.currentAmount(), target.block.liquidCapacity - target.liquids.currentAmount());
            liquids.remove(data.liquidType, data.liquidAmount);

            float angle = tile.angleTo(target);

            LiquidMassDriverBolt bullet = new LiquidMassDriverBolt(bulletOrbSize){{
                hitColor = data.liquidType.color;
            }};
            bullet.create(this, this.team,
                    this.x + Angles.trnsx(angle, translation), this.y + Angles.trnsy(angle, translation),
                    angle, 80f * data.liquidAmount / liquidCapacity, bulletSpeed, bulletLifetime, data);

            shootEffect.at(x + Angles.trnsx(angle, translation), y + Angles.trnsy(angle, translation), angle, data.liquidType.color);
            smokeEffect.at(x + Angles.trnsx(angle, translation), y + Angles.trnsy(angle, translation), angle, data.liquidType.color);

            Effect.shake(shake, shake, this);

            shootSound.at(x, y, 1f + Mathf.range(0.2f), shootSoundVolume);
        }

        public void handleLiquidPayload(Bullet bullet, LiquidDriverBulletData data){
            liquids.add(data.liquidType, data.liquidAmount);
            data.liquidAmount = 0;

            if(liquids.currentAmount() >= 1.5f * liquidCapacity){
                float remove = liquids.currentAmount() - 1.5f * liquidCapacity;
                liquids.remove(liquids.current(), remove);
            }

            Effect.shake(shake, shake, this);
            receiveEffect.at(bullet.getX(), bullet.getY(), data.liquidType.color);
            receiveSound.at(x, y, 1f + Mathf.range(0.2f), shootSoundVolume);

            reloadCounter = 1f;
            bullet.remove();
        }

        protected boolean liquidShooterValid(Building other){
            return other instanceof LiquidMassDriverBuild entity && other.isValid() && other.efficiency > 0 && entity.block == block && entity.link == pos() && within(other, range);
        }

        protected boolean liquidLinkValid(){
            if(link == -1) return false;
            return world.build(this.link) instanceof LiquidMassDriverBuild other && other.block == block && other.team == team && within(other, range);
        }

        @Override
        public Point2 config(){
            if(tile == null) return null;
            return Point2.unpack(link).sub(tile.x, tile.y);
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.i(link);
            write.f(rotation);
            write.b((byte)state.ordinal());
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            link = read.i();
            rotation = read.f();
            state = LiquidDriverState.all[read.b()];
        }
    }

    public enum LiquidDriverState{
        idle, //nothing is shooting at this mass driver and it does not have any target
        accepting, //currently getting shot at, unload items
        shooting;

        public static final LiquidDriverState[] all = values();
    }
}
