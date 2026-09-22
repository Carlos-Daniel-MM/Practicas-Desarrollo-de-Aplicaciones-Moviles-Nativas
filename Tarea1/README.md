# Práctica: Implementación y Evaluación de Model Context Protocol (MCP)

---

## 1. Datos de Identificación

- **Nombre completo:** Carlos Daniel [Completar Apellidos]
- **Número de boleta:** [Completar Número de Boleta]
- **Grupo:** [Completar Grupo]
- **Asignatura / Curso:** Inteligencia Artificial / Desarrollo de Sistemas Agénticos
- **Fecha de realización:** Septiembre de 2026

---

## 2. Resumen de la Actividad e Índice de Documentos

### 2.1. Resumen Ejecutivo
El presente informe documenta la implementación, configuración y evaluación práctica del protocolo abierto **Model Context Protocol (MCP)** en un entorno de ejecución real. Para el desarrollo de la práctica se seleccionó **Google Antigravity** como cliente anfitrión, integrando el servidor oficial de sistema de archivos (`@modelcontextprotocol/server-filesystem`). 

El servidor fue confinado a un directorio de trabajo específico (`/home/carlos/Documentos/mcp-sandbox`), evitando deliberadamente exponer la raíz del disco o el directorio personal del usuario. Se demostraron de forma empírica las cinco operaciones fundamentales de gestión de archivos (listar, crear, leer, modificar y buscar), se puso a prueba la frontera de seguridad intentando acceder a archivos no autorizados del sistema operativo y, como componente opcional (+10 puntos extra), se desarrolló un servidor MCP personalizado desde cero con dos herramientas operativas empleando el SDK oficial.

### 2.2. Índice de Documentos de la Carpeta `docs/`
Para una consulta modular y técnica a bajo nivel, los registros y especificaciones se encuentran desglosados en los siguientes documentos auxiliares:

- 📄 [01-arquitectura-cliente.md](docs/01-arquitectura-cliente.md): Justificación técnica del cliente seleccionado (Google Antigravity), modelo de integración y diagrama de secuencia del intercambio de mensajes JSON-RPC 2.0.
- 📄 [02-operaciones-filesystem.md](docs/02-operaciones-filesystem.md): Trazas detalladas, argumentos de entrada y respuestas de las cinco operaciones ejecutadas sobre el sistema de archivos delimitado.
- 📄 [03-limites-seguridad.md](docs/03-limites-seguridad.md): Análisis exhaustivo del mecanismo de contención (*Boundary Enforcement*), canonicalización de rutas y pruebas de denegación pre-syscall.
- 📄 [04-servidor-personalizado.md](docs/04-servidor-personalizado.md): Especificación técnica, código y pruebas del servidor MCP personalizado implementado para monitoreo del sistema y análisis textual (+10 pts extra).

---

## 3. Tabla Comparativa: MCP vs. API Tradicional (REST / GraphQL)

| Criterio de Comparación | Model Context Protocol (MCP) | API Tradicional (REST / GraphQL) |
| :--- | :--- | :--- |
| **Propósito Principal** | Conectar modelos de IA generativa con herramientas, recursos y contexto de ejecución de manera estandarizada. | Permitir la comunicación e intercambio de datos estructurados entre aplicaciones cliente-servidor convencionales. |
| **Descubrimiento de Capacidades (Discovery)** | **Dinámico y Autodescriptivo**: El servidor expone sus herramientas (`tools/list`), recursos y prompts mediante esquemas estándar JSON Schema consumibles directamente por el LLM en tiempo de ejecución. | **Estático / Manual**: Requiere documentación externa (OpenAPI/Swagger) y desarrollo de código específico en el cliente para cada endpoint. |
| **Mecanismo de Transporte** | Diseñado para canales locales bidireccionales por **Stdio** (`stdin`/`stdout`) o remotos mediante **SSE** (Server-Sent Events) sobre HTTP. | Típicamente unidireccional por petición/respuesta síncrona sobre HTTP/HTTPS (REST) o WebSockets en casos específicos. |
| **Formato de Mensajería** | Protocolo estricto **JSON-RPC 2.0** con estados bien definidos (`initialize`, `tools/list`, `tools/call`). | Cargas JSON libres regidas por verbos HTTP (GET, POST, PUT, DELETE) o queries GraphQL. |
| **Integración con IA / LLMs** | **Nativa**: Las herramientas se inyectan automáticamente en la interfaz de *Function Calling* del modelo sin adaptadores intermedios. | **Requiere Wrapper**: Es necesario programar capas de integración (*tool definitions*) manuales en el orquestador del LLM (ej. LangChain). |
| **Límites de Seguridad y Sandbox** | **Contención por frontera**: El servidor aplica controles rigurosos en el punto de ejecución local (ej. validación estricta de rutas de archivos). | **Autenticación y Autorización**: Basado en tokens (JWT, API Keys, OAuth) y políticas de cortafuegos de red. |
| **Persistencia de Sesión** | Mantiene un ciclo de vida con negociación inicial de capacidades (`handshake`) entre el proceso cliente y el servidor hijo. | Predominantemente *stateless* (sin estado entre peticiones consecutivas). |

