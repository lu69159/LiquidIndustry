exports.PowerCore = (name, damage, range, reload, powerout) => {
    var starRegion;
    const core = extend(CoreBlock, name, {
        load(){
            this.super$load();
            starRegion = Core.atlas.find("液体工艺-sTar");
        },
        setStats() {
            this.super$setStats();
            this.stats.add(Stat.basePowerGeneration, powerout, StatUnit.powerSecond);
            this.stats.add(Stat.shootRange, range / Vars.tilesize, StatUnit.blocks);
        },
        setBars() {
            this.super$setBars();
            this.addBar("power", func((entity) => new Bar(
                prov(() => Core.bundle.format("bar.poweroutput", Strings.fixed(entity.getPowerProduction() * 60, 1))),
                prov(() => Pal.powerBar),
                floatp(() => 1)
            )));
        },
        drawPlace(x, y, rotation, valid) {
            this.super$drawPlace(x, y, rotation, valid);
            Drawf.dashCircle(x * 8 + this.offset, y * 8 + this.offset, range, Pal.accent);
        }
    });
    core.buildType = prov(() => {
        return new JavaAdapter(CoreBlock.CoreBuild, {
            UCtimer: 0,
            created(){
                this.super$created();
                this.UCtimer = 0;
            },
            UC(){
                let PC = this;
                var unitConsumer = cons(unit => {
                    let dst = unit.hitSize/2 * range - unit.dst(PC);
                    if(dst > 0){
                        PC.UCtimer = reload;    
                        for(let i = 0; i < 2; i++) Fx.chainLightning.at(PC.x, PC.y, 0 ,Color.valueOf("8EFFEAC0"), unit);         
                        Fx.circleColorSpark.at(unit.x, unit.y, Color.valueOf("8EFFEAC0"));
                        unit.apply(StatusEffects.electrified, 60);
                        Damage.damage(PC.team, unit.x, unit.y, Vars.tilesize, damage);   
                        Sounds.shootArc.at(unit.x, unit.y);            
                    }
                });
                return unitConsumer;
            },
            getPowerProduction() {
                return powerout / 60;
            },
            updateTile() {
                if(this.UCtimer > 0) this.UCtimer -= Time.delta;
                this.UCtimer = Math.max(0, this.UCtimer);
                if(this.UCtimer == 0){
                    Units.nearbyEnemies(this.team, this.x, this.y, range, this.UC());
                }   
                this.super$updateTile();
            },
            draw(){
                this.super$draw();
                Draw.z(Layer.effect);
                Draw.color(Color.valueOf("8EFFEAC0"));
                var sin = (2.5 * Math.sin(Time.time * 0.05) + 20);
                Draw.rect(
                    starRegion,
                    this.x,
                    this.y,
                    this.block.size * 0.3 * sin,
                    this.block.size * 0.3 * sin,
                    45
                );
            },
            drawSelect() {
                this.super$drawSelect();
                Drawf.dashCircle(this.x, this.y, range, Pal.accent); //点击时显示的虚线圆
            }
        }, core);
    });

    return core;
};

exports.WallLiquidRouter = (name) => {
    var bottomRegion,Region;
    const wall = extend(Wall, name, {
        load(){
            this.super$load();
            bottomRegion = Core.atlas.find(this.name + "-bottom");
            Region = Core.atlas.find(this.name);
        },
        drawPlanRegion(plan, list){
            Draw.rect(bottomRegion, plan.drawx(), plan.drawy());
            Draw.rect(Region, plan.drawx(), plan.drawy());
        },
        icons(){
            return [bottomRegion, Region];
        }
    });
    wall.update = true;
    wall.buildCostMultiplier = 2.5
    wall.buildType = (() => {
        return extend(Wall.WallBuild, wall, {
            acceptLiquid(source, liquid){
                if(this.liquids.current() == null) return true;
                return (this.liquids.current() == liquid || this.liquids.currentAmount() < 0.2);
            },
            draw(){
                Draw.rect(bottomRegion, this.x, this.y);
                if(this.liquids.current() != null && this.liquids.currentAmount() > 0.001){
                    this.drawLiquid();
                }
                Draw.rect(Region, this.x, this.y);
            },
            drawLiquid(){
                let frame = this.liquids.current().getAnimationFrame();
                let gas = this.liquids.current().gas ? 1 : 0;
                let lq = Vars.renderer.fluidFrames[gas][frame];
                let liquidRegion = Tmp.tr1;
                liquidRegion.set(lq);
                Drawf.liquid(liquidRegion, this.x, this.y, this.liquids.currentAmount() / this.block.liquidCapacity * 1.0, this.liquids.current().color.write(Tmp.c1));
            },
            updateTile(){
                if(this.liquids.current() != null && this.liquids.currentAmount() > 0.01){
                    this.dumpLiquid(this.liquids.current());
                }
                this.super$updateTile();
            }
        });
    });

    return wall;
};

