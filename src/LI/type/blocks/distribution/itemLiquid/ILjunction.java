package LI.type.blocks.distribution.itemLiquid;

import arc.graphics.g2d.Draw;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.world.Block;
import mindustry.world.blocks.distribution.Sorter;
import mindustry.world.blocks.liquid.LiquidJunction;
import mindustry.world.meta.*;

public class ILjunction extends Sorter {
    public ILjunction(String name) {
        super(name);
        health = 90;
        invert = true;
        configurable = false;
        floating = true;
        hasLiquids = true;
        outputsLiquid = true;
        liquidCapacity = 0f;
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        Draw.rect(region, plan.drawx(), plan.drawy());
    }

    @Override
    public void drawPlanConfig(BuildPlan plan, Eachable<BuildPlan> list) {}

    @Override
    public boolean canReplace(Block other){
        if(other.alwaysReplace) return true;
        if(other.privileged) return false;
        return other.replaceable && (other != this || (rotate && quickRotate)) && ((this.group != BlockGroup.none && (other.group == this.group || other.group == BlockGroup.liquids)) || other == this) &&
                (size == other.size || (size >= other.size && ((subclass != null && subclass == other.subclass) || group.anyReplace)));
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.remove(Stat.liquidCapacity);
    }

    @Override
    public void setBars() {
        super.setBars();
        removeBar("liquid");
    }

    public class ILjunctionBuild extends SorterBuild{
        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            return true;
        }

        @Override
        public Item config() {
            return null;
        }

        @Override
        public void draw() {
            Draw.rect(region, x, y);
        }

        @Override
        public Building getLiquidDestination(Building source, Liquid liquid) {
            if(!enabled) return this;

            var next = nearby((source.relativeTo(tile.x, tile.y) + 4) % 4);
            if(next == null || (!next.acceptLiquid(this, liquid) && !(next.block instanceof LiquidJunction))){
                return this;
            }
            if(isSame(source) && isSame(next)){
                return this;
            }
            return next.getLiquidDestination(this, liquid);
        }
    }
}
