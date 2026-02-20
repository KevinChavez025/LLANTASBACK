# 🛞 Haida Inversiones Llantas — Backend API

Backend REST para e-commerce de llantas. Construido con **Spring Boot 4** y **PostgreSQL**.

## 🚀 Stack

| Tecnología | Versión |
|---|---|
| Java | 22 |
| Spring Boot | 4.0.2 |
| PostgreSQL | 18 |
| Hibernate ORM | 7.2 |
| JWT (jjwt) | 0.11.5 |
| Swagger / OpenAPI | 3 |

---

## ⚙️ Configuración

### 1. Requisitos previos
- Java 22+
- PostgreSQL 15+ corriendo localmente
- Maven 3.9+

### 2. Base de datos
Crear la base de datos en PostgreSQL:
```sql
CREATE DATABASE haidainversiones_llantas;
```

Luego ejecutar el schema completo en pgAdmin o psql:
```
src/main/resources/schema.sql
```

### 3. Variables de entorno (opcional en desarrollo)
El proyecto tiene valores por defecto para desarrollo. Para producción, configurar:

| Variable | Descripción | Default (dev) |
|---|---|---|
| `DB_URL` | URL de conexión PostgreSQL | `jdbc:postgresql://localhost:5432/haidainversiones_llantas` |
| `DB_USERNAME` | Usuario de BD | `postgres` |
| `DB_PASSWORD` | Contraseña de BD | `llantas123` |
| `JWT_SECRET` | Secret para firmar tokens JWT (mín. 32 chars) | Secret por defecto |
| `JWT_EXPIRATION` | Expiración del token en ms | `86400000` (24h) |
| `FRONTEND_URL` | URL del frontend para CORS | `http://localhost:4200` |
| `COSTO_ENVIO` | Costo de envío en soles | `15.00` |

Copiar `.env.example` como `.env` y completar los valores reales.

### 4. Correr el proyecto
```bash
./mvnw spring-boot:run
```

O desde IntelliJ: correr `HaidainversionesLlantasApplication.java`

---

## 📋 Endpoints principales

La documentación completa está disponible en Swagger:
```
http://localhost:8080/swagger-ui/index.html
```

| Método | Endpoint | Acceso | Descripción |
|---|---|---|---|
| POST | `/api/auth/register` | Público | Registro de usuario |
| POST | `/api/auth/login` | Público | Login, retorna JWT |
| GET | `/api/productos` | Público | Listar productos (paginado) |
| GET | `/api/productos/{id}` | Público | Detalle de producto |
| POST | `/api/productos` | ADMIN | Crear producto |
| PUT | `/api/productos/{id}` | ADMIN | Actualizar producto |
| DELETE | `/api/productos/{id}` | ADMIN | Desactivar producto |
| GET | `/api/carrito` | Público | Ver carrito |
| POST | `/api/carrito` | Público | Agregar al carrito |
| POST | `/api/pedidos` | Público | Crear pedido |
| GET | `/api/pedidos` | ADMIN | Listar todos los pedidos |
| GET | `/api/pedidos/usuario/{id}` | Auth | Pedidos del usuario |

---

## 🔐 Autenticación

El API usa **JWT Bearer Token**. Después de hacer login, incluir el token en el header:

```
Authorization: Bearer <token>
```

---

## 🏗️ Estructura del proyecto

```
src/main/java/.../
├── controller/      # Endpoints REST
├── service/         # Lógica de negocio
├── repository/      # Acceso a datos (JPA)
├── entity/          # Entidades JPA
├── dto/             # Request/Response objects
├── enums/           # EstadoPedido, EstadoPago, MetodoPago
├── converter/       # JPA AttributeConverters para enums
├── security/        # JWT + Spring Security
└── exception/       # Manejo global de errores
```

---

## ✅ Características de seguridad

- **Doble submit protection** — Idempotency keys evitan pedidos duplicados
- **Lock pesimista** — `SELECT FOR UPDATE` previene overselling concurrente
- **Stock automático** — Se descuenta al confirmar pedido y se devuelve al cancelar
- **Soft delete** — Los productos no se borran físicamente para preservar historial
- **Roles** — `USER`, `VENDEDOR`, `ADMIN` con control de acceso por endpoint
- **BCrypt** — Contraseñas hasheadas

---

## 📦 Métodos de pago aceptados

`EFECTIVO` · `YAPE` · `PLIN` · `TRANSFERENCIA` · `TARJETA_CREDITO` · `TARJETA_DEBITO`
