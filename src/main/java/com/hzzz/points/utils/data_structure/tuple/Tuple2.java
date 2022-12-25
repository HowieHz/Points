package com.hzzz.points.utils.data_structure.tuple;

/**
 * <p>tuple2，从vavr中精简了一下</p>
 *
 * @author <a href="https://github.com/HowieHz/">HowieHz</a>
 * @version 0.2.4.2
 * @since 2022-12-25 15:33
 */
public class Tuple2<T1, T2, T3, T4> {
    /**
     * 建立一个二元素元组
     * @param t1 元素一
     * @param t2 元素二
     */
    public Tuple2(T1 t1, T2 t2) {
        this._1 = t1;
        this._2 = t2;
    }

    /**
     * The 1st element of this tuple.
     */
    public final T1 _1;

    /**
     * The 2nd element of this tuple.
     */
    public final T2 _2;

    /**
     * The 1st element of this tuple.
     */
    public T1 _1() {
        return this._1;
    }

    /**
     * The 2nd element of this tuple.
     */
    public T2 _2() {
        return this._2;
    }
}