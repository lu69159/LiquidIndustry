function newAttr(name) {
    exports[name] =(() => {
        const c = Attribute.add(name);
        return c;
    })();
}

newAttr("cryofluid");
newAttr("scrapfluid");