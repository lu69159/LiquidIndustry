package LI.type.blocks.distribution.itemLiquid;

import arc.func.Boolf;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.math.geom.*;
import arc.struct.Seq;
import arc.util.Tmp;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.*;
import mindustry.input.Placement;
import mindustry.type.Liquid;
import mindustry.world.*;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.liquid.*;
import mindustry.world.meta.BlockGroup;
import LI.content.LIblocks;

import static mindustry.Vars.renderer;

public class ILduct extends Duct {
    public ILduct(String name) {
        super(name);
        health = 155;
        floating = hasLiquids = outputsLiquid = true;
        liquidCapacity = 20;
        liquidPressure = 1.03f;
        speed = 2f;
    }

    @Override
    public void handlePlacementLine(Seq<BuildPlan> plans) {
        boolean hasJunction = junctionReplacement != null && junctionReplacement.unlockedNow() && !junctionReplacement.isHidden();
        if(bridgeReplacement instanceof ItemBridge bridge) Placement.calculateBridges(plans, bridge, hasJunction, b -> b instanceof Duct || b instanceof StackConveyor || b instanceof Conveyor || b instanceof Conduit);
    }

    @Override
    public Block getReplacement(BuildPlan req, Seq<BuildPlan> plans){
        if(junctionReplacement == null) return this;

        Boolf<Point2> cont = p -> plans.contains(o -> o.x == req.x + p.x && o.y == req.y + p.y && (req.block instanceof Conveyor || req.block instanceof Junction || req.block instanceof Duct || req.block instanceof DuctJunction || req.block instanceof Conduit || req.block instanceof LiquidJunction));
        return cont.get(Geometry.d4(req.rotation)) &&
                cont.get(Geometry.d4(req.rotation - 2)) &&
                req.tile() != null &&
                (req.tile().block() instanceof Conveyor || req.tile().block() instanceof Duct || req.tile().block() instanceof Conduit) &&
                Mathf.mod(req.build().rotation - req.rotation, 2) == 1 ? junctionReplacement : this;
    }

    @Override
    public boolean blends(Tile tile, int rotation, int otherx, int othery, int otherrot, Block otherblock){
        //armored == false
        boolean conduitBlends = otherblock.hasLiquids && (otherblock.outputsLiquid || (lookingAt(tile, rotation, otherx, othery, otherblock))) && lookingAtEither(tile, rotation, otherx, othery, otherrot, otherblock);
        boolean ductBlends = (otherblock.outputsItems() || (lookingAt(tile, rotation, otherx, othery, otherblock) && otherblock.hasItems)) && lookingAtEither(tile, rotation, otherx, othery, otherrot, otherblock);
        return conduitBlends || ductBlends;
    }

    @Override
    public boolean canReplace(Block other) {
        if(other.alwaysReplace) return true;
        if(other.privileged) return false;
        return other.replaceable && (other != this || (rotate && quickRotate)) && ((this.group != BlockGroup.none && (other.group == this.group || other.group == BlockGroup.liquids))|| other == this) &&
            (size == other.size || (size >= other.size && ((subclass != null && subclass == other.subclass) || group.anyReplace))) && !(other instanceof StackConveyor);
    }

    @Override
    public void init(){
        super.init();
        if(junctionReplacement == null) junctionReplacement = LIblocks.SCJCQ;
    }

    public class ILductBuild extends DuctBuild {
        float smoothLiquid = 0f;

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            return (liquids.current() == liquid || liquids.currentAmount() < 0.2f)
                    && (tile == null || source == this || (source.relativeTo(tile.x, tile.y) + 2) % 4 != rotation);
        }

        @Override
        public void updateTile() {
            smoothLiquid = Mathf.lerpDelta(smoothLiquid, liquids.currentAmount() / liquidCapacity, 0.05f);
            if (liquids.currentAmount() > 0f) moveLiquidForward(false, liquids.current());
            super.updateTile();
        }

        @Override
        public void draw() {
            super.draw();
            if (liquids != null){
                drawLiquid();
            }
        }

        public void drawLiquid(){
            TextureRegion liquidRegion = Tmp.tr1;
            liquidRegion.set(renderer.fluidFrames[liquids.current().gas ? 1 : 0][liquids.current().getAnimationFrame()]);
            Draw.z(Layer.blockUnder+0.05f);
            Drawf.liquid(liquidRegion, x, y, liquids.currentAmount() / block.liquidCapacity, liquids.current().color.write(Tmp.c1));
        }
    }
}
