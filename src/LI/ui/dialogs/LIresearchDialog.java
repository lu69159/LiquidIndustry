package LI.ui.dialogs;

import LI.content.LIplanets;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import mindustry.content.Planets;
import mindustry.type.Item;
import mindustry.type.ItemSeq;
import mindustry.type.Planet;
import mindustry.type.Sector;
import mindustry.ui.dialogs.ResearchDialog;

import static mindustry.Vars.content;

public class LIresearchDialog extends ResearchDialog {
    @Override
    public void rebuildItems() {
        items = new ItemSeq(){
            ObjectMap<Sector, ItemSeq> cache = new ObjectMap<>();

            {
                Seq<Planet> rootPlanets = new Seq<>();
                for(var planet : content.planets()){
                    if(planet.techTree == lastNode || lastNode.planet == planet){
                        rootPlanets.add(planet);
                    }
                }

                if(rootPlanets.size == 0 || rootPlanets.contains(LIplanets.NT)){
                    rootPlanets.add(Planets.serpulo);
                }

                for(Planet planet : rootPlanets){
                    for(Sector sector : planet.sectors){
                        if(sector.hasBase() && !sector.isFrozen()){
                            ItemSeq cached = sector.items();
                            cache.put(sector, cached);
                            cached.each((item, amount) -> {
                                values[item.id] += Math.max(amount, 0);
                                total += Math.max(amount, 0);
                            });
                        }
                    }
                }
            }

            @Override
            public void add(Item item, int amount){
                if(amount < 0){
                    amount = -amount;

                    double percentage = (double)amount / get(item);
                    int[] counter = {amount};
                    cache.each((sector, seq) -> {
                        if(counter[0] == 0) return;
                        int toRemove = Math.min((int)Math.ceil(percentage * seq.get(item)), counter[0]);
                        sector.removeItem(item, toRemove);
                        seq.remove(item, toRemove);
                        counter[0] -= toRemove;
                    });
                    amount = -amount;
                }
                super.add(item, amount);
            }
        };
        itemDisplay.rebuild(items);
    }
}
