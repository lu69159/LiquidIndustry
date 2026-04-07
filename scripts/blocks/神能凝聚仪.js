var 光环Region, starRegion;

const 神能凝聚仪 = extend(GenericCrafter, "神能凝聚仪", {
    load(){
        this.super$load();
        光环Region = Core.atlas.find("液体工艺-神佑光环");
        starRegion = Core.atlas.find("液体工艺-sTar");
    },
    setStats() {
        this.super$setStats();
        if(this.destroyBullet != null) this.stats.add(new Stat("damageondestroy", StatCat.function), StatValues.ammo(ObjectMap.of(this, this.destroyBullet), true, false));
    }
});

神能凝聚仪.buildType = prov(() => {
    return extend(GenericCrafter.GenericCrafterBuild, 神能凝聚仪, {
        draw(){
            this.super$draw();
            Draw.z(Layer.effect);
            Draw.color(Color.valueOf("FFFFFFCF"));
            if(this.power.status>0){
                Draw.rect(
                    光环Region,
                    this.x,
                    this.y,
                    this.block.size * 8,
                    this.block.size * 8,
                    Time.time * 1
                );
                Draw.rect(
                        光环Region,
                        this.x,
                        this.y,
                        this.block.size * 16,
                        this.block.size * 16,
                        Time.time * 1
                );
            }
        
            //updatedraw
            if(this.warmup > 0.01){
                let sin = (2.5 * Math.sin(Time.time * 0.05) + 20) * this.warmup;
                Draw.alpha(this.warmup * 0.55);
                Draw.rect(
                    starRegion,
                    this.x,
                    this.y,
                    this.block.size * sin,
                    this.block.size * sin,
                    Time.time * (-0.8)
                );
            }

        }
    });
});
exports.神能凝聚仪 = 神能凝聚仪;