package ru.music.streaming.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public class DailyMixRequest {

    @Size(max = 100, message = "Название микса не должно превышать 100 символов")
    private String name;

    @Size(max = 500, message = "Описание микса не должно превышать 500 символов")
    private String description;

    @Size(max = 100, message = "Название жанра слишком длинное")
    private String genre;

    @Min(value = 1, message = "Количество треков должно быть больше нуля")
    @Max(value = 50, message = "За один микс можно выбрать не более 50 треков")
    private Integer limit;

    private Boolean makePublic;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Boolean getMakePublic() {
        return makePublic;
    }

    public void setMakePublic(Boolean makePublic) {
        this.makePublic = makePublic;
    }
}
