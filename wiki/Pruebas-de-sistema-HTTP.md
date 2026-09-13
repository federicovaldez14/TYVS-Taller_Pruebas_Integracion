# Pruebas de sistema (HTTP)

Archivo: [`RegistryControllerIT.java`](../blob/master/registraduria/src/test/java/edu/unisabana/tyvs/registry/delivery/rest/RegistryControllerIT.java)

Levantan la aplicación Spring Boot **completa** en un puerto aleatorio
(`webEnvironment = RANDOM_PORT`) y la ejercitan por HTTP con
`TestRestTemplate`, como lo haría un cliente real. No conocen clases internas
(`Registry`, `Person`): solo el contrato público `/register`.

## Decisiones de diseño relevantes

1. **No define beans propios.** El cableado real vive en `RegistryConfig`; si
   la prueba lo duplicara, estaría probando su propio cableado y no el de
   producción.
2. **Base de datos propia** (`registry.jdbc-url=jdbc:h2:mem:regdb_ctrl_it`)
   para no compartir estado con las demás pruebas: Surefire/Failsafe
   reutilizan la JVM entre clases, y H2 con `DB_CLOSE_DELAY=-1` sobrevive
   mientras viva esa JVM.
3. **`RANDOM_PORT`** evita depender de un puerto fijo (8080) que podría estar
   ocupado.

## Casos cubiertos

| Método | Escenario | Código HTTP | Cuerpo esperado |
|---|---|---|---|
| `shouldRegisterValidPerson` | Registro válido | `200 OK` | `VALID` |
| `shouldReturnDuplicatedWhenIdAlreadyRegistered` | Id ya registrado | `200 OK` | `DUPLICATED` |
| `shouldReturnUnderageWhenPersonIsMinor` | Edad 17 | `200 OK` | `UNDERAGE` |
| `shouldReturnDeadWhenPersonIsNotAlive` | `alive=false` | `200 OK` | `DEAD` |
| `shouldReturnBadRequestWhenGenderIsNotValid` | `gender="X"` (fuera del enum) | `400 Bad Request` | — |

## Manejo de errores del cliente

Antes de introducir `RegistryExceptionHandler`, un género inválido producía un
`IllegalArgumentException` sin capturar y Spring respondía `500 Internal
Server Error` — código incorrecto para un dato mal enviado por el cliente. El
`@RestControllerAdvice` traduce ese caso (y un JSON malformado) a `400 Bad
Request`, y reserva los códigos `5xx` para fallos reales de infraestructura
(`RegistryPersistenceException` → `503 Service Unavailable`).

## Evidencia de ejecución

> Reemplace esta sección con una captura de Postman o de la consola mostrando
> una petición `POST /register` real y su respuesta (200/400), y con la salida
> de `mvn verify` mostrando `RegistryControllerIT` en verde.
