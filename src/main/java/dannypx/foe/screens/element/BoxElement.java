package dannypx.foe.screens.element;

import dannypx.foe.FishOnMCExtras;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BoxElement extends Element {
    //region Fields
    private final MinecraftClient minecraftClient;
    private final TextRenderer textRenderer;

    private final Identifier BOX_TEXTURE = Identifier.of(FishOnMCExtras.MOD_ID, "textures/gui/sprites/elements/box_atlas.png");
    private final Identifier BOX_SOLID_TEXTURE = Identifier.of(FishOnMCExtras.MOD_ID, "textures/gui/sprites/elements/box_solid_atlas.png");
    private final Identifier BOX_ALT_TEXTURE = Identifier.of(FishOnMCExtras.MOD_ID, "textures/gui/sprites/elements/box_alt_atlas.png");
    private final Identifier BOX_SOLID_ALT_TEXTURE = Identifier.of(FishOnMCExtras.MOD_ID, "textures/gui/sprites/elements/box_solid_alt_atlas.png");

    private final boolean isSolid;
    private final boolean isAlt;
    private final int x;
    private final int y;
    private final int z;

    private final boolean showTop;
    private final boolean showRight;
    private final boolean showBottom;
    private final boolean showLeft;
    //endregion

    public BoxElement(MinecraftClient minecraftClient, int x, int y, int width, int height, boolean isSolid, boolean isAlt) {
        super(width,
                height,
                x,
                y,
                null,
                Text.literal("Box Element"),
                false);
        this.minecraftClient = minecraftClient;
        this.textRenderer = minecraftClient.textRenderer;
        this.isSolid = isSolid;
        this.isAlt = isAlt;
        this.x = x;
        this.y = y;
        this.z = -1;
        this.showTop = true;
        this.showRight = true;
        this.showBottom = true;
        this.showLeft = true;
    }

    public BoxElement(MinecraftClient minecraftClient, int x, int y, int z, int width, int height, boolean isSolid, boolean isAlt) {
        super(width,
                height,
                x,
                y,
                null,
                Text.literal("Box Element"),
                false);
        this.minecraftClient = minecraftClient;
        this.textRenderer = minecraftClient.textRenderer;
        this.isSolid = isSolid;
        this.isAlt = isAlt;
        this.x = x;
        this.y = y;
        this.z = z;
        this.showTop = true;
        this.showRight = true;
        this.showBottom = true;
        this.showLeft = true;
    }

    public BoxElement(MinecraftClient minecraftClient, int x, int y, int z, int width, int height, boolean isSolid, boolean isAlt, boolean showTop, boolean showRight, boolean showBottom, boolean showLeft) {
        super(width,
                height,
                x,
                y,
                null,
                Text.literal("Box Element"),
                false);
        this.minecraftClient = minecraftClient;
        this.textRenderer = minecraftClient.textRenderer;
        this.isSolid = isSolid;
        this.isAlt = isAlt;
        this.x = x;
        this.y = y;
        this.z = z;
        this.showTop = showTop;
        this.showRight = showRight;
        this.showBottom = showBottom;
        this.showLeft = showLeft;
    }

    //region Methods
    @Override
    public void render(DrawContext drawContext, RenderTickCounter tickCounter) {
        this.renderBox(drawContext);
    }

    private void renderBox(DrawContext drawContext) {
        Identifier TEXTURE = isAlt ? (isSolid ? BOX_SOLID_ALT_TEXTURE : BOX_ALT_TEXTURE) : (isSolid ? BOX_SOLID_TEXTURE : BOX_TEXTURE);

        // Top Left
        if(showTop && showLeft) drawContext.drawTexture(RenderPipelines.GUI_TEXTURED,
                TEXTURE,
                this.x, this.y,
                0, 0,
                5, 5,
                5, 5,
                15,15
        );

        // Top
        if(showTop) drawContext.drawTexture(RenderPipelines.GUI_TEXTURED,
                TEXTURE,
                this.x + 5, this.y,
                5, 0,
                this.width - 10, 5,
                5, 5,
                15, 15
        );

        // Top Right
        if(showTop && showRight) drawContext.drawTexture(RenderPipelines.GUI_TEXTURED,
                TEXTURE,
                x + this.width - 5, this.y,
                10, 0,
                5, 5,
                5, 5,
                15, 15
        );

        // Centre Left
        if(showLeft) drawContext.drawTexture(RenderPipelines.GUI_TEXTURED,
                TEXTURE,
                this.x, this.y + 5,
                0, 5,
                5, this.height - 10,
                5, 5,
                15,15
        );

        // Centre
        drawContext.drawTexture(RenderPipelines.GUI_TEXTURED,
                TEXTURE,
                this.x + 5, this.y + 5,
                5, 5,
                this.width - 10, this.height - 10,
                5, 5,
                15, 15
        );

        // Centre Right
        if(showRight) drawContext.drawTexture(RenderPipelines.GUI_TEXTURED,
                TEXTURE,
                x + this.width - 5, this.y + 5,
                10, 5,
                5, this.height - 10,
                5, 5,
                15, 15
        );

        // Bottom Left
        if(showLeft && showBottom) drawContext.drawTexture(RenderPipelines.GUI_TEXTURED,
                TEXTURE,
                this.x, y + this.height - 5,
                0, 10,
                5, 5,
                5, 5,
                15,15
        );

        // Bottom
        if(showBottom) drawContext.drawTexture(RenderPipelines.GUI_TEXTURED,
                TEXTURE,
                this.x + 5, y + this.height - 5,
                5, 10,
                this.width - 10, 5,
                5, 5,
                15, 15
        );

        // Bottom Right
        if(showBottom && showRight) drawContext.drawTexture(RenderPipelines.GUI_TEXTURED,
                TEXTURE,
                x + this.width - 5, y + this.height - 5,
                10, 10,
                5, 5,
                5, 5,
                15, 15
        );
    }
    //endregion
}
