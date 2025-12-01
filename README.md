# SpotFinder - Aplicación Android

SpotFinder es una aplicación móvil nativa para Android diseñada para ayudar a los usuarios a descubrir, compartir y calificar "spots" o espacios públicos para socializar. Este proyecto nace de la necesidad de tener una fuente de información centralizada y confiable, enriquecida por la propia comunidad.

## Desarolladores
Este proyecto está siendo desarollado por:
*  **Tomás Olmos Vásquez**
*  **Ignacio Kort Kort**

## Funcionalidades

*   **Arquitectura MVVM**: La aplicación está construida sobre un patrón Model-View-ViewModel robusto, separando la lógica de la interfaz de usuario.
*   **Base de Datos Local**: Se utiliza **Room** para la persistencia de datos locales, permitiendo almacenar usuarios y spots directamente en el dispositivo.
*   **Autenticación de Usuarios**:
    *   **Registro de Usuarios**: Creación de nuevas cuentas que se almacenan de forma segura en la base de datos, con manejo de errores para correos ya existentes.
    *   **Inicio de Sesión**: Validación de credenciales contra la base de datos local.
*   **Gestión de Spots**:
    *   **Visualización de Spots**: Pantalla principal que muestra una lista de todos los spots guardados en la base de datos.
    *   **Creación de Spots**: Un formulario permite a los usuarios añadir nuevos spots, incluyendo nombre, descripción y comuna.
*   **Integración de Hardware**:
    *   **Cámara y Galería**: Los usuarios pueden añadir una imagen a un nuevo spot, ya sea tomándola con la cámara o seleccionándola desde la galería del dispositivo.
*   **Interfaz de Usuario Moderna**: La UI está construida enteramente con **Jetpack Compose**, el framework declarativo moderno de Android.

## Endpoints Usados

Actualmente, la aplicación funciona de manera local y no consume endpoints externos. La persistencia de datos se gestiona a través de una base de datos Room en el dispositivo.

## Instrucciones para Ejecutar el Proyecto

1.  **Clonar el Repositorio**: 
    ```bash
    git clone https://github.com/ignaciokort/SpotFinder-android.git
    ```
2.  **Abrir en Android Studio**:
    *   Abre Android Studio.
    *   Selecciona `File > Open` y navega hasta la carpeta del proyecto clonado.
3.  **Sincronizar Gradle**: Android Studio debería sincronizar automáticamente el proyecto. Si no lo hace, puedes forzar la sincronización desde `File > Sync Project with Gradle Files`.
4.  **Ejecutar la Aplicación**:
    *   Selecciona un emulador o un dispositivo físico.
    *   Presiona el botón `Run 'app'` (o `Shift + F10`).

## Stack Tecnológico

*   **Lenguaje**: [Kotlin](https://kotlinlang.org/)
*   **UI Toolkit**: [Jetpack Compose](https://developer.android.com/jetpack/compose)
*   **Arquitectura**: MVVM (Model-View-ViewModel)
*   **Asincronía**: [Kotlin Coroutines & Flow](https://kotlinlang.org/docs/coroutines-overview.html)
*   **Base de Datos**: [Jetpack Room](https://developer.android.com/training/data-storage/room)
*   **Navegación**: [Jetpack Navigation for Compose](https://developer.android.com/jetpack/compose/navigation)
*   **Carga de Imágenes**: [Coil](https://coil-kt.github.io/coil/)
*   **Inyección de Dependencias (manual)**: Usando `ViewModelFactory` para proveer dependencias a los ViewModels.

*   **Perfiles de Usuario**: Una sección donde los usuarios puedan ver los spots que han creado o guardado como favoritos.
*   **Edición y Eliminación**: Permitir a los creadores de un spot que puedan editar su información o eliminarlo.
