package com.ning.fsp.util;

import java.io.Serializable;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class Pair<K, V> implements Serializable
{
    private static final long serialVersionUID = 2;

    private final K key;
    private final V value;

    public Pair(final K key,
                final V value)
    {
        this.key = key;
        this.value = value;
    }

    public K getKey()
    {
        return key;
    }

    public V getValue()
    {
        return value;
    }

    public static final <K, V> Pair<K, V> pair(final K k,
                                               final V v)
    {
        return new Pair<K, V>(k, v);
    }

    @Override
    public boolean equals(final Object other)
    {
        if (!(other instanceof Pair))
            return false;
        Pair<?, ?> castOther = (Pair<?, ?>) other;
        return new EqualsBuilder().append(key, castOther.key)
                .append(value, castOther.value)
                .isEquals();
    }

    private transient int hashCode;

    @Override
    public int hashCode()
    {
        if (hashCode == 0) {
            hashCode = new HashCodeBuilder().append(key)
                    .append(value)
                    .toHashCode();
        }
        return hashCode;
    }

    private transient String toString;

    @Override
    public String toString()
    {
        if (toString == null) {
            toString = new ToStringBuilder(this).append("key", key)
                    .append("value", value)
                    .toString();
        }
        return toString;
    }
}
