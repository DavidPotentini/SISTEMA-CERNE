-- ============================================================================
-- Seed da metodologia CERNE 1 (Nível I) — Espaço Empreendedor / UFES
-- Fonte: Manual CERNE I e II (2023), seção 1 "Desenvolvimento dos empreendimentos".
--
-- Popula a árvore viva da metodologia do tenant: PROCESSOS_METODOLOGIA -> PRATICAS_METODOLOGIA ->
-- INDICADORES_METODOLOGIA. Os 5 processos-chave do Nível I, suas práticas-chave
-- e as "Métricas (indicadores)" de cada prática. Indicadores nascem com periodicidade MENSAL
-- (padrão do sistema; ajustável por indicador na metodologia).
--
-- USO: rode UMA vez, no schema do tenant (defina o search_path antes), sobre uma
-- metodologia ainda vazia. IDs são IDENTITY; as práticas/indicadores localizam o
-- pai por NOME (nomes de processo e de prática são únicos neste seed).
--   SET search_path TO "<schema_do_tenant>";
--   \i seed_cerne1_metodologia.sql
-- Depois, carregue as atividades-padrão com seed_cerne1_atividades_metodologia.sql.
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Processos-chave do CERNE 1
-- ----------------------------------------------------------------------------
INSERT INTO PROCESSOS_METODOLOGIA (ORDEM, NOME, DESCRICAO) VALUES
    (1, 'Sensibilização e Prospecção',
        'Ampliar a quantidade e a qualidade das propostas apresentadas à incubadora, por meio do fomento ao empreendedorismo nas comunidades local e regional e da busca ativa por empreendimentos de alto impacto e alto crescimento.'),
    (2, 'Seleção',
        'Selecionar empreendimentos inovadores com maior probabilidade de sucesso, garantindo a recepção, a avaliação e a contratação das propostas de incubação.'),
    (3, 'Desenvolvimento do Empreendimento',
        'Desenvolver os empreendimentos incubados de forma sistemática — planejando, agregando valor e monitorando a evolução nos cinco eixos: empreendedor, tecnologia, capital, mercado e gestão.'),
    (4, 'Graduação e Relacionamento com Graduados',
        'Conduzir a saída bem-sucedida dos empreendimentos (graduação) e manter vínculos com os graduados, acompanhando sua evolução e ofertando serviços de valor agregado.'),
    (5, 'Gerenciamento Básico',
        'Viabilizar o funcionamento efetivo da incubadora: estrutura organizacional, operação (gestão financeira, infraestrutura e serviços) e comunicação/marketing.');

