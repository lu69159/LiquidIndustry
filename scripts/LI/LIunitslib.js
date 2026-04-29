/*
function newUnit(name, unitType) {
	const u = extend(UnitType, name, {});
	u.constructor = () => extend(unitType, {});
	return exports[name] = u;
}
*/
const LIfx = require("base/effects");
const type = require("base/type");
const healCommand = type.HealCommand();

//纯辅助
const JZQ = new UnitType("建造球");
JZQ.constructor = prov(() => extend(UnitTypes.flare.constructor.get().class, {}));
JZQ.defaultCommand = UnitCommand.rebuildCommand;
exports.JZQ = JZQ;

const XFQ = new UnitType("修复球");
XFQ.constructor = prov(() => extend(UnitTypes.flare.constructor.get().class, {}));
XFQ.defaultCommand = UnitCommand.repairCommand;
exports.XFQ = XFQ;

const ZLQ = new UnitType("治疗球");
ZLQ.constructor = prov(() => extend(UnitTypes.flare.constructor.get().class, {}));
ZLQ.commands.add(UnitCommand.moveCommand, UnitCommand.enterPayloadCommand, healCommand);
ZLQ.defaultCommand = healCommand;
exports.ZLQ = ZLQ;

const ZTQ = new UnitType("状态球");
ZTQ.constructor = prov(() => extend(UnitTypes.flare.constructor.get().class, {}));
exports.ZTQ = ZTQ;

//核心
const JS = new UnitType("基石");
JS.constructor = prov(() => extend(UnitTypes.gamma.constructor.get().class, {}));
exports.JS = JS;

const GMS = new UnitType("伽马-S");
GMS.constructor = prov(() => extend(UnitTypes.gamma.constructor.get().class, {}));
exports.GMS = GMS;

const DET = new UnitType("德尔塔");
DET.constructor = prov(() => extend(UnitTypes.mega.constructor.get().class, {}));
exports.DET = DET;

//悬浮
const SM = new UnitType("水黾");
SM.constructor = prov(() => extend(UnitTypes.elude.constructor.get().class, {}));
exports.SM = SM;

const FZ = new UnitType("筏蛛");
FZ.constructor = prov(() => extend(UnitTypes.elude.constructor.get().class, {}));
exports.FZ = FZ;

const HL = type.HoverTank("河狸");
exports.HL = HL;

const DY = type.HoverTank("电鳐");
exports.DY = DY;

const JX = type.HoverTank("巨蟹");
exports.JX = JX;

const YA = new UnitType("渊螯");
YA.constructor = prov(() => extend(UnitTypes.elude.constructor.get().class, {}));
exports.YA = YA;
Events.on(ContentInitEvent, cons(e => {
	YA.weapons.add(
		Object.assign(new Weapon("液体工艺-渊螯3"), {
			x: 18,
			y: -21.5,
			shootY: 8,
			recoil: 2,
			mirror: true,
			alternate: true,
			rotate: true,
			rotateSpeed: 2,
			reload: 75,
			cooldownTime: 60,
			inaccuracy: 0,
			ejectEffect: Fx.none, //Fx.sparkShoot
			shootSound: Sounds.shootOmura,
			bullet: Object.assign(new RailBulletType(), {
				hitColor: Color.white,
				damage: 720,
				length: 648,    
				range: 648,
				shootEffect: LIfx.whiteRailShoot,
				pierceEffect: LIfx.whiteRailHit,
				pointEffect: LIfx.whiteRailTrail,
				pointEffectSpace: 24,
				hitEffect: Fx.massiveExplosion,
				smokeEffect: Fx.shootBig2,
				pierceDamageFactor: 0,
				buildingDamageMultiplier: 0.2,
				status: StatusEffects.shocked
			})
		})
	);
	YA.init();
}));
