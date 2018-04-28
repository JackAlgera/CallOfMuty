package callofmuty;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class SoundPlayer {

    private InputStream inputStream;

    private Player player;
    private boolean repeat, paused;
    private long pauseLocation, totalSongLength;
    private String name;
    
    public SoundPlayer(String name, boolean repeat){
        this.name = name;
        this.repeat = repeat;
    }

    public void play(){
        inputStream = this.getClass().getResourceAsStream("/resources/audio/"+name);
        try {
            totalSongLength = inputStream.available();
        } catch (IOException ex) {
            Logger.getLogger(SoundPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            player = new Player(inputStream);
        } catch (JavaLayerException ex) {
            Logger.getLogger(SoundPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        new Thread() {
            @Override
            public void run() {
                try {
                    player.play();

                    if (player.isComplete() && repeat) {
                        play();
                    }
                } catch (JavaLayerException ex) {
                    System.err.println("There was an error to play /resources/audio/"+name);
                }
            }
        }.start();
    }

    public void resume(){
        try {
            paused = false;
            inputStream = this.getClass().getResourceAsStream("/resources/audio/"+name);
            try {
                inputStream.skip(totalSongLength - pauseLocation);
            } catch (IOException ex) {
                Logger.getLogger(SoundPlayer.class.getName()).log(Level.SEVERE, null, ex);
            }
            player = new Player(inputStream);
            new Thread() {
                @Override
                public void run() {
                    try {
                        player.play();
                    } catch (JavaLayerException ex) {
                        System.err.println("::: there was an error to play " + "/resources/audio/"+name);
                    }
                }
            }.start();
        } catch (JavaLayerException ex) {
            Logger.getLogger(SoundPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void stop() {
        paused = false;
        if (null != player) {
            player.close();

            totalSongLength = 0;
            pauseLocation = 0;
        }
    }

    public void pause() {
        paused = true;
        if (null != player) {
            try {
                pauseLocation = inputStream.available();
                player.close();
            } catch (IOException ex) {
                System.out.println("Error when song is paused");
            }
        }
    }

    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }
}