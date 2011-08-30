package com.ning.fsp.paging;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.ning.fsp.Filter;
import com.ning.fsp.FilterParameter;
import com.ning.fsp.Pager;
import com.ning.fsp.PagerParameter;
import com.ning.fsp.filtering.FilterCriteriaFactory;
import com.ning.fsp.filtering.FilterIntegerFactory;
import com.ning.fsp.sorting.SortCriteriaFactory;
import com.ning.fsp.sorting.SortFactory;
import com.ning.fsp.util.Adapter;


@Test(groups = "fast")
public class TestPaging
{

    /**
     * simple stupid class that sorts Itegers, so we can test the pagination.
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


    public final Map<String, SortCriteriaFactory<Integer>> getLabelResultMap() {

        final Map<String, SortCriteriaFactory<Integer>> criterias = Maps.newHashMap();

        criterias.put("quantity", new SortFactory<Integer, Integer>(false, new Adapter<Integer, Integer>() {
                    @Override
                    public Integer getValue(Integer val) {
                        return val.intValue();
                    }
                }));
        return Collections.unmodifiableMap(criterias);
    }


    private final Map<String, FilterCriteriaFactory<Integer>> filterCriteriaMap =  getIntegerResultMap();

    public void testBasicCheapPagination()
    {
        List<Integer> elements = new ImmutableList.Builder<Integer>().add(0).add(100).add(4).add(14).add(24).add(34).add(44).add(54).add(64).add(84).add(74).add(34).add(76).build();

        final PagerParameter pagerParams = new PagerParameter(1, 4);

        final Pager<Integer> pager = new Pager<Integer>(pagerParams, Pager.PagerCost.CHEAP);

        final Collection<Integer> pagedResults = pager.page(elements);

        final Object results[] = pagedResults.toArray();
        Assert.assertEquals(results.length, elements.size());

        for (int i = 0; i < results.length; i++) {
            Assert.assertEquals(results[i], elements.get(i));
        }
    }

    public void testBasicExpensivePagination()
    {
        List<Integer> elements = new ImmutableList.Builder<Integer>().add(0).add(100).add(4).add(14).add(24).add(34).add(44).add(54).add(64).add(84).add(74).add(34).add(76).build();

        int start = 1;
        int len = 4;
        final PagerParameter pagerParams = new PagerParameter(start, len);

        final Pager<Integer> pager = new Pager<Integer>(pagerParams, Pager.PagerCost.EXPENSIVE);

        final Collection<Integer> pagedResults = pager.page(elements);

        final Object results[] = pagedResults.toArray();
        Assert.assertEquals(results.length, len);

        for (int i = 0; i < results.length; i++) {
            Assert.assertEquals(results[i], elements.get(i + start));
        }
    }

    public void testFilterPagination()
    {
        List<Integer> elements = new ImmutableList.Builder<Integer>().add(0).add(100).add(4).add(4).add(4).add(4).add(44).add(4).add(4).add(4).add(74).add(34).add(76).build();

        final PagerParameter pagerParams = new PagerParameter(0, 4);

        List<FilterParameter> filterParams = new ImmutableList.Builder<FilterParameter>().add(new FilterParameter("quantity", "4")).build();

        final Filter<Integer> filter = new Filter<Integer>(filterParams, filterCriteriaMap, Filter.FilterCost.EXPENSIVE);
        final Pager<Integer> pager = new Pager<Integer>(pagerParams, filter, null);

        final Collection<Integer> filteredResults = filter.filter(elements);
        final Collection<Integer> pagedResults = pager.page(filteredResults);

        final Object results[] = pagedResults.toArray();
        Assert.assertEquals(results.length, 4);
        Assert.assertEquals(results[0], 4);
        Assert.assertEquals(results[1], 4);
        Assert.assertEquals(results[2], 4);
        Assert.assertEquals(results[3], 4);
    }
}

