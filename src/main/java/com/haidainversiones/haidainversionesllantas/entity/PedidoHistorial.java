package com.haidainversiones.haidainversionesllantas.entity;

import com.haidainversiones.haidainversionesllantas.enums.EstadoPedido;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Registra cada cambio de estado de un pedido.
 * Permite auditar quién cambió el estado y cuándo.
 */
@Entity
@Table(name = "pedido_historial")
@Data
public class PedidoHistorial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @Column(name = "estado_anterior", columnDefinition = "estado_pedido_enum")
    private EstadoPedido estadoAnterior;

    @Column(name = "estado_nuevo", nullable = false, columnDefinition = "estado_pedido_enum")
    private EstadoPedido estadoNuevo;

    /** Email del admin o sistema que realizó el cambio */
    @Column(name = "cambiado_por", length = 100)
    private String cambiadoPor;

    @Column(columnDefinition = "TEXT")
    private String observacion;

    @CreationTimestamp
    @Column(name = "fecha_cambio", updatable = false)
    private LocalDateTime fechaCambio;
}
