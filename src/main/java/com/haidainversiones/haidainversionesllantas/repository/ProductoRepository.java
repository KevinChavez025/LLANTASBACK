package com.haidainversiones.haidainversionesllantas.repository;

import com.haidainversiones.haidainversionesllantas.entity.Producto;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // ===== LOCK PESIMISTA =====
    // Bloquea la fila del producto mientras dura la transacción.
    // Si dos pedidos intentan comprar el mismo producto al mismo tiempo,
    // el segundo espera a que el primero termine → nunca hay doble descuento.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Producto p WHERE p.id = :id")
    Optional<Producto> findByIdForUpdate(@Param("id") Long id);

    // ========== MÉTODOS BÁSICOS ==========
    List<Producto> findByDisponibleTrue();
    List<Producto> findByEsDestacadoTrue();
    List<Producto> findByEsNuevoTrue();

    // ========== BÚSQUEDAS EXACTAS ==========
    List<Producto> findByMarca(String marca);
    List<Producto> findByModelo(String modelo);
    List<Producto> findByTipoVehiculo(String tipoVehiculo);
    List<Producto> findByMedida(String medida);
    List<Producto> findByCategoria(String categoria);

    // ========== BÚSQUEDAS FLEXIBLES ==========
    List<Producto> findByMarcaContainingIgnoreCase(String marca);
    List<Producto> findByModeloContainingIgnoreCase(String modelo);
    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    // ========== BÚSQUEDA POR TEXTO ==========
    @Query("SELECT p FROM Producto p WHERE " +
            "LOWER(p.nombre) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.marca) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.modelo) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Producto> buscarPorTexto(@Param("query") String query);

    // ========== FILTROS COMBINADOS ==========
    List<Producto> findByMarcaAndCategoria(String marca, String categoria);
    List<Producto> findByDisponibleTrueAndEsDestacadoTrue();

    // ========== ORDENAMIENTO ==========
    List<Producto> findTop10ByOrderByFechaCreacionDesc();

    // ========== FILTROS POR RANGO ==========
    List<Producto> findByStockGreaterThan(Integer stock);

    @Query("SELECT p FROM Producto p WHERE p.precio BETWEEN :minPrecio AND :maxPrecio")
    List<Producto> findByPrecioRange(@Param("minPrecio") BigDecimal minPrecio,
                                     @Param("maxPrecio") BigDecimal maxPrecio);

    // ========== PROTECCIÓN DE INTEGRIDAD ==========
    @Query("SELECT COUNT(d) > 0 FROM DetallePedido d WHERE d.producto.id = :productoId")
    boolean tieneDetallesPedido(@Param("productoId") Long productoId);
}
