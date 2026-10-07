# Plataforma de Gestión y Control de Servicios Funerarios

Proyecto del curso **Desarrollo de Aplicaciones Web y Patrones (SC-403)**
Universidad Fidélitas · Ingeniería en Sistemas de Computación · Tercer cuatrimestre, 2026
Docente: MSc. Allam Mauricio Fernández Rivera

> Estado: 🚧 En planificación (Avance 1: historias de usuario, prototipo y modelo preliminar)

---

## 📌 Descripción

Aplicación web con base de datos relacional centralizada para **Memoriales del Valle**, empresa costarricense de servicios funerarios con dos sedes propias y convenios con seis cementerios de la GAM. La plataforma sustituye los registros físicos y las hojas de cálculo independientes con los que opera actualmente la organización.

### Problemática
- **Datos fragmentados:** múltiples archivos sin vinculación, duplicidad de registros y sin trazabilidad de contratos.
- **Inconsistencias financieras:** seguimiento manual de cuotas y saldos, con errores de cobro y control deficiente de la morosidad.
- **Gestión de infraestructura ineficiente:** control manual de nichos y unidades de sepultura, con riesgo de asignaciones dobles.
- **Seguridad débil:** no hay autenticación formal para proteger datos personales (Ley n.º 8968).
- **Toma de decisiones lenta:** los reportes gerenciales se consolidan a mano.

## 🎯 Objetivo general

Desarrollar una plataforma web con base de datos relacional centralizada que gestione de forma integrada los servicios funerarios, planes de previsión, financiamientos, inventario y disponibilidad de unidades de sepultura, garantizando la integridad y la seguridad de la información y facilitando la toma de decisiones gerenciales.

## 👤 Usuarios del sistema

| Rol | Responsabilidades principales |
|-----|-------------------------------|
| **Administrador** | Gestiona usuarios y roles, la seguridad del sistema y consulta los reportes gerenciales. |
| **Encargado de Ventas** | Registra clientes, gestiona contratos de planes de previsión, ventas directas y facturas. |
| **Personal Operativo** | Registra expedientes de fallecidos, programa servicios y sepelios, reserva unidades de sepultura y actualiza el inventario. |
| **Contador** | Procesa pagos de cuotas, supervisa saldos de financiamiento y monitorea contratos en mora. |

Las familias que contratan servicios o planes son **beneficiarias indirectas** y no acceden al sistema.

## ⚙️ Módulos (alcance)

- [ ] Seguridad y administración (autenticación, usuarios, roles)
- [ ] Clientes y contratos de planes de previsión
- [ ] Ventas y facturación
- [ ] Expedientes y servicios funerarios / sepelios
- [ ] Unidades de sepultura (reserva y disponibilidad)
- [ ] Inventario con alertas de stock mínimo
- [ ] Financiamiento y cobros (cuotas, saldos, morosidad)
- [ ] Reportes gerenciales

**Fuera de alcance:** portal para familias, integración con comprobantes electrónicos de Hacienda, pasarelas de pago, contabilidad/planillas/RR. HH. y apps móviles nativas.

## 🗄️ Modelo de datos (preliminar)

Modelo relacional en **3FN** elaborado en draw.io. Convenciones: prefijo `FIDE_`, sufijo `_TB`, columnas en mayúsculas y eliminación lógica mediante `FIDE_ESTADOS_TB`.