-- ----------------------------------------------------------------------------
-- Práticas-chave (o processo pai é resolvido por NOME)
-- ----------------------------------------------------------------------------
INSERT INTO PRATICAS_METODOLOGIA (PRC_COD, ORDEM, NOME, DESCRICAO) VALUES
    -- 1. Sensibilização e Prospecção
    ((SELECT PRC_COD FROM PROCESSOS_METODOLOGIA WHERE NOME = 'Sensibilização e Prospecção'), 1,
        'Sensibilização',
        'Ampliar a quantidade e a qualidade das propostas por meio de ações de conscientização da importância da inovação (eventos internos e externos, rádio/TV e mídias sociais).'),
    ((SELECT PRC_COD FROM PROCESSOS_METODOLOGIA WHERE NOME = 'Sensibilização e Prospecção'), 2,
        'Prospecção',
        'Buscar ativamente empreendimentos com potencial de alto impacto e crescimento que contribuam para o desenvolvimento da região de atuação da incubadora.'),
    ((SELECT PRC_COD FROM PROCESSOS_METODOLOGIA WHERE NOME = 'Sensibilização e Prospecção'), 3,
        'Qualificação de Potenciais Empreendedores',
        'Aprimorar os conhecimentos e a capacidade dos potenciais empreendedores para a concepção de empreendimentos com alto potencial de sucesso.'),
    -- 2. Seleção
    ((SELECT PRC_COD FROM PROCESSOS_METODOLOGIA WHERE NOME = 'Seleção'), 1,
        'Recepção de Propostas',
        'Garantir a inclusão, pelos empreendedores, das informações necessárias à avaliação da proposta de empreendimento pela incubadora.'),
    ((SELECT PRC_COD FROM PROCESSOS_METODOLOGIA WHERE NOME = 'Seleção'), 2,
        'Avaliação',
        'Avaliar as propostas de incubação com critérios definidos e avaliadores qualificados, considerando os eixos empreendedor, tecnologia, capital, mercado e gestão.'),
    ((SELECT PRC_COD FROM PROCESSOS_METODOLOGIA WHERE NOME = 'Seleção'), 3,
        'Contratação',
        'Estabelecer condições e dar transparência ao relacionamento entre empreendedores e incubadora, definindo direitos e deveres das partes.'),
    -- 3. Desenvolvimento do Empreendimento
    ((SELECT PRC_COD FROM PROCESSOS_METODOLOGIA WHERE NOME = 'Desenvolvimento do Empreendimento'), 1,
        'Planejamento do Empreendimento',
        'Elaborar o plano de desenvolvimento do empreendimento, definindo ações para que ele se desenvolva no menor tempo possível nos cinco eixos.'),
    ((SELECT PRC_COD FROM PROCESSOS_METODOLOGIA WHERE NOME = 'Desenvolvimento do Empreendimento'), 2,
        'Agregação de Valor',
        'Promover o desenvolvimento global dos empreendimentos por meio de qualificações, consultorias, mentorias, participação em feiras/eventos e uso de laboratórios.'),
    ((SELECT PRC_COD FROM PROCESSOS_METODOLOGIA WHERE NOME = 'Desenvolvimento do Empreendimento'), 3,
        'Monitoramento do Empreendimento',
        'Acompanhar o desenvolvimento do empreendimento para identificar desvios em relação ao planejado e definir o momento de graduação.'),
    -- 4. Graduação e Relacionamento com Graduados
    ((SELECT PRC_COD FROM PROCESSOS_METODOLOGIA WHERE NOME = 'Graduação e Relacionamento com Graduados'), 1,
        'Graduação',
        'Garantir que a saída do empreendimento seja bem-sucedida, com a mudança de status de incubado para graduado.'),
    ((SELECT PRC_COD FROM PROCESSOS_METODOLOGIA WHERE NOME = 'Graduação e Relacionamento com Graduados'), 2,
        'Relacionamento com Graduados',
        'Manter vínculos com os graduados, monitorando a evolução e prestando serviços de valor agregado.'),
    -- 5. Gerenciamento Básico
    ((SELECT PRC_COD FROM PROCESSOS_METODOLOGIA WHERE NOME = 'Gerenciamento Básico'), 1,
        'Estrutura Organizacional',
        'Viabilizar o funcionamento da incubadora com os documentos que comprovem sua existência e uma equipe adequada à sua operacionalização.'),
    ((SELECT PRC_COD FROM PROCESSOS_METODOLOGIA WHERE NOME = 'Gerenciamento Básico'), 2,
        'Operação da Incubadora',
        'Viabilizar os recursos físicos e financeiros para a operação da incubadora: gestão financeira, infraestrutura física/tecnológica e serviços operacionais.'),
    ((SELECT PRC_COD FROM PROCESSOS_METODOLOGIA WHERE NOME = 'Gerenciamento Básico'), 3,
        'Comunicação e Marketing',
        'Divulgar a incubadora e os empreendimentos junto à comunidade, fortalecendo sua imagem e visibilidade.');

