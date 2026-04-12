const mod = Vars.mods.locateMod("液体工艺");
Events.on(ClientLoadEvent, cons(e => {
    // 主对话框
    var dialog = new BaseDialog(Core.bundle.format("NOTICE"));

    // 主内容区域
    dialog.cont.pane((() => {
        let table = new Table();
        table.add(Core.bundle.format("PROBLEM")).left().wrap().width(500).maxWidth(500).pad(4).labelAlign(Align.left);
        table.row();
        return table;
    })()).grow().center().maxWidth(900);

    dialog.buttons.button("[accent]" + Core.bundle.format("updatelog"), run(() => {
        var updatelog= new BaseDialog(Core.bundle.format("updatelog"));
        updatelog.cont.pane((() => {
            var table = new Table();
            var tex = new Texture(mod.root.child("icon.png"));       
            table.image(new TextureRegion(tex)).size(500,500).pad(3).left().row();
            table.add(mod.root.child("updatelog.txt").readString("UTF-8")).
            left().
            growX().
            wrap().
            width(900).
            maxWidth(900).
            pad(4).
            labelAlign(Align.left);
            table.row();
            return table;
        })()).
        grow().
        center().
        maxWidth(540);
        updatelog.buttons.defaults().size(128, 64);
        updatelog.addCloseButton();
        updatelog.show();
    })).size(128, 64);

    dialog.buttons.button("@close", run(() => {
        dialog.hide()
    })).size(128, 64);

    dialog.show();
}));