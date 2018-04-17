package callofmuty;

public class Animation {
    double totalTime, switchTime;
    int[] numberOfImagesAnimation;
    int currentImage, firstImage, animationImageLength, row; // currentImage starts at 1
    boolean isIdle;
    
    public Animation(double switchTime, int numberOfImagesX, int numberOfImagesY, int animationImageLength, int firstImage)
    {
        row = 3;
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
        if (isIdle)
        {
            return ((row-1)*numberOfImagesAnimation[0] + currentImage - 1);
        }
        else
        {
            return 0;//(row*numberOfImagesAnimation[0] + firstImage + 1);
        }
    }
    
    public void setFirstImage(int firstImage)
    {
        this.firstImage = firstImage;
    }
    
    public void setRow(int row)
    {
        this.row = row;
    }
    
    public void setIsIdle(boolean isIdle)
    {
        this.isIdle = isIdle;
    }
}
