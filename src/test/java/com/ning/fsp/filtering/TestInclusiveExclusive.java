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
import com.ning.fsp.filtering.FilterBooleanFactory;
import com.ning.fsp.filtering.FilterCriteriaFactory;
import com.ning.fsp.util.Adapter;



@Test(groups="fast")
public class TestInclusiveExclusive
{
    public void testBooleanTrueFiltering() {
        final Map<String, FilterCriteriaFactory<Boolean>> filterCriteriaMap =  getBooleanResultMap();
        List<FilterParameter> filterParams = new ImmutableList.Builder<FilterParameter>().add(new FilterParameter("value", "true")).build();
        final Filter<Boolean> filter = new Filter<Boolean>(filterParams, filterCriteriaMap, Filter.FilterCost.EXPENSIVE);

        Assert.assertTrue(filter.isExpensive());

        List<Boolean> elements = new ImmutableList.Builder<Boolean>().add(Boolean.TRUE).add(Boolean.FALSE).build();

        Object[] filteredResults = filter.filter(elements).toArray();

        Assert.assertEquals(filteredResults.length  , 1);
        Assert.assertEquals(filteredResults[filteredResults.length-1], Boolean.TRUE);
    }

    public void testBooleanFalseFiltering() {
        final Map<String, FilterCriteriaFactory<Boolean>> filterCriteriaMap =  getBooleanResultMap();
        List<FilterParameter> filterParams = new ImmutableList.Builder<FilterParameter>().add(new FilterParameter("value", "false")).build();
        final Filter<Boolean> filter = new Filter<Boolean>(filterParams, filterCriteriaMap, Filter.FilterCost.EXPENSIVE);

        Assert.assertTrue(filter.isExpensive());

        List<Boolean> elements = new ImmutableList.Builder<Boolean>().add(Boolean.TRUE).add(Boolean.FALSE).build();

        Object[] filteredResults = filter.filter(elements).toArray();

        Assert.assertEquals(filteredResults.length  , 1);
        Assert.assertEquals(filteredResults[filteredResults.length-1], Boolean.FALSE);
    }

    public void testBooleanInclTrueFiltering() {
        final Map<String, FilterCriteriaFactory<Boolean>> filterCriteriaMap =  getBooleanResultMap();
        List<FilterParameter> filterParams = new ImmutableList.Builder<FilterParameter>().add(new FilterParameter("+value", "true")).build();
        final Filter<Boolean> filter = new Filter<Boolean>(filterParams, filterCriteriaMap, Filter.FilterCost.EXPENSIVE);

        Assert.assertTrue(filter.isExpensive());

        List<Boolean> elements = new ImmutableList.Builder<Boolean>().add(Boolean.TRUE).add(Boolean.FALSE).build();

        Object[] filteredResults = filter.filter(elements).toArray();

        Assert.assertEquals(filteredResults.length  , 1);
        Assert.assertEquals(filteredResults[filteredResults.length-1], Boolean.TRUE);
    }

    public void testBooleanInclFalseFiltering() {
        final Map<String, FilterCriteriaFactory<Boolean>> filterCriteriaMap =  getBooleanResultMap();
        List<FilterParameter> filterParams = new ImmutableList.Builder<FilterParameter>().add(new FilterParameter("+value", "false")).build();
        final Filter<Boolean> filter = new Filter<Boolean>(filterParams, filterCriteriaMap, Filter.FilterCost.EXPENSIVE);

        Assert.assertTrue(filter.isExpensive());

        List<Boolean> elements = new ImmutableList.Builder<Boolean>().add(Boolean.TRUE).add(Boolean.FALSE).build();

        Object[] filteredResults = filter.filter(elements).toArray();

        Assert.assertEquals(filteredResults.length  , 1);
        Assert.assertEquals(filteredResults[filteredResults.length-1], Boolean.FALSE);
    }



    public void testBooleanExclTrueFiltering() {
        final Map<String, FilterCriteriaFactory<Boolean>> filterCriteriaMap =  getBooleanResultMap();
        List<FilterParameter> filterParams = new ImmutableList.Builder<FilterParameter>().add(new FilterParameter("-value", "true")).build();
        final Filter<Boolean> filter = new Filter<Boolean>(filterParams, filterCriteriaMap, Filter.FilterCost.EXPENSIVE);

        Assert.assertTrue(filter.isExpensive());

        List<Boolean> elements = new ImmutableList.Builder<Boolean>().add(Boolean.TRUE).add(Boolean.FALSE).build();

        Object[] filteredResults = filter.filter(elements).toArray();

        Assert.assertEquals(filteredResults.length  , 1);
        Assert.assertEquals(filteredResults[filteredResults.length-1], Boolean.FALSE);
    }

    public void testBooleanExclFalseFiltering() {
        final Map<String, FilterCriteriaFactory<Boolean>> filterCriteriaMap =  getBooleanResultMap();
        List<FilterParameter> filterParams = new ImmutableList.Builder<FilterParameter>().add(new FilterParameter("-value", "false")).build();
        final Filter<Boolean> filter = new Filter<Boolean>(filterParams, filterCriteriaMap, Filter.FilterCost.EXPENSIVE);

        Assert.assertTrue(filter.isExpensive());

        List<Boolean> elements = new ImmutableList.Builder<Boolean>().add(Boolean.TRUE).add(Boolean.FALSE).build();

        Object[] filteredResults = filter.filter(elements).toArray();

        Assert.assertEquals(filteredResults.length  , 1);
        Assert.assertEquals(filteredResults[filteredResults.length-1], Boolean.TRUE);
    }

    public final Map<String, FilterCriteriaFactory<Boolean>> getBooleanResultMap() {
        final Map<String, FilterCriteriaFactory<Boolean>> criterias = Maps.newHashMap();

        criterias.put("value", new FilterBooleanFactory<Boolean>(new Adapter<Boolean, Boolean>() {
                            @Override
                            public Boolean getValue(final Boolean value) {
                                return value;
                            }
                        }));

        return Collections.unmodifiableMap(criterias);
    }
}

