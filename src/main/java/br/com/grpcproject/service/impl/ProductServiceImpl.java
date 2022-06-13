package br.com.grpcproject.service.impl;

import br.com.grpcproject.domain.Product;
import br.com.grpcproject.dto.ProductInputDTO;
import br.com.grpcproject.dto.ProductOutputDTO;
import br.com.grpcproject.exceptions.ProductAlreadyExistsException;
import br.com.grpcproject.exceptions.ProductNotFoundException;
import br.com.grpcproject.repository.ProductRepository;
import br.com.grpcproject.service.ProductService;
import br.com.grpcproject.util.ProductConverterUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductOutputDTO create(ProductInputDTO inputDTO) {

        checkDuplicity(inputDTO.getName());

        var product = ProductConverterUtil.toModel(inputDTO);
        var productCreated = this.productRepository.save(product);

        return ProductConverterUtil.toDTO(productCreated);
    }

    @Override
    public ProductOutputDTO findById(Long id) {

        var product = this.productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        return ProductConverterUtil.toDTO(product);
    }

    @Override
    public void delete(Long id) {

        var product = this.productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        this.productRepository.delete(product);
    }

    @Override
    public List<ProductOutputDTO> findAll() {
        List<Product> productList = this.productRepository.findAll();

        return productList.stream()
                .map(ProductConverterUtil::toDTO)
                .collect(Collectors.toList());
    }

    private void checkDuplicity(final String name) {

        this.productRepository.findByNameIgnoreCase(name)
                .ifPresent(e -> {
                    throw new ProductAlreadyExistsException(name);
                });
    }
}
