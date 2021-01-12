package github.tuquanrong.model.constant;

/**
 * tutu
 * 2021/1/5
 * MagicNumber(4)
 * PackageLength(4)
 * Version(1)
 * SerializationType(1)
 * MessageType(1)
 * RequestId(4)
 */
public class PackageConstant {
    public static final byte[] MagicNumber = new byte[]{'t', 'u', 't', 'u'};
    public static final byte BetaVersion = 1;
    public static final byte ProtostufSerializer = 1;
    public static final byte RequestPackage = 1;
    public static final byte ResposnePackage = 2;
    public static final int HeaderLength = 15;
    public static final int PackageMaxLength = 65535;
}
