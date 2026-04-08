const {NT} = require("planets/Nepture");
var cache;
function itemseq(){
    var items = extend(ItemSeq, {
        add(item, amount){
            this.sectorItemRemove(item, -amount);
            this.super$add(item, amount);
        },
        sectorItemRemove(item, amount){
            if(amount <= 0) return;
            let percentage = amount / this.get(item), counter = [amount];
            cache.each((sector, seq) => {
                if(counter[0] == 0) return;
                let toRemove = Math.min(Math.ceil(percentage * seq.get(item)), counter[0]);
                //actually remove it from the sector
                sector.removeItem(item, toRemove);
                seq.remove(item, toRemove);
                counter[0] -= toRemove;
            });
        }
    });
    return items;
}
Events.on(ClientLoadEvent, e => {
    Vars.ui.research = extend(ResearchDialog, {
        rebuildItems(){        
            this.items = itemseq();
            cache = new ObjectMap();

            let rootPlanets = new Seq();
            Vars.content.planets().each(planet => {
                if(planet.techTree == this.lastNode || this.lastNode.planet == planet){
                    rootPlanets.add(planet);
                }
            });
            if(rootPlanets.size == 0 || (rootPlanets.size == 1 && rootPlanets.contains(NT))){
                rootPlanets.add(Planets.serpulo);
            };
            rootPlanets.each(planet => {
                planet.sectors.each(sector => {
                    if(sector.hasBase() && !sector.isFrozen()){
                        let cached = sector.items();//ItemSeq
                        cache.put(sector, cached);
                        cached.each((item, amount) => {
                            this.items.add(item, Math.max(amount, 0));
                        });
                    }
                });
            });   
            this.itemDisplay.rebuild(this.items);
        }
    });
});