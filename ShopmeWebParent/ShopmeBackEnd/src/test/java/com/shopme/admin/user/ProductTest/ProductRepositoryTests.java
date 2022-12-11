package com.shopme.admin.user.ProductTest;

import static org.assertj.core.api.Assertions.assertThat;

import com.shopme.admin.product.repository.ProductRepository;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateProduct() {
        Brand brand = entityManager.find(Brand.class, 3);
        Category category = entityManager.find(Category.class, 15);

        Product product = new Product();
        product.setName("Samsung Galaxy A31");
        product.setAlias("samsung_galaxy_a31");
        product.setShortDescription("A good smartphone froms Samsung");
        product.setFullDescription("This is a very good smartphone full description");

        product.setBrand(brand);
        product.setCategory(category);

        product.setPrice(456);
        product.setCreatedTime(new Date());
        product.setUpdatedTime(new Date());

        Product savedProduct = productRepository.save(product);

        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateProduct2() {
        Brand brand = entityManager.find(Brand.class, 4);
        Category category = entityManager.find(Category.class, 15);

        Product product = new Product();
        product.setName("Dell Inspiron 3000");
        product.setAlias("dell_inspiron_3000");
        product.setShortDescription("Short description for dell Inspiron 3000");
        product.setFullDescription("Full description for dell Inspiron 3000");

        product.setBrand(brand);
        product.setCategory(category);

        product.setPrice(456);
        product.setCost(400);
        product.setEnabled(true);
        product.setInStock(true);

        product.setCreatedTime(new Date());
        product.setUpdatedTime(new Date());

        Product savedProduct = productRepository.save(product);

        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateProduct3() {
        Brand brand = entityManager.find(Brand.class, 4);
        Category category = entityManager.find(Category.class, 15);

        Product product = new Product();
        product.setName("Acer Aspire Desktop");
        product.setAlias("acer_aspire_desktop");
        product.setShortDescription("Short description for Acer Aspire");
        product.setFullDescription("Full description for dell Acer Aspire");

        product.setBrand(brand);
        product.setCategory(category);

        product.setPrice(678);
        product.setCost(600);
        product.setEnabled(true);
        product.setInStock(true);

        product.setCreatedTime(new Date());
        product.setUpdatedTime(new Date());

        Product savedProduct = productRepository.save(product);

        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isGreaterThan(0);
    }

    @Test
    public void testListAllProducts() {
        List<Product> productList = (List<Product>) productRepository.findAll();

        productList.forEach(System.out::println);
    }

    @Test
    public void testGetProduct() {
        Integer id = 2;
        Product product = productRepository.findById(id).get();

        assertThat(product).isNotNull();
    }

    @Test
    public void testUpdateProduct() {
        Integer id = 1;
        Product product = productRepository.findById(id).get();
        product.setPrice(499);

        productRepository.save(product);

        Product updatedProduct = entityManager.find(Product.class, id);

        assertThat(updatedProduct.getPrice()).isEqualTo(499);
    }

    @Test
    public void testDeleteProduct() {
        Integer id = 3;

        productRepository.deleteById(id);

        Optional<Product> result = productRepository.findById(id);

        assertThat(!result.isPresent());
    }

}
