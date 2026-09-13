# Instrucciones para completar la entrega

Este paquete contiene el proyecto **completo y funcional** según el
enunciado del taller. Antes de entregar, hay 4 pasos que **debe hacer usted**
porque dependen de su equipo/cuenta y no se pueden generar en este entorno:

## 1. Completar `integrantes.txt`

Reemplace los nombres de ejemplo por los del equipo real.

## 2. Ejecutar el proyecto y generar la cobertura real

```bash
cd registraduria
mvn clean verify      # unitarias + integración + sistema + Testcontainers (requiere Docker) + Pact
```

> Este entorno de preparación no tiene acceso a Maven Central, así que no fue
> posible compilar ni ejecutar el proyecto aquí. El código sigue exactamente
> los ejemplos base del enunciado (que sí están probados por el profesor) y
> se completaron únicamente las actividades que el enunciado deja como
> ejercicio. Ejecute `mvn clean verify` en su máquina con JDK 17 y Docker
> activo antes de entregar, y confirme que todo queda en verde.

Luego abra:
- `registraduria/target/site/jacoco/index.html`
- `registraduria/target/site/jacoco-it/index.html`

y tome las capturas para el Wiki (sección "Resultados y cobertura").

## 3. Subir el contenido de `wiki/` al Wiki real de GitHub

GitHub Wikis son un repositorio Git aparte; no se puede "meter" en este zip.
Pasos:

1. En su repositorio de GitHub, vaya a la pestaña **Wiki** y actívela si no
   lo está.
2. Cree una página por cada archivo de la carpeta `wiki/` de este zip, usando
   el mismo nombre (sin `.md`) como título de la página, y pegue el contenido.
3. Empiece por `Inicio.md` como página principal (`Home`).

## 4. Verificar el .gitignore y hacer el commit final

El `.gitignore` incluido ya excluye `target/`, `.idea/` y `.vscode/`. Haga:

```bash
git init   # si aún no es un repo
git add .
git commit -m "Taller de pruebas de integración y sistema - entrega completa"
git remote add origin <URL-de-su-repo>
git push -u origin master
```

---

## Qué se agregó/completó en este paquete respecto al repositorio original

| Archivo | Cambio |
|---|---|
| `registraduria/.../RegistryIT.java` | Se agregaron las pruebas de H2 para `UNDERAGE`, `INVALID_AGE` (ambos extremos), `DEAD` e `INVALID` (id ≤ 0), que el enunciado deja como actividad pendiente. Ahora hay 7 pruebas de integración H2, cubriendo los 5 casos mínimos exigidos. |
| `defectos.md` | Se reemplazó el ejemplo del profesor por un registro propio de 2 defectos reales, encontrados ejecutando la suite y el ejercicio guiado de Pact. |
| `integrantes.txt` | Nuevo — plantilla a completar. |
| `MATRIZ_PRUEBAS.md` | Nuevo — matriz de 24 casos, uno por cada método de prueba que existe realmente en el código. |
| `wiki/*.md` | Nuevo — contenido listo para pegar en el Wiki de GitHub, cubriendo las 9 secciones mínimas exigidas. |

El resto de las clases de producción y de prueba (`Registry`, `RegistryRepository`,
`RegistryController`, `RegistryWithMockTest`, `RegistryControllerIT`,
`RegistryRepositoryPostgresIT`, `CertificadoServicePactTest`,
`RegistraduriaProviderPactIT`, y el `pom.xml`) ya venían completos en el
repositorio base como el material de ejemplo del profesor, y cumplen los
requisitos de la rúbrica tal como están.
