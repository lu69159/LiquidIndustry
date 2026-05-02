function Package(name) {
	var p = Packages.rhino.NativeJavaPackage(name, Vars.mods.mainLoader());
	Packages.rhino.ScriptRuntime.setObjectProtoAndParent(p, Vars.mods.scripts.scope)
	return p
}
var LI = Package('LI');
importPackage(LI);
importPackage(LI.type.defense.turrets);
importPackage(LI.blocks.defense.turrets);

exports.LIMod = LI.LIMod;
exports.LIBlocks = LI.LIBlocks;
exports.PayloadTurret = LI.type.defense.turrets.PayloadTurret;
exports.ThoriumReactorLauncher = LI.blocks.defense.turrets.ThoriumReactorLauncher;

