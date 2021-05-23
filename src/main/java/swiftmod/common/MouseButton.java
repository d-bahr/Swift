package swiftmod.common;

public enum MouseButton
{
    Left(0),
    Right(1),
    Middle(2),
    Button3(3),
    Button4(4),
    Button5(5),
    Button6(6),
    Button7(7),
    Button8(8),
    Other(9);

    private MouseButton(int i)
    {
        button = i;
    }

    public int get()
    {
        return button;
    }

    public static MouseButton from(int i)
    {
        switch (i)
        {
        case 0: return Left;
        case 1: return Right;
        case 2: return Middle;
        case 3: return Button3;
        case 4: return Button4;
        case 5: return Button5;
        case 6: return Button6;
        case 7: return Button7;
        case 8: return Button8;
        default: return Other;
        }
    }

    private int button;
}
