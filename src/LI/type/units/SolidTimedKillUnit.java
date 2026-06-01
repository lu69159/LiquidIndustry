package LI.type.units;

import mindustry.entities.EntityCollisions;
import mindustry.gen.*;

public class SolidTimedKillUnit extends TimedKillUnit{
    protected SolidTimedKillUnit(){
        super();
    }

    @Override
    public EntityCollisions.SolidPred solidity() {
        return EntityCollisions::legsSolid;
    }

    public static SolidTimedKillUnit create() {
        return new SolidTimedKillUnit();
    }
}
