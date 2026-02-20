package com.haidainversiones.haidainversionesllantas.repository;

import com.haidainversiones.haidainversionesllantas.entity.Articulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticuloRepository extends JpaRepository<Articulo, Long> {

    List<Articulo> findByPublicadoTrueOrderByFechaPublicacionDesc();

    List<Articulo> findByCategoria(String categoria);

    List<Articulo> findByAutorId(Long autorId);
}