---

## 4. Instrucciones de Instalación Paso a Paso (Reproducibles)

Esta guía permite reproducir el entorno completo en una instalación limpia del sistema operativo.

### 4.1. Entorno del Sistema Operativo y Versiones
- **Sistema Operativo:** Linux (Ubuntu 22.04 LTS / Debian 12 x86_64)
- **Node.js:** `v18.19.1` (o superior)
- **npm:** `9.2.0` (o superior)
- **Cliente MCP:** Google Antigravity (o compatible: Claude Desktop / VS Code con extensión MCP)
- **SDK MCP Utilizado:** `@modelcontextprotocol/sdk` versión `^1.6.0`

### 4.2. Paso 1: Instalación de Requisitos Previos
En una terminal limpia con Linux, asegúrese de contar con Node.js y npm:
```bash
sudo apt update && sudo apt install -y nodejs npm
node -v   # Debe mostrar >= v18.0.0
npm -v    # Debe mostrar >= 9.0.0
```

### 4.3. Paso 2: Creación del Directorio Delimitado (Sandbox)
Cree el directorio que actuará como frontera segura para el servidor de archivos:
```bash
mkdir -p /home/carlos/Documentos/mcp-sandbox
```

### 4.4. Paso 3: Configuración del Archivo `mcp_config.json`
El archivo de configuración debe crearse en la ruta global de configuración del cliente:
- **Ruta exacta:** `~/.gemini/config/mcp_config.json`

Cree o modifique dicho archivo con el siguiente contenido JSON:
```json
{
  "mcpServers": {
    "filesystem": {
      "command": "npx",
      "args": [
        "-y",
        "@modelcontextprotocol/server-filesystem",
        "/home/carlos/Documentos/mcp-sandbox"
      ]
    },
    "custom-system-tools": {
      "command": "node",
      "args": [
        "/home/carlos/Documentos/mcp-sandbox/custom-mcp-server/server.js"
      ]
    }
  }
}
```

> **Nota de seguridad:** El argumento `"/home/carlos/Documentos/mcp-sandbox"` establece el único directorio accesible. Ninguna subcarpeta ajena ni archivos raíz podrán ser leídos o alterados.

### 4.5. Paso 4: Instalación del Servidor Personalizado (+10 Puntos Extra)
Para habilitar el servidor opcional de herramientas del sistema:
```bash
mkdir -p /home/carlos/Documentos/mcp-sandbox/custom-mcp-server
cd /home/carlos/Documentos/mcp-sandbox/custom-mcp-server
npm init -y
npm install @modelcontextprotocol/sdk
```

Configure `package.json` para habilitar ES Modules agregando `"type": "module"`, y coloque el archivo `server.js` con las dos herramientas (`get_system_metrics` y `text_analyzer`).

### 4.6. Paso 5: Verificación de Reconocimiento en el Cliente
Inicie el cliente Google Antigravity. Las herramientas aparecerán listadas bajo el panel de herramientas disponibles, confirmando la conexión Stdio.

---

## 5. Evidencias de Ejecución

A continuación se presentan los registros exactos de las pruebas ejecutadas, junto con las indicaciones para las capturas de pantalla correspondientes.

### 5.1. Operación 1: Listar el Contenido del Directorio Autorizado
- **Herramienta invocada:** `list_directory`
- **Argumentos enviados:**
  ```json
  { "path": "/home/carlos/Documentos/mcp-sandbox" }
  ```
- **Resultado obtenido:**
  ```text
  [FILE] README.md
  [DIR] custom-mcp-server
  [FILE] ejemplo.txt
  [DIR] docs
  ```

```
+-----------------------------------------------------------------------------------+
|                           EVIDENCIA FOTOGRÁFICA 01                                |
|  [Pegar aquí Captura de Pantalla mostrando la ejecución de list_directory         |
|   en la interfaz de Google Antigravity mostrando los archivos listados]           |
+-----------------------------------------------------------------------------------+
```

---

### 5.2. Operación 2: Crear un Archivo Nuevo y Escribir Contenido
- **Herramienta invocada:** `write_file`
- **Argumentos enviados:**
  ```json
  {
    "path": "/home/carlos/Documentos/mcp-sandbox/ejemplo.txt",
    "content": "Este es un archivo de prueba creado a traves del servidor MCP de sistema de archivos en Google Antigravity.\nFecha de creacion: 2026-09-21\nUsuario: Carlos\nEstado: Operacion exitosa."
  }
  ```
- **Resultado devuelto por el servidor:**
  ```text
  Successfully wrote to /home/carlos/Documentos/mcp-sandbox/ejemplo.txt
  ```

```
+-----------------------------------------------------------------------------------+
|                           EVIDENCIA FOTOGRÁFICA 02                                |
|  [Pegar aquí Captura de Pantalla mostrando la llamada a write_file con éxito]     |
+-----------------------------------------------------------------------------------+
```

---

### 5.3. Operación 3: Leer un Archivo Existente
- **Herramienta invocada:** `read_text_file`
- **Argumentos enviados:**
  ```json
  { "path": "/home/carlos/Documentos/mcp-sandbox/ejemplo.txt" }
  ```
- **Resultado devuelto por el servidor:**
  ```text
  Este es un archivo de prueba creado a traves del servidor MCP de sistema de archivos en Google Antigravity.
  Fecha de creacion: 2026-09-21
  Usuario: Carlos
  Estado: Operacion exitosa.
  ```

```
+-----------------------------------------------------------------------------------+
|                           EVIDENCIA FOTOGRÁFICA 03                                |
|  [Pegar aquí Captura de Pantalla mostrando la lectura del archivo ejemplo.txt]   |
+-----------------------------------------------------------------------------------+
```

---

### 5.4. Operación 4: Modificar un Archivo Existente
- **Herramienta invocada:** `edit_file`
- **Argumentos enviados:**
  ```json
  {
    "path": "/home/carlos/Documentos/mcp-sandbox/ejemplo.txt",
    "edits": [
      {
        "oldText": "Estado: Operacion exitosa.",
        "newText": "Estado: Operacion modificada exitosamente mediante MCP edit_file.\nLinea adicional: Demostracion de modificacion completada."
      }
    ]
  }
  ```
- **Resultado devuelto por el servidor (Diff generado):**
  ```diff
  Index: /home/carlos/Documentos/mcp-sandbox/ejemplo.txt
  ===================================================================
  --- /home/carlos/Documentos/mcp-sandbox/ejemplo.txt	original
  +++ /home/carlos/Documentos/mcp-sandbox/ejemplo.txt	modified
  @@ -1,4 +1,5 @@
   Este es un archivo de prueba creado a traves del servidor MCP de sistema de archivos en Google Antigravity.
   Fecha de creacion: 2026-09-21
   Usuario: Carlos
  -Estado: Operacion exitosa.
  \ No newline at end of file
  +Estado: Operacion modificada exitosamente mediante MCP edit_file.
  +Linea adicional: Demostracion de modificacion completada.
  \ No newline at end of file
  ```

```
+-----------------------------------------------------------------------------------+
|                           EVIDENCIA FOTOGRÁFICA 04                                |
|  [Pegar aquí Captura de Pantalla mostrando la modificación y el diff unificado]   |
+-----------------------------------------------------------------------------------+
```

---

### 5.5. Operación 5: Buscar un Archivo por Nombre o Contenido
- **Herramienta invocada:** `search_files`
- **Argumentos enviados:**
  ```json
  {
    "path": "/home/carlos/Documentos/mcp-sandbox",
    "pattern": "*.txt"
  }
  ```
- **Resultado devuelto por el servidor:**
  ```text
  /home/carlos/Documentos/mcp-sandbox/ejemplo.txt
  ```

```
+-----------------------------------------------------------------------------------+
|                           EVIDENCIA FOTOGRÁFICA 05                                |
|  [Pegar aquí Captura de Pantalla con la coincidencia devuelta por search_files]   |
+-----------------------------------------------------------------------------------+
```

---

### 5.6. Prueba del Límite de Seguridad (Boundary Enforcement)
Se solicitó al modelo la lectura de dos archivos críticos ubicados deliberadamente fuera de la frontera autorizada:
1. Archivo del sistema: `/etc/os-release`
2. Archivo del directorio del usuario: `/home/carlos/.bashrc`

#### Respuestas obtenidas del servidor:
```text
Encountered error in tool execution: Access denied - path outside allowed directories: /etc/os-release not in /home/carlos/Documentos/mcp-sandbox
```
```text
Encountered error in tool execution: Access denied - path outside allowed directories: /home/carlos/.bashrc not in /home/carlos/Documentos/mcp-sandbox
```

```
+-----------------------------------------------------------------------------------+
|                           EVIDENCIA FOTOGRÁFICA 06                                |
|  [Pegar aquí Captura de Pantalla con el mensaje de error de denegación de acceso] |
+-----------------------------------------------------------------------------------+
```

#### Explicación del Mecanismo de Bloqueo:
El servidor MCP implementa una política estricta de lista blanca (*whitelist validation*). Antes de invocar cualquier syscall del sistema operativo (`fs.readFile`), el servidor resuelve la ruta canónica del archivo mediante `path.resolve()` y `fs.realpathSync()`, eliminando cualquier secuencia de escape de directorios (`../`). Posteriormente, comprueba si la ruta resultante inicia exactamente con la cadena de alguno de los directorios autorizados (`allowedDirectories`). Al no coincidir el prefijo, la ejecución se aborta en la capa del protocolo sin llegar a consultar el kernel de Linux.

---

### 5.7. Evidencia del Servidor Personalizado (+10 Puntos Extra)
Invocación de las dos herramientas implementadas en `server.js`:

#### Herramienta `get_system_metrics`:
```json
{
  "platform": "linux",
  "architecture": "x64",
  "hostname": "carlosdaniel",
  "uptime_hours": 0.35,
  "memory": {
    "total_mb": 7252,
    "free_mb": 3195,
    "used_mb": 4057,
    "usage_percentage": "55.9%"
  },
  "cpu": {
    "model": "AMD Ryzen 5 5500U with Radeon Graphics",
    "cores": 12,
    "loadavg_1_5_15": [1.42, 1.13, 0.67]
  }
}
```

#### Herramienta `text_analyzer`:
```json
{
  "lines": 1,
  "word_count": 18,
  "character_count": 129,
  "characters_without_spaces": 112,
  "estimated_reading_time_minutes": 0.09,
  "top_words": [
    { "word": "mcp", "count": 2 },
    { "word": "protocolo", "count": 1 },
    { "word": "permite", "count": 1 },
    { "word": "conectar", "count": 1 },
    { "word": "modelos", "count": 1 }
  ]
}
```

```
+-----------------------------------------------------------------------------------+
|                           EVIDENCIA FOTOGRÁFICA 07                                |
|  [Pegar aquí Captura de Pantalla mostrando las herramientas propias en ejecución] |
+-----------------------------------------------------------------------------------+
```

---

## 6. Conclusiones Personales

La realización de esta práctica ha permitido constatar la relevancia crítica de contar con un estándar abierto como **Model Context Protocol (MCP)** en el ecosistema actual de la inteligencia artificial. Previo a la llegada de MCP, la integración de modelos de lenguaje con herramientas externas se encontraba severamente fragmentada: cada proveedor (OpenAI, Anthropic, Google, etc.) y cada framework agéntico (LangChain, AutoGen, CrewAI) proponían interfaces incompatibles y esquemas propietarios, obligando a reescribir conectores para cada cliente.

MCP resuelve este problema desacoplando limpiamente el **cliente de consumo** del **proveedor de capacidades**. A través de una interfaz de bajo acoplamiento basada en JSON-RPC 2.0 y JSON Schema, los servidores pueden desarrollarse una única vez y funcionar de inmediato en clientes tan diversos como Google Antigravity, Claude Desktop o VS Code.

Desde el punto de vista de la **ciberseguridad**, la práctica demostró que el sandboxing a nivel de protocolo es indispensable. Los modelos de lenguaje pueden generar rutas alucinadas o manipuladas por prompt injection; sin embargo, al existir una barrera determinista en el servidor MCP que verifica canónicamente las rutas antes de cualquier llamada al sistema, la integridad del equipo anfitrión permanece resguardada. Finalmente, el desarrollo del servidor personalizado evidenció lo accesible que resulta extender las capacidades del asistente mediante pocas líneas de código gracias a los SDKs oficiales.

---

## 7. Referencias en Formato APA

- Anthropic. (2024). *Model Context Protocol Specification* (Versión 2024-11-05). Model Context Protocol Working Group. https://modelcontextprotocol.io/specification/2024-11-05
- Google DeepMind. (2026). *Google Antigravity: Architecture and Model Context Protocol Integration Guide*. Google Developer Documentation. https://antigravity.google/docs/mcp
- Model Context Protocol Authors. (2024). *Filesystem Server for MCP* (@modelcontextprotocol/server-filesystem) [Software de código abierto]. GitHub. https://github.com/modelcontextprotocol/servers/tree/main/src/filesystem
- Model Context Protocol Authors. (2024). *TypeScript SDK for the Model Context Protocol* (@modelcontextprotocol/sdk) [Software de código abierto]. GitHub. https://github.com/modelcontextprotocol/typescript-sdk
- Node.js Contributors. (2024). *Node.js v18 LTS Documentation: Child Process and OS Modules*. OpenJS Foundation. https://nodejs.org/docs/latest-v18.x/api/
