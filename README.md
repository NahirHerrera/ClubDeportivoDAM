# Club Deportivo – Equipo 1

App Android (Kotlin) para la gestión de un club deportivo: login, registro de clientes, cobro de cuotas, carnet de socio, inscripción a actividades y listados de clientes y deudores.

> Los datos son de prueba y están en memoria (`SociosRepository`), hasta que se conecte la base de datos.
> Todo lo que se cambia (pagos, inscripciones) se pierde al cerrar la app.

---

## Usuarios de prueba

### 1. Login

El login no valida contra usuarios guardados: acepta cualquier email y contraseña **con formato válido**.

| Caso | Email | Contraseña | Resultado |
|---|---|---|---|
| ✅ Ingreso correcto | `admin@club.com` | `Admin123` | "Datos correctos. Iniciando sesión..." → Home |
| ❌ Email inválido | `admin` | `Admin123` | "Ingresá un correo válido" |
| ❌ Contraseña corta | `admin@club.com` | `Ab1` | "Al menos 6 caracteres" |
| ❌ Sin mayúscula | `admin@club.com` | `admin123` | "Al menos una mayúscula" |
| ❌ Sin minúscula | `admin@club.com` | `ADMIN123` | "Al menos una minúscula" |
| ❌ Sin número | `admin@club.com` | `Adminabc` | "Al menos un número" |

### 2. Socios (DNI)

Sirven para **Cobrar Cuota**, **Carnet**, **Listado de Clientes** y **Listado de Deudores**.

| DNI | Socio | N° | Activo | Cuota | Actividades | Apto físico* |
|---|---|---|---|---|---|---|
| `26298456` | Juan Perez | 0001 | Sí | ✅ Al día | Musculación, Funcional | Vencido (15/03/2025) |
| `35987654` | Lucía Fernández | 0004 | Sí | ✅ Al día | Spinning, Zumba | Vigente (10/01/2026) |
| `27666333` | Diego Sánchez | 0007 | Sí | ✅ Al día | — | Vigente (01/07/2026) |
| `30123456` | María Gómez | 0002 | Sí | ❌ Debe | — | Vencido (02/06/2025) |
| `28555111` | Carlos López | 0003 | Sí | ❌ Debe | — | Vencido (20/08/2025) |
| `40111222` | Sofía Martínez | 0006 | Sí | ❌ Debe | — | Vigente (18/04/2026) |
| `33444222` | Martín Rodríguez | 0005 | ❌ No | ❌ Debe | — | Vencido (05/11/2024) |

\* El apto físico vence **un año después** de la fecha de presentación (entre paréntesis). El estado mostrado es al 04/10/2026; con el paso del tiempo puede cambiar.

### 3. Qué pasa con cada DNI en cada pantalla

| Condición a probar | DNI | Cobrar Cuota | Listado de Clientes | Listado de Deudores |
|---|---|---|---|---|
| Al día, con actividades | `26298456` | "Este socio tiene la cuota al dia" + Mostrar Carnet | Figura con sus actividades | No figura |
| Al día, sin actividades | `27666333` | "Este socio tiene la cuota al dia" + Mostrar Carnet | Figura con "Sin actividades inscriptas" | No figura |
| Debe la cuota | `30123456` | Muestra las formas de pago | "No tiene la cuota al día, por eso no figura" | Figura |
| Debe → paga | `30123456` | Pagar → "Pago registrado" → **Ver Comprobante** | Pasa a figurar, sin actividades | Deja de figurar |
| Inactivo | `33444222` | Muestra las formas de pago (Cobrar Cuota no revisa si está activo) | "No está activo" | No figura |
| No existe | cualquier otro, ej. `11111111` | "No se encontró un socio con DNI …" | "No existe un cliente con DNI …" | "Ningún deudor coincide…" |

### 4. Carnet y apto físico

En **Cobrar Cuota**, buscá un socio al día y tocá **Mostrar Carnet**. Si el socio debe la cuota, la app avisa que tiene que pagar primero.

| Condición | DNI | Carnet |
|---|---|---|
| Apto físico vigente | `35987654` (Lucía) o `27666333` (Diego) | "APTO FISICO: VIGENTE" |
| Apto físico vencido | `26298456` (Juan) | "APTO FISICO: VENCIDO" |
| Vigencia del carnet | cualquier socio al día | "Vigencia: ACTIVO" |

### 5. Formas de pago (Cobrar Cuota)

Cuota mensual: **$30.000**

| Forma de pago | Monto |
|---|---|
| Efectivo (10% de descuento) | $27.000 |
| Tarjeta de crédito, 3 cuotas sin interés | 3 × $10.000 |
| Tarjeta de crédito, 6 cuotas sin interés | 6 × $5.000 |

Después de pagar, **Ver Comprobante** abre el comprobante (`ComprobanteActivity`) con el N° de comprobante (empieza en 1001 y suma 1 por cada pago), la fecha, los datos del socio, la forma de pago y el total.

### 6. Actividad

La pantalla **Actividad** todavía no busca socios: solo valida el formato del documento.

| Caso | Documento | Resultado |
|---|---|---|
| ✅ Válido | `26298456` | "Buscando persona..." |
| ❌ Vacío | — | "Ingresá el documento" |
| ❌ Corto | `12345` | "Debe tener al menos 6 dígitos" |
| ❌ Con letras | `26a98456` | "Ingrese solo números" |

- **Inscribir** sin elegir ninguna tarjeta muestra "Seleccioná al menos una actividad".
- **Ir a pagar** sin elegir forma de pago muestra "Elegí una forma de pago".

---

## Puntos pendientes de conectar

| Qué | Dónde | Estado |
|---|---|---|
| Inscripción a actividades | `SociosRepository.inscribirEnActividad(socio, actividad)` | La pantalla **Actividad** todavía no la usa. Solo inscribe si el cliente está activo y con la cuota al día. Cuando se conecte, hay que quitar las actividades cargadas a mano en `SociosRepository`. |
| Búsqueda en Actividad | `ActividadActivity`, botón Buscar | Valida el documento pero no busca al socio. |
| Renovar apto físico | `SociosRepository.renovarApto(dni)` | Está creada pero ninguna pantalla la usa. |
| Registrar Cliente | `RegistrarClienteActivity` | Solo la pantalla, sin lógica de registro. |
| Botón Descargar del comprobante de cuota | `ComprobanteActivity` | El botón está en el layout pero no tiene acción. La descarga en PDF existe en `ComprobantePagoActivity` (comprobante de actividad). |
| Comprobante de actividad | `ComprobantePagoActivity` | Ninguna pantalla lo abre todavía. |
| Base de datos | `SociosRepository` | Lista en memoria. Al conectar la base se reemplazan sus funciones y las pantallas no cambian. |
