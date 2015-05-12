package test;

import test.ApiWrapper.LCallBack;

public abstract class AsyncJob<T,K> {
	public abstract void start(LCallBack<T,K> callback);
	
	
	
	public interface Func<T, R> {
	    R call(T t);
	}
	public <R> AsyncJob<R,K> map(final Func<T, R> func){
        final AsyncJob<T,K> source = this;
        return new AsyncJob<R,K>() {
            @Override
            public void start(final LCallBack<R,K> callback) {
                source.start(new LCallBack<T,K>() {
                    public void onResult(T result) {
                        R mapped = func.call(result);
                        callback.onResult(mapped);
                    }
                    public void onError(K e) {
                        callback.onError(e);
                    }
                });
            }
        };
    }
	
	public <R> AsyncJob<R,K> flatMap(final Func<T, AsyncJob<R,K>> func){
        final AsyncJob<T,K> source = this;
        return new AsyncJob<R,K>() {
            public void start(final LCallBack<R,K> callback) {
                source.start(new LCallBack<T,K>() {
                    public void onResult(T result) {
                        AsyncJob<R,K> mapped = func.call(result);
                        mapped.start(new LCallBack<R,K>() {
                            public void onResult(R result) {
                            	callback.onResult(result);
                            }
                            public void onError(K e) {
                            	callback.onError(e);
                            }
                        });
                    }
                    public void onError(K e) {
                        callback.onError(e);
                    }
                });
            }
        };
    }
}
