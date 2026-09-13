# Pruebas de integración (H2)

Archivo: [`RegistryIT.java`](../blob/master/registraduria/src/test/java/edu/unisabana/tyvs/registry/application/usecase/RegistryIT.java)

Prueban la interacción real entre el caso de uso
[`Registry`](../blob/master/registraduria/src/main/java/edu/unisabana/tyvs/registry/application/usecase/Registry.java)
y el adaptador
[`RegistryRepository`](../blob/master/registraduria/src/main/java/edu/unisabana/tyvs/registry/infrastructure/persistence/RegistryRepository.java)
contra una base de datos H2 real en memoria. No hay mocks: cada aserción se
apoya en una consulta real a la tabla `registry`.

## Casos cubiertos (formato AAA)

| Método | Escenario | Verifica también persistencia real |
|---|---|---|
| `shouldRegisterValidPerson` | Persona válida | `repo.existsById(100)` → `true` |
| `shouldPersistValidVoterAndRejectDuplicates` | Duplicado por id | segundo intento → `DUPLICATED` |
| `shouldRejectUnderagePersonAndNotPersistIt` | Menor de edad (17) | `repo.existsById(200)` → `false` |
| `shouldRejectImpossibleAgeAndNotPersistIt` | Edad negativa (-1) | `repo.existsById(201)` → `false` |
| `shouldRejectAgeAboveBiologicalMaximum` | Edad > 120 (121) | `repo.existsById(202)` → `false` |
| `shouldRejectDeadPersonAndNotPersistIt` | Persona fallecida | `repo.existsById(203)` → `false` |
| `shouldRejectNonPositiveIdAndNotPersistIt` | Id = 0 | `repo.existsById(0)` → `false` |

Cada prueba usa su propia base H2 (`regdb_usecase_it`), y `@Before` la limpia
(`initSchema()` + `deleteAll()`) antes de cada método, así que el orden de
ejecución no afecta el resultado.

## Por qué se afirma más que el valor de retorno

Para los casos de rechazo (menor, edad imposible, fallecido, id inválido) no
basta con comprobar el `RegisterResult` devuelto: también se confirma con una
consulta real que **no quedó ningún registro** en la base de datos. Eso es lo
que distingue a una prueba de integración de una unitaria con mock: aquí se
verifica el efecto persistido, no solo la llamada al colaborador.

## Evidencia de ejecución

> Reemplace esta sección con una captura de `mvn clean verify` mostrando
> `RegistryIT` en verde (7/7 pruebas), tomada al ejecutar el proyecto.
