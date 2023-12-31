package com.example.springboot.models;

import jakarta.persistence.*;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "TB_PRODUCT")
public class ProductModel extends RepresentationModel<ProductModel> implements Serializable {
    //Represententação é uma classe que contém os dados que serão enviados para o cliente da biblioteca heteoas para hyperlinks.
    private static final long serialVersionUID = 1L; // Used to versioning apos serialização

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID idProduct;

    private String name;

    private BigDecimal value;

    public ProductModel(UUID idProduct, String name, BigDecimal value) {
        this.idProduct = idProduct;
        this.name = name;
        this.value = value;
    }

    public ProductModel() {}

    public UUID getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(UUID idProduct) {
        this.idProduct = idProduct;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ProductModel{" +
                "idProduct=" + idProduct +
                ", name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}
