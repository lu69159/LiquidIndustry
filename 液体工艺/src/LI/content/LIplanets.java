package LI.content;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Vec3;
import arc.util.noise.Simplex;
import mindustry.content.Planets;
import mindustry.maps.planet.TantrosPlanetGenerator;
import mindustry.graphics.g3d.*;
import mindustry.type.*;
import mindustry.game.*;

public class LIplanets {
    public static Planet NT //, NTsatellite
    ;

    public static void load(){
        NT = new Planet("Nepture", Planets.sun, 1.2f, 2){{
            generator = new TantrosPlanetGenerator(){
                final Color c1 = Color.valueOf("D8F3FF"), c2 = Color.valueOf("5ECCF7");

                @Override
                public void addWeather(Sector sector, Rules rules) {
                    if(sector.preset == LImaps.ZXmap2){}
                    else if(sector.preset == LImaps.ZXmap3){
                        rules.weather.add(new Weather.WeatherEntry(LIweathers.BX){{ always = true; }});
                    }
                    else{
                        rules.weather.add(new Weather.WeatherEntry(LIweathers.JH, 60*5f, 60*30f, 60*60f, 60*180f));
                    }
                }

                @Override
                public void getColor(Vec3 position, Color out) {
                    float depth = Simplex.noise3d(2, 2.0, 0.56, 1.7, position.x, position.y, position.z);
                    out.set(c1).lerp(c2, Mathf.clamp(Mathf.round(depth, 0.25f))).toFloatBits();
                }
            };
            meshLoader = () -> new MultiMesh(
                new HexMesh(NT, 5),
                new HexSkyMesh(NT, 2, 0.15f, 0.05f, 5, Color.valueOf("D8F3FF30"), 2, 0.42f, 1f, 0.43f)
            );
            cloudMeshLoader = () -> new MultiMesh(
                new HexSkyMesh(NT, 2, 0.15f, 0.05f, 5, Color.valueOf("D8F3FF"), 2, 0.42f, 1f, 0.43f),
                new HexSkyMesh(NT, 3, 0.6f, 0.15f, 5, Color.valueOf("D8F3FF80"), 2, 0.42f, 1.2f, 0.45f)
            );
            campaignRules = new CampaignRules(){
                @Override
                public void apply(Planet planet, Rules rules) {
                    if(!rules.showSpawns) rules.showSpawns = true;
                    rules.objectiveTimerMultiplier = difficulty.waveTimeMultiplier;
                    rules.teams.get(rules.waveTeam).blockHealthMultiplier = difficulty.enemyHealthMultiplier;
                    rules.teams.get(rules.waveTeam).unitHealthMultiplier = difficulty.enemyHealthMultiplier;
                    rules.teams.get(rules.waveTeam).unitCostMultiplier = 1f / difficulty.enemySpawnMultiplier;
                    rules.teams.get(rules.waveTeam).unitBuildSpeedMultiplier = difficulty.enemySpawnMultiplier;
                }
            };
            ruleSetter = (r) -> {
                r.lighting = r.showSpawns = true;
                r.solarMultiplier = 0.05f;
            };
            startSector = 1;
            visible = accessible = alwaysUnlocked = allowCampaignRules = clearSectorOnLose = prebuildBase = true;
            showRtsAIRule = allowSectorInvasion = bloom = updateLighting = false;
            rotateTime = 60 * 10f;
            orbitTime = 60 * 360f;
            orbitRadius = 90f;
            atmosphereRadIn = 0.03f;
            atmosphereRadOut = 0.3f;
            atmosphereColor = Color.valueOf("598DA4");
            landCloudColor = Color.valueOf("D8F3FF80");
            iconColor = Color.valueOf("D8F3FF");
            launchMusic = LIaudio.NTlaunch;
        }};
    }
}