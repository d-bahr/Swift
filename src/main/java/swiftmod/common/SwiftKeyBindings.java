package swiftmod.common;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

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

    public static boolean isKeyDown(KeyMapping keyBinding)
    {
    	InputConstants.Key key = keyBinding.getKey();
        int keyCode = key.getValue();
        if (keyCode != InputConstants.UNKNOWN.getValue())
        {
            long windowHandle = Minecraft.getInstance().getWindow().getWindow();
            try
            {
                if (key.getType() == InputConstants.Type.KEYSYM)
                {
                    return InputConstants.isKeyDown(windowHandle, keyCode);
                }
                else if (key.getType() == InputConstants.Type.MOUSE)
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
        if (keyCode != InputConstants.UNKNOWN.getValue())
        {
            long windowHandle = Minecraft.getInstance().getWindow().getWindow();
            try
            {
                return InputConstants.isKeyDown(windowHandle, keyCode);
            }
            catch (Exception ignored)
            {
            }
        }
        return false;
    }
}
