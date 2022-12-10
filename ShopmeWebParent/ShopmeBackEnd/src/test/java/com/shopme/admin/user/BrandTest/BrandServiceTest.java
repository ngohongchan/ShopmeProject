package com.shopme.admin.user.BrandTest;

import com.shopme.admin.brand.repository.BrandRepository;
import com.shopme.admin.brand.service.BrandService;
import com.shopme.common.entity.Brand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class BrandServiceTest {

    @MockBean private BrandRepository repository;

    @InjectMocks private BrandService service;

    @Test
    public void testCheckUniqueInNewReturnDuplicate() {
        Integer id = null;
        String name = "Acer";
        Brand brand = new Brand(name);

        Mockito.when(repository.findByName(name)).thenReturn(brand);
        String result = service.checkUnique(id, name);
        assertThat(result).isEqualTo("Duplicate");
    }

    @Test
    public void testCheckUniqueInNewReturnOk() {
        Integer id = null;
        String name = "Cannon";
        Brand brand = new Brand(id, name);

        Mockito.when(repository.findByName(name)).thenReturn(null);
        String result = service.checkUnique(id, name);
        assertThat(result).isEqualTo("Ok");
    }

    @Test
    public void testCheckUniqueInEditReturnDuplicate() {
        Integer id = 1;
        String name = "Acer";
        Brand brand = new Brand(id, name);

        Mockito.when(repository.findByName(name)).thenReturn(brand);
        String result = service.checkUnique(2, "Acer");
        assertThat(result).isEqualTo("Duplicate");
    }

    @Test
    public void testCheckUniqueInEditReturnOk() {
        Integer id = 1;
        String name = "Acer";
        Brand brand = new Brand(id, name);

        Mockito.when(repository.findByName(name)).thenReturn(brand);
        String result = service.checkUnique(id, name);
        assertThat(result).isEqualTo("Ok");

    }

    @Test
    public void testCheckUniqueInEditModeReturnOk() {
        Integer id = 1;
        String name = "Xiaomi";

        Mockito.when(repository.findByName(name)).thenReturn(null);
        String result = service.checkUnique(id, name);
        assertThat(result).isEqualTo("Ok");

    }
}
