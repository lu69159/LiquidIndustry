package LI.type.status;

import arc.struct.Seq;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;

public class ImmuneStatusEffect extends StatusEffect {
    public Seq<StatusEffect> immuneEffects = new Seq<>();

    public ImmuneStatusEffect(String name) {
        super(name);
    }

    public ImmuneStatusEffect(String name, Seq<StatusEffect> immuneEffects) {
        this(name);
        this.immuneEffects = immuneEffects;
    }

    @Override
    public void init(){
        super.init();
        for(StatusEffect e :  immuneEffects){
            handleOpposite(e);
        }
    }

    @Override
    protected void handleOpposite(StatusEffect other){
        opposites.add(other);
        trans(other, (unit, result, time) -> {});
    }

    @Override
    public void applied(Unit unit, float time, boolean extend){
        if(immuneEffects.size == 0) super.applied(unit, time, extend);
        for(var e : immuneEffects){
            if(unit.hasEffect(e)) unit.unapply(e);
        }
        if(!extend || applyExtend) applyEffect.at(unit.x, unit.y, unit.rotation - 90, applyColor, unit);
    }
}
