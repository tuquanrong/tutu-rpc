package github.tuquanrong.serializer;

/**
 * tutu
 * 2021/1/7
 */
public interface Serializer {
    public <T> byte[] serialize(T obj);

    public <T> T deserialize(byte[] data, Class<T> clazz);
}
