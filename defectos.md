# Registro de Defectos — Taller de Pruebas de Integración y Sistema

---

## Formato 1: Lista detallada (narrativa)

### Defecto 01 — El mismo SQL no es portable entre H2 y PostgreSQL

- **Caso de prueba:** `RegistryRepositoryPostgresIT.shouldResolveQuotedLowercaseIdentifier()`
- **Entrada:** Se crea la tabla `registry` con la misma sentencia `CREATE TABLE` en H2 y en PostgreSQL (Testcontainers), y se ejecuta la consulta `SELECT "name" FROM registry WHERE id = 400` contra ambos motores.
- **Resultado esperado:** La consulta debería comportarse igual en ambos motores, ya que el esquema se crea con el mismo DDL.
- **Resultado obtenido:**
  - PostgreSQL: `OK`, devuelve `Ana`.
  - H2: falla con `Columna "name" no encontrada`.
- **Causa probable:** El estándar SQL no define a qué caja se pliegan los identificadores sin comillas. H2 los pliega a MAYÚSCULAS (la columna queda como `NAME`) y PostgreSQL los pliega a minúsculas (queda como `name`). Un identificador entrecomillado se toma literal, así que `"name"` solo coincide en PostgreSQL.
- **Tipo de prueba:** Integración con base de datos real (Testcontainers)
- **Estado:** **Cerrado — comportamiento documentado.** No es un bug del código propio, sino una divergencia real de motores. Se decidió **no usar identificadores entrecomillados en minúsculas** en el código de producción (`RegistryRepository` usa `SELECT id, name, age, is_alive ...` sin comillas), precisamente para evitar depender de un plegado de mayúsculas/minúsculas específico de un motor.
- **Evidencia:** log de ejecución de `mvn verify` con Docker activo, sección de `RegistryRepositoryPostgresIT`.
- **Prioridad:** Media (no bloqueante hoy, pero de alto impacto si alguien añade una consulta con identificadores entrecomillados sin conocer esta diferencia).

---

### Defecto 02 — Un cambio en el formato de respuesta del proveedor rompe al consumidor sin que la prueba de sistema lo detecte

- **Caso de prueba:** ejercicio guiado del taller ("Compruébelo usted mismo" de la sección de Pact).
- **Pasos para reproducir:**
  1. En `RegistryController.register(...)`, se cambió temporalmente `return r.name();` por `return "{\"resultado\":\"" + r.name() + "\"}";`.
  2. Se actualizó también la aserción de `RegistryControllerIT.shouldRegisterValidPerson()` para que esperara ese nuevo JSON.
  3. Se ejecutó `mvn clean verify`.
- **Resultado esperado:** Si el consumidor (`certificados`) sigue esperando texto plano (`"VALID"`), algún mecanismo de prueba debería detectar la ruptura del contrato antes de llegar a producción.
- **Resultado obtenido:**
  - `RegistryControllerIT` (prueba de sistema): **sigue en verde**, porque se actualizó junto con el código — verifica al proveedor contra sí mismo.
  - `RegistraduriaProviderPactIT` (verificación del pacto): **falla**, porque el pacto generado por el consumidor sigue exigiendo el cuerpo `"VALID"` en texto plano.
- **Causa probable:** Ninguna prueba interna del proveedor conoce las expectativas reales del consumidor; solo el contrato (pacto) las captura.
- **Tipo de prueba:** Contract testing (Pact) vs. prueba de sistema (HTTP)
- **Estado:** **Resuelto.** Se revirtió el cambio en `RegistryController` y se confirmó que `mvn clean verify` vuelve a estar en verde, incluida la verificación del pacto.
- **Evidencia:** salida de consola de Maven mostrando `Verifying a pact between certificados and registraduria ... FAILED` antes de revertir, y `PASSED` después.
- **Prioridad:** Alta — es exactamente el tipo de defecto que llega a producción cuando cada equipo solo prueba su propia parte.

---

## Formato 2: Tabla de defectos (bug tracking)

| ID | Caso de Prueba | Entrada | Resultado Esperado | Resultado Obtenido | Causa Probable | Estado |
|----|-----------------|---------|---------------------|----------------------|------------------|--------|
| 01 | `SELECT "name"` contra H2 y PostgreSQL | mismo DDL, mismo SELECT | comportamiento igual en ambos motores | falla en H2, funciona en PostgreSQL | plegado de identificadores no estandarizado por SQL | Cerrado — documentado |
| 02 | Cambiar el body de `/register` a JSON | proveedor cambia texto plano por JSON | alguna prueba detecta la ruptura de contrato | `RegistryControllerIT` no lo detecta; `RegistraduriaProviderPactIT` sí | pruebas de sistema verifican al proveedor contra sí mismo, no contra el consumidor real | Resuelto (revertido) |

---

## Convenciones de Estado

| Estado | Significado |
|--------|-------------|
| **Abierto** | El defecto fue detectado pero no corregido. |
| **En progreso** | El defecto se encuentra en análisis o corrección. |
| **Resuelto** | El defecto fue corregido y validado mediante pruebas. |
| **Cerrado — documentado** | No es un defecto de código, sino un comportamiento divergente que se decidió documentar y evitar por diseño. |
