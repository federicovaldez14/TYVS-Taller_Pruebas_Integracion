# Conclusiones técnicas

- **Ninguna técnica reemplaza a las demás.** Los mocks verifican colaboración
  (¿se llamó `save()`?), H2 verifica persistencia real bajo un motor
  aproximado, PostgreSQL vía Testcontainers verifica fidelidad total al motor
  de producción, las pruebas de sistema verifican el contrato HTTP expuesto,
  y Pact verifica que ese contrato siga siendo el que otro equipo espera.
  Cada una cubre una frontera distinta; usar solo una dejaría huecos reales.

- **La arquitectura limpia es lo que hace posible probar por capas.** Que
  `Registry` dependa de una interfaz (`RegistryRepositoryPort`) y no de JDBC
  directamente es lo único que permite ejecutar exactamente el mismo caso de
  uso con un mock, con H2 y con PostgreSQL sin tocar su código.

- **Una prueba en verde no siempre significa "sin defectos".** Se comprobó
  con el ejercicio de Pact que una prueba de sistema puede seguir en verde
  después de romper el contrato con otro servicio, si se actualiza junto con
  el código que rompió el contrato. Solo una prueba que no comparte autoría
  con el cambio (el pacto, generado por el consumidor) lo detecta.

- **La convención de nombres (`*Test` vs. `*IT`) no es un detalle de estilo.**
  Determina qué runner ejecuta cada prueba y, por lo tanto, qué tan rápido y
  confiable es el ciclo de desarrollo diario.

- **JaCoCo con dos ejecuciones (Surefire + Failsafe) es indispensable** para
  que la cobertura de las pruebas `*IT` cuente en el reporte global; de lo
  contrario, buena parte del código de infraestructura y delivery aparecería
  como no cubierto pese a estar probado por integración.
