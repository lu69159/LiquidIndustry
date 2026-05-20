package LI.type.blocks.power;

import arc.Core;
import arc.Events;
import arc.audio.Sound;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.type.*;
import mindustry.content.*;
import mindustry.world.meta.*;
import mindustry.gen.Sounds;
import mindustry.ui.Bar;
import mindustry.game.EventType;
import mindustry.entities.Effect;
import mindustry.entities.effect.MultiEffect;
import mindustry.world.blocks.power.NuclearReactor;


import static mindustry.Vars.*;

public class OutputsItemNuclearReactor extends NuclearReactor {
    public int outputAmount = 8;
    public Item outputItem = Items.scrap;
    Color CEcolor = Color.valueOf("FF6060");
    Effect consumeEffect = new MultiEffect(Fx.explosion, Fx.shockwave, Fx.blockExplosionSmoke);
    Sound consumeSound = Sounds.explosion;

    public OutputsItemNuclearReactor(String name) {
        super(name);

        itemCapacity = 20;
        liquidCapacity = 50;
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.output, StatValues.items(itemDuration, new ItemStack(outputItem, outputAmount)));
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("items", (OutputsItemNuclearReactorBuild entity) -> new Bar(outputItem.localizedName, outputItem.color, () -> (float) entity.items.get(outputItem) / itemCapacity));
    }

    public class OutputsItemNuclearReactorBuild extends NuclearReactorBuild {

        @Override
        public void updateTile(){
            int fuel = items.get(fuelItem);
            float fullness = (float)fuel / itemCapacity;
            productionEfficiency = fullness;

            if(fuel > 0 && enabled){
                heat += fullness * heating * Math.min(delta(), 4f);

                if(timer(timerFuel, itemDuration / timeScale)){
                    consume();
                    items.add(outputItem, outputAmount);
                    consumeEffect.at(x, y, CEcolor);
                    consumeSound.at(this.x, this.y, Mathf.random(0.5f, 0.8f), Mathf.random(0.8f, 1.1f));
                }
            }else{
                productionEfficiency = 0f;
                heat = Math.max(0f, heat - Time.delta / ambientCooldownTime);
            }

            if(heat > 0){
                float maxUsed = Math.min(liquids.currentAmount(), heat / coolantPower);
                heat -= maxUsed * coolantPower;
                liquids.remove(liquids.current(), maxUsed);
            }

            if(heat > smokeThreshold){
                float smoke = 1.0f + (heat - smokeThreshold) / (1f - smokeThreshold); //ranges from 1.0 to 2.0
                if(Mathf.chance(smoke / 20.0 * delta())){
                    Fx.reactorsmoke.at(x + Mathf.range(size * tilesize / 2f),
                            y + Mathf.range(size * tilesize / 2f));
                }
            }

            heat = Mathf.clamp(heat);
            heatProgress = heatOutput > 0f ? Mathf.approachDelta(heatProgress, heat * heatOutput * (enabled ? 1f : 0f), heatWarmupRate * delta()) : 0f;

            if(heat >= 0.999f){
                Events.fire(EventType.Trigger.thoriumReactorOverheat);
                kill();
            }

            dumpAccumulate(outputItem);

            if(items.get(outputItem) >= itemCapacity && enabled){
                Events.fire(new EventType.GeneratorPressureExplodeEvent(this));
                kill();
            }
        }
    }
}
