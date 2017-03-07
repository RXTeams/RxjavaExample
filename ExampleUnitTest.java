package com.ws.rxjava2;

import org.junit.Test;
import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.flowables.ConnectableFlowable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by wang.song on 17/3/6.
 */
public class ExampleUnitTest {

    private void print(Object o){
        System.out.println(o);
    }

    private void save(Object o){

    }

    private void sleep(int time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    //-------------------------创建操作-----------------------------
    /*
    1、Just操作符将某个对象转化为Observable对象，并且将其发射出去，可以使一个数字、一个字符串、数组、Iterate对象等，是一种非常快捷的创建Observable对象的方法
     */
    @Test
    public void just() throws Exception {
        Flowable.just("Hello RxJava2").subscribe(this::print);//Hello RxJava2
    }


    /*
    2、defer直到有订阅，才会创建Observable,具有延时的效果
    在使用just的时候，便创建了Observable对象，随后改变a的值，并不会改变Observable对象中的值
     */
    @Test
    public void defer() throws Exception {
        Flowable.defer(() -> Flowable.just(System.currentTimeMillis())).subscribe(this::print);
        Flowable.defer(() -> Flowable.just(System.currentTimeMillis())).subscribe(this::print);
        Flowable.defer(() -> Flowable.just(System.currentTimeMillis())).subscribe(this::print);
        Flowable.just(System.currentTimeMillis()).subscribe(this::print);
        Flowable.just(System.currentTimeMillis()).subscribe(this::print);
        Flowable.just(System.currentTimeMillis()).subscribe(this::print);
    }

    /*
    3、range(n,m)
    Range操作符根据出入的初始值n和数目m发射一系列大于等于n的m个值
    n , n+1 , n+2 , n+m-1
     */
    @Test
    public void range() throws Exception {
        Flowable.range(10 , 5).subscribe(this::print);//10 11 12 13 14
    }

    /*
    4、使用doOnNext()来调试
    使用doOnNext()去保存/缓存网络结果
     */
    @Test
    public void doOnNext() throws Exception{
        Flowable.just("Hello RxJava2").doOnNext(this::save).subscribe(this::print);//Hello RxJava2
    }

    /*
    5、使用from进行遍历
     */
    @Test
    public void from() throws Exception{
        String[] s = {"1" , "2" , "3"};
        Flowable.fromArray(s).doOnNext(this::print).subscribe(this::print);// 1 2 3
    }

    /*
    6、使用timer定时器
     */
    @Test
    public void timer() throws Exception{
        Flowable.timer(1000 , TimeUnit.MILLISECONDS).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                print("timer");
            }
        });
    }

    /*
    7、使用interval创建一个按照给定的时间间隔发射从0开始的整数序列的(轮询)
    */
    @Test
    public void interval() throws Exception{
        Flowable.interval(1000 , TimeUnit.MILLISECONDS).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                print("timer");
            }
        });
    }


    //-------------------------合并操作-----------------------------
    /*
    8、使用concat 按顺序连接多个Observables。需要注意的是Observable.concat(a,b)等价于a.concatWith(b)
    */
    @Test
    public void concat() throws Exception{
        Flowable<Integer> flowable1 = Flowable.just(1,2,3,4,5);
        Flowable<Integer> flowable2 = Flowable.just(4,5,6);
        Flowable.concat(flowable1 , flowable2).subscribe(item->print(item.toString()));//1 2 3 4 5 4 5 6
    }

    /*
    9、使用startWith 在数据序列的开头增加一项数据。startWith的内部也是调用了concat
    */
    @Test
    public void startWith() throws Exception{
        Flowable.just(1,2,3).startWith(6).subscribe(this::print);// 6 1 2 3
    }

    /*
    10、merge 不同于concat，merge不是按照添加顺序连接，而是按照时间线来连接
    */
    @Test
    public void merge() throws Exception{
        Flowable<Integer> flowable1 = Flowable.just(1,2,3,4);
        Flowable<Integer> flowable2 = Flowable.just(4,5,6);
        Flowable.merge(flowable1 , flowable2).subscribe(item->print(item.toString()));//1 2 3 4 4 5 6
    }

    /*
    11、zip 使用一个函数组合多个Observable发射的数据集合，然后再发射这个结果
    */
    @Test
    public void zip() throws Exception{
        Consumer<Object> consumer = v -> System.out.println("[" + System.currentTimeMillis() / 100 + "] " + v);
        Flowable<Long> f1 = Flowable.interval(100, TimeUnit.MILLISECONDS);
        Flowable<Long> f2 = Flowable.interval(200, TimeUnit.MILLISECONDS);

        Flowable<Long> f3 = Flowable.zip(f1, f2, (x, y) -> x * 10000 + y);
        f3.subscribe(consumer);
    }



    //-------------------------过滤操作-----------------------------
    /*
    12、filter 过滤数据
    s->print(s) = this::print
    */
    @Test
    public void filter() throws Exception{
        Flowable.just(1,2,3,4).filter(i->i > 1).subscribe(this::print);//2 3 4
    }

    /*
    13、ofType 过滤指定类型的数据
    */
    @Test
    public void ofType() throws Exception{
        Flowable.just(1,2,3,"4").ofType(String.class).subscribe(this::print);//4
    }

    /*
    14、take 只发射开始的N项数据或者一定时间内的数据
    */
    @Test
    public void take() throws Exception{
        Flowable.just(1,2,3,4).take(2).subscribe(this::print);//1 , 2
    }

    /*
    15、takeLast 只发射最后的N项数据或者一定时间内的数据
    */
    @Test
    public void takeLast() throws Exception{
        Flowable.just(1,2,3,4).takeLast(2).subscribe(this::print);// 3 , 4
    }

    /*
    16、skip 跳过开始的N项数据或者一定时间内的数据
    */
    @Test
    public void skip() throws Exception{
        Flowable.just(1,2,3,4,5).skip(1).subscribe(this::print);//2 3 4 5
    }

    /*
    17、skipLast 跳过最后的N项数据或者一定时间内的数据
    */
    @Test
    public void skipLast() throws Exception{
        Flowable.just(1,2,3,4,5).skipLast(1).subscribe(this::print);//1 2 3 4
    }

    /*
    18、elementAt 跳过最后的N项数据或者一定时间内的数据 参数index
    */
    @Test
    public void elementAt() throws Exception{
        Flowable.just(1,2,3,4,5).elementAt(3).subscribe(this::print);//4

        Flowable.just(1,2,3,4,5).elementAt(9 , 20).subscribe(this::print);// 超过索引9，默认是20  输出:20
    }

    /*
    19、distinct 过滤重复数据
    */
    @Test
    public void distinct() throws Exception{
        Flowable.just(1,1,2,2,3,4,5).distinct().subscribe(this::print);//1 2 3 4 5
    }

    /*
    20、distinctUntilChanged 过滤重复数据
    */
    @Test
    public void distinctUntilChanged() throws Exception{
        Flowable.just(1,1,2,3,4,5,1,4).distinctUntilChanged().subscribe(this::print);//1 2 3 4 5 1 4
    }

    /*
    21、throttleFirst 定期发射Observable发射的第一项数据  限制200毫秒只能发射一次
    */
    @Test
    public void throttleFirst() throws Exception{
        Flowable.interval(1000 , TimeUnit.MILLISECONDS).throttleFirst(200,TimeUnit.MILLISECONDS).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                print("timer");
            }
        });
    }


    //-------------------------条件/布尔操作-----------------------------
    /*
    22、all 判断所有的数据项是否满足某个条件
    */
    @Test
    public void all() throws Exception{
        Flowable.just(1,2,3,4).all(i->i>1).subscribe(this::print);//是否所有数据都大于1 false
    }

    /*
    23、exists 判断是否存在数据项满足某个条件
    */
    @Test
    public void exists() throws Exception{
        Flowable.just(1,2,3,4).all(i->i>3).subscribe(this::print);//是否存在有数据大于3 true
    }

    /*
    24、contains 判断在发射的所有数据项中是否包含指定的数据
    */
    @Test
    public void contains() throws Exception{
        Flowable.just(1,2,3,4).contains(3).subscribe(this::print);// true
    }

    /*
    25、sequenceEqual 用于判断两个Observable发射的数据是否相同（数据，发射顺序，终止状态）。
    */
    @Test
    public void sequenceEqual() throws Exception{
        Flowable.sequenceEqual(Flowable.just(1,2,3,4) , Flowable.just(1,2,3,4)).subscribe(this::print);//true
    }

    /*
    26、isEmpty 用于判断Observable发射完毕时，有没有发射数据
    */
    @Test
    public void isEmpty() throws Exception{
        Flowable.just(1,2,3,4).isEmpty().subscribe(this::print);// false
    }

    /*
    27、takeUntil 当发射的数据满足某个条件后（包含该数据），或者第二个Observable发送完毕，终止第一个Observable发送数据
    */
    @Test
    public void takeUntil() throws Exception{
        Flowable.just(1,2,3,4,5).takeUntil(i->i==3).subscribe(this::print);// 1 2 3
    }


    //-------------------------聚合操作-----------------------------
    /*
    28、reduce 对序列使用reduce()函数并发射最终的结果
    */
    @Test
    public void reduce() throws Exception{
        Flowable.just(1,2,3,4,5).reduce((i1, i2) -> i1 + i2).subscribe(this::print);//15
    }

    /*
    29、collect 对序列使用reduce()函数并发射最终的结果
    */
    @Test
    public void collect() throws Exception{
        Flowable.just(1,2,3,4,5).collect((Callable<List<Integer>>) ArrayList::new, List::add).subscribe(integers -> {
            Flowable.fromIterable(integers).subscribe(this::print);//1 2 3 4 5
        });
    }



    //-------------------------转换操作-----------------------------
    /*
    30、toList 收集原始Observable发射的所有数据到一个列表，然后返回这个列表
    */
    @Test
    public void toList() throws Exception{
        Flowable.just(1,2,3,4,5).toList().subscribe(this::print);// [1, 2, 3, 4, 5]
    }

    /*
    31、toSortedList 收集原始Observable发射的所有数据到一个有序列表，然后返回这个列表
    */
    @Test
    public void toSortedList() throws Exception{
        Flowable.just(1,3,2,5,4).toSortedList((t1, t2) -> {
            return t1 - t2; //>0 升序 ，<0 降序
        }).subscribe(this::print);// [1, 2, 3, 4, 5]
    }

    /*
    32、toMap 收集原始Observable发射的所有数据到一个有序列表，然后返回这个列表
    */
    @Test
    public void toMap() throws Exception{
        Flowable.just(1,3,2,5,4).toMap(integer -> "key:" + integer, integer -> "value:" + integer).subscribe(this::print);//{key:2=value:2, key:1=value:1, key:5=value:5, key:4=value:4, key:3=value:3}
    }




    //-------------------------变换操作-----------------------------
    /*
    33、map 对Observable发射的每一项数据都应用一个函数来变换
    */
    @Test
    public void map() throws Exception{
        Flowable.just(1,2,3,4).map(i->"ws" + i).subscribe(this::print);//ws1 ws2 ws3 ws4
    }

    /*
    34、flatMap 将Observable发射的数据变换为Observables集合，然后将这些Observable发射的数据平坦化的放进一个单独的Observable，内部采用merge合并
    */
    @Test
    public void flatMap() throws Exception{
        Flowable.just(1,2,3,4).flatMap((Function<Integer, Publisher<?>>) integer -> Flowable.just(integer + "ws")).subscribe(this::print);// 1ws 2ws 3ws 4ws
    }

    /*
    35、scan 与reduce很像，对Observable发射的每一项数据应用一个函数，然后按顺序依次发射每一个值
    */
    @Test
    public void scan() throws Exception{
        Flowable.just(1,2,3).scan((i1, i2) -> i1 + i2).subscribe(this::print);// 1 3 6
    }

    /*
    36、buffer 它定期从Observable收集数据到一个集合，然后把这些数据集合打包发射，而不是一次发射一个
    */
    @Test
    public void buffer() throws Exception{
        Flowable.just(2,3,5,6,7).buffer(3).subscribe(this::print);//[2, 3, 5] [6, 7]

    }

    /*
    37、window 它定期从Observable收集数据到一个集合，然后把这些数据集合打包发射，而不是一次发射一个
    */
    @Test
    public void window() throws Exception{
        Flowable.just(2,3,5,6,7).window(3).subscribe(integerFlowable -> {
            integerFlowable.subscribe(this::print);//2, 3, 5 ,6, 7
        });

    }


    //-------------------------错误处理/重试机制-----------------------------
    /*
    38、onErrorResumeNext 当原始Observable在遇到错误时，使用备用
    */
    @Test
    public void onErrorResumeNext() throws Exception{
        Flowable.just(1,2,"3").cast(Integer.class).onErrorResumeNext(Flowable.just(4,5,6)).subscribe(this::print);// 1 2 4 5 6
    }

    /*
    39、onErrorReturn 当原始Observable在遇到错误时发射一个特定的数据
    */
    @Test
    public void onErrorReturn() throws Exception{
        Flowable.just(1,"2",3).cast(Integer.class).onErrorReturn(throwable -> 4).subscribe(this::print);// 1 4
    }

    /*
    40、retry 当原始Observable在遇到错误时，使用备用
    */
    @Test
    public void retry() throws Exception{
        Flowable.just(1,"2",3).cast(Integer.class).retry(3).subscribe(this::print);// 1 1 1 1
    }

    //-------------------------连接操作-----------------------------
    /*
    40、ConnectableObservable
        ConnectableObservable与普通的Observable差不多，但是可连接的Observable在被订阅时并不开始发射数据，只有在它的connect()被调用时才开始。用这种方法，你可以等所有的潜在订阅者都订阅了这个Observable之后才开始发射数据。
        ConnectableObservable.connect()指示一个可连接的Observable开始发射数据.
        Observable.publish()将一个Observable转换为一个可连接的Observable
        Observable.replay()确保所有的订阅者看到相同的数据序列的ConnectableObservable，即使它们在Observable开始发射数据之后才订阅。
        ConnectableObservable.refCount()让一个可连接的Observable表现得像一个普通的Observable。
    */
    @Test
    public void ConnectableObservable() throws Exception{
        ConnectableFlowable<Integer> connect = Flowable.just(1,2,3).publish();
        connect.subscribe(this::print);
        connect.connect();//此时开始发射数据
    }
}