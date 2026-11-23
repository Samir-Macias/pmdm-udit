
//
// PANTALLA DEL CATÁLOGO
//
// Muestra el catálogo interactivo de UDITflix:
// - Barra de búsqueda en tiempo real.
// - Contenido destacado (Hero Banner) con acceso directo a ficha o reproducción.
// - Tarjeta destacada de UDIT Arcade.
// - Filtros por categoría (Todos, Vídeo, Cómic, Juego).
// - Tarjetas visuales con portada, valoración, duración y animación Hero.
//

import 'package:flutter/material.dart';

import '../data/contenidos.dart';
import '../models/contenido.dart';
import 'ficha_screen.dart';
import 'reproductor_screen.dart';
import 'udit_arcade_screen.dart';

class CatalogoScreen extends StatefulWidget {
  const CatalogoScreen({super.key});

  @override
  State<CatalogoScreen> createState() => _CatalogoScreenState();
}

class _CatalogoScreenState extends State<CatalogoScreen> {
  static const List<String> _categorias = ['Todos', 'Vídeo', 'Cómic', 'Juego'];

  String _categoriaSeleccionada = 'Todos';
  String _busqueda = '';
  bool _mostrarBuscador = false;
  final TextEditingController _searchController = TextEditingController();

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  IconData _obtenerIcono(String tipo) {
    if (tipo == 'Vídeo') return Icons.play_arrow_rounded;
    if (tipo == 'Cómic') return Icons.auto_stories_rounded;
    if (tipo == 'Juego') return Icons.sports_esports_rounded;
    return Icons.movie_rounded;
  }

  Color _obtenerColorTipo(String tipo) {
    if (tipo == 'Vídeo') return const Color(0xFFFF3B30);
    if (tipo == 'Cómic') return const Color(0xFFD4FF00);
    if (tipo == 'Juego') return const Color(0xFF00F0FF);
    return const Color(0xFFFF3B30);
  }

  List<Contenido> get _contenidosFiltrados {
    return contenidos.where((c) {
      final bool coincideCategoria =
          _categoriaSeleccionada == 'Todos' || c.tipo == _categoriaSeleccionada;
      final String query = _busqueda.trim().toLowerCase();
      final bool coincideTexto = query.isEmpty ||
          c.titulo.toLowerCase().contains(query) ||
          c.descripcion.toLowerCase().contains(query);
      return coincideCategoria && coincideTexto;
    }).toList();
  }

  Contenido get _destacado {
    return contenidos.firstWhere(
      (c) => c.destacado,
      orElse: () => contenidos.first,
    );
  }

  void _abrirArcade() {
    Navigator.push(
      context,
      MaterialPageRoute(builder: (context) => const UditArcadeScreen()),
    );
  }

  void _abrirFicha(Contenido contenido) {
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => FichaScreen(contenido: contenido),
      ),
    );
  }

  Widget _buildPortada(Contenido contenido, {double height = 110, double? width}) {
    final Color accent = _obtenerColorTipo(contenido.tipo);
    final Widget fallback = Container(
      width: width,
      height: height,
      decoration: BoxDecoration(
        gradient: LinearGradient(
          colors: [
            const Color(0xFF15151C),
            accent.withValues(alpha: 0.28),
          ],
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
        ),
      ),
      child: Center(
        child: Icon(
          _obtenerIcono(contenido.tipo),
          size: height * 0.36,
          color: const Color(0xFFF4F2EE).withValues(alpha: 0.85),
        ),
      ),
    );

    if (contenido.imagenUrl == null) return fallback;

    return Image.network(
      contenido.imagenUrl!,
      width: width,
      height: height,
      fit: BoxFit.cover,
      errorBuilder: (_, _, _) => fallback,
      loadingBuilder: (context, child, loadingProgress) {
        if (loadingProgress == null) return child;
        return fallback;
      },
    );
  }

  Widget _buildHeroDestacado(Contenido destacado) {
    return Padding(
      padding: const EdgeInsets.fromLTRB(16, 10, 16, 10),
      child: Container(
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(18),
          border: Border.all(color: const Color(0x26FFFFFF)),
        ),
        child: ClipRRect(
          borderRadius: BorderRadius.circular(17),
          child: Stack(
            children: [
              SizedBox(
                height: 230,
                width: double.infinity,
                child: _buildPortada(destacado, height: 230),
              ),
              Positioned.fill(
                child: Container(
                  decoration: BoxDecoration(
                    gradient: LinearGradient(
                      begin: Alignment.topCenter,
                      end: Alignment.bottomCenter,
                      colors: [
                        const Color(0xFF070709).withValues(alpha: 0.2),
                        const Color(0xFF070709).withValues(alpha: 0.96),
                      ],
                    ),
                  ),
                ),
              ),
              Positioned(
                top: 14,
                left: 14,
                right: 14,
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Container(
                      padding: const EdgeInsets.symmetric(
                        horizontal: 10,
                        vertical: 4,
                      ),
                      decoration: BoxDecoration(
                        color: const Color(0xFFFF3B30),
                        borderRadius: BorderRadius.circular(4),
                      ),
                      child: const Text(
                        '01 · DESTACADO',
                        style: TextStyle(
                          fontSize: 10,
                          fontWeight: FontWeight.w800,
                          letterSpacing: 1.4,
                          color: Colors.white,
                        ),
                      ),
                    ),
                    Container(
                      padding: const EdgeInsets.symmetric(
                        horizontal: 8,
                        vertical: 4,
                      ),
                      decoration: BoxDecoration(
                        color: Colors.black54,
                        borderRadius: BorderRadius.circular(4),
                        border: Border.all(color: const Color(0x33FFFFFF)),
                      ),
                      child: Text(
                        '${destacado.anio} · ★ ${destacado.valoracion}',
                        style: const TextStyle(
                          fontSize: 11,
                          fontWeight: FontWeight.w600,
                          color: Color(0xFFF4F2EE),
                        ),
                      ),
                    ),
                  ],
                ),
              ),
              Positioned(
                left: 16,
                right: 16,
                bottom: 16,
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      destacado.titulo,
                      style: const TextStyle(
                        fontSize: 24,
                        fontWeight: FontWeight.w800,
                        letterSpacing: -0.6,
                        color: Color(0xFFF4F2EE),
                      ),
                    ),
                    const SizedBox(height: 6),
                    Text(
                      destacado.descripcion,
                      maxLines: 2,
                      overflow: TextOverflow.ellipsis,
                      style: const TextStyle(
                        fontSize: 13,
                        height: 1.35,
                        color: Color(0xFFB8B5C0),
                      ),
                    ),
                    const SizedBox(height: 14),
                    Row(
                      children: [
                        ElevatedButton.icon(
                          onPressed: () {
                            Navigator.push(
                              context,
                              MaterialPageRoute(
                                builder: (context) => ReproductorScreen(
                                  contenido: destacado,
                                ),
                              ),
                            );
                          },
                          icon: const Icon(Icons.play_arrow_rounded, size: 20),
                          label: const Text('Reproducir'),
                          style: ElevatedButton.styleFrom(
                            padding: const EdgeInsets.symmetric(
                              horizontal: 16,
                              vertical: 11,
                            ),
                          ),
                        ),
                        const SizedBox(width: 10),
                        OutlinedButton.icon(
                          onPressed: () => _abrirFicha(destacado),
                          icon: const Icon(Icons.arrow_outward_rounded, size: 17),
                          label: const Text('Ver ficha'),
                          style: OutlinedButton.styleFrom(
                            foregroundColor: const Color(0xFFF4F2EE),
                            backgroundColor: Colors.white.withValues(alpha: 0.06),
                            side: const BorderSide(color: Color(0x33FFFFFF)),
                            padding: const EdgeInsets.symmetric(
                              horizontal: 14,
                              vertical: 11,
                            ),
                            shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(10),
                            ),
                          ),
                        ),
                      ],
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildArcadeCard() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 6),
      child: InkWell(
        onTap: _abrirArcade,
        borderRadius: BorderRadius.circular(14),
        child: Container(
          padding: const EdgeInsets.all(16),
          decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(14),
            gradient: const LinearGradient(
              colors: [Color(0xFF14161F), Color(0xFF0C1C22)],
              begin: Alignment.topLeft,
              end: Alignment.bottomRight,
            ),
            border: Border.all(
              color: const Color(0xFFD4FF00).withValues(alpha: 0.45),
            ),
          ),
          child: Row(
            children: [
              Container(
                padding: const EdgeInsets.all(12),
                decoration: BoxDecoration(
                  color: const Color(0xFFD4FF00).withValues(alpha: 0.14),
                  borderRadius: BorderRadius.circular(10),
                  border: Border.all(
                    color: const Color(0xFFD4FF00).withValues(alpha: 0.4),
                  ),
                ),
                child: const Icon(
                  Icons.rocket_launch_rounded,
                  color: Color(0xFFD4FF00),
                  size: 28,
                ),
              ),
              const SizedBox(width: 14),
              const Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      'EXPERIENCIA INTERACTIVA // 02',
                      style: TextStyle(
                        fontSize: 10,
                        fontWeight: FontWeight.w700,
                        letterSpacing: 1.3,
                        color: Color(0xFF00F0FF),
                      ),
                    ),
                    SizedBox(height: 3),
                    Text(
                      'UDIT Arcade',
                      style: TextStyle(
                        fontSize: 18,
                        fontWeight: FontWeight.w800,
                        letterSpacing: -0.3,
                        color: Color(0xFFF4F2EE),
                      ),
                    ),
                    SizedBox(height: 2),
                    Text(
                      'Space Shooter 2D · Pulsa para entrar',
                      style: TextStyle(
                        fontSize: 12,
                        color: Color(0xFF9E9BA6),
                      ),
                    ),
                  ],
                ),
              ),
              Container(
                padding: const EdgeInsets.symmetric(
                  horizontal: 14,
                  vertical: 9,
                ),
                decoration: BoxDecoration(
                  color: const Color(0xFFD4FF00),
                  borderRadius: BorderRadius.circular(8),
                ),
                child: const Text(
                  'Jugar',
                  style: TextStyle(
                    color: Color(0xFF070709),
                    fontWeight: FontWeight.w800,
                    fontSize: 13,
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildFiltros() {
    return SizedBox(
      height: 50,
      child: ListView.separated(
        scrollDirection: Axis.horizontal,
        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
        itemCount: _categorias.length,
        separatorBuilder: (_, _) => const SizedBox(width: 8),
        itemBuilder: (context, index) {
          final String categoria = _categorias[index];
          final bool selected = _categoriaSeleccionada == categoria;
          return ChoiceChip(
            label: Text(categoria),
            selected: selected,
            showCheckmark: false,
            selectedColor: const Color(0xFFF4F2EE),
            backgroundColor: const Color(0xFF111116),
            labelStyle: TextStyle(
              color: selected ? const Color(0xFF070709) : const Color(0xFF9E9BA6),
              fontWeight: selected ? FontWeight.w800 : FontWeight.w500,
              fontSize: 13,
            ),
            side: BorderSide(
              color: selected ? const Color(0xFFF4F2EE) : const Color(0x1FFFFFFF),
            ),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(8),
            ),
            onSelected: (_) {
              setState(() {
                _categoriaSeleccionada = categoria;
              });
            },
          );
        },
      ),
    );
  }

  Widget _buildContenidoCard(Contenido contenido, int index) {
    final Color tipoColor = _obtenerColorTipo(contenido.tipo);
    final String numero = (index + 1).toString().padLeft(2, '0');

    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 6),
      clipBehavior: Clip.antiAlias,
      child: InkWell(
        onTap: () => _abrirFicha(contenido),
        child: Row(
          children: [
            Hero(
              tag: 'portada-${contenido.id}',
              child: SizedBox(
                width: 122,
                height: 108,
                child: _buildPortada(contenido, width: 122, height: 108),
              ),
            ),
            Expanded(
              child: Padding(
                padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 12),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                      children: [
                        Text(
                          numero,
                          style: const TextStyle(
                            fontSize: 11,
                            fontWeight: FontWeight.w700,
                            color: Color(0xFF6E6B78),
                            letterSpacing: 1.0,
                          ),
                        ),
                        const SizedBox(width: 8),
                        Container(
                          padding: const EdgeInsets.symmetric(
                            horizontal: 7,
                            vertical: 2,
                          ),
                          decoration: BoxDecoration(
                            color: tipoColor.withValues(alpha: 0.14),
                            borderRadius: BorderRadius.circular(4),
                            border: Border.all(
                              color: tipoColor.withValues(alpha: 0.45),
                            ),
                          ),
                          child: Text(
                            contenido.tipo.toUpperCase(),
                            style: TextStyle(
                              fontSize: 10,
                              fontWeight: FontWeight.w800,
                              letterSpacing: 0.8,
                              color: tipoColor,
                            ),
                          ),
                        ),
                        const Spacer(),
                        const Icon(
                          Icons.star_rounded,
                          size: 14,
                          color: Color(0xFFD4FF00),
                        ),
                        const SizedBox(width: 3),
                        Text(
                          contenido.valoracion,
                          style: const TextStyle(
                            fontSize: 12,
                            color: Color(0xFFF4F2EE),
                            fontWeight: FontWeight.w600,
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 8),
                    Text(
                      contenido.titulo,
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                      style: const TextStyle(
                        fontSize: 16,
                        fontWeight: FontWeight.w700,
                        letterSpacing: -0.2,
                        color: Color(0xFFF4F2EE),
                      ),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      '${contenido.anio} · ${contenido.duracion}',
                      style: const TextStyle(
                        fontSize: 12,
                        color: Color(0xFF9E9BA6),
                      ),
                    ),
                  ],
                ),
              ),
            ),
            const Padding(
              padding: EdgeInsets.only(right: 12),
              child: Icon(
                Icons.arrow_outward_rounded,
                size: 18,
                color: Color(0xFF9E9BA6),
              ),
            ),
          ],
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final List<Contenido> filtrados = _contenidosFiltrados;
    final List<Widget> cabecera = [
      if (!_mostrarBuscador && _busqueda.isEmpty) ...[
        _buildHeroDestacado(_destacado),
        _buildArcadeCard(),
      ],
      _buildFiltros(),
      Padding(
        padding: const EdgeInsets.fromLTRB(16, 10, 16, 4),
        child: Row(
          children: [
            Text(
              'CATÁLOGO (${filtrados.length})',
              style: const TextStyle(
                fontSize: 12,
                fontWeight: FontWeight.w800,
                letterSpacing: 1.4,
                color: Color(0xFF9E9BA6),
              ),
            ),
          ],
        ),
      ),
    ];

    return Scaffold(
      appBar: AppBar(
        title: _mostrarBuscador
            ? TextField(
                controller: _searchController,
                autofocus: true,
                style: const TextStyle(color: Color(0xFFF4F2EE)),
                decoration: const InputDecoration(
                  hintText: 'Buscar películas, cómics, juegos...',
                  hintStyle: TextStyle(color: Color(0xFF6E6B78)),
                  border: InputBorder.none,
                ),
                onChanged: (value) {
                  setState(() {
                    _busqueda = value;
                  });
                },
              )
            : Row(
                children: [
                  Container(
                    width: 10,
                    height: 10,
                    decoration: const BoxDecoration(
                      color: Color(0xFFFF3B30),
                      shape: BoxShape.circle,
                    ),
                  ),
                  const SizedBox(width: 10),
                  const Text(
                    'UDITflix',
                    style: TextStyle(
                      fontWeight: FontWeight.w900,
                      letterSpacing: -0.5,
                    ),
                  ),
                ],
              ),
        actions: [
          IconButton(
            tooltip: _mostrarBuscador ? 'Cerrar búsqueda' : 'Buscar',
            icon: Icon(_mostrarBuscador ? Icons.close : Icons.search_rounded),
            onPressed: () {
              setState(() {
                _mostrarBuscador = !_mostrarBuscador;
                if (!_mostrarBuscador) {
                  _busqueda = '';
                  _searchController.clear();
                }
              });
            },
          ),
          Padding(
            padding: const EdgeInsets.only(right: 12),
            child: OutlinedButton.icon(
              onPressed: _abrirArcade,
              icon: const Icon(
                Icons.sports_esports_rounded,
                size: 17,
                color: Color(0xFFD4FF00),
              ),
              label: const Text(
                'Arcade',
                style: TextStyle(
                  color: Color(0xFFD4FF00),
                  fontWeight: FontWeight.w700,
                  fontSize: 13,
                ),
              ),
              style: OutlinedButton.styleFrom(
                padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                side: BorderSide(
                  color: const Color(0xFFD4FF00).withValues(alpha: 0.45),
                ),
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(8),
                ),
              ),
            ),
          ),
        ],
      ),
      body: ListView.builder(
        padding: EdgeInsets.zero,
        itemCount:
            cabecera.length + (filtrados.isEmpty ? 1 : filtrados.length) + 1,
        itemBuilder: (context, index) {
          if (index < cabecera.length) return cabecera[index];

          final int indiceContenido = index - cabecera.length;
          if (filtrados.isEmpty && indiceContenido == 0) {
            return const Center(
              child: Padding(
                padding: EdgeInsets.all(24),
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Icon(Icons.search_off, size: 48, color: Colors.white38),
                    SizedBox(height: 12),
                    Text(
                      'No se encontraron contenidos con ese filtro.',
                      style: TextStyle(color: Colors.white60),
                    ),
                  ],
                ),
              ),
            );
          }
          if (indiceContenido < filtrados.length) {
            return _buildContenidoCard(
              filtrados[indiceContenido],
              indiceContenido,
            );
          }
          return const SizedBox(height: 28);
        },
      ),
    );
  }
}
