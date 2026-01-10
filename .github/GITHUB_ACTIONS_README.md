# GitHub Actions CI/CD - NovaCommerce

## 📋 Descripción General

Este documento describe el flujo de Integración Continua (CI) configurado para el monorepo **Nova Commerce** usando GitHub Actions.

---

## 🔄 Workflow: `ci.yml`

### Ubicación
```
.github/workflows/ci.yml
```

### Nombre del Workflow
```
NovaCommerce CI
```

---

## 🎯 Configuración de Triggers

El workflow se ejecuta automáticamente en los siguientes eventos:

### Push
- **Ramas:** `develop`, `main`
- **Evento:** Cada push a cualquiera de estas ramas

### Pull Request
- **Ramas:** `develop`, `main`
- **Evento:** Cada PR creado o actualizado contra estas ramas

**Ejemplo:**
```yaml
on:
  push:
    branches:
      - develop
      - main
  pull_request:
    branches:
      - develop
      - main
```

---

## 🏃 Pasos del Pipeline (10 pasos)

### 1️⃣ **Checkout del Repositorio**
```yaml
- name: Checkout repository
  uses: actions/checkout@v4
```
- Descarga el código fuente del repositorio
- Acceso completo al historial de git

### 2️⃣ **Configuración de Java 17**
```yaml
- name: Set up Java 17
  uses: actions/setup-java@v4
  with:
    java-version: '17'
    distribution: 'temurin'
```
- Instala OpenJDK 17 (distribución Temurin)
- Versión requerida por Spring Boot 3.4.3

### 3️⃣ **Cache de Dependencias Maven**
```yaml
- name: Cache Maven dependencies
  uses: actions/cache@v4
  with:
    path: ~/.m2/repository
    key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}
    restore-keys: |
      ${{ runner.os }}-maven-
```
- Almacena en caché las dependencias de Maven (~/.m2/repository)
- **Key:** Hash de todos los pom.xml
- **Restore:** Recupera cache en builds posteriores
- **Beneficio:** Acelera builds subsecuentes en 2-3 minutos

### 4️⃣ **Verificación de Estructura del Monorepo**
```yaml
- name: Verify monorepo structure
  run: |
    echo "=== Microservicios encontrados ==="
    ls -la backend/
    ...
```
- Valida que todos los microservicios estén presentes
- Verifica la existencia de pom.xml en cada servicio
- **Microservicios esperados:**
  - nova-gateway
  - auth-service
  - user-service
  - product-service
  - customer-service
  - order-service

### 5️⃣ **Build de Todos los Microservicios**
```yaml
- name: Build all microservices
  run: |
    mvn clean compile \
      --file backend/pom.xml \
      -DskipTests \
      -B \
      -V
```
- Ejecuta `mvn clean compile` en el pom.xml padre
- **Flags:**
  - `clean`: Limpia compilaciones previas
  - `compile`: Compila todos los módulos
  - `--file backend/pom.xml`: Apunta al pom padre del monorepo
  - `-DskipTests`: Omite tests en esta fase (se ejecutan después)
  - `-B`: Modo batch (sin interacción)
  - `-V`: Muestra versiones de Maven y Java

### 6️⃣ **Ejecución de Tests y Cobertura JaCoCo**
```yaml
- name: Run tests and generate JaCoCo coverage reports
  run: |
    mvn verify \
      --file backend/pom.xml \
      -B \
      --fail-at-end
```
- Ejecuta `mvn verify` que incluye:
  - Tests unitarios (fase `test`)
  - Generación de reportes JaCoCo (fase `verify`)
  - **Validación de umbral de cobertura (80%)**
- **Flags:**
  - `verify`: Ejecuta tests y verifica las métricas
  - `--fail-at-end`: Continúa hasta el final pero falla si hay errores
  - `-B`: Modo batch

**Configuración JaCoCo en pom.xml de cada servicio:**
- Versión: 0.8.11
- Genera reportes en: `target/site/jacoco/`
- Genera archivo: `target/jacoco.exec`

### 7️⃣ **Verificación del Umbral de Cobertura**
```yaml
- name: Check JaCoCo coverage threshold
  run: |
    echo "=== Verificando umbral de cobertura (80%) ==="
    ...
```
- Verifica que la cobertura global sea >= 80%
- **Nota:** La validación real ocurre en `mvn verify` (paso 6)
- Este paso es informativo y documenta el proceso

### 8️⃣ **Publicación de Resultados de Tests**
```yaml
- name: Publish test results
  if: always()
  uses: EnricoMi/publish-unit-test-result-action@v2
  with:
    files: '**/target/surefire-reports/*.xml'
    check_name: Unit Test Results
```
- Publica resultados de tests en la UI de GitHub
- Crea anotaciones en el PR con detalles de tests fallidos
- Se ejecuta incluso si pasos anteriores fallan (`if: always()`)