-- ----------------------------------------------------------------------------
-- Indicadores (Métricas) por prática (a prática pai é resolvida por NOME)
-- ----------------------------------------------------------------------------
INSERT INTO INDICADORES_METODOLOGIA (PRT_COD, NOME, UNIDADE, PERIODICIDADE) VALUES
    -- Sensibilização
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Sensibilização'),
        'Número de pessoas sensibilizadas', 'unidade', 'MENSAL'),
    -- Prospecção
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Prospecção'),
        'Número de oportunidades prospectadas', 'unidade', 'MENSAL'),
    -- Qualificação de Potenciais Empreendedores
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Qualificação de Potenciais Empreendedores'),
        'Número de potenciais empreendedores qualificados', 'unidade', 'MENSAL'),
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Qualificação de Potenciais Empreendedores'),
        'Número de ações de qualificação realizadas', 'unidade', 'MENSAL'),
    -- Recepção de Propostas
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Recepção de Propostas'),
        'Número de propostas recebidas', 'unidade', 'MENSAL'),
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Recepção de Propostas'),
        'Número de propostas recebidas da região', 'unidade', 'MENSAL'),
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Recepção de Propostas'),
        'Número de propostas recebidas fora da região', 'unidade', 'MENSAL'),
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Recepção de Propostas'),
        'Número de propostas recebidas da UFES', 'unidade', 'MENSAL'),
    -- Avaliação
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Avaliação'),
        'Número de empreendimentos selecionados em cada processo de seleção', 'unidade', 'MENSAL'),
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Avaliação'),
        'Percentual de empreendimentos selecionados em relação ao total de propostas submetidas', '%', 'MENSAL'),
    -- Contratação
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Contratação'),
        'Número de empreendimentos com contratos assinados', 'unidade', 'MENSAL'),
    -- Planejamento do Empreendimento
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Planejamento do Empreendimento'),
        'Percentual de empreendimentos com plano de desenvolvimento atualizado', '%', 'MENSAL'),
    -- Agregação de Valor
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Agregação de Valor'),
        'Número de serviços oferecidos', 'unidade', 'MENSAL'),
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Agregação de Valor'),
        'Número de horas de consultoria oferecidas', 'horas', 'MENSAL'),
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Agregação de Valor'),
        'Número de horas de capacitação oferecidas', 'horas', 'MENSAL'),
    -- Monitoramento do Empreendimento
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Monitoramento do Empreendimento'),
        'Número de empregos gerados pelos empreendimentos incubados', 'unidade', 'MENSAL'),
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Monitoramento do Empreendimento'),
        'Percentual dos empreendimentos que não concluem o processo de incubação', '%', 'MENSAL'),
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Monitoramento do Empreendimento'),
        'Percentual de empreendimentos de alto impacto', '%', 'MENSAL'),
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Monitoramento do Empreendimento'),
        'Faturamento total dos empreendimentos incubados', 'R$', 'MENSAL'),
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Monitoramento do Empreendimento'),
        'Total de impostos gerados pelos empreendimentos incubados', 'R$', 'MENSAL'),
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Monitoramento do Empreendimento'),
        'Investimentos recebidos pelos empreendimentos incubados', 'R$', 'MENSAL'),
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Monitoramento do Empreendimento'),
        'Investimento-anjo recebido pelos empreendimentos incubados', 'R$', 'MENSAL'),
    -- Graduação
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Graduação'),
        'Número de graduadas', 'unidade', 'MENSAL'),
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Graduação'),
        'Percentual das graduadas que se estabeleceram na região', '%', 'MENSAL'),
    -- Relacionamento com Graduados
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Relacionamento com Graduados'),
        'Percentual das graduadas classificadas como de alto impacto', '%', 'MENSAL'),
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Relacionamento com Graduados'),
        'Percentual das graduadas classificadas como de alto crescimento', '%', 'MENSAL'),
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Relacionamento com Graduados'),
        'Percentual de graduadas que permanecem no mercado após 1 ano de graduação', '%', 'MENSAL'),
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Relacionamento com Graduados'),
        'Percentual de graduadas que permanecem no mercado após 5 anos de graduação', '%', 'MENSAL'),
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Relacionamento com Graduados'),
        'Número de empregos gerados pelas graduadas', 'unidade', 'MENSAL'),
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Relacionamento com Graduados'),
        'Faturamento total das graduadas', 'R$', 'MENSAL'),
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Relacionamento com Graduados'),
        'Total de impostos gerados pelas graduadas', 'R$', 'MENSAL'),
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Relacionamento com Graduados'),
        'Investimentos recebidos pelas graduadas', 'R$', 'MENSAL'),
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Relacionamento com Graduados'),
        'Percentual de graduadas adquiridas por outras empresas', '%', 'MENSAL'),
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Relacionamento com Graduados'),
        'Percentual de graduadas que realizaram fusão com outras empresas', '%', 'MENSAL'),
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Relacionamento com Graduados'),
        'Percentual das graduadas que mantêm interação com a incubadora', '%', 'MENSAL'),
    -- Estrutura Organizacional
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Estrutura Organizacional'),
        'Número de parceiros regionais, nacionais e internacionais', 'unidade', 'MENSAL'),
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Estrutura Organizacional'),
        'Número de eventos organizados pela incubadora ou dos quais participou da organização', 'unidade', 'MENSAL'),
    -- Operação da Incubadora
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Operação da Incubadora'),
        'Total de receita gerada pela incubadora (econômica e financeira)', 'R$', 'MENSAL'),
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Operação da Incubadora'),
        'Quantidade de recursos captados pela incubadora em instituições públicas e privadas', 'R$', 'MENSAL'),
    -- Comunicação e Marketing
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Comunicação e Marketing'),
        'Número de pessoas alcançadas pelas ações de marketing', 'unidade', 'MENSAL');

