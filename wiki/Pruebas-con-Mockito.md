# Pruebas con Mockito

Archivo: [`RegistryWithMockTest.java`](../blob/master/registraduria/src/test/java/edu/unisabana/tyvs/registry/application/usecase/RegistryWithMockTest.java)

Aíslan `Registry` de la infraestructura real usando un mock de
`RegistryRepositoryPort`. Son pruebas **unitarias** (por eso el sufijo
`Test`, no `IT`): si todos los colaboradores están simulados, no se está
integrando nada.

## Ejemplos de uso de la API de Mockito

**Simular una respuesta (`when...thenReturn`):**
```java
when(repo.existsById(7)).thenReturn(true);
```

**Verificar que un método SÍ se llamó, y con qué argumentos:**
```java
verify(repo, times(1)).save(8, "Luis", 30, true);
```

**Verificar que un método NUNCA se llamó:**
```java
verify(repo, never()).save(anyInt(), anyString(), anyInt(), anyBoolean());
```

**Verificar que no hubo ninguna interacción con el mock:**
```java
verifyNoInteractions(repo);
```

**Simular una excepción de infraestructura:**
```java
when(repo.existsById(9)).thenThrow(new java.sql.SQLException("conexión perdida"));
```

## Casos cubiertos

| Método | Qué comportamiento verifica |
|---|---|
| `shouldReturnDuplicatedWhenRepoSaysExists` | `DUPLICATED` y `save()` nunca invocado |
| `shouldSaveWhenPersonIsValid` | `VALID` y `save()` invocado exactamente una vez |
| `shouldWrapPersistenceFailure` | `SQLException` del puerto se traduce a `RegistryPersistenceException` |
| `shouldRejectUnderageWithoutTouchingRepository` | reglas de dominio que ni siquiera consultan el repositorio |
| `shouldReturnInvalidWhenPersonIsNull` / `...WhenIdIsNotPositive` | validación defensiva de entrada |
| `shouldReturnDeadWhenPersonIsNotAlive` | rechazo antes de evaluar edad |
| `shouldReturnInvalidAgeWhenAgeIsNegative` / `...ExceedsMaximum` | frontera de edades imposibles |
| `shouldReturnUnderageWhenAgeIsZero` / `shouldAcceptTheMaximumAge` | valores límite (0 y 120) |

## Unitaria (mock) vs. integración (H2): la diferencia que importa

| | Mock | H2 |
|---|---|---|
| Colaborador | Simulado | Real |
| Qué prueba | Que `Registry` **llamó** bien al puerto | Que los datos **quedaron** guardados |
| Detecta | Errores de lógica de negocio | Errores de SQL, esquema, tipos |
| No detecta | Que el `INSERT` esté mal escrito | — |

Un mock siempre responde lo que se le configuró, incluso si la base de datos
real haría otra cosa. Por eso el taller pide ambos tipos, no uno en lugar del
otro.
