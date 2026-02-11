package com.haidainversiones.haidainversionesllantas.repository;

import com.haidainversiones.haidainversionesllantas.entity.Pago;
import com.haidainversiones.haidainversionesllantas.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    Optional<Pago> findByPedido(Pedido pedido);

    Optional<Pago> findByTransactionId(String transactionId);

    Optional<Pago> findByPedidoId(Long pedidoId);
}
