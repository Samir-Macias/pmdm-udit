
//
// DATOS SIMULADOS DE UDITFLIX
//
// En esta primera versión no tenemos una API ni una base de datos.
// Por eso utilizamos una lista de objetos creada manualmente.
//
// Más adelante sustituiremos estos datos por información
// que recibiremos desde el servidor mediante una API REST.
//

import '../models/contenido.dart';

// Lista de contenidos que aparecerán en nuestra aplicación.
// Cada elemento de la lista es un objeto de tipo Contenido.

const List<Contenido> contenidos = [

  Contenido(
    id: 1,
    titulo: 'Introducción a Flutter',
    descripcion: 'Aprende a crear aplicaciones multiplataforma utilizando Flutter y Dart.',
    tipo: 'Vídeo',
    duracion: '15 minutos',
    videoUrl: 'https://flutter.github.io/assets-for-api-docs/assets/videos/butterfly.mp4',
    imagenUrl: 'https://picsum.photos/seed/uditflutter/800/450',
    valoracion: '4.9',
    anio: 2026,
    destacado: true,
  ),

  Contenido(
    id: 2,
    titulo: 'Desarrollo con Java',
    descripcion: 'Introducción a la programación orientada a objetos con Java.',
    tipo: 'Vídeo',
    duracion: '25 minutos',
    videoUrl: 'https://flutter.github.io/assets-for-api-docs/assets/videos/bee.mp4',
    imagenUrl: 'https://picsum.photos/seed/uditjava/800/450',
    valoracion: '4.7',
    anio: 2025,
  ),

  Contenido(
    id: 3,
    titulo: 'El mundo de los cómics',
    descripcion: 'Una colección de historias y aventuras ilustradas.',
    tipo: 'Cómic',
    duracion: '10 páginas',
    imagenUrl: 'https://picsum.photos/seed/uditcomics/800/450',
    valoracion: '4.6',
    anio: 2025,
  ),

  Contenido(
    id: 4,
    titulo: 'Programación de videojuegos',
    descripcion: 'Descubre los fundamentos de la lógica de videojuegos.',
    tipo: 'Juego',
    duracion: '20 minutos',
    imagenUrl: 'https://picsum.photos/seed/uditgames/800/450',
    valoracion: '4.9',
    anio: 2026,
  ),

  Contenido(
    id: 5,
    titulo: 'Bases de datos MySQL',
    descripcion: 'Aprende los conceptos fundamentales de las bases de datos relacionales.',
    tipo: 'Vídeo',
    duracion: '30 minutos',
    videoUrl: 'https://flutter.github.io/assets-for-api-docs/assets/videos/butterfly.mp4',
    imagenUrl: 'https://picsum.photos/seed/uditmysql/800/450',
    valoracion: '4.5',
    anio: 2024,
  ),

  Contenido(
    id: 6,
    titulo: 'Aventuras digitales',
    descripcion: 'Una historia interactiva para explorar el mundo digital.',
    tipo: 'Cómic',
    duracion: '18 páginas',
    imagenUrl: 'https://picsum.photos/seed/uditdigital/800/450',
    valoracion: '4.8',
    anio: 2026,
  ),

];
