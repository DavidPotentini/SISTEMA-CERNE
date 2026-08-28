-- ============================================================================
-- Seed da metodologia CERNE 1 (Nível I) — Espaço Empreendedor / UFES
-- Fonte: Manual CERNE I e II (2023), seção 1 "Desenvolvimento dos empreendimentos".
--
-- Popula a árvore viva da metodologia do tenant: PROCESSOS -> PRATICAS ->
-- INDICADORES_METODOLOGIA. Os 5 processos-chave do Nível I, suas práticas-chave
-- e as "Métricas (indicadores)" de cada prática.
--
-- USO: rode UMA vez, no schema do tenant (defina o search_path antes), sobre uma
-- metodologia ainda vazia. IDs são IDENTITY; as práticas/indicadores localizam o
-- pai por NOME (nomes de processo e de prática são únicos neste seed).
--   SET search_path TO "<schema_do_tenant>";
--   \i seed_cerne1_metodologia.sql
-- Depois, gere um modelo padrão com seed_cerne1_modelo_padrao.sql.
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Processos-chave do CERNE 1
-- ----------------------------------------------------------------------------
INSERT INTO PROCESSOS (ORDEM, NOME, DESCRICAO) VALUES
    (1, '1.1 Sensibilização e Prospecção',
        'Ampliar a quantidade e a qualidade das propostas apresentadas à incubadora, por meio do fomento ao empreendedorismo nas comunidades local e regional e da busca ativa por empreendimentos de alto impacto e alto crescimento.'),
    (2, '1.2 Seleção',
        'Selecionar empreendimentos inovadores com maior probabilidade de sucesso, garantindo a recepção, a avaliação e a contratação das propostas de incubação.'),
    (3, '1.3 Desenvolvimento do Empreendimento',
        'Desenvolver os empreendimentos incubados de forma sistemática — planejando, agregando valor e monitorando a evolução nos cinco eixos: empreendedor, tecnologia, capital, mercado e gestão.'),
    (4, '1.4 Graduação e Relacionamento com Graduados',
        'Conduzir a saída bem-sucedida dos empreendimentos (graduação) e manter vínculos com os graduados, acompanhando sua evolução e ofertando serviços de valor agregado.'),
    (5, '1.5 Gerenciamento Básico',
        'Viabilizar o funcionamento efetivo da incubadora: estrutura organizacional, operação (gestão financeira, infraestrutura e serviços) e comunicação/marketing.');

