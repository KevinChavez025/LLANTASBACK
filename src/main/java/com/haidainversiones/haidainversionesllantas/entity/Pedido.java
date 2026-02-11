package com.haidainversiones.haidainversionesllantas.entity;

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
    @Size(max = 50, message = "El número de pedido no puede exceder 50 caracteres")
    @Column(name = "numero_pedido", unique = true, nullable = false)
    private String numeroPedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePedido> detalles = new ArrayList<>();

    @NotBlank(message = "El estado del pedido es obligatorio")
    @Size(max = 50, message = "El estado no puede exceder 50 caracteres")
    @Column(nullable = false)
    private String estado;

    @DecimalMin(value = "0.0", inclusive = true, message = "El subtotal no puede ser negativo")
    @Digits(integer = 10, fraction = 2, message = "El subtotal debe tener máximo 10 enteros y 2 decimales")
    @Column(name = "subtotal", precision = 10, scale = 2)
    private BigDecimal subtotal;

    @DecimalMin(value = "0.0", inclusive = true, message = "El impuesto no puede ser negativo")
    @Digits(integer = 10, fraction = 2, message = "El impuesto debe tener máximo 10 enteros y 2 decimales")
    @Column(name = "impuesto", precision = 10, scale = 2)
    private BigDecimal impuesto;

    @DecimalMin(value = "0.0", inclusive = true, message = "El costo de envío no puede ser negativo")
    @Digits(integer = 10, fraction = 2, message = "El costo de envío debe tener máximo 10 enteros y 2 decimales")
    @Column(name = "costo_envio", precision = 10, scale = 2)
    private BigDecimal costoEnvio;

    @NotNull(message = "El total es obligatorio")
    @DecimalMin(value = "0.01", message = "El total debe ser mayor a 0")
    @Digits(integer = 10, fraction = 2, message = "El total debe tener máximo 10 enteros y 2 decimales")
    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @NotBlank(message = "La dirección de envío es obligatoria")
    @Size(max = 200, message = "La dirección de envío no puede exceder 200 caracteres")
    @Column(name = "direccion_envio", nullable = false)
    private String direccionEnvio;

    @Size(max = 100, message = "La ciudad de envío no puede exceder 100 caracteres")
    @Column(name = "ciudad_envio")
    private String ciudadEnvio;

    @Size(max = 100, message = "El distrito de envío no puede exceder 100 caracteres")
    @Column(name = "distrito_envio")
    private String distritoEnvio;

    @Size(max = 10, message = "El código postal de envío no puede exceder 10 caracteres")
    @Column(name = "codigo_postal_envio")
    private String codigoPostalEnvio;

    @Size(max = 20, message = "El teléfono de contacto no puede exceder 20 caracteres")
    @Column(name = "telefono_contacto")
    private String telefonoContacto;

    @Size(max = 500, message = "Las notas no pueden exceder 500 caracteres")
    @Column(columnDefinition = "TEXT")
    private String notas;

    @Size(max = 50, message = "El método de pago no puede exceder 50 caracteres")
    @Column(name = "metodo_pago")
    private String metodoPago;

    @Size(max = 50, message = "El estado de pago no puede exceder 50 caracteres")
    @Column(name = "estado_pago")
    private String estadoPago;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "fecha_entrega_estimada")
    private LocalDateTime fechaEntregaEstimada;

    @Column(name = "fecha_entrega_real")
    private LocalDateTime fechaEntregaReal;

    // Datos para compras sin cuenta
    @Email(message = "El email del invitado debe ser válido")
    @Size(max = 100, message = "El email del invitado no puede exceder 100 caracteres")
    @Column(name = "email_invitado")
    private String emailInvitado;

    @Size(max = 100, message = "El nombre del invitado no puede exceder 100 caracteres")
    @Column(name = "nombre_invitado")
    private String nombreInvitado;

    @Size(max = 20, message = "El teléfono del invitado no puede exceder 20 caracteres")
    @Column(name = "telefono_invitado")
    private String telefonoInvitado;

    @Size(max = 100, message = "La ciudad no puede exceder 100 caracteres")
    @Column(name = "ciudad")
    private String ciudad;

    @Size(max = 100, message = "El departamento no puede exceder 100 caracteres")
    @Column(name = "departamento")
    private String departamento;

    @Size(max = 10, message = "El código postal no puede exceder 10 caracteres")
    @Column(name = "codigo_postal")
    private String codigoPostal;

    @Size(max = 500, message = "Las notas adicionales no pueden exceder 500 caracteres")
    @Column(name = "notas_adicionales", columnDefinition = "TEXT")
    private String notasAdicionales;

    @Column(name = "fecha_pedido")
    private LocalDateTime fechaPedido;

    @DecimalMin(value = "0.0", inclusive = true, message = "El IGV no puede ser negativo")
    @Digits(integer = 10, fraction = 2, message = "El IGV debe tener máximo 10 enteros y 2 decimales")
    @Column(name = "igv", precision = 10, scale = 2)
    private BigDecimal igv;
}
