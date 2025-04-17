package by.kirylarol.spendsculptor.entities;


import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

@Entity
@Table(name = "Categories")
public class Category {


    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "category_id")
    private int categoryId;

    @Column(unique = true, name = "category_name")
    private String categoryName;

    @OneToMany (mappedBy = "category",fetch=FetchType.EAGER)
    private List<Position> positions = new ArrayList<>();


    @JsonGetter
    public int categoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    @JsonGetter
    public String categoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(categoryName, category.categoryName);
    }

    public Category() {
    }

    public Category(String categoryName) {
        this.categoryName = categoryName;
    }

    @JsonIgnore
    public List<Position> getPositions() {
        return positions;
    }

    public void setPositions(List<Position> positions) {
        this.positions = positions;
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryId, categoryName, positions);
    }
}
