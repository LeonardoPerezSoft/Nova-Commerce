# 🚀 Guía de Despliegue - GitHub Actions NovaCommerce CI

## 📋 Checklist Pre-Despliegue

### ✅ Verificaciones Locales

Antes de hacer push a GitHub, ejecuta:

```bash
cd Nova-Commerce

# 1. Verificar estructura del monorepo
ls -la backend/
# Esperado: auth-service, customer-service, nova-gateway, order-service, product-service, user-service

# 2. Verificar pom.xml padre
cat backend/pom.xml | grep -A 10 "<modules>"
# Esperado: 6 módulos listados

# 3. Compilar localmente
mvn clean compile --file backend/pom.xml
# Esperado: BUILD SUCCESS

# 4. Ejecutar tests con cobertura
mvn verify --file backend/pom.xml
# Esperado: BUILD SUCCESS + Coverage >= 80%

# 5. Revisar reportes de cobertura
open backend/auth-service/target/site/jacoco/index.html
# Esperado: Reporte HTML con estadísticas de cobertura
```

---

## 🔧 Pasos de Despliegue en GitHub

### 1. Habilitar GitHub Actions (si no está activo)

```
1. Ir a: https://github.com/[tu-usuario]/Nova-Commerce
2. Navegar a: Settings → Actions → General
3. Bajo "Actions permissions" seleccionar:
   ✓ "Allow all actions and reusable workflows"
4. Bajo "Workflow permissions" seleccionar:
   ✓ "Read and write permissions"
   ✓ "Allow GitHub Actions to create and approve pull requests"
5. Hacer click en "Save"
```

### 2. Hacer Push de los Archivos Creados

```bash
cd Nova-Commerce

# Staging de archivos
git add .github/
git add backend/pom.xml

# Verificar cambios
git status
# Esperado: Ver los archivos nuevos

# Commit
git commit -m "feat: Add GitHub Actions CI/CD pipeline for Nova Commerce

- Add .github/workflows/ci.yml with 10-step pipeline
- Add parent pom.xml for monorepo build
- Include JaCoCo coverage validation (80% threshold)
- Auto-publish test results and coverage reports
- Include SonarCloud integration (optional)"

# Push a rama develop (para testing primero)
git push origin develop
```

### 3. Monitorear la Primera Ejecución

```
1. Ir a: https://github.com/[tu-usuario]/Nova-Commerce/actions
2. Ver workflow "NovaCommerce CI" en ejecución
3. Hacer click en "Build and Test"
4. Ver logs en tiempo real
5. Esperar a que termine (5-10 minutos en primer run)
```

### 4. Revisar Resultados

**Si BUILD SUCCESS ✓:**
```
✓ All steps completed successfully
✓ Test results published
✓ Coverage reports uploaded
✓ Artifacts available for download
```

**Si BUILD FAILURE ✗:**
```
1. Ir a: Actions → Latest run
2. Hacer click en el paso que falló
3. Revisar logs detallados
4. Comparar con ejecución local: mvn verify
5. Hacer fix localmente y push nuevamente
```

---

## 📊 Verificación Post-Despliegue

### ✅ Checklist de Validación

- [ ] Workflow aparece en Actions tab
- [ ] Workflow se ejecuta automáticamente en push
- [ ] Todos los 10 pasos completan exitosamente
- [ ] Test results se publican en GitHub UI
- [ ] Coverage reports se descargan como artifacts
- [ ] Build falla si cobertura < 80% (prueba local)
- [ ] Build pasa si cobertura >= 80%

### 🔍 Detalles en GitHub UI

**En la pestaña de Actions:**
```
NovaCommerce CI
├── Build and Test
│   ├── Checkout repository ✓
│   ├── Set up Java 17 ✓
│   ├── Cache Maven dependencies ✓
│   ├── Verify monorepo structure ✓
│   ├── Build all microservices ✓
│   ├── Run tests and generate JaCoCo coverage ✓
│   ├── Check JaCoCo coverage threshold ✓
│   ├── Publish test results ✓
│   ├── Upload JaCoCo coverage reports ✓
│   └── Print build summary ✓
└── [Artifacts]
    └── jacoco-coverage-reports (30 days)
```

---

## 🔐 Configuración de Branch Protection (Opcional pero Recomendado)

Para requerir que el CI pase antes de mergear:

```
1. Ir a: Settings → Branches
2. Hacer click en "Add rule"
3. Pattern: develop (o main)
4. Habilitar:
   ✓ "Require status checks to pass before merging"
   ✓ Seleccionar "NovaCommerce CI" como required check
5. Habilitar:
   ✓ "Require branches to be up to date before merging"
6. Hacer click en "Create"
```

