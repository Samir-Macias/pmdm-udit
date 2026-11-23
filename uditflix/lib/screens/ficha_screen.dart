
//
// PANTALLA DE FICHA
//
// Muestra el detalle completo de un Contenido seleccionado desde el catálogo:
// - Cabecera visual con animación Hero y degradado.
// - Etiquetas de tipo, año, duración y valoración.
// - Botón interactivo de "Mi lista" con retroalimentación visual.
// - Botón principal para abrir el reproductor.
//

import 'package:flutter/material.dart';

import '../models/contenido.dart';
import 'reproductor_screen.dart';

class FichaScreen extends StatefulWidget {
  final Contenido contenido;

  const FichaScreen({
    super.key,
    required this.contenido,
  });

  @override
  State<FichaScreen> createState() => _FichaScreenState();
}

class _FichaScreenState extends State<FichaScreen> {
  bool _enMiLista = false;

  IconData _iconoPorTipo(String tipo) {
    if (tipo == 'Vídeo') return Icons.play_circle_fill;
    if (tipo == 'Cómic') return Icons.menu_book_rounded;
    if (tipo == 'Juego') return Icons.sports_esports_rounded;
    return Icons.movie_rounded;
  }

  Widget _buildHeaderImage() {
    final Widget fallback = Container(
      decoration: BoxDecoration(
        gradient: LinearGradient(
          colors: [
            const Color(0xFF15151C),
            const Color(0xFFFF3B30).withValues(alpha: 0.35),
          ],
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
        ),
      ),
      child: Center(
        child: Icon(
          _iconoPorTipo(widget.contenido.tipo),
          size: 86,
          color: Colors.white.withValues(alpha: 0.9),
        ),
      ),
    );

    if (widget.contenido.imagenUrl == null) return fallback;

    return Image.network(
      widget.contenido.imagenUrl!,
      fit: BoxFit.cover,
      errorBuilder: (_, _, _) => fallback,
      loadingBuilder: (context, child, loadingProgress) {
        if (loadingProgress == null) return child;
        return fallback;
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    final Contenido contenido = widget.contenido;

    return Scaffold(
      body: CustomScrollView(
        slivers: [
          SliverAppBar(
            expandedHeight: 240,
            pinned: true,
            flexibleSpace: FlexibleSpaceBar(
              title: Text(
                contenido.titulo,
                style: const TextStyle(
                  fontSize: 16,
                  fontWeight: FontWeight.bold,
                  shadows: [Shadow(blurRadius: 8, color: Colors.black)],
                ),
              ),
              background: Stack(
                fit: StackFit.expand,
                children: [
                  Hero(
                    tag: 'portada-${contenido.id}',
                    child: _buildHeaderImage(),
                  ),
                  Container(
                    decoration: BoxDecoration(
                      gradient: LinearGradient(
                        begin: Alignment.topCenter,
                        end: Alignment.bottomCenter,
                        colors: [
                          Colors.black.withValues(alpha: 0.25),
                          const Color(0xFF070709),
                        ],
                      ),
                    ),
                  ),
                ],
              ),
            ),
          ),
          SliverPadding(
            padding: const EdgeInsets.all(20),
            sliver: SliverList(
              delegate: SliverChildListDelegate([
                Wrap(
                  spacing: 8,
                  runSpacing: 8,
                  children: [
                    Chip(
                      avatar: Icon(
                        _iconoPorTipo(contenido.tipo),
                        size: 16,
                        color: Colors.cyanAccent,
                      ),
                      label: Text(contenido.tipo),
                      backgroundColor: const Color(0xFF18162B),
                    ),
                    Chip(
                      avatar: const Icon(
                        Icons.schedule,
                        size: 16,
                        color: Colors.white70,
                      ),
                      label: Text(contenido.duracion),
                      backgroundColor: const Color(0xFF18162B),
                    ),
                    Chip(
                      avatar: const Icon(
                        Icons.star_rounded,
                        size: 16,
                        color: Colors.amber,
                      ),
                      label: Text('${contenido.valoracion} · ${contenido.anio}'),
                      backgroundColor: const Color(0xFF18162B),
                    ),
                  ],
                ),
                const SizedBox(height: 20),
                const Text(
                  'Sinopsis',
                  style: TextStyle(
                    fontSize: 18,
                    fontWeight: FontWeight.bold,
                    color: Colors.white,
                  ),
                ),
                const SizedBox(height: 8),
                Text(
                  contenido.descripcion,
                  style: const TextStyle(
                    fontSize: 16,
                    height: 1.5,
                    color: Colors.white70,
                  ),
                ),
                const SizedBox(height: 28),
                Row(
                  children: [
                    Expanded(
                      child: ElevatedButton.icon(
                        icon: const Icon(Icons.play_arrow_rounded),
                        label: const Text('Ver contenido'),
                        onPressed: () {
                          Navigator.push(
                            context,
                            MaterialPageRoute(
                              builder: (context) => ReproductorScreen(
                                contenido: contenido,
                              ),
                            ),
                          );
                        },
                      ),
                    ),
                    const SizedBox(width: 12),
                    OutlinedButton.icon(
                      onPressed: () {
                        setState(() {
                          _enMiLista = !_enMiLista;
                        });
                        ScaffoldMessenger.of(context)
                          ..hideCurrentSnackBar()
                          ..showSnackBar(
                            SnackBar(
                              content: Text(
                                _enMiLista
                                    ? 'Añadido a Mi lista'
                                    : 'Eliminado de Mi lista',
                              ),
                              duration: const Duration(seconds: 2),
                            ),
                          );
                      },
                      icon: Icon(
                        _enMiLista ? Icons.check : Icons.add,
                        color: _enMiLista ? Colors.cyanAccent : Colors.white,
                      ),
                      label: Text(
                        _enMiLista ? 'En mi lista' : 'Mi lista',
                        style: TextStyle(
                          color: _enMiLista ? Colors.cyanAccent : Colors.white,
                        ),
                      ),
                      style: OutlinedButton.styleFrom(
                        padding: const EdgeInsets.symmetric(
                          horizontal: 16,
                          vertical: 14,
                        ),
                        side: BorderSide(
                          color: _enMiLista
                              ? Colors.cyanAccent
                              : Colors.white38,
                        ),
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(12),
                        ),
                      ),
                    ),
                  ],
                ),
              ]),
            ),
          ),
        ],
      ),
    );
  }
}
