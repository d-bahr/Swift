package swiftmod.common.gui;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import swiftmod.common.MouseButton;
import swiftmod.common.Swift;
import swiftmod.common.SwiftKeyBindings;

/**
 * Ripped lots of code from the Vanilla TextFieldWidget class.
 */
@OnlyIn(Dist.CLIENT)
public class GuiTextField extends GuiTextWidget implements IRenderable, IGuiEventListener
{
    /** Has the current text being edited on the textbox. */
    private String text = "";
    private int maxStringLength = 32;
    private int cursorCounter;
    private boolean enableBackgroundDrawing = true;
    /** If this value is true along with isFocused, keyTyped will process the keys. */
    private boolean isEnabled = true;
    private boolean suppressShift = false;
    /** The current character index that should be used as start of the rendered text. */
    private int lineScrollOffset;
    private int cursorPosition;
    /** other selection position, maybe the same as the cursor */
    private int selectionEnd;
    private int enabledColor = 14737632;
    private int disabledColor = 7368816;
    private String suggestion;
    private Consumer<String> guiResponder;
    /** Called to check if the text is valid */
    private Predicate<String> validator = Objects::nonNull;
    private BiFunction<String, Integer, IReorderingProcessor> textFormatter = (p_195610_0_, p_195610_1_) ->
    {
        return IReorderingProcessor.forward(p_195610_0_, Style.EMPTY);
    };

    public GuiTextField(GuiContainerScreen<?> screen, int x, int y, int width, int height, ITextComponent text)
    {
        this(screen, x, y, width, height, text, BACKGROUND_TEXTURE);
    }

    public GuiTextField(GuiContainerScreen<?> screen, int x, int y, int width, int height, ITextComponent text,
            ResourceLocation background)
    {
        super(screen, x, y, width, height, text);
        m_background = background;
        setText(text);
        m_playClickOnPress = false;
        suppressShift = false;
        m_lastMouseClick = 0;
        m_lastMouseClickPos = Integer.MIN_VALUE;
    }

    public GuiTextField(GuiContainerScreen<?> screen, int x, int y, int width, int height, ITextComponent text,
            FontRenderer font)
    {
        this(screen, x, y, width, height, text, font, BACKGROUND_TEXTURE);
    }

    public GuiTextField(GuiContainerScreen<?> screen, int x, int y, int width, int height, ITextComponent text,
            FontRenderer font, ResourceLocation background)
    {
        super(screen, x, y, width, height, text, font);
        m_background = background;
        setText(text);
        m_playClickOnPress = false;
        suppressShift = false;
        m_lastMouseClick = 0;
        m_lastMouseClickPos = Integer.MIN_VALUE;
    }

    public void setResponder(Consumer<String> rssponderIn)
    {
        this.guiResponder = rssponderIn;
    }

    public void setTextFormatter(BiFunction<String, Integer, IReorderingProcessor> textFormatterIn)
    {
        this.textFormatter = textFormatterIn;
    }
    
    public void setFocused(boolean focused)
    {
        super.setFocused(focused);
        if (!focused)
            m_lastMouseClickPos = Integer.MIN_VALUE;
    }

    /**
     * Increments the cursor counter
     */
    @Override
    public void tick()
    {
        super.tick();
        if (isFocused())
            ++cursorCounter;
        else
            cursorCounter = 0;
    }

    protected IFormattableTextComponent getNarrationMessage()
    {
        ITextComponent itextcomponent = this.getMessage();
        return new TranslationTextComponent("gui.narrate.editBox", itextcomponent, this.text);
    }

    /**
     * Sets the text of the textbox, and moves the cursor to the end.
     */
    public void setText(String textIn)
    {
        if (this.validator.test(textIn))
        {
            if (textIn.length() > this.maxStringLength)
            {
                this.text = textIn.substring(0, this.maxStringLength);
            }
            else
            {
                this.text = textIn;
            }

            this.setCursorPositionEnd();
            this.setSelectionPos(this.cursorPosition);
            this.onTextChanged(textIn);
        }
    }

