package com.example.springboot.controllers;

import com.example.springboot.dtos.ProductForm;
import com.example.springboot.dtos.ProductRecordDto;
import com.example.springboot.models.ProductModel;
import com.example.springboot.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class ProductController {

    @Autowired
    ProductService productService;
                                                                      //@valid utilieza do record para validar, é necessário utilizar o mesmo
    @PostMapping("/product")                                       // POST http://localhost:8080/product
    public ResponseEntity<ProductModel> saveProduct(@RequestBody @Valid ProductRecordDto productRecordDto){
        var productModel = new ProductModel();                       //instancia de productModel
        BeanUtils.copyProperties(productRecordDto, productModel);    //Copiando atrivutos do dto
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.save(productModel));
    }

    @GetMapping("/product")
    public ResponseEntity<List<ProductModel>> getAllProducts(){

        Optional<ArrayList<ProductModel>> productO = Optional.ofNullable(productService.findAll());

        if(!productO.isEmpty()){
           for(ProductModel productModel : productO.get() ){
               //produtoModel.add é um metodo da classe RepresentationModel que adiciona um link ao objeto para Class Controller e metodo getOneProduct
               productModel.add(linkTo(methodOn(ProductController.class).getOneProduct(productModel.getIdProduct())).withSelfRel());
               //o método linkTo(), o qual irá construir uma URI de acordo com o controller e o método definido, methodOn() faz o mapeamento
               //do método de destino da chamada e withSelfRel() e withRel() criam um autOlink de acordo com a relação.
           }
        }
        return ResponseEntity.status(HttpStatus.OK).body(productO.get());
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<Object> getOneProduct(@PathVariable(value = "id")UUID id){
        Optional<ProductModel> productO = Optional.ofNullable(productService.findById(id));
        if(productO.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto not found.");
        }
        productO.get().add(linkTo(methodOn(ProductController.class).getAllProducts()).withRel("Lista de Produtos"));
        return ResponseEntity.status(HttpStatus.OK).body(productO.get());
    }

    @PutMapping("/product/{id}")
    public ResponseEntity<Object> updateProduct(@PathVariable(value="id") UUID id, @RequestBody @Valid ProductRecordDto productRecordDto){
        Optional<ProductModel> productO = Optional.ofNullable(productService.findById(id));
        if(productO.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not Found.");
        }
        var productModel = productO.get();
        BeanUtils.copyProperties(productRecordDto, productModel );
        return  ResponseEntity.status(HttpStatus.OK).body(productService.save(productModel));
    }

    @DeleteMapping("/product/{id}")
    public ResponseEntity<Object> deleteProduct(@PathVariable(value="id") UUID id){
        Optional<ProductModel> productO = Optional.ofNullable(productService.findById(id));
        if(productO.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto not found.");
        }
        productService.delete(productO.get());
        return ResponseEntity.status(HttpStatus.OK).body("Product deleted successfully " + productO.get().toString());
    }

    @GetMapping("/product-filter")
    public ResponseEntity<Object> productFilter(@RequestParam(required = false) String nome,
                                                @RequestParam(required = false) BigDecimal value){
        Optional<ArrayList<ProductModel>> productO = Optional.ofNullable(productService.productFilter(nome, value) );
        if(productO.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto not found.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(productO);
    }

    @GetMapping("/product-filter-page") //filtro com paginação http://localhost:8080/product-filter-page?nome=comida&page=0&size=2&sort=name,asc,
    public ResponseEntity<Object> productFilterPage(@RequestParam(required = false) String nome,
                                                @RequestParam(required = false) BigDecimal value,
                                                Pageable pageable){
        Optional<Page<ProductModel>> productO = Optional.ofNullable(productService.productFilterPage(nome, value, pageable) );
        if(productO.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto not found.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(productO);
    }
    @GetMapping("/product-pageSimple") //pagias simples padrões tamanho 3
    public ResponseEntity<Object> productPageSimple(){
        Page<ProductModel> page = productService.findAll(PageRequest.of(0,3));
        return ResponseEntity.status(HttpStatus.OK).body(page);
    }

    @GetMapping("/product-pageParameter") //paginas dinamicas especificadas pela URI http://localhost:8080/product-pageParameter?page=0&size=5&sort=name,asc
    public ResponseEntity<Object> productPageParameter(Pageable pageable){
        Page<ProductModel> page = productService.findAll(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(page);
    }

    @GetMapping("/product-pageDefault") //definir padrão caso n seja passado nada
    public ResponseEntity<Object> productPageDefault(@PageableDefault(direction = Sort.Direction.ASC, size = 2, sort = "name") Pageable pageable){
        Page<ProductModel> page = productService.findAll(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(page);
    }

    @PostMapping("/product-filter-generic") //definir padrão caso n seja passado nada
    public ResponseEntity<Object> productFilterGeneric(@RequestBody ProductForm filter, Pageable pageable){
        Optional<Page<ProductModel>> productO = Optional.ofNullable(productService.findAllFilterGeneric(filter , pageable));
        if(productO.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto not found.");
        }
        for(ProductModel productModel : productO.get() ){
            productModel.add(linkTo(methodOn(ProductController.class).getOneProduct(productModel.getIdProduct())).withSelfRel());
        }

        return ResponseEntity.status(HttpStatus.OK).body(productO.get());
    }

}


