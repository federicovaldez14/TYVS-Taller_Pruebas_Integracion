# Arquitectura limpia

```
                         ┌───────────────────────────┐
                         │        delivery/rest       │  ← adaptador de entrada (REST)
                         │  RegistryController         │
                         │  RegistryExceptionHandler   │
                         └──────────────┬──────────────┘
                                        │ usa
                         ┌──────────────▼──────────────┐
                         │      application/usecase     │  ← reglas de la aplicación
                         │  Registry                     │
                         │  RegistryPersistenceException │
                         └──────────────┬──────────────┘
                                        │ depende del PUERTO (interfaz), no de JDBC
                         ┌──────────────▼──────────────┐
                         │    application/port/out      │  ← frontera hacia afuera
                         │  RegistryRepositoryPort       │
                         └──────────────┬──────────────┘
                                        │ implementado por
                         ┌──────────────▼──────────────┐
                         │   infrastructure/persistence │  ← adaptador de salida (JDBC)
                         │  RegistryRepository           │
                         │  RegistryRecord               │
                         └───────────────────────────────┘

                         ┌───────────────────────────────┐
                         │          domain/model          │  ← reglas de negocio puras,
                         │  Person, Gender, RegisterResult │    sin dependencias de Spring
                         └───────────────────────────────┘

                         ┌───────────────────────────────┐
                         │            config              │  ← composition root:
                         │  RegistryConfig                │    decide qué implementación
                         └───────────────────────────────┘    se inyecta en cada perfil
```

## Por qué esto facilita las pruebas

- **`Registry` no conoce Spring ni JDBC.** Solo depende de la interfaz
  `RegistryRepositoryPort`. Eso permite probarlo dos veces con el mismo código
  de producción: una vez con un mock (`RegistryWithMockTest`, prueba unitaria)
  y otra con H2 real (`RegistryIT`, prueba de integración).
- **El cableado vive en un solo lugar (`RegistryConfig`).** Las pruebas de
  sistema (`RegistryControllerIT`) no redefinen beans propios: usan el mismo
  cableado de producción, cambiando solo la URL JDBC vía
  `@TestPropertySource`, así que prueban el cableado real, no uno paralelo.
- **El adaptador de infraestructura (`RegistryRepository`) es intercambiable.**
  El mismo código corre contra H2 (`RegistryIT`) y contra PostgreSQL real en
  Docker (`RegistryRepositoryPostgresIT`) sin cambiar una línea del caso de
  uso.
- **Los errores de infraestructura se traducen en la frontera.** `Registry`
  convierte cualquier fallo del puerto en `RegistryPersistenceException`, y es
  `RegistryExceptionHandler` (capa `delivery`) quien decide el código HTTP.
  Ninguna capa interna conoce JDBC ni HTTP.
