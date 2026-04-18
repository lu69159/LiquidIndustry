const NT = require("planets/Nepture").NT;

const NTsatellite = new Planet("冰卫一", NT, 1, 1.5);
NTsatellite.meshLoader = prov(() => new MultiMesh(
	new HexMesh(NTsatellite, 2),
	new HexSkyMesh(NTsatellite, 3, 0.15, 0.02, 2, Color.valueOf("C0ECFF"), 2, 0.42, 1, 0.43),
));
NTsatellite.generator = extend(TantrosPlanetGenerator, {
    getColor(position ,out){
        out.set(Color.valueOf("3C3C3C"));
    }
});
Object.assign(NTsatellite, {
    localizedName: "实验卫星",
    description: "各种实验地图试验玩法",
    accessible: false,
    bloom: false,
    hasAtmosphere: false,
    orbitRadius: 10,
    orbitTime: 60 * 60,
});
exports.NTsatellite = NTsatellite;
//statParent = NT
