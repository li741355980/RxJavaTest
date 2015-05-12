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
	 * ���÷��ͽ���ӳ��
	 */
	
	public AsyncJob<URI,Exception> saveTheCutestCat4(String query) {
		
		/*
		 * ��ӳ��
		 */
		final AsyncJob<List<Cat>,Exception> catsListAsyncJob = api1ApiWrapper.rxQueryCats(query);
		final AsyncJob<Cat,Exception> cutestCatAsyncJob = catsListAsyncJob.map(new Func<List<Cat>, Cat>() {
			public Cat call(List<Cat> cats) {
				return findCutest(cats);
			}
		});
		/*
		 * �߼�ӳ��
		 */
		AsyncJob<URI,Exception> storedUriAsyncJob = cutestCatAsyncJob.flatMap(new Func<Cat, AsyncJob<URI,Exception>>() {
            public AsyncJob<URI,Exception> call(Cat cat) {
                return api1ApiWrapper.rxStore(cat);
            }
        });
        return storedUriAsyncJob;
    }
/*
 * ��2�еĲ���ֿ�д
 * 1 ��ȡ����è�첽��ȡ��������AsyncJob
 * 2 ��ȡ��Ư��è�Ĺ�������
 * 3 �洢���첽����
 * 4 ���ش洢���첽����
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
	 * �򵥴���˫���첽�ص�
	 * 1.ʵ��ͳһ�Ļص��ӿ�LCallBack <T,K> TΪ�ɹ��ص��Ĳ�����KΪʧ�ܵ�
	 * 2.��װApi��ΪApiWrapper����ص�����ͳһʹ��LCallBack <T,K>����������������ģʽ��
	 * 3.�첽����������Щ��������Ϊ���� 
	 *   3.1.�Իص�Ϊ��һ����
	 *   3.2.ִ�гɹ���ʧ�ܶ�Ӧ�ص��ص������ĺ���
	 * 4. ����������������е��첽����ʹ�ã������ͽ���AsyncJob��������Ͷ��ص�������ִ�����ǶԻص������
	 *       P.S. ��֮ΪAsyncTask������һ��
	 * 5.�ı�ApiWrapper�������첽�ӿ�ʹ���� ����    AsyncJob��ʵ�������
	 * 
	 * -->����AsyncJob���� ʵ�ֵ���ΪΪ�� ��ȡ����è��AsyncJob����ִ�У�Ͷ��ص����󣩣���ȡ��Ư����è����ȡ�洢è��AsyncJob����ִ�У�Ͷ��ص�����
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
 * �򵥴���˫���첽�ص�
 * 1.�������첽��ȡList<cat> ����Api�ĺ��� queryCats  �ص�����Api.CutestCatCallback
 * 2.���1�ɹ���������ص��ĳɹ�������ִ�У�������Ư����cat���������첽�洢�����أ��ص�����Api.StoreCallback
 * 3.�ͻ��˵���ʱͶ��ص�����CutestCatCallback�����1 2 �ɹ�����2�ص��ĳɹ������ص�Ͷ��Ļص��ĳɹ�����
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
