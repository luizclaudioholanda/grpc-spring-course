package br.com.grpcproject.service.impl;

import br.com.grpcproject.domain.Product;
import br.com.grpcproject.dto.ProductInputDTO;
import br.com.grpcproject.dto.ProductOutputDTO;
import br.com.grpcproject.exceptions.ProductAlreadyExistsException;
import br.com.grpcproject.exceptions.ProductNotFoundException;
import br.com.grpcproject.repository.ProductRepository;
import org.assertj.core.api.Assertions;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    @DisplayName("when create product service is called with valid data a product is returned.")
    public void createProductSuccessTest(){

        Product product = new Product(1L, "product name", 10.00, 10);

        when(productRepository.save(any())).thenReturn(product);

        ProductInputDTO inputDTO = new ProductInputDTO("product name", 10.00, 10);
        ProductOutputDTO outputDTO = productService.create(inputDTO);

        Assertions.assertThat(outputDTO)
                .usingRecursiveComparison()
                .isEqualTo(product);

    }

    @Test
    @DisplayName("when create product service is called with duplicated name throw ProductAlreadyExistsException.")
    public void createProductExceptionTest(){

        Product product = new Product(1L, "product name", 10.00, 10);

        when(productRepository.findByNameIgnoreCase(any())).thenReturn(Optional.of(product));

        ProductInputDTO inputDTO = new ProductInputDTO("product name", 10.00, 10);

        Assertions.assertThatExceptionOfType(ProductAlreadyExistsException.class)
                .isThrownBy(() -> productService.create(inputDTO));

    }

    @Test
    @DisplayName("when findById product is called with a valid id a product is returned")
    public void findByIdSuccessTest(){

        Long id = 1L;

        Product product = new Product(1L, "product name", 10.00, 10);

        when(productRepository.findById(any())).thenReturn(Optional.of(product));

        ProductOutputDTO outputDTO = productService.findById(id);

        Assertions.assertThat(outputDTO)
                .usingRecursiveComparison()
                .isEqualTo(product);

    }

    @Test
    @DisplayName("when findById product is called with a invalid id throws ProductNotFoundException.")
    public void findByIdExceptionTest(){

        Long id = 1L;

        Product product = new Product(1L, "product name", 10.00, 10);

        when(productRepository.findById(any())).thenReturn(Optional.empty());

        Assertions.assertThatExceptionOfType(ProductNotFoundException.class)
                .isThrownBy(() -> productService.findById(id));

    }

    @Test
    @DisplayName("when delete method is called should remove a product.")
    public void deleteProductSuccessTest(){

        Long id = 1L;

        Product product = new Product(1L, "product name", 10.00, 10);

        when(productRepository.findById(any())).thenReturn(Optional.of(product));

        Assertions.assertThatNoException()
                .isThrownBy(() -> productService.findById(id));

    }

    @Test
    @DisplayName("when delete method is called with not exist id throws ProductNotFoundException.")
    public void deleteProductExceptionTest(){

        Long id = 10L;

        when(productRepository.findById(any())).thenReturn(Optional.empty());

        Assertions.assertThatExceptionOfType(ProductNotFoundException.class)
                .isThrownBy(() -> productService.delete(id));

    }

    @Test
    @DisplayName("when findAll product service is called a list of product is returned.")
    public void findAllProductSuccessTest(){

        List<Product> products = List.of(
                new Product(1L, "product name A", 10.00, 10),
                new Product(2L, "product name B", 11.00, 100)
        );

        when(productRepository.findAll()).thenReturn(products);

        List<ProductOutputDTO> productOutputDTOList = productService.findAll();

        Assertions.assertThat(productOutputDTOList)
                .extracting("id","name","price","quantityInStock")
                .contains(
                        tuple(1L,"product name A",10.00, 10),
                        tuple(2L,"product name B",11.00, 100)
                );

    }
}