    /**
     * Sets the text of the textbox, and moves the cursor to the end. TODO: Clean this up; this is
     * probably all wrong.
     */
    public void setText(ITextComponent text)
    {
        setText(text.getString());
    }

    /**
     * Returns the contents of the textbox TODO: Clean this up; this is probably all wrong.
     */
    public ITextComponent getText()
    {
        return new StringTextComponent(text);
    }

    /**
     * returns the text between the cursor and selectionEnd
     */
    public String getSelectedText()
    {
        int i = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
        int j = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
        return this.text.substring(i, j);
    }

    public void setValidator(Predicate<String> validatorIn)
    {
        this.validator = validatorIn;
    }

    /**
     * Adds the given text after the cursor, or replaces the currently selected text if there is a
     * selection.
     */
    public void writeText(String textToWrite)
    {
        int i = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
        int j = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
        int k = this.maxStringLength - this.text.length() - (i - j);
        String s = SharedConstants.filterText(textToWrite);
        int l = s.length();
        if (k < l)
        {
            s = s.substring(0, k);
            l = k;
        }

        String s1 = (new StringBuilder(this.text)).replace(i, j, s).toString();
        if (this.validator.test(s1))
        {
            this.text = s1;
            this.clampCursorPosition(i + l);
            this.setSelectionPos(this.cursorPosition);
            this.onTextChanged(this.text);
        }
    }

    private void onTextChanged(String newText)
    {
        if (this.guiResponder != null)
        {
            this.guiResponder.accept(newText);
        }

        this.nextNarration = Util.getMillis() + 500L;
    }

    private void delete(int p_212950_1_)
    {
        if (Screen.hasControlDown())
        {
            this.deleteWords(p_212950_1_);
        }
        else
        {
            this.deleteFromCursor(p_212950_1_);
        }

    }

    /**
     * Deletes the given number of words from the current cursor's position, unless there is currently a
     * selection, in which case the selection is deleted instead.
     */
    public void deleteWords(int num)
    {
        if (!this.text.isEmpty())
        {
            if (this.selectionEnd != this.cursorPosition)
            {
                this.writeText("");
            }
            else
            {
                this.deleteFromCursor(this.getNthWordFromCursor(num) - this.cursorPosition);
            }
        }
    }

    /**
     * Deletes the given number of characters from the current cursor's position, unless there is
     * currently a selection, in which case the selection is deleted instead.
     */
    public void deleteFromCursor(int num)
    {
        if (!this.text.isEmpty())
        {
            if (this.selectionEnd != this.cursorPosition)
            {
                this.writeText("");
            }
            else
            {
                int i = this.func_238516_r_(num);
                int j = Math.min(i, this.cursorPosition);
                int k = Math.max(i, this.cursorPosition);
                if (j != k)
                {
                    String s = (new StringBuilder(this.text)).delete(j, k).toString();
                    if (this.validator.test(s))
                    {
                        this.text = s;
                        this.setCursorPosition(j);
                    }
                }
            }
        }
    }

    /**
     * Gets the starting index of the word at the specified number of words away from the cursor
     * position.
     */
    public int getNthWordFromCursor(int numWords)
    {
        return this.getNthWordFromPos(numWords, getCursorPosition());
    }

    /**
     * Gets the starting index of the word at a distance of the specified number of words away from the
     * given position.
     */
    private int getNthWordFromPos(int n, int pos)
    {
        return this.getNthWordFromPosWS(n, pos, true);
    }

