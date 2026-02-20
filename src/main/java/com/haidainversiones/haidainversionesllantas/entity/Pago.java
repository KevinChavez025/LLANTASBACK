package com.haidainversiones.haidainversionesllantas.entity;

import com.haidainversiones.haidainversionesllantas.enums.EstadoPago;
import com.haidainversiones.haidainversionesllantas.enums.MetodoPago;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
@Data
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false, unique = true)
    private Pedido pedido;

    // SIN @Enumerated — converter se aplica automáticamente
    @Column(name = "metodo_pago", nullable = false, columnDefinition = "metodo_pago_enum")
    private MetodoPago metodoPago;

    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "moneda", length = 3)
    private String moneda = "PEN";

    @Column(nullable = false, columnDefinition = "estado_pago_enum")
    private EstadoPago estado;

    @Column(name = "pasarela_pago")
    private String pasarelaPago;

    @Column(name = "transaction_id", unique = true)
    private String transactionId;

    @Column(name = "codigo_autorizacion")
    private String codigoAutorizacion;

    @Column(name = "ultimos_digitos_tarjeta", length = 4)
    private String ultimosDigitosTarjeta;

    @Column(name = "tipo_tarjeta")
    private String tipoTarjeta;

    @Column(columnDefinition = "TEXT")
    private String notas;

    @Column(name = "mensaje_error", columnDefinition = "TEXT")
    private String mensajeError;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_procesado")
    private LocalDateTime fechaProcesado;
}
