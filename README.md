# Club Deportivo – Equipo 1

App Android (Kotlin) para la gestión de un club deportivo: login, registro de clientes, cobro de cuotas, carnet de socio, inscripción a actividades y listados de clientes y deudores.

## Datos

La app usa una base **SQLite** (`ClubDBHelper`, archivo `Club.db`). Los datos quedan guardados aunque se cierre la app.

| Pantalla | De dónde lee los datos |
|---|---|
| Login, Cobrar Cuota, Carnet, Listado de Clientes, Actividad (buscar) | Base de datos (`ClubDBHelper`) |
| Listado de Deudores, Comprobante de actividad, apto físico del carnet | Memoria (`SociosRepository`), pendiente de pasar a la base |

> **Importante:** si ya tenías la app instalada con una versión anterior de la base, **desinstalala antes de correrla**. La base se crea una sola vez y no se actualiza sola.

---

## Usuarios de prueba

### 1. Login

El login valida contra la tabla `usuarios`. Ingresan:

| Rol | Email | Contraseña |
|---|---|---|
| Administrador | `admin@fitnesscenter.com` | `Club1234` |
| Empleado | `empleado@fitnesscenter.com` | `Club1234` |

Antes de consultar la base, el login revisa el formato. Estos casos dan error:

| Caso | Ejemplo | Resultado |
|---|---|---|
| Email inválido | `admin` | "Ingresá un correo válido" |
| Contraseña corta | `Ab1` | "Al menos 6 caracteres" |
| Sin mayúscula / minúscula / número | `club1234` | El mensaje de la regla que falta |
| Formato válido pero no registrado | `otro@fitnesscenter.com` | "Usuario o contraseña incorrectos" |

### 2. Cobrar Cuota (con base de datos)

Al abrir **Cobrar Cuota** se cargan en la base estos clientes de prueba. Si ya existen no se duplican, así que la pantalla se puede abrir muchas veces.

| DNI | Cliente | N° socio | Tipo | Cuota al cargarse |
|---|---|---|---|---|
| `26298456` | Juan Perez | 0001 | Socio | ✅ Al día (vence en 20 días) |
| `35987654` | Lucía Fernández | 0004 | Socio | ✅ Al día (vence en 1 mes) |
| `27666333` | Diego Sánchez | 0007 | Socio | ✅ Al día (vence en 5 días) |
| `30123456` | María Gómez | 0002 | Socio | ❌ Debe (venció hace 10 días) |
| `28555111` | Carlos López | 0003 | Socio | ❌ Debe (venció hace 40 días) |
| `40111222` | Sofía Martínez | 0006 | Socio | ❌ Debe (venció hace 3 días) |
| `33444222` | Martín Rodríguez | — | No socio | No paga cuota mensual |

- Los vencimientos se calculan **la primera vez** que se cargan, contando desde ese día. Con el paso del tiempo un socio al día puede pasar a deber, igual que en la vida real.
- Para volver a los datos iniciales, desinstalá la app o borrá sus datos desde los ajustes del celular.

**Regla:** un socio está **al día** si la fecha de vencimiento de su cuota (`socios.fecha_vencimiento_cuota`) es hoy o una fecha posterior.

#### Qué pasa con cada caso

| Caso | DNI | Resultado |
|---|---|---|
| Socio al día | `26298456` | "Este socio tiene la cuota al dia", con el vencimiento. Se puede tocar **Mostrar Carnet**. |
| Socio que debe | `30123456` | Muestra cuándo venció y las formas de pago |
| Pagar la cuota | `30123456` | Elegir forma de pago → **Pagar Cuota** → confirmar → "Pago registrado" con el próximo vencimiento → **Ver Comprobante** |
| Buscar de nuevo después de pagar | `30123456` | Ya figura al día, aunque cierres y vuelvas a abrir la app |
| No socio | `33444222` | "No es socio: no paga cuota mensual, abona por actividad" |
| No existe | `11111111` | "No se encontró un cliente con DNI …" |
| DNI vacío o con letras | — / `26a98456` | "Ingresá un DNI" / "Ingresá solo números" |
| Pagar sin elegir forma de pago | — | "Seleccioná una forma de pago" |
| Mostrar Carnet de un socio que debe | `30123456` | Avisa que tiene que tener la cuota al día |

#### Formas de pago

Cuota mensual: **$30.000**

| Forma de pago | Monto |
|---|---|
| Efectivo (10% de descuento) | $27.000 |
| Tarjeta de crédito, 3 cuotas sin interés | 3 × $10.000 |
| Tarjeta de crédito, 6 cuotas sin interés | 6 × $5.000 |

#### Qué se guarda en la base al pagar

Todo en una sola transacción: si algo falla, no se guarda nada y la app avisa.

1. **`cuota`:** una fila con el monto de la cuota, el nuevo vencimiento y el estado "Pagada".
2. **`pagos`:** una fila con el monto abonado (con el descuento si fue en efectivo), la fecha y el N° de comprobante. Los comprobantes empiezan en 1001 y suman 1 por cada pago.
3. **`socios`:** el vencimiento de la cuota pasa a **hoy + 1 mes**.

**Ver Comprobante** abre `ComprobanteActivity` con el N° de comprobante, la fecha, el N° de socio, nombre, apellido, DNI, la forma de pago y el total.

### 3. Carnet y QR

El carnet lee los datos de la **base**: nombre, N° de socio, DNI y vigencia. La vigencia es **ACTIVO** si la cuota está al día e **INACTIVO** si no.

Hay dos formas de abrirlo desde **Cobrar Cuota**:

| Cómo | Qué hacer |
|---|---|
| **Mostrar Carnet** | Buscá un socio al día por DNI y tocá el botón. Si debe la cuota, avisa que tiene que pagar primero. |
| **Escanear Carnet** | Abre la cámara (la primera vez pide permiso). Al leer el QR de un carnet abre directo el carnet de ese socio y lo deja cargado en Cobrar Cuota, por si hay que cobrarle. |

- **Contenido del QR:** `CLUBDEPORTIVO-DNI:` + el DNI del socio, por ejemplo `CLUBDEPORTIVO-DNI:30123456`.
- **Otros QR:** si escaneás un QR que no es de un carnet del club, la app avisa y no abre nada.
- **Socio inexistente:** si el QR es del club pero el DNI no corresponde a ningún socio, también avisa.

**Apto físico:** la base todavía no guarda la fecha de presentación, así que se toma de la memoria (`SociosRepository`). Los 7 clientes de prueba muestran VIGENTE o VENCIDO según esa fecha. Un socio que exista solo en la base muestra "SIN DATOS".

#### Probar el escáner en el emulador

La cámara del emulador muestra una habitación virtual, y en ella se puede poner una imagen del QR:

1. Abrí el carnet de un socio con **Mostrar Carnet** y sacá una captura con el botón 📷 de la barra lateral del emulador. Se guarda en tu compu.
2. En el emulador abrí **⋯ (Extended controls) → Camera** y, en **Virtual scene images**, cargá la captura en **Wall** o **Table**.
3. En la app tocá **Escanear Carnet**. Con la tecla **Alt** apretada, movete con el mouse y **W/A/S/D** hasta enfocar la pared o la mesa donde quedó la imagen.

> Si la cámara trasera del emulador no es "VirtualScene", cambiala en **Device Manager → editar el emulador → Show Advanced Settings → Camera → Back: VirtualScene**.

---

## Puntos pendientes

| Qué | Dónde | Estado |
|---|---|---|
| Fecha del apto físico | `CarnetActivity` / tabla `clientes` | La base solo tiene `aptoFisico` (0/1), sin fecha. Hasta agregarla, el carnet toma la fecha de `SociosRepository`. |
| Comprobante de actividad desde la base | `ComprobantePagoActivity` | Busca al socio en `SociosRepository`. Ninguna pantalla lo abre todavía. |
| Listado de Deudores desde la base | `ListadoDeudoresActivity` | Lee de `SociosRepository`. Cobrar Cuota le copia el estado de la cuota al buscar y al pagar, pero la memoria se reinicia al cerrar la app. |
| Registrar Cliente | `RegistrarClienteActivity` | Valida los campos pero todavía no guarda en la base. |
| Inscripción a actividades | `ActividadActivity` | Busca al cliente en la base, pero la inscripción y el pago no se guardan. |
| Botón Descargar del comprobante de cuota | `ComprobanteActivity` | El botón está en el layout pero no tiene acción. |
| `onUpgrade` de la base | `ClubDBHelper` | Está sin implementar (`TODO`). Si se sube la versión de la base, la app se cierra. Hasta implementarlo, para cambios en las tablas hay que desinstalar la app. |
