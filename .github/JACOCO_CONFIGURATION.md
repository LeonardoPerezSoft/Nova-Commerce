# Configuración de JaCoCo - Guía de Implementación

## 📋 Descripción

Configuración recomendada de JaCoCo (Java Code Coverage) para los microservicios de Nova Commerce. Garantiza que todos los servicios generen reportes de cobertura consistentes y validen un umbral mínimo del 80%.

---

## 🎯 Objetivo

- Medir cobertura de código en todos los microservicios
- Establecer umbral mínimo del 80%
- Automatizar validación en el pipeline CI/CD
- Generar reportes HTML para análisis manual

---

## 📦 Configuración en pom.xml

### Versión de JaCoCo Recomendada

```xml
<version>0.8.11</version>
```

---

## ✅ Configuración Completa

### 1. Plugin JaCoCo (Build)

```xml
<!-- JaCoCo for code coverage -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <executions>
        <!-- Prepare agent for test execution -->
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <!-- Generate report during verify phase -->
        <execution>
            <id>report</id>
            <phase>verify</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
        <!-- Check coverage thresholds -->
        <execution>
            <id>jacoco-check</id>
            <phase>verify</phase>
            <goals>
                <goal>check</goal>
            </goals>
            <configuration>
                <rules>
                    <rule>
                        <element>BUNDLE</element>
                        <excludes>
                            <exclude>*Test</exclude>
                            <exclude>*.config.*</exclude>
                            <exclude>*.dto.*</exclude>
                            <exclude>*.entity.*</exclude>
                            <exclude>*Application</exclude>
                        </excludes>
                        <limits>
                            <limit>
                                <counter>LINE</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.80</minimum>
                            </limit>
                        </limits>
                    </rule>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### 2. Plugin Maven Surefire (Tests)

```xml
<!-- Ensure surefire passes the JaCoCo agent to the test JVM -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.2.5</version>
    <configuration>
        <useModulePath>false</useModulePath>
        <argLine>${argLine}</argLine>
    </configuration>
</plugin>
```

---

## 🔍 Parámetros Explicados

### `prepare-agent`
- **Propósito:** Prepara el agente JaCoCo para monitorear la ejecución de tests
- **Salida:** Crea variable `argLine` con configuración del agente
- **Fase:** Pre-test

### `report`
- **Propósito:** Genera reporte HTML de cobertura
- **Ubicación:** `target/site/jacoco/index.html`
- **Fase:** `verify`

### `check`
- **Propósito:** Valida que la cobertura cumpla con umbrales
- **Acción:** Falla el build si no se cumplen los límites
- **Configuración:** Umbral mínimo 80%

### Exclusiones
```xml
<exclude>*Test</exclude>              <!-- Clases de test -->
<exclude>*.config.*</exclude>         <!-- Clases de configuración -->
<exclude>*.dto.*</exclude>            <!-- Data Transfer Objects -->
<exclude>*.entity.*</exclude>         <!-- Entidades JPA -->
<exclude>*Application</exclude>       <!-- Clases Application.java -->
```

---

## 📊 Archivo de Salida

### Ubicaciones Generated

```
target/
├── jacoco.exec                      # Datos raw de cobertura
└── site/
    └── jacoco/
        ├── index.html               # Reporte HTML principal
        ├── jacoco.xml               # Reporte XML (para CI/CD)
        ├── status.svg               # Badge de cobertura
        └── [por package/class]      # Detalles por componente
```

---

## 🚀 Comandos de Uso

### Build Completo con Cobertura
```bash
mvn clean verify
```

### Build sin Tests (rápido)
```bash
mvn clean compile
```

### Build con Tests pero sin validar cobertura
```bash
mvn clean test
```

### Build con validación estricta
```bash
mvn clean verify --fail-at-end
```

### Generar reporte solo
```bash
mvn jacoco:report
```

### Ver reporte
```bash
open target/site/jacoco/index.html    # macOS
start target/site/jacoco/index.html   # Windows
xdg-open target/site/jacoco/index.html # Linux
```

---

## ✅ Checklist de Implementación

Para cada microservicio:

- [ ] **Versión de JaCoCo:** 0.8.11
- [ ] **Fase de prepare-agent:** Pre-test
- [ ] **Fase de report:** verify
- [ ] **Fase de check:** verify
- [ ] **Umbral mínimo:** 80%
- [ ] **argLine configurado:** `${argLine}`
- [ ] **useModulePath:** false en Surefire
- [ ] **Exclusiones aplicadas:** config, dto, entity, *Test, *Application

---

## 📈 Interpretación de Reportes

### En CLI
```
BUILD SUCCESS / BUILD FAILURE
[INFO] JaCoCo Coverage Check: PASS / FAIL
[INFO] Coverage: 85.3% >= 80% ✓
```

### En Reporte HTML

**Verde (>80%):** Cobertura adecuada  
**Amarillo (60-80%):** Cobertura parcial  
**Rojo (<60%):** Cobertura baja  

**Métricas:**
- **Line Coverage:** Líneas de código ejecutadas
- **Branch Coverage:** Ramas de condicionales (if/else)
- **Method Coverage:** Métodos ejecutados
- **Class Coverage:** Clases ejecutadas

---

## 🔧 Troubleshooting

### Problema: JaCoCo check falla sin motivo
**Solución:**
```bash
mvn clean verify -X  # Enable debug mode
```

### Problema: Report no se genera
**Solución:**
```bash
# Asegurar que tests existen y ejecutan
mvn test
# Generar reporte manualmente
mvn jacoco:report
```

### Problema: argLine not found
**Solución:**
```xml
<!-- En Surefire configuration -->
<argLine>${argLine}</argLine>
<!-- Asegurar que prepare-agent corre antes -->
```

---

## 📊 Ejemplo de Configuración Completa

Ver implementación en:
- `backend/auth-service/pom.xml` (líneas 146-180)
- `backend/user-service/pom.xml` (líneas 153-187)

---

## 🎯 Mejores Prácticas

1. **Ejecutar tests localmente antes de push**
   ```bash
   mvn clean verify
   ```

2. **Revisar reportes HTML regularmente**
   - Identificar áreas con baja cobertura
   - Agregar tests según sea necesario

3. **No disminuir el umbral del 80%**
   - Mantiene consistencia
   - Garantiza calidad

4. **Excluir clases apropiadamente**
   - DTOs, entities, config
   - Código autogenerado

5. **Documentar tests complejos**
   - Facilita mantenimiento
   - Mejora comprensión

---

## 📚 Referencias

- [JaCoCo Official Documentation](https://www.jacoco.org/jacoco/trunk/doc/maven.html)
- [Maven Surefire Plugin](https://maven.apache.org/surefire/maven-surefire-plugin/)
- [Code Coverage Best Practices](https://martinfowler.com/bliki/TestCoverage.html)

---

**Última actualización:** Enero 2026  
**Versión:** 1.0