-- ----------------------------------------------------------------------------
-- Práticas-chave (o processo pai é resolvido por NOME)
-- ----------------------------------------------------------------------------
INSERT INTO PRATICAS (PRC_COD, NOME, DESCRICAO) VALUES
    -- 1. Sensibilização e Prospecção
    ((SELECT PRC_COD FROM PROCESSOS WHERE NOME = '1.1 Sensibilização e Prospecção'),
        'Sensibilização',
        'Ampliar a quantidade e a qualidade das propostas por meio de ações de conscientização da importância da inovação (eventos internos e externos, rádio/TV e mídias sociais).'),
    ((SELECT PRC_COD FROM PROCESSOS WHERE NOME = '1.1 Sensibilização e Prospecção'),
        'Prospecção',
        'Buscar ativamente empreendimentos com potencial de alto impacto e crescimento que contribuam para o desenvolvimento da região de atuação da incubadora.'),
    ((SELECT PRC_COD FROM PROCESSOS WHERE NOME = '1.1 Sensibilização e Prospecção'),
        'Qualificação de Potenciais Empreendedores',
        'Aprimorar os conhecimentos e a capacidade dos potenciais empreendedores para a concepção de empreendimentos com alto potencial de sucesso.'),
    -- 2. Seleção
    ((SELECT PRC_COD FROM PROCESSOS WHERE NOME = '1.2 Seleção'),
        'Recepção de Propostas',
        'Garantir a inclusão, pelos empreendedores, das informações necessárias à avaliação da proposta de empreendimento pela incubadora.'),
    ((SELECT PRC_COD FROM PROCESSOS WHERE NOME = '1.2 Seleção'),
        'Avaliação',
        'Avaliar as propostas de incubação com critérios definidos e avaliadores qualificados, considerando os eixos empreendedor, tecnologia, capital, mercado e gestão.'),
    ((SELECT PRC_COD FROM PROCESSOS WHERE NOME = '1.2 Seleção'),
        'Contratação',
        'Estabelecer condições e dar transparência ao relacionamento entre empreendedores e incubadora, definindo direitos e deveres das partes.'),
    -- 3. Desenvolvimento do Empreendimento
    ((SELECT PRC_COD FROM PROCESSOS WHERE NOME = '1.3 Desenvolvimento do Empreendimento'),
        'Planejamento do Empreendimento',
        'Elaborar o plano de desenvolvimento do empreendimento, definindo ações para que ele se desenvolva no menor tempo possível nos cinco eixos.'),
    ((SELECT PRC_COD FROM PROCESSOS WHERE NOME = '1.3 Desenvolvimento do Empreendimento'),
        'Agregação de Valor',
        'Promover o desenvolvimento global dos empreendimentos por meio de qualificações, consultorias, mentorias, participação em feiras/eventos e uso de laboratórios.'),
    ((SELECT PRC_COD FROM PROCESSOS WHERE NOME = '1.3 Desenvolvimento do Empreendimento'),
        'Monitoramento do Empreendimento',
        'Acompanhar o desenvolvimento do empreendimento para identificar desvios em relação ao planejado e definir o momento de graduação.'),
    -- 4. Graduação e Relacionamento com Graduados
    ((SELECT PRC_COD FROM PROCESSOS WHERE NOME = '1.4 Graduação e Relacionamento com Graduados'),
        'Graduação',
        'Garantir que a saída do empreendimento seja bem-sucedida, com a mudança de status de incubado para graduado.'),
    ((SELECT PRC_COD FROM PROCESSOS WHERE NOME = '1.4 Graduação e Relacionamento com Graduados'),
        'Relacionamento com Graduados',
        'Manter vínculos com os graduados, monitorando a evolução e prestando serviços de valor agregado.'),
    -- 5. Gerenciamento Básico
    ((SELECT PRC_COD FROM PROCESSOS WHERE NOME = '1.5 Gerenciamento Básico'),
        'Estrutura Organizacional',
        'Viabilizar o funcionamento da incubadora com os documentos que comprovem sua existência e uma equipe adequada à sua operacionalização.'),
    ((SELECT PRC_COD FROM PROCESSOS WHERE NOME = '1.5 Gerenciamento Básico'),
        'Operação da Incubadora',
        'Viabilizar os recursos físicos e financeiros para a operação da incubadora: gestão financeira, infraestrutura física/tecnológica e serviços operacionais.'),
    ((SELECT PRC_COD FROM PROCESSOS WHERE NOME = '1.5 Gerenciamento Básico'),
        'Comunicação e Marketing',
        'Divulgar a incubadora e os empreendimentos junto à comunidade, fortalecendo sua imagem e visibilidade.');

