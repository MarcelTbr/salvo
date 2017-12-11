package salvo;


import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.net.URI;
import java.net.URISyntaxException;


@Configuration
public class DataSourceConfig {

    Logger log = LoggerFactory.getLogger(getClass());

    @Primary
    @Bean
    @Profile("postgres")
    public DataSource postgresDataSource() {
        String databaseUrl = System.getenv("JDBC_DATABASE_*");//"gopvoefwfhejwq:1c41b79924ca4bb5dbc372ccf5e19afd333cf9a9c73273620c8fb27ca584c44f@ec2-46-137-174-67.eu-west-1.compute.amazonaws.com:5432/d7cp8qrrvsoq5t";
//System.getenv("DATABASE_URL");
        log.info("Initializing PostgreSQL database: {}", databaseUrl);
        System.out.println("Initializing PostgreSQL database: {}   " +  databaseUrl);
        URI dbUri;
        try {
            dbUri = new URI(databaseUrl);
        }
        catch (URISyntaxException e) {
            log.error(String.format("Invalid DATABASE_URL: %s", databaseUrl), e);
            System.out.println(String.format("Invalid DATABASE_URL: %s" ,  databaseUrl) + e);

            return null;
        }

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':'
                + dbUri.getPort() + dbUri.getPath();

        org.apache.tomcat.jdbc.pool.DataSource dataSource
                = new org.apache.tomcat.jdbc.pool.DataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setTestOnBorrow(true);
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnReturn(true);
        dataSource.setValidationQuery("SELECT 1");
        return dataSource;
    }


}