**Resultado:** PRs no pueden mergearse si el CI falla.

---

## 🔧 Integración con SonarCloud (Opcional)

Si deseas análisis de código estático:

### 1. Setup en SonarCloud

```
1. Ir a: https://sonarcloud.io
2. Sign in con GitHub
3. Hacer click en "Analyze new project"
4. Seleccionar "Nova-Commerce"
5. Generar token en SonarCloud
6. Copiar: Organization key y Project key
```

### 2. Agregar Secretos en GitHub

```
1. Ir a: Settings → Secrets and variables → Actions
2. Click "New repository secret"
3. Name: SONAR_TOKEN
   Value: [copiar token de SonarCloud]
4. Click "New repository secret"
5. Name: SONAR_ORGANIZATION
   Value: [copiar organization key]
6. Click "New repository secret"
7. Name: SONAR_PROJECT_KEY
   Value: [copiar project key]
```

### 3. Descomentar SonarCloud en ci.yml

```yaml
# En .github/workflows/ci.yml, buscar la sección:
# - name: SonarCloud Scan
# Descomentar el bloque (quitar los # )
```

### 4. Hacer Push

```bash
git add .github/workflows/ci.yml
git commit -m "feat: Enable SonarCloud integration"
git push origin develop
```

---

## 📈 Monitoreo Continuo

### Diariamente
```
1. Revisar Actions tab regularmente
2. Asegurar que todos los runs pasan
3. Monitorear cobertura de código
```

### Semanalmente
```
1. Descargar reportes de cobertura
2. Analizar áreas con baja cobertura
3. Planificar tests adicionales si es necesario
```

### Mensualmente
```
1. Revisar logs de failing builds
2. Investigar patrones de fallos
3. Optimizar configuración si es necesario
```

---

## 🐛 Troubleshooting

### Problema: "File not found: backend/pom.xml"

**Causa:** Parent pom.xml no fue creado correctamente

**Solución:**
```bash
# Verificar
ls -la backend/pom.xml

# Si no existe, crear manualmente
touch backend/pom.xml
git add backend/pom.xml
git commit -m "Add parent pom.xml"
```

### Problema: "mvn command not found"

**Nota:** Este error no ocurrirá en GitHub (usa la acción setup-java), pero localmente:

**Solución:**
```bash
# Instalar Maven
# macOS
brew install maven

# Linux
sudo apt-get install maven

# Windows
choco install maven
```

### Problema: Tests fallan en CI pero pasan localmente

**Causa:** Diferencias de ambiente (timezone, locale, etc.)

**Solución:**
```yaml
# En ci.yml, agregar env vars
env:
  LANG: en_US.UTF-8
  TZ: UTC
```

### Problema: Cobertura reporta 0% o muy baja

**Causa:** JaCoCo no se ejecutó correctamente

**Solución:**
```bash
# Verificar localmente
mvn clean verify -X  # Debug mode
# Revisar que los tests se ejecuten
# Revisar logs de maven-surefire-plugin
```

---

## 📞 Soporte y Referencias

### Documentación
- **GitHub Actions:** `.github/GITHUB_ACTIONS_README.md`
- **JaCoCo Config:** `.github/JACOCO_CONFIGURATION.md`
- **Implementación:** `.github/IMPLEMENTATION_SUMMARY.md`

### Links Útiles
- [GitHub Actions Docs](https://docs.github.com/en/actions)
- [Maven CI/CD Best Practices](https://maven.apache.org/)
- [JaCoCo Documentation](https://www.jacoco.org/)

---

## ✅ Validación Final

Una vez todo está configurado:

```bash
# 1. Verificar archivos creados
ls -la .github/workflows/ci.yml
ls -la .github/*.md
ls -la backend/pom.xml

# 2. Ver commit history
git log --oneline | head -5

# 3. Ver estado de GitHub Actions
open https://github.com/[tu-usuario]/Nova-Commerce/actions
```

**Esperado:**
- ✅ Todos los archivos existen
- ✅ Commits incluyen CI/CD changes
- ✅ Actions tab muestra "NovaCommerce CI" workflow
- ✅ Workflow se ejecuta en push a develop/main

---

## 🎉 ¡Listo para Producción!

Con esta configuración tienes:

- ✅ CI/CD automático en cada push
- ✅ Validación de cobertura (80%)
- ✅ Publicación de resultados
- ✅ Reportes descargables
- ✅ Extensibilidad para SonarCloud
- ✅ Branch protection opcional

**Tiempo de setup:** ~15 minutos  
**ROI:** Detección temprana de bugs + Mejora de calidad  

---

**Guía completada:** Enero 2026  
**Versión:** 1.0
