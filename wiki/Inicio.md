# Inicio

## Dominio del sistema

Este proyecto simula el proceso de **registro de votantes de una Registraduría**.
Un ciudadano se registra enviando sus datos (nombre, documento, edad, género, si está vivo)
y el sistema decide si el registro es válido, o lo rechaza indicando la razón: menor de edad,
edad imposible, persona fallecida, identificador inválido o documento duplicado.

Alrededor de ese caso de uso central (`Registry`) se agregó un segundo servicio,
**Certificados**, que consume el resultado de la Registraduría por HTTP: si el
votante quedó `VALID`, emite un certificado electoral (`CERT-<id>`).

## Propósito del taller

Aplicar y comparar distintas estrategias de **pruebas de integración y de sistema**
sobre una aplicación con arquitectura limpia:

- Pruebas de integración con base de datos real en memoria (**H2**).
- Dobles de prueba (**Mockito**) para aislar el caso de uso de la infraestructura.
- Bases de datos reales en contenedor (**Testcontainers** + PostgreSQL).
- **Contract testing** entre servicios (**Pact**) para detectar rupturas de contrato
  que ni los mocks ni las pruebas de sistema del proveedor pueden ver.
- Pruebas de sistema de caja negra vía HTTP (**TestRestTemplate**).
- Medición de cobertura combinada (unitarias + integración) con **JaCoCo**.

## Integrantes del equipo

Ver [`integrantes.txt`](../blob/master/integrantes.txt) en la raíz del repositorio.

## Cómo navegar este Wiki

| Página | Contenido |
|---|---|
| [Tipos de pruebas](Tipos-de-pruebas) | Diferencia entre pruebas unitarias, de integración y de sistema |
| [Arquitectura limpia](Arquitectura-limpia) | Diagrama de capas y cómo se aíslan las dependencias |
| [Pruebas de integración](Pruebas-de-integración) | Cómo se prueba `Registry` + `RegistryRepository` con H2 |
| [Pruebas con Mockito](Pruebas-con-Mockito) | Ejemplos de `when`, `verify`, `never` |
| [Pruebas de sistema HTTP](Pruebas-de-sistema-HTTP) | Escenarios end-to-end contra `/register` |
| [Testcontainers y Pact](Testcontainers-y-Pact) | Bases de datos reales y contract testing |
| [Resultados y cobertura](Resultados-y-cobertura) | Reporte JaCoCo y análisis |
| [Conclusiones técnicas](Conclusiones-técnicas) | Aprendizajes del equipo |
| [Reflexión final](Reflexión-final) | Respuestas a las preguntas guía del taller |
