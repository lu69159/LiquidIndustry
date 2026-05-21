package multicraft.ui;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import mindustry.core.*;
import mindustry.type.*;
import mindustry.ui.*;

public class ItemImage extends Stack{
    public ItemImage(TextureRegion region) {
        add(new Table(o -> {
            o.left();
            o.image(region).size(32f);
        }));
    }

    public ItemImage(TextureRegion region, int amount) {
        add(new Table(o -> {
            o.left();
            o.image(region).size(32f);
        }));

        if (amount != 0) {
            add(new Table(t -> {
                t.left().bottom();
                t.add(amount >= 1000 ? UI.formatAmount(amount) : amount + "").fontScale(0.9f)
                .style(Styles.outlineLabel);
                t.pack();
            }));
        }
    }

    public ItemImage(ItemStack stack) {
        this(stack.item.uiIcon, stack.amount);
    }
}
