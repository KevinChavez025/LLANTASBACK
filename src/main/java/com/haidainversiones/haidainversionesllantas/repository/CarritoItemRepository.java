package com.haidainversiones.haidainversionesllantas.repository;

import com.haidainversiones.haidainversionesllantas.entity.CarritoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarritoItemRepository extends JpaRepository<CarritoItem, Long> {

    // Para usuarios invitados (por sessionId)
    List<CarritoItem> findBySessionId(String sessionId);

    // Para usuarios registrados
    List<CarritoItem> findByUsuarioId(Long usuarioId);

    // Buscar item específico en carrito de invitado
    Optional<CarritoItem> findBySessionIdAndProductoId(String sessionId, Long productoId);

    // Buscar item específico en carrito de usuario registrado
    Optional<CarritoItem> findByUsuarioIdAndProductoId(Long usuarioId, Long productoId);

    // Eliminar todos los items de un carrito (invitado)
    void deleteBySessionId(String sessionId);

    // Eliminar todos los items de un carrito (usuario)
    void deleteByUsuarioId(Long usuarioId);
}
