package com.shopme.admin.user.CategoryTest;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import com.shopme.admin.brand.repository.BrandRepository;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;



@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class BrandRepositoryTests {

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateBrand1() {
        Category category = new Category(6);
        Brand acer = new Brand("Acer");

        acer.getCategories().add(category);

        Brand savedBrand = brandRepository.save(acer);

        assertThat(savedBrand).isNotNull();
        assertThat(savedBrand.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateBrand2() {
        Category cellphones = new Category(4);
        Category tablets = new Category(7);

        Brand apple = new Brand("apple watch");
        apple.getCategories().addAll(List.of(cellphones, tablets));

        Brand savedBrand = brandRepository.save(apple);

        assertThat(savedBrand).isNotNull();
        assertThat(savedBrand.getId()).isGreaterThan(0);
    }


    @Test
    public void testUpdateBrand() {
        String newBrand = "Apple Watch";
        Brand samsung = brandRepository.findById(3).get();
        samsung.setName(newBrand);

        Brand savedBrand = brandRepository.save(samsung);

        assertThat(savedBrand.getName()).isEqualTo(newBrand);
    }

    @Test
    public void testDeleteBrand() {
       Integer id = 3;
       brandRepository.deleteById(id);

        Optional<Brand> result = brandRepository.findById(3);

        assertThat(result.isEmpty());
    }


}
