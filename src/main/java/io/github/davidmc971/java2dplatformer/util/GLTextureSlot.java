package io.github.davidmc971.java2dplatformer.util;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public enum GLTextureSlot {
    SLOT0(0, GL13.GL_TEXTURE0),
    SLOT1(1, GL13.GL_TEXTURE1),
    SLOT2(2, GL13.GL_TEXTURE2),
    SLOT3(3, GL13.GL_TEXTURE3),
    SLOT4(4, GL13.GL_TEXTURE4),
    SLOT5(5, GL13.GL_TEXTURE5),
    SLOT6(6, GL13.GL_TEXTURE6),
    SLOT7(7, GL13.GL_TEXTURE7),
    SLOT8(8, GL13.GL_TEXTURE8),
    SLOT9(9, GL13.GL_TEXTURE9),
    SLOT10(10, GL13.GL_TEXTURE10),
    SLOT11(11, GL13.GL_TEXTURE11),
    SLOT12(12, GL13.GL_TEXTURE12),
    SLOT13(13, GL13.GL_TEXTURE13),
    SLOT14(14, GL13.GL_TEXTURE14),
    SLOT15(15, GL13.GL_TEXTURE15),
    SLOT16(16, GL13.GL_TEXTURE16),
    SLOT17(17, GL13.GL_TEXTURE17),
    SLOT18(18, GL13.GL_TEXTURE18),
    SLOT19(19, GL13.GL_TEXTURE19),
    SLOT20(20, GL13.GL_TEXTURE20),
    SLOT21(21, GL13.GL_TEXTURE21),
    SLOT22(22, GL13.GL_TEXTURE22),
    SLOT23(23, GL13.GL_TEXTURE23),
    SLOT24(24, GL13.GL_TEXTURE24),
    SLOT25(25, GL13.GL_TEXTURE25),
    SLOT26(26, GL13.GL_TEXTURE26),
    SLOT27(27, GL13.GL_TEXTURE27),
    SLOT28(28, GL13.GL_TEXTURE28),
    SLOT29(29, GL13.GL_TEXTURE29),
    SLOT30(30, GL13.GL_TEXTURE30),
    SLOT31(31, GL13.GL_TEXTURE31);

    public final int slotNumber;
    public final int glTextureSlot;

    private GLTextureSlot(int slotNumber, int glTextureSlot) {
        this.slotNumber = slotNumber;
        this.glTextureSlot = glTextureSlot;
    }

    private static int maxSlots = -1;

    public static int getMaxTextureSlots() {
        if (maxSlots != -1)
            return maxSlots;
        maxSlots = GL11.glGetInteger(GL13.GL_MAX_TEXTURE_UNITS);
        return maxSlots;
    }

    private static int[] textureSlotNumberArray = null;

    public static int[] getMaxTextureSlotsNumberArray() {
        if (textureSlotNumberArray != null)
            return textureSlotNumberArray;
        textureSlotNumberArray = new int[getMaxTextureSlots()];
        for (int i = 0; i < textureSlotNumberArray.length; i++) {
            textureSlotNumberArray[i] = i;
        }
        return textureSlotNumberArray;
    }

    public static GLTextureSlot get(int i) {
        assert i < getMaxTextureSlots()
                : "Trying to request texture slot not available on platform, max number is " + getMaxTextureSlots();
        return GLTextureSlot.values()[i];
    }
}
