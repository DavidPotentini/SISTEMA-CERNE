package com.github.davidpotentini.cerne2.service.cadastro.multitenancy;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

@Service
public class TenantSchemaProvisioner {

    private static final Pattern SCHEMA_NAME = Pattern.compile("^[a-z0-9_]+$");
    private static final String DDL_LOCATION = "classpath:sql/create_tenant_schema_tables.sql";

    private final JdbcTemplate jdbcTemplate;
    private final ResourceLoader resourceLoader;

    public TenantSchemaProvisioner(JdbcTemplate jdbcTemplate, ResourceLoader resourceLoader) {
        this.jdbcTemplate = jdbcTemplate;
        this.resourceLoader = resourceLoader;
    }

    public void criarSchemaECarregarDDL(String nomeSchema) {
        if (nomeSchema == null || !SCHEMA_NAME.matcher(nomeSchema).matches()) {
            throw new IllegalArgumentException("Nome de schema inválido: " + nomeSchema);
        }

        String ddl = lerDDL();

        jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS \"" + nomeSchema + "\"");
        try {
            jdbcTemplate.execute("SET search_path TO \"" + nomeSchema + "\"");
            jdbcTemplate.execute(ddl);
        } finally {
            jdbcTemplate.execute("SET search_path TO public");
        }
    }

    private String lerDDL() {
        Resource resource = resourceLoader.getResource(DDL_LOCATION);
        try (InputStream in = resource.getInputStream()) {
            return StreamUtils.copyToString(in, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao ler DDL do tenant: " + DDL_LOCATION, e);
        }
    }
}
