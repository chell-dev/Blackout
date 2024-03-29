package me.finz0.blackout.gui.button.sub;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.finz0.blackout.gui.ClickGUI;
import me.finz0.blackout.gui.button.ModuleButton;
import me.finz0.blackout.gui.button.SubButton;
import me.finz0.blackout.setting.Bind;
import me.finz0.blackout.setting.Setting;
import me.finz0.blackout.util.Wrapper;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;

public class BindButton extends SubButton {
    public BindButton(Setting<Bind> setting, int x, int y, ModuleButton parent) {
        super(x, y, parent.getWidth() - 2, Wrapper.getFontRenderer().FONT_HEIGHT + 4, parent);
        this.setting = setting;
    }

    private Setting<Bind> setting;

    private boolean listening = false;

    @Override
    public void draw(int mouseX, int mouseY) {
        Gui.drawRect(getX(), getY(), getX() + 2, getY() + getHeight(), new Color(10, 10, 10, 200).getRGB());
        Gui.drawRect(getX() + 2, getY(), getX() + 2 + getWidth(), getY() + getHeight() - 1, getColor(mouseX, mouseY));
        Gui.drawRect(getX() + 2, getY() + getHeight() - 1, getX() + 2 + getWidth(), getY() + getHeight(), new Color(10, 10, 10, 200).getRGB());
        Wrapper.getFontRenderer().drawStringWithShadow("Bind", getX() + 4, getY() + 2, -1);
        String val = "" + ChatFormatting.GRAY + (listening ? "Listening" : setting.getValue().getKeyName());
        Wrapper.getFontRenderer().drawStringWithShadow(val, getX() + getWidth() - Wrapper.getFontRenderer().getStringWidth(val), getY() + 2, -1);
    }

    private int getColor(int mouseX, int mouseY){
        Color color = new Color(50, 50, 50, 200);
        boolean hovered = mouseX > getX() + 2 && mouseY > getY() && mouseX < getX() + 2 + getWidth() && mouseY < getY() + getHeight() - 1;
        return hovered ? color.brighter().brighter().getRGB() : color.getRGB();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        if(mouseX > getX() + 2 && mouseY > getY() && mouseX < getX() + 2 + getWidth() && mouseY < getY() + getHeight() - 1 && button == 0){
            listening = !listening;
            ClickGUI.bindListening = listening;
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if(listening){
            if(keyCode != 0 && keyCode != Keyboard.KEY_ESCAPE){
                if(keyCode == Keyboard.KEY_DELETE) setting.getValue().setKey(0);
                else setting.setValue(new Bind(keyCode));
            }
            listening = false;
        }
    }

    @Override
    public void guiClosed(){
        listening = false;
    }
}
