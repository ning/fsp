package com.ning.fsp.util;

/**
 * An adapter defines a way to retrieve a type T from an object type V. It is used
 * in the filters and sorters to get the elements that should be filtered and sorted.
 *
 * @param <V> The value type that gets examined.
 * @param <T> The return type.
 *
 * Soundtrack: Parliament of Fooles / Paint my world.
 */
public interface Adapter<V, T>
{
    T getValue(V v);
}
