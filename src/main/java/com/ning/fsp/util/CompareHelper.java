package com.ning.fsp.util;


public final class CompareHelper
{
    private CompareHelper() {
    }

    /**
     * Compare two elements null-safe using ==.
     */
    public static <T extends Comparable<T>> int compareIdentity(final T o1, final T o2) {

        if (o1 == o2) {
            return 0;
        }
        else if (o1 == null) {
            // o2 can't be null, in that case, the identity compare would have caught it.
            return -1;
        }
        else if (o2 == null) {
            return 1;
        }
        return o1.compareTo(o2);
    }

    /**
     * Compare two elements null-safe using equals.
     */
    public static <T extends Comparable<T>> int compareEquality(final T o1, final T o2) {

        if (o1 == null) {
            return (o2 == null) ? 0 : -1;
        }
        else if (o2 == null) {
            return 1;
        }

        return o1.compareTo(o2);
    }
}



