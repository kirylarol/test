package by.kirylarol.spendsculptor.dto;

import by.kirylarol.spendsculptor.entities.Category;
import by.kirylarol.spendsculptor.entities.Position;

import java.util.ArrayList;
import java.util.List;

public class CategoryWithPositionsDTO {
    private String categoryName;
    private List<PostitionWithDateDTO> positionList;

    public CategoryWithPositionsDTO(Category category){
        this.categoryName = category.categoryName();
        positionList = new ArrayList<>();
        category.getPositions().forEach(
                position -> {
                    positionList.add(new PostitionWithDateDTO(position));
                });
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<PostitionWithDateDTO> getPositionList() {
        return positionList;
    }

    public void setPositionList(List<PostitionWithDateDTO> positionList) {
        this.positionList = positionList;
    }
}
