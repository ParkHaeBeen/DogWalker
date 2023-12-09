package com.project.dogwalker.config;

import com.project.dogwalker.domain.user.elastic.WalkerSearchRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackageClasses = WalkerSearchRepository.class)
public class ElasticSearchConfig  {

}