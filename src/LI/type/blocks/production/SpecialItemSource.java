package LI.type.blocks.production;

import arc.Core;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.Vars;
import mindustry.entities.units.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.sandbox.*;
import LI.content.LIitems;

public class SpecialItemSource extends ItemSource{
    TextureRegion bottomRegion, centerRegion, ringRegion;

    public SpecialItemSource(String name) {
        super(name);
        sync = hasLiquids = true;
        health = 3260;
        size = 3;
        liquidCapacity = 5f;
    }

    @Override
    public void load() {
        super.load();
        bottomRegion = Core.atlas.find(name + "-bottom");
        centerRegion = Core.atlas.find(name + "-center");
        ringRegion = Core.atlas.find("液体工艺-神佑光环");
    }

    @Override
    protected TextureRegion[] icons() {
        return new TextureRegion[]{bottomRegion, region};
    }

    @Override
    public void drawPlanConfig(BuildPlan plan, Eachable<BuildPlan> list) {
        drawPlanConfigCenter(plan, plan.config, "");
    }

    @Override
    public void drawPlanConfigCenter(BuildPlan plan, Object content, String region) {
        if(content == null){
            Draw.rect(centerRegion, plan.drawx(), plan.drawy());
            return;
        }
        if(content instanceof Item i && i.color != null){
            Draw.color(i.color);
            Draw.rect(centerRegion, plan.drawx(), plan.drawy());
            Draw.color();
        }
    }

    public class SpecialItemSourceBuild extends ItemSourceBuild{
        @Override
        public void buildConfiguration(Table table) {
            var items = Vars.content.items().copy();
            if(items.contains(LIitems.SMWZ)){ items.remove(LIitems.SMWZ); }
            if(items.contains(LIitems.SMSP)){ items.remove(LIitems.SMSP); }
            ItemSelection.buildTable(block, table, items, () -> this.outputItem, this::configure, block.selectionRows, block.selectionColumns);
        }

        @Override
        public void draw() {
            Color color;
            if(outputItem == null){
                color = Color.valueOf("FFFFFFCF");
            }
            else{
                color = outputItem.color;
            }
            Draw.color(color);
            Draw.rect(centerRegion, x, y);
            Draw.color();
            Draw.rect(region, x, y);

            Draw.z(Layer.effect);
            Draw.color(color);
            Draw.rect(ringRegion, x, y, size * Vars.tilesize, size * Vars.tilesize, Time.time * 1.6f);
        }

        @Override
        public boolean shouldConsume() {
            return outputItem != null && enabled;
        }
    }
}
