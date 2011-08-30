package com.ning.fsp.filtering;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.ning.fsp.Filter;
import com.ning.fsp.FilterParameter;
import com.ning.fsp.filtering.FilterCriteriaFactory;
import com.ning.fsp.filtering.FilterIntegerFactory;
import com.ning.fsp.filtering.FilterStringFactory;
import com.ning.fsp.util.Adapter;
import com.ning.fsp.util.StringMatchType;



@Test(groups="fast")
public class TestFiltering
{

    public void testBasicFiltering(){

        final Map<String, FilterCriteriaFactory<Integer>> filterCriteriaMap =  getIntegerResultMap();
        List<FilterParameter> filterParams = new ImmutableList.Builder<FilterParameter>().add(new FilterParameter("quantity", "4")).build();
        final Filter<Integer> filter = new Filter<Integer>(filterParams, filterCriteriaMap, Filter.FilterCost.EXPENSIVE);

        List<Integer> elements = new ImmutableList.Builder<Integer>().add(0).add(100).add(4).add(14).add(24).add(34).add(44).add(54).add(64).add(84).add(74).add(34).add(76).build();

        Object[] filteredResults = filter.filter(elements).toArray();

        Assert.assertEquals(filteredResults.length  , 1 );
        Assert.assertEquals(((Integer)filteredResults[0]).intValue(), 4);

    }


    public void testBasicOrFiltering(){

        final Map<String, FilterCriteriaFactory<Integer>> filterCriteriaMap =  getIntegerResultMap();
        List<FilterParameter> filterParams = new ImmutableList.Builder<FilterParameter>().add(new FilterParameter("quantity", "4")).add(new FilterParameter("quantity", "14")).build();
        final Filter<Integer> filter = new Filter<Integer>(filterParams, filterCriteriaMap, Filter.FilterCost.EXPENSIVE);

        List<Integer> elements = new ImmutableList.Builder<Integer>().add(0).add(100).add(4).add(14).add(24).add(34).add(44).add(54).add(64).add(84).add(74).add(34).add(76).build();

        Object[] filteredResults = filter.filter(elements).toArray();

        Assert.assertEquals(filteredResults.length  , 2 );
        Assert.assertEquals((Integer)filteredResults[1], new Integer(14));

    }

    public void testBasicStringOrFiltering(){

        final Map<String, FilterCriteriaFactory<String>> filterCriteriaMap =  getStringResultMap();
        List<FilterParameter> filterParams = new ImmutableList.Builder<FilterParameter>().add(new FilterParameter("name", "Joe")).add(new FilterParameter("name", "jim")).build();
        final Filter<String> filter = new Filter<String>(filterParams, filterCriteriaMap, Filter.FilterCost.EXPENSIVE);

        List<String> elements = new ImmutableList.Builder<String>().add("Joe").add("Joe Bob").add("Joe3").add("Jim").add("Bob").build();

        Object[] filteredResults = filter.filter(elements).toArray();

        Assert.assertEquals(filteredResults.length  , 4 );
        Assert.assertTrue(filteredResults[filteredResults.length-1].equals("Jim"));
        Assert.assertFalse(filteredResults[filteredResults.length-1].equals("jim"));

    }

    public void testBasicStringAndFiltering(){

        final Map<String, FilterCriteriaFactory<String>> filterCriteriaMap =  getStringResultMap();
        List<FilterParameter> filterParams = new ImmutableList.Builder<FilterParameter>().add(new FilterParameter("name", "Joe")).build();
        final Filter<String> filter = new Filter<String>(filterParams, filterCriteriaMap, Filter.FilterCost.EXPENSIVE);

        Assert.assertTrue(filter.isExpensive());

        List<String> elements = new ImmutableList.Builder<String>().add("Joe").add("Joe Bob").add("Joe3").add("Jim").add("Bob").build();

        Object[] filteredResults = filter.filter(elements).toArray();

        Assert.assertEquals(filteredResults.length  , 3 );
        Assert.assertTrue(filteredResults[filteredResults.length-1].equals("Joe3"));
        Assert.assertFalse(filteredResults[filteredResults.length-1].equals("joe9"));

    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void testEmptyArgument() {
        new FilterParameter("", "false");
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void testLonePlus() {
        new FilterParameter("+", "false");
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void testLoneMinus() {
        new FilterParameter("-", "false");
    }

    public void testEquals() {
        FilterParameter p1 = new FilterParameter("value", "true");
        FilterParameter p2 = new FilterParameter("value", "false");
        FilterParameter p3 = new FilterParameter("+value", "true");
        FilterParameter p4 = new FilterParameter("-value", "true");
        FilterParameter p5 = new FilterParameter("value2", "false");

        Assert.assertFalse(p1.equals(p2));
        Assert.assertFalse(p1.equals(p4));
        Assert.assertFalse(p1.equals(p5));

        Assert.assertFalse(p2.equals(p3));
        Assert.assertFalse(p2.equals(p4));
        Assert.assertFalse(p2.equals(p5));

        Assert.assertFalse(p3.equals(p4));
        Assert.assertFalse(p3.equals(p5));

        Assert.assertFalse(p4.equals(p5));

        // Default is 'inclusive'
        Assert.assertTrue(p1.equals(p3));

        Assert.assertTrue(p1.equals(p1));
        Assert.assertTrue(p2.equals(p2));
        Assert.assertTrue(p3.equals(p3));
        Assert.assertTrue(p4.equals(p4));
        Assert.assertTrue(p5.equals(p5));
    }

    /**
     * This is a fakey class that returns an input int as the value for the quantity
     * @return
     */
    public final Map<String, FilterCriteriaFactory<Integer>> getIntegerResultMap() {

        final Map<String, FilterCriteriaFactory<Integer>> criterias = Maps.newHashMap();

        criterias.put("quantity", new FilterIntegerFactory<Integer>("quantity", new Adapter<Integer, Integer>() {
            @Override
            public Integer getValue(final Integer result) {
                return result.intValue();
            }
        }));
        return Collections.unmodifiableMap(criterias);
    }


    public final Map<String, FilterCriteriaFactory<String>> getStringResultMap() {

        final Map<String, FilterCriteriaFactory<String>> criterias = Maps.newHashMap();

        criterias.put("name", new FilterStringFactory<String>(StringMatchType.CASE_INSENSITIVE_PARTIAL, "name", new Adapter<String, String>() {
            @Override
            public String getValue(final String result) {
                return result;
            }
        }));
        return Collections.unmodifiableMap(criterias);
    }
}

