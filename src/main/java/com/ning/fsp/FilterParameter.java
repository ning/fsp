package com.ning.fsp;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * Extracts a filter criteria out of a query parameter. This is a purely syntactic check, as
 * we don't have the exact details for a given call when this object is created.
 * Jersey understands this if the C'tor takes a single String argument (see Jersey Docs, 1.3).
 */
public class FilterParameter
{
    private final String fieldName;
    private final String fieldMatch;
    private final boolean fieldIncluding;

    public FilterParameter(final String fieldName, final String fieldMatch)
    {
        if (StringUtils.isEmpty(fieldName)) {
            throw new IllegalArgumentException("field name must not be empty");
        }

        switch(fieldName.charAt(0)) {
            case '+':
                this.fieldName = fieldName.substring(1).toLowerCase(Locale.ENGLISH);
                this.fieldIncluding = true;
                break;
            case '-':
                this.fieldName = fieldName.substring(1).toLowerCase(Locale.ENGLISH);
                this.fieldIncluding = false;
                break;
            default:
                this.fieldName = fieldName.toLowerCase(Locale.ENGLISH);
                this.fieldIncluding = true;
            break;
        }

        this.fieldMatch = fieldMatch;

        if (StringUtils.isEmpty(this.fieldName)) {
            throw new IllegalArgumentException("field name must not be empty");
        }

    }

    public String getFieldName()
    {
        return fieldName;
    }

    public String getFieldMatch()
    {
        return fieldMatch;
    }

    public boolean isIncluding()
    {
        return fieldIncluding;
    }

    @Override
    public boolean equals(final Object other)
    {
        if (this == other)
            return true;
        if (!(other instanceof FilterParameter))
            return false;
        FilterParameter castOther = (FilterParameter) other;
        return new EqualsBuilder().append(fieldName, castOther.fieldName)
            .append(fieldMatch, castOther.fieldMatch)
            .append(fieldIncluding, castOther.fieldIncluding)
            .isEquals();
    }

    private transient int hashCode;

    @Override
    public int hashCode()
    {
        if (hashCode == 0) {
            hashCode = new HashCodeBuilder().append(fieldName)
                .append(fieldMatch)
                .append(fieldIncluding)
                .toHashCode();
        }
        return hashCode;
    }

    private transient String toString;

    @Override
    public String toString()
    {
        if (toString == null) {
            toString = new ToStringBuilder(this).append("fieldName", fieldName)
                .append("fieldMatch", fieldMatch)
                .append("fieldIncluding", fieldIncluding)
                .toString();
        }
        return toString;
    }
}
