# Matriz de pruebas de integración — Registraduría

> Requisito #7 del taller. Cada fila corresponde a un método de prueba que existe
> realmente en el código del proyecto (`registraduria/src/test/...`).

| # | Caso | Entrada | Resultado esperado | Tipo | Test que lo valida |
|---|------|---------|---------------------|------|---------------------|
| 1 | Persona válida | id=100, edad=30, viva | `VALID` | H2 | `RegistryIT.shouldRegisterValidPerson()` |
| 2 | Persona duplicada | id=100 registrado dos veces | `VALID` luego `DUPLICATED` | H2 | `RegistryIT.shouldPersistValidVoterAndRejectDuplicates()` |
| 3 | Persona menor de edad | id=200, edad=17 | `UNDERAGE`, sin persistir | H2 | `RegistryIT.shouldRejectUnderagePersonAndNotPersistIt()` |
| 4 | Edad imposible (negativa) | id=201, edad=-1 | `INVALID_AGE`, sin persistir | H2 | `RegistryIT.shouldRejectImpossibleAgeAndNotPersistIt()` |
| 5 | Edad imposible (superior) | id=202, edad=121 | `INVALID_AGE`, sin persistir | H2 | `RegistryIT.shouldRejectAgeAboveBiologicalMaximum()` |
| 6 | Persona fallecida | id=203, alive=false | `DEAD`, sin persistir | H2 | `RegistryIT.shouldRejectDeadPersonAndNotPersistIt()` |
| 7 | ID inválido | id=0 | `INVALID`, sin persistir | H2 | `RegistryIT.shouldRejectNonPositiveIdAndNotPersistIt()` |
| 8 | Duplicado sin base de datos | mock indica que el id existe | `DUPLICATED`, `save()` nunca invocado | Mock | `RegistryWithMockTest.shouldReturnDuplicatedWhenRepoSaysExists()` |
| 9 | Registro exitoso con mock | mock indica que el id no existe | `VALID`, `save()` invocado 1 vez | Mock | `RegistryWithMockTest.shouldSaveWhenPersonIsValid()` |
| 10 | Fallo de persistencia | el puerto lanza `SQLException` | `RegistryPersistenceException` | Mock | `RegistryWithMockTest.shouldWrapPersistenceFailure()` |
| 11 | Menor sin tocar repositorio | edad=17 | `UNDERAGE`, `verifyNoInteractions(repo)` | Mock | `RegistryWithMockTest.shouldRejectUnderageWithoutTouchingRepository()` |
| 12 | Persona nula | `null` | `INVALID` | Mock | `RegistryWithMockTest.shouldReturnInvalidWhenPersonIsNull()` |
| 13 | Valor límite: edad 0 | edad=0 | `UNDERAGE` | Mock | `RegistryWithMockTest.shouldReturnUnderageWhenAgeIsZero()` |
| 14 | Valor límite: edad máxima (120) | edad=120 | `VALID` | Mock | `RegistryWithMockTest.shouldAcceptTheMaximumAge()` |
| 15 | Registro exitoso vía HTTP | JSON válido | `200 OK`, body `VALID` | HTTP | `RegistryControllerIT.shouldRegisterValidPerson()` |
| 16 | Duplicado vía HTTP | mismo id dos veces | `200 OK`, body `DUPLICATED` | HTTP | `RegistryControllerIT.shouldReturnDuplicatedWhenIdAlreadyRegistered()` |
| 17 | Menor de edad vía HTTP | edad=17 | `200 OK`, body `UNDERAGE` | HTTP | `RegistryControllerIT.shouldReturnUnderageWhenPersonIsMinor()` |
| 18 | Fallecido vía HTTP | alive=false | `200 OK`, body `DEAD` | HTTP | `RegistryControllerIT.shouldReturnDeadWhenPersonIsNotAlive()` |
| 19 | Género inválido vía HTTP | gender="X" | `400 Bad Request` | HTTP | `RegistryControllerIT.shouldReturnBadRequestWhenGenderIsNotValid()` |
| 20 | Persistencia y duplicado en PostgreSQL real | id=100 dos veces | `VALID` luego `DUPLICATED` | Testcontainers | `RegistryRepositoryPostgresIT.shouldPersistAndRejectDuplicate()` |
| 21 | Round-trip de datos en PostgreSQL | id=200 | los mismos datos al leer | Testcontainers | `RegistryRepositoryPostgresIT.shouldRoundTripRecord()` |
| 22 | Dialecto SQL divergente H2 vs PostgreSQL | `SELECT "name"` | resuelve en PostgreSQL, falla en H2 | Testcontainers | `RegistryRepositoryPostgresIT.shouldResolveQuotedLowercaseIdentifier()` |
| 23 | Contrato consumidor válido | pacto `certificados` → votante válido | interacción verificada | Pact | `RegistraduriaProviderPactIT.verificarPacto()` (estado `sinVotante900`) |
| 24 | Contrato consumidor duplicado | pacto `certificados` → votante duplicado | interacción verificada | Pact | `RegistraduriaProviderPactIT.verificarPacto()` (estado `conVotante901`) |

**Cobertura de los "casos mínimos" exigidos por el enunciado (sección 3):**
válida (#1), duplicada (#2), menor de edad (#3), edad imposible (#4-5), fallecida (#6) — las 5 quedan cubiertas con BD H2 real, sin mocks.
