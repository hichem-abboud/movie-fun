package org.superbiz.moviefun;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.EntityManagerFactoryAccessor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ServletRegistrationBean actionServletRegistration(ActionServlet actionServlet) {
        return new ServletRegistrationBean(actionServlet, "/moviefun/*");
    }
    @Bean
    DatabaseServiceCredentials databaseServiceCredentials(@Value ("${VCAP_SERVICES:toto}") String vcapServices) {
        System.out.println("vcapServices = " + vcapServices);
        return new DatabaseServiceCredentials(vcapServices);
    }

    @Bean
    public DataSource albumsDataSource(DatabaseServiceCredentials serviceCredentials) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(serviceCredentials.jdbcUrl("albums-mysql"));
        return dataSource;
    }

    @Bean
    public DataSource moviesDataSource(DatabaseServiceCredentials serviceCredentials) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(serviceCredentials.jdbcUrl("movies-mysql"));
        return dataSource;
    }

    @Bean
    public HibernateJpaVendorAdapter hibernateJpaVendorAdapter() {

        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setDatabase(Database.MYSQL);
        adapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        adapter.setGenerateDdl(true);
        adapter.setShowSql(true);

        return adapter;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean localContainerEntityManagerMovies( DataSource moviesDataSource, HibernateJpaVendorAdapter adapter) {

        LocalContainerEntityManagerFactoryBean localContainer = new LocalContainerEntityManagerFactoryBean();
        localContainer.setDataSource(moviesDataSource);
        localContainer.setJpaVendorAdapter(adapter);
        localContainer.setPackagesToScan("org.superbiz.moviefun.movies");
        localContainer.setPersistenceUnitName("movies-name");

        return localContainer;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean localContainerEntityManagerAlbums( DataSource albumsDataSource, HibernateJpaVendorAdapter adapter) {

        LocalContainerEntityManagerFactoryBean localContainer = new LocalContainerEntityManagerFactoryBean();
        localContainer.setDataSource(albumsDataSource);
        localContainer.setJpaVendorAdapter(adapter);
        localContainer.setPackagesToScan("org.superbiz.moviefun.albums");
        localContainer.setPersistenceUnitName("albums-name");

        return localContainer;
    }

    @Bean(name="platformTransactionManagerMovies")
   public PlatformTransactionManager platformTransactionManagerMovies( EntityManagerFactory localContainerEntityManagerMovies){
        return new JpaTransactionManager(localContainerEntityManagerMovies);

    }
    @Bean(name="platformTransactionManagerAlbums")
    public PlatformTransactionManager platformTransactionManagerAlbums(  EntityManagerFactory localContainerEntityManagerAlbums) {
        return new JpaTransactionManager(localContainerEntityManagerAlbums);

    }
}
