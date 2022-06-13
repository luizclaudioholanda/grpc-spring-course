package br.com.grpcproject.resources;

import br.com.grpcproject.*;
import br.com.grpcproject.dto.ProductInputDTO;
import br.com.grpcproject.dto.ProductOutputDTO;
import br.com.grpcproject.service.ProductService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;
import java.util.stream.Collectors;

@GrpcService
public class ProductResource extends ProductServiceGrpc.ProductServiceImplBase {

    private final ProductService productService;

    public ProductResource(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void create(ProductRequest request, StreamObserver<ProductResponse> responseObserver) {

        var productInputDTO = new ProductInputDTO(request.getName(),
                request.getPrice(),
                request.getQuantityInStock()
        );

        var productOutputDTO = productService.create(productInputDTO);

        ProductResponse productResponse = ProductResponse.newBuilder()
                .setId(productOutputDTO.getId())
                .setName(productOutputDTO.getName())
                .setPrice(productOutputDTO.getPrice())
                .setQuantityInStock(productOutputDTO.getQuantityInStock())
                .build();

        responseObserver.onNext(productResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void findById(RequestById request, StreamObserver<ProductResponse> responseObserver) {

        ProductOutputDTO outputDTO = productService.findById(request.getId());

        var productResponse = ProductResponse.newBuilder()
                .setId(outputDTO.getId())
                .setName(outputDTO.getName())
                .setPrice(outputDTO.getPrice())
                .setQuantityInStock(outputDTO.getQuantityInStock())
                .build();

        responseObserver.onNext(productResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteById(RequestById request, StreamObserver<EmptyResponse> responseObserver) {
        productService.delete(request.getId());

        responseObserver.onNext(EmptyResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void findAll(EmptyRequest request, StreamObserver<ProductResponseList> responseObserver) {

        List<ProductOutputDTO> outputDTOList = productService.findAll();

        List<ProductResponse> productResponseList = outputDTOList.stream()
                .map(outputDTO -> ProductResponse.newBuilder()
                        .setId(outputDTO.getId())
                        .setName(outputDTO.getName())
                        .setPrice(outputDTO.getPrice())
                        .setQuantityInStock(outputDTO.getQuantityInStock())
                        .build())
                .collect(Collectors.toList());

        ProductResponseList response = ProductResponseList.newBuilder()
                .addAllProducts(productResponseList)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }
}