exports.LiquidMassDriver = (name, bulletSize) => {
    var hitEffect = Fx.hitLiquid;
    //液体质驱弹
    const LiquidMassDriverBolt = extend(BulletType, {
        damage: bulletSize * 12.5,
        lifetime: bulletSize * 75,
        collidesTiles: false,
        hitEffect: Fx.hitLiquid,
        despawnEffect: Fx.hitLiquid,
        update(b){
            this.super$update(b);
            var hitDst = 7;
            var data = b.data;

            if(data.to.dead){
                return;
            }
            var baseDst = data.from.dst(data.to),
                dst1 = b.dst(data.from),
                dst2 = b.dst(data.to);
            var intersect = false;

            if(dst1 > baseDst){
                var angleTo = b.angleTo(data.to),
                    baseAngle = data.to.angleTo(data.from);

                if(Angles.near(angleTo, baseAngle, 2)){
                    intersect = true;
                    b.set(data.to.x + Angles.trnsx(baseAngle, hitDst), data.to.y + Angles.trnsy(baseAngle, hitDst));
                }
            }

            if(Math.abs(dst1 + dst2 - baseDst) < 4 && dst2 <= hitDst){
                intersect = true;
            }
            if(intersect){
                data.to.handleLiquidPayload(b, data);
            }
        },
        draw(b){
            this.super$draw(b);
            const orbSize = bulletSize, boilTime = 5;
            var liquid = b.data.liquidType;
            if(liquid.willBoil()){
                Draw.color(liquid.color, Tmp.c3.set(liquid.gasColor), b.time / Mathf.randomSeed(b.id, boilTime));
                Fill.circle(b.x, b.y, orbSize * (b.fin() * 1.1 + 1));
            }
            else{
                Draw.color(liquid.color, Color.white, b.fout() / 100);
                Fill.circle(b.x, b.y, orbSize);
            }
            Draw.reset();
        },
        despawned(b){
            this.super$despawned(b);
            if(!b.data.liquidType.willBoil()){
                hitEffect.at(b.x, b.y, b.rotation(), b.data.liquidType.color);
            }
        },
        hit(b, hitx, hity){
            hitEffect.at(b.x, b.y, b.rotation(), b.data.liquidType.color);
            if(b.data.liquidAmount == 0) return;
            Puddles.deposit(Vars.world.tileWorld(b.x, b.y), b.data.liquidType, 6 + b.data.liquidAmount / 400);
            if(b.data.liquidType.effect != null){
                Damage.status(b.team, b.x, b.y, 4 * Vars.tilesize, b.data.liquidType.effect, bulletSize * 60, true, true);
            };
            if(b.data.liquidType.temperature >= 1 || b.data.liquidType.flammability >= 0.5){
                Fires.create(b.tileOn());
            };
            if(b.data.liquidType.explosiveness >= 0.5){
                Damage.damage(b.team, b.x, b.y, 4 * Vars.tilesize, this.damage, true);
            };
        }
    });

    //子弹DATA
    function LiquidDriverBulletData(){
        const LiquidDriverBulletData = {
            from: null,
            to: null,
            liquidType: null,
            liquidAmount: 0,
            init(){
                this.liquidType = null;
                this.liquidAmount = 0;
            },
            reset(){
                this.from = null;
                this.to = null;
                this.liquidType = null;
            }
        };
        return LiquidDriverBulletData;
    };

    //质驱部分
    var liquidRegion, topRegion;
    const LiquidMassDriver = extend(MassDriver, name, {
        receiveEffect: Fx.hitLiquid,
        group: BlockGroup.liquids,
        knockback: 1.5,
        minDistribute: 400,
        hasLiquids: true,
        outputsLiquid: true,
        hasItems: false,
        noUpdateDisabled: false,
        load(){
            this.super$load();
            liquidRegion = Core.atlas.find(this.name + "-liquid");
            topRegion = Core.atlas.find(this.name + "-top");
        },
        setBars(){
            this.super$setBars();
            this.removeBar("items");
        },
        drawPlanRegion(plan, list){
            this.super$drawPlanRegion(plan, list);
            Draw.color(Color.white);
            Draw.rect(topRegion, plan.drawx(), plan.drawy());
            Draw.reset();
        }
    });

    LiquidMassDriver.buildType = (() => {
        return extend(MassDriver.MassDriverBuild, LiquidMassDriver, {
            created(){
                this.super$created();
                this.waitingShooter = -1; //POS
            },
            liquidLinkValid(){
                if(this.link == -1) return false;
                var other = Vars.world.build(this.link);
                return other != null && other.isValid() && other.block == this.block && other.team == this.team && this.within(other, this.block.range);
            },
            liquidShooterValid(){
                if(this.waitingShooter == -1) return false;
                var other = Vars.world.build(this.waitingShooter);
                return other != null && other.isValid() && other.block == this.block && other.team == this.team && other.link == this.pos() && this.within(other, this.block.range);
            },
            setShooter(shooter){
                if(shooter == null){
                    this.waitingShooter = -1;
                }
                else{
                    this.waitingShooter = shooter.pos();
                }      
            },
            onConfigureBuildTapped(other){
                if(this == other){
                    if(this.link == -1){
                        Vars.control.input.config.hideConfig();
                    }
                    else{
                        this.configure(-1);
                    }
                    return false;
                }
                else if(this.link == other.pos()){
                    this.configure(-1);
                    return false;
                }
                else if(this.block == other.block && other.dst(this.tile) <= this.block.range && this.team == other.team && this.liquidShooterValid() == false && other.liquidShooterValid() == false && other.liquidLinkValid() == false){
                    this.configure(other.pos());
                    return false;
                }

                return true;
            },
            configured(builder, value){
                if(builder != null && builder.isPlayer()){
                    this.updateLastAccess(builder.getPlayer());
                }

                if(value == -1){
                    let oth = Vars.world.build(this.link);
                    if(oth != null) oth.setShooter(null);
                    this.link = -1;
                }
                //POS返回坐标的BUG
                else if(typeof value === 'object' && value.x !== undefined && value.y !== undefined){
                    // 如果是坐标对象，尝试从坐标获取建筑
                    let oth = Vars.world.tileWorld(value.x, value.y);
                    if(oth != null && oth.build != null){
                        oth.setShooter(this);
                        this.link = oth.pos();
                    }
                }
                //
                else{
                    let oth = Vars.world.build(value);
                    if(oth != null) oth.setShooter(this);
                    this.link = value;
                }
            },
            drawConfigure(){
                var sin = Mathf.absin(Time.time, 6, 1);

                Draw.color(Pal.accent);
                Lines.stroke(1);
                Drawf.circles(this.x, this.y, (this.block.size / 2 + 1) * Vars.tilesize + sin - 2, Pal.accent);

                if(this.liquidLinkValid()){
                    var shooter = Vars.world.build(this.link);
                    Drawf.circles(shooter.x, shooter.y, (shooter.block.size / 2 + 1) * Vars.tilesize + sin - 2, Pal.place);
                    Drawf.arrow(this.x, this.y, shooter.x, shooter.y, (this.block.size / 2 + 1) * Vars.tilesize + sin, 4 + sin, Pal.accent);
                }

                Drawf.dashCircle(this.x, this.y, this.block.range, Pal.accent);
            },
            acceptItem(source, item){
                return false;
            },
            acceptLiquid(source, liquid){
                return this.liquidLinkValid() && this.state == MassDriver.DriverState.shooting && !liquid.gas && (this.liquids.current() == null || (this.liquids.current() == liquid && this.liquids.currentAmount() < this.block.liquidCapacity) || this.liquids.currentAmount() < this.block.minDistribute);
            },
            canDumpLiquid(to, liquid){
                return !(this.liquidLinkValid());
            },
            draw(){
                this.super$draw();

                if(this.liquids.current() != null){
                    let color = Color.valueOf("000000FF").cpy().lerp(this.liquids.current().color, this.liquids.currentAmount() / this.block.liquidCapacity);
                    Draw.color(color);
                    Draw.rect(liquidRegion,
                    this.x + Angles.trnsx(this.rotation + 180, this.reloadCounter * this.block.knockback),
                    this.y + Angles.trnsy(this.rotation + 180, this.reloadCounter * this.block.knockback), this.rotation - 90);
                };

                Draw.color(Color.white);

                Draw.rect(topRegion,
                this.x + Angles.trnsx(this.rotation + 180, this.reloadCounter * this.block.knockback),
                this.y + Angles.trnsy(this.rotation + 180, this.reloadCounter * this.block.knockback), this.rotation - 90);
            },
            updateTile(){
                var link = Vars.world.build(this.link);
                var hasLink = this.liquidLinkValid();
                var shooter = Vars.world.build(this.waitingShooter);
                var hasShooter = this.liquidShooterValid();

                if(hasLink){
                    this.link = link.pos();
                }
                if(hasShooter){
                    this.waitingShooter = shooter.pos();
                }
                if(this.reloadCounter > 0){
                    this.reloadCounter = Mathf.clamp(this.reloadCounter - this.edelta() / this.block.reload);
                }
                
                if(this.state == MassDriver.DriverState.idle){
                    if(hasShooter){
                        this.state = MassDriver.DriverState.accepting;
                    }
                    else if(hasLink){
                        this.state = MassDriver.DriverState.shooting;
                    }
                }

                if(this.state == MassDriver.DriverState.accepting){
                    if(!hasShooter){
                        this.setShooter(null);
                        this.state = MassDriver.DriverState.idle;
                        return;
                    }
                    var shooterRotation = this.angleTo(shooter);
                    this.rotation = Angles.moveToward(this.rotation, shooterRotation, this.block.rotateSpeed * this.efficiency);
                }

                if(this.state == MassDriver.DriverState.idle || this.state == MassDriver.DriverState.accepting){
                    this.dumpLiquid(this.liquids.current(), 1);
                }

                if(this.efficiency <= 0){
                    return;
                }

                if(this.state == MassDriver.DriverState.shooting){
                    if(!hasLink){
                        this.link = -1;
                        this.state = MassDriver.DriverState.idle;
                        return;
                    }
                    var targetRotation = this.angleTo(link);
                    this.rotation = Angles.moveToward(this.rotation, targetRotation, this.block.rotateSpeed * this.efficiency);

                    if(this.liquidLinkValid() && this.liquids.current() != null && this.liquids.currentAmount() >= this.block.minDistribute &&  // 连接有效，液体不为空，发射量足够
                    (link.liquids.current() == null || (link.liquids.current() == this.liquids.current() && link.block.liquidCapacity - link.liquids.currentAmount() >= this.block.minDistribute) || (link.liquids.currentAmount() < 1))){  // 目标的液体为空/相且有容量接纳/不同但量足够少(少于1)
                        var other = link;

                        if(this.reloadCounter <= 0.0001){
                            
                            if(other.state == MassDriver.DriverState.accepting &&
                            Angles.near(this.rotation, targetRotation, 2) && 
                            Angles.near(other.rotation, targetRotation + 180, 2)){
                                this.fireLiquid(other);
                                const timeToArrive = Math.min(this.block.bulletLifetime, this.dst(other) / this.block.bulletSpeed);
                                Time.run(timeToArrive, () => {
                                    other.state = MassDriver.DriverState.idle;
                                });
                                this.state = MassDriver.DriverState.idle;
                            }
                        }
                    }
                }
            },
            fireLiquid(target){
                this.reloadCounter = 1;

                var data = LiquidDriverBulletData();
                    data.from = this;
                    data.to = target;

                let maxTransfer = Math.min(this.liquids.currentAmount(), target.block.liquidCapacity - target.liquids.currentAmount());
                data.liquidAmount = maxTransfer;
                this.liquids.remove(this.liquids.current(), maxTransfer);
                data.liquidType = this.liquids.current();

                let angle = this.tile.angleTo(target);

                LiquidMassDriverBolt.create(this, this.team,
                this.x + Angles.trnsx(angle, this.block.translation), this.y + Angles.trnsy(angle, this.block.translation),
                angle, -1, this.block.bulletSpeed, this.block.bulletLifetime, data);

                this.block.shootEffect.at(this.x + Angles.trnsx(angle, this.block.translation), this.y + Angles.trnsy(angle, this.block.translation), angle);
                this.block.smokeEffect.at(this.x + Angles.trnsx(angle, this.block.translation), this.y + Angles.trnsy(angle, this.block.translation), angle);
                Effect.shake(this.block.shake, this.block.shake, this);
                this.block.shootSound.at(this.tile, Mathf.random(0.9, 1.1));
            },
            handleLiquidPayload(bullet, data){
                this.liquids.add(data.liquidType, data.liquidAmount);
                data.liquidAmount = 0;
                if(this.liquids.current() !=null && this.liquids.currentAmount() >= 1.5 * this.block.liquidCapacity){
                    var RM = this.liquids.currentAmount() - 1.5 * this.block.liquidCapacity;
                    this.liquids.remove(this.liquids.current(), RM);  //超额接收液体时最多接收1.5倍容量
                }
                
                Effect.shake(this.block.shake, this.block.shake, this);
                this.block.receiveEffect.at(bullet);

                this.reloadCounter = 1;
                bullet.remove();
            },
            write(write){
                this.super$write(write);
                write.i(this.waitingShooter);
            },
            read(read, revision){
                this.super$read(read, revision);
                this.waitingShooter = read.i();
            }
        })
    });
    return LiquidMassDriver;
};