    /**
     * Like getNthWordFromPos (which wraps this), but adds option for skipping consecutive spaces
     */
    private int getNthWordFromPosWS(int n, int pos, boolean skipWs)
    {
        int i = pos;
        boolean flag = n < 0;
        int j = Math.abs(n);

        for (int k = 0; k < j; ++k)
        {
            if (!flag)
            {
                int l = this.text.length();
                i = this.text.indexOf(32, i);
                if (i == -1)
                {
                    i = l;
                }
                else
                {
                    while (skipWs && i < l && this.text.charAt(i) == ' ')
                    {
                        ++i;
                    }
                }
            }
            else
            {
                while (skipWs && i > 0 && this.text.charAt(i - 1) == ' ')
                {
                    --i;
                }

                while (i > 0 && this.text.charAt(i - 1) != ' ')
                {
                    --i;
                }
            }
        }

        return i;
    }

    /**
     * Moves the text cursor by a specified number of characters and clears the selection
     */
    public void moveCursorBy(int num)
    {
        this.setCursorPosition(this.func_238516_r_(num));
    }

    private int func_238516_r_(int p_238516_1_)
    {
        return Util.offsetByCodepoints(this.text, this.cursorPosition, p_238516_1_);
    }

    /**
     * Sets the current position of the cursor.
     */
    public void setCursorPosition(int pos)
    {
        this.clampCursorPosition(pos);
        if (!SwiftKeyBindings.isShiftKeyPressed() || suppressShift)
            this.setSelectionPos(this.cursorPosition);

        this.onTextChanged(this.text);
        cursorCounter = 0;
    }

    public void clampCursorPosition(int pos)
    {
        this.cursorPosition = MathHelper.clamp(pos, 0, this.text.length());
    }

    /**
     * Moves the cursor to the very start of this text box.
     */
    public void setCursorPositionZero()
    {
        this.setCursorPosition(0);
    }

    /**
     * Moves the cursor to the very end of this text box.
     */
    public void setCursorPositionEnd()
    {
        this.setCursorPosition(this.text.length());
    }

    @SuppressWarnings("resource")
    @Override
    public boolean onKeyPress(int keyCode, int scanCode, int modifiers)
    {
        if (!this.canWrite())
        {
            return false;
        }
        else
        {
            m_lastMouseClickPos = Integer.MIN_VALUE;

            Minecraft mc = Minecraft.getInstance();
            if (Screen.isSelectAll(keyCode))
            {
                this.setCursorPositionEnd();
                this.setSelectionPos(0);
                return true;
            }
            else if (Screen.isCopy(keyCode))
            {
                mc.keyboardHandler.setClipboard(this.getSelectedText());
                return true;
            }
            else if (Screen.isPaste(keyCode))
            {
                if (this.isEnabled)
                {
                    this.writeText(mc.keyboardHandler.getClipboard());
                }

                return true;
            }
            else if (Screen.isCut(keyCode))
            {
                mc.keyboardHandler.setClipboard(this.getSelectedText());
                if (this.isEnabled)
                {
                    this.writeText("");
                }

                return true;
            }
            else
            {
                switch (keyCode)
                {
                case 259:
                    if (this.isEnabled)
                    {
                        suppressShift = true;
                        delete(-1);
                        suppressShift = false;
                    }

                    return true;
                case 260:
                case 264:
                case 265:
                case 266:
                case 267:
                default:
                    // Suppress closing the window when the key bind for the inventory button is pressed
                    // while the text field has focus.
                    return getMinecraft().options.keyInventory
                            .isActiveAndMatches(InputMappings.getKey(keyCode, scanCode));
                case 261:
                    if (this.isEnabled)
                    {
                        suppressShift = true;
                        delete(1);
                        suppressShift = false;
                    }

                    return true;
                case 262:
                    if (Screen.hasControlDown())
                    {
                        this.setCursorPosition(this.getNthWordFromCursor(1));
                    }
                    else
                    {
                        this.moveCursorBy(1);
                    }

                    return true;
                case 263:
                    if (Screen.hasControlDown())
                    {
                        this.setCursorPosition(this.getNthWordFromCursor(-1));
                    }
                    else
                    {
                        this.moveCursorBy(-1);
                    }

                    return true;
                case 268:
                    this.setCursorPositionZero();
                    return true;
                case 269:
                    this.setCursorPositionEnd();
                    return true;
                }
            }
        }
    }

    public boolean canWrite()
    {
        return this.getVisible() && isFocused() && isEnabled();
    }

    @Override
    public boolean onCharTyped(char codePoint, int modifiers)
    {
        if (!canWrite())
        {
            return false;
        }
        else if (SharedConstants.isAllowedChatCharacter(codePoint))
        {
            if (isEnabled)
            {
                writeText(Character.toString(codePoint));
            }

            m_lastMouseClickPos = Integer.MIN_VALUE;

            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public boolean onMousePress(MouseButton button, double mouseX, double mouseY)
    {
        if (!visible)
        {
            return false;
        }
        else if (button == MouseButton.Left && containsMouse(mouseX, mouseY))
        {
            long timeNow = Util.getMillis();
            long elapsedMouseClickTime = timeNow - m_lastMouseClick;
            m_lastMouseClick = timeNow;

            int i = MathHelper.floor(mouseX) - leftAbsolute();
            i /= m_scale;
            i += 1;
            if (enableBackgroundDrawing)
            {
                i -= 4;
            }

            String s = m_font.plainSubstrByWidth(text.substring(lineScrollOffset), getAdjustedWidth());
            int pos = m_font.plainSubstrByWidth(s, i).length() + lineScrollOffset;

            boolean posMatches = m_lastMouseClickPos == pos;
            m_lastMouseClickPos = pos;

            if (posMatches && elapsedMouseClickTime < 300)
            {
                int j = this.cursorPosition - this.lineScrollOffset;
                int k = this.selectionEnd - this.lineScrollOffset;
                if (j != k)
                {
                    // Triple-click; highlight everything.
                    this.setCursorPositionEnd();
                    this.setSelectionPos(0);
                }
                else
                {
                    // Double-click; highlight current word.
                    setCursorPosition(getNthWordFromPosWS(1, getCursorPosition(), false));
                    setSelectionPos(getNthWordFromPosWS(-1, getCursorPosition(), false));
                }
                return true;
            }
            else
            {
                setCursorPosition(pos);
                return true;
            }
        }
        else
        {
            return false;
        }
    }

    @Override
    public void draw(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        super.draw(matrixStack, mouseX, mouseY, partialTicks);

        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        textureManager.bind(m_background);

        RenderSystem.enableDepthTest();
        blit(matrixStack, x, y, 0.0f, 0.0f, width, height, width, height);

        if (m_scale == 1.0f)
        {
            drawWorker(matrixStack, mouseX, mouseY, partialTicks);
        }
        else
        {
            final float defaultFontHeight = 8.0f;
            float inverse = 1.0f / m_scale - 1;
            float yOffset = ((1.0f - m_scale) * defaultFontHeight) / (2.0f * m_scale);
            matrixStack.pushPose();
            matrixStack.translate(2.0f, (height - defaultFontHeight) / 2.0f, 0.0f);
            matrixStack.scale(m_scale, m_scale, 1.0f);
            matrixStack.translate(x * inverse, y * inverse + yOffset, 0.0f);
            drawWorker(matrixStack, mouseX, mouseY, partialTicks);
            matrixStack.popPose();
        }
    }

    private void drawWorker(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        if (this.getEnableBackgroundDrawing())
        {
            int i = this.isFocused() ? -1 : -6250336;
            fill(matrixStack, this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, i);
            fill(matrixStack, this.x, this.y, this.x + this.width, this.y + this.height, -16777216);
        }

        int i2 = this.isEnabled ? this.enabledColor : this.disabledColor;
        int j = this.cursorPosition - this.lineScrollOffset;
        int k = this.selectionEnd - this.lineScrollOffset;
        String s = m_font.plainSubstrByWidth(this.text.substring(this.lineScrollOffset), this.getAdjustedWidth());
        boolean flag = j >= 0 && j <= s.length();
        boolean flag1 = this.isFocused() && cursorCounter / 12 % 2 == 0 && flag;
        int l = this.enableBackgroundDrawing ? this.x + 4 : this.x;
        int i1 = this.enableBackgroundDrawing ? this.y + (this.height - 8) / 2 : this.y;
        int j1 = l;
        if (k > s.length())
        {
            k = s.length();
        }

        if (!s.isEmpty())
        {
            String s1 = flag ? s.substring(0, j) : s;
            j1 = m_font.draw(matrixStack, this.textFormatter.apply(s1, this.lineScrollOffset), (float) l,
                    (float) i1, i2);
        }

        boolean flag2 = this.cursorPosition < this.text.length() || this.text.length() >= this.getMaxStringLength();
        int k1 = j1;
        if (!flag)
        {
            k1 = j > 0 ? l + this.width : l;
        }
        else if (flag2)
        {
            k1 = j1 - 1;
            --j1;
        }

        if (!s.isEmpty() && flag && j < s.length())
        {
            m_font.draw(matrixStack, this.textFormatter.apply(s.substring(j), this.cursorPosition),
                    (float) j1 + 1, (float) i1, i2);
        }

        if (!flag2 && this.suggestion != null)
        {
            m_font.drawShadow(matrixStack, this.suggestion, (float) (k1 - 1), (float) i1, -8355712);
        }

        if (flag1)
        {
            if (flag2)
            {
                AbstractGui.fill(matrixStack, k1, i1 - 1, k1 + 1, i1 + 1 + 9, -3092272);
            }
            else
            {
                m_font.drawShadow(matrixStack, "_", (float) k1, (float) i1, i2);
            }
        }

        if (k != j)
        {
            int l1 = l + m_font.width(s.substring(0, k));
            this.drawSelectionBox(matrixStack, k1, i1 - 1, l1 - 1, i1 + 1 + 9);
        }
    }

    /**
     * Draws the blue selection box.
     */
    @SuppressWarnings("deprecation")
    private void drawSelectionBox(MatrixStack matrixStack, int startX, int startY, int endX, int endY)
    {
        if (startX < endX)
        {
            int i = startX;
            startX = endX;
            endX = i;
        }

        if (startY < endY)
        {
            int j = startY;
            startY = endY;
            endY = j;
        }

        if (endX > this.x + this.width)
        {
            endX = this.x + this.width;
        }

        if (startX > this.x + this.width)
        {
            startX = this.x + this.width;
        }

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        RenderSystem.color4f(0.0F, 0.0F, 255.0F, 255.0F);
        RenderSystem.disableTexture();
        RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
        Matrix4f pose = matrixStack.last().pose();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.vertex(pose, (float) startX, (float) endY, 0.0f).endVertex();
        bufferbuilder.vertex(pose, (float) endX, (float) endY, 0.0f).endVertex();
        bufferbuilder.vertex(pose, (float) endX, (float) startY, 0.0f).endVertex();
        bufferbuilder.vertex(pose, (float) startX, (float) startY, 0.0f).endVertex();
        tessellator.end();
        RenderSystem.disableColorLogicOp();
        RenderSystem.enableTexture();
    }

    /**
     * Sets the maximum length for the text in this text box. If the current text is longer than this
     * length, the current text will be trimmed.
     */
    public void setMaxStringLength(int length)
    {
        this.maxStringLength = length;
        if (this.text.length() > length)
        {
            this.text = this.text.substring(0, length);
            this.onTextChanged(this.text);
        }

    }

    /**
     * returns the maximum number of character that can be contained in this textbox
     */
    private int getMaxStringLength()
    {
        return this.maxStringLength;
    }

    /**
     * returns the current position of the cursor
     */
    public int getCursorPosition()
    {
        return this.cursorPosition;
    }

    /**
     * Gets whether the background and outline of this text box should be drawn (true if so).
     */
    private boolean getEnableBackgroundDrawing()
    {
        return this.enableBackgroundDrawing;
    }

    /**
     * Sets whether or not the background and outline of this text box should be drawn.
     */
    public void setEnableBackgroundDrawing(boolean enableBackgroundDrawingIn)
    {
        this.enableBackgroundDrawing = enableBackgroundDrawingIn;
    }

    /**
     * Sets the color to use when drawing this text box's text. A different color is used if this text
     * box is disabled.
     */
    public void setTextColor(int color)
    {
        this.enabledColor = color;
    }

    /**
     * Sets the color to use for text in this text box when this text box is disabled.
     */
    public void setDisabledTextColour(int color)
    {
        this.disabledColor = color;
    }

    public boolean changeFocus(boolean focus)
    {
        return this.visible && this.isEnabled ? super.changeFocus(focus) : false;
    }

    public boolean isMouseOver(double mouseX, double mouseY)
    {
        return this.visible && mouseX >= (double) this.x && mouseX < (double) (this.x + this.width)
                && mouseY >= (double) this.y && mouseY < (double) (this.y + this.height);
    }

    protected void onFocusedChanged(boolean focused)
    {
        if (focused)
        {
            cursorCounter = 0;
        }

    }

    private boolean isEnabled()
    {
        return this.isEnabled;
    }

    /**
     * Sets whether this text box is enabled. Disabled text boxes cannot be typed in.
     */
    public void setEnabled(boolean enabled)
    {
        this.isEnabled = enabled;
    }

    /**
     * returns the width of the textbox depending on if background drawing is enabled
     */
    public int getAdjustedWidth()
    {
        return this.getEnableBackgroundDrawing() ? this.width - 8 : this.width;
    }

    /**
     * Sets the position of the selection anchor (the selection anchor and the cursor position mark the
     * edges of the selection). If the anchor is set beyond the bounds of the current text, it will be
     * put back inside.
     */
    public void setSelectionPos(int position)
    {
        int i = this.text.length();
        this.selectionEnd = MathHelper.clamp(position, 0, i);
        if (m_font != null)
        {
            if (this.lineScrollOffset > i)
            {
                this.lineScrollOffset = i;
            }

            int j = this.getAdjustedWidth();
            String s = m_font.plainSubstrByWidth(this.text.substring(this.lineScrollOffset), j);
            int k = s.length() + this.lineScrollOffset;
            if (this.selectionEnd == this.lineScrollOffset)
            {
                this.lineScrollOffset -= m_font.plainSubstrByWidth(this.text, j, true).length();
            }

            if (this.selectionEnd > k)
            {
                this.lineScrollOffset += this.selectionEnd - k;
            }
            else if (this.selectionEnd <= this.lineScrollOffset)
            {
                this.lineScrollOffset -= this.lineScrollOffset - this.selectionEnd;
            }

            this.lineScrollOffset = MathHelper.clamp(this.lineScrollOffset, 0, i);
        }
    }

    /**
     * returns true if this textbox is visible
     */
    public boolean getVisible()
    {
        return this.visible;
    }

    /**
     * Sets whether or not this textbox is visible
     */
    public void setVisible(boolean isVisible)
    {
        this.visible = isVisible;
    }

    public void setSuggestion(@Nullable String p_195612_1_)
    {
        this.suggestion = p_195612_1_;
    }

    public int func_195611_j(int p_195611_1_)
    {
        return p_195611_1_ > this.text.length() ? this.x
                : this.x + m_font.width(this.text.substring(0, p_195611_1_));
    }

    public void setX(int xIn)
    {
        this.x = xIn;
    }

    public void setScale(float scale)
    {
        m_scale = scale;
    }

    public float getScale()
    {
        return m_scale;
    }

    public int getHeight()
    {
        return height;
    }

    public void clearText()
    {
        setText("");
    }

    protected static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Swift.MOD_NAME,
            "textures/gui/inner_screen.png");

    private float m_scale;
    private ResourceLocation m_background;
    private long m_lastMouseClick;
    private int m_lastMouseClickPos;
}
