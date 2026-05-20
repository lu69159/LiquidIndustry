package LI.type.blocks.production;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.WaveEffect;
import mindustry.game.Team;
import mindustry.type.*;
import mindustry.world.blocks.production.GenericCrafter;
import LI.content.LIliquids;

public class FullExplosionCrafter extends GenericCrafter{
    public TextureRegion lightRegion;
    public Liquid explosionLiquid = LIliquids.SBRY;

    public BulletType explosionBullet = new ExplosionBulletType(2400f, 12 * 8f);
    public Effect explosionEffect = new WaveEffect(){{
        lifetime = 25f;
        sizeFrom = 0f;
        sizeTo = 12 * 8f;
        strokeFrom = 4f;
        strokeTo = 0f;
        colorFrom = Color.red.cpy().a(208/255f);
        colorTo = Color.valueOf("FF1010D0");
    }};

    public FullExplosionCrafter(String name) {
        super(name);
        hasLiquids = true;
        outputsLiquid = true;
        craftEffect = Fx.none;
    }

    @Override
    public void load() {
        super.load();
        lightRegion = Core.atlas.find(name + "-light");
    }

    public class FullExplosionCrafterBuild extends GenericCrafterBuild{
        float flash;

        @Override
        public void draw() {
            super.draw();
            float warn = liquids.get(explosionLiquid) / liquidCapacity;

            Draw.color(Color.orange.cpy().a(0f), Color.orange.cpy(), Math.max((warn - 0.3f)/0.7f, 0f));
            Fill.rect(x, y, size * Vars.tilesize, size * Vars.tilesize);
            if(warn > 0.3f){
                flash += (1f +(warn - 0.3f)/0.7f * 6f) * Time.delta;
                Draw.color(Color.yellow.cpy(), Color.red.cpy(), Mathf.absin(flash, 9f, 1f));
                Draw.alpha(warn);
                Draw.rect(lightRegion, x, y);
            }
        }

        @Override
        public void updateTile() {
            super.updateTile();
            if(liquids.get(explosionLiquid) >= liquidCapacity - 0.01f){
                kill();
                explosionBullet.create(this, Team.derelict, x, y, 0f);
                explosionEffect.at(x, y);
            }
        }
    }
}
