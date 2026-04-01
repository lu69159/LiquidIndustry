const jh = require("LI/LIweathers").极寒;
const bx = require("LI/LIweathers").暴雪;
const NT = new Planet("Nepture", Planets.sun, 1.2, 2.5);
NT.localizedName = "尼普顿";
NT.description = "温度极低，暗无天日，常年暴雪。地表矿物稀缺，存在大量未被开发的液体资源。";
NT.meshLoader = prov(() => new MultiMesh(
	new HexMesh(NT, 5),
	new HexSkyMesh(NT, 2, 0.15, 0.05, 5, Color.valueOf("D8F3FF30"), 2, 0.42, 1, 0.43),
));

const c1 = Color.valueOf("D8F3FF"), c2 = Color.valueOf("5ECCF7");
NT.generator = extend(TantrosPlanetGenerator, {
	addWeather(sector, rules){
		const ZX2 = require("map/maps")["地火"];
		const map6bx = require("map/maps")["暴雪前哨"];
		if(sector.preset != ZX2 && sector.preset != map6bx){
			rules.weather.add(
				new Weather.WeatherEntry(
					jh,
					60 * 5,    // fMin
					60 * 30,   // fMax
					60 * 60,   // durationMin
					60 * 300,  // durationMax
				)
			);
		}
		if(sector.preset == map6bx){
			var w6 = new Weather.WeatherEntry(bx);
			w6.always = true;
			rules.weather.add(w6);
		}
    },
	//generateSector(sector){	},
	getColor(position ,out){
		var depth = Simplex.noise3d(2, 2, 0.56, 1.7, position.x, position.y, position.z);
		out.set(c1).lerp(c2, Mathf.clamp(Mathf.round(depth, 0.25)));
	}
});
NT.cloudMeshLoader = prov(() => new MultiMesh(
	new HexSkyMesh(NT, 2, 0.15, 0.05, 5, Color.valueOf("D8F3FF"), 2, 0.42, 1, 0.43),
	new HexSkyMesh(NT, 3, 0.6, 0.15, 5, Color.valueOf("D8F3FF80"), 2, 0.42, 1.2, 0.45),
));
NT.ruleSetter = (r) => {	
	r.lighting = true;
	r.showSpawns = true;
};
NT.landCloudColor = Color.valueOf("D8F3FF80");
NT.visible = true;
NT.accessible = true;
NT.alwaysUnlocked = true;
NT.allowCampaignRules = false;
NT.showRtsAIRule = false;
NT.allowSectorInvasion = false;
//NT.allowLaunchToNumbered = false;
NT.clearSectorOnLose = true;
NT.tidalLock = false;
NT.localizedName = "尼普顿";
NT.prebuildBase = true;
NT.bloom = false;
NT.startSector = 1;
NT.orbitRadius = 90;
NT.orbitTime = 360 * 60;
NT.rotateTime = 10 * 60;
NT.atmosphereRadIn = 0.03;
NT.atmosphereRadOut = 0.3;
NT.atmosphereColor = Color.valueOf("598DA4");
NT.iconColor = Color.valueOf("D8F3FF");
//NT.hiddenItems.addAll(Items.erekirItems).removeAll(Items.serpuloItems);
NT.updateLighting = false;
NT.launchMusic = Vars.tree.loadMusic("NTlaunch");

exports.NT = NT;

const ATD = require("base/ATD");
ATD.AddAllToDatabase("液体工艺", NT);
ATD.AddPlanetToDatabaseWithFunc(Planets.serpulo, NT, (thing) => {
	return !(thing instanceof SectorPreset) && thing != Blocks.advancedLaunchPad &&
		thing != Blocks.landingPad && thing != Blocks.interplanetaryAccelerator;
});

ATD.AddPlanetToDatabaseWithoutSectors(NT, Planets.serpulo);