exports.StatusProjector = (name, status) => {        //支持单个/多个状态效果
    var isSeq;
    let stflag = true;
    if(status instanceof StatusEffect){
        isSeq = false;
    }
    else if(status instanceof Seq){
        status.each(s => {
            if(!(s instanceof StatusEffect)){
                stflag = false;
            }
        });
        isSeq = true;
    }
    else{
        stflag = false;
    }

    if(!stflag) throw new Error("status参数错误");

    const statusStat = new Stat("status", StatCat.function),  //状态效果
        statusTime = new Stat("statustime", StatCat.function), //状态施加间隔
        statusDuration = new Stat("statusduration", StatCat.function);  //状态持续时间
    
    //useTime -> 状态持续时间
    //reload -> 施加状态间隔时间
    const SP = extend(OverdriveProjector, name, {
        setStats(){
            this.super$setStats();
            this.stats.remove(Stat.speedIncrease);
            this.stats.remove(Stat.productionTime);

            if(isSeq){
                var statusStr = "";
                status.each(s => {
                    statusStr += (s.hasEmoji() ? s.emoji() : "") + "[stat]" + s.localizedName + " ";
                });
                this.stats.add(statusStat, statusStr);
            }
            else{
                this.stats.add(statusStat, (status.hasEmoji() ? status.emoji() : "") + "[stat]" + status.localizedName);
            }

            this.stats.add(statusTime, this.reload / 60, StatUnit.seconds);
            this.stats.add(statusDuration, this.useTime / 60, StatUnit.seconds);

        },
        setBars(){
            this.super$setBars();
            this.removeBar("boost");
        },
        drawPlace(x, y, rotation, valid){
            this.drawPotentialLinks(x, y);
            this.drawOverlay(x * Vars.tilesize + this.offset, y * Vars.tilesize + this.offset, rotation);
            Drawf.dashCircle(x * Vars.tilesize + this.offset, y * Vars.tilesize + this.offset, this.range, Pal.accent);
        }
    });

    SP.hasBoost = false;
    SP.emitLight = true;
    SP.hasItems = false;

    //多状态效果时使用白色
    var color = (isSeq)?Color.valueOf("FFFFFF"):status.color; 
    const SE = new WaveEffect();

    Events.on(ContentInitEvent, cons(e => {
        SE.sizeFrom = 0;
        SE.sizeTo = SP.range;
        SE.strokeFrom = 3;
        SE.strokeTo = 0;
        SE.colorFrom = color;
        SE.colorTo = color
        SE.sides = 12;
        SE.lifetime = 60;

        SP.lightRadius = SP.range * 1.1;
    }));

    SP.buildType = prov(() => {
        var targets = new Seq();

        return extend(OverdriveProjector.OverdriveBuild, SP, {
            created(){
                this.super$created();
                this.refresh = 0;
                this.sflag = false;
            },
            updateTile(){
                if(this.efficiency > 0 && (this.refresh += Time.delta * this.efficiency) >= this.block.reload){
                    targets.clear();
                    this.refresh = 0;
                    this.sflag = true;
                    Units.nearby(this.team, this.x, this.y, this.block.range, u => {
                        targets.add(u);
                    });     
                }

                if(this.efficiency > 0 && this.sflag){
                    targets.each(target => {
                        if(isSeq){
                            status.each(s => {
                                target.apply(s, this.block.useTime);
                            });
                        }
                        else{
                            target.apply(status, this.block.useTime);
                        }  
                    });
                    this.sflag = false;
                    SE.at(this.x, this.y);
                }
            },
            drawSelect(){
                Drawf.dashCircle(this.x, this.y, this.block.range, Pal.accent);
            },
            draw(){
                Draw.rect(this.block.region, this.x, this.y, 0);

                let f = 1 - (Time.time / 100) % 1;
                Draw.alpha(1);
                Draw.color(color);
                Lines.stroke((2 * f + 0.2) * this.efficiency);
                Lines.square(this.x, this.y, Math.min(1 + (1 - f) * this.block.size * Vars.tilesize / 2, this.block.size * Vars.tilesize/2));

                Draw.reset();
            },
            write(write){
                write.f(this.refresh);
                write.bool(this.sflag);
            },
            read(read, revision){
                this.refresh = read.f();
                this.sflag = read.bool();
            }
        });
    });

    return SP;
};

//其实就改了颜色和Units.nearby
exports.EnemyStatusProjector = (name, status) => {        //支持单个/多个状态效果
    var isSeq;
    let stflag = true;
    if(status instanceof StatusEffect){
        isSeq = false;
    }
    else if(status instanceof Seq){
        status.each(s => {
            if(!(s instanceof StatusEffect)){
                stflag = false;
            }
        });
        isSeq = true;
    }
    else{
        stflag = false;
    }

    if(!stflag) throw new Error("status参数错误");

    const statusStat = new Stat("status", StatCat.function),  //状态效果
        statusTime = new Stat("statustime", StatCat.function), //状态施加间隔
        statusDuration = new Stat("statusduration", StatCat.function);  //状态持续时间
    
    //useTime -> 状态持续时间
    //reload -> 施加状态间隔时间
    const SP = extend(OverdriveProjector, name, {
        setStats(){
            this.super$setStats();
            this.stats.remove(Stat.speedIncrease);
            this.stats.remove(Stat.productionTime);

            if(isSeq){
                var statusStr = "";
                status.each(s => {
                    statusStr += (s.hasEmoji() ? s.emoji() : "") + "[stat]" + s.localizedName + " ";
                });
                this.stats.add(statusStat, statusStr);
            }
            else{
                this.stats.add(statusStat, (status.hasEmoji() ? status.emoji() : "") + "[stat]" + status.localizedName);
            }

            this.stats.add(statusTime, this.reload / 60, StatUnit.seconds);
            this.stats.add(statusDuration, this.useTime / 60, StatUnit.seconds);

        },
        setBars(){
            this.super$setBars();
            this.removeBar("boost");
        },
        drawPlace(x, y, rotation, valid){
            this.drawPotentialLinks(x, y);
            this.drawOverlay(x * Vars.tilesize + this.offset, y * Vars.tilesize + this.offset, rotation);
            Drawf.dashCircle(x * Vars.tilesize + this.offset, y * Vars.tilesize + this.offset, this.range, Pal.accent);
        }
    });

    SP.hasBoost = false;
    SP.emitLight = true;
    SP.hasItems = false;

    //多状态效果时使用黑色
    var color = (isSeq)?Color.valueOf("000000"):status.color; 
    const SE = new WaveEffect();

    Events.on(ContentInitEvent, cons(e => {
        SE.sizeFrom = 0;
        SE.sizeTo = SP.range;
        SE.strokeFrom = 3;
        SE.strokeTo = 0;
        SE.colorFrom = color;
        SE.colorTo = color
        SE.sides = 12;
        SE.lifetime = 60;

        SP.lightRadius = SP.range * 1.1;
    }));

    SP.buildType = prov(() => {
        var targets = new Seq();

        return extend(OverdriveProjector.OverdriveBuild, SP, {
            created(){
                this.super$created();
                this.refresh = 0;
                this.sflag = false;
            },
            updateTile(){
                if(this.efficiency > 0 && (this.refresh += Time.delta * this.efficiency) >= this.block.reload){
                    targets.clear();
                    this.refresh = 0;
                    this.sflag = true;
                    Units.nearbyEnemies(this.team, this.x, this.y, this.block.range, u => {
                        targets.add(u);
                    });     
                }

                if(this.efficiency > 0 && this.sflag){
                    targets.each(target => {
                        if(isSeq){
                            status.each(s => {
                                target.apply(s, this.block.useTime);
                            });
                        }
                        else{
                            target.apply(status, this.block.useTime);
                        }  
                    });
                    this.sflag = false;
                    SE.at(this.x, this.y);
                }
            },
            drawSelect(){
                Drawf.dashCircle(this.x, this.y, this.block.range, Pal.accent);
            },
            draw(){
                Draw.rect(this.block.region, this.x, this.y, 0);

                let f = 1 - (Time.time / 100) % 1;
                Draw.alpha(1);
                Draw.color(color);
                Lines.stroke((2 * f + 0.2) * this.efficiency);
                Lines.square(this.x, this.y, Math.min(1 + (1 - f) * this.block.size * Vars.tilesize / 2, this.block.size * Vars.tilesize/2));

                Draw.reset();
            },
            write(write){
                write.f(this.refresh);
                write.bool(this.sflag);
            },
            read(read, revision){
                this.refresh = read.f();
                this.sflag = read.bool();
            }
        });
    });

    return SP;
};

