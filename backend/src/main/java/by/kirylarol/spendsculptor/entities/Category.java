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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private int categoryId;

    @Column(unique = true, name = "category_name")
    private String categoryName;

    @Column(name = "spending_limit")
    private Double spendingLimit;

    @Column(name = "notification_threshold")
    private Integer notificationThreshold;

    @OneToMany(mappedBy = "category", fetch = FetchType.EAGER)
    private List<Position> positions = new ArrayList<>();

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CategoryLimitNotification> notifications = new ArrayList<>();

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

    @JsonGetter
    public Double getSpendingLimit() {
        return spendingLimit;
    }

    public void setSpendingLimit(Double spendingLimit) {
        this.spendingLimit = spendingLimit;
    }

    @JsonGetter
    public Integer getNotificationThreshold() {
        return notificationThreshold;
    }

    public void setNotificationThreshold(Integer notificationThreshold) {
        this.notificationThreshold = notificationThreshold;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(categoryName, category.categoryName);
    }

    public Category() {
        this.notificationThreshold = 80; // Default threshold at 80%
    }

    public Category(String categoryName) {
        this.categoryName = categoryName;
        this.notificationThreshold = 80; // Default threshold at 80%
    }

    public Category(String categoryName, Double spendingLimit, Integer notificationThreshold) {
        this.categoryName = categoryName;
        this.spendingLimit = spendingLimit;
        this.notificationThreshold = notificationThreshold != null ? notificationThreshold : 80;
    }

    @JsonIgnore
    public List<Position> getPositions() {
        return positions;
    }

    public void setPositions(List<Position> positions) {
        this.positions = positions;
    }

    @JsonIgnore
    public List<CategoryLimitNotification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<CategoryLimitNotification> notifications) {
        this.notifications = notifications;
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryId, categoryName, positions, spendingLimit, notificationThreshold);
    }
}
