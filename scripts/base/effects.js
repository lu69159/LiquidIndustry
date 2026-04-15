    const sparkColor = Color.valueOf("00FFFF");
    const sparkColorBack = Color.valueOf("8EFFEA");
    const sparkBomb = new Effect(15, 100, e => {
        Draw.color(sparkColorBack);
        Lines.stroke(e.fout() * 4);
        Lines.circle(e.x, e.y, 4 + e.finpow() * 20);
        for(let i = 0; i < 4; i++){
            Drawf.tri(e.x, e.y, 6, 80 * e.fout(), i*90 + 45);
        }

        Draw.color();
        for(let i = 0; i < 4; i++){
            Drawf.tri(e.x, e.y, 3, 30 * e.fout(), i*90 + 45);
        }

        Drawf.light(e.x, e.y, 150, sparkColorBack, 0.9 * e.fout());
    });
    exports.sparkBomb = sparkBomb;

    const sparkTrail = new Effect(30, e => {
        for(let i = 0; i < 2; i++){
            Draw.color(i == 0 ? sparkColorBack : sparkColor);

            let m = i == 0 ? 1 : 0.5;

            let rot = e.rotation + 180;
            let w = 15 * e.fout() * m;
            Drawf.tri(e.x, e.y, w, (30 + Mathf.randomSeedRange(e.id, 15)) * m, rot);
            Drawf.tri(e.x, e.y, w, 10 * m, rot + 180);
        }

        Drawf.light(e.x, e.y, 60, sparkColorBack, 0.6 * e.fout());
    });
    exports.sparkTrail = sparkTrail;

    const sparkShoot = new Effect(24, e => {
        e.scaled(10, b => {
            Draw.color(Color.white, sparkColorBack, b.fin());
            Lines.stroke(b.fout() * 3 + 0.2);
            Lines.circle(b.x, b.y, b.fin() * 50);
        });

        Draw.color(sparkColorBack);

        Mathf.signs.forEach(i => {
            Drawf.tri(e.x, e.y, 13 * e.fout(), 85, e.rotation + 90 * i);
            Drawf.tri(e.x, e.y, 13 * e.fout(), 50, e.rotation + 20 * i);
        });

        Drawf.light(e.x, e.y, 180, sparkColorBack, 0.9 * e.fout());
    });
    exports.sparkShoot = sparkShoot;

    const sparkHit = new Effect(20, 200, e => {
        Draw.color(sparkColorBack);

        for(let i = 0; i < 2; i++){
            Draw.color(i == 0 ? sparkColorBack : sparkColor);

            let m = i == 0 ? 1 : 0.5;

            for(let j = 0; j < 5; j++){
                let rot = e.rotation + Mathf.randomSeedRange(e.id + j, 50);
                let w = 23 * e.fout() * m;
                Drawf.tri(e.x, e.y, w, (80 + Mathf.randomSeedRange(e.id + j, 40)) * m, rot);
                Drawf.tri(e.x, e.y, w, 20 * m, rot + 180);
            }
        }

        e.scaled(10, c => {
            Draw.color(sparkColor);
            Lines.stroke(c.fout() * 2 + 0.2);
            Lines.circle(e.x, e.y, c.fin() * 30);
        });

        e.scaled(12, c => {
            Draw.color(sparkColorBack);
            Angles.randLenVectors(e.id, 25, 5 + e.fin() * 80, e.rotation, 60, (x, y) => {
                Fill.square(e.x + x, e.y + y, c.fout() * 3, 45);
            });
        });
    });
    exports.sparkHit = sparkHit;

    const surgeAlloyShoot = new Effect(12, e => {
        Draw.color(Color.white, Color.valueOf("F3E979"), e.fin());
        Lines.stroke(e.fout() * 1.2 + 0.5);

        Angles.randLenVectors(e.id, 7, 25 * e.finpow(), e.rotation, 50, (x, y) => {
            Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fin() * 5 + 2);
        });
    });
    exports.surgeAlloyShoot = surgeAlloyShoot;

    const colorHitBullet = new Effect(14, e => {
        Draw.color(e.color);

        e.scaled(7, s => {
            Lines.stroke(0.5 + s.fout());
            Lines.circle(e.x, e.y, s.fin() * 5);
        });

        Lines.stroke(0.5 + e.fout());

        Angles.randLenVectors(e.id, 5, e.fin() * 15, (x, y) => {
            var ang = Mathf.angle(x, y);
            Lines.lineAngle(e.x + x, e.y + y, ang, e.fout() * 3 + 1);
        });

        Drawf.light(e.x, e.y, 20, e.color, 0.6 * e.fout());
    });
    exports.colorHitBullet = colorHitBullet;

    const blessApply = new Effect(120, e => {
        let unit = e.data, unittype = unit.type, region = unittype.fullIcon;
        Draw.color(e.color);
        Draw.alpha(e.fout() * 1);
        Draw.rect(region, unit.x, unit.y, region.width * 0.8, region.height * 0.8, unit.rotation - 90);
    });
    exports.blessApply = blessApply;
