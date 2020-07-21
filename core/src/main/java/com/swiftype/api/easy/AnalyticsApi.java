package com.swiftype.api.easy;

import com.swiftype.api.easy.helper.Client;
import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Provides access to the @see <a href="https://swiftype.com/documentation/analytics">Analytics API</a>
 *
 */
public class AnalyticsApi {
	private static final SimpleDateFormat DATE_FORMATER = new SimpleDateFormat("yyyy-MM-dd");

	private final String analyticsPath;

	public AnalyticsApi(final String engineId) {
		analyticsPath = EnginesApi.enginePath(engineId) + "/analytics";
	}

	/**
	 * @return 		List of searches per day
	 */
	public List<DateCount> searches() {
		return toDateCountList(Client.get(analyticsPath + "/searches"));
	}

	/**
	 * @param from	First day you want to have searches from
	 * @param to	Last day you want to have searches from
	 * @return		List of searches per day
	 */
	public List<DateCount> searches(final Date from, final Date to) {
		return toDateCountList(Client.get(analyticsPath + "/searches", queryRange(from, to)));
	}

	/**
	 * @param from	First day as "yyyy-MM-dd"
	 * @param to	Last day as "yyyy-MM-dd"
	 * @return		List of searches per day
	 */
	public List<DateCount> searches(final String from, final String to) {
		try {
			return searches(DATE_FORMATER.parse(from), DATE_FORMATER.parse(to));
		} catch (ParseException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	/**
	 * @return		List of clicks on autocomplete results per day
	 */
	public List<DateCount> autoselects() {
		return toDateCountList(Client.get(analyticsPath + "/autoselects"));
	}

	/**
	 * @param from	First day you want to have autoselects from
	 * @param to	Last day you want to have autoselects from
	 * @return		List of clicks on autocomplete results per day
	 */
	public List<DateCount> autoselects(final Date from, final Date to) {
		return toDateCountList(Client.get(analyticsPath + "/autoselects", queryRange(from, to)));
	}

	/**
	 * @param from 	First day as "yyyy-MM-dd"
	 * @param to	Last day as "yyyy-MM-dd"
	 * @return		List of clicks on autocomplete results per day
	 */
	public List<DateCount> autoselects(final String from, final String to) {
		try {
			return autoselects(DATE_FORMATER.parse(from), DATE_FORMATER.parse(to));
		} catch (ParseException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	/**
	 * @return		List of top queries with counts
	 */
	public List<QueryCount> topQueries() {
			return toQueryCountList(Client.get(analyticsPath + "/top_queries"));
	}

	/**
	 * @param page		Page you want to see
	 * @param perPage	Results per page
	 * @return			List of top queries with counts
	 */
	public List<QueryCount> topQueries(final int page, final int perPage) {
		final String[] pageParam = {"page", Integer.toString(page)};
		final String[] perPageParam = {"per_page", Integer.toString(perPage)};
		return toQueryCountList(Client.get(analyticsPath + "/top_queries", pageParam, perPageParam));
	}

	/**
	 * @param from		Start day
	 * @param to		Last day
	 * @return			Top 10 queries between start and last day
	 */
	public List<QueryCount> topQueries(final Date from, final Date to) {
		return toQueryCountList(Client.get(analyticsPath + "/top_queries_in_range", queryRange(from, to)));
	}

	/**
	 * @param from		Start day as "yyyy-MM-dd"
	 * @param to		Last day as "yyyy-MM-dd"
	 * @return			Top 10 queries between start and last day
	 */
	public List<QueryCount> topQueries(final String from, final String to) {
		try {
			return topQueries(DATE_FORMATER.parse(from), DATE_FORMATER.parse(to));
		} catch (ParseException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	/**
	 * @return		Top queries with no results in the last 14 days
	 */
	public List<QueryCount> topNoResultQueries() {
		return toQueryCountList(Client.get(analyticsPath + "/top_no_result_queries_in_range"));
	}

	/**
	 * @param from		Start day
	 * @param to		Last day
	 * @return			Top queries with no result between start and last day
	 */
	public List<QueryCount> topNoResultQueries(final Date from, final Date to) {
		return toQueryCountList(Client.get(analyticsPath + "/top_no_result_queries_in_range", queryRange(from, to)));
	}

	/**
	 * @param from		Start day as "yyyy-MM-dd"
	 * @param to		Last day as "yyyy-MM-dd"
	 * @return			Top queries with no result between start and last day
	 */
	public List<QueryCount> topNoResultQueries(final String from, final String to) {
		try {
			return topNoResultQueries(DATE_FORMATER.parse(from), DATE_FORMATER.parse(to));
		} catch (ParseException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	private String[][] queryRange(final Date from, final Date to) {
		final String[] fromDate = {"start_date", DATE_FORMATER.format(from)};
		final String[] toDate = {"end_date", DATE_FORMATER.format(to)};
		return new String[][] {fromDate, toDate};
	}

	private List<DateCount> toDateCountList(final String response) {
		try {
			final JSONArray dateCountsJson = new JSONArray(response);
			final List<DateCount> dateCounts = new ArrayList<DateCount>(dateCountsJson.length());
			for (int i = 0; i < dateCountsJson.length(); ++i) {
				final JSONArray dataPoint = dateCountsJson.getJSONArray(i);
				final Date date = DATE_FORMATER.parse(dataPoint.getString(0));
				final int count = dataPoint.getInt(1);
				dateCounts.add(new DateCount(date, count));
			}
			return dateCounts;
		} catch (JSONException e) {
			return null;
		} catch (ParseException e) {
			return null;
		}
	}

	private List<QueryCount> toQueryCountList(final String response) {
		try {
			final JSONArray queryCountsJson = new JSONArray(response);
			final List<QueryCount> queryCounts = new ArrayList<QueryCount>(queryCountsJson.length());
			for (int i = 0; i < queryCountsJson.length(); ++i) {
				final JSONArray dataPoint = queryCountsJson.getJSONArray(i);
				queryCounts.add(new QueryCount(dataPoint.getString(0), dataPoint.getInt(1)));
			}
			return queryCounts;
		} catch (JSONException e) {
			return null;
		}
	}

	public static class DateCount {
		public final Date day;
		public final int count;

		public DateCount(final Date day, final int count) {
			this.day = day;
			this.count = count;
		}

		@Override
		public String toString() {
			return "DateCount [day=" + day + ", count=" + count + "]";
		}
	}

	public static class QueryCount {
		public final String query;
		public final int count;

		private QueryCount(final String query, final int count) {
			this.query = query;
			this.count = count;
		}

		@Override
		public String toString() {
			return "QueryCount [query=" + query + ", count=" + count + "]";
		}
	}
}
