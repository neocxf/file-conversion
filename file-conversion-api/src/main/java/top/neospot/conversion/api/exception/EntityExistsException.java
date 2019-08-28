package top.neospot.conversion.api.exception;

/**
 * @author Xiaofei Chen <a href="mailto:neocxf@qq.com">Email the author</a>
 * @version 1.0 1/6/2016
 */
public class EntityExistsException extends RuntimeException {

    public EntityExistsException(Class<?> clazz, String key) {
        super(clazz.getSimpleName() + " --- " + key + " already existed");
    }
}
