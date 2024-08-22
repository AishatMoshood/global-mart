package com.verraki.globalmart.stockmonitoring.service.serviceimpl;

import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.util.concurrent.SuccessCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class SimpleListenableFuture<T> implements ListenableFuture<T> {

    private final CompletableFuture<T> completableFuture = new CompletableFuture<>();
    private final List<ListenableFutureCallback<? super T>> callbacks = new ArrayList<>();

    @Override
    public void addCallback(ListenableFutureCallback<? super T> callback) {
        completableFuture.whenComplete((result, throwable) -> {
            if (throwable != null) {
                callback.onFailure(throwable);
            } else {
                callback.onSuccess(result);
            }
        });
    }

    @Override
    public void addCallback(SuccessCallback<? super T> successCallback, FailureCallback failureCallback) {

    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return completableFuture.isCancelled();
    }

    @Override
    public boolean isDone() {
        return completableFuture.isDone();
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        return completableFuture.get();
    }

    @Override
    public T get(long timeout, java.util.concurrent.TimeUnit unit)
            throws InterruptedException, ExecutionException, java.util.concurrent.TimeoutException {
        return completableFuture.get(timeout, unit);
    }

    public void set(T result) {
        completableFuture.complete(result);
    }

    public void setException(Throwable ex) {
        completableFuture.completeExceptionally(ex);
    }
}
