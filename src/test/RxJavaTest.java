package test;

import java.net.URI;

import test.ApiWrapper.LCallBack;
import test.CatsHelper.CutestCatCallback;

public class RxJavaTest {
	public static void main(String[] args){
//		new CatsHelper().saveTheCutestCat("fdaf", new CutestCatCallback(){
//			public void onCutestCatSaved(URI uri){
//				System.out.println("query suc in CLIENT: "+uri.toString());
//			}
//			public void onQueryFailed(Exception e){
//				e.printStackTrace();
//			}
//		});
		new CatsHelper().saveTheCutestCat2("fdfd")
		.start(new LCallBack<URI, Exception>() {
			
			public void onResult(URI result) {
				// TODO Auto-generated method stub
				System.out.println("query suc in CLIENT: "+result.toString());
			}
			
			public void onError(Exception result) {
				// TODO Auto-generated method stub
				
			}
		});
	}
}
