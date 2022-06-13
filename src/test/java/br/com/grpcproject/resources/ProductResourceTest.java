package br.com.grpcproject.resources;


import br.com.grpcproject.*;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.assertj.core.api.Assertions;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.groups.Tuple.tuple;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
@DirtiesContext
public class ProductResourceTest {

    @GrpcClient("inProcess")
    private ProductServiceGrpc.ProductServiceBlockingStub serviceBlockingStub;

    @Autowired
    private Flyway flyway;

    @BeforeEach
    public void setUp(){

        flyway.clean();
        flyway.migrate();
    }

    @Test
    @DisplayName("when valid data is provided a product is created.")
    public void createProductSuccessTest(){

        ProductRequest productRequest = ProductRequest.newBuilder()
                .setName("product name")
                .setPrice(10.00)
                .setQuantityInStock(100)
                .build();

        ProductResponse productResponse = serviceBlockingStub.create(productRequest);

        Assertions.assertThat(productRequest)
                .usingRecursiveComparison()
                .comparingOnlyFields("name","price","quantity_in_stock")
                .isEqualTo(productResponse);
    }

    @Test
    @DisplayName("when create is called with duplicated name, throw ProductAlreadyExistsException.")
    public void createProductAlreadyExistsExceptionTest(){

        ProductRequest productRequest = ProductRequest.newBuilder()
                .setName("Product A")
                .setPrice(10.00)
                .setQuantityInStock(100)
                .build();

        Assertions.assertThatExceptionOfType(StatusRuntimeException.class)
                .isThrownBy(() -> serviceBlockingStub.create(productRequest))
                .withMessage("ALREADY_EXISTS: Product Product A already register on system");

    }

    @Test
    @DisplayName("when findById is called a valid product is returned.")
    public void findByIdProductSuccessTest(){

        RequestById request = RequestById.newBuilder()
                .setId(1l)
                .build();

        ProductResponse productResponse = serviceBlockingStub.findById(request);

        Assertions.assertThat(productResponse.getId()).isEqualTo(request.getId());
    }

    @Test
    @DisplayName("when findById is called with an invalid throws ProductNotFoundException.")
    public void findByIdProductExceptionTest(){

        RequestById request = RequestById.newBuilder()
                .setId(10l)
                .build();

        Assertions.assertThatExceptionOfType(StatusRuntimeException.class)
                .isThrownBy(() -> serviceBlockingStub.findById(request))
                .withMessage("NOT_FOUND: Product ID 10 not found");
    }

    @Test
    @DisplayName("when delete is called with a valid ID should not throw.")
    public void deleteProductSuccessTest(){

        RequestById request = RequestById.newBuilder()
                .setId(1l)
                .build();

        Assertions.assertThatNoException().isThrownBy(() -> serviceBlockingStub.deleteById(request));
    }

    @Test
    @DisplayName("when delete is called with an invalid ID should throw ProductNotFoundException.")
    public void deleteProductExceptionTest(){

        RequestById request = RequestById.newBuilder()
                .setId(100l)
                .build();

        Assertions.assertThatExceptionOfType(StatusRuntimeException.class)
                .isThrownBy(() -> serviceBlockingStub.deleteById(request))
                .withMessage("NOT_FOUND: Product ID 100 not found");
    }

    @Test
    @DisplayName("when findAll is called a valid product list is returned.")
    public void findAllProductSuccessTest(){

        EmptyRequest emptyRequest = EmptyRequest.newBuilder().build();

        ProductResponseList productResponseList = serviceBlockingStub.findAll(emptyRequest);

        Assertions.assertThat(productResponseList).isInstanceOf(ProductResponseList.class);
        Assertions.assertThat(productResponseList.getProductsCount()).isEqualTo(2);

        Assertions.assertThat(productResponseList.getProductsList())
                .extracting("id","name","price","quantityInStock")
                .contains(
                        tuple(1L,"Product A", 10.99, 10),
                        tuple(2L,"Product B", 10.99, 10)
                );
    }
}