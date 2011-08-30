package com.ning.fsp;

import com.ning.fsp.util.Pair;

public class PagerParameter extends Pair<Integer, Integer> {

    private static final long serialVersionUID = 1L;

    public PagerParameter(final Integer start, final Integer count) {
        super(start, count);
    }

}