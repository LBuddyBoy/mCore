package dev.minechase.core.bukkit.npc.model.packet;

import io.netty.channel.*;
import io.netty.util.concurrent.*;
import org.jetbrains.annotations.NotNull;

import java.net.SocketAddress;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class NPCChannel extends AbstractChannel {

    private final ChannelConfig config = new DefaultChannelConfig(this);
    private final EventLoop loop;

    public NPCChannel() {
        super(null);
        config.setAutoRead(true);

        this.loop = new EventLoop() {
            @Override
            public EventLoopGroup parent() {
                return null;
            }

            @Override
            public EventLoop next() {
                return null;
            }

            @Override
            public ChannelFuture register(final Channel channel) {
                return null;
            }

            @Override
            public ChannelFuture register(final ChannelPromise promise) {
                return null;
            }

            @Override
            public ChannelFuture register(final Channel channel, final ChannelPromise promise) {
                return null;
            }

            @Override
            public boolean inEventLoop() {
                return false;
            }

            @Override
            public boolean inEventLoop(final Thread thread) {
                return false;
            }

            @Override
            public <V> Promise<V> newPromise() {
                return null;
            }

            @Override
            public <V> ProgressivePromise<V> newProgressivePromise() {
                return null;
            }

            @Override
            public <V> Future<V> newSucceededFuture(final V result) {
                return null;
            }

            @Override
            public <V> Future<V> newFailedFuture(final Throwable cause) {
                return null;
            }

            @Override
            public boolean isShuttingDown() {
                return false;
            }

            @Override
            public Future<?> shutdownGracefully() {
                return null;
            }

            @Override
            public Future<?> shutdownGracefully(final long quietPeriod, final long timeout, final TimeUnit unit) {
                return null;
            }

            @Override
            public Future<?> terminationFuture() {
                return null;
            }

            @Override
            public void shutdown() {

            }

            @Override
            public List<Runnable> shutdownNow() {
                return null;
            }

            @Override
            public Iterator<EventExecutor> iterator() {
                return null;
            }

            @Override
            public Future<?> submit(final Runnable task) {
                return null;
            }

            @Override
            public <T> Future<T> submit(final Runnable task, final T result) {
                return null;
            }

            @Override
            public <T> Future<T> submit(final Callable<T> task) {
                return null;
            }

            @Override
            public ScheduledFuture<?> schedule(final Runnable command, final long delay, final TimeUnit unit) {
                return null;
            }

            @Override
            public <V> ScheduledFuture<V> schedule(final Callable<V> callable, final long delay, final TimeUnit unit) {
                return null;
            }

            @Override
            public ScheduledFuture<?> scheduleAtFixedRate(final Runnable command, final long initialDelay, final long period, final TimeUnit unit) {
                return null;
            }

            @Override
            public ScheduledFuture<?> scheduleWithFixedDelay(final Runnable command, final long initialDelay, final long delay, final TimeUnit unit) {
                return null;
            }

            @Override
            public boolean isShutdown() {
                return false;
            }

            @Override
            public boolean isTerminated() {
                return false;
            }

            @Override
            public boolean awaitTermination(final long timeout, @NotNull final TimeUnit unit) throws InterruptedException {
                return false;
            }

            @NotNull
            @Override
            public <T> List<java.util.concurrent.Future<T>> invokeAll(@NotNull final Collection<? extends Callable<T>> tasks) throws InterruptedException {
                return null;
            }

            @NotNull
            @Override
            public <T> List<java.util.concurrent.Future<T>> invokeAll(@NotNull final Collection<? extends Callable<T>> tasks, final long timeout, @NotNull final TimeUnit unit) throws InterruptedException {
                return null;
            }

            @NotNull
            @Override
            public <T> T invokeAny(@NotNull final Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
                return null;
            }

            @Override
            public <T> T invokeAny(@NotNull final Collection<? extends Callable<T>> tasks, final long timeout, @NotNull final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                return null;
            }

            @Override
            public void execute(@NotNull final Runnable command) {

            }
        };
    }

    @Override
    protected AbstractUnsafe newUnsafe() {
        return null;
    }

    @Override
    protected boolean isCompatible(final EventLoop loop) {
        return true;
    }

    @Override
    protected SocketAddress localAddress0() {
        return null;
    }

    @Override
    protected SocketAddress remoteAddress0() {
        return null;
    }

    @Override
    protected void doBind(final SocketAddress localAddress) {
    }

    @Override
    protected void doDisconnect() {

    }

    @Override
    protected void doClose() {
    }

    @Override
    protected void doBeginRead() {
    }

    @Override
    protected void doWrite(final ChannelOutboundBuffer in) {
    }

    @Override
    public ChannelConfig config() {
        return this.config;
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public ChannelMetadata metadata() {
        return new ChannelMetadata(true);
    }

    @Override
    public EventLoop eventLoop() {
        return this.loop;
    }
}