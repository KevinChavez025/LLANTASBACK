package com.haidainversiones.haidainversionesllantas.service;

import com.haidainversiones.haidainversionesllantas.entity.Producto;
import com.haidainversiones.haidainversionesllantas.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    public Optional<Producto> obtenerPorId(Long id) {
        return productoRepository.findById(id);
    }

    public Producto guardar(Producto producto) {
        return productoRepository.save(producto);
    }

    public List<Producto> obtenerDestacados() {
        return productoRepository.findByEsDestacadoTrue();
    }

    public List<Producto> obtenerNuevos() {
        return productoRepository.findByEsNuevoTrue();
    }

    public List<Producto> obtenerDisponibles() {
        return productoRepository.findByDisponibleTrue();
    }

    public List<Producto> obtenerPorMarca(String marca) {
        return productoRepository.findByMarca(marca);
    }

    public List<Producto> obtenerPorTipoVehiculo(String tipoVehiculo) {
        return productoRepository.findByTipoVehiculo(tipoVehiculo);
    }

    public List<Producto> obtenerPorMedida(String medida) {
        return productoRepository.findByMedida(medida);
    }

    public void eliminar(Long id) {
        productoRepository.deleteById(id);
    }

    public Producto actualizar(Long id, Producto producto) {
        producto.setId(id);
        return productoRepository.save(producto);
    }

    public Optional<Producto> getProductoById(Long id) {
        return productoRepository.findById(id);
    }

    public void deleteProducto(Long id) {
        productoRepository.deleteById(id);
    }
}


