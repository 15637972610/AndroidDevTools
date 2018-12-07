package com.dkp.shopping.utils;

import com.trello.rxlifecycle.components.RxFragment;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * Created by Dchan on 2017/3/13.
 * 异步处理工具
 */

public class RxAsyncHelper<T> {

    Observable<T> observable;

    public RxAsyncHelper(final T t){
        observable = Observable.create(new OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                subscriber.onNext(t);
            }
        });
    }

    public RxAsyncHelper(Iterable<? extends T> iterable){
        observable = Observable.from(iterable);
//        observable = Observable.create(new OnSubscribe<T>() {
//            @Override
//            public void call(Subscriber<? super T> subscriber) {
//                subscriber.onNext(t);
//            }
//        });
    }

    public RxAsyncHelper(Observable<T> observable){
        this.observable = observable;
    }

    public Subscription subscribe(){
        return this.observable.observeOn(AndroidSchedulers.mainThread()).subscribe();
    }

    public Subscription subscribe(Action1<? super T> action){
        return this.observable.observeOn(AndroidSchedulers.mainThread()).subscribe(action);
    }

    public Subscription subscribe(Subscriber<? super T> subscriber){
        return this.observable.observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);
    }

    /**
     * 运行在子线程,线程池随着需要而增加线程
     * @param func
     * @param <R>
     * @return
     */
    public <R>RxAsyncHelper<R> runInThread(Func1<? super T, ? extends R> func){
        return  new RxAsyncHelper(this.observable.observeOn(Schedulers.from(CacheThreadPool.getInstance()
                .getCacheThreadExecutor())).map(func));
    }

    /**
     * 运行在子线程,线程最大数跟CPU数量一致
     * @param func
     * @param <R>
     * @return
     */
    public <R>RxAsyncHelper<R> runInThreadForCumpute(Func1<? super T, ? extends R> func){
        return  new RxAsyncHelper(this.observable.observeOn(Schedulers.computation()).map(func));
    }

    /**
     * 固定的单一线程
     * @param func
     * @param <R>
     * @return
     */
    public <R>RxAsyncHelper<R> runInSingleFixThread(Func1<? super T, ? extends R> func){
        return new RxAsyncHelper(this.observable.observeOn(Schedulers.from(SingleThreadPool.getInstance()
                .getSingleThreadExecutor())).map(func));
    }

    /**
     * 运行在主线程
     * @param func
     * @param <R>
     * @return
     */
    public <R>RxAsyncHelper<R> runOnMainThread(Func1<? super T, ? extends R> func){
        return new RxAsyncHelper(this.observable.observeOn(AndroidSchedulers.mainThread()).map(func));
    }

    /**
     * 过滤多个连续事件，只取最后一个
     * @param <T>
     * @return
     */
    public <T>RxAsyncHelper<T> debound(){
        return debound(600);
    }

    public <T>RxAsyncHelper<T> debound(long timeOut){
        return new RxAsyncHelper(this.observable.debounce(timeOut , TimeUnit.MILLISECONDS));
    }

    /**
     * 绑定activity的生命周期函数，activity结束时observable也释放
     * @param rxActivity
     * @return
     */
    public RxAsyncHelper bindToLifeStyle(RxAppCompatActivity rxActivity){
        this.observable.compose(rxActivity.bindToLifecycle());
        return this;
    }

    /**
     * 绑定fragment的生命周期函数，fragment结束时observable也释放
     * @param rxFragment
     * @return
     */
    public RxAsyncHelper bindToLifeStyle(RxFragment rxFragment){
        this.observable.compose(rxFragment.bindToLifecycle());
        return this;
    }

    public static ExecutorService getSingleThreadPool(){
        return SingleThreadPool.getInstance().getSingleThreadExecutor();
    }

    public static ExecutorService getCacheThreadPool(){
        return CacheThreadPool.getInstance().getCacheThreadExecutor();
    }

    private static class SingleThreadPool{
        private ExecutorService mExecutorService;
        private static SingleThreadPool singleThreadPool;

        private SingleThreadPool() {
            mExecutorService = Executors.newFixedThreadPool(1);
        }

        public static SingleThreadPool getInstance(){
            if(singleThreadPool == null){
                synchronized (SingleThreadPool.class){
                    if(singleThreadPool == null){
                        singleThreadPool = new SingleThreadPool();
                    }
                }
            }
            return singleThreadPool;
        }

        public ExecutorService getSingleThreadExecutor(){
            return mExecutorService;
        }
    }

    private static class CacheThreadPool{
        private ExecutorService mExecutorService;
        private static CacheThreadPool cacheThreadPool;

        private CacheThreadPool() {
            mExecutorService = Executors.newCachedThreadPool();
        }

        public static CacheThreadPool getInstance(){
            if(cacheThreadPool == null){
                synchronized (SingleThreadPool.class){
                    if(cacheThreadPool == null){
                        cacheThreadPool = new CacheThreadPool();
                    }
                }
            }
            return cacheThreadPool;
        }

        public ExecutorService getCacheThreadExecutor(){
            return mExecutorService;
        }
    }
}
