const LIfx = require("base/effects");
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
        }catch(e){
            Log.err(String(e));
        }
    }
    if(hasMod(SF2)){
        Log.info("发现饱和火力2,加载对应适配");
        try{
            SF2change();
        }catch(e){
            Log.err(String(e));
        }
    }
}));