package LI.ui.dialogs;

import arc.Core;
import arc.Events;
import arc.graphics.Texture;
import arc.graphics.g2d.TextureRegion;
import arc.scene.ui.layout.Table;
import arc.util.Align;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Icon;
import mindustry.mod.Mods;
import mindustry.ui.dialogs.BaseDialog;

public class LIstartDialog {
    static Mods.LoadedMod mod = Vars.mods.locateMod("液体工艺");

    public LIstartDialog(){
        Events.on(EventType.ClientLoadEvent.class, (e) -> {
            show();
        });
    }

    public void show(){
            var dialog = new BaseDialog(Core.bundle.get("NOTICE"));

            Table table = new Table();
            table.add(Core.bundle.format("PROBLEM")).left().wrap().width(500).maxWidth(500).pad(4).labelAlign(Align.left);
            table.row();

            dialog.cont.pane(table).grow().center().maxWidth(900);
            dialog.buttons.button("Github", Icon.github, () -> Core.app.openURI("https://github.com/lu69159/LiquidIndustry-Java")).size(210, 64);
            dialog.buttons.button("[accent]" + Core.bundle.format("updatelog"), () -> {
                BaseDialog updatelog = new BaseDialog(Core.bundle.format("updatelog"));

                Table updateTable = new Table();
                updateTable.image(new TextureRegion(new Texture(mod.root.child("icon.png")))).size(500, 500).pad(3).left().row();
                updateTable.add(mod.root.child("updatelog.txt").readString("UTF-8"))
                        .left()
                        .growX()
                        .wrap()
                        .width(900)
                        .maxWidth(900)
                        .pad(4)
                        .labelAlign(Align.left);
                updateTable.row();

                updatelog.cont.pane(updateTable).grow().center().maxWidth(540);
                updatelog.buttons.defaults().size(210, 64);
                updatelog.addCloseButton(210);
                updatelog.show();
            }).size(210, 64);

            dialog.buttons.button("@close", Icon.cancel, dialog::hide).size(210, 64);
            dialog.show();
    }
}
