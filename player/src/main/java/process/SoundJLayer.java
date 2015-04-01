package process;

import javazoom.jl.player.advanced.*;
import listener.CurrentMusic;


public class SoundJLayer extends PlaybackListener implements Runnable
{
    private String filePath;
    private AdvancedPlayer player;
    private Thread playerThread;
	private int pausedOnFrame = 0;
	private CurrentMusic cm;

    public SoundJLayer(CurrentMusic cm)
    {
        this.cm = cm;
    }

    
    
    public void stop()
    {
    	player.stop();
    	
    }
    public void play(String path)
    {
        try
        {
            String urlAsString = 
                "file:///" 
                
                + "/" 
                + path;
//            String urlAsString = "/Users/Lam/Downloads/City.mp3";
            

            this.player = new AdvancedPlayer
            (
                new java.net.URL(urlAsString).openStream(),
                javazoom.jl.player.FactoryRegistry.systemRegistry().createAudioDevice()
            );

            this.player.setPlayBackListener(this);

            this.playerThread = new Thread(this, "AudioPlayerThread");

            this.playerThread.start();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    // PlaybackListener members

    public void playbackStarted(PlaybackEvent playbackEvent)
    {
        System.out.println("playbackStarted");
    }

    public void playbackFinished(PlaybackEvent playbackEvent)
    {
    	if (player.isComplete()){
        System.out.println("playbackEnded");
        cm.notifySongFinished();
        
        System.out.println("Chanson terminée");
    	}
    	else
    	{
    		pausedOnFrame = playbackEvent.getFrame();
            System.out.println("Chanson En cours mise en pause");
    	}
//        playbackEvent.getSource().
        
    }    

    
    // Runnable members

    public void run()
    {
        try
        {
        	if (pausedOnFrame == 0){
            this.player.play();
        	}else
        	{
        		player.play(pausedOnFrame, Integer.MAX_VALUE);
        	}
        }
        catch (javazoom.jl.decoder.JavaLayerException ex)
        {
            ex.printStackTrace();
        }

    }
}
