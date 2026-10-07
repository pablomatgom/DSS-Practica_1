# TiendaDSS · Práctica 1

Aplicación web de **carrito de la compra** hecha con Spring Boot, Thymeleaf y una base de datos H2.
Incluye gestión de un catálogo de productos (con stock), carrito por sesión, autenticación con roles
y exportación del catálogo como script SQL.

**Asignatura:** Desarrollo de Sistemas de Software Basados en Componentes y Servicios (DSS) · Máster en Ingeniería Informática, Universidad de Granada.

## Cómo ejecutarla

**Requisitos:** JDK 17 o superior. No hace falta instalar Maven: el proyecto incluye el *wrapper* (`mvnw`),
que descarga Maven la primera vez (requiere internet). Se ha ejecutado con JDK 21 y se ha construido
con Maven y ejecutado el `.jar` con JDK 27.

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

O generando el `.jar`:

```bash
mvnw.cmd package
java -jar target/Practica_1-0.0.1-SNAPSHOT.jar
```

La aplicación queda en **http://localhost:8080**.

## Usuarios de prueba

Están definidos en memoria en `config/SecurityConfig.java` (las propiedades `spring.security.user.*` del
`application.properties` **no se usan**, porque ese bean las sustituye).

| Usuario | Contraseña | Rol | Puede |
|---|---|---|---|
| `admin` | `admin` | `ADMIN` | Todo: gestionar y **eliminar** productos, panel de administración, exportar SQL, consola H2 |
| `manager` | `manager` | `MANAGER` | **Añadir y editar** productos (no eliminar) |
| `user` | `user` | `USER` | Lo mismo que un visitante, identificado |

Cualquier visitante, **sin iniciar sesión**, puede ver el catálogo, buscar y usar su carrito.

> Las contraseñas están escritas en el código (`{noop}`, sin cifrar) solo por ser una práctica.
> En un sistema real irían cifradas (BCrypt) y en una base de datos.

## Rutas y permisos

| Ruta | Método | Acceso | Qué hace |
|---|---|---|---|
| `/`, `/index` | GET | Público | Página de inicio |
| `/login`, `/logout` | GET / POST | Público | Inicio y cierre de sesión (el logout exige POST) |
| `/products` | GET | **Público** | Catálogo, con filtros (ver más abajo) |
| `/products/add` | GET | `ADMIN`, `MANAGER` | Formulario de producto nuevo |
| `/products/edit/{id}` | GET | `ADMIN`, `MANAGER` | Formulario de edición (404 si no existe) |
| `/products/save` | POST | `ADMIN`, `MANAGER` | Crea o actualiza (según haya `id` o no) |
| `/products/delete/{id}` | POST | `ADMIN` | Elimina un producto |
| `/cart` | GET | Público | Ver el carrito |
| `/cart/add/{id}` | POST | Público | Añade `quantity` unidades (respeta el stock) |
| `/cart/update/{id}` | POST | Público | Cambia la cantidad de una línea |
| `/cart/remove/{id}` | POST | Público | Quita una línea |
| `/admin` | GET | `ADMIN` | Panel de administración |
| `/admin/export` | GET | `ADMIN` | Descarga `products.sql` |
| `/h2-console` | GET / POST | `ADMIN` | Consola de la base de datos (solo desarrollo) |

Todas las peticiones `POST` llevan **token CSRF** (excepto la consola H2, que no lo admite).
Las rutas de gestión no definidas explícitamente caen en `anyRequest().authenticated()`.

## Funcionalidades

### Catálogo y stock
- Alta, edición y baja de productos con **nombre, precio y stock**.
- Insignia de disponibilidad: **Agotado** (0), **Pocas unidades** (1 a 5) y **En stock** (más de 5).
- Los botones de gestión solo se muestran a quien tiene el rol necesario (la seguridad real está en el servidor).

### Búsqueda y filtros
Parámetros de la URL de `/products`, combinables entre sí:

| Parámetro | Efecto |
|---|---|
| `searcher` | Nombre que **contiene** el texto, sin distinguir mayúsculas |
| `minPrice` / `maxPrice` | Rango de precio (extremos incluidos; si el mínimo supera al máximo se intercambian) |
| `inStock=true` | Solo productos con stock |

La página muestra una **etiqueta por cada filtro activo** (se quita con un clic), el número de resultados
y un mensaje distinto si el catálogo está vacío o si ningún producto coincide.

### Carrito
- Es **de sesión** (`@SessionScope`): cada visitante tiene el suyo, independiente del de los demás.
- Guarda **ids y cantidades**; el nombre, el precio y el stock se leen de la base de datos cada vez, así que
  los precios siempre están al día y un producto borrado del catálogo desaparece solo del carrito.
- La cantidad nunca supera el stock. Si se pide más, se añade lo que queda y se avisa; si el stock baja
  después de añadirlo, el carrito lo señala.
- Los métodos del servicio están `synchronized`: una misma sesión puede lanzar peticiones a la vez
  (doble clic, dos pestañas).

