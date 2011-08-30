package com.ning.fsp.sorting;

import org.joda.time.DateTime;

import com.google.common.collect.Ordering;
import com.ning.fsp.SortParameter;
import com.ning.fsp.util.Adapter;
import com.ning.fsp.util.CompareHelper;


/**
 * A factory returning criterias to sort by {@link DateTime} objects.
 *
 * @param <T>
 *            Type of the sorted objects.
 */
public class SortDateFactory<T> implements SortCriteriaFactory<T>
{
    private final boolean expensive;
    private final boolean nullsFirst;
    private final String columnName;
    private final Adapter<T, DateTime> dateAdapter;

    public SortDateFactory(final boolean nullsFirst, final Adapter<T, DateTime> dateAdapter)
    {
        this.expensive = true;
        this.nullsFirst = nullsFirst;
        this.columnName = null;
        this.dateAdapter = dateAdapter;
    }

    public SortDateFactory(final boolean nullsFirst, final String columnName, final Adapter<T, DateTime> dateAdapter)
    {
        this.expensive = false;
        this.nullsFirst = nullsFirst;
        this.columnName = columnName;
        this.dateAdapter = dateAdapter;
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
                final Ordering<T> dateOrdering = new Ordering<T>() {

                    @Override
                    @SuppressWarnings("unchecked")
                    public int compare(final T left, final T right)
                    {
                        final DateTime leftDate = dateAdapter.getValue(left);
                        final DateTime rightDate = dateAdapter.getValue(right);

                        return CompareHelper.<DateTime>compareEquality(leftDate, rightDate);
                    }
                };

                return nullsFirst ? dateOrdering.nullsFirst() : dateOrdering.nullsLast();

            }

            @Override
            public String toString()
            {
                return String.format("DateTimeSortCriteria(columnName=%s, descending=%b, nullsFirst=%b)",
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
