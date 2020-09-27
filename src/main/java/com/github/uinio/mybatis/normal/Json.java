package com.github.uinio.mybatis.normal;

import java.util.Objects;

/**
 * @author uinio
 */
public enum Json {
    fastJson,
    jackson;

    public static Json get(String json) {
        if (Objects.equals(json, Json.fastJson.name())) {
            return Json.fastJson;
        } else if (Objects.equals(json, Json.jackson.name())) {
            return Json.jackson;
        }
        return null;
    }
}