const GCD = extend(Conveyor, "光传带", {
    itemCapacity: 0,
    emitLight: true,
    setStats(){
        this.super$setStats();
        this.stats.remove(Stat.itemsMoved);
    },
    isGCD(){
        return true;
    },
    isAccessible(){
        return false;
    }
});
GCD.buildType = prov(() => {
    return extend(Conveyor.ConveyorBuild, GCD, {
        farthestGCD(comefrom){
            let next = this.nearby(this.rotation);           
            if(next != null && next != comefrom){
                if(typeof next.block.isGCD === 'function'){
                    if(next.nearby(next.rotation) == this){
                        return this;
                    }
                    else{
                        next = next.farthestGCD(comefrom);
                        return next;
                    }
                }
            }
            return this;
        },
        GCDnearby(){
            let to = this.farthestGCD(this).front();
            if(to == null || to.team != this.team || to.block.instantTransfer || typeof to.block.isGCD === 'function') return null;
            return to;
        },
        updateTile(){

        },
        drawLight(){
            Drawf.light(this.x, this.y, 3 * Vars.tilesize, Color.white, 0.7);
        },
        acceptItem(source, item){
            if(this.power.status <= 0 || source.team != this.team) return false;
            let far = this.farthestGCD(this), to = this.GCDnearby();
            if(to == null || !to.acceptItem(far, item)) return false;
            return !(source.block.rotate && this.front() == source) && Edges.getFacingEdge(source.tile, this.tile) != null && Math.abs(Edges.getFacingEdge(source.tile, this.tile).relativeTo(this.tile.x, this.tile.y) - this.rotation) != 2;
        },
        handleItem(source, item){
            if(this.power.status <= 0) return;
            let to = this.GCDnearby();
            if(to == null) return;
            to.handleItem(this, item);
        },
        acceptStack(item, amount, source){
            if(this.acceptItem(source, item) && this.power.status > 0){
                return amount;
            }else{
                return 0;
            }
        },
        handleStack(item, amount, source){
            if(this.power.status <= 0) return;
            let to = this.GCDnearby();
            if(to == null) return;
            to.handleStack(item, amount, this);
        },
    });
});
exports.光传带 = GCD;