package mods.flammpfeil.slashblade.client.model;

import mods.flammpfeil.slashblade.ItemSlashBladeWrapper;
import mods.flammpfeil.slashblade.client.util.LightSetup;
import mods.flammpfeil.slashblade.client.model.obj.Face;
import mods.flammpfeil.slashblade.client.model.obj.WavefrontObject;
import mods.flammpfeil.slashblade.client.renderer.entity.BladeFirstPersonRender;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.util.ResourceLocationRaw;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Color4f;
import java.awt.*;
import java.util.EnumSet;

/**
 * Created by Furia on 2016/06/21.
 */
public class BladeSpecialRender extends TileEntityItemStackRenderer {
    private static final ResourceLocationRaw RES_ITEM_GLINT = new ResourceLocationRaw("textures/misc/enchanted_item_glint.png");

    private void bindTexture(ResourceLocation res){
        Minecraft.getInstance().getTextureManager().bindTexture(res);
    }

    @Override
    public void renderByItem(ItemStack itemStackIn) {
        if(!(itemStackIn.getItem() instanceof ItemSlashBlade)) return;
        ItemSlashBlade item = (ItemSlashBlade)itemStackIn.getItem();

        ResourceLocationRaw resourceTexture = BladeModel.itemBlade.getModelTexture(BladeModel.targetStack);
        bindTexture(resourceTexture);

        if(render() && itemStackIn.hasEffect()){
            renderEffect();
        }
    }

    private void renderEffect()
    {
        GlStateManager.depthMask(false);
        GlStateManager.depthFunc(514);
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
        bindTexture(RES_ITEM_GLINT);
        GlStateManager.matrixMode(5890);
        GlStateManager.pushMatrix();
        GlStateManager.scalef(8.0F, 8.0F, 8.0F);
        float f = (float)(Util.milliTime() % 3000L) / 3000.0F / 8.0F;
        GlStateManager.translatef(f, 0.0F, 0.0F);
        GlStateManager.rotatef(-50.0F, 0.0F, 0.0F, 1.0F);
        this.render();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.scalef(8.0F, 8.0F, 8.0F);
        float f1 = (float)(Util.milliTime() % 4873L) / 4873.0F / 8.0F;
        GlStateManager.translatef(-f1, 0.0F, 0.0F);
        GlStateManager.rotatef(10.0F, 0.0F, 0.0F, 1.0F);
        this.render();
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableLighting();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
    }

    boolean checkRenderNaked(){
        ItemStack mainHand = BladeModel.user.getHeldItemMainhand();
        if(!(mainHand.getItem() instanceof ItemSlashBlade))
            return true;

        if(ItemSlashBlade.hasScabbardInOffhand(BladeModel.user))
            return true;

        EnumSet<ItemSlashBlade.SwordType> type = BladeModel.itemBlade.getSwordType(mainHand);
        if(type.contains(ItemSlashBlade.SwordType.NoScabbard))
            return true;

        return false;
    }

