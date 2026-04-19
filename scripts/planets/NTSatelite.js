const NT = require("planets/Nepture").NT;

const NTsatellite = new Planet("冰卫一", NT, 1, 1);
NTsatellite.meshLoader = prov(() => new MultiMesh(
	new HexMesh(NTsatellite, 2),
	new HexSkyMesh(NTsatellite, 3, 0.15, 0.02, 2, Color.valueOf("3C3C3C"), 2, 0.42, 1, 0.43),
));
NTsatellite.generator = extend(TantrosPlanetGenerator, {
    getColor(position ,out){
        out.set(Color.valueOf("3C3C3C"));
    }
});
Object.assign(NTsatellite, {
    localizedName: "实验卫星",
    description: "各种实验地图试验玩法",
    iconColor: Color.valueOf("3C3C3C"),
    landCloudColor: Color.valueOf("3C3C3C80"),
    atmosphereColor: Color.valueOf("3C3C3C"),
    launchMusic: Vars.tree.loadMusic("NTlaunch"),
    //visible: true,
    accessible: false,
    bloom: false,
    hasAtmosphere: false,
    //alwaysUnlocked: true,
    //allowCampaignRules: true,
    //showRtsAIRule: false,
    //allowSectorInvasion: false,
    orbitRadius: 10,
    orbitTime: 60 * 60,
    //startSector: 1
});
exports.NTsatellite = NTsatellite;
//statParent = NT

//const ATD = require("base/ATD");
//ATD.AddPlanetToDatabaseWithoutSectors(NT, NTsatellite);
