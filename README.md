# Como rodar a aplicação

**Stack:** backend Spring Boot 4 / Java 21 (Maven) · frontend Angular 21 · banco PostgreSQL ·
arquivos em MinIO (compatível com S3).

O backend já vem configurado (`application.yaml`) para o ambiente local:

- Banco: `localhost:5432`, base `cerne`, usuário `admin`, senha `admin`
- MinIO: `localhost:9000`, bucket `cerne-documentos`, chaves `minioadmin` / `minioadmin123`

Basta a infra subir com essas credenciais — nenhuma configuração precisa ser alterada.

---

## Pré-requisitos

- **JDK 21**
- **Node.js 22** (LTS) + npm
- **Docker** (usado só para subir Postgres e MinIO)
- IntelliJ IDEA (opcional, para rodar o backend)

---

## Passo 1 — Subir o banco e o MinIO (Docker)

**PostgreSQL:**

```bash
docker run -d --name cerne-postgres \
  -e POSTGRES_DB=cerne -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=admin \
  -p 5432:5432 postgres:16
```

**MinIO:**

```bash
docker run -d --name cerne-minio \
  -e MINIO_ROOT_USER=minioadmin -e MINIO_ROOT_PASSWORD=minioadmin123 \
  -p 9000:9000 -p 9001:9001 minio/minio server /data --console-address ":9001"
```

---

## Passo 2 — Preparar banco e bucket (uma vez)

**Criar as tabelas do schema `public`** (tabelas compartilhadas, ex.: contas de login).
Na raiz do projeto:

```bash
docker exec -i cerne-postgres psql -U admin -d cerne \
  < backend/src/main/resources/sql/create_public_schema_tables.sql
```

**Criar o bucket dos arquivos:** abrir o painel do MinIO em `http://localhost:9001`
(login `minioadmin` / `minioadmin123`), ir em *Buckets → Create Bucket* e criar
`cerne-documentos`.

---

## Passo 3 — Rodar o backend (porta 8080)

Pelo IntelliJ: abrir a pasta `backend/` e executar a classe `CerneApplication`.

Ou pelo terminal:

```bash
cd backend
./mvnw spring-boot:run
```

O backend sobe em `http://localhost:8080`. No primeiro start em ambiente local, ele **cria
automaticamente uma incubadora de teste** ("Espaço Empreendedor") e os usuários abaixo.

---

## Passo 4 — Rodar o frontend (porta 4200)

```bash
cd frontend
npm install
npm start
```

O `npm start` (`ng serve`) sobe em `http://localhost:4200` e já aponta para a API em
`http://localhost:8080` (ambiente de desenvolvimento).

---

## Passo 5 — Acessar

Abrir `http://localhost:4200` e entrar com um dos usuários criados pelo seed (senha `cerne123`):

| Perfil | E-mail | Uso |
|---|---|---|
| Incubadora "Espaço Empreendedor" | `admin@espacoempreendedor.com` | Testar o sistema da incubadora |
| Administrador da plataforma | `admin@cerne.local` | Área de administração (criar outras incubadoras/usuários) |

---

## Parar / limpar

```bash
docker stop cerne-postgres cerne-minio     # pausar
docker start cerne-postgres cerne-minio    # retomar
docker rm -f cerne-postgres cerne-minio    # remover (apaga os dados locais)
```

---

## Observações

- O backend valida em IntelliJ; o `./mvnw` é alternativa via terminal.
- Ver logs de infra: `docker logs -f cerne-postgres` (ou `cerne-minio`).
- Este `README.md` está na raiz do projeto, que não é versionada (ver `.gitignore`).