| Bloque | Entidades principales |
|--------|-----------------------|
| Personas y contacto | `FIDE_PERSONAS_TB`, `FIDE_CLIENTES_TB`, `FIDE_FALLECIDOS_TB`, `FIDE_CORREO_TB`, `FIDE_TELEFONO_TB`, `FIDE_DIRECCIONES_TB`, `FIDE_PROVINCIA_TB`, `FIDE_CANTON_TB`, `FIDE_DISTRITO_TB` |
| Servicios y sepelios | `FIDE_SERVICIOS_TB`, `FIDE_TIPOS_SERVICIO_TB`, `FIDE_SERVICIOS_COSTOS_TB`, `FIDE_SEPELIOS_TB` |
| Planes y financiamiento | `FIDE_PLANES_PREVISION_TB`, `FIDE_PLANES_SERVICIOS_TB`, `FIDE_PLANES_COSTOS_TB`, `FIDE_FINANCIAMIENTOS_TB`, `FIDE_CUOTAS_TB`, `FIDE_PAGOS_TB`, `FIDE_TIPO_METODO_PAGO_TB` |
| Cementerios e inventario | `FIDE_CEMENTERIOS_TB`, `FIDE_UNIDADES_SEPULTURA_TB`, `FIDE_TIPOS_UNIDAD_TB`, `FIDE_PRODUCTOS_TB`, `FIDE_SERVICIO_PRODUCTO_TB` |
| Ventas y facturación | `FIDE_VENTAS_TB`, `FIDE_DETALLE_VENTA_TB`, `FIDE_FACTURACION_TB` |
| Usuarios y acceso | `FIDE_ROLES_TB`, `FIDE_USUARIOS_TB`, `FIDE_ESTADOS_TB` |

📎 Diagrama ER completo: [Google Drive](https://drive.google.com/file/d/1mI9dlWyvXNulpM9dzGw3hhXqacS1UK_8/view?usp=sharing)
📎 Historias de usuario: [Apéndice A](https://docs.google.com/document/d/1IHDob6zCsJgsrWz-IcHNy-TcZDQAFLMJ/edit?usp=sharing&ouid=112324371036536014488&rtpof=true&sd=true)

## 🛠️ Tecnologías

| Capa | Tecnología |
|------|-----------|
| Frontend | Por definir |
| Backend | Por definir |
| Base de datos | Por definir (relacional) |
| Prototipo | Por definir |
| Modelado de datos | draw.io |
| Control de versiones | Git + GitHub |

## 👥 Integrantes

| Nombre | Usuario de GitHub |
|--------|-------------------|
| Jafet Oviedo Trigueros | [@usuario] |
| Daniela Muñoz Valverde | [@usuario] |
| Austin Agüero Montero | [@usuario] |

## 📂 Estructura del repositorio (preliminar)

```
/
├── docs/          # Planteamiento, historias de usuario, modelo de datos, mapa de navegación
├── prototipo/     # Enlaces o exportaciones del prototipo
├── src/           # Código fuente (a partir de los siguientes avances)
└── README.md
```

---

## 🌿 Acuerdo de trabajo por ramas

| Rama | Propósito |
|------|-----------|
| `main` | Versión estable y entregable. **No se hace commit directo.** |
| `develop` | Integración del trabajo del equipo. |
| `feature/<descripcion-corta>` | Una rama por funcionalidad o tarea (ej. `feature/modulo-inventario`). |
| `fix/<descripcion-corta>` | Corrección de errores. |
| `docs/<descripcion-corta>` | Cambios de documentación. |

### Flujo
1. Actualizar `develop` antes de empezar:
   ```bash
   git checkout develop
   git pull origin develop
   ```
2. Crear la rama de trabajo desde `develop`:
   ```bash
   git checkout -b feature/nombre-tarea
   ```
3. Hacer commits pequeños y descriptivos.
4. Subir la rama y abrir un **Pull Request hacia `develop`**.
5. Al menos **un integrante distinto al autor** revisa y aprueba el PR antes del merge.
6. Al cerrar cada avance, se integra `develop` → `main` mediante PR.

### Convención de commits
```
tipo: descripción breve en presente
```
Tipos: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`
Ejemplo: `feat: agregar reserva de unidades de sepultura`

### Reglas básicas
- No subir credenciales, contraseñas ni archivos `.env`.
- Usar solo datos ficticios o anonimizados en pruebas (Ley n.º 8968).
- Resolver conflictos en la rama propia antes de pedir revisión.
- Borrar la rama después del merge.

---

## 📅 Avances del curso

| Avance | Semana | Contenido | Estado |
|--------|--------|-----------|--------|
| Avance 1 | 5 | Planteamiento, historias de usuario, prototipo, modelo preliminar, repositorio | 🚧 En progreso |
