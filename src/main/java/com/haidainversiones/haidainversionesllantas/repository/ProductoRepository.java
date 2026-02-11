package com.haidainversiones.haidainversionesllantas.repository;

import com.haidainversiones.haidainversionesllantas.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByDisponibleTrue();

    List<Producto> findByEsDestacadoTrue();

    List<Producto> findByEsNuevoTrue();

    List<Producto> findByMarca(String marca);

    List<Producto> findByTipoVehiculo(String tipoVehiculo);

    List<Producto> findByMedida(String medida);
}
