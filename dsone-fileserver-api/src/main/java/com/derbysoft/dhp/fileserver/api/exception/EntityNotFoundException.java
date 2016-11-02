package com.derbysoft.dhp.fileserver.api.exception;

public class EntityNotFoundException extends RuntimeException {
    private Object id;

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(Class<?> clazz, Object id) {
        super("" + clazz.getSimpleName() +"#" + id + " not found");
        this.id = id;
    }

    public EntityNotFoundException(Object id, String message) {
        super("" + message);
        this.id = id;

    }


    public Object getEntity() {
        return id;
    }
}
