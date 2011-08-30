package com.ning.fsp;

import java.util.Locale;
import com.ning.fsp.sorting.SortDirection;



/**
 * Contains the information for a field sort.
 */
public class SortParameter
{
    private final String fieldName;
    private final SortDirection sortDirection;

    public SortParameter(final String fieldName, final SortDirection sortDirection)
    {
        this.fieldName = fieldName.toLowerCase(Locale.ENGLISH);
        this.sortDirection = sortDirection;
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public SortDirection getSortDirection()
    {
        return sortDirection;
    }
}
