package mx.tecnm.toluca.service;

import mx.tecnm.toluca.model.Product;
import mx.tecnm.toluca.repository.ProductRepository;

import java.io.InputStream;
import java.util.List;

public class ProductService {
    private final ProductRepository productRepository;

    public ProductService() {
        this.productRepository = new ProductRepository();
    }

    public void addProduct(Product product, InputStream imageStream, String imageName) {
        if (imageStream != null) {
            String imageId = productRepository.saveImage(imageStream, imageName);
            product.setImageId(imageId);
        }
        productRepository.save(product);
    }

    public void updateProduct(Product product, InputStream imageStream, String imageName) {
        if (imageStream != null) {
            Product existingProduct = productRepository.findById(product.getId());
            if (existingProduct != null && existingProduct.getImageId() != null) {
                productRepository.deleteImage(existingProduct.getImageId());
            }
            String imageId = productRepository.saveImage(imageStream, imageName);
            product.setImageId(imageId);
        }
        productRepository.update(product);
    }

    public void deleteProduct(String id) {
        productRepository.delete(id);
    }

    public Product getProductById(String id) {
        return productRepository.findById(id);
    }

    public List<Product> getProducts(int page, int pageSize) {
        return productRepository.findAll(page, pageSize);
    }

    public long getProductCount() {
        return productRepository.count();
    }

    public byte[] getProductImage(String imageId) {
        return productRepository.getImage(imageId);
    }
}