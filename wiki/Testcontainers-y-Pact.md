# Bases de datos reales (Testcontainers) y Contract testing (Pact)

## Testcontainers

Archivo: [`RegistryRepositoryPostgresIT.java`](../blob/master/registraduria/src/test/java/edu/unisabana/tyvs/registry/infrastructure/persistence/RegistryRepositoryPostgresIT.java)

H2 es rápida pero no es la base de datos de producción: difiere en dialecto
SQL, tipos y plegado de identificadores. Testcontainers levanta un
**PostgreSQL real en Docker** desde la propia prueba, corre el mismo código
de producción contra él, y lo destruye al terminar.

- El contenedor es **`static`** (una instancia por clase, no por prueba):
  arrancar PostgreSQL cuesta segundos.
- Si Docker no está disponible, la clase completa se **omite** (`@EnabledIf`),
  no falla: un compañero sin Docker no debería ver el build en rojo por eso.

### Divergencia documentada entre motores

La misma sentencia `CREATE TABLE` y la misma consulta `SELECT "name" FROM
registry` se comportan distinto:

| Motor | Identificador sin comillas | `SELECT "name"` |
|---|---|---|
| H2 | se pliega a MAYÚSCULAS (`NAME`) | falla: columna no encontrada |
| PostgreSQL | se pliega a minúsculas (`name`) | funciona |

Ver el análisis completo en [`defectos.md`](../blob/master/defectos.md) (Defecto 01).

## Contract testing con Pact

- **Consumidor:** [`CertificadoServicePactTest.java`](../blob/master/registraduria/src/test/java/edu/unisabana/tyvs/certificados/CertificadoServicePactTest.java) — declara 2 interacciones (votante válido, votante duplicado) contra un servidor simulado, y genera `target/pacts/certificados-registraduria.json`.
- **Proveedor:** [`RegistraduriaProviderPactIT.java`](../blob/master/registraduria/src/test/java/edu/unisabana/tyvs/registry/delivery/rest/RegistraduriaProviderPactIT.java) — levanta la Registraduría completa y reproduce esas interacciones, montando el estado (`@State`) que cada una necesita.

### Por qué hace falta si ya hay mocks y pruebas de sistema

| Prueba | ¿Detecta un cambio en el formato de respuesta del proveedor? |
|---|---|
| Mock (`RegistryWithMockTest`) | No — el mock responde lo que se le configuró |
| H2 (`RegistryIT`) | No — no pasa por HTTP |
| Sistema (`RegistryControllerIT`) | No — verifica al proveedor contra sí mismo |
| **Pact** (`RegistraduriaProviderPactIT`) | **Sí** — verifica contra lo que el consumidor realmente espera |

Se demostró en la práctica (documentado como Defecto 02 en `defectos.md`):
cambiar el cuerpo de `/register` a JSON mantiene `RegistryControllerIT` en
verde, pero rompe la verificación del pacto.

## Evidencia de ejecución

> Reemplace esta sección con capturas de:
> - `mvn clean verify` con Docker corriendo, mostrando `RegistryRepositoryPostgresIT` en verde.
> - La salida de la verificación del pacto (`Verifying a pact between certificados and registraduria ... PASSED`).
