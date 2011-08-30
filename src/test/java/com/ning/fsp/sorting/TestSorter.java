package com.ning.fsp.sorting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.ning.fsp.SortParameter;
import com.ning.fsp.Sorter;
import com.ning.fsp.sorting.SortCriteriaFactory;
import com.ning.fsp.sorting.SortDirection;
import com.ning.fsp.sorting.SortFactory;
import com.ning.fsp.util.Adapter;


@Test(groups = "fast")
public class TestSorter
{
    private static class IdentityAdapter<T> implements Adapter<T, T> {
        @Override
        public T getValue(final T value) {
            return value;
        }
    }

    public void testBasicIntegerSorting(){

        List<SortParameter> sortParams = new ImmutableList.Builder<SortParameter>().add(new SortParameter("Integer", SortDirection.DESCENDING)).build();
        Adapter<Integer, Integer> iAdaptor = new IdentityAdapter<Integer>();

        //We have to use 'integer' here for the field name, as the field is lowercased.
        Map<String, SortCriteriaFactory<Integer>> criteriaMap = new ImmutableMap.Builder<String, SortCriteriaFactory<Integer>>().put("integer", new SortFactory<Integer, Integer>(false, iAdaptor )).build() ;

        Sorter<Integer> longSorter = new Sorter<Integer>(sortParams, criteriaMap);

        List<Integer> numbers = new ArrayList<Integer>();
        numbers.add(1);
        numbers.add(3);
        numbers.add(17);
        numbers.add(18);
        numbers.add(100);

        List<Integer> sortedNumbers = (List<Integer>) longSorter.sort(numbers);

        Assert.assertEquals(sortedNumbers.get(0).longValue(), 100 );

        //Fieldname gets lowercased.
        Assert.assertEquals(sortParams.get(0).getFieldName(), "integer");
        Assert.assertEquals(sortParams.get(0).getSortDirection(), SortDirection.DESCENDING);

    }

    public void testBasicIntegerSortingNullsFirst(){

        List<SortParameter> sortParams = new ImmutableList.Builder<SortParameter>().add(new SortParameter("Integer", SortDirection.DESCENDING)).build();
        Adapter<Integer, Integer> iAdaptor = new IdentityAdapter<Integer>();

        //We have to use 'integer' here for the field name, as the field is lowercased.
        Map<String, SortCriteriaFactory<Integer>> criteriaMap = new ImmutableMap.Builder<String, SortCriteriaFactory<Integer>>().put("integer", new SortFactory<Integer, Integer>(false, iAdaptor )).build() ;

        Sorter<Integer> longSorter = new Sorter<Integer>(sortParams, criteriaMap);

        List<Integer> numbers = new ArrayList<Integer>();
        numbers.add(1);
        numbers.add(3);
        numbers.add(null);
        numbers.add(17);
        numbers.add(18);
        numbers.add(100);

        List<Integer> sortedNumbers = (List<Integer>) longSorter.sort(numbers);

        Assert.assertNull(sortedNumbers.get(0));
        Assert.assertEquals(sortedNumbers.get(1).longValue(), 100 );

        //Fieldname gets lowercased.
        Assert.assertEquals(sortParams.get(0).getFieldName(), "integer");
        Assert.assertEquals(sortParams.get(0).getSortDirection(), SortDirection.DESCENDING);

    }

    public void testBasicIntegerSortingNullsLast(){

        List<SortParameter> sortParams = new ImmutableList.Builder<SortParameter>().add(new SortParameter("Integer", SortDirection.DESCENDING)).build();
        Adapter<Integer, Integer> iAdaptor = new IdentityAdapter<Integer>();

        //We have to use 'integer' here for the field name, as the field is lowercased.
        Map<String, SortCriteriaFactory<Integer>> criteriaMap = new ImmutableMap.Builder<String, SortCriteriaFactory<Integer>>().put("integer", new SortFactory<Integer, Integer>(true, iAdaptor )).build() ;

        Sorter<Integer> longSorter = new Sorter<Integer>(sortParams, criteriaMap);

        List<Integer> numbers = new ArrayList<Integer>();
        numbers.add(1);
        numbers.add(3);
        numbers.add(null);
        numbers.add(17);
        numbers.add(18);
        numbers.add(100);

        List<Integer> sortedNumbers = (List<Integer>) longSorter.sort(numbers);

        Assert.assertEquals(sortedNumbers.get(0).longValue(), 100 );

        //Fieldname gets lowercased.
        Assert.assertEquals(sortParams.get(0).getFieldName(), "integer");
        Assert.assertEquals(sortParams.get(0).getSortDirection(), SortDirection.DESCENDING);


        Assert.assertEquals(sortedNumbers.get(0).intValue(), 100 );

        Assert.assertNull(sortedNumbers.get(5));

    }

    public void testBasicStringSorting(){

        List<SortParameter> sortParams = new ImmutableList.Builder<SortParameter>().add(new SortParameter("name", SortDirection.ASCENDING)).build();
        Adapter<String, String> sAdaptor = new IdentityAdapter<String>();

        Map<String, SortCriteriaFactory<String>> criteriaMap = new ImmutableMap.Builder<String, SortCriteriaFactory<String>>().put("name", new SortFactory<String, String>(false, sAdaptor )).build() ;

        Sorter<String> stringSorter = new Sorter<String>(sortParams, criteriaMap);

        List<String> strings = new ArrayList<String>();
        strings.add("za");
        strings.add("bb");
        strings.add("ab");
        strings.add(" ");
        strings.add("zz");

        List<String> sortedStrings = (List<String>) stringSorter.sort(strings);

        //Fieldname gets lowercased.
        Assert.assertEquals(sortParams.get(0).getFieldName(), "name");
        Assert.assertEquals(sortParams.get(0).getSortDirection(), SortDirection.ASCENDING);


        Assert.assertEquals(sortedStrings.get(0), " ");

        strings.remove(3);
        sortedStrings = (List<String>) stringSorter.sort(strings);

        Assert.assertEquals(sortedStrings.get(0), "ab");

    }

}

