package com.hzzz.points.utils.data_structure;

/**
 * <p>tuple4，从vavr中精简了一下</p>
 *
 * @author <a href="https://github.com/HowieHz/">HowieHz</a>
 * @version 0.2.4.1
 * @since 2022-12-25 15:33
 */
public class Tuple4<T1, T2, T3, T4> {
    public Tuple4(T1 t1, T2 t2, T3 t3, T4 t4) {
        this._1 = t1;
        this._2 = t2;
        this._3 = t3;
        this._4 = t4;
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
     * The 3rd element of this tuple.
     */
    public final T3 _3;

    /**
     * The 4th element of this tuple.
     */
    public final T4 _4;

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

    /**
     * The 3rd element of this tuple.
     */
    public T3 _3() {
        return this._3;
    }

    /**
     * The 4th element of this tuple.
     */
    public T4 _4() {
        return this._4;
    }
}