exports.LiquidProjector = (name, transferAmount) => {
    var bottomRegion;
    const LP = extend(OverdriveProjector, name, {
        canOverdrive: false,
        hasLiquids: true,
        baseColor: Color.valueOf("6F80E8"),
        phaseColor: Color.valueOf("88A4FF"),
        reload: 10,
        phaseRangeBoost: 64,
        init(){
            this.super$init();
            this.isLiquidProjector = true;
        },
        load(){
            this.super$load();
            bottomRegion = Core.atlas.find(this.name + "-bottom");
        },
        drawPlanRegion(plan, list){
            Draw.rect(bottomRegion, plan.drawx(), plan.drawy());
            Draw.rect(this.region, plan.drawx(), plan.drawy());
        },
        drawPlace(x, y, rotation, valid){
            this.drawPotentialLinks(x, y);
            this.drawOverlay(x * Vars.tilesize + this.offset, y * Vars.tilesize + this.offset, rotation);
            Drawf.dashCircle(x * Vars.tilesize + this.offset, y * Vars.tilesize + this.offset, this.range, this.baseColor);
        },
        setStats(){
            this.super$setStats();
            this.stats.remove(Stat.speedIncrease);
            this.stats.remove(Stat.booster);
            var items = this.findConsumer(f => f instanceof ConsumeItems);
            if(this.hasBoost && items instanceof ConsumeItems) this.stats.add(Stat.booster, StatValues.itemBoosters("{0}", this.stats.timePeriod, 0, this.phaseRangeBoost, items.items));
        },
        setBars(){
            this.super$setBars();
            this.removeBar("boost");
        },
        isLP(){
            return true;
        },
        icons(){
            return [bottomRegion, this.region];
        }
    });
    LP.buildType = prov(() => {
        return extend(OverdriveProjector.OverdriveBuild, LP, {
            acceptLiquid(source, liquid){
                return this.liquids.current() == null || (this.liquids.current() == liquid && this.liquids.currentAmount() < this.block.liquidCapacity) || this.liquids.currentAmount() < 0.1;
            },
            updateTile(){
                this.smoothEfficiency = Mathf.lerpDelta(this.smoothEfficiency, this.efficiency, 0.08);
                this.heat = Mathf.lerpDelta(this.heat, this.efficiency > 0 ? 1 : 0, 0.08);
                this.charge += this.heat * Time.delta * this.efficiency;

                if(this.block.hasBoost){
                    this.phaseHeat = Mathf.lerpDelta(this.phaseHeat, this.optionalEfficiency, 0.1);
                }

                if(this.charge >= this.block.reload){
                    var realRange = this.block.range + this.phaseHeat * this.block.phaseRangeBoost;
                    if(this.liquids.current() != null && this.liquids.currentAmount() > 0.01){
                        Vars.indexer.eachBlock(this, realRange, other => (!(typeof other.block.isLP === 'function') && other.block.hasLiquids && !(other.block instanceof LiquidBlock || other.block instanceof Autotiler || other.block instanceof Wall || other.block instanceof ItemBridge)), other => {
                            if(other.acceptLiquid(this, this.liquids.current())){
                                let maxamount = Math.min(transferAmount, other.block.liquidCapacity - other.liquids.get(this.liquids.current()), this.liquids.currentAmount());
                                if(maxamount > 0.1){
                                    other.liquids.add(this.liquids.current(), maxamount);      
                                    this.liquids.remove(this.liquids.current(), maxamount);
                                }          
                            }
                        });
                    }
                    this.charge = 0;
                }

                if(this.efficiency > 0){
                    this.useProgress += this.delta();
                }

                if(this.useProgress >= this.block.useTime){
                    this.consume();
                    this.useProgress %= this.block.useTime;
                }
            },
            draw(){
                Draw.rect(bottomRegion, this.x, this.y);
                if(this.liquids.current() != null && this.liquids.currentAmount() > 0.001){
                    this.drawLiquid();
                }
                this.super$draw();
            },
            drawLiquid(){
                let frame = this.liquids.current().getAnimationFrame();
                let gas = this.liquids.current().gas ? 1 : 0;
                let lq = Vars.renderer.fluidFrames[gas][frame];
                let liquidRegion = Tmp.tr1;
                liquidRegion.set(lq);
                let size = this.block.size, threshold = (size - 1) / 2;
                let x0 = this.x - threshold * Vars.tilesize, y0 = this.y - threshold * Vars.tilesize;
                for(let x = 0; x < size; x++){
                    for(let y = 0; y < size; y++){
                        Drawf.liquid(liquidRegion, x0 + x * Vars.tilesize, y0 + y * Vars.tilesize, this.liquids.currentAmount() / this.block.liquidCapacity * 1.0, this.liquids.current().color.write(Tmp.c1));
                    }
                }
                //Drawf.liquid(liquidRegion, this.x, this.y, this.liquids.currentAmount() / this.block.liquidCapacity * 1.0, this.liquids.current().color.write(Tmp.c1));
            },
            drawSelect(){
                var realRange = this.block.range + this.phaseHeat * this.block.phaseRangeBoost;
                if(this.liquids.current() != null && this.liquids.currentAmount() > 0.01){
                    var color = this.liquids.current().color.cpy();
                    color.a = Mathf.absin(4, 1);
                    Vars.indexer.eachBlock(this, realRange, other => (!(typeof other.block.isLP === 'function') && other.block != LP && other.block.hasLiquids && other.acceptLiquid(this, this.liquids.current()) && !(other.block instanceof LiquidBlock || other.block instanceof Autotiler || other.block instanceof Wall || other.block instanceof ItemBridge)), other => Drawf.selected(other, color));
                }
                Drawf.dashCircle(this.x, this.y, realRange, this.block.baseColor);
            }
        });
    });
    return LP;
};

exports.ShieldDoor = (name) => {
    const shieldColor = Color.valueOf("00FFFF");
    const SD = extend(ShieldWall, name, {
        solid: false,
        absorbLasers: true
    });
    SD.buildType = prov(() => {
        return extend(ShieldWall.ShieldWallBuild, SD, {
            UC(){
                let SDB = this;
                var unitConsumer = cons(unit => {
                    let overdst = unit.hitSize/2 + this.shieldRadius + 10 - unit.dst(SDB);
                    if(overdst > 0){
                        unit.vel.setZero();
                        unit.move(Tmp.v1.set(unit).sub(this).setLength(overdst + 0.01));
                        if(Mathf.chanceDelta(0.12 * Time.delta)){
                            Fx.circleColorSpark.at(unit.x, unit.y, shieldColor);
                        }
                    }
                });
                return unitConsumer;
            },
            updateTile(){
                if(this.power.status > 0){
                    if(!this.broken()){
                        Units.nearbyEnemies(this.team, this.x, this.y, this.shieldRadius + 10, this.UC());
                    } 

                    if(this.breakTimer > 0){
                        this.breakTimer -= Time.delta * this.efficiency;
                    }else{
                        this.shield = Mathf.clamp(this.shield + this.block.regenSpeed * this.efficiency * this.edelta(), 0, this.block.shieldHealth * this.efficiency);
                    }
                }

                if(this.hit > 0){
                    this.hit -= Time.delta / 10;
                    this.hit = Math.max(this.hit, 0);
                }

                this.shieldRadius = Mathf.lerpDelta(this.shieldRadius, this.broken() ? 0 : 1, 0.12);
            },
            draw(){
                Draw.rect(this.block.region, this.x, this.y);
                Draw.color(this.team.color);
                Draw.alpha(0.5);
                Draw.rect(this.block.teamRegion, this.x, this.y);
                Draw.reset();
                if(this.shieldRadius > 0){
                    let radius = this.shieldRadius * Vars.tilesize * this.block.size / 2;
                    Draw.z(Layer.shields + 0.01);
                    Draw.color(shieldColor, Color.white, Mathf.clamp(this.hit));
                    this.drawShield(radius);
                    Draw.reset();
                    Drawf.additive(this.block.glowRegion, this.block.glowColor, (1 - this.block.glowMag + Mathf.absin(this.block.glowScl, this.block.glowMag)) * this.shieldRadius, this.x, this.y, 0, Layer.blockAdditive);
                }
            },
            drawShield(radius){
                if(Vars.renderer.animateShields){
                    Fill.square(this.x, this.y, radius);
                }else{
                    Lines.stroke(1.5);
                    Draw.alpha(0.09 + Mathf.clamp(0.08 * hit));
                    Fill.square(this.x, this.y, radius);
                    Draw.alpha(1);
                    Lines.poly(this.x, this.y, 4, radius, 45);
                    Draw.reset();
                }
            },
            absorbLasers(){
                return this.block.absorbLasers && !this.broken();
            }
        });
    });
    return SD;
};

