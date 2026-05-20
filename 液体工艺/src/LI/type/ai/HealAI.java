package LI.type.ai;

import arc.math.Mathf;
import mindustry.ai.types.DefenderAI;
import mindustry.entities.Units;
import mindustry.gen.*;

public class HealAI extends DefenderAI {
    Unit damagedTarget;
    boolean hasHealRange = false;
    float healRange = 480f;

    public HealAI(boolean hasHealRange, float healRange){
        this.hasHealRange = hasHealRange;
        this.healRange = healRange;
    }

    public HealAI(){}

    @Override
    public void updateMovement(){
        unloadPayloads();

        if(target instanceof Unit u && u.team == unit.team){
            if(!u.within(unit, unit.type.range * 0.6f)){
                moveTo(target, unit.type.range * 0.6f);
            }
        }
    }

    @Override
    public void updateTargeting() {
        if(retarget()){
            if(hasHealRange) damagedTarget = Units.closest(unit.team, unit.x, unit.y, healRange, u -> !u.dead && u.type != unit.type && u.health < u.maxHealth, (u, tx, ty) ->  -u.maxHealth - (u.maxHealth - u.health) + Mathf.dst2(u.x, u.y, tx, ty) / 6400);
            else damagedTarget = Units.closest(unit.team, unit.x, unit.y, u -> !u.dead && u.type != unit.type && u.health < u.maxHealth);

            if(damagedTarget == null || damagedTarget.dead) target = findTarget(unit.x, unit.y, unit.range(), true, true);
            else target = damagedTarget;
        }
    }
}
