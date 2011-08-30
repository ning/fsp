package com.ning.fsp.sorting;

/**
 * Sorting directions. Currently, only Ascending and Descending are supported.
 */
public enum SortDirection {

    ASCENDING('A'), DESCENDING('D');

    private final char status;

    private SortDirection(final char status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        return Character.toString(status);
    }

    public char getStatus()
    {
        return status;
    }

    public static SortDirection parse(final Character value) {

      if (value != null) {
          for (SortDirection type : SortDirection.values()) {
              if (type.getStatus() == value) {
                  return type;
              }
          }
      }
      return ASCENDING;
    }
}
