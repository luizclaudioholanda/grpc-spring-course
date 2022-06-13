package br.com.grpcproject.util;

import br.com.grpcproject.domain.Product;
import br.com.grpcproject.dto.ProductInputDTO;
import br.com.grpcproject.dto.ProductOutputDTO;

public class ProductConverterUtil {

    public static ProductOutputDTO toDTO(Product model){
        return new ProductOutputDTO(
                model.getId(),
                model.getName(),
                model.getPrice(),
                model.getQuantityInStock()
        );
    }

    public static Product toModel(ProductInputDTO dto){
        return new Product(
                null,
                dto.getName(),
                dto.getPrice(),
                dto.getQuantityInStock()
        );
    }
}
