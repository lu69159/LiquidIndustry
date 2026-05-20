package LI.content;

import arc.struct.*;
import mindustry.content.*;
import mindustry.entities.Puddles;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.ParticleEffect;
import mindustry.gen.Bullet;
import mindustry.graphics.Pal;
import mindustry.type.*;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.liquid.*;
import mindustry.world.blocks.units.*;
import mindustry.ctype.UnlockableContent;

import static mindustry.Vars.world;
import static mindustry.content.Blocks.*;
import static mindustry.content.StatusEffects.*;

public class LIoverride {
    public static void loadOverride(){
        //region 地形
        ((Floor)hotrock).attributes.set(LIattr.cryofluid, -0.75f);
        ((Floor)magmarock).attributes.set(LIattr.cryofluid, -1f);
        ((Floor)snow).attributes.set(LIattr.cryofluid, 0.15f);
        ((Floor)iceSnow).attributes.set(LIattr.cryofluid, 0.3f);
        ((Floor)ice).attributes.set(LIattr.cryofluid, 0.5f);

        //region 液体相关
        liquidContainer.liquidCapacity = 1000f;
        liquidTank.liquidCapacity = 3000f;
        ((LiquidBridge)bridgeConduit).range = 6;
        ((LiquidBridge)phaseConduit).range = 24;

        //region 物流相关
        ((ItemBridge)phaseConveyor).range = 24;
        ((ArmoredConveyor)armoredConveyor).speed = ((ArmoredConveyor)armoredConveyor).displayedSpeed = 30;
        plastaniumConveyor.itemCapacity = 20;

        illuminator.canOverdrive = false; //照明

        //region 单位制造
        ((UnitFactory)navalFactory).plans.add(new UnitFactory.UnitPlan(LIunits.SM, 60 * 30f, ItemStack.with(Items.silicon, 10, LIitems.QSZ, 12)));
        ((Reconstructor)additiveReconstructor).upgrades.add(new UnitType[]{LIunits.SM, LIunits.FZ});
        ((Reconstructor)multiplicativeReconstructor).upgrades.add(new UnitType[]{LIunits.FZ, LIunits.HL});
        ((Reconstructor)exponentialReconstructor).upgrades.add(new UnitType[]{LIunits.HL, LIunits.DY});
        ((Reconstructor)tetrativeReconstructor).upgrades.add(new UnitType[]{LIunits.DY, LIunits.JX});

        //状态相关
        shielded.effect = new ParticleEffect(){{
            particles = 1;
            baseLength = 10;
            length = 30;
            lifetime = 30;
            spin = 8;
            region = "液体工艺-六角";
            sizeFrom = 3;
            sizeTo = 0;
            colorFrom = colorTo = Pal.accent;
        }};

        loadTurretOverride();
    }

