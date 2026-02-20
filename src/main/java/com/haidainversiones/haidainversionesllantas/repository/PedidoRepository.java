package com.haidainversiones.haidainversionesllantas.repository;

import com.haidainversiones.haidainversionesllantas.entity.Pedido;
import com.haidainversiones.haidainversionesllantas.entity.Usuario;
import com.haidainversiones.haidainversionesllantas.enums.EstadoPago;
import com.haidainversiones.haidainversionesllantas.enums.EstadoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    Optional<Pedido> findByNumeroPedido(String numeroPedido);

    List<Pedido> findByUsuario(Usuario usuario);

    List<Pedido> findByUsuarioOrderByFechaCreacionDesc(Usuario usuario);

    // FIX: Añadido método por ID con orden (usado en PedidoService)
    List<Pedido> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);

    List<Pedido> findByUsuarioId(Long usuarioId);

    // FIX: Enums en lugar de Strings libres
    List<Pedido> findByEstado(EstadoPedido estado);

    List<Pedido> findByEstadoPago(EstadoPago estadoPago);

    List<Pedido> findByEmailInvitado(String emailInvitado);
}
