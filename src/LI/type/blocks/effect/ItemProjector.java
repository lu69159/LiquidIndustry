package LI.type.blocks.effect;

import LI.meta.LIStatValues;
import arc.Core;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.Geometry;
import arc.struct.Seq;
import arc.util.*;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.Autotiler;
import mindustry.world.blocks.distribution.ItemBridge;
import mindustry.world.blocks.distribution.MassDriver;
import mindustry.world.blocks.sandbox.ItemSource;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.blocks.storage.StorageBlock;
import mindustry.world.blocks.storage.Unloader;
import mindustry.world.blocks.units.UnitCargoLoader;
import mindustry.world.blocks.units.UnitCargoUnloadPoint;
import mindustry.world.consumers.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

public class ItemProjector extends Block{
    public TextureRegion topRegion;

    public float reload = 6f;
    public float range = 80f;
    public float boostRange = 48f;
    public float boostSpeed = 0.5f;
    public boolean hasBoost = true;
    public Color baseColor = Pal.accent;

    public ItemProjector(String name){
        super(name);
        solid = true;
        update = true;
        group = BlockGroup.projectors;
        hasPower = true;
        hasItems = true;
        hasLiquids = true;
        canOverdrive = false;
        emitLight = true;
        lightRadius = 40f;
        envEnabled |= Env.space;
        ambientSound = Sounds.loopCircuit;
        ambientSoundVolume = 0.08f;

        itemCapacity = 30;
        liquidCapacity = 10f;
    }

    @Override
    public void load() {
        super.load();
        topRegion = Core.atlas.find(this.name + "-top");
    }

    @Override
    public boolean outputsItems(){
        return false;
    }


    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);
        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range, baseColor);
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.add(Stat.range, range / tilesize, StatUnit.blocks);

        if(hasBoost){
            stats.remove(Stat.booster);
            if(findConsumer(f -> f instanceof ConsumeLiquids) instanceof ConsumeLiquids cons){
                stats.add(Stat.booster, LIStatValues.itemBoosters("{0}" + StatUnit.timesSpeed.localized(), 1+boostSpeed, boostRange, cons.liquids));
            }else if(findConsumer(f -> f instanceof ConsumeLiquid) instanceof ConsumeLiquid cons){
                stats.add(Stat.booster, LIStatValues.itemBoosters("{0}" + StatUnit.timesSpeed.localized(), 1+boostSpeed, boostRange, new LiquidStack[]{new LiquidStack(cons.liquid, cons.amount)}));
            }
        }

        stats.add(Stat.speed, (60f / reload), StatUnit.itemsSecond);
    }

    @Override
    public void setBars() {
        super.setBars();
        removeBar("items");
    }

    public class ItemProjectorBuild extends Building implements Ranged{
        public float heat, charge = 0f, boostHeat, smoothEfficiency;

        @Override
        public float range() {
            return range;
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return items.get(item) < itemCapacity;
        }

        public float realRange(){
            return efficiency * (range + boostHeat * boostRange);
        }

        public float realReload(){
            if(!hasBoost) return reload;
            return reload / (1f + boostSpeed * boostHeat);
        }

        public boolean canTransferItem(Building to){
            var b = to.block;
            if(to.items == null) return false;
            if(to instanceof CoreBlock.CoreBuild || b instanceof ItemSource || b instanceof Autotiler || b instanceof ItemBridge || b instanceof MassDriver || b instanceof Unloader || b instanceof StorageBlock || b instanceof UnitCargoLoader || b instanceof UnitCargoUnloadPoint) return false;
            return b.acceptsItems;
        }

        @Override
        public void updateTile() {
            smoothEfficiency = Mathf.lerpDelta(smoothEfficiency, efficiency, 0.08f);
            heat = Mathf.lerpDelta(heat, efficiency > 0 ? 1f : 0f, 0.08f);
            charge += heat * Time.delta;

            if(hasBoost){
                boostHeat = Mathf.lerpDelta(boostHeat, optionalEfficiency, 0.08f);
            }

            if(charge >= realReload()){
                Seq<Item> its= new Seq<>();
                for(int i = 0; i < content.items().size; i++){
                    if(items.get(content.item(i)) > 0) its.add(content.item(i));
                }
                indexer.eachBlock(this, realRange(), b -> canTransferItem(b) && its.contains(i -> b.acceptItem(this, i)), other -> {
                    for(var i : its){
                        if(items.get(i) > 0 && other.acceptItem(this, i)){
                            other.items.add(i, 1);
                            this.items.remove(i, 1);
                        }
                    }
                });
                charge = 0f;
            }
        }

        @Override
        public void drawLight(){
            Drawf.light(x, y, lightRadius * smoothEfficiency, baseColor, 0.7f * smoothEfficiency);
        }

        @Override
        public void drawSelect(){
            var color = baseColor.cpy().a(Mathf.absin(4f, 1f));
            indexer.eachBlock(this, realRange(), this::canTransferItem, other -> Drawf.selected(other, color));
            Drawf.dashCircle(x, y, realRange(), baseColor);
        }

        @Override
        public void draw(){
            super.draw();

            if(items != null && items.any()){
                float f = 1f - (Time.time / 100f) % 1f;
                Draw.color(baseColor.cpy());
                Draw.alpha(boostHeat * Mathf.absin(Time.time, 50f / Mathf.PI2, 1f) * 0.5f);
                Draw.rect(topRegion, x, y);
                Draw.alpha(1f);
                Lines.stroke((2f * f + 0.1f) * heat);

                float r = Math.max(0f, Mathf.clamp(2f - f * 2f) * size * tilesize / 2f - f - 0.2f), w = Mathf.clamp(0.5f - f) * size * tilesize;
                Lines.beginLine();
                for (int i = 0; i < 4; i++) {
                    Lines.linePoint(x + Geometry.d4(i).x * r + Geometry.d4(i).y * w, y + Geometry.d4(i).y * r - Geometry.d4(i).x * w);
                    if (f < 0.5f)
                        Lines.linePoint(x + Geometry.d4(i).x * r - Geometry.d4(i).y * w, y + Geometry.d4(i).y * r + Geometry.d4(i).x * w);
                }
                Lines.endLine(true);

                Draw.reset();
            }
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(heat);
            write.f(boostHeat);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            heat = read.f();
            boostHeat = read.f();
        }
    }
}
