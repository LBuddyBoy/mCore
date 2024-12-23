package dev.minechase.core.api.util;

import dev.lbuddyboy.commons.api.util.Callable;
import lombok.Getter;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

@Getter
public class ExceptedFuture<T> {

    private final CompletableFuture<T> future;

    public ExceptedFuture(CompletableFuture<T> future) {
        this.future = future;
    }

    public <U> CompletableFuture<U> thenApplyAsync(Function<? super T, ? extends U> fn) {
        return this.future.thenApplyAsync(fn);
    }

    public CompletableFuture<T> whenCompleteAsync(Consumer<T> consumer) {
        return this.future.whenCompleteAsync((t, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return;
            }

            consumer.accept(t);
        });
    }

    public CompletableFuture<T> whenCompleteAsyncExcept(Consumer<T> consumer, Consumer<Throwable> exception) {
        return this.future.whenCompleteAsync((t, throwable) -> {
            if (throwable != null) {
                exception.accept(throwable);
                return;
            }

            consumer.accept(t);
        });
    }

}
