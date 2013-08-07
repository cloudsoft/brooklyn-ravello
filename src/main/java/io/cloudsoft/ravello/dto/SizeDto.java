package io.cloudsoft.ravello.dto;

import static com.google.common.base.Preconditions.checkArgument;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SizeDto {

    private final String unit;
    private final Integer value;

    public SizeDto(
            @JsonProperty("value") Integer value,
            @JsonProperty("unit") String unit) {
        this.unit = unit;
        this.value = value;
    }

    public static SizeDto gigabytes(Integer value) {
        checkArgument(value > 0);
        return new SizeDto(value, "GB");
    }

    public Integer getValue() {
        return value;
    }

    public String getUnit() {
        return unit;
    }

}
