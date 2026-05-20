package LI.type.blocks.distribution.liquid;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.world.blocks.sandbox.LiquidSource;
import mindustry.world.meta.*;

public class LiquidUnloader extends LiquidSource {
    TextureRegion centerRegion;
    float speed = 2f;

    public LiquidUnloader(String name) {
        super(name);
    }

    @Override
    public void load() {
        super.load();
        centerRegion = Core.atlas.find(this.name + "-center");
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.speed, speed * 60f, StatUnit.liquidSecond);
        stats.remove(Stat.liquidCapacity);
    }

    @Override
    public void drawPlanConfig(BuildPlan plan, Eachable<BuildPlan> list) {
        drawPlanConfigCenter(plan, plan.config, centerRegion + "", true);
    }

    public class LiquidUnloaderBuild extends LiquidSourceBuild{
        Building dumpingTo = null;
        Liquid liquidBegin = null;
        int offset = 0;
        @Override
        public void updateTile() {
            if (liquidBegin != source) {
                liquids.clear();
                liquidBegin = source;
            }
            for (int i = 0; i < proximity.size; i++) {
                Building other = proximity.get((offset + i) % proximity.size);

                if (other.interactable(team) && other.block.hasLiquids && source != null && other.liquids.get(source) > 0) {
                    dumpingTo = other;
                    if (liquids.currentAmount() < block.liquidCapacity) {
                        float amount = Math.min(speed, other.liquids.get(source));
                        liquids.add(source, amount);
                        other.liquids.remove(source, amount);
                    }
                }
            }
            if (proximity.size > 0) {
                offset++;
                offset %= proximity.size;
            }
            dumpLiquid(liquids.current());
        }


        @Override
        public boolean canDumpLiquid(Building to, Liquid liquid) {
            return to != dumpingTo;
        }

        @Override
        public void draw() {
            Draw.rect(region, x, y);
            if (source == null) {
                Draw.color(181 / 255f, 171 / 255f, 171 / 255f);
            } else {
                Draw.color(source.color);
            }
            Draw.rect(centerRegion, x, y);
            Draw.color();
        }
    }
}
