package LI.type.blocks.defense.turrets;

import arc.struct.*;
import arc.util.io.*;
import mindustry.ctype.*;
import mindustry.gen.*;
import mindustry.ui.*;
import mindustry.entities.part.DrawPart;
import mindustry.graphics.Pal;
import mindustry.logic.LAccess;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.consumers.*;
import mindustry.world.draw.DrawTurret;
import mindustry.world.meta.*;
import arc.Events;
import mindustry.game.EventType;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Table;
import mindustry.entities.bullet.BulletType;

import static mindustry.Vars.*;

public class PayloadTurret extends Turret {
    public ObjectMap<UnlockableContent, BulletType> ammoTypes = new ObjectMap<>();

    /** Whether to draw parts for each ammoType */
    public boolean drawPartsForAmmo = false;
    public ObjectMap<UnlockableContent, Seq<DrawPart>> ammoParts = new OrderedMap<>();
    private ObjectMap<UnlockableContent, DrawTurret> realDrawers = new OrderedMap<>();

    public PayloadTurret(String name) {
        super(name);

        maxAmmo = 1;
        ammoPerShot = 1;
        acceptsPayload = true;
    }

    public void ammo(Object... objects){
        ammoTypes = ObjectMap.of(objects);
    }
    public void ammoparts(Object... objects){ ammoParts = OrderedMap.of(objects); }

    /** Makes copies of all bullets and limits their range. */
    public void limitRange(){
        limitRange(1f);
    }

    /** Makes copies of all bullets and limits their range. */
    public void limitRange(float margin){
        for(var entry : ammoTypes.copy().entries()){
            entry.value.lifetime = (range + margin) / entry.value.speed;
        }
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.remove(Stat.itemCapacity);
        stats.add(Stat.ammo, StatValues.ammo(ammoTypes, true));
        stats.add(Stat.ammoCapacity, maxAmmo / ammoPerShot, StatUnit.shots);
    }

    @Override
    public void setBars(){
        super.setBars();
        addBar("ammo", (PayloadTurretBuild entity) ->
            new Bar(
                "stat.ammo",
                Pal.ammo,
                () -> (float)entity.totalAmmo / maxAmmo
            )
        );
    }

    @Override
    public void init(){
        consume(new ConsumePayloadFilter(p -> ammoTypes.containsKey(p)){
            @Override
            public void build(Building build, Table table){
                MultiReqImage image = new MultiReqImage();

                for(var block : content.blocks()) displayContent(build, image, block);
                for(var unit : content.units()) displayContent(build, image, unit);

                table.add(image).size(8 * 4);
            }

            void displayContent(Building build, MultiReqImage image, UnlockableContent content){
                if(filter.get(content) && content.unlockedNow()){
                    image.add(new ReqImage(new Image(content.uiIcon), () -> build instanceof PayloadTurret.PayloadTurretBuild it && it.ammo.size>0 && ((PayloadEntry)it.ammo.peek()).content == content));
                }
            }

            @Override
            public float efficiency(Building build){
                //valid when there's any ammo in the turret
                return (build instanceof PayloadTurretBuild it && it.totalAmmo > 0)? 1 : 0;
            }

            @Override
            public void display(Stats stats){
                //don't display
            }
        });

        if(targetGround){
            ammoTypes.each((content, type) -> placeOverlapRange = Math.max(placeOverlapRange, range + type.rangeChange + placeOverlapMargin));
        }

        super.init();
    }

    @Override
    public void load(){
        super.load();
        if(drawPartsForAmmo && ammoParts.size > 0){
            if(!(drawer instanceof DrawTurret dt)) return;

            var orderedKeys = ammoParts.keys().toSeq().sort();
            for(UnlockableContent i : orderedKeys){
                realDrawers.put(i, new DrawTurret(dt.basePrefix){{
                    parts.addAll(dt.parts);
                    parts.addAll(ammoParts.get(i));
                }});
                realDrawers.get(i).load(this);
            }
        }
    }

    public class PayloadTurretBuild extends TurretBuild{

        @Override
        public Object senseObject(LAccess sensor){
            return switch(sensor){
                case currentAmmoType -> ammo.size > 0 ? ((PayloadEntry)ammo.peek()).content : null;
                default -> super.senseObject(sensor);
            };
        }

        @Override
        public void draw(){
            if(drawPartsForAmmo && realDrawers.size > 0){
                if(ammo.size > 0){
                    realDrawers.get(((PayloadEntry)ammo.peek()).content).draw(this);
                    return;
                }
            }
            super.draw();
        }

        @Override
        public boolean acceptPayload(Building source, Payload payload){
            return ammoTypes.containsKey(payload.content()) && totalAmmo + ammoTypes.get(payload.content()).ammoMultiplier <= maxAmmo;
        }

        @Override
        public void handlePayload(Building source, Payload payload){
            if(totalAmmo == 0){
                Events.fire(EventType.Trigger.resupplyTurret);
            }

            BulletType type = ammoTypes.get(payload.content());
            if(type == null) return;
            totalAmmo += type.ammoMultiplier;

            for(int i = 0; i < ammo.size; i++){
                PayloadEntry entry = (PayloadEntry)ammo.get(i);
                //if found, put it to the right
                if(entry.content == payload.content()){
                    entry.amount += type.ammoMultiplier;
                    ammo.swap(i, ammo.size - 1);
                    return;
                }
            }

            //not be found
            ammo.add(new PayloadEntry(payload, (int)type.ammoMultiplier));
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.b(ammo.size);
            for(AmmoEntry entry : ammo){
                PayloadEntry i = (PayloadEntry)entry;
                write.s(i.content.id);
                write.bool(i.content instanceof Block);
                write.s(i.amount);
            }
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            ammo.clear();
            totalAmmo = 0;
            int amount = read.ub();
            for(int i = 0; i < amount; i++){
                UnlockableContent payload;
                int id = read.s();
                boolean isBlock = read.bool();

                if(isBlock) payload = content.block(id);
                else payload = content.unit(id);

                int payloadAmount = Math.min(read.s(), maxAmmo);

                //only add ammo if this is a valid ammo type
                if(payload != null && ammoTypes.containsKey(payload)){
                    totalAmmo += payloadAmount;
                    ammo.add(new PayloadEntry(payload, payloadAmount));
                }
            }
        }
    }

    public class PayloadEntry extends AmmoEntry{
        public UnlockableContent content;

        PayloadEntry(Payload payload, int amount){
            this.content = payload.content();
            this.amount = amount;
        }

        PayloadEntry(UnlockableContent content, int amount){
            this.content = content;
            this.amount = amount;
        }

        @Override
        public BulletType type(){
            return ammoTypes.get(content);
        }

        @Override
        public String toString(){
            return "PayloadEntry{" +
                    "payloadContent=" + content +
                    ", amount=" + amount +
                    '}';
        }
    }
}
