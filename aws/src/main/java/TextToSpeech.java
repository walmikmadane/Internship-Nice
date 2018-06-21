import com.amazonaws.services.polly.AmazonPolly;
import com.amazonaws.services.polly.AmazonPollyClientBuilder;
import com.amazonaws.services.polly.model.SynthesizeSpeechRequest;
import com.amazonaws.services.polly.model.SynthesizeSpeechResult;
import sun.audio.*;

import java.io.InputStream;

public class TextToSpeech
{
    public static void main(String[] args) throws Exception
    {
        AmazonPolly client = AmazonPollyClientBuilder.standard().build();
        SynthesizeSpeechRequest request = new SynthesizeSpeechRequest().withOutputFormat("mp3").withSampleRate("8000")
                .withText("All Gaul is divided into three parts").withTextType("text").withVoiceId("Joanna");
        SynthesizeSpeechResult response = client.synthesizeSpeech(request);
        InputStream inputStream=response.getAudioStream();
        // create an audiostream from the inputstream
        AudioStream audioStream = new AudioStream(inputStream);

        // play the audio clip with the audioplayer class
        AudioPlayer.player.start(audioStream);

    }
}
