const LIfx = require("base/effects");
const ATD = require("base/ATD");
function hasMod(MOD){
    return MOD !=null && MOD.state == Mods.ModState.enabled;
};
function copyPE(effect, attr){
    if(!(effect instanceof Effect)) return effect.copy();
    let PE;
    if(effect instanceof ParticleEffect){
        PE = new ParticleEffect();
        Object.assign(PE,{
            baseLength: effect.baseLength, cap: effect.cap, casingFlip: effect.casingFlip,
            colorFrom: effect.colorFrom, colorTo: effect.colorTo, cone: effect.cone,
            interp: effect.interp, lenFrom: effect.lenFrom, lenTo: effect.lenTo,
            length: effect.length, lightColor: effect.lightColor, lightOpacity: effect.lightOpacity,
            lightScl: effect.lightScl, line: effect.line, offset: effect.offset,
            offsetX: effect.offsetX, offsetY: effect.offsetY, particles: effect.particles,
            randLength: effect.randLength, region: effect.region, sizeChangeStart: effect.sizeChangeStart,
            sizeFrom: effect.sizeFrom, sizeTo: effect.sizeTo, sizeInterp: effect.sizeInterp,
            spin: effect.spin, strokeFrom: effect.strokeFrom, strokeTo: effect.strokeTo, useRotation: effect.useRotation,

            baseRotation: effect.baseRotation, clip: effect.clip, followParent: effect.followParent, 
            layer: effect.layer, layerDuration: effect.layerDuration, lifetime: effect.lifetime,
            rotWithParent: effect.rotWithParent, startDelay: effect.startDelay
        });
    }
    else if(effect instanceof WaveEffect){
        PE = new WaveEffect();
        Object.assign(PE,{
            colorFrom: effect.colorFrom, colorTo: effect.colorTo, interp: effect.interp,
            lightColor: effect.lightColor, lightInterp: effect.lightInterp, lightOpacity: effect.lightOpacity,
            lightScl: effect.lightScl, offsetX: effect.offsetX, offsetY: effect.offsetY,
            rotation: effect.rotation, sides: effect.sides, sizeFrom: effect.sizeFrom, 
            sizeTo: effect.sizeTo, strokeFrom: effect.strokeFrom, strokeTo: effect.strokeTo,

            baseRotation: effect.baseRotation, clip: effect.clip, followParent: effect.followParent, 
            layer: effect.layer, layerDuration: effect.layerDuration, lifetime: effect.lifetime,
            rotWithParent: effect.rotWithParent, startDelay: effect.startDelay
        });
    }

    if(attr !=null && typeof attr === "object"){
        Object.assign(PE, attr);
    }
    return PE;
}
function getBlock(fullName){
    return Vars.content.getByName(ContentType.block, fullName);
}
function copyAmmoType(turretName, item){
    let turret = getBlock(turretName), map = turret.ammoTypes;
    let orderedKeys = map.keys().toSeq();
    orderedKeys.sort();
    
    try{
        let type = map.get(item);
        if(type instanceof BulletType){
            return type.copy();
        }

    }catch(e){
        Log.err("get ammo type fail：" + String(e));
    }
}
function addAmmoType(turretName, item, bullet){
    let turret = getBlock(turretName);
    if(!(turret instanceof Turret && item instanceof UnlockableContent && bullet instanceof BulletType)){
        Log.err("add ammo fail");
        return;
    }
    turret.ammoTypes.put(item, bullet); 
}
/////////////////////////////////////////////////////////////////////////////////////////////////////
function SFchange(){
    let DCFB = getBlock("液体工艺-电磁风暴"), DCFBreq = [];
    DCFB.requirements.forEach(stack => {
        DCFBreq.push(stack);
    });
    DCFBreq.push(new ItemStack(Vars.content.getByName(ContentType.item, "饱和火力-二级协议"), 2));
    DCFB.requirements = DCFBreq;

    //铁流
    let bulletTL = copyAmmoType("饱和火力-铁流", Liquids.slag);
    Object.assign(bulletTL, {
        shootEffect: LIfx.redShoot,
        pointEffect: LIfx.redRailTrail,
        damage: 135 * 5,
        pierce: true,
        pierceDamageFactor: 0,
        status: StatusEffects.burning
    });
    addAmmoType("饱和火力-铁流", require("LI/LIliquids")["衰变熔岩"], bulletTL);

    //涡流
    let bulletWL1 = copyAmmoType("饱和火力-涡流", Liquids.slag), 
    bulletWL2 = copyAmmoType("饱和火力-涡流", Liquids.cryofluid);
    Object.assign(bulletWL1, {
        liquid: require("LI/LIliquids")["衰变熔岩"],
        damage: 9.5 * 5,
        status: StatusEffects.burning
    });
    Object.assign(bulletWL2, {
        liquid: require("LI/LIliquids")["超级冷冻液"],
        status: require("LI/LIstatus").冰封,
        statusDuration: 300
    });
    addAmmoType("饱和火力-涡流", require("LI/LIliquids")["衰变熔岩"], bulletWL1);
    addAmmoType("饱和火力-涡流", require("LI/LIliquids")["超级冷冻液"], bulletWL2);

    //流冲
    let bulletLC1 = copyAmmoType("饱和火力-流冲", Vars.content.getByName(ContentType.item, "饱和火力-水桶")), fragBulletLC1 = bulletLC1.fragBullet.copy(),
    bulletLC2 = copyAmmoType("饱和火力-流冲", Vars.content.getByName(ContentType.item, "饱和火力-冷冻液桶")), fragBulletLC2 = bulletLC2.fragBullet.copy(),
    bulletLC3 = copyAmmoType("饱和火力-流冲", Vars.content.getByName(ContentType.item, "饱和火力-石油桶")), fragBulletLC3 = bulletLC3.fragBullet.copy(),
    bulletLC4 = copyAmmoType("饱和火力-流冲", Vars.content.getByName(ContentType.item, "饱和火力-冷冻液桶")), fragBulletLC4 = bulletLC4.fragBullet.copy();
    Object.assign(fragBulletLC1, {
        pierce: true,
        pierceBuilding: true,
        statusDuration: 300
    });
    Object.assign(bulletLC1, {
        rangeChange: 120,
        lifetime: 24 + 10,
        damage: 33 * 450 / 60,
        splashDamage: 33 * 450 / 60,
        splashDamageRadius: 64,
        splashDamagePierce: true,
        fragBullets: 180,
        fragBullet: fragBulletLC1,
    });
    Object.assign(fragBulletLC2, {
        pierce: true,
        pierceBuilding: true,
        statusDuration: 300
    });
    Object.assign(bulletLC2, {
        rangeChange: 120,
        lifetime: 24 + 10,
        damage: 33 * 450 / 60,
        splashDamage: 33 * 450 / 60,
        splashDamageRadius: 64,
        splashDamagePierce: true,
        fragBullets: 180,
        fragBullet: fragBulletLC2,
    });
    Object.assign(fragBulletLC3, {
        pierce: true,
        pierceBuilding: true,
        statusDuration: 300
    });
    Object.assign(bulletLC3, {
        rangeChange: 120,
        lifetime: 24 + 10,
        damage: 33 * 450 / 60,
        splashDamage: 33 * 450 / 60,
        splashDamageRadius: 64,
        splashDamagePierce: true,
        fragBullets: 180,
        fragBullet: fragBulletLC3,
    });
    Object.assign(fragBulletLC4, {
        pierce: true,
        pierceBuilding: true,
        liquid: require("LI/LIliquids")["超级冷冻液"],
        status: require("LI/LIstatus").冰封,
        statusDuration: 300
    });
    Object.assign(bulletLC4, {
        rangeChange: 120,
        lifetime: 24 + 10,
        backColor: Color.valueOf("D8F3FF"),
        trailColor: Color.valueOf("D8F3FF"),
        damage: 33 * 450 / 60,
        splashDamage: 33 * 450 / 60,
        splashDamageRadius: 64,
        splashDamagePierce: true,
        fragBullets: 180,
        fragBullet: fragBulletLC4,
    });
    addAmmoType("饱和火力-流冲", require("LI/LIitems")["固态水"], bulletLC1);
    addAmmoType("饱和火力-流冲", require("LI/LIitems")["固态冷冻液"], bulletLC2);
    addAmmoType("饱和火力-流冲", require("LI/LIitems")["固态石油"], bulletLC3);
    addAmmoType("饱和火力-流冲", require("LI/LIitems")["固态超级冷冻液"], bulletLC4);

    //死诏
    let bulletSZ = copyAmmoType("饱和火力-死诏", Items.surgeAlloy);
    let SZspawnBullet = bulletSZ.spawnBullets.get(0).copy();
    Object.assign(SZspawnBullet, { 
        lifetime: 25.5,
        pierce: true,
        pierceBuilding: true,
        pierceCap: -1,
        trailEffect: copyPE(bulletSZ.spawnBullets.get(0).trailEffect, {
            colorFrom: Color.valueOf("00FFFF"),
            colorTo: Color.valueOf("8EFFEA"),
        }),
        trailColor: Color.valueOf("00FFFF"),
        backColor: Color.valueOf("00FFFF"),
        hitEffect: LIfx.sparkHit,
        despawnEffect: LIfx.sparkBomb,
    });
    Object.assign(bulletSZ, {
        rangeChange: 160,
        length: 1060,
        pierceCap: -1,
        shootEffect: LIfx.sparkShoot,
        smokeEffect: copyPE(bulletSZ.smokeEffect, {
            colorFrom: Color.valueOf("00FFFF"),
            colorTo: Color.valueOf("8EFFEA"),
        }),
        pierceEffect: new MultiEffect(
            copyPE(bulletSZ.pierceEffect.effects[0], {
                colorFrom: Color.valueOf("00FFFF"),
                colorTo: Color.valueOf("8EFFEA"),
            }),
            copyPE(bulletSZ.pierceEffect.effects[1], {
                colorFrom: Color.valueOf("00FFFF"),
                colorTo: Color.valueOf("8EFFEA"),
            }),
        ),
        pointEffect: copyPE(bulletSZ.pointEffect.effects[0], {
            colorFrom: Color.valueOf("00FFFF"),
            colorTo: Color.valueOf("8EFFEA"),
        }),
        spawnBullets: Seq.with(SZspawnBullet),
        buildingDamageMultiplier: 0.12,
        pierceDamageFactor: 0,
        status: StatusEffects.electrified,
        statusDuration: 4,
        ammoMultiplier: 8
    });
    addAmmoType("饱和火力-死诏", require("LI/LIitems")["超导质"], bulletSZ);

    //竹林
    let bulletZL = copyAmmoType("饱和火力-竹林", Items.surgeAlloy);
    Object.assign(bulletZL, {
        despawnEffect: LIfx.sparkBomb,
        shootEffect: copyPE(bulletZL.shootEffect, {
            colorFrom: Color.valueOf("00FFFF")
        }),
        hitEffect: new MultiEffect(
            copyPE(bulletZL.hitEffect.effects[0], {
                colorFrom: Color.valueOf("00FFFF"),
                colorTo: Color.valueOf("8EFFEA")
            }),
            copyPE(bulletZL.hitEffect.effects[1], {
                colorFrom: Color.valueOf("00FFFF"),
                colorTo: Color.valueOf("00FFFF")
            }),
            copyPE(bulletZL.hitEffect.effects[2], {
                colorFrom: Color.valueOf("8EFFEA")
            }),
        ),
        ammoMultiplier: 20,
        pierceCap: -1,
        lightningLength: 32,
        frontColor: Color.valueOf("00FFFF"),
        lightningColor: Color.valueOf("00FFFF"),
		backColor: Color.valueOf("8EFFEA"),
        trailColor: Color.valueOf("8EFFEA"),
        status: StatusEffects.electrified,
        statusDuration: 240
    });
    addAmmoType("饱和火力-竹林", require("LI/LIitems")["超导质"], bulletZL); 
}

