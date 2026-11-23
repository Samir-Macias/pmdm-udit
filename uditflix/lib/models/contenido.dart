
//
// MODELO CONTENIDO
//
// Esta clase representa un contenido de nuestra plataforma UDITflix.
// Por ejemplo: una película, un cómic o un videojuego.
//
// En programación orientada a objetos, una clase define
// qué características tendrán los objetos que creemos.
//

class Contenido {

  // Identificador único de cada contenido.
  final int id;

  // Título que aparecerá en el catálogo.
  final String titulo;

  // Descripción del contenido.
  final String descripcion;

  // Tipo de contenido: vídeo, cómic o juego.
  final String tipo;

  // Duración aproximada o tiempo estimado.
  final String duracion;

  // URL opcional del vídeo asociado al contenido.
  final String? videoUrl;

  // Imagen de portada opcional para enriquecer el catálogo.
  final String? imagenUrl;

  // Valoración y año para mostrar en las fichas.
  final String valoracion;
  final int anio;
  final bool destacado;

  // Constructor: permite crear nuevos objetos Contenido.
  const Contenido({
    required this.id,
    required this.titulo,
    required this.descripcion,
    required this.tipo,
    required this.duracion,
    this.videoUrl,
    this.imagenUrl,
    this.valoracion = '4.8',
    this.anio = 2026,
    this.destacado = false,
  });
}