    private static void loadTurretOverride(){
        addAmmoType(foreshadow, LIitems.CDZ, new RailBulletType(){{
            rangeChange = 80f;
            shootEffect = LIfx.sparkShoot;
            hitEffect = LIfx.sparkHit;
            pierceEffect = LIfx.sparkHit;
            smokeEffect = Fx.smokeCloud;
            pointEffect = LIfx.sparkTrail;
            despawnEffect = LIfx.sparkBomb;
            pointEffectSpace = 20;
            damage = 1350;
            buildingDamageMultiplier = 0.2f;
            pierceDamageFactor = 0;
            length = 500 + rangeChange;
            hitShake = 6;
            ammoMultiplier = 5;
            status = StatusEffects.electrified;
            statusDuration = 90;
        }});
        addAmmoType(fuse,
            Items.surgeAlloy, new ShrapnelBulletType(){{
                    rangeChange = 90;
                    length = 190;
                    damage = 185;
                    ammoMultiplier = 5;
                    toColor = Pal.surge;
                    shootEffect = LIfx.surgeAlloyShoot;
                    smokeEffect = LIfx.surgeAlloyShoot;
                    width = 22;
                    reloadMultiplier = 0.9f;
                    status = StatusEffects.shocked;

                    intervalBullet = new LightningBulletType(){{
                        damage = 15f;
                        pierceArmor = true;
                        lightningColor = Pal.surge;
                        lightningLength = 20;
                        lightningLengthRand = 10;
                    }};
                    intervalBullets = 1;
                    intervalRandomSpread = 10;
                    intervalSpread = 15;
                    intervalAngle = 0;
            }},
            LIitems.CDZ,new ShrapnelBulletType(){{
                    rangeChange = 90;
                    length = 190;
                    damage = 425;
                    ammoMultiplier = 6;
                    toColor = LIcolor.sparkColor;
                    shootEffect = LIfx.CDZshoot;
                    smokeEffect = LIfx.CDZshoot;
                    width = 22;
                    reloadMultiplier = 0.8f;
                    status = StatusEffects.electrified;
                    statusDuration = 60f;

                    intervalBullet = new LightningBulletType(){{
                        damage = 30f;
                        pierceArmor = true;
                        lightningColor = LIcolor.sparkColor;
                        lightningLength = 20;
                        lightningLengthRand = 10;
                    }};
                    intervalBullets = 1;
                    intervalRandomSpread = 10;
                    intervalSpread = 15;
                    intervalAngle = 0;
            }}
        );
        addAmmoType(tsunami,
                LIliquids.CJLDY, new LiquidBulletType(LIliquids.CJLDY){{
                    lifetime = 49f;
                    speed = 4f;
                    knockback = 1.3f;
                    puddleSize = 8f;
                    orbSize = 4f;
                    drag = 0.001f;
                    ammoMultiplier = 0.4f;
                    status = LIstatus.BF;
                    statusDuration = 60f * 4f;
                    damage = 0.2f;
                }},
                LIliquids.SBRY, new LiquidBulletType(LIliquids.SBRY){{
                    lifetime = 49f;
                    speed = 4f;
                    knockback = 1.3f;
                    puddleSize = 8f;
                    orbSize = 4f;
                    damage = 23.75f;
                    drag = 0.001f;
                    ammoMultiplier = 0.4f;
                    status = StatusEffects.burning;
                    statusDuration = 60f * 4f;

                    incendAmount = 2;
                }
                    @Override
                    public void despawned(Bullet b) {
                        super.despawned(b);
                        Puddles.deposit(world.tileWorld(b.x, b.y), liquid, puddleSize);
                    }
                }
        );
        addAmmoType(wave,
                LIliquids.CJLDY, new LiquidBulletType(LIliquids.CJLDY){{
                    drag = 0.01f;
                    status = LIstatus.BF;
                }},
                LIliquids.SBRY, new LiquidBulletType(LIliquids.SBRY){{
                    damage = 20f;
                    drag = 0.01f;
                    status = StatusEffects.burning;

                    incendAmount = 1;
                }
                    @Override
                    public void despawned(Bullet b) {
                        super.despawned(b);
                        Puddles.deposit(world.tileWorld(b.x, b.y), liquid, puddleSize);
                    }
                }
        );
    }

    private static void addAmmoType(Block turret, Object... objects){
        ObjectMap<UnlockableContent, BulletType> map = OrderedMap.of(objects);
        map.each((u, bullet) -> {
            addAmmoType(turret, u, bullet);
        });
    }

    private static void addAmmoType(Block turret, UnlockableContent u, BulletType bullet){
        if(turret instanceof ItemTurret t){
            try{
                t.ammoTypes.put((Item)u, bullet);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        else if(turret instanceof LiquidTurret t){
            try{
                t.ammoTypes.put((Liquid)u, bullet);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        else if(turret instanceof ContinuousLiquidTurret t){
            try{
                t.ammoTypes.put((Liquid)u, bullet);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