function SF2change(){
    //铁流
    let bulletTL = copyAmmoType("sfire-mod-tieliu", Liquids.slag),
    bulletHX = new LiquidBulletType(require("LI/LIliquids")["衰变熔岩"]);
    Object.assign(bulletHX, {
        lifetime: 49,
        speed: 4,
        knockback: 1.3,
        puddleSize: 8,
        orbSize: 4,
        drag: 0.001,
        ammoMultiplier: 0.4,
        status: StatusEffects.burning,
        statusDuration: 60 * 4,
        damage: 23.75,
        makeFire: true
    });
    Object.assign(bulletTL, {
        shootEffect: Fx.none,
        rangeChange: 120,
        length: 230 + 120,
        pointEffect: new Effect(16.0, (e) => {
            Draw.color(Color.valueOf("FF2020"));
            Angles.randLenVectors(e.id, 5, 1.0 + e.fin() * 15.0, e.rotation, 60.0, (x, y) => Fill.circle(e.x + x, e.y + y, e.fout() * 2.0));
        }),
        damage: 45 * 5,
        pierce: true,
        pierceBuilding: true,
        pierceDamageFactor: 0,
        status: StatusEffects.burning,
        fragBullet: bulletHX
    });
    addAmmoType("sfire-mod-tieliu", require("LI/LIliquids")["衰变熔岩"], bulletTL);

    //涡流
    let bulletWL1 = copyAmmoType("sfire-mod-woliu", Liquids.slag), fragbulletWL1 = bulletWL1.fragBullet.copy(),
    bulletWL2 = copyAmmoType("sfire-mod-woliu", Liquids.cryofluid), fragbulletWL2 = bulletWL2.fragBullet.copy();
    Object.assign(fragbulletWL1, {
        liquid: require("LI/LIliquids")["衰变熔岩"],
        status: StatusEffects.burning
    });
    Object.assign(bulletWL1, {
        liquid: require("LI/LIliquids")["衰变熔岩"],
        damage: 6.25 * 5,
        status: StatusEffects.burning,
        fragBullet: fragbulletWL1
    });
    Object.assign(fragbulletWL2, {
        liquid: require("LI/LIliquids")["超级冷冻液"],
        status: require("LI/LIstatus").冰封
    });
    Object.assign(bulletWL2, {
        liquid: require("LI/LIliquids")["超级冷冻液"],
        status: require("LI/LIstatus").冰封,
        fragBullet: fragbulletWL2
    });
    addAmmoType("sfire-mod-woliu", require("LI/LIliquids")["衰变熔岩"], bulletWL1);
    addAmmoType("sfire-mod-woliu", require("LI/LIliquids")["超级冷冻液"], bulletWL2);

    //死诏
    let bulletSZ = copyAmmoType("sfire-mod-sizhao", Vars.content.getByName(ContentType.item, "sfire-mod-discordance-fabric"));
    let SZspawnBullet = bulletSZ.spawnBullets.get(0).copy();
    Object.assign(SZspawnBullet, { 
        pierce: true,
        pierceBuilding: true,
        pierceCap: -1,
        pierceDamageFactor: 0,
        lightningLength: 16,
        trailEffect: copyPE(bulletSZ.spawnBullets.get(0).trailEffect, {
            colorFrom: Color.valueOf("00FFFF"),
            colorTo: Color.valueOf("8EFFEA"),
        }),
        lightningColor: Color.valueOf("00FFFF"),
        trailColor: Color.valueOf("00FFFF"),
        backColor: Color.valueOf("00FFFF"),
        hitEffect: LIfx.sparkHit,
        despawnEffect: LIfx.sparkBomb,
    });
    Object.assign(bulletSZ, {
        pierce: true,
        pierceBuilding: true,
        pierceCap: -1,
        pierceDamageFactor: 0,
        maxDamageFraction: -1,
        shootEffect: LIfx.sparkShoot,
        pierceEffect: LIfx.sparkHit,
        hitEffect: LIfx.sparkHit,
        smokeEffect: copyPE(bulletSZ.smokeEffect, {
            colorFrom: Color.valueOf("00FFFF"),
            colorTo: Color.valueOf("8EFFEA"),
        }),
        pointEffect: copyPE(bulletSZ.pointEffect, {
            colorFrom: Color.valueOf("00FFFF"),
            colorTo: Color.valueOf("8EFFEA"),
        }),
        spawnBullets: Seq.with(SZspawnBullet),
        status: StatusEffects.electrified,
        ammoMultiplier: 6
    });
    addAmmoType("sfire-mod-sizhao", require("LI/LIitems")["超导质"], bulletSZ);

    //竹林
    let bulletZL = copyAmmoType("sfire-mod-zhulin", Items.surgeAlloy);
    Object.assign(bulletZL, {
        despawnEffect: LIfx.sparkBomb,
        hitEffect: copyPE(bulletZL.hitEffect.effects[1], {
            colorFrom: Color.valueOf("00FFFF"),
            colorTo: Color.valueOf("00FFFF")
        }),
        ammoMultiplier: 20,
        pierce: true,
        pierceBuilding: true,
        pierceCap: -1,
        lightning: 3,
        lightningLength: 32,
        frontColor: Color.valueOf("00FFFF"),
        lightningColor: Color.valueOf("00FFFF"),
        hitColor: Color.valueOf("00FFFF"),
		backColor: Color.valueOf("8EFFEA"),
        trailColor: Color.valueOf("8EFFEA"),
        status: StatusEffects.electrified,
        statusDuration: 240
    });
    addAmmoType("sfire-mod-zhulin", require("LI/LIitems")["超导质"], bulletZL);
}
/////////////////////////////////////////////////////////////////////////////////////////////////////
Events.on(ContentInitEvent, cons(e => {
    let SF = Vars.mods.locateMod("饱和火力"), SF2 = Vars.mods.locateMod("sfire-mod");
    if(hasMod(SF)){
        Log.info("发现饱和火力,加载对应适配(3.3.0+)");
        try{
            SFchange();
            ATD.AddAllToDatabaseWithFuncWithoutEvent("液体工艺", Vars.content.planet("饱和火力-泰伯利亚"), (thing) => {
                return !(thing instanceof SectorPreset || thing instanceof Planet || thing instanceof Weather);
            });
        }catch(e){
            Log.err(String(e));
        }
    }
    if(hasMod(SF2)){
        Log.info("发现饱和火力2,加载对应适配");
        try{
            SF2change();
            ATD.AddAllToDatabaseWithFuncWithoutEvent("液体工艺", Vars.content.planet("sfire-mod-tiberia"), (thing) => {
                return !(thing instanceof SectorPreset || thing instanceof Planet || thing instanceof Weather);
            });
        }catch(e){
            Log.err(String(e));
        }
    }
}));