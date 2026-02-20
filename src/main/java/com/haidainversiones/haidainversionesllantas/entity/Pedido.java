package com.haidainversiones.haidainversionesllantas.entity;

import com.haidainversiones.haidainversionesllantas.enums.EstadoPago;
import com.haidainversiones.haidainversionesllantas.enums.EstadoPedido;
import com.haidainversiones.haidainversionesllantas.enums.MetodoPago;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Data
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El número de pedido es obligatorio")
    @Column(name = "numero_pedido", unique = true, nullable = false, length = 50)
    private String numeroPedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DetallePedido> detalles = new ArrayList<>();

    // SIN @Enumerated — el converter EstadoPedidoConverter se aplica automáticamente
    // columnDefinition le dice a Hibernate el tipo nativo de la columna en PostgreSQL
    @Column(nullable = false, columnDefinition = "estado_pedido_enum")
    private EstadoPedido estado;

    @Column(name = "metodo_pago", columnDefinition = "metodo_pago_enum")
    private MetodoPago metodoPago;

    @Column(name = "estado_pago", columnDefinition = "estado_pago_enum")
    private EstadoPago estadoPago;

    // ===== MONTOS =====
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "subtotal", precision = 10, scale = 2)
    private BigDecimal subtotal;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "igv", precision = 10, scale = 2)
    private BigDecimal igv;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "costo_envio", precision = 10, scale = 2)
    private BigDecimal costoEnvio;

    @NotNull(message = "El total es obligatorio")
    @DecimalMin(value = "0.01")
    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    // ===== DIRECCIÓN DE ENVÍO =====
    @NotBlank(message = "La dirección de envío es obligatoria")
    @Size(max = 200)
    @Column(name = "direccion_envio", nullable = false)
    private String direccionEnvio;

    @Size(max = 100)
    @Column(name = "ciudad_envio")
    private String ciudadEnvio;

    @Size(max = 100)
    @Column(name = "distrito_envio")
    private String distritoEnvio;

    @Size(max = 100)
    @Column(name = "departamento_envio")
    private String departamentoEnvio;

    @Size(max = 10)
    @Column(name = "codigo_postal_envio")
    private String codigoPostalEnvio;

    @Size(max = 20)
    @Column(name = "telefono_contacto")
    private String telefonoContacto;

    // ===== DATOS INVITADO =====
    @Email
    @Size(max = 100)
    @Column(name = "email_invitado")
    private String emailInvitado;

    @Size(max = 100)
    @Column(name = "nombre_invitado")
    private String nombreInvitado;

    @Size(max = 20)
    @Column(name = "telefono_invitado")
    private String telefonoInvitado;

    // ===== NOTAS Y FECHAS =====
    @Size(max = 500)
    @Column(name = "notas", columnDefinition = "TEXT")
    private String notas;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    @Column(name = "fecha_entrega_estimada")
    private LocalDateTime fechaEntregaEstimada;

    @Column(name = "fecha_entrega_real")
    private LocalDateTime fechaEntregaReal;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
}
