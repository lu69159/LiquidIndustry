package LI.type.blocks.distribution.itemLiquid;

import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.blocks.distribution.ItemBridge;
import mindustry.world.meta.BlockGroup;

public class ILbridge extends ItemBridge {
    public ILbridge(String name) {
        super(name);
        health = 90;
        range = 36;
        arrowPeriod = 1.35f;
        arrowTimeScl = 1.83f;
        transportTime = 0.2f;
        itemCapacity = 20;
        floating = true;
        hasLiquids = true;
        outputsLiquid = true;
        liquidCapacity = 20f;
    }

    @Override
    public boolean canReplace(Block other){
        if(other.alwaysReplace) return true;
        if(other.privileged) return false;
        return other.replaceable && (other != this || (rotate && quickRotate)) && ((this.group != BlockGroup.none && (other.group == this.group || other.group == BlockGroup.liquids)) || other == this) &&
                (size == other.size || (size >= other.size && ((subclass != null && subclass == other.subclass) || group.anyReplace)));
    }

    public class ILbridgeBuild extends ItemBridgeBuild{
        @Override
        public void updateTransport(Building other){
            if(warmup > 0){
                moved = (moveLiquidQuick() > 0.05f) || moved;
                var item = items.take();
                if(item != null && other.acceptItem(this, item)){
                    other.handleItem(this, item);
                    moved = true;
                }else if(item != null){
                    items.add(item, 1);
                    items.undoFlow(item);
                }
            }
        }

        public float moveLiquidQuick(){
            var other = Vars.world.build(link);
            if(other != null && linkValid(this.tile, other.tile) && liquids != null){
                if(other.liquids != null && other.liquids.current() != liquids.current() && other.liquids.currentAmount() > 0.2f) return 0f;
                float amount = Math.min(20f, Math.min(liquids.currentAmount(), 20f - other.liquids.currentAmount()));
                if(amount == 0)return 0;
                other.liquids.add(this.liquids.current(), amount);
                this.liquids.remove(this.liquids.current(), amount);
                return amount;
            }else{
                return 0f;
            }
        }

        @Override
        public void doDump() {
            dumpLiquid(liquids.current(), 1f);
            super.doDump();
        }
    }
}