    private boolean render(){

        boolean depthState = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
        if(!depthState)
            GlStateManager.enableDepthTest();

        if(BladeModel.type == ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND
                || BladeModel.type == ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND
                || BladeModel.type == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND
                || BladeModel.type == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND
                || BladeModel.type == ItemCameraTransforms.TransformType.NONE) {

            if(BladeModel.user == null)
                return false;

            EnumSet<ItemSlashBlade.SwordType> types = BladeModel.itemBlade.getSwordType(BladeModel.targetStack);

            boolean handle = false;

            if(!types.contains(ItemSlashBlade.SwordType.NoScabbard)) {
                handle = BladeModel.user.getPrimaryHand() == EnumHandSide.RIGHT ?
                        BladeModel.type == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND :
                        BladeModel.type == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND;
            }


            if(BladeModel.type == ItemCameraTransforms.TransformType.NONE) {
                if(checkRenderNaked()){
                    renderNaked(true);
                }
                else if(BladeModel.targetStack == BladeModel.user.getHeldItemMainhand()){
                    BladeFirstPersonRender.getInstance().renderVR();
                }
            }else {
                if(checkRenderNaked()){
                    renderNaked();
                }else if(BladeModel.targetStack == BladeModel.user.getHeldItemMainhand()){
                    BladeFirstPersonRender.getInstance().render();
                }
            }

            return false;
        }

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        //GL11.glPushClientAttrib(GL11.GL_ALL_ATTRIB_BITS);

        if(BladeModel.renderPath++ >= 1) {
            Face.setColor(0xFF8040CC);
            GL11.glMatrixMode(GL11.GL_TEXTURE);
            GlStateManager.scalef(0.1F, 0.1F, 0.1F);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
        }else{
            Face.resetColor();

            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            GL11.glDisable(GL11.GL_CULL_FACE);


            GlStateManager.disableLighting(); //Forge: Make sure that render states are reset, ad renderEffect can derp them up.
            GL11.glEnable(GL11.GL_ALPHA_TEST);

            GL11.glAlphaFunc(GL11.GL_GEQUAL, 0.05f);
        }

        GL11.glPushMatrix();

        GL11.glTranslatef(0.5f, 0.5f, 0.5f);

        float scale = 0.0095f;
        if(BladeModel.type == ItemCameraTransforms.TransformType.GUI)
            scale = 0.008f;
        GL11.glScalef(scale, scale, scale);

        EnumSet<ItemSlashBlade.SwordType> types = BladeModel.itemBlade.getSwordType(BladeModel.targetStack);
        WavefrontObject model = BladeModelManager.getInstance().getModel(BladeModel.itemBlade.getModelLocation(BladeModel.targetStack));

        String renderTarget;
        if(types.contains(ItemSlashBlade.SwordType.Broken))
            renderTarget = "item_damaged";
        else if(!types.contains(ItemSlashBlade.SwordType.NoScabbard)){
            renderTarget = "item_blade";
        }else{
            renderTarget = "item_bladens";
        }

        model.renderPart(renderTarget);


        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE, GL11.GL_ZERO);

        //RenderHelper.enableStandardItemLighting();
        try(LightSetup ls = LightSetup.setup()){
            model.renderPart(renderTarget + "_luminous");
        }

        OpenGlHelper.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

        if(BladeModel.renderPath == 1 && BladeModel.type == ItemCameraTransforms.TransformType.GUI){
            model = BladeModelManager.getInstance().getModel(BladeModelManager.resourceDurabilityModel);
            bindTexture(BladeModelManager.resourceDurabilityTexture);

            double par = BladeModel.itemBlade.getDurabilityForDisplay(BladeModel.targetStack);
            par = Math.min(Math.max(par, 0.0),1.0);

            GlStateManager.translatef(0.0F, 0.0F, 0.1f);

            Color4f aCol = new Color4f(new Color(0.25f,0.25f,0.25f,1.0f));
            Color4f bCol = new Color4f(new Color(0xA52C63));
            aCol.interpolate(bCol,(float)par);

            Face.setColor(aCol.get().getRGB());
            model.renderPart("base");
            Face.resetColor();

            boolean isBroken = types.contains(ItemSlashBlade.SwordType.Broken);

            if(isBroken){
                GL11.glMatrixMode(GL11.GL_TEXTURE);
                GlStateManager.translatef(0.0F, 0.5F, 0.0f);
                GL11.glMatrixMode(GL11.GL_MODELVIEW);
            }

            GlStateManager.translated(0.0F, 0.0F, -2.0f * BladeModel.itemBlade.getDurabilityForDisplay(BladeModel.targetStack));
            model.renderPart("color");

            if(isBroken){
                GL11.glMatrixMode(GL11.GL_TEXTURE);
                GlStateManager.loadIdentity();
                GL11.glMatrixMode(GL11.GL_MODELVIEW);
            }
        }

        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GlStateManager.enableLighting();

