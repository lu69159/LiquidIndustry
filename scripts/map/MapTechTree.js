const lib = require("base/lib");
const LIblockslib = require("LI/LIblockslib");
const SD = require("blocks/cores/闪电核心");
const maps = require("map/maps");

//Nepture.techTree = TechTree.nodeRoot("微型核心基座", LIlib.微型核心基座, () => {});

Events.on(ContentInitEvent, cons(e => {
    
    lib.addToResearch(maps["测试区"], {
        parent: LIblockslib.微型核心基座.name,
        objectives: Seq.with(
		new Objectives.Research(LIblockslib.微型核心基座)
	    )
    });

    lib.addToResearch(maps["狭长冰谷"], {
        parent: "测试区",
        objectives: Seq.with(
            new Objectives.SectorComplete(maps["测试区"])
        )
    });

    lib.addToResearch(maps["极冰溶洞"], {
        parent: "狭长冰谷",
        objectives: Seq.with(
            new Objectives.SectorComplete(maps["狭长冰谷"]),
            new Objectives.Research(LIblockslib.冰冷废液分离机)
        )
    });

    lib.addToResearch(maps["永夜荒地"], {
        parent: "极冰溶洞",
        objectives: Seq.with(
            new Objectives.SectorComplete(maps["极冰溶洞"]),
            new Objectives.Research(SD.SDcore),
            new Objectives.Research(LIblockslib.电裂),
        )
    });

    lib.addToResearch(maps["极光壁垒"], {
        parent: "永夜荒地",
        objectives: Seq.with(
            new Objectives.SectorComplete(maps["永夜荒地"]),
            new Objectives.Research(LIblockslib.三级精华转化器),
            new Objectives.Research(LIblockslib.解冻投影),
            new Objectives.Research(LIblockslib.状态穹顶)
        )
    });

    lib.addToResearch(maps["暴雪前哨"], {
        parent: "极光壁垒",
        objectives: Seq.with(
            new Objectives.SectorComplete(maps["极光壁垒"]),
            new Objectives.Research(LIblockslib.废液混合器),
            new Objectives.Research(LIblockslib.预警雷达)
        )
    });

    lib.addToResearch(maps["教程：获取钛"], {
        parent: "测试区",
        objectives: Seq.with(
            new Objectives.SectorComplete(maps["测试区"])
        )
    });

    lib.addToResearch(maps["教程：获取钍"], {
        parent: "教程：获取钛",
        objectives: Seq.with(
            new Objectives.SectorComplete(maps["狭长冰谷"])
        )
    });

    lib.addToResearch(maps["蛇行道"], {
        parent: "永夜荒地",
        objectives: Seq.with(
            new Objectives.SectorComplete(maps["永夜荒地"])
        )
    });

    lib.addToResearch(maps["地火"], {
        parent: "蛇行道",
        objectives: Seq.with(
            new Objectives.SectorComplete(maps["蛇行道"])
        )
    });

}));