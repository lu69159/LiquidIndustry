package LI.type.blocks.production;

import arc.graphics.g2d.*;
import mindustry.graphics.*;
import mindustry.world.blocks.production.*;

public class CustomDrawCrafter extends GenericCrafter {
    public CustomDrawCrafter(String name) {
        super(name);
    }

    public void customDraw(CustomDrawCrafterBuild c){
        //在实例化时自定义重写
    }

    public class CustomDrawCrafterBuild extends GenericCrafterBuild{
        @Override
        public void draw() {
            super.draw();
            customDraw(this);
        }
    }
}
