package LI.meta;

import arc.util.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;
import static mindustry.world.meta.StatValues.*;

public class LIStatValues{
    public static StatValue itemBoosters(String unit, float speedBoost, float rangeBoost, LiquidStack[] liquids){
        return table -> {
            table.row();
            table.table(c -> {
                c.table(Styles.grayPanel, b -> {
                    b.table(it -> {
                        for(LiquidStack stack : liquids){
                            it.add(displayLiquid(stack.liquid, stack.amount * 60f, true)).pad(10f).padLeft(15f).left().row();
                        }
                    }).left();

                    b.table(bt -> {
                        bt.right().defaults().padRight(3).left();
                        if(rangeBoost != 0) bt.add("[lightgray]+[stat]" + Strings.autoFixed(rangeBoost / tilesize, 2) + "[lightgray] " + StatUnit.blocks.localized()).row();
                        if(speedBoost != 0) bt.add("[lightgray]" + unit.replace("{0}", "[stat]" + Strings.autoFixed(speedBoost, 2) + "[lightgray]"));
                    }).right().top().grow().pad(10f).padRight(15f);
                }).growX().pad(5).padBottom(-5).row();
            }).growX().colspan(table.getColumns());
            table.row();
        };
    }
}
