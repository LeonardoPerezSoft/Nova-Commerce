Registro público de clientes (User + Customer)

Necesitamos implementar el registro público de clientes en un sistema de microservicios.

CONTEXTO GENERAL

Java 17
Spring Boot
Arquitectura Hexagonal / Clean Architecture
Microservicios independientes
Spring Security con JWT
NO romper arquitectura existente
NO reutilizar controllers internos vía HTTP
NO meter lógica de negocio en controllers

MICROSERVICIOS INVOLUCRADOS
1️⃣ USER-SERVICE (ORQUESTADOR DEL REGISTRO)
Este microservicio será el único punto de entrada público para el registro.

2️⃣ CUSTOMER-SERVICE
Se usará SOLO mediante llamada interna desde User-Service.
❌ Auth-Service NO participa en el registro
✔ Auth-Service se usa solo para login / refresh token

OBJETIVO FUNCIONAL
Permitir que un usuario NO autenticado se registre como CLIENTE, creando:
Un User (credenciales y rol)
Un Customer (perfil de negocio)
Ambas operaciones deben ejecutarse como parte del flujo de registro.

ENDPOINT PÚBLICO (USER-SERVICE)
Endpoint
POST /api/public/register
Seguridad
Accesible sin JWT (permitAll)
SOLO este endpoint es público

Todo /api/users/** debe seguir protegido

REQUEST BODY (DTO)
{
  "email": "leonard@correo.com",
  "password": "Password123!",
  "firstName": "Leonard",
  "lastName": "Pérez",
  "phone": "3114483021"
}

Reglas
email único
password debe cifrarse con BCrypt
El email se usa como username interno
Validaciones con Bean Validation
COMPORTAMIENTO DEL FLUJO
En USER-SERVICE:
Validar que el email NO exista
Crear el User con:
email
password (BCrypt)
role = CLIENT
status = ACTIVE
NO exponer password en ninguna respuesta
NO generar JWT
Llamar a CUSTOMER-SERVICE para crear el Customer

CUSTOMER-SERVICE (ENDPOINT INTERNO)
Endpoint
POST /api/customers/internal

Seguridad
NO accesible desde frontend
Protegido para llamadas internas (role SERVICE o API key)
Request
{
  "email": "leonard@correo.com",
  "firstName": "Leonard",
  "lastName": "Pérez",
  "phone": "3114483021"
}

Comportamiento

Validar email único
Crear Customer con:
status = ACTIVE
loyaltyLevel = BRONZE (default)
Retornar customerId

RESPUESTA FINAL (USER-SERVICE)
HTTP 201 Created
{
  "userId": "uuid",
  "customerId": 5,
  "email": "leonard@correo.com",
  "roles": ["CLIENT"],
  "message": "Registro exitoso. Ahora puedes iniciar sesión."
}

REQUERIMIENTOS DE ARQUITECTURA (OBLIGATORIOS)

✔ Crear controlador PublicRegisterController
✔ Crear RegisterClientUseCase
✔ Lógica SOLO en capa de aplicación
✔ Comunicación entre micros vía puertos / clients
✔ NO lógica en controller
✔ NO llamadas HTTP a controllers propios
✔ Respetar estructura de paquetes existente
✔ Código limpio, mantenible y testeable

SEGURIDAD

Configurar Spring Security:
/api/public/register → permitAll
resto de endpoints → protegidos
NO emitir token en el registro
Login se hace luego en Auth-Service

RESULTADO ESPERADO

Registro público funcional
Arquitectura limpia
Sin acoplamiento indebido
Listo para crecer (email verification, welcome flow, etc.)
