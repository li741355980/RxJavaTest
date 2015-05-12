package test;

import java.net.URI;
import java.util.List;

import test.Api.CatsQueryCallback;

public class ApiWrapper {
	public interface LCallBack <T,K>{
		void onResult(T result);
	    void onError(K result);
	}
	Api api=new Api();
	public void queryCats(String query,final LCallBack<List<Cat>,Exception> catsCallback){
        api.queryCats(query, new CatsQueryCallback() {
            public void onCatListReceived(List<Cat> cats) {
            	catsCallback.onResult(cats);
            }

            public void onError(Exception e) {
            	catsCallback.onError(e);
            }
        });
    }
	 public void store(Cat cat,final LCallBack<URI,Exception> uriCallback){
	        api.store(cat, new Api.StoreCallback() {
	            public void onCatStored(URI uri) {
	            	uriCallback.onResult(uri);
	            }
	            public void onStoreFailed(Exception e) {
	            	uriCallback.onError(e);
	            }
	        });
	    }
	
	 public AsyncJob<List<Cat>,Exception> rxQueryCats(final String query) {
	        return new AsyncJob<List<Cat>,Exception>() {
	            @Override
	            public void start(final LCallBack<List<Cat>,Exception> catsCallback) {
	                api.queryCats(query, new Api.CatsQueryCallback() {
	                    public void onCatListReceived(List<Cat> cats) {
	                        catsCallback.onResult(cats);
	                    }
	                    public void onError(Exception e) {
	                        catsCallback.onError(e);
	                    }
	                });
	            }
	        };
	    }

	    public AsyncJob<URI,Exception> rxStore(final Cat cat) {
	        return new AsyncJob<URI,Exception>() {
	            public void start(final LCallBack<URI,Exception> uriCallback) {
	                api.store(cat, new Api.StoreCallback() {
	                    public void onCatStored(URI uri) {
	                        uriCallback.onResult(uri);
	                    }
	                    public void onStoreFailed(Exception e) {
	                        uriCallback.onError(e);
	                    }
	                });
	            }

	        };
	    }
}
