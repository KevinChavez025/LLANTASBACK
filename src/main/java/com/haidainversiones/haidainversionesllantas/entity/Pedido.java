package com.haidainversiones.haidainversionesllantas.entity;

import jakarta.persistence.*;
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

    @Column(name = "numero_pedido", unique = true, nullable = false)
    private String numeroPedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePedido> detalles = new ArrayList<>();

    @Column(nullable = false)
    private String estado;

    @Column(name = "subtotal", precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "impuesto", precision = 10, scale = 2)
    private BigDecimal impuesto;

    @Column(name = "costo_envio", precision = 10, scale = 2)
    private BigDecimal costoEnvio;

    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(name = "direccion_envio", nullable = false)
    private String direccionEnvio;

    @Column(name = "ciudad_envio")
    private String ciudadEnvio;

    @Column(name = "distrito_envio")
    private String distritoEnvio;

    @Column(name = "codigo_postal_envio")
    private String codigoPostalEnvio;

    @Column(name = "telefono_contacto")
    private String telefonoContacto;

    @Column(columnDefinition = "TEXT")
    private String notas;

    @Column(name = "metodo_pago")
    private String metodoPago;

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
    @Column(name = "email_invitado")
    private String emailInvitado;

    @Column(name = "nombre_invitado")
    private String nombreInvitado;

    @Column(name = "telefono_invitado")
    private String telefonoInvitado;

    @Column(name = "ciudad")
    private String ciudad;

    @Column(name = "departamento")
    private String departamento;

    @Column(name = "codigo_postal")
    private String codigoPostal;

    @Column(name = "notas_adicionales", columnDefinition = "TEXT")
    private String notasAdicionales;

    @Column(name = "fecha_pedido")
    private LocalDateTime fechaPedido;

    @Column(name = "igv", precision = 10, scale = 2)
    private BigDecimal igv;

}
