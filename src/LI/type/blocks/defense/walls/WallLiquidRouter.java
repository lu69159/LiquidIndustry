package LI.type.blocks.defense.walls;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.Eachable;
import arc.util.Tmp;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.type.Liquid;
import mindustry.world.blocks.defense.Wall;

import static mindustry.Vars.*;

public class WallLiquidRouter extends Wall {
    public TextureRegion bottomRegion;

    public WallLiquidRouter(String name) {
        super(name);
        update = true;
        hasLiquids = true;
        buildCostMultiplier = 2.5f; //为了平衡建造时间
    }

    @Override
    public void load() {
        super.load();
        bottomRegion = Core.atlas.find(name + "-bottom");
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        Draw.rect(bottomRegion, plan.drawx(), plan.drawy());
        Draw.rect(region, plan.drawx(), plan.drawy());
    }

    @Override
    public TextureRegion[] icons() {
        return new TextureRegion[]{bottomRegion, region};
    }

    public class WallLiquidRouterBuild extends WallBuild{
        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            if(liquids.current() == null) return true;
            return (liquids.current() == liquid || liquids.currentAmount() < 0.2);
        }

        @Override
        public void updateTile(){
            if(liquids.current() != null && liquids.currentAmount() > 0.01){
                dumpLiquid(liquids.current());
            }
            super.updateTile();
        }

        @Override
        public void draw() {
            Draw.rect(bottomRegion, x, y);
            if(liquids.current() != null && liquids.currentAmount() > 0.001){
                drawLiquid();
            }
            Draw.rect(region, x, y);
        }

        public void drawLiquid(){
            var liquidRegion = Tmp.tr1;
            liquidRegion.set(renderer.fluidFrames[liquids.current().gas ? 1 : 0][liquids.current().getAnimationFrame()]);
            float threshold = (size - 1) / 2f;
            for(int tx = 0; tx < size; tx++){
                for(int ty = 0; ty < size; ty++){
                    Drawf.liquid(liquidRegion, x - threshold * tilesize + tx * tilesize, y - threshold * tilesize + ty * tilesize, liquids.currentAmount() / block.liquidCapacity, liquids.current().color.write(Tmp.c1));
                }
            }
        }
    }
}
