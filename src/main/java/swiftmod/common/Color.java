package swiftmod.common;

public enum Color
{
    Transparent(0, "None"),
    Black(1, "Black"),
    Gray(2, "Gray"),
    LightGray(3, "Light Gray"),
    White(4, "White"),
    Pink(5, "Pink"),
    Red(6, "Red"),
    Orange(7, "Orange"),
    Yellow(8, "Yellow"),
    Lime(9, "Lime"),
    Green(10, "Green"),
    LightBlue(11, "Light Blue"),
    Cyan(12, "Cyan"),
    Blue(13, "Blue"),
    Magenta(14, "Magenta"),
    Purple(15, "Purple"),
    Brown(16, "Brown"),
    Rainbow(17, "Rainbow");

    private int index;
    private String name;

    private Color(int i, String n)
    {
        index = i;
        name = n;
    }

    public int getIndex()
    {
        return index;
    }

    public String getName()
    {
        return name;
    }

    private static final Color[] BY_INDEX = { Transparent, Black, Gray, LightGray, White, Pink, Red, Orange,
            Yellow, Lime, Green, LightBlue, Cyan, Blue, Magenta, Purple, Brown, Rainbow };

    public static Color fromIndex(int index)
    {
        return BY_INDEX[index];
    }
}
