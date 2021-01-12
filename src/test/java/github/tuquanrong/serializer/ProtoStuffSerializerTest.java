package github.tuquanrong.serializer;

import java.util.Arrays;

public class ProtoStuffSerializerTest {
    public static void main(String[] argv) {
        Test test = new Test("one", "two");
        Serializer serializer=new ProtostuffSerializer();
        byte[] bytes = serializer.serialize(test);
        System.out.println(Arrays.toString(bytes));
        Test output=serializer.deserialize(bytes,Test.class);
        System.out.println(output);
    }
}

class Test {
    private String name1;
    private String name2;

    Test(String name1, String name2) {
        this.name1 = name1;
        this.name2 = name2;
    }

    @Override
    public String toString() {
        return "Test{" +
                "name1='" + name1 + '\'' +
                ", name2='" + name2 + '\'' +
                '}';
    }
}