package com.lothrazar.cyclicmagic.gui.vector;
import java.io.IOException;
import java.util.ArrayList;
import com.lothrazar.cyclicmagic.ModCyclic;
import com.lothrazar.cyclicmagic.block.tileentity.TileVector;
import com.lothrazar.cyclicmagic.block.tileentity.TileVector.Fields;
import com.lothrazar.cyclicmagic.gui.GuiBaseContainer;
import com.lothrazar.cyclicmagic.net.PacketTileVector;
import com.lothrazar.cyclicmagic.util.UtilChat;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiVector extends GuiBaseContainer {
  private static final int SOUTH = 0;
  private static final int NORTH = 180;
  private static final int EAST = 270;
  private static final int WEST = 90;
  private TileVector tile;
  private int xAngle = 10;
  private int yAngle = 38;
  private int xPower = 60;
  private int yPower = yAngle;
  private int xYaw = 118;
  private int yYaw = yAngle;
  private ArrayList<GuiTextFieldInteger> txtBoxes = new ArrayList<GuiTextFieldInteger>();
  public GuiVector(InventoryPlayer inventoryPlayer, TileVector tileEntity) {
    super(new ContainerVector(inventoryPlayer, tileEntity), tileEntity);
    tile = tileEntity;
  }
  public String getTitle() {
    return "tile.plate_vector.name";
  }
  @Override
  public void initGui() {
    super.initGui();
    int id = 1;
    //angle text box
    GuiTextFieldInteger txtAngle = addTextbox(id++, xAngle, yAngle, tile.getAngle() + "", 2);
    txtAngle.setFocused(true);//default
    txtAngle.setMaxVal(TileVector.MAX_ANGLE);
    txtAngle.setMinVal(0);
    txtAngle.setTileFieldId(TileVector.Fields.ANGLE.ordinal());
    //then the power text box
    GuiTextFieldInteger txtPower = addTextbox(id++, xPower, yPower, tile.getPower() + "", 2);
    txtPower.setMaxVal(TileVector.MAX_POWER);
    txtPower.setMinVal(1);
    txtPower.setTileFieldId(TileVector.Fields.POWER.ordinal());
    // yaw text box
    GuiTextFieldInteger txtYaw = addTextbox(id++, xYaw, yYaw, tile.getYaw() + "", 3);
    txtYaw.setMaxVal(TileVector.MAX_YAW);
    txtYaw.setMinVal(0);
    txtYaw.setTileFieldId(TileVector.Fields.YAW.ordinal());
    //now the YAW buttons
    int btnYawSpacing = 22;
    addButtonAt(id++, xYaw + 5, yYaw + btnYawSpacing, SOUTH, Fields.YAW.ordinal()).displayString = "S";
    addButtonAt(id++, xYaw + 5, yYaw - btnYawSpacing, NORTH, Fields.YAW.ordinal()).displayString = "N";
    addButtonAt(id++, xYaw + btnYawSpacing + 10, yYaw, EAST, Fields.YAW.ordinal()).displayString = "E";
    addButtonAt(id++, xYaw - btnYawSpacing, yYaw, WEST, Fields.YAW.ordinal()).displayString = "W";
    addButtonAt(id++, xYaw + btnYawSpacing + 10, yYaw - btnYawSpacing, (NORTH + EAST) / 2, Fields.YAW.ordinal()).displayString = "NE";
    addButtonAt(id++, xYaw - btnYawSpacing, yYaw - btnYawSpacing, (NORTH + WEST) / 2, Fields.YAW.ordinal()).displayString = "NW";
    addButtonAt(id++, xYaw + btnYawSpacing + 10, yYaw + btnYawSpacing, (360 + EAST) / 2, Fields.YAW.ordinal()).displayString = "SE";
    addButtonAt(id++, xYaw - btnYawSpacing, yYaw + btnYawSpacing, (SOUTH + WEST) / 2, Fields.YAW.ordinal()).displayString = "SW";
    //angle buttons
    //    addButtonAt(id++, xAngle, yAngle - btnYawSpacing, 90, Fields.ANGLE.ordinal());
    //    addButtonAt(id++, xAngle, yAngle + btnYawSpacing, 0, Fields.ANGLE.ordinal());
  }
  @Override
  protected void actionPerformed(GuiButton button) throws IOException {
    super.actionPerformed(button);
    if (button instanceof ButtonVector) {
      ButtonVector btn = (ButtonVector) button;
      for (GuiTextFieldInteger txt : txtBoxes) { //push value to the matching textbox
        if (txt.getTileFieldId() == btn.getFieldId()) {
          txt.setText(btn.getValue() + "");
        }
      }
    }
  }
  private GuiTextFieldInteger addTextbox(int id, int x, int y, String text, int maxLen) {
    int width = 10 * maxLen, height = 20;
    GuiTextFieldInteger txt = new GuiTextFieldInteger(id, this.fontRendererObj, x, y, width, height);
    txt.setMaxStringLength(maxLen);
    txt.setText(text);
    txtBoxes.add(txt);
    return txt;
  }
  private ButtonVector addButtonAt(int id, int x, int y, int val, int f) {
    ButtonVector btn = new ButtonVector(tile.getPos(), id,
        this.guiLeft + x,
        this.guiTop + y,
        val, f);
    this.buttonList.add(btn);
    return btn;
  }
  @SideOnly(Side.CLIENT)
  @Override
  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    for (GuiTextField txt : txtBoxes) {
      if (txt != null) {
        txt.drawTextBox();
      }
    }
    renderString("tile.plate_vector.gui.power", xPower + 8, yPower - 12);
    renderString("tile.plate_vector.gui.angle", xAngle + 8, yAngle - 12);
    super.drawGuiContainerForegroundLayer(mouseX, mouseY);
  }
  private void renderString(String s, int x, int y) {
    String str = UtilChat.lang(s);
    int strWidth = this.fontRendererObj.getStringWidth(str);
    this.fontRendererObj.drawString(str, x - strWidth / 2, y, 4210752);
  }
  @Override
  public void updateScreen() { // http://www.minecraftforge.net/forum/index.php?topic=22378.0
    super.updateScreen();
    for (GuiTextField txt : txtBoxes) {
      if (txt != null) {
        txt.updateCursorCounter();
      }
    }
  }
  @Override
  protected void keyTyped(char pchar, int keyCode) throws IOException {
    super.keyTyped(pchar, keyCode);
    for (GuiTextFieldInteger txt : txtBoxes) {
      String oldval = txt.getText();
      txt.textboxKeyTyped(pchar, keyCode);
      String newval = txt.getText();
      boolean yes = false;
      try {
        int val = Integer.parseInt(newval);
        if (val <= txt.getMaxVal() && val >= txt.getMinVal()) {
          yes = true;
          //also set it clientisde to hopefully prevent desycn
          tile.setField(txt.getTileFieldId(), val);
          ModCyclic.network.sendToServer(new PacketTileVector(tile.getPos(), val, txt.getTileFieldId()));
        }
      }
      catch (NumberFormatException e) {}
      if (!yes && !newval.isEmpty()) {//allow empty string in case user is in middle of deleting all and retyping
        txt.setText(oldval);//rollback to the last valid value. ex if they type 'abc' revert to valid 
      }
    }
  }
  @Override
  protected void mouseClicked(int mouseX, int mouseY, int btn) throws IOException {
    super.mouseClicked(mouseX, mouseY, btn);// x/y pos is 33/30
    for (GuiTextField txt : txtBoxes) {
      txt.mouseClicked(mouseX, mouseY, btn);
      if (btn == 0) {//basically left click
        boolean flag = mouseX >= this.guiLeft + txt.xPosition && mouseX < this.guiLeft + txt.xPosition + txt.width
            && mouseY >= this.guiTop + txt.yPosition && mouseY < this.guiTop + txt.yPosition + txt.height;
        txt.setFocused(flag);
      }
    }
  }
}
