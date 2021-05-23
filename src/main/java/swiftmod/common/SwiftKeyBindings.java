package swiftmod.common;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;

public class SwiftKeyBindings
{
    @SuppressWarnings("resource")
    public static boolean isSneakKeyPressed()
    {
        return isKeyDown(Minecraft.getInstance().options.keyShift);
    }

    @SuppressWarnings("resource")
    public static boolean isSprintKeyPressed()
    {
        return isKeyDown(Minecraft.getInstance().options.keySprint);
    }

    public static boolean isShiftKeyPressed()
    {
        return isKeyDown(340) || isKeyDown(344);
    }

    public static boolean isControlKeyPressed()
    {
        if (Minecraft.ON_OSX)
            return isKeyDown(343) || isKeyDown(347);
        else
            return isKeyDown(341) || isKeyDown(345);
    }

    public static boolean isKeyDown(KeyBinding keyBinding)
    {
        InputMappings.Input key = keyBinding.getKey();
        int keyCode = key.getValue();
        if (keyCode != InputMappings.UNKNOWN.getValue())
        {
            long windowHandle = Minecraft.getInstance().getWindow().getWindow();
            try
            {
                if (key.getType() == InputMappings.Type.KEYSYM)
                {
                    return InputMappings.isKeyDown(windowHandle, keyCode);
                }
                else if (key.getType() == InputMappings.Type.MOUSE)
                {
                    return GLFW.glfwGetMouseButton(windowHandle, keyCode) == GLFW.GLFW_PRESS;
                }
            }
            catch (Exception ignored)
            {
            }
        }
        return false;
    }

    public static boolean isKeyDown(int keyCode)
    {
        if (keyCode != InputMappings.UNKNOWN.getValue())
        {
            long windowHandle = Minecraft.getInstance().getWindow().getWindow();
            try
            {
                return InputMappings.isKeyDown(windowHandle, keyCode);
            }
            catch (Exception ignored)
            {
            }
        }
        return false;
    }
}
