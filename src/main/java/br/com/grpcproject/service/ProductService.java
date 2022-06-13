package br.com.grpcproject.service;

import br.com.grpcproject.dto.ProductInputDTO;
import br.com.grpcproject.dto.ProductOutputDTO;

import java.util.List;

public interface ProductService {

    ProductOutputDTO create (ProductInputDTO inputDTO);
    ProductOutputDTO findById (Long id);
    void delete (Long id);
    List<ProductOutputDTO> findAll();
}
