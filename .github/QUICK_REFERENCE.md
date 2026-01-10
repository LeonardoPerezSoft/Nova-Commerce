# 📖 Guía Rápida - GitHub Actions CI/CD NovaCommerce

## 📁 Archivos Creados

| Archivo | Descripción |
|---------|-------------|
| `.github/workflows/ci.yml` | Workflow principal (10 pasos) |
| `.github/GITHUB_ACTIONS_README.md` | Documentación completa |
| `.github/JACOCO_CONFIGURATION.md` | Guía de cobertura JaCoCo |
| `.github/IMPLEMENTATION_SUMMARY.md` | Resumen de implementación |
| `.github/DEPLOYMENT_GUIDE.md` | Pasos para desplegar |
| `.github/QUICK_REFERENCE.md` | Este archivo |
| `backend/pom.xml` | Parent POM para monorepo |

---

## ⚡ Comandos Rápidos

### Build Local
```bash
# Compilar todo
mvn clean compile --file backend/pom.xml

# Tests + Cobertura (80% threshold)
mvn verify --file backend/pom.xml

# Ver reporte
open backend/auth-service/target/site/jacoco/index.html
```

### Git
```bash
# Push a develop (para testing)
git push origin develop

# Ver workflow en GitHub
# → Actions tab → NovaCommerce CI

# Ver logs
# → Click en workflow → Click en step
```

---

## 🔍 Estructura Workflow

```
Paso 1: Checkout
Paso 2: Java 17
Paso 3: Maven Cache
    │
Paso 4: Verify Monorepo
    │
Paso 5: Build (mvn compile)
    │
Paso 6: Test + Coverage (mvn verify)
    │
Paso 7-10: Reportes & Artifacts
```

---

## ✅ Microservicios Compilados

```
✓ nova-gateway       (8080)
✓ auth-service       (8081)
✓ user-service       (8082)
✓ product-service    (8083)
✓ customer-service   (8084)
✓ order-service      (8085)
```

---

## 🎯 Build Pasa Si

- ✅ Sin errores de compilación
- ✅ Tests pasan
- ✅ Cobertura >= 80%

---

## ❌ Build Falla Si

- ❌ Error de compilación
- ❌ Tests fallidos
- ❌ Cobertura < 80%

---

## 📊 Triggers

```
Push a develop o main
    ↓
PR a develop o main
    ↓
Workflow ejecuta automáticamente
```

---

## 🔐 Secretos (Opcional - SonarCloud)

```
Settings → Secrets and variables → Actions

Required para SonarCloud:
- SONAR_TOKEN
- SONAR_PROJECT_KEY
- SONAR_ORGANIZATION
```

---

## 🚀 Deploy Steps

```
1. mvn verify (local)
2. git push origin develop
3. Ver Actions tab
4. Revisar logs
5. Si pasa: merge a main
```

---

## 📈 Reportes

**Ubicación en GitHub:**
```
Actions → Latest Run → Artifacts
    └─ jacoco-coverage-reports (ZIP)
```

**Contenido:**
```
backend/*/target/site/jacoco/
    └─ index.html (abrir en navegador)
```

---

## 💡 Notas Importantes

- ⏱️ Primer build: ~10 min (sin cache)
- ⚡ Builds posteriores: ~5 min (con cache)
- 🔒 Los secretos no se muestran en logs
- 📊 Cobertura >= 80% es obligatorio
- 🎯 Los reportes se guardan 30 días

---

## 🔗 Links Importantes

| Recurso | URL |
|---------|-----|
| Actions | https://github.com/[repo]/actions |
| Secrets | https://github.com/[repo]/settings/secrets/actions |
| Branches | https://github.com/[repo]/settings/branches |
| Docs CI | `.github/GITHUB_ACTIONS_README.md` |
| Docs JaCoCo | `.github/JACOCO_CONFIGURATION.md` |
| Guía Deploy | `.github/DEPLOYMENT_GUIDE.md` |

---

## ❓ FAQ

**P: ¿Cuánto tarda el primer run?**  
R: 8-10 minutos (sin cache Maven)

**P: ¿Por qué falla con cobertura 79%?**  
R: Umbral mínimo es 80% (requiere 80.00% o más)

**P: ¿Dónde veo los logs?**  
R: Actions tab → Click en workflow → Click en step

**P: ¿Qué pasa si un test falla?**  
R: Build falla, workflow se detiene, no puede mergear

**P: ¿Puedo cambiar el umbral de cobertura?**  
R: Sí, en `backend/*/pom.xml` línea ~165 (no recomendado)

---

## ✨ Features Implementadas

- ✅ CI en push y PR
- ✅ Build de monorepo
- ✅ Tests automáticos
- ✅ Cobertura JaCoCo
- ✅ Validación de umbral
- ✅ Test result publishing
- ✅ Artifact storage (30d)
- ✅ SonarCloud ready (comentado)

---

## 🎓 Próximos Pasos

1. **Hacer push** a develop
2. **Ver workflow** en Actions
3. **Revisar reportes** en artifacts
4. **Configurar branch protection** (opcional)
5. **Agregar SonarCloud** (opcional)

---

**Creado:** Enero 2026  
**Estado:** ✅ Producción  
**Versión:** 1.0
