package callofmuty;

public class Animation {
    double totalTime, switchTime;
    int numberOfImagesAnimation, currentImage; // currentImage starts at 0
    
    public Animation(double switchTime, int numberOfImagesAnimation, int firstImage)
    {
        this.totalTime = 0;
        this.switchTime = switchTime;
        this.numberOfImagesAnimation = numberOfImagesAnimation;
        this.currentImage = firstImage;
    }
    
    public void update(double dT)
    {
        totalTime += dT;
        if (totalTime >= switchTime)
        {
            totalTime -= switchTime;
            if (currentImage == numberOfImagesAnimation-1)
            {
                currentImage = 0;
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

    public int getNumberOfImagesAnimation() {
        return numberOfImagesAnimation;
    }

    public int getCurrentImage() {
        return currentImage;
    }
}
