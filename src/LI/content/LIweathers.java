package LI.content;

import arc.graphics.Color;
import mindustry.content.StatusEffects;
import mindustry.type.Weather;
import mindustry.type.weather.*;
import mindustry.world.meta.Attribute;

public class LIweathers {
    public static Weather JH,BX;

    public static void load(){
        JH = new ParticleWeather("极寒"){{
            particleRegion = "circle-small";
            color = Color.valueOf("C0CEFF");
            sizeMin = 5f;
            sizeMax = 12f;
            minAlpha = 0.2f;
            maxAlpha = 0.99f;
            opacityMultiplier = 0.8f;
            baseSpeed = 1f;
            xspeed = 7.5f;
            yspeed = -20f;
            density = 7000f;
            duration = statusDuration = 60f;
            status = StatusEffects.freezing;
        }};
        BX = new ParticleWeather("暴雪"){{
            drawNoise = true;
            drawParticles = false;
            noiseLayers = 3;
            noiseLayerSclM = 0.8f;
            noiseLayerAlphaM = 0.5f;
            noiseLayerSpeedM = 2f;
            noisePath = "clouds";
            noiseScale = 1100f;
            noiseColor = Color.valueOf("FFFFFF70");
            color = Color.valueOf("FFFFFFC0");
            opacityMultiplier = 0.6f;
            baseSpeed = 0.05f;
            xspeed = 12f;
            yspeed = -30f;
            sound = LIaudio.wind;
            soundVol = 0.75f;
            attrs.set(Attribute.light, -1f);
            attrs.set(Attribute.water, 1f);
            attrs.set(LIattr.cryofluid, 0.5f);
            status = LIstatus.XM;
            statusDuration = 120f;
            duration = 36000f;
        }};
    }
}
