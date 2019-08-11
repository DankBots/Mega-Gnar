package xyz.gnarbot.gnar.music;

import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;

import java.nio.ByteBuffer;

public class AudioPlayerSendHandler implements AudioSendHandler {
    private final AudioPlayer audioPlayer;
    private MutableAudioFrame lastFrame;

    /**
     * @param audioPlayer Audio player to wrap.
     */
    public AudioPlayerSendHandler(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
        this.lastFrame = new MutableAudioFrame();
        this.lastFrame.setFormat(StandardAudioDataFormats.DISCORD_OPUS);
        this.lastFrame.setBuffer(ByteBuffer.allocate(this.lastFrame.getFormat().maximumChunkSize()));
    }

    @Override
    public boolean canProvide() {
        return audioPlayer.provide(lastFrame);
    }

    @Override
    public ByteBuffer provide20MsAudio() {
        return ByteBuffer.wrap(lastFrame.getData());
    }

    @Override
    public boolean isOpus() {
        return true;
    }
}
