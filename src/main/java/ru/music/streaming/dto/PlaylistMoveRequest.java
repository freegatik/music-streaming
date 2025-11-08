package ru.music.streaming.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class PlaylistMoveRequest {

    @NotNull(message = "Укажите трек, который нужно переместить")
    private Long trackId;

    @NotNull(message = "Укажите новую позицию")
    @Min(value = 0, message = "Позиция должна быть неотрицательной")
    private Integer newPosition;

    public Long getTrackId() {
        return trackId;
    }

    public void setTrackId(Long trackId) {
        this.trackId = trackId;
    }

    public Integer getNewPosition() {
        return newPosition;
    }

    public void setNewPosition(Integer newPosition) {
        this.newPosition = newPosition;
    }
}
