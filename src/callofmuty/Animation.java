package callofmuty;

public class Animation {
    double totalTime, switchTime;
    int[] numberOfImagesAnimation;
    int currentImage, firstImage, animationImageLength, row, type; // currentImage starts at 1
    boolean isIdle;
    int skinId, numberOfSkins;
    
    public static final int PLAYER = 0, GUN = 1, STILL_IMAGE = 2;
    
    public Animation(double switchTime, int numberOfImagesX, int numberOfImagesY, int animationImageLength, int firstImage, int type)
    {
        skinId = 1;
        numberOfSkins = 3;
        this.type = type;
        row = 1;
        this.totalTime = 0;
        this.switchTime = switchTime;
        numberOfImagesAnimation = new int[2];
        this.numberOfImagesAnimation[0] = numberOfImagesX;
        this.numberOfImagesAnimation[1] = numberOfImagesY;
        this.currentImage = firstImage;
        this.firstImage = firstImage;
        this.animationImageLength = animationImageLength;
        isIdle = false;
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
                image = ((row-1)*numberOfImagesAnimation[0] + currentImage - 1);
                break;
                
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
    }
    
    public int getCurrentImageValue()
    {
        return currentImage;
    }
    
    public void setSkinId(int skinId)
    {
        this.skinId = skinId;
    }
}
