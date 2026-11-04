package LI.type.blocks.power;

import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.util.*;
import mindustry.content.*;
import mindustry.type.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;
import mindustry.game.EventType;
import mindustry.world.blocks.power.NuclearReactor;
import LI.content.LIfx;
import LI.content.LIliquids;

import static mindustry.Vars.tilesize;

public class ModerateNuclearReactor extends NuclearReactor {
    public Liquid moderator = LIliquids.ZS;
    public Liquid coolant = Liquids.cryofluid;
    public float moderateThreshold = 10f;

    public @Nullable LiquidStack outputLiquid;
    public boolean explodeOnFull = false;

    public ModerateNuclearReactor(String name) {
        super(name);

        itemCapacity = 40;
        liquidCapacity = 40f;

        explosionShake = 9f;
        explosionShakeDuration = 120f;
        explosionRadius = 30;
        explosionDamage = 10000;

        explodeEffect = LIfx.moderateReactorExplosion;
    }

    @Override
    public void init(){
        if(outputLiquid != null){
            outputsLiquid = true;
            hasLiquids = true;
        }
        if(explodeOnFull && outputLiquid != null && explosionPuddleLiquid == null){
            explosionPuddleLiquid = outputLiquid.liquid;
        }
        super.init();
    }

    @Override
    public void setStats() {
        super.setStats();
        if(outputLiquid != null){
            stats.add(Stat.output, StatValues.liquids(1f, outputLiquid));
        }
    }

    @Override
    public void setBars(){
        super.setBars();
        if(outputLiquid != null){
            addLiquidBar(outputLiquid.liquid);
        }
    }

    public class ModerateNuclearReactorBuild extends NuclearReactorBuild {
        public float outputLiquidHeat = 0f;

        @Override
        public void updateTile(){
            int fuel = items.get(fuelItem);
            float moderatorFullness = liquids.get(moderator) >= moderateThreshold ? 1f : liquids.get(moderator) / moderateThreshold;
            float fullness = (float) fuel / itemCapacity;
            productionEfficiency = moderatorFullness * fullness;

            if(fuel > 0 && enabled){
                heat += fullness * heating * Math.min(delta(), 4f);

                if(timer(timerFuel, itemDuration / timeScale)){
                    consume();
                }
            }else{
                productionEfficiency = 0f;
                heat = Math.max(0f, heat - Time.delta / ambientCooldownTime);
            }

            if(outputLiquid != null){
                float added = Math.min(productionEfficiency * delta() * outputLiquid.amount, liquidCapacity - liquids.get(outputLiquid.liquid));
                liquids.add(outputLiquid.liquid, added);
                dumpLiquid(outputLiquid.liquid);

                if(explodeOnFull && liquids.get(outputLiquid.liquid) >= liquidCapacity - 0.01f){
                    Events.fire(new EventType.GeneratorPressureExplodeEvent(this));
                    kill();
                }

                outputLiquidHeat = liquids.get(outputLiquid.liquid) / liquidCapacity;
            }

            if(heat > 0 && liquids.get(moderator) >= 0.1f){
                float maxUsed = Math.min(liquids.get(coolant), heat / coolantPower);
                if(maxUsed > 0f){
                    heat -= maxUsed * coolantPower;
                    liquids.remove(coolant, maxUsed / moderatorFullness);
                }
            }

            if(heat > smokeThreshold || outputLiquidHeat > smokeThreshold){
                var h = Math.max(heat, outputLiquidHeat);
                float smoke = 1.0f + (h - smokeThreshold) / (1f - smokeThreshold); //ranges from 1.0 to 2.0
                if(Mathf.chance(smoke / 20.0 * delta())){
                    Fx.reactorsmoke.at(x + Mathf.range(size * tilesize / 2f),y + Mathf.range(size * tilesize / 2f));
                }
            }

            heat = Mathf.clamp(heat);
            heatProgress = heatOutput > 0f ? Mathf.approachDelta(heatProgress, heat * heatOutput * (enabled ? 1f : 0f), heatWarmupRate * delta()) : 0f;

            if(heat >= 0.999f){
                Events.fire(EventType.Trigger.thoriumReactorOverheat);
                kill();
            }
        }

        @Override
        public void draw(){
            drawer.draw(this);

            Draw.color(coolColor, hotColor, heat);
            Fill.rect(x, y, size * tilesize, size * tilesize);

            Draw.color(moderator.color);
            Draw.alpha(liquids.get(moderator) / liquidCapacity);
            Draw.rect(topRegion, x, y);

            Draw.color(coolant.color);
            Draw.alpha(liquids.get(coolant) / liquidCapacity);
            Draw.rect(topRegion, x, y);

            if(heat > flashThreshold || outputLiquidHeat > flashThreshold){
                float finalHeat = Math.max(heat, outputLiquidHeat);
                flash += (1f + ((finalHeat - flashThreshold) / (1f - flashThreshold)) * 5.4f) * Time.delta;
                Draw.color(Color.red, Color.yellow, Mathf.absin(flash, 9f, 1f));
                Draw.alpha(0.3f);
                Draw.rect(lightsRegion, x, y);
            }

            Draw.reset();
        }
    }
}
