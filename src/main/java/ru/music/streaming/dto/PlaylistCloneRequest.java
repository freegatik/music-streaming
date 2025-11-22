package ru.music.streaming.dto;

import jakarta.validation.constraints.Size;

public class PlaylistCloneRequest {

    @Size(max = 100, message = "Название не должно превышать 100 символов")
    private String name;

    @Size(max = 500, message = "Описание не должно превышать 500 символов")
    private String description;

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

    public Boolean getMakePublic() {
        return makePublic;
    }

    public void setMakePublic(Boolean makePublic) {
        this.makePublic = makePublic;
    }
}