-- ----------------------------------------------------------------------------
-- Indicadores (Métricas) por prática (a prática pai é resolvida por NOME)
-- ----------------------------------------------------------------------------
INSERT INTO INDICADORES_METODOLOGIA (PRT_COD, NOME, UNIDADE, PERIODICIDADE) VALUES
    -- Sensibilização
    ((SELECT PRT_COD FROM PRATICAS WHERE NOME = 'Sensibilização'),
        'Número de pessoas sensibilizadas', 'unidade', 'ANUAL'),
    -- Prospecção
    ((SELECT PRT_COD FROM PRATICAS WHERE NOME = 'Prospecção'),
        'Número de oportunidades prospectadas', 'unidade', 'ANUAL'),
    -- Qualificação de Potenciais Empreendedores
    ((SELECT PRT_COD FROM PRATICAS WHERE NOME = 'Qualificação de Potenciais Empreendedores'),
        'Número de potenciais empreendedores qualificados', 'unidade', 'ANUAL'),
    ((SELECT PRT_COD FROM PRATICAS WHERE NOME = 'Qualificação de Potenciais Empreendedores'),
        'Número de ações de qualificação realizadas', 'unidade', 'ANUAL'),
    -- Recepção de Propostas
    ((SELECT PRT_COD FROM PRATICAS WHERE NOME = 'Recepção de Propostas'),
        'Número de propostas recebidas por ano', 'unidade', 'ANUAL'),
    ((SELECT PRT_COD FROM PRATICAS WHERE NOME = 'Recepção de Propostas'),
        'Número de propostas recebidas da região por ano', 'unidade', 'ANUAL'),
    ((SELECT PRT_COD FROM PRATICAS WHERE NOME = 'Recepção de Propostas'),
        'Número de propostas recebidas fora da região por ano', 'unidade', 'ANUAL'),
    ((SELECT PRT_COD FROM PRATICAS WHERE NOME = 'Recepção de Propostas'),
        'Número de propostas recebidas da UFES', 'unidade', 'ANUAL'),
    -- Avaliação
    ((SELECT PRT_COD FROM PRATICAS WHERE NOME = 'Avaliação'),
        'Número de empreendimentos selecionados em cada processo de seleção', 'unidade', 'ANUAL'),
    ((SELECT PRT_COD FROM PRATICAS WHERE NOME = 'Avaliação'),
        'Percentual de empreendimentos selecionados em relação ao total de propostas submetidas', '%', 'ANUAL'),
    -- Contratação
    ((SELECT PRT_COD FROM PRATICAS WHERE NOME = 'Contratação'),
        'Número de empreendimentos com contratos assinados', 'unidade', 'ANUAL'),
    -- Planejamento do Empreendimento
    ((SELECT PRT_COD FROM PRATICAS WHERE NOME = 'Planejamento do Empreendimento'),
        'Percentual de empreendimentos com plano de desenvolvimento atualizado', '%', 'ANUAL'),
    -- Agregação de Valor
    ((SELECT PRT_COD FROM PRATICAS WHERE NOME = 'Agregação de Valor'),
        'Número de serviços oferecidos', 'unidade', 'ANUAL'),
    ((SELECT PRT_COD FROM PRATICAS WHERE NOME = 'Agregação de Valor'),
        'Número de horas de consultoria oferecidas', 'horas', 'ANUAL'),
    ((SELECT PRT_COD FROM PRATICAS WHERE NOME = 'Agregação de Valor'),
        'Número de horas de capacitação oferecidas', 'horas', 'ANUAL'),
    -- Monitoramento do Empreendimento
    ((SELECT PRT_COD FROM PRATICAS WHERE NOME = 'Monitoramento do Empreendimento'),
        'Número de empregos gerados pelos empreendimentos incubados', 'unidade', 'ANUAL'),
    ((SELECT PRT_COD FROM PRATICAS WHERE NOME = 'Monitoramento do Empreendimento'),
        'Percentual dos empreendimentos que não concluem o processo de incubação', '%', 'ANUAL'),
    ((SELECT PRT_COD FROM PRATICAS WHERE NOME = 'Monitoramento do Empreendimento'),
        'Percentual de empreendimentos de alto impacto', '%', 'ANUAL'),
    ((SELECT PRT_COD FROM PRATICAS WHERE NOME = 'Monitoramento do Empreendimento'),
        'Faturamento total dos empreendimentos incubados', 'R$', 'ANUAL'),
    ((SELECT PRT_COD FROM PRATICAS WHERE NOME = 'Monitoramento do Empreendimento'),
        'Total de impostos gerados pelos empreendimentos incubados', 'R$', 'ANUAL'),
    ((SELECT PRT_COD FROM PRATICAS WHERE NOME = 'Monitoramento do Empreendimento'),
        'Investimentos recebidos pelos empreendimentos incubados', 'R$', 'ANUAL'),
    ((SELECT PRT_COD FROM PRATICAS WHERE NOME = 'Monitoramento do Empreendimento'),
        'Investimento-anjo recebido pelos empreendimentos incubados', 'R$', 'ANUAL'),
    -- Graduação
    ((SELECT PRT_COD FROM PRATICAS WHERE NOME = 'Graduação'),
        'Número de graduadas', 'unidade', 'ANUAL'),
    ((SELECT PRT_COD FROM PRATICAS WHERE NOME = 'Graduação'),
        'Percentual das graduadas que se estabeleceram na região', '%', 'ANUAL'),
    -- Relacionamento com Graduados
    ((SELECT PRT_COD FROM PRATICAS WHERE NOME = 'Relacionamento com Graduados'),
        'Percentual das graduadas classificadas como de alto impacto', '%', 'ANUAL'),
    ((SELECT PRT_COD FROM PRATICAS WHERE NOME = 'Relacionamento com Graduados'),
        'Percentual das graduadas classificadas como de alto crescimento', '%', 'ANUAL'),
    ((SELECT PRT_COD FROM PRATICAS WHERE NOME = 'Relacionamento com Graduados'),
        'Percentual de graduadas que permanecem no mercado após 1 ano de graduação', '%', 'ANUAL'),
    ((SELECT PRT_COD FROM PRATICAS WHERE NOME = 'Relacionamento com Graduados'),
        'Percentual de graduadas que permanecem no mercado após 5 anos de graduação', '%', 'ANUAL'),
    ((SELECT PRT_COD FROM PRATICAS WHERE NOME = 'Relacionamento com Graduados'),
        'Número de empregos gerados pelas graduadas', 'unidade', 'ANUAL'),
    ((SELECT PRT_COD FROM PRATICAS WHERE NOME = 'Relacionamento com Graduados'),
        'Faturamento total das graduadas', 'R$', 'ANUAL'),
    ((SELECT PRT_COD FROM PRATICAS WHERE NOME = 'Relacionamento com Graduados'),
        'Total de impostos gerados pelas graduadas', 'R$', 'ANUAL'),
    ((SELECT PRT_COD FROM PRATICAS WHERE NOME = 'Relacionamento com Graduados'),
        'Investimentos recebidos pelas graduadas', 'R$', 'ANUAL'),
    ((SELECT PRT_COD FROM PRATICAS WHERE NOME = 'Relacionamento com Graduados'),
        'Percentual de graduadas adquiridas por outras empresas', '%', 'ANUAL'),
    ((SELECT PRT_COD FROM PRATICAS WHERE NOME = 'Relacionamento com Graduados'),
        'Percentual de graduadas que realizaram fusão com outras empresas', '%', 'ANUAL'),
    ((SELECT PRT_COD FROM PRATICAS WHERE NOME = 'Relacionamento com Graduados'),
        'Percentual das graduadas que mantêm interação com a incubadora', '%', 'ANUAL'),
    -- Estrutura Organizacional
    ((SELECT PRT_COD FROM PRATICAS WHERE NOME = 'Estrutura Organizacional'),
        'Número de parceiros regionais, nacionais e internacionais', 'unidade', 'ANUAL'),
    ((SELECT PRT_COD FROM PRATICAS WHERE NOME = 'Estrutura Organizacional'),
        'Número de eventos organizados pela incubadora ou dos quais participou da organização', 'unidade', 'ANUAL'),
    -- Operação da Incubadora
    ((SELECT PRT_COD FROM PRATICAS WHERE NOME = 'Operação da Incubadora'),
        'Total de receita gerada pela incubadora (econômica e financeira)', 'R$', 'ANUAL'),
    ((SELECT PRT_COD FROM PRATICAS WHERE NOME = 'Operação da Incubadora'),
        'Quantidade de recursos captados pela incubadora em instituições públicas e privadas', 'R$', 'ANUAL'),
    -- Comunicação e Marketing
    ((SELECT PRT_COD FROM PRATICAS WHERE NOME = 'Comunicação e Marketing'),
        'Número de pessoas alcançadas pelas ações de marketing', 'unidade', 'ANUAL');
