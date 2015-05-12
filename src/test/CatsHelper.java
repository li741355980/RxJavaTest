package test;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import test.Api.CatsQueryCallback;
import test.Api.StoreCallback;
import test.ApiWrapper.LCallBack;
import test.AsyncJob.Func;

public class CatsHelper {

	public interface CutestCatCallback {
		void onCutestCatSaved(URI uri);
		void onQueryFailed(Exception e);
	}

	Api api=new Api();
	ApiWrapper api1ApiWrapper=new ApiWrapper();

	
	
//	  public AsyncJob<URI,Exception> saveTheCutestCat5( String query) {
//	        AsyncJob<List<Cat>,Exception> catsListAsyncJob = api1ApiWrapper.rxQueryCats(query);
//	        AsyncJob<Cat,Exception> cutestCatAsyncJob = catsListAsyncJob.map(cats->findCutest(cats));
//	        AsyncJob<URI,Exception> storedUriAsyncJob = cutestCatAsyncJob.flatMap(cat->apiWrapper.store(cat));
//	        return storedUriAsyncJob;
//	    }

	/*
	 * 利用泛型进行映射
	 */
	
	public AsyncJob<URI,Exception> saveTheCutestCat4(String query) {
		
		/*
		 * 简单映射
		 */
		final AsyncJob<List<Cat>,Exception> catsListAsyncJob = api1ApiWrapper.rxQueryCats(query);
		final AsyncJob<Cat,Exception> cutestCatAsyncJob = catsListAsyncJob.map(new Func<List<Cat>, Cat>() {
			public Cat call(List<Cat> cats) {
				return findCutest(cats);
			}
		});
		/*
		 * 高级映射
		 */
		AsyncJob<URI,Exception> storedUriAsyncJob = cutestCatAsyncJob.flatMap(new Func<Cat, AsyncJob<URI,Exception>>() {
            public AsyncJob<URI,Exception> call(Cat cat) {
                return api1ApiWrapper.rxStore(cat);
            }
        });
        return storedUriAsyncJob;
    }
/*
 * 将2中的步骤分开写
 * 1 获取网络猫异步获取工作对象AsyncJob
 * 2 获取最漂亮猫的工作对象
 * 3 存储的异步对象
 * 4 返回存储的异步对象
 */
	public AsyncJob<URI,Exception> saveTheCutestCat3(String query) {

		final AsyncJob<List<Cat>,Exception> catsListAsyncJob = api1ApiWrapper.rxQueryCats(query);

		final AsyncJob<Cat,Exception> cutestCatAsyncJob = new AsyncJob<Cat,Exception>() {
			@Override
			public void start(final LCallBack<Cat,Exception> callback) {
				catsListAsyncJob.start(new LCallBack<List<Cat>,Exception>() {
					public void onResult(List<Cat> result) {
						callback.onResult(findCutest(result));
					}
					public void onError(Exception e) {
						callback.onError(e);
					}
				});
			}
		};

		final AsyncJob<URI,Exception> storedUriAsyncJob = new AsyncJob<URI,Exception>() {
			@Override
			public void start(final LCallBack<URI,Exception> cutestCatCallback) {
				cutestCatAsyncJob.start(new LCallBack<Cat,Exception>() {
					public void onResult(Cat cutest) {
						api1ApiWrapper.rxStore(cutest)
						.start(new LCallBack<URI,Exception>() {
							public void onResult(URI result) {
								cutestCatCallback.onResult(result);
							}
							public void onError(Exception e) {
								cutestCatCallback.onError(e);
							}
						});
					}
					public void onError(Exception e) {
						cutestCatCallback.onError(e);
					}
				});
			}
		};
		return storedUriAsyncJob;
	}
	/*
	 * 简单处理双层异步回调
	 * 1.实现统一的回调接口LCallBack <T,K> T为成功回调的参数，K为失败的
	 * 2.封装Api类为ApiWrapper让其回调函数统一使用LCallBack <T,K>。。。。（适配器模式）
	 * 3.异步操作具有以些常见的行为，即 
	 *   3.1.以回调为单一参数
	 *   3.2.执行成功，失败对应回调回调函数的函数
	 * 4. 定义这样的类给所有的异步操作使用，这个类就叫它AsyncJob。。。。投入回调，具体执行则是对回调的填充
	 *       P.S. 称之为AsyncTask更合适一点
	 * 5.改变ApiWrapper的两个异步接口使他们 返回    AsyncJob的实现类对象
	 * 
	 * -->返回AsyncJob对象， 实现的行为为： 获取网络猫的AsyncJob对象，执行（投入回调对象），获取最漂亮的猫，获取存储猫的AsyncJob对象，执行（投入回调对象）
	 */
	public AsyncJob<URI, Exception> saveTheCutestCat2(final String query){
		return new AsyncJob<URI, Exception>() {
			@Override
			public void start(final LCallBack<URI, Exception> cutestCatCallback) {
				api1ApiWrapper.rxQueryCats(query)
				.start(new LCallBack<List<Cat>, Exception>() {
					public void onResult(List<Cat> cats) {
						System.out.println("query suc in CatsQueryCallback get cats : "+cats.size());

						Cat cutest = findCutest(cats);
						api1ApiWrapper.rxStore(cutest)
						.start(new LCallBack<URI, Exception>() {
							public void onResult(URI result) {
								System.out.println("rxStore LCallBack onResult: "+result.toString());
								cutestCatCallback.onResult(result);
							}
							public void onError(Exception e) {
								cutestCatCallback.onError(e);
							}
						});
					}
					public void onError(Exception e) {
						cutestCatCallback.onError(e);
					}
				});
			}
		};
	}


