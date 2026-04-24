package dannypx.foe.entity;

import dannypx.foe.FishOnMCExtras;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.FishingHookRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class FishingHookEntityModel<T extends FishingHookRenderState> extends EntityModel<T> {
    public static final ModelLayerLocation MODEL_LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath(FishOnMCExtras.MOD_ID, "fishing_hook"), "main");
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(FishOnMCExtras.MOD_ID, "textures/entity/fishing_hook.png");

    public static final RenderType RENDER_LAYER = RenderTypes.entityTranslucent(TEXTURE);

    private static final float ANGLE_180_DEGREES = (float) (1f * Math.PI);

    public FishingHookEntityModel(@NotNull ModelPart root) {
        super(root);
    }

    public static LayerDefinition generateModel() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        partDefinition.addOrReplaceChild("top", CubeListBuilder.create().texOffs(12, 0).addBox(-.5f, 3f, -.5f, 1f, 1f, 1f), PartPose.ZERO);
        partDefinition.addOrReplaceChild("fishing_hook", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5f, 0f, -1.5f, 3f, 3f, 3f), PartPose.offsetAndRotation(0f, 3f, 0f, ANGLE_180_DEGREES, 0f, 0f));
        partDefinition.addOrReplaceChild("hook1", CubeListBuilder.create().texOffs(12, 0).addBox(-.5f, -1f, -.5f, 1f, 1f, 1f), PartPose.ZERO);
        partDefinition.addOrReplaceChild("hook2", CubeListBuilder.create().texOffs(12, 0).addBox(-.5f, -2f, -.5f, 1f, 1f, 1f), PartPose.ZERO);
        partDefinition.addOrReplaceChild("hook3", CubeListBuilder.create().texOffs(12, 0).addBox(-.5f, -3f, -.5f, 1f, 1f, 1f), PartPose.ZERO);
        partDefinition.addOrReplaceChild("hook4", CubeListBuilder.create().texOffs(12, 0).addBox(-1.5f, -3f, -.5f, 1f, 1f, 1f), PartPose.ZERO);
        partDefinition.addOrReplaceChild("hook5", CubeListBuilder.create().texOffs(12, 0).addBox(-2.5f, -2f, -.5f, 1f, 1f, 1f), PartPose.ZERO);

        return LayerDefinition.create(meshDefinition, 16, 9);
    }

    @Override
    public void setupAnim(T object) {
    }


}
