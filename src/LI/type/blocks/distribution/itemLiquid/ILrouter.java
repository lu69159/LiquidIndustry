package LI.type.blocks.distribution.itemLiquid;

import arc.Core;
import arc.graphics.g2d.*;
import arc.util.Eachable;
import arc.util.Tmp;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.type.Liquid;
import mindustry.world.Block;
import mindustry.world.blocks.distribution.Router;
import mindustry.world.meta.BlockGroup;

import static mindustry.Vars.renderer;
import static mindustry.Vars.tilesize;

public class ILrouter extends Router {
    public TextureRegion bottomRegion;

    public ILrouter(String name) {
        super(name);
        health = 90;
        speed = 3f;
        floating = true;
        hasLiquids = true;
        liquidCapacity = 30f;
    }

    @Override
    public void load() {
        super.load();
        bottomRegion = Core.atlas.find(name + "-bottom");
    }

    @Override
    public boolean canReplace(Block other){
        if(other.alwaysReplace) return true;
        if(other.privileged) return false;
        return other.replaceable && (other != this || (rotate && quickRotate)) && ((this.group != BlockGroup.none && (other.group == this.group || other.group == BlockGroup.liquids)) || other == this) &&
                (size == other.size || (size >= other.size && ((subclass != null && subclass == other.subclass) || group.anyReplace)));
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        Draw.rect(bottomRegion, plan.drawx(), plan.drawy());
        Draw.rect(region,plan.drawx(), plan.drawy());
    }

    @Override
    protected TextureRegion[] icons() {
        return new TextureRegion[]{bottomRegion,region};
    }

    public class ILrouterBuild extends RouterBuild{
        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            if (liquids == null) return true;
            return liquids.current() == liquid || liquids.currentAmount() < 0.2f;
        }

        @Override
        public boolean canControl() {
            return false;
        }

        @Override
        public void draw() {
            Draw.rect(bottomRegion, x, y);
            if(liquids != null) drawLiquid();
            Draw.rect(region, x, y);
        }

        public void drawLiquid(){
            var liquidRegion = Tmp.tr1;
            liquidRegion.set(renderer.fluidFrames[liquids.current().gas ? 1 : 0][liquids.current().getAnimationFrame()]);
            var threshold = (size - 1) / 2;
            for(int tx = 0; tx < size; tx++){
                for(int ty = 0; ty < size; ty++){
                    Drawf.liquid(liquidRegion, x - threshold * tilesize + tx * tilesize, y - threshold * tilesize + ty * tilesize, liquids.currentAmount() / block.liquidCapacity, liquids.current().color.write(Tmp.c1));
                }
            }
        }

        @Override
        public void updateTile(){
            if(liquids != null && liquids.currentAmount() > 0.0001f) dumpLiquid(liquids.current());
            super.updateTile();
        }
    }
}
