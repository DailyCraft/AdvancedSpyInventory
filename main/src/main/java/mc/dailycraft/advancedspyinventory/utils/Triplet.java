package mc.dailycraft.advancedspyinventory.utils;

import io.netty.channel.ChannelPipeline;

import java.util.function.Function;

public class Triplet<T> {
    public final ChannelPipeline pipeline;
    public final Class<T> packet;
    public final Function<T, String> line;

    public Triplet(ChannelPipeline pipeline, Class<T> packet, Function<T, String> line) {
        this.pipeline = pipeline;
        this.packet = packet;
        this.line = line;
    }
}