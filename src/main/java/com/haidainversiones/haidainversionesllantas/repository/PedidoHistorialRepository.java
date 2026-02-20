package com.haidainversiones.haidainversionesllantas.repository;

import com.haidainversiones.haidainversionesllantas.entity.PedidoHistorial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoHistorialRepository extends JpaRepository<PedidoHistorial, Long> {

    List<PedidoHistorial> findByPedidoIdOrderByFechaCambioAsc(Long pedidoId);
}
