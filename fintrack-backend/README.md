# Fintrack Backend

API de gestión financiera que permite registrar ingresos y gastos, administrar categorías, metas de ahorro y obtener resúmenes financieros protegidos con JWT.

---

## Requisitos

- Docker y Docker Compose (recomendado)
- Java 17 + Maven (solo si prefieres ejecutar sin contenedores)

---

## Levantar el proyecto

1. **Clonar** este repositorio.
2. **Arrancar con Docker Compose**
   ```bash
   docker compose up -d
   ```
3. **Verificar logs (opcional)**
   ```bash
   docker compose logs -f backend
   ```
4. La API quedará disponible en `http://localhost:8080`.

> Para detener el entorno: `docker compose down`. Si quieres recrear la base con los datos de `sql/init.sql`: `docker compose down -v` y luego `docker compose up -d`.

---

## Datos iniciales

Al levantar el stack por primera vez se cargan usuarios, categorías y metas de ejemplo:

| Usuario | Email                     | Password               |
|---------|---------------------------|------------------------|
| Juan    | `juan.perez@example.com`  | `hashed_password_123`  |
| María   | `maria.lopez@example.com` | `hashed_password_456`  |
| Carlos  | `carlos.ruiz@example.com` | `hashed_password_789`  |

---

## Endpoints destacados

### Reporte mensual en PDF

```bash
curl -L -o reporte.pdf http://localhost:8080/api/reports/monthly-transactions \
  -H "Authorization: Bearer $TOKEN"
```

Genera un PDF con el detalle de ingresos, gastos y totales del mes calendario actual para el usuario autenticado.

### Autenticación

```bash
curl -s http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"juan.perez@example.com","password":"hashed_password_123"}'
```
Respuesta: token JWT, datos básicos del usuario y un snapshot inicial.

### Resumen de dashboard (refresco rápido)

```bash
curl -s http://localhost:8080/api/dashboard/summary \
  -H "Authorization: Bearer $TOKEN"
```
Devuelve saldo actual, totales del mes y últimas 4 transacciones para el usuario autenticado.

### Transacciones

- Crear ingreso:
  ```bash
  curl -s -X POST http://localhost:8080/api/transactions/income \
    -H 'Content-Type: application/json' \
    -H "Authorization: Bearer $TOKEN" \
    -d '{"amount":3000,"categoryId":1,"description":"Salario"}'
  ```
- Listar ingresos (paginado):
  ```bash
  curl -s "http://localhost:8080/api/transactions/income?page=0&size=10" \
    -H "Authorization: Bearer $TOKEN"
  ```

### Categorías

- Crear categoría:
  ```bash
  curl -s -X POST http://localhost:8080/api/categories \
    -H 'Content-Type: application/json' \
    -H "Authorization: Bearer $TOKEN" \
    -d '{"name":"Gastos médicos"}'
  ```
- Listar categorías: `GET /api/categories`

### Metas de ahorro

```bash
# Crear meta
curl -s -X POST http://localhost:8080/api/goals \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"name":"Laptop","target":2500,"description":"Trabajo"}'

# Aportar dinero
curl -s -X POST http://localhost:8080/api/goals/1/contributions \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"amount":500}'

# Archivar meta (devuelve el ahorro al saldo)
curl -s -X POST http://localhost:8080/api/goals/1/archive \
  -H "Authorization: Bearer $TOKEN"
```

---

## Estructura principal

- `src/main/java/com/fintrack/fintrackbackend/controller`: controladores REST (auth, transacciones, categorías, metas, dashboard).
- `src/main/java/com/fintrack/fintrackbackend/service`: lógica de negocio (cálculo de saldos, validaciones).
- `src/main/java/com/fintrack/fintrackbackend/repository`: acceso a datos vía Spring Data JPA.
- `sql/init.sql`: creación del esquema MySQL y datos demo.

---

## Notas

- El token JWT debe enviarse en cada request protegido (`Authorization: Bearer <token>`).
- `currentBalance` representa saldo disponible después de considerar ingresos, gastos y dinero reservado en metas activas.
- Revisa `application.properties` para la configuración de base de datos si deseas correr sin Docker.

¡Listo! Ya puedes levantar y explorar la API con las pruebas `curl`. Si necesitas más detalles o automatizar pruebas, considera añadir una colección Postman o scripts en `/scripts`. 
