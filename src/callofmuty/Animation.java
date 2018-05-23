package callofmuty;

public class Animation {
    double totalTime, switchTime;
    int[] numberOfImagesAnimation;
    int currentImage, firstImage, animationImageLength, row, type; // currentImage starts at 1
    boolean isIdle;
    int skinId, numberOfSkins;
    
    public static final int PLAYER = 0, GUN = 1, STILL_IMAGE = 2;
    private static final int GUN_SWITCH_TIME = 100, GUN_NUMBER_OF_IMAGES_X = 5, GUN_NUMBER_OF_IMAGES_Y = 6, GUN_ANIMATION_IMAGE_LENTH = 5, GUN_FIRST_IMAGE = 1;
    private static final int PLAYER_SWITCH_TIME = 115, PLAYER_NUMBER_OF_IMAGES_X = 8, PLAYER_NUMBER_OF_IMAGES_Y = 20, PLAYER_ANIMATION_IMAGE_LENTH = 8, PLAYER_FIRST_IMAGE = 1;
            
    public Animation(int type){
        skinId = 1;
        numberOfSkins = 4;
        this.type = type;
        row = 1;
        this.totalTime = 0;
        numberOfImagesAnimation = new int[2];
        isIdle = false;  
        
        switch(type)
        {
            case GUN:
                this.switchTime = GUN_SWITCH_TIME;
                this.numberOfImagesAnimation[0] = GUN_NUMBER_OF_IMAGES_X;
                this.numberOfImagesAnimation[1] = GUN_NUMBER_OF_IMAGES_Y;
                this.currentImage = GUN_FIRST_IMAGE;
                this.firstImage = GUN_FIRST_IMAGE;
                this.animationImageLength = GUN_ANIMATION_IMAGE_LENTH;
                break;
            case PLAYER:
                this.switchTime = PLAYER_SWITCH_TIME;
                this.numberOfImagesAnimation[0] = PLAYER_NUMBER_OF_IMAGES_X;
                this.numberOfImagesAnimation[1] = PLAYER_NUMBER_OF_IMAGES_Y;
                this.currentImage = PLAYER_FIRST_IMAGE;
                this.firstImage = PLAYER_FIRST_IMAGE;
                this.animationImageLength = PLAYER_ANIMATION_IMAGE_LENTH;
                break;
        }
    }
    
    public void update(double dT)
    {
        totalTime += dT;
        if (totalTime >= switchTime)
        {
            totalTime -= switchTime;
            if (currentImage >= firstImage + animationImageLength-1)
            {
                currentImage = firstImage;
            }
            else
            {
                currentImage +=1;
            }    
        }
    }

    public double getSwitchTime() {
        return switchTime;
    }

    public int getAnimationImageLength() {
        return animationImageLength;
    }
    
    public int getNumberOfImagesX()
    {
        return numberOfImagesAnimation[0];
    }
   
    public int getNumberOfImagesY()
    {
        return numberOfImagesAnimation[1];
    }
    
    public int getCurrentImage()
    {
        int image = 0;
        switch(type)
        {
            case PLAYER:
            case GUN:
                image = ((row-1)*numberOfImagesAnimation[0] + currentImage - 1);
                break;
                
            case STILL_IMAGE :
                image = (row-1) * numberOfImagesAnimation[0]; // Takes the first image of the a given row
                break;
        }
        return image;
    }
    
    public void setFirstImage(int firstImage)
    {
        this.firstImage = firstImage;
    }
    
    public void setRow(int row)
    {
        this.row = row;
    }
    
    public void setIsIdle(boolean isIdle, int direction)
    {
        this.isIdle = isIdle;
        if(isIdle)
        {
            switch(direction)
            {
                case 1:
                    row = (skinId - 1) * 4 + 4;
                    break;
                case 2:
                    row = (skinId - 1) * 4 + 3;
            }
        }
        else
        {
            switch(direction)
            {
                case 1:
                    row = (skinId - 1) * 4 + 2;
                    break;
                case 2:
                    row = (skinId - 1) * 4 + 1;
            }
        }
    }
    
    public boolean endOfAnimation()
    {
        return currentImage == animationImageLength + firstImage - 1;
    }
    
    public void setAnimation(int state)
    {
        type = state;
        currentImage ++;
    }
    
    public int getCurrentImageValue()
    {
        return currentImage;
    }
    
    public void setSkinId(int skinId)
    {
        this.skinId = skinId;
    }
    
    public void incrCurrentImage()
    {
        currentImage ++;
    }
}
