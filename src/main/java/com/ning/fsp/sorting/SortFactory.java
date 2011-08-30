package com.ning.fsp.sorting;

import com.google.common.collect.Ordering;
import com.ning.fsp.SortParameter;
import com.ning.fsp.util.Adapter;
import com.ning.fsp.util.CompareHelper;


/**
 * Factory that creates criterias to sort a result set by integer values in a column.
 */
public class SortFactory<T, V extends Comparable<V>> implements SortCriteriaFactory<T>
{
    private final boolean expensive;
    private final boolean nullsFirst;
    private final String columnName;
    private final Adapter<T, V> adapter;

    public SortFactory(final boolean nullsFirst, final Adapter<T, V> adapter)
    {
        this.expensive = true;
        this.nullsFirst = nullsFirst;
        this.columnName = null;
        this.adapter = adapter;
    }

    /**
     * A factory returning criterias to sort by integer values.
     *
     * @param <T> Type of the sorted objects.
     */
    public SortFactory(final boolean nullsFirst, final String columnName, final Adapter<T, V> adapter)
    {
        this.expensive = false;
        this.nullsFirst = nullsFirst;
        this.columnName = columnName;
        this.adapter = adapter;
    }

    public SortCriteria<T> getCriteria(final SortParameter sortParameter)
    {

        return new SortCriteria<T>() {

            public boolean isDescending()
            {
                return sortParameter.getSortDirection() == SortDirection.DESCENDING;
            }

            public boolean isNullsFirst()
            {
                return nullsFirst;
            }

            public String getColumnName()
            {
                return columnName;
            }

            public boolean isExpensive()
            {
                return expensive;
            }

            public Ordering<T> getOrdering()
            {
                final Ordering<T> ordering = new Ordering<T>() {

                    @Override
                    public int compare(final T left, final T right)
                    {
                        final V leftElement = adapter.getValue(left);
                        final V rightElement = adapter.getValue(right);

                        return CompareHelper.compareEquality(leftElement, rightElement);
                    }
                };

                return nullsFirst ? ordering.nullsFirst() : ordering.nullsLast();
            }

            @Override
            public String toString()
            {
                return String.format("SortCriteria(columnName=%s, descending=%b, nullsFirst=%b)",
                                     columnName,
                                     isDescending(),
                                     nullsFirst);
            }

            @Override
            public boolean equals(final Object o)
            {
                return this == o;
            }

            @Override
            public int hashCode() {
                return System.identityHashCode(this);
            }
        };
    }
}