	public void saveTheCutestCat1(String query,final  LCallBack<URI, Exception> cBack){
		api1ApiWrapper.queryCats(query, new LCallBack<List<Cat>,Exception>() {
			public void onResult(List<Cat> cats) {
				Cat cutest = findCutest(cats);
				api1ApiWrapper.store(cutest, cBack);
			}
			public void onError(Exception e) {
				cBack.onError(e);
			}
		});
	}
/*
 * 简单处理双层异步回调
 * 1.从网络异步获取List<cat> 调用Api的函数 queryCats  回调函数Api.CutestCatCallback
 * 2.如果1成功，则在其回调的成功函数中执行，查找最漂亮的cat，并将其异步存储到本地，回调函数Api.StoreCallback
 * 3.客户端调用时投入回调函数CutestCatCallback，如果1 2 成功则在2回调的成功函数回调投入的回调的成功函数
 */
	public void saveTheCutestCat(String query,final CutestCatCallback cutestCatCallback){
		api.queryCats(query, new CatsQueryCallback() {
			public void onCatListReceived(List<Cat> cats) {
				Cat cutest = findCutest(cats);
				System.out.println("query suc in CatsQueryCallback get cats : "+cats.size());
				api.store(cutest, new StoreCallback() {

					public void onStoreFailed(Exception e) {
						// TODO Auto-generated method stub
						cutestCatCallback.onQueryFailed(e);
						e.printStackTrace();
					}
					public void onCatStored(URI uri) {
						// TODO Auto-generated method stub
						System.out.println("query suc in StoreCallback: "+uri.toString());
						cutestCatCallback.onCutestCatSaved(uri);
					}
				});

			}
			public void onError(Exception e) {
				cutestCatCallback.onQueryFailed(e);
			}
		});
	}

	private Cat findCutest(List<Cat> cats) {
		return maxBeauttyCat(cats);
	}
	private Cat maxBeauttyCat(List<Cat> cats){
		Cat maxCat=cats.get(0);
		for (Cat cat : cats) {
			if(maxCat.cuteness < cat.cuteness){
				maxCat=cat;
			}
		}
		return maxCat;
	}
}
