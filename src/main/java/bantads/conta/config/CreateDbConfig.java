package bantads.conta.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "createEntityManagerFactory",
        transactionManagerRef = "createTransactionManager",
        basePackages = { "bantads.conta.create.repository" }
)
public class CreateDbConfig {

    @Bean(name = "createDataSource")
    @ConfigurationProperties(prefix = "spring.second-datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "createEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean createEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("createDataSource") DataSource dataSource
    ) {
        return builder
                .dataSource(dataSource)
                .packages("bantads.conta.model")
                .persistenceUnit("conta").persistenceUnit("movimentacao")
                .build();
    }
    @Bean(name = "createTransactionManager")
    public PlatformTransactionManager createTransactionManager(
            @Qualifier("createEntityManagerFactory") EntityManagerFactory
                    createEntityManagerFactory
    ) {
        return new JpaTransactionManager(createEntityManagerFactory);
    }
}