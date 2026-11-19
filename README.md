# ms-warframe (proyecto único: categorías + ítems)

Spring Boot 4.0.6 · Java 21 · Maven · PostgreSQL (Neon).
Unifica los antiguos `ms-categorias` y `ms-items` en un solo proyecto, con la misma
estructura que `ms_usuarios` de NovaLogistics: `DTO/`, `assembler/`, `controller/`,
`model/`, `repository/`, `service/` (más `exception/` para el manejo de errores).

## Cómo ejecutar
1. Crear la base con `neon_warframe_db.sql`.
2. Variables de entorno (o valores locales por defecto): `DB_URL`, `DB_USER`, `DB_PASSWORD`, `PORT` (8080).
3. `./mvnw spring-boot:run`  (o `mvn spring-boot:run`)

## Endpoints
- `/api/v1/categorias`: GET, GET /{id}, POST, PUT /{id}, DELETE /{id}
- `/api/v1/items`: GET (?categoria&min&max&q), GET /{id}, POST, PUT /{id},
  PATCH /{id}/precio, POST /ajuste-masivo, DELETE /{id}

Las credenciales nunca van en el código: solo por variables de entorno.
