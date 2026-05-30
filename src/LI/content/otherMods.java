package LI.content;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.Log;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.entities.Effect;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.*;
import mindustry.gen.Sounds;
import mindustry.mod.*;
import mindustry.type.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.units.*;

import java.util.ArrayList;
import java.util.Arrays;

import static arc.graphics.g2d.Draw.*;
import static arc.graphics.g2d.Lines.*;

public class otherMods {
    public static void loadOverride(){
        Mods.LoadedMod SF1 = Vars.mods.locateMod("饱和火力"), SF2 = Vars.mods.locateMod("sfire-mod");
        if(hadMod(SF1)){
            SF1change();
        }
        if(hadMod(SF2)){
            SF2change();
        }
    }

    private static boolean hadMod(Mods.LoadedMod mod){
        return mod != null && mod.state == Mods.ModState.enabled;
    }

    /**
     * 以下为其他模组适配改动
     * XXXchange对应XXX模组改动
     */
    private static void SF1change(){
        Log.info("发现饱和火力(3.3.0+), 加载对应适配");

        //region 本体修改
        //电磁风暴
        LIblocks.DCFB.requirements = ItemStack.with(Items.lead, 1500, Items.metaglass, 1000, Items.silicon, 750, Items.graphite,750, Items.surgeAlloy, 500, LIitems.QSZ, 125, LIitems.GTS, 10, Vars.content.getByName(ContentType.item, "饱和火力-二级协议"), 3);
        ((ItemTurret)LIblocks.DCFB).ammoTypes.get(LIitems.CDZ).status = Vars.content.getByName(ContentType.status, "饱和火力-干扰");
        BasicBulletType DCFB1 = (BasicBulletType)((ItemTurret)LIblocks.DCFB).ammoTypes.get(LIitems.GTS);
        DCFB1.status = Vars.content.getByName(ContentType.status, "饱和火力-崩溃");
        DCFB1.hitColor = Color.valueOf("EEC591");
        DCFB1.damage = 1500f;
        DCFB1.intervalBullet = new LightningBulletType(){{
            pierceArmor = true;
            lightningColor = Color.valueOf("EEC591");
            damage = 10f;
            buildingDamageMultiplier = 0.05f;
            lightningLength = 5;
        }};
        DCFB1.fragBullet = new PointBulletType(){{
            trailEffect = Fx.none;
            hitEffect = Fx.instHit;
            status = Vars.content.getByName(ContentType.status, "饱和火力-崩溃");
            statusDuration = 150f;
            damage = 100f;
            splashDamage = 200f;
            splashDamageRadius = 80f;
            lightning = 3;
            lightningDamage = 50f;
            lightningLength = 15;
            lightningLengthRand = 5;
            lightningColor = Color.valueOf("EEC591");
        }};
        ((ItemTurret)LIblocks.DCFB).ammoTypes.put(Vars.content.getByName(ContentType.item, "饱和火力-裂位能"), DCFB1);

        //禁空
        Seq<ItemStack> JKreq = new Seq<>();
        for(ItemStack s : LIblocks.JK.requirements){
            JKreq.add(s);
        }
        JKreq.add(new ItemStack(Vars.content.getByName(ContentType.item, "饱和火力-三级协议"), 20));
        LIblocks.JK.requirements = ItemStack.with(Items.lead, 6000, Items.graphite, 3500, Items.surgeAlloy, 1800, Items.plastanium, 1200, LIitems.CDZ, 125, LIitems.SMWZ, 1, Vars.content.getByName(ContentType.item, "饱和火力-三级协议"), 15);

        //电裂
        ((PowerTurret)LIblocks.DL).shootType.status = Vars.content.getByName(ContentType.status, "饱和火力-干扰");

        //单位工厂
        Reconstructor T13 = (Reconstructor)LIblocks.BCJDWZGGC, T14 = (Reconstructor)LIblocks.DMJDWZGGC, T15 = (Reconstructor)LIblocks.WLJDWZGGC;
        T13.addUpgrade(Vars.content.getByName(ContentType.unit, "饱和火力-陆1"), Vars.content.getByName(ContentType.unit, "饱和火力-陆3"));
        T13.addUpgrade(Vars.content.getByName(ContentType.unit, "饱和火力-空1"), Vars.content.getByName(ContentType.unit, "饱和火力-空3"));
        T13.addUpgrade(Vars.content.getByName(ContentType.unit, "饱和火力-海1"), Vars.content.getByName(ContentType.unit, "饱和火力-海3"));

        T14.addUpgrade(Vars.content.getByName(ContentType.unit, "饱和火力-陆1"), Vars.content.getByName(ContentType.unit, "饱和火力-陆4"));
        T14.addUpgrade(Vars.content.getByName(ContentType.unit, "饱和火力-空1"), Vars.content.getByName(ContentType.unit, "饱和火力-空4"));
        T14.addUpgrade(Vars.content.getByName(ContentType.unit, "饱和火力-海1"), Vars.content.getByName(ContentType.unit, "饱和火力-海4"));

        T15.addUpgrade(Vars.content.getByName(ContentType.unit, "饱和火力-陆1"), Vars.content.getByName(ContentType.unit, "饱和火力-陆5"));
        T15.addUpgrade(Vars.content.getByName(ContentType.unit, "饱和火力-空1"), Vars.content.getByName(ContentType.unit, "饱和火力-空5"));
        T15.addUpgrade(Vars.content.getByName(ContentType.unit, "饱和火力-海1"), Vars.content.getByName(ContentType.unit, "饱和火力-海5"));

        //单位
        LIunits.JX.health = 21000;
        LIunits.YA.health = 180000;
        LIunits.YA.immunities.addAll(StatusEffects.unmoving, StatusEffects.disarmed, StatusEffects.electrified, Vars.content.getByName(ContentType.status, "饱和火力-干扰"), Vars.content.getByName(ContentType.status, "饱和火力-崩溃"), Vars.content.getByName(ContentType.status, "饱和火力-休克"));
        LIunits.YA.weapons.get(2).bullet.buildingDamageMultiplier = 0.3f;

        //region SF修改
        //铁流
        var TL = (LiquidTurret)Vars.content.getByName(ContentType.block, "饱和火力-铁流");
        RailBulletType TL1 = (RailBulletType)TL.ammoTypes.get(Liquids.slag).copy();
        TL1.shootEffect = LIfx.redShoot;
        TL1.pointEffect = LIfx.redRailTrail;
        TL1.damage *= 6;
        TL1.pierceDamageFactor = 0;
        TL1.status = StatusEffects.burning;
        TL.ammoTypes.put(LIliquids.SBRY, TL1);

        //涡流
        var WL = (LiquidTurret)Vars.content.getByName(ContentType.block, "饱和火力-涡流");
        LiquidBulletType WL1 = (LiquidBulletType)WL.ammoTypes.get(Liquids.slag).copy(), WL2 = (LiquidBulletType)WL.ammoTypes.get(Liquids.cryofluid).copy();
        WL1.liquid = LIliquids.SBRY;
        WL1.damage *= 6;
        WL1.status = StatusEffects.burning;
        WL2.liquid = LIliquids.CJLDY;
        WL2.status = LIstatus.BF;
        WL2.statusDuration = 300f;
        WL.ammoTypes.putAll(LIliquids.SBRY, WL1, LIliquids.CJLDY, WL2);

        //死诏
        var SZ = (ItemTurret)Vars.content.getByName(ContentType.block, "饱和火力-死诏");
        RailBulletType SZ1 = (RailBulletType)SZ.ammoTypes.get(Items.surgeAlloy).copy();
        BasicBulletType SZ1spawn = (BasicBulletType)SZ1.spawnBullets.get(0).copy();
        SZ1spawn.lifetime = 25.5f;
        SZ1spawn.rangeChange = 160f;
        SZ1spawn.pierce = SZ1spawn.pierceBuilding = true;
        SZ1spawn.pierceCap = -1;
        SZ1spawn.trailColor = SZ1spawn.backColor = LIcolor.sparkColor;
        SZ1spawn.trailEffect = new ParticleEffect(){{
            sizeFrom = 8f;
            sizeTo = 0f;
            baseLength = 8f;
            length = 10f;
            sizeInterp = Interp.circleOut;
            interp = Interp.circleOut;
            colorFrom = LIcolor.sparkColor.cpy();
            colorTo = LIcolor.sparkColor.cpy().a(160/255f);
        }};
        SZ1spawn.hitEffect = LIfx.sparkHit;
        SZ1spawn.despawnEffect = LIfx.sparkBomb;
        SZ1.spawnBullets = Seq.with(SZ1spawn);
        SZ1.rangeChange = 160f;
        SZ1.length = 1060f;
        SZ1.pierceCap = -1;
        SZ1.pierceDamageFactor = 0;
        SZ1.ammoMultiplier = 8;
        SZ1.status = StatusEffects.electrified;
        SZ1.statusDuration = 240f;
        SZ1.shootEffect = LIfx.sparkShoot;
        SZ1.pointEffect = new ParticleEffect(){{
            particles = 1;
            region = "饱和火力-箭头";
            sizeInterp = Interp.pow5In;
            sizeFrom = 20f;
            sizeTo = 0f;
            length = 30f;
            randLength = false;
            baseLength = 0f;
            lifetime = 30f;
            colorFrom = colorTo = LIcolor.sparkColor;
            cone = 0f;
        }};
        SZ1.smokeEffect = new ParticleEffect(){{
            particles = 8;
            interp = Interp.circleOut;
            sizeInterp = Interp.circleOut;
            sizeFrom = 12f;
            sizeTo = 0f;
            length = 70f;
            baseLength = 0f;
            lifetime = 80f;
            colorFrom = colorTo = LIcolor.sparkColor;
            cone = 60f;
        }};
        SZ1.pierceEffect = LIfx.sparkHit;
        SZ.ammoTypes.put(LIitems.CDZ, SZ1);

        //竹林
        var ZL = (ItemTurret)Vars.content.getByName(ContentType.block, "饱和火力-竹林");
        BasicBulletType ZL1 = (BasicBulletType)ZL.ammoTypes.get(Items.surgeAlloy).copy();
        ZL1.ammoMultiplier = 20;
        ZL1.damage = 205f;
        ZL1.fragBullet = new LightningBulletType(){{
            lightningColor = LIcolor.sparkColor;
            lightningLength = 16;
            lightningDamage = 25f;
            hitEffect = Fx.none;
        }};
        ZL1.fragBullets = 2;
        ZL1.fragOnDespawn = false;
        ZL1.shootEffect = new ParticleEffect(){{
            particles = 8;
            line = true;
            strokeFrom = 3f;
            strokeTo = 0f;
            lenFrom = 12f;
            lenTo = 0f;
            length = 35f;
            baseLength = 3f;
            lifetime = 15f;
            colorFrom = LIcolor.sparkColor.cpy();
            colorTo = Color.white.cpy();
            cone = 60f;
        }};
        ZL1.despawnEffect = LIfx.sparkBomb;
        ZL1.hitEffect = Fx.none;
        ZL1.pierceCap = -1;
        ZL1.lightningLength = 32;
        ZL1.lightningDamage = 50f;
        ZL1.frontColor = ZL1.backColor = ZL1.lightningColor = LIcolor.sparkColor;
        ZL1.backColor = ZL1.trailColor = LIcolor.sparkColorBack;
        ZL1.status = StatusEffects.electrified;
        ZL1.statusDuration = 240f;
        ZL.ammoTypes.put(LIitems.CDZ, ZL1);

        //扩散轨道炮
        var KSGDP = (ItemTurret)Vars.content.getByName(ContentType.block, "饱和火力-扩散轨道炮");
        BasicBulletType KSGDP1 = new BasicBulletType(16f, 500f){{
            reflectable = false;
            lifetime = 50f;
            ammoMultiplier = 4;
            reloadMultiplier = 0.8f;
            width = 32f;
            height = 64f;
            sprite = "饱和火力-菱形";
            shootSound = Sounds.shootSmite;
            shootEffect = KSGDP.ammoTypes.get(Vars.content.getByName(ContentType.item, "饱和火力-泰勒合金")).shootEffect;
            trailLength = 12;
            trailWidth = 5f;
            trailInterval = 1.5f;
            trailEffect = new WaveEffect(){{
                interp = Interp.circleOut;
                lifetime = 10f;
                sizeFrom = 4f;
                sizeTo = 48f;
                strokeFrom = 3f;
                strokeTo = 0f;
                colorFrom = LIcolor.sparkColor.cpy();
                colorTo = Color.white.cpy();
            }};
            hitColor = trailColor = frontColor = LIcolor.sparkColor.cpy();
            backColor = LIcolor.sparkColorBack.cpy();
            fragBullets = 1;
            fragOnAbsorb = fragOnHit = fragOnDespawn = true;
            fragBullet = new EmpBulletType(){{
                absorbable = reflectable = false;
                pierce = splashDamagePierce = pierceBuilding = instantDisappear = true;
                timeDuration = 300f;
                timeIncrease = 3f;
                powerSclDecrease = 0f;
                unitDamageScl = 1f;
                damage = 000f;
                splashDamage = 500f;
                radius = 200f;
                splashDamageRadius = 200f;
                status = Vars.content.getByName(ContentType.status, "饱和火力-干扰");
                statusDuration = 300f;
                hitShake = 12f;
                hitSound = despawnSound = Sounds.explosionQuad;
                hitColor = LIcolor.sparkColor.cpy();
                hitPowerEffect = LIfx.sparkEmp;
                hitEffect = despawnEffect = new Effect(40f, e -> {
                    color(e.color, Color.white.cpy(), e.fout());
                    stroke(e.fout() * 6f);
                    circle(e.x, e.y, 200f);

                    new Rand().setSeed(e.id);
                    for(int i = 0; i < 16; i++){
                        float angle = new Rand().random(360f);
                        float lenRand = new Rand().random(0.5f, 1f);
                        lineAngle(e.x, e.y, angle, 100f * 0.8f * new Rand().random(1f, 0.6f) + 2f, 100f * e.finpow() * 1.2f * lenRand + 6f);
                    }
                    color(e.color.cpy().a(0), e.color.cpy().a(160/255f), e.foutpow());
                    Fill.circle(e.x, e.y, 200f);
                });

                fragLifeMin = 0.3f;
                fragLifeMax = 1.2f;

                fragBullets = 60;
                fragBullet = new BasicBulletType(4f, 0){{
                    lifetime = 60f;
                    absorbable = reflectable = collidesTiles = false;
                    splashDamagePierce = true;
                    sprite = "饱和火力-星";
                    spin = 1f;
                    width = height = 32f;
                    frontColor = hitColor = backColor = lightColor = Color.white.cpy().lerp(LIcolor.sparkColor.cpy(), 0.5f);
                    splashDamage = 150f;
                    splashDamageRadius = 48f;
                    homingPower = 0.1f;
                    homingRange = 40f;
                    status = Vars.content.getByName(ContentType.status, "饱和火力-干扰");
                    statusDuration = 900f;
                    hitSound = despawnSound = Sounds.explosionQuad;
                    hitEffect = despawnEffect = new MultiEffect(
                            new ParticleEffect(){{
                                lifetime = 20f;
                                particles = 1;
                                baseLength = 0f;
                                length = 0f;
                                region = "circle";
                                sizeFrom = sizeTo = 48f;
                                colorFrom = Color.white.cpy().lerp(LIcolor.sparkColor.cpy(), 0.5f);
                                colorTo = colorFrom.cpy().a(0);
                            }},
                            new WaveEffect(){{
                                lifetime = 20f;
                                sizeFrom = sizeTo = 48f;
                                strokeFrom = 3f;
                                strokeTo = 0f;
                                colorFrom = colorTo = Color.white.cpy().lerp(LIcolor.sparkColor.cpy(), 0.5f);
                            }}
                    );
                }};
            }};
        }};
        KSGDP.ammoTypes.put(LIitems.CDZ, KSGDP1);

        //实验工厂
        ((UnitFactory)Vars.content.getByName(ContentType.block, "饱和火力-实验工厂")).plans.add(new UnitFactory.UnitPlan(LIunits.YA, 23400, ItemStack.with(
                Items.silicon, 3000, Vars.content.getByName(ContentType.item, "饱和火力-镄"), 1200, Vars.content.getByName(ContentType.item, "饱和火力-硅钢"), 1500,
                Items.plastanium, 2200, Items.surgeAlloy, 1800, Vars.content.getByName(ContentType.item, "饱和火力-裂位能"), 1200, Vars.content.getByName(ContentType.item, "饱和火力-泰勒合金"), 1000,
                LIitems.QSZ, 20, Vars.content.getByName(ContentType.item, "饱和火力-二级协议"), 30, Vars.content.getByName(ContentType.item, "饱和火力-三级协议"), 10
        )));

        //特种工厂
        ((UnitFactory)Vars.content.getByName(ContentType.block, "饱和火力-特种工厂")).plans.add(new UnitFactory.UnitPlan(LIunits.HL, 2520, ItemStack.with(
                Items.silicon, 180, Items.graphite, 40, Items.titanium, 80, LIitems.QSZ, 15
        )));
    }

    private static void SF2change(){
        Log.info("发现饱和火力2, 加载对应适配");
        //TODO
    }
}
