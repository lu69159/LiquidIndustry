exports.getClass = (name) => {
    return Packages.rhino.NativeJavaClass(Vars.mods.scripts.scope, Packages.java.net.URLClassLoader([
        Vars.mods.getMod("液体工艺").file.file().toURI().toURL()
    ], Vars.mods.mainLoader()).loadClass(name));
};

function Package(name) {
	var p = Packages.rhino.NativeJavaPackage(name, Vars.mods.mainLoader());
	Packages.rhino.ScriptRuntime.setObjectProtoAndParent(p, Vars.mods.scripts.scope)
	return p
}
var LI = Package('LI');
importPackage(LI);
importPackage(LI.blocks.defense.turret);

exports.LIMod = LI.LIMod;
exports.PayloadTurret = LI.blocks.defense.turrets.PayloadTurret;

