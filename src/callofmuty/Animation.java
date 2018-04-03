package callofmuty;

public class Animation {
    double totalTime, switchTime;
    int[] numberOfImagesAnimation;
    int currentImage, firstImage, animationImageLength; // currentImage starts at 0
    
    public Animation(double switchTime, int numberOfImagesY, int numberOfImagesX, int animationImageLength, int firstImage)
    {
        this.totalTime = 0;
        this.switchTime = switchTime;
        numberOfImagesAnimation = new int[2];
        this.numberOfImagesAnimation[0] = numberOfImagesX;
        this.numberOfImagesAnimation[1] = numberOfImagesY;
        this.currentImage = firstImage;
        this.firstImage = firstImage;
        this.animationImageLength = animationImageLength;
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
    
    public int getCurrentImage(int row, boolean isIdle)
    {
        if (isIdle)
        {
            return (row*numberOfImagesAnimation[0] + firstImage + 1);
        }
        else
        {
            return (row*numberOfImagesAnimation[0] + currentImage);
        }
    }
    
    public void setFirstImage(int firstImage)
    {
        this.firstImage = firstImage;
    }
}