### 9️⃣ **Subida de Reportes de Cobertura**
```yaml
- name: Upload JaCoCo coverage reports
  if: always()
  uses: actions/upload-artifact@v4
  with:
    name: jacoco-coverage-reports
    path: |
      backend/*/target/site/jacoco/
      backend/*/target/jacoco.exec
    retention-days: 30
```
- Almacena reportes JaCoCo como artefactos
- **Ubicación:** Tab "Artifacts" en la ejecución del workflow
- **Retención:** 30 días
- Permite descargar reportes HTML completos

### 🔟 **Resumen del Workflow**
```yaml
- name: Print build summary
  if: always()
  run: |
    echo "Job Status: ${{ job.status }}"
    echo "Git Branch: ${{ github.ref }}"
    echo "Commit SHA: ${{ github.sha }}"
```
- Imprime resumen final con:
  - Estado del build (success/failure)
  - Rama de git
  - SHA del commit

---

## 🔒 Configuración Requerida en GitHub

### 1. Habilitar GitHub Actions
```
Repo Settings → Actions → General → Allow all actions and reusable workflows
```

### 2. Secretos (Opcional - Solo si usas SonarCloud)
```
Repo Settings → Secrets and variables → Actions
```

Agregar:
- `SONAR_TOKEN`: Token de SonarCloud
- `SONAR_PROJECT_KEY`: Clave del proyecto
- `SONAR_ORGANIZATION`: Organización en SonarCloud

---

## 🔧 Integración con SonarCloud (Opcional)

Si deseas análisis de código estático, descomenta la sección en `ci.yml`:

```yaml
- name: SonarCloud Scan
  uses: SonarSource/sonarcloud-github-action@master
  env:
    GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
    SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
  with:
    args: >
      -Dsonar.projectKey=${{ secrets.SONAR_PROJECT_KEY }}
      -Dsonar.organization=${{ secrets.SONAR_ORGANIZATION }}
      -Dsonar.sources=backend
      -Dsonar.exclusions=backend/*/target/**,**/generated-sources/**
      -Dsonar.coverage.jacoco.xmlReportPaths=backend/*/target/site/jacoco/jacoco.xml
```

**Requisitos:**
1. Crear cuenta en [SonarCloud](https://sonarcloud.io)
2. Agregar repositorio
3. Generar token
4. Crear secretos en GitHub

---

## 📊 Monorepo - Estructura Maven

### Archivo Padre: `backend/pom.xml`
```xml
<modules>
    <module>nova-gateway</module>
    <module>auth-service</module>
    <module>user-service</module>
    <module>product-service</module>
    <module>customer-service</module>
    <module>order-service</module>
</modules>
```

### Ventajas de esta estructura:
- ✅ Build único para todos los servicios
- ✅ Cache compartido de dependencias
- ✅ Versionado centralizado
- ✅ Control de dependencias consistente

---

## ✅ Criterios de Éxito del Pipeline

El pipeline **PASA** si:

1. ✅ Todos los microservicios compilan sin errores
2. ✅ Todos los tests unitarios pasan
3. ✅ La cobertura de código es >= 80% (JaCoCo)
4. ✅ No hay warnings críticos en el build

El pipeline **FALLA** si:

1. ❌ Error de compilación en cualquier módulo
2. ❌ Tests unitarios fallan
3. ❌ Cobertura de código < 80%
4. ❌ Error en ejecución de cualquier paso

---

## 📈 Ejemplo de Ejecución

### Push a rama `develop`
```
1. GitHub detects push to develop
2. Workflow "NovaCommerce CI" inicia
3. Ejecuta los 10 pasos del pipeline
4. Publica resultados en el commit
5. Notifica al autor del build status
```

### Pull Request a `main`
```
1. GitHub detects PR creation/update
2. Workflow "NovaCommerce CI" inicia
3. Ejecuta el pipeline completo
4. Publica check con estado PASS/FAIL
5. Bloquea merge si el build falla (opcional)
```

---

## 🔍 Monitoreo y Debugging

### Ver logs del workflow
1. Ir a `Actions` tab en GitHub
2. Seleccionar el workflow "NovaCommerce CI"
3. Hacer click en la ejecución
4. Ver logs detallados de cada paso

### Descargar reportes
1. Ir a la ejecución del workflow
2. Sección "Artifacts"
3. Descargar `jacoco-coverage-reports`
4. Abrir `index.html` en cada servicio para ver cobertura detallada

### Debug local
```bash
# Ejecutar el mismo build localmente
cd Nova-Commerce
mvn clean verify -DskipTests  # Compilar
mvn verify                     # Con tests y cobertura
```

---

## 🚀 Mejoras Futuras

- [ ] Agregar step de build de Docker
- [ ] Agregar step de deploy a staging
- [ ] Integración con SonarCloud
- [ ] Notificaciones en Slack
- [ ] Análisis de performance
- [ ] Security scanning (Snyk, Trivy)

---

## 📚 Referencias

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Maven POM Reference](https://maven.apache.org/pom.html)
- [JaCoCo Maven Plugin](https://www.jacoco.org/jacoco/trunk/doc/maven.html)
- [Spring Boot 3.4.3 with Java 17](https://spring.io/projects/spring-boot)

---

**Última actualización:** Enero 2026  
**Versión:** 1.0
