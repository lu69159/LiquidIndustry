package LI.type.blocks.defense.turrets;

import arc.util.Nullable;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.entities.bullet.BulletType;
import mindustry.content.Items;
import mindustry.type.Item;
import mindustry.ui.Bar;

public class ThoriumReactorLauncher extends PayloadTurret {
    Item fuel;

    public ThoriumReactorLauncher(String name, Item fuel) {
        super(name);
        unloadable = false;
        hasItems = true;
        acceptsItems = true;
        itemCapacity = 5;
        this.fuel = fuel;
        consumeItem(fuel, itemCapacity).update(false);
    }

    public ThoriumReactorLauncher(String name){
        this(name, Items.thorium);
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("items", (ThoriumReactorLauncherBuild entity) -> new Bar(
            () -> fuel.localizedName,
            () -> fuel.color,
            () -> (float)entity.items.get(fuel) / itemCapacity)
        );
    }

    public class ThoriumReactorLauncherBuild extends PayloadTurretBuild {

        @Override
        public boolean hasAmmo(){
            if(items.get(fuel) < itemCapacity && !cheating()) return false;
            return super.hasAmmo();
        }

        @Override
        public BulletType useAmmo(){
            if(!cheating()) items.clear();
            return super.useAmmo();
        }

        @Override
        public void handleStack(Item item, int amount, @Nullable Teamc source){
            for(int i = 0; i < amount; i++){
                handleItem(null, item);
            }
        }

        @Override
        public int removeStack(Item item, int amount){
            return 0;
        }

        @Override
        public void onDestroyed(){
            if(totalAmmo > 0 && items.get(fuel) > 0) {
                var su = ammo.peek().type().spawnUnit;
                su.deathExplosionEffect.at(x, y);
                su.deathSound.at(x, y);
                su.weapons.get(0).bullet.create(this, Team.derelict, x, y, 0);
            }
            super.onDestroyed();
        }
    }
}
