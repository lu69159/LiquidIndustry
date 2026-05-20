package LI.type.blocks.storage;

import arc.Core;
import arc.func.Cons;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.Strings;
import arc.util.Time;
import mindustry.content.*;
import mindustry.entities.Units;
import mindustry.world.meta.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.entities.Damage;
import mindustry.ui.Bar;
import mindustry.world.blocks.storage.CoreBlock;

import static mindustry.Vars.*;

public class PowerCore extends CoreBlock {
    public float powerout = 200f;
    public float range = 24f;
    public float damage = 10f;
    public float reload = 45f;
    public Color powerColor = Color.valueOf("8EFFEAC0");

    public TextureRegion powerRegion;

    public PowerCore(String name) {
        super(name);
        consumesPower = false;
        hasPower = outputsPower = conductivePower = true;
    }

    @Override
    public void load() {
        super.load();
        powerRegion = Core.atlas.find(name + "-power");
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.basePowerGeneration, powerout, StatUnit.powerSecond);
        stats.add(Stat.shootRange, range, StatUnit.blocks);
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("power", (PowerCoreBuild core) -> new Bar(
                () -> Core.bundle.format("bar.poweroutput", Strings.fixed(core.getPowerProduction() * 60f, 1)),
                () -> Pal.powerBar,
                () -> 1
        ));
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range * tilesize, Pal.accent);
    }

    public class PowerCoreBuild extends CoreBuild{
        private float attackTimer = 0f;

        private final PowerCoreBuild build = this;
        private final Cons<Unit> unitConsumer = unit -> {
            float dst = unit.hitSize/2 * range * tilesize - unit.dst(build);
            if(dst > 0){
                build.attackTimer = reload;
                for(int i = 0; i < 2; i++) Fx.chainLightning.at(build.x, build.y, 0, Color.valueOf("8EFFEAC0"), unit);
                Fx.circleColorSpark.at(unit.x, unit.y, Color.valueOf("8EFFEAC0"));
                unit.apply(StatusEffects.electrified, 60);
                Damage.damage(build.team, unit.x, unit.y, tilesize * 1f, damage);
                Sounds.shootArc.at(unit.x, unit.y);
            }
        };

        @Override
        public float getPowerProduction() {
            return powerout / 60f;
        }

        @Override
        public void updateTile() {
            if(attackTimer > 0) attackTimer -= Time.delta;
            attackTimer = Math.max(0, attackTimer);
            if(attackTimer == 0){
                Units.nearbyEnemies(team, x, y, range * tilesize, unitConsumer);
            }
            super.updateTile();
        }

        @Override
        public void draw() {
            super.draw();
            Draw.z(Layer.effect);
            Draw.color(powerColor);
            float sin = 2.5f * (float)Math.sin(Time.time * 0.05f) + 20f;
            Draw.rect(powerRegion, x, y, size * 0.3f * sin, size * 0.3f * sin, 45);
        }

        @Override
        public void drawSelect() {
            super.drawSelect();
            Drawf.dashCircle(x, y, range * tilesize, Pal.accent);
        }
    }
}
