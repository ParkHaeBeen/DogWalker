package com.project.dogwalker.config;

import com.project.dogwalker.domain.user.elastic.WalkerSearchRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackageClasses = WalkerSearchRepository.class)
public class ElasticSearchConfig  extends ElasticsearchConfiguration {
  @Value("${spring.elastic.url}")
  private String elasticUrl;
  @Override
  public ClientConfiguration clientConfiguration() {
    return ClientConfiguration.builder()
        .connectedTo(elasticUrl)
        .build();
  }
}