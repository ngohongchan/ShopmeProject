package com.shopme.common.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "brands")
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false,length = 45, unique = true)
    private String name;

    @Column(nullable = false,length = 45)
    private String logo;

    @ManyToMany
    @JoinTable(name = "brands_categories",
            joinColumns = @JoinColumn(name = "brand_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories = new HashSet<>();

    public Brand() {
    }

    public Brand(String name) {
        this.name = name;
        this.logo = "default";
    }

    public Brand(String name, String logo) {
        this.name = name;
        this.logo = logo;
    }

    public Brand(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    @Override
    public String toString() {
        return "Brand{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", logo='" + logo + '\'' +
                ", categories=" + categories +
                '}';
    }

    @Transient
    public String getLogoPath() {
//        /images/image-thumbnail.png
        if(this.id == null) return "https://www.contentviewspro.com/wp-content/uploads/2017/07/default_image.png";

        return "/brand-logos/" + this.id + "/" + this.logo;
    }
}
