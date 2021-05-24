package com.apex.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocaleData {
    private String type;
    private List<LocaleEntry> entries;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocaleData that = (LocaleData) o;

        return Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return type != null ? type.hashCode() : 0;
    }
}