-- ----------------------------------------------------------------------------
-- Agrupamentos (sub-planos/"Meios/Ações" do Manual) por prática. Cada sub-plano
-- é um fluxo próprio; as atividades (seed seguinte) ligam-se ao agrupamento por
-- (PRT_COD, NOME). Práticas de fluxo único têm 1 agrupamento.
-- ----------------------------------------------------------------------------
INSERT INTO AGRUPAMENTOS (PRT_COD, ORDEM, NOME) VALUES
    -- Sensibilização
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Sensibilização'), 1, 'Participação em eventos externos'),
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Sensibilização'), 2, 'Participação em eventos internos'),
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Sensibilização'), 3, 'Participação em programas de rádio e televisão'),
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Sensibilização'), 4, 'Divulgação em mídias sociais'),
    -- Prospecção
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Prospecção'), 1, 'Participação nas bancas do projetão'),
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Prospecção'), 2, 'Reuniões com pesquisadores da UFES'),
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Prospecção'), 3, 'Reuniões com os setores público e privado'),
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Prospecção'), 4, 'Curadoria em editais e chamadas'),
    -- Qualificação de Potenciais Empreendedores
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Qualificação de Potenciais Empreendedores'), 1, 'Participação nas disciplinas da universidade'),
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Qualificação de Potenciais Empreendedores'), 2, 'Participação em eventos internos'),
    -- Recepção de Propostas
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Recepção de Propostas'), 1, 'Edital com formulário online de submissão de propostas'),
    -- Avaliação
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Avaliação'), 1, 'Avaliação em duas etapas'),
    -- Contratação
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Contratação'), 1, 'Assinatura do contrato de incubação'),
    -- Planejamento do Empreendimento
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Planejamento do Empreendimento'), 1, 'Planejamento do desenvolvimento do empreendimento'),
    -- Agregação de Valor
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Agregação de Valor'), 1, 'Ações de agregação de valor'),
    -- Monitoramento do Empreendimento
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Monitoramento do Empreendimento'), 1, 'Monitoramento com instrumento próprio'),
    -- Graduação
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Graduação'), 1, 'Evento de graduação'),
    -- Relacionamento com Graduados
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Relacionamento com Graduados'), 1, 'Acompanhamento da evolução do desempenho dos graduados'),
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Relacionamento com Graduados'), 2, 'Oferta de serviços especializados para graduados'),
    -- Estrutura Organizacional
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Estrutura Organizacional'), 1, 'Instrumentos jurídicos e equipe de gestão'),
    -- Operação da Incubadora
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Operação da Incubadora'), 1, 'Gestão financeira da incubadora'),
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Operação da Incubadora'), 2, 'Gestão da infraestrutura física e tecnológica'),
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Operação da Incubadora'), 3, 'Disponibilização de serviços operacionais'),
    -- Comunicação e Marketing
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Comunicação e Marketing'), 1, 'Material de comunicação impresso e digital'),
    ((SELECT PRT_COD FROM PRATICAS_METODOLOGIA WHERE NOME = 'Comunicação e Marketing'), 2, 'Estratégia de presença digital da incubadora');