### Exportación SQL
`GET /admin/export` descarga **`products.sql`** con un `INSERT` por producto:

```sql
INSERT INTO product (id, name, price, stock) VALUES (1, 'Taza de cerámica', 7.5, 25);
INSERT INTO product (id, name, price, stock) VALUES (2, 'O''Brien', 49.9, 3);
```

Las comillas simples se duplican, los decimales llevan punto y el archivo va en UTF-8.

## Base de datos

- **H2 en archivo**: `./data/testdb` (se crea sola y los datos sobreviven a los reinicios).
  Se configura en `spring.datasource.url` de `src/main/resources/application.properties`.
  Para una base **en memoria** (se vacía al parar), usar `jdbc:h2:mem:testdb`.
- Hibernate crea y ajusta las tablas con `ddl-auto=update` y muestra el SQL en la consola (`show-sql=true`).
- **Consola H2** en `/h2-console` (solo `ADMIN`): URL `jdbc:h2:file:./data/testdb`, usuario `sa`, contraseña vacía.
- Para empezar de cero: parar la aplicación y borrar la carpeta `data/`.

## Estructura del proyecto

```
src/main/java/dss/practicas/practica_1/
├── Practica1Application.java
├── config/       SecurityConfig          Usuarios, roles, reglas de acceso, CSRF, login/logout
├── model/        Product                 Entidad JPA (id, name, price, stock)
├── repository/   ProductRepo             Consultas derivadas de Spring Data
├── service/      ProductService          CRUD y búsqueda con filtros
│                 CartService             Carrito de sesión (con la clase interna CartItem)
│                 DatabaseExportService   Genera el script SQL
└── controller/   Product, Cart, Admin, Login, Home

src/main/resources/
├── application.properties
├── static/css/app.css
└── templates/    products, product_form, cart, admin, login, index
    └── fragments/  head, navbar          Partes comunes reutilizables
```

La aplicación sigue una arquitectura por capas: **controlador → servicio → repositorio → base de datos**,
y cada capa solo habla con la siguiente.

## Decisiones de diseño

- **Gestión de productos solo para `ADMIN` y `MANAGER`.** El enunciado exige "autenticado" para añadir,
  editar y eliminar, pero permitirlo a cualquier usuario con sesión dejaría que un `USER` modifique el catálogo
  compartido. Se aplica el **mínimo privilegio**; el enunciado se sigue cumpliendo porque `ADMIN` implica
  estar autenticado, y `GET /products` es lo único público.
- **Eliminar es solo de `ADMIN`**, por ser la operación destructiva.
- **Consola H2 restringida a `ADMIN`.** Su acceso solo pide el usuario de la base de datos (`sa`, sin contraseña),
  así que abierta saltaría todos los roles.
- **`/error` es público**, para que los errores dentro de rutas públicas (404, 400) no se conviertan en una
  redirección al login.
- **Búsqueda con consultas derivadas de Spring Data**: `findByNameContainingIgnoreCase...` para el nombre y
  `findByPriceBetween...AndStockGreaterThanEqual...` para precio y stock. 

  Cuando hay varios filtros se cruzan los dos resultados por `id` (una intersección).
- **El carrito guarda ids**, no copias de los productos: así el precio está siempre al día.

## Limitaciones conocidas

- **El stock no baja**: no existe todavía un paso de "finalizar compra" (no estaba en el enunciado).
- **Sin validación en el servidor**: el formulario limita los valores (`min`, `required`), pero una petición
  `POST` directa podría guardar un precio o un stock negativo, o un producto sin nombre.
- **La búsqueda distingue las tildes**: `ceramica` no encuentra "cerámica".
- El precio es un `double` (lo pide el enunciado); para dinero real se usaría `BigDecimal`.
- El carrito vive en la sesión: se pierde al reiniciar la aplicación o al caducar la sesión.
- Las contraseñas están en el código; ver la nota de la sección de usuarios.
- Los estilos dependen de la CDN de Bootstrap (necesita internet).

## Pruebas

El proyecto incluye el test `Practica1ApplicationTests` (carga el contexto de Spring), que se ejecuta con
`mvnw.cmd package`. La funcionalidad se verificó además de forma manual y con peticiones HTTP
contra una instancia aparte, para cada rol (anónimo, `user`, `manager`, `admin`).
Ampliar los tests automáticos (seguridad por rol, servicio de búsqueda) queda como mejora.

## Recorrido de prueba rápido

1. Abrir `/products` sin iniciar sesión: se ve el catálogo y se puede buscar y filtrar.
2. Añadir productos al carrito con distintas cantidades y revisar `/cart`.
3. Iniciar sesión con `manager` / `manager`: aparecen "Añadir producto" y "Editar", pero no "Eliminar".
4. Iniciar sesión con `admin` / `admin`: aparece también "Eliminar" y el enlace "Administración".
5. En `/admin`, descargar `products.sql`.
6. Cerrar sesión: vuelve a la página de inicio con un aviso.
