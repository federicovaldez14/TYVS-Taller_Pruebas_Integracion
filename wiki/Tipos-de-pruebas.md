# Tipos de pruebas

| | Unitaria | Integración | Sistema |
|---|---|---|---|
| **Qué verifica** | Una clase o método aislado | La interacción real entre 2+ componentes (p. ej. `Registry` + `RegistryRepository`) | El comportamiento del sistema completo, visto desde afuera (HTTP) |
| **Colaboradores** | Mocks/stubs de todo lo externo | Al menos un colaborador real (BD, otro servicio) | Ninguno simulado: la app completa arriba |
| **Ejemplo en el proyecto** | `RegistryWithMockTest` (mock del puerto) | `RegistryIT` (H2 real), `RegistryRepositoryPostgresIT` (PostgreSQL real) | `RegistryControllerIT` (HTTP de punta a punta) |
| **Velocidad** | Milisegundos | Décimas de segundo (H2) a segundos (Testcontainers) | Segundos (levanta Spring Boot completo) |
| **Detecta** | Errores de lógica de negocio | Errores de SQL, esquema, tipos, transacciones | Errores de serialización, códigos HTTP, cableado de Spring |
| **No detecta** | Nada sobre infraestructura real | Rupturas de contrato con otros servicios | Nada sobre servicios externos reales (usa su propia BD embebida) |
| **Convención de nombre** | `*Test.java` → Surefire (`mvn test`) | `*IT.java` → Failsafe (`mvn verify`) | `*IT.java` → Failsafe (`mvn verify`) |

## Por qué la convención de nombres no es cosmética

`mvn test` solo ejecuta `*Test.java`. Una prueba que abre una base de datos real
nunca debe llamarse `*Test`, porque entraría al ciclo rápido y lo haría lento y
frágil. Por eso `RegistryIT`, `RegistryControllerIT` y
`RegistryRepositoryPostgresIT` terminan en `IT`: solo corren con `mvn verify`.

## Caso especial: Contract testing (Pact)

Pact no encaja limpiamente en la tabla anterior. No es una prueba de integración
clásica (no hay dos componentes reales conversando en el mismo proceso), ni una
prueba de sistema pura (el consumidor nunca se levanta). Es una categoría propia
que verifica un **acuerdo** entre dos servicios que se despliegan y prueban por
separado. Ver [Testcontainers y Pact](Testcontainers-y-Pact).