        GL11.glEnable(GL11.GL_CULL_FACE);


        GL11.glPopMatrix();
        //GL11.glPopClientAttrib();
        GL11.glPopAttrib();

        Face.resetColor();

        if(!depthState)
            GlStateManager.disableDepthTest();

        return true;
    }

    private void renderNaked(){
        renderNaked(false);
    }
    private void renderNaked(boolean isVR){
        EntityLivingBase entitylivingbaseIn = BladeModel.user ;
        ItemStack itemstack = BladeModel.targetStack;
        ItemSlashBlade itemBlade = BladeModel.itemBlade;


        if (!itemstack.isEmpty())
        {

            Item item = itemstack.getItem();

            boolean isScabbard = (item instanceof ItemSlashBladeWrapper && !ItemSlashBladeWrapper.hasWrapedItem(itemstack));

            if(isScabbard) {
                ItemStack mainHnad = entitylivingbaseIn.getHeldItemMainhand();
                if (mainHnad.getItem() instanceof ItemSlashBlade) {
                    EnumSet<ItemSlashBlade.SwordType> mainhandtypes = ((ItemSlashBlade) (mainHnad.getItem())).getSwordType(mainHnad);
                    if (!mainhandtypes.contains(ItemSlashBlade.SwordType.NoScabbard)) {
                        itemstack = mainHnad;
                    }else{
                        return;
                    }
                }
            }

            GlStateManager.pushMatrix();

            EnumSet<ItemSlashBlade.SwordType> swordType = itemBlade.getSwordType(itemstack);

            {
                WavefrontObject model = BladeModelManager.getInstance().getModel(itemBlade.getModelLocation(itemstack));
                ResourceLocationRaw resourceTexture = itemBlade.getModelTexture(itemstack);
                bindTexture(resourceTexture);

                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
                GL11.glAlphaFunc(GL11.GL_GEQUAL, 0.05f);

                if(isVR) {
                    GL11.glTranslatef(-0.4f, -0.1f, -0.05f);
                }

                GL11.glTranslatef(0.5f, 0.3f, 0.55f);
                float scale = 0.008f;
                GL11.glScalef(scale,scale,scale);
                GL11.glTranslatef(0.0f, 0.15f, 0.0f);

                if(isVR) {
                    GL11.glRotatef(-90, 0, 1, 0);
                }

                GL11.glRotatef(90, 0, 1, 0);
                GL11.glRotatef(-90, 0, 0, 1);

                if(isVR) {
                    GL11.glRotatef(-43, 0, 0, 1);
                }
                /*
                GL11.glTranslatef(0.0f, 0.15f, 0.0f);
                float scale = 0.008f;
                GL11.glScalef(scale,scale,scale);
                */

                if(isScabbard){
                    //GL11.glRotatef(180, 0, 0, 1);
                    GL11.glRotatef(180, 0, 1, 0);
                    GL11.glTranslatef(75.0f, 0.0f, 0.0f);
                }

                String renderTargets[];

                if(isScabbard){
                    renderTargets = new String[]{"sheath"};
                }else if(swordType.contains(ItemSlashBlade.SwordType.Cursed)){
                    renderTargets = new String[]{"sheath", "blade"};
                }else{
                    if(swordType.contains(ItemSlashBlade.SwordType.Broken)){
                        renderTargets = new String[]{"blade_damaged"};
                    }else{
                        renderTargets = new String[]{"blade"};
                    }
                }

                model.renderOnly(renderTargets);

                GlStateManager.disableLighting();
                try(LightSetup ls = LightSetup.setupAdd()){
                    for(String renderTarget : renderTargets)
                        model.renderPart(renderTarget + "_luminous");
                }

                GlStateManager.enableLighting();
            }

            GlStateManager.popMatrix();
        }
    }
}
