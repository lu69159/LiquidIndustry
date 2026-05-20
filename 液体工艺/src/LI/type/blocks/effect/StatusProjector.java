package LI.type.blocks.effect;

import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.io.*;
import mindustry.Vars;
import mindustry.entities.*;
import mindustry.entities.effect.WaveEffect;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;
import mindustry.ui.Bar;
import mindustry.world.blocks.defense.OverdriveProjector;
import mindustry.graphics.*;
import mindustry.world.meta.*;

public class StatusProjector extends OverdriveProjector {
    public Seq<StatusEffect> status = new Seq<>();
    public float effectTime = 60f;
    public Effect applyEffect;
    public Color applyColor;

    public boolean applyOnEnemies = false;

    public StatusProjector(String name){
        super(name);
        range = 120f;
        useTime = 600f;
        reload = 240f;
        hasBoost = false;
        hasItems = false;
        canOverdrive = false;
        lightRadius = range * 1.1f;
    }

    @Override
    public void init() {
        super.init();
        applyEffect = new WaveEffect(){{
            lifetime = effectTime;
            sizeTo = range;
            colorFrom = colorTo = applyColor = status.size == 1 ? status.first().color : applyOnEnemies ? Color.gray : Pal.accent;
        }};
    }

    public void setStatus(StatusEffect... effects){
        status.addAll(effects);
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.remove(Stat.speedIncrease);
        stats.remove(Stat.productionTime);
        String statusStr = "";
        for(var s : status){
            statusStr += (s.hasEmoji() ? s.emoji() : "") + "[stat]" + s.localizedName + " ";
        }
        stats.add(new Stat("status", StatCat.function), statusStr);
        stats.add(new Stat("statustime", StatCat.function), reload / 60, StatUnit.seconds);
        stats.add(new Stat("statusduration", StatCat.function), useTime / 60, StatUnit.seconds);
    }

    @Override
    public void setBars() {
        super.setBars();
        removeBar("boost");
        addBar("cooldown", (StatusProjectorBuild entity) -> new Bar("bar.cooldown", Pal.lightOrange, () -> entity.refresh / reload));
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        drawPotentialLinks(x, y);
        drawOverlay(x * Vars.tilesize + offset, y * Vars.tilesize + offset, rotation);
        Drawf.dashCircle(x * Vars.tilesize + offset, y * Vars.tilesize + offset, range, applyColor);
    }

    public class StatusProjectorBuild extends OverdriveBuild{
        Seq<Unit> targets = new Seq<Unit>();
        float refresh;
        boolean hasTarget;

        @Override
        public boolean shouldConsume() {
            return enabled && (hasTarget || refresh > 0);
        }

        @Override
        public void updateTile(){
            smoothEfficiency = Mathf.lerpDelta(smoothEfficiency, efficiency, 0.05f);
            heat = (reload - refresh)/effectTime;

            targets.clear();
            if(applyOnEnemies){
                Units.nearbyEnemies(team, x, y, range, u -> {
                    targets.add(u);
                });
            }else{
                Units.nearby(team, x, y, range, u -> {
                    targets.add(u);
                });
            }

            hasTarget = targets.size > 0;

            if(efficiency > 0){
                if(refresh > 0) refresh = Math.max(refresh - delta() * efficiency, 0f);
                if(refresh <= 0 && hasTarget){
                    refresh = reload;
                    applyEffect.at(x, y);
                    for(var u : targets){
                        for(var s : status){
                            u.apply(s, useTime);
                        }
                    }
                }
            }
        }

        @Override
        public void draw() {
            Draw.rect(region, x, y, 0);
            if(heat <= 1f){
                Draw.alpha(1);
                Draw.color(applyColor.cpy());
                float f = 2 * heat - 1;
                Lines.stroke(1.5f * (1 - f*f) * smoothEfficiency);
                Lines.square(x, y, Math.min(heat * size * Vars.tilesize / 2, (float) (size * Vars.tilesize) / 2));

                Draw.reset();
            }
        }

        @Override
        public void drawSelect() {
            Drawf.dashCircle(x, y, range, applyColor);
        }

        @Override
        public void write(Writes write) {
            write.f(refresh);
        }

        @Override
        public void read(Reads read, byte revision) {
            refresh = read.f();
        }
    }
}
