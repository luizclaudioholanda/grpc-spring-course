package br.com.grpcproject.util;


import br.com.grpcproject.domain.Product;
import br.com.grpcproject.dto.ProductInputDTO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProductConverterUtilTest {

    @Test
    public void productToProductOutputDtoTest() {

        var product = new Product(1L, "product name", 10.0, 10);

        var productOutputDto = ProductConverterUtil.toDTO(product);

        Assertions.assertThat(product)
                .usingRecursiveComparison()
                .isEqualTo(productOutputDto);
    }

    @Test
    public void productInputDtoToProductTest() {

        var productInput = new ProductInputDTO("product name", 10.0, 10);

        var productOutputDto = ProductConverterUtil.toModel(productInput);

        Assertions.assertThat(productInput)
                .usingRecursiveComparison()
                .isEqualTo(productOutputDto);
    }
}