# Resultados y cobertura (JaCoCo)

## Cómo generar el reporte

```bash
cd registraduria
mvn clean verify
```

El `pom.xml` ya declara las cuatro ejecuciones necesarias de JaCoCo
(`prepare-agent`, `report`, `prepare-agent-integration`, `report-integration`),
así que `mvn verify` genera cobertura **combinada** de unitarias (Surefire) e
integración (Failsafe):

- `target/site/jacoco/index.html` → cobertura de pruebas unitarias
- `target/site/jacoco-it/index.html` → cobertura de pruebas de integración

> **Nota:** para que `RegistryRepositoryPostgresIT` corra (y sume a la
> cobertura), Docker debe estar activo. Sin Docker, esa clase se omite y el
> resto de la suite sigue en verde.

## Meta de la rúbrica

- Cobertura global ≥ **80%**
- Cobertura en `application` y `delivery` ≥ **70%**

## Plantilla de resultados

> Complete esta tabla y adjunte las capturas de `index.html` después de
> ejecutar `mvn clean verify` en su máquina (no se pudo ejecutar Maven en el
> entorno donde se preparó esta entrega, por no tener acceso a Maven Central).

| Paquete | % Instrucciones | % Ramas | Observación |
|---|---|---|---|
| `domain.model` | | | |
| `application.usecase` | | | |
| `application.port.out` | | | (interfaz, sin lógica que cubrir) |
| `infrastructure.persistence` | | | |
| `delivery.rest` | | | |
| `config` | | | |
| **Global** | | | |

## Clases difíciles de cubrir al 100% (y por qué)

- `RegistryApplication`: el método `main` de arranque de Spring Boot no se
  ejecuta durante las pruebas unitarias/de integración estándar.
- Ramas de manejo de errores muy específicas de JDBC (p. ej. fallos de
  conexión reales) que solo se disparan con mocks (`shouldWrapPersistenceFailure`)
  y no con H2, porque H2 casi nunca falla de esa forma en un entorno de prueba.
