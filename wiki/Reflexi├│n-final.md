# Reflexión final

### ¿Qué capas fueron más difíciles de probar y por qué?

La capa de infraestructura (`RegistryRepository`) fue la más costosa de
probar con fidelidad completa: H2 permite avanzar rápido, pero solo
Testcontainers reveló una divergencia real (el plegado de identificadores)
que ninguna prueba contra H2 podía detectar. Configurar Testcontainers
también exige más cuidado operativo (Docker corriendo, contenedor `static`,
`@EnabledIf` en vez de `assumeTrue` dentro de `@BeforeAll`) que las demás
capas.

### ¿Qué beneficios observas en usar mocks frente a H2 o base real?

Los mocks son inmediatos y deterministas: permiten forzar escenarios casi
imposibles de reproducir con infraestructura real, como una `SQLException` a
mitad de una operación. Su límite es justamente ese: un mock nunca puede
decirnos si el `INSERT` real está bien escrito, ni si el motor de producción
se comporta como se espera. H2 y PostgreSQL sí lo confirman, a costo de mayor
lentitud y, en el caso de PostgreSQL, de un prerrequisito de infraestructura
(Docker).

### ¿Cómo mejorarías el diseño de `RegistryController` o `RegistryRepository` para facilitar las pruebas automáticas?

- En `RegistryController`, se podría exponer explícitamente los códigos HTTP
  esperados por caso (`VALID`→200, `DUPLICATED`→409, `UNDERAGE`/`DEAD`/`INVALID_AGE`→422,
  por ejemplo) en vez de devolver siempre 200 con el nombre del resultado en
  texto plano. Eso simplificaría las aserciones en las pruebas de sistema
  (verificar el código HTTP, no solo el cuerpo) y sería más idiomático en una
  API REST.
- En `RegistryRepository`, separar la creación del esquema (`initSchema`) de
  las operaciones de negocio permitiría inyectar una estrategia de migración
  (Flyway/Liquibase) sin cambiar el resto del adaptador, y facilitaría probar
  el DDL de forma independiente del CRUD.

### ¿Qué aprendiste sobre integración continua (CI) al ejecutar tus pruebas con Maven y JaCoCo?

Que separar `mvn test` (rápido, sin infraestructura) de `mvn verify` (completo,
con BD e incluso Docker) es lo que hace viable tener un pipeline de CI útil:
un desarrollador puede correr `mvn test` en cada guardado, mientras que
`mvn verify` — más lento — se reserva para el pipeline antes de fusionar a la
rama principal. Sin esa separación, cualquier prueba con base de datos
volvería insoportable el ciclo rápido de desarrollo, y el equipo terminaría
ignorando o deshabilitando pruebas para no perder velocidad.
