package github.tuquanrong.serializer;

/**
 * tutu
 * 2021/1/7
 */
public interface Serializer {
    <T> byte[] serialize(T obj);

    <T> T deserialize(byte[] data, Class<T> clazz);
}
