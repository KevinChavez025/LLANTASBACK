package com.haidainversiones.haidainversionesllantas.repository;

import com.haidainversiones.haidainversionesllantas.entity.Pedido;
import com.haidainversiones.haidainversionesllantas.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    Optional<Pedido> findByNumeroPedido(String numeroPedido);

    List<Pedido> findByUsuario(Usuario usuario);

    List<Pedido> findByUsuarioOrderByFechaCreacionDesc(Usuario usuario);

    List<Pedido> findByEstado(String estado);

    List<Pedido> findByEstadoPago(String estadoPago);

    List<Pedido> findByEmailInvitado(String emailInvitado);

    List<Pedido> findByUsuarioId(Long usuarioId);

}
