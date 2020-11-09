package me.finz0.blackout.gui.button.sub;

import com.google.common.collect.Lists;
import me.finz0.blackout.gui.ClickGUI;
import me.finz0.blackout.gui.button.ModuleButton;
import me.finz0.blackout.gui.button.SubButton;
import me.finz0.blackout.setting.ColorValue;
import me.finz0.blackout.setting.Setting;
import me.finz0.blackout.util.Wrapper;
import net.minecraft.client.gui.Gui;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class ColorButton extends SubButton {

    public ColorButton(Setting<ColorValue> setting, int x, int y, ModuleButton parent) {
        super(x, y, parent.getWidth() - 2, Wrapper.getFontRenderer().FONT_HEIGHT + 4, parent);
        this.setting = setting;
        typing = false;
    }

    private final Setting<ColorValue> setting;

    private boolean typing;

    @Override
    public boolean isVisible(){
        return setting.isVisible();
    }

    @Override
    public void guiClosed(){
        typing = false;
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        boolean dark = darkColor();
        Gui.drawRect(getX(), getY(), getX() + 2, getY() + getHeight(), new Color(10, 10, 10, 200).getRGB());
        Gui.drawRect(getX() + 2, getY(), getX() + 2 + getWidth(), getY() + getHeight() - 1, getColor(mouseX, mouseY, dark).getRGB());
        Gui.drawRect(getX() + 2, getY() + getHeight() - 1, getX() + 2 + getWidth(), getY() + getHeight(), new Color(10, 10, 10, 200).getRGB());

        int color = dark ? 0 : -1;

        Wrapper.getFontRenderer().drawString(setting.getName(), getX() + 4, getY() + 2, color);

        String val = Integer.toHexString(setting.getValue().getHex());
        if(typing) val = input.isEmpty() ? "_" : input;
        Wrapper.getFontRenderer().drawString(val, getX() + getWidth() - Wrapper.getFontRenderer().getStringWidth(val), getY() + 2, color);
    }

    private Color getColor(int mouseX, int mouseY, boolean dark) {
        Color color = setting.getValue().getColor(200);
        boolean hovered = mouseX > getX() + 2 && mouseY > getY() && mouseX < getX() + 2 + getWidth() && mouseY < getY() + getHeight() - 1;

        return hovered ? (dark ? color.darker() : color.brighter()) : color;
    }

    private boolean darkColor(){
        Color color = setting.getValue().getColor();
        double y = (299 * color.getRed() + 587 * color.getGreen() + 114 * color.getBlue()) / 1000f;

        return y >= 128;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        if (mouseX > getX() + 2 && mouseY > getY() && mouseX < getX() + 2 + getWidth() && mouseY < getY() + getHeight() - 1){
            typing = !typing;
            ClickGUI.colorTyping = typing;
        }
    }

    private ArrayList<Character> validChars = Lists.newArrayList(
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    );

    private String input = "";

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if(!typing){
            input = "";
            return;
        }

        switch (keyCode){
            case Keyboard.KEY_ESCAPE:
                typing = false;
                ClickGUI.colorTyping = false;
                input = "";
                return;
            case Keyboard.KEY_RETURN:
            case Keyboard.KEY_NUMPADENTER:
                if(input.length() == 6 || input.length() == 8)
                    valueEntered();
                typing = false;
                ClickGUI.colorTyping = false;
                input = "";
                return;
            case Keyboard.KEY_BACK:
            case Keyboard.KEY_DELETE:
                input = StringUtils.chop(input);
                return;
        }

        if(validChars.contains(typedChar)){
            if(input.length() == 8) return;
            input += typedChar;
        }
    }

    private void valueEntered(){
        try {
            setting.setValue(new ColorValue(Integer.parseInt(input, 16)));
        } catch (NumberFormatException ignored){
        }
    }

}