//以下是单位相关/////////////////////////////////////////////////////////////////////////////

exports.HoverTank = (name) => {

    const 倍乘级单位直构工厂 = require("LI/LIblockslib").倍乘级单位直构工厂;
    const 多幂级单位直构工厂 = require("LI/LIblockslib").多幂级单位直构工厂;
    const 无量级单位直构工厂 = require("LI/LIblockslib").无量级单位直构工厂;

    const HT = extend(UnitType, name, {
        getDependencies(cons){ //用来防止直构工厂被添加到单位研究要求中
            Vars.content.blocks().each(block => {
                if(block != 倍乘级单位直构工厂 && block != 多幂级单位直构工厂 && block != 无量级单位直构工厂 && block instanceof Reconstructor){
                    block.upgrades.each(recipe => {
                        if(recipe[1] == this){
                            cons.get(block);
                        }
                    });
                }
            });

            let researchReqs = this.researchRequirements();
            for(let i = 0; i < researchReqs.length; i++){
                let stack = researchReqs[i];
                cons.get(stack.item);
            }
        }
    });
    HT.constructor = prov(() => extend(UnitTypes.elude.constructor.get().class, {}));

    return HT;
};

exports.HealCommand = () => {
    //healAI:寻找残血单位治疗，优先寻找高血量、掉血多的单位
    function healAI(){
        const healRange = 480;
        const healAI = extend(DefenderAI, {
            damagedTarget: null,
            updateMovement(){         
                if(this.target instanceof Unit && this.target.team == this.unit.team){
                    if(!this.target.within(this.unit, this.unit.type.range * 0.65)){
                        this.moveTo(this.target, this.unit.type.range * 0.65);
                    }
                }
            },
            updateTargeting(){
                if(this.timer.get(this.timerTarget, 15)){
                    this.damagedTarget = Units.closest(this.unit.team, this.unit.x, this.unit.y, healRange, u => !u.dead && u.type != this.unit.type && u.health < u.maxHealth, (u, tx, ty) =>  -u.maxHealth - (u.maxHealth - u.health) + Mathf.dst2(u.x, u.y, tx, ty) / 6400);
                }

                if(this.damagedTarget == null){
                    this.super$updateTargeting();
                }
                else{
                    this.target = this.damagedTarget;
                }
            }
        });
        return healAI;
    };
    const HC = new UnitCommand("heal", "add", u => new healAI());
    return HC;
};
