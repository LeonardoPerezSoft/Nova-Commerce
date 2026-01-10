GitHub Actions CI (NovaCommerce)

Actúa como un DevOps Engineer Senior.

Necesito que generes un archivo de GitHub Actions para el monorepo Java (Spring Boot) llamado Nova-Commerce, que contiene la carpeta backend con múltiples microservicios (api-gateway, auth-service, user-service, product-service, customer-service, order-service).

Requisitos del pipeline CI:

1. El workflow debe ejecutarse en cada push y pull request a las ramas:
   - develop
   - main

2. El pipeline debe ejecutarse en Ubuntu latest.

3. Pasos obligatorios:
   - Checkout del repositorio
   - Configuración de Java (Java 17)
   - Cache de dependencias Maven
   - Build y compilación de TODOS los microservicios usando Maven
   - Ejecución de tests unitarios en todos los microservicios

4. Cobertura de código:
   - Usar JaCoCo
   - Falla el pipeline si la cobertura global es menor al 80%
   - La verificación de cobertura debe hacerse durante el build (maven verify)

5. Estándares:
   - Usar comandos Maven claros y explícitos
   - El pipeline debe fallar ante cualquier error de compilación, test o cobertura
   - El workflow debe ser simple, legible y mantenible

6. (Opcional / Bonus):
   - Incluir integración con SonarCloud de forma opcional (comentada)
   - Incluir placeholders claros para:
     - SONAR_TOKEN
     - SONAR_PROJECT_KEY
     - SONAR_ORGANIZATION

7. Estructura esperada del archivo:
   - Ubicación: .github/workflows/ci.yml
   - Nombre del workflow: NovaCommerce CI

Notas importantes:
- El proyecto es un monorepo, por lo tanto el build debe recorrer todos los módulos(Verifica los modulos).
- No incluir pasos de Docker, despliegue ni infraestructura.
- Asumir que cada microservicio tiene su propio pom.xml y JaCoCo configurado(Verficalo).
- Priorizar claridad sobre optimización prematura.
