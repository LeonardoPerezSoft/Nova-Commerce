Actúa como arquitecto de software senior y genera el README general del proyecto NovaCommerce.
El objetivo es describir la plataforma de forma clara, técnica y entendible, sin ser excesivamente extensa.

👉 Sigue estas instrucciones estrictamente:

1️⃣ Sección — Descripción General del Sistema

Breve introducción del ecosistema de microservicios
Objetivo del proyecto (E-commerce modular y escalable)
Principios aplicados:
Clean Architecture
SOLID
Ports & Adapters
DevOps & CI/CD enfoque
Enfoque MVP actual (sin exagerar funcionalidades futuras)

2️⃣ Sección — Arquitectura General

Explica la responsabilidad de cada microservicio:

API Gateway
Auth Service
User Service
Product Service
Customer Service
Order Service

Incluye un diagrama de arquitectura en Mermaid que muestre:

flujo de llamadas
comunicación entre servicios
quién valida seguridad
quién expone APIs

👉 Usa un diagrama Mermaid en formato:
mermaid
graph LR
...

3️⃣ Sección — Diagramas de Secuencia por Microservicio

Para cada microservicio incluye un diagrama de secuencia Mermaid mostrando:

cliente → gateway → servicio
validaciones internas
adaptadores / puertos
persistencia
servicios externos (si aplica)

👉 Usa el formato:
mermaid
sequenceDiagram
...

Microservicios que deben tener diagrama:
Auth Service — login & token validation
User Service — creación de usuario
Product Service — consulta de productos
Customer Service — registro de cliente
Order Service — creación de orden con reglas de descuento

4️⃣ Sección — Alcance del MVP Actual

qué está implementado
por qué la arquitectura permite escalar

5️⃣ Reglas de Estilo del Documento

lenguaje técnico pero claro
evitar párrafos largos
usar tablas cuando ayuden
no repetir información que ya está en los README de cada micro
enlaces hacia los readme individuales

6️⃣ Formato de salida solicitado

Markdown compatible con GitHub
Encabezados claros
Secciones bien estructuradas
Diagramas Mermaid legibles