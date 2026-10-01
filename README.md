# Club Deportivo – Equipo 1

## Rama `Gaston-CobrarCuota-ListadoClientes`

Esta rama agrega dos pantallas al menú principal (Home):

- **Cobrar Cuota** (`CobrarCuotaActivity`): busca al socio por DNI y verifica si pagó la cuota.
  - Si está al día, muestra el mensaje *"Este socio tiene la cuota al dia"* y permite ver el carnet.
  - Si la debe, muestra las formas de pago:
    - Efectivo, con 10% de descuento: $27.000
    - Tarjeta de crédito en 3 cuotas sin interés: $10.000 cada una
    - Tarjeta de crédito en 6 cuotas sin interés: $5.000 cada una
  - Al pagar se confirma el pago, se muestra un aviso y quedan disponibles **Ver Comprobante** y **Mostrar Carnet**.
- **Emitir Listado de Clientes** (`ListadoClientesActivity`): se ingresa un DNI y se muestra al cliente con sus actividades inscriptas.
  - Solo figuran los clientes **activos** y **con la cuota al día**.
  - Si el cliente no tiene actividades, se muestra *"Sin actividades inscriptas"*.

> Los datos son de prueba y están en memoria (`SociosRepository`), hasta que se conecte la base de datos.
> Los pagos se pierden al cerrar la app.

---

## DNIs de prueba

| DNI | Nombre | Situación | En Cobrar Cuota | En Listado de Clientes |
|---|---|---|---|---|
| `26298456` | Juan Perez | Al día, con actividades | "Este socio tiene la cuota al dia" + Mostrar Carnet | Figura con Musculación y Funcional |
| `35987654` | Lucía Fernández | Al día, con actividades | "Este socio tiene la cuota al dia" + Mostrar Carnet | Figura con Spinning y Zumba |
| `27666333` | Diego Sánchez | Al día, sin actividades | "Este socio tiene la cuota al dia" + Mostrar Carnet | Figura con "Sin actividades inscriptas" |
| `30123456` | María Gómez | Debe la cuota | Muestra las formas de pago | No figura; después de pagar aparece sin actividades |
| `28555111` | Carlos López | Debe la cuota | Muestra las formas de pago | No figura; después de pagar aparece sin actividades |
| `40111222` | Sofía Martínez | Debe la cuota | Muestra las formas de pago | No figura; después de pagar aparece sin actividades |
| `33444222` | Martín Rodríguez | Inactivo | Muestra las formas de pago | Mensaje "no está activo" |
| Cualquier otro | — | No existe | "No se encontró un socio con DNI …" | "No existe un cliente con DNI …" |

---

## Endpoints abiertos para conectar

Son los puntos preparados para integrar el trabajo de otros integrantes del equipo. Están marcados con `// ENDPOINT ABIERTO` y `TODO` en el código.

### 1. Comprobante de pago
- **Dónde:** `CobrarCuotaActivity.mostrarComprobante(socio, medioPago, monto, nroComprobante)`
- **Cuándo se llama:** al tocar **Ver Comprobante** en el aviso "Pago registrado".
- **Qué recibe:**
  - `socio`: el `Socio` que pagó (nombre, N° de socio, DNI)
  - `medioPago`: la forma de pago elegida, como texto
  - `monto`: el importe abonado, con el descuento ya aplicado si fue en efectivo
  - `nroComprobante`: el número de comprobante generado
- **Estado actual:** muestra un Toast de "pendiente de integración". Hay que reemplazarlo por la apertura de la pantalla de comprobante.

### 2. Carnet del socio
- **Dónde:** `CobrarCuotaActivity.mostrarCarnet(socio)`
- **Cuándo se llama:** al tocar **Mostrar Carnet**. Solo funciona si el socio tiene la cuota al día.
- **Qué envía:** el DNI del socio en el extra `CobrarCuotaActivity.EXTRA_DNI_SOCIO` (`"dni_socio"`).
- **Estado actual:** abre `CarnetActivity`, que carga nombre, N° de socio, DNI y vigencia del socio, y tiene el botón Volver funcionando. Cuando esté listo el carnet definitivo, se conecta acá.
  - Al unir las ramas puede haber un conflicto en `CarnetActivity.kt`. Lo único que se agregó ahí es la lectura del extra `dni_socio` y el botón Volver.

### 3. Inscripción a actividades (menú Actividad)
- **Dónde:** `SociosRepository.inscribirEnActividad(socio, actividad): Boolean`
- **Para qué:** el menú **Actividad** tiene que usar esta función para inscribir clientes, en lugar de cargar las actividades a mano.
- **Regla:** solo inscribe si el cliente está activo y tiene la cuota al día. Si no, devuelve `false`.
- **Flujo esperado:**
  1. El cliente se registra.
  2. Se verifica si tiene la cuota paga. Si no la tiene, no figura en el Listado de Clientes.
  3. Con la cuota paga, figura en el listado **sin actividades**.
  4. Cuando se inscribe desde el menú Actividad, el listado muestra sus actividades.
- **Estado actual:** las actividades de Juan Perez y Lucía Fernández están cargadas a mano en `SociosRepository` solo para poder probar. Hay que quitarlas cuando el menú Actividad esté conectado.

### 4. Base de datos
- **Dónde:** `SociosRepository`
- **Estado actual:** lista en memoria con datos de prueba. Al conectar la base de datos, se reemplazan:
  - `buscarPorDni`
  - `registrarPago`
  - `inscribirEnActividad`

  Las pantallas no necesitan cambios.

---

## Archivos de la rama

| Archivo | Descripción |
|---|---|
| `CobrarCuotaActivity.kt` / `activity_cobrar_cuota.xml` | Pantalla Cobrar Cuota |
| `ListadoClientesActivity.kt` / `activity_listado_clientes.xml` / `item_cliente.xml` | Pantalla Listado de Clientes |
| `Socio.kt` | Modelo de datos del socio |
| `SociosRepository.kt` | Datos de prueba y lógica de pago e inscripción |
| `CarnetActivity.kt` | Carga los datos del socio y botón Volver |
| `HomeActivity.kt` | Conecta los botones Cobrar Cuota y Emitir Listado de Clientes |
| `fondo_blanco.xml`, `borde_blanco.xml`, `color/radio_tint.xml` | Estilos nuevos (tarjetas, recuadro de pago, radio buttons) |
