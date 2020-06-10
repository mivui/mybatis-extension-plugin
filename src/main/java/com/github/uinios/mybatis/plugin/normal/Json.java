package com.github.uinios.mybatis.plugin.normal;

import java.util.Objects;

public enum Json {
    fastjon,
    jackson;

    public static Json get(String json) {
        if (Objects.equals(json, Json.fastjon.name())) {
            return Json.fastjon;
        } else if (Objects.equals(json, Json.jackson.name())) {
            return Json.jackson;
        }
        return null;
    }
}