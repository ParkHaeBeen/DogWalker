package com.project.dogwalker.domain.user.elastic;


import com.project.dogwalker.walkerList.dto.SearchCond;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.erhlc.NativeSearchQuery;
import org.springframework.data.elasticsearch.client.erhlc.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomWalkerSearchImplRepository implements CustomWalkerSearchRepository{

  private final ElasticsearchOperations elasticsearchOperations;
  private final double distanceMax=10;

  @Override
  public Page <WalkerDocument> searchByName(final SearchCond searchCond,final Pageable pageable) {
    NativeSearchQuery query = createConditionQuery(
        searchCond , pageable);

    SearchHits <WalkerDocument> searchHits = elasticsearchOperations.search(query, WalkerDocument.class);

    List <WalkerDocument> list = searchHits.stream().map(SearchHit::getContent)
        .collect(Collectors.toList());

    return new PageImpl <>(list, pageable, searchHits.getTotalHits());
  }

  private NativeSearchQuery createConditionQuery(final SearchCond searchCond ,final Pageable pageable) {
    BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

    boolQueryBuilder.filter(QueryBuilders.geoDistanceQuery("location")
        .point(searchCond.getLat(), searchCond.getLnt()).distance(distanceMax + "km"));

    if(searchCond.getWalkerName()!=null||!searchCond.getWalkerName().equals("")){
      boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("walkerName", searchCond.getWalkerName()));
    }

    GeoDistanceSortBuilder geoDistanceSort = SortBuilders.geoDistanceSort("location", searchCond.getLat(),
            searchCond.getLat())
        .order(SortOrder.ASC)
        .unit(DistanceUnit.KILOMETERS);

    NativeSearchQuery query = new NativeSearchQueryBuilder()
        .withQuery(boolQueryBuilder)
        .withSorts(geoDistanceSort)
        .withPageable(pageable)
        .build();
    return query;
  }
}
