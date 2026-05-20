package LI.type.blocks.effect;

import arc.Core;
import arc.math.Mathf;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.geom.Geometry;
import arc.util.*;
import arc.util.io.*;
import mindustry.entities.units.BuildPlan;
import mindustry.graphics.Drawf;
import mindustry.logic.Ranged;
import mindustry.type.Liquid;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.liquid.LiquidBlock;
import mindustry.world.blocks.Autotiler;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.distribution.ItemBridge;
import mindustry.world.meta.*;
import mindustry.world.consumers.*;

import static mindustry.Vars.*;

public class LiquidProjector extends Block {
    public TextureRegion bottomRegion;
    public TextureRegion topRegion;

    public float reload = 10f;
    public float range = 80f;
    public float transferAmount;
    public float useTime = 900f;
    public float boostRange = 48f;
    public boolean hasBoost = true;
    public Color baseColor = Color.valueOf("6F80E8");

    public LiquidProjector(String name, float transferAmount){
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

        liquidCapacity = 800f;

        this.transferAmount = transferAmount;
    }

    public LiquidProjector(String name){
        this(name, 40f);
    }

    @Override
    public void load() {
        super.load();
        bottomRegion = Core.atlas.find(this.name + "-bottom");
        topRegion = Core.atlas.find(this.name + "-top");
    }

    @Override
    public boolean outputsItems(){
        return false;
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        Draw.rect(bottomRegion, plan.drawx(), plan.drawy());
        Draw.rect(this.region, plan.drawx(), plan.drawy());
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);
        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range, baseColor);
    }

    @Override
    public void setStats(){
        stats.timePeriod = useTime;
        super.setStats();

        stats.add(Stat.range, range / tilesize, StatUnit.blocks);
        stats.add(Stat.productionTime, useTime / 60f, StatUnit.seconds);

        if(hasBoost && findConsumer(f -> f instanceof ConsumeItems) instanceof ConsumeItems items){
            stats.remove(Stat.booster);
            stats.add(Stat.booster, StatValues.itemBoosters("{0}", stats.timePeriod, 0, boostRange, items.items));
        }
    }

    @Override
    protected TextureRegion[] icons() {
        return new TextureRegion[]{bottomRegion, region};
    }

    public class LiquidProjectorBuild extends Building implements Ranged {
        public float heat, charge = 0f, boostHeat, smoothEfficiency, useProgress;

        @Override
        public float range(){
            return range;
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            return liquids.current() == null || (liquids.current() == liquid && liquids.currentAmount() < liquidCapacity) || liquids.currentAmount() < 0.1;
        }

        public float realRange(){
            return efficiency * (range + boostHeat * boostRange);
        }

        public boolean canTransferLiquid(Building to) {
            if(!to.block.hasLiquids) return false;
            return !(to instanceof LiquidProjectorBuild) &&
                    !(to.block instanceof LiquidBlock || to.block instanceof Autotiler || to.block instanceof ItemBridge || to.block instanceof Wall) &&
                    to.acceptLiquid(this, liquids.current());
        }

        @Override
        public void updateTile() {
            smoothEfficiency = Mathf.lerpDelta(smoothEfficiency, efficiency, 0.08f);
            heat = Mathf.lerpDelta(heat, efficiency > 0 ? 1f : 0f, 0.08f);
            charge += heat * Time.delta;

            if (hasBoost) {
                boostHeat = Mathf.lerpDelta(boostHeat, optionalEfficiency, 0.08f);
            }

            if(charge >= reload){
                if(liquids.current() != null && liquids.currentAmount() > 0.1f){
                    indexer.eachBlock(this, realRange(), this::canTransferLiquid, other -> {
                        float maxAmount = Math.min(Math.min(transferAmount, liquids.currentAmount()), other.block.liquidCapacity - other.liquids.get(liquids.current()));
                        if(maxAmount > 0.1f){
                            other.liquids.add(liquids.current(), maxAmount);
                            liquids.remove(liquids.current(), maxAmount);
                        }
                    });
                }
                charge = 0f;
            }

            if(efficiency > 0){
                useProgress += delta();
            }

            if(useProgress >= useTime){
                consume();
                useProgress %= useTime;
            }
        }

        @Override
        public void drawLight(){
            Drawf.light(x, y, lightRadius * smoothEfficiency, (liquids.current() != null && liquids.currentAmount() >= 0.1 * liquidCapacity) ? liquids.current().color : baseColor, 0.7f * smoothEfficiency);
        }

        @Override
        public void drawSelect(){
            if(liquids.current() != null && liquids.currentAmount() > 0.1f){
                Drawf.dashCircle(x, y, realRange(), liquids.current().color);
                var color = liquids.current().color.cpy().a(Mathf.absin(4f, 1f));
                indexer.eachBlock(this, realRange(), this::canTransferLiquid, other -> Drawf.selected(other, color));
            }
            else{
                Drawf.dashCircle(x, y, realRange(), baseColor);
            }
        }

        @Override
        public void draw(){
            Draw.rect(bottomRegion, x, y);
            drawLiquid();
            super.draw();

            if(liquids != null && liquids.currentAmount() > 0.1f) {
                float f = 1f - (Time.time / 100f) % 1f;
                Draw.color(liquids.current().color.cpy());
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

        public void drawLiquid(){
            TextureRegion liquidRegion = Tmp.tr1;
            liquidRegion.set(renderer.fluidFrames[liquids.current().gas ? 1 : 0][liquids.current().getAnimationFrame()]);
            float threshold = (size - 1) / 2f;
            for(int tx = 0; tx < size; tx++){
                for(int ty = 0; ty < size; ty++){
                    Drawf.liquid(liquidRegion, x - (threshold - tx) * tilesize, y - (threshold - ty) * tilesize, liquids.currentAmount() / block.liquidCapacity, liquids.current().color.write(Tmp.c1));
                }
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
