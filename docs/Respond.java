package com.github.uinios.mybatis.basic.io;


import java.util.Objects;

/**
 * Response result set
 *
 * @author Jingle-Cat
 */

public class Respond {

    private Respond() {
    }

    private int status;

    private Object json;

    private Object message;

    //200
    public static Respond ok(Object message) {
        Respond respond = new Respond();
        respond.setStatus(200);
        respond.setMessage(message);
        return respond;
    }

    //401
    public static Respond unAuthorized(Object message) {
        Respond respond = new Respond();
        respond.setStatus(401);
        respond.setMessage(message);
        return respond;
    }

    //403
    public static Respond forbidden(Object message) {
        Respond respond = new Respond();
        respond.setStatus(403);
        respond.setMessage(message);
        return respond;
    }

    //404
    public static Respond notFound(Object message) {
        Respond respond = new Respond();
        respond.setStatus(404);
        respond.setMessage(message);
        return respond;
    }

    //500
    public static Respond error(Object message) {
        Respond respond = new Respond();
        respond.setStatus(500);
        respond.setMessage(message);
        return respond;
    }

    public static Respond success(Object data) {
        final Respond respond = new Respond();
        respond.setStatus(200);
        respond.setJson(data);
        return respond;
    }

    public static Respond success(Object message, Object data) {
        final Respond respond = new Respond();
        respond.setStatus(200);
        respond.setMessage(message);
        respond.setJson(data);
        return respond;
    }

    public static Respond success(String message, String... contents) {
        final Respond respond = new Respond();
        respond.setStatus(200);
        if (Objects.nonNull(message)) {
            for (String content : contents) {
                message = message.replaceFirst("\\{}", content);
            }
        }
        respond.setMessage(message);
        return respond;
    }

    public static Respond failure(Object message) {
        final Respond respond = new Respond();
        respond.setStatus(500);
        respond.setMessage(message);
        return respond;
    }

    public static Respond failure(Object message, Object data) {
        final Respond respond = new Respond();
        respond.setStatus(500);
        respond.setMessage(message);
        respond.setJson(data);
        return respond;
    }

    public static Respond failure(String message, String... contents) {
        final Respond respond = new Respond();
        respond.setStatus(500);
        if (Objects.nonNull(message)) {
            for (String content : contents) {
                message = message.replaceFirst("\\{}", content);
            }
        }
        respond.setMessage(message);
        return respond;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Object getJson() {
        return json;
    }

    public void setJson(Object json) {
        this.json = json;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }
}
