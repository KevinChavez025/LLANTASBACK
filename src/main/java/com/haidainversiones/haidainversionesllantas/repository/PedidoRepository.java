package com.haidainversiones.haidainversionesllantas.repository;

import com.haidainversiones.haidainversionesllantas.entity.Pedido;
import com.haidainversiones.haidainversionesllantas.entity.Usuario;
import com.haidainversiones.haidainversionesllantas.enums.EstadoPago;
import com.haidainversiones.haidainversionesllantas.enums.EstadoPedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // ✅ EntityGraph en los métodos que usa mapearAPedidoResponse
    @EntityGraph(attributePaths = {"usuario", "detalles", "detalles.producto"})
    Optional<Pedido> findById(Long id);

    @EntityGraph(attributePaths = {"usuario", "detalles", "detalles.producto"})
    Page<Pedido> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"usuario", "detalles", "detalles.producto"})
    List<Pedido> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);

    // Sin EntityGraph — solo se usan internamente sin serializar
    Optional<Pedido> findByNumeroPedido(String numeroPedido);

    List<Pedido> findByUsuario(Usuario usuario);

    List<Pedido> findByUsuarioOrderByFechaCreacionDesc(Usuario usuario);

    List<Pedido> findByUsuarioId(Long usuarioId);

    List<Pedido> findByEstado(EstadoPedido estado);

    List<Pedido> findByEstadoPago(EstadoPago estadoPago);

    List<Pedido> findByEmailInvitado(String emailInvitado);
}
