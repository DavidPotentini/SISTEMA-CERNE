package com.github.davidpotentini.comum.tenant;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

@Service
public class TenantSchemaProvisioner {

    private static final Pattern SCHEMA_NAME = Pattern.compile("^[a-z0-9_]+$");
    private static final String DDL_LOCATION = "classpath:sql/create_tenant_schema_tables.sql";

    // Registros padrão carregados na criação do tenant, na ordem: metodologia
    // (processos/práticas/indicadores) e depois o modelo padrão, que depende dela.
    private static final String[] SEED_LOCATIONS = {
        "classpath:sql/seed_cerne1_metodologia.sql",
        "classpath:sql/seed_cerne1_atividades_metodologia.sql"
    };

    private final JdbcTemplate jdbcTemplate;
    private final ResourceLoader resourceLoader;

    public TenantSchemaProvisioner(JdbcTemplate jdbcTemplate, ResourceLoader resourceLoader) {
        this.jdbcTemplate = jdbcTemplate;
        this.resourceLoader = resourceLoader;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void criarSchemaECarregarDDL(String nomeSchema) {

        if (nomeSchema == null || !SCHEMA_NAME.matcher(nomeSchema.toLowerCase()).matches()) {
            throw new IllegalArgumentException("Nome de schema inválido: " + nomeSchema);
        }

        String ddl = lerSql(DDL_LOCATION);

        jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS \"" + nomeSchema.toLowerCase() + "\"");
        try {
            jdbcTemplate.execute("SET search_path TO \"" + nomeSchema.toLowerCase() + "\"");
            jdbcTemplate.execute(ddl);
            for (String seed : SEED_LOCATIONS) {
                jdbcTemplate.execute(lerSql(seed));
            }
        } finally {
            jdbcTemplate.execute("SET search_path TO public");
        }
    }

    private String lerSql(String location) {
        Resource resource = resourceLoader.getResource(location);
        try (InputStream in = resource.getInputStream()) {
            return StreamUtils.copyToString(in, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao ler SQL do tenant: " + location, e);
        }
    }
}
