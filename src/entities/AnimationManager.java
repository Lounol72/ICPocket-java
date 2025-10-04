package entities;


import static utilz.Constants.PLAYER.ANI_SPEED;
import static utilz.HelpMethods.GetSpriteAmount;

import java.awt.image.BufferedImage;

public class AnimationManager{
    private BufferedImage[][] animations;
    private int aniTick, aniIndex;

    public AnimationManager(BufferedImage[][] animations){
        this.animations = animations;
    }


    public void updateFrame(int action, boolean loop, boolean attacking){
        aniTick++;
        if(aniTick >= ANI_SPEED){
            aniTick = 0;
            aniIndex++;
            if (aniIndex >= GetSpriteAmount(action) && !attacking){
                aniIndex = loop ? 0 : GetSpriteAmount(action) -1;
            }
        }
    }

    public BufferedImage getFrame(int action, boolean loop){
        
        return animations[action][aniIndex];
    }

    public void reset(){
        aniIndex = 0;
        aniTick = 0;
    }

    public int getAniIndex() {
        return aniIndex;
    }

    public void setAniIndex(int aniIndex) {
        this.aniIndex = aniIndex;
    }
}