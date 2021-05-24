package com.apex.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocaleEntry {
    private String id;
    private String en;
    private String fr;
    private String es;
    private String de;
    private String pl;
    private String ru;
    private String uk;
    private String ko;
    private String zh;
    private String ja;

    public void set(String value, Language language) {
        switch (language) {
            case DE:
                de = value;
                break;
            case EN:
                en = value;
                break;
            case ES:
                es = value;
                break;
            case FR:
                fr = value;
                break;
            case JA:
                ja = value;
                break;
            case KO:
                ko = value;
                break;
            case PL:
                pl = value;
                break;
            case RU:
                ru = value;
                break;
            case UK:
                uk = value;
                break;
            case ZH:
                zh = value;
                break;
            default:
                break;
        }
    }
}
