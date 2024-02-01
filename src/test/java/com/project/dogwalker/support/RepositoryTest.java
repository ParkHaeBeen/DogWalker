package com.project.dogwalker.support;

import com.project.dogwalker.common.config.ElasticSearchConfig;
import com.project.dogwalker.common.config.JpaAuditingConfig;
import com.project.dogwalker.common.config.QueryDslConfig;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest
@SpringBatchTest
@Import({ElasticSearchConfig.class, JpaAuditingConfig.class, QueryDslConfig.class})
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles(profiles = "test")
public @interface RepositoryTest {

}
