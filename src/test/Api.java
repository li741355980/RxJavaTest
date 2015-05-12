package test;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class Api {
    public interface CatsQueryCallback {
        void onCatListReceived(List<Cat> cats);
        void onError(Exception e);
    }
    public interface StoreCallback{
        void onCatStored(URI uri);
        void onStoreFailed(Exception e);
    }

    public void queryCats(String query, CatsQueryCallback catsQueryCallback){
    	new Thread(new TestQueryCatsRunnable(query,catsQueryCallback)).start();
    }

    public void store(Cat cat,StoreCallback cb){
    	new Thread(new TestStoreCatRunnable(cat,cb)).start();
    }
    
    private class TestQueryCatsRunnable  implements Runnable{
    	CatsQueryCallback callback;
    	String string;
    	public TestQueryCatsRunnable(String query,CatsQueryCallback cb){
    		callback=cb;
    		string=query;
    	}
    	public void run() {
    		// TODO Auto-generated method stub
    		try {
				Thread.sleep(1000);
				if (true) {
					List<Cat> catlistCats=new ArrayList<Cat>();
					catlistCats.add(new Cat(0));
					catlistCats.add(new Cat(1));
					catlistCats.add(new Cat(2));
					callback.onCatListReceived(catlistCats);
				}else {
					callback.onError(new Exception("this is a error happen in query cats online"));
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
    	}
    }
    
    private class TestStoreCatRunnable  implements Runnable{
    	StoreCallback callback;
    	Cat cat;
    	TestStoreCatRunnable(Cat cat,StoreCallback cb){
    		callback=cb;
    		this.cat=cat;
    	}
    	public void run() {
    		try {
				Thread.sleep(1000);
				if (true) {
					callback.onCatStored(URI.create("https://lss.beautys/"+cat.cuteness));
				}else {
					callback.onStoreFailed(new Exception("this is a error happen in store"));
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
    	}
    }
}
