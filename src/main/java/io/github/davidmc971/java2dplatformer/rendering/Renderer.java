package io.github.davidmc971.java2dplatformer.rendering;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryUtil;

import io.github.davidmc971.java2dplatformer.main.AssetManager;
import io.github.davidmc971.java2dplatformer.main.Camera;
import io.github.davidmc971.java2dplatformer.main.Game;
import io.github.davidmc971.java2dplatformer.rendering.ShaderType.CouldNotInferShaderTypeException;
import io.github.davidmc971.java2dplatformer.util.GLTextureSlot;

public class Renderer {

    private ShaderProgram shaderProgram;

    private int uLocModel, uLocView, uLocProjection, vao, vbo, ebo;

    private ShaderProgram renderedSceneProgram;
    private int fboScene, sceneMapId;
    private IntBuffer drawBuffers;
    private int fbVao, fbVbo, uLocFbTex;
    private int bgVao, bgVbo;

    private Matrix4f combinedMVP = new Matrix4f();

    private float[] fbQuad = {
            1, -1, 1, 0, // br
            -1, -1, 0, 0, // bl
            -1, 1, 0, 1, // tl
            1, -1, 1, 0, // br
            1, 1, 1, 1, // tr
            -1, 1, 0, 1 // tl
    };

    private static float BG_QUAD_FACTOR = 0.25f;
    private float[] bgQuad = {
            1, -1, 16 * BG_QUAD_FACTOR, 0, // br
            -1, -1, 0, 0, // bl
            -1, 1, 0, -9 * BG_QUAD_FACTOR, // tl
            1, -1, 16 * BG_QUAD_FACTOR, 0, // br
            1, 1, 16 * BG_QUAD_FACTOR, -9 * BG_QUAD_FACTOR, // tr
            -1, 1, 0, -9 * BG_QUAD_FACTOR // tl
    };

    private FloatBuffer vertexBuffer;
    private IntBuffer elementBuffer;

    private FloatBuffer fbProjectionMatrix;
    private FloatBuffer fbViewMatrix;
    private FloatBuffer fbModelMatrix;

    private Camera camera;
    private Matrix4f m4fModel = new Matrix4f();

    private Texture textureBrick1;
    private Texture textureBrick2;
    private Texture textureBrick3;
    private Texture textureCobble1;

    private static final int RENDER_BATCH_QUAD_AMOUNT = 8192;

    private LightingSystem lightingSystem;
    private List<Vector4f> lights = new ArrayList<>();
    private Vector4f mouseLight = new Vector4f();
    private int fboLightingSystem, lightingMapId;
    public static int LIGHTING_RESOLUTION_DIVISOR = 4;

    public void initialize(Camera camera) {
        this.camera = camera;
        shaderProgram = new ShaderProgram();
        renderedSceneProgram = new ShaderProgram();
        try {
            shaderProgram.attachShader(AssetManager.getShaderInternal("/shaders/main.vert"));
            shaderProgram.attachShader(AssetManager.getShaderInternal("/shaders/main.frag"));
            renderedSceneProgram.attachShader(AssetManager.getShaderInternal("/shaders/renderedScene.vert"));
            renderedSceneProgram.attachShader(AssetManager.getShaderInternal("/shaders/renderedScene.frag"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CouldNotInferShaderTypeException e) {
            e.printStackTrace();
        }
        shaderProgram.link();
        renderedSceneProgram.link();

        textureBrick1 = AssetManager.getTextureInternal("/img/textures/Brick-01.png");
        textureBrick2 = AssetManager.getTextureInternal("/img/textures/Brick-02.png");
        textureBrick3 = AssetManager.getTextureInternal("/img/textures/Brick-03.png");
        textureCobble1 = AssetManager.getTextureInternal("/img/textures/Cobble-01.png");

        uLocModel = shaderProgram.uniform("model");
        uLocView = shaderProgram.uniform("view");
        uLocProjection = shaderProgram.uniform("projection");

        vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);

        int positionsSize = 3;
        int colorSize = 4;
        int uvSize = 2;
        int textureIdSize = 1;
        int vertexSize = (positionsSize + colorSize + uvSize + textureIdSize);
        int vertexSizeBytes = vertexSize * Float.BYTES;

        vertexBuffer = BufferUtils.createFloatBuffer(4 * vertexSize * RENDER_BATCH_QUAD_AMOUNT);
        elementBuffer = BufferUtils.createIntBuffer(6 * RENDER_BATCH_QUAD_AMOUNT);

        vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_DYNAMIC_DRAW);

        ebo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL15.GL_DYNAMIC_DRAW);

        GL20.glVertexAttribPointer(0, positionsSize, GL11.GL_FLOAT, false, vertexSizeBytes, 0);
        GL20.glEnableVertexAttribArray(0);

        GL20.glVertexAttribPointer(1, colorSize, GL11.GL_FLOAT, false, vertexSizeBytes, positionsSize * Float.BYTES);
        GL20.glEnableVertexAttribArray(1);

        GL20.glVertexAttribPointer(2, uvSize, GL11.GL_FLOAT, false, vertexSizeBytes,
                (positionsSize + colorSize) * Float.BYTES);
        GL20.glEnableVertexAttribArray(2);

        GL20.glVertexAttribPointer(3, textureIdSize, GL11.GL_FLOAT, false, vertexSizeBytes,
                (positionsSize + colorSize + uvSize) * Float.BYTES);
        GL20.glEnableVertexAttribArray(3);

        fbProjectionMatrix = MemoryUtil.memAllocFloat(16);
        fbViewMatrix = MemoryUtil.memAllocFloat(16);
        fbModelMatrix = MemoryUtil.memAllocFloat(16);

        fboScene = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fboScene);
        sceneMapId = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, sceneMapId);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, Game.WIDTH, Game.HEIGHT, 0, GL11.GL_RGBA,
                GL11.GL_UNSIGNED_BYTE, 0);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);

        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, sceneMapId, 0);

        drawBuffers = BufferUtils.createIntBuffer(32);

        drawBuffers.put(GL30.GL_COLOR_ATTACHMENT0);

        assert GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) == GL30.GL_FRAMEBUFFER_COMPLETE
                : "Framebuffer initialization error.";

        fbVao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(fbVao);

        fbVbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, fbVbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, fbQuad, GL15.GL_STATIC_DRAW);

        GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 4 * Float.BYTES, 0);
        GL20.glEnableVertexAttribArray(0);

        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES);
        GL20.glEnableVertexAttribArray(1);

        bgVao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(bgVao);

        bgVbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bgVbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, bgQuad, GL15.GL_STATIC_DRAW);

        GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 4 * Float.BYTES, 0);
        GL20.glEnableVertexAttribArray(0);

        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES);
        GL20.glEnableVertexAttribArray(1);

        renderedSceneProgram.use();
        uLocFbTex = renderedSceneProgram.uniform("renderedTexture");

        fboLightingSystem = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fboLightingSystem);
        lightingMapId = GL11.glGenTextures();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, lightingMapId);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, Game.WIDTH / LIGHTING_RESOLUTION_DIVISOR,
                Game.HEIGHT / LIGHTING_RESOLUTION_DIVISOR, 0, GL11.GL_RGBA,
                GL11.GL_UNSIGNED_BYTE, 0);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT1, GL11.GL_TEXTURE_2D, lightingMapId,
                0);

        // // create a renderbuffer object to store depth info
        // int rboId = GL30.glGenRenderbuffers();
        // GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, rboId);
        // GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH_COMPONENT,
        // Game.WIDTH, Game.HEIGHT);
        // GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0);
        // // attach the renderbuffer to depth attachment point
        // GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, // 1. fbo target:
        // GL_FRAMEBUFFER
        // GL30.GL_DEPTH_ATTACHMENT, // 2. attachment point
        // GL30.GL_RENDERBUFFER, // 3. rbo target: GL_RENDERBUFFER
        // rboId); // 4. rbo ID

        drawBuffers.put(GL30.GL_COLOR_ATTACHMENT1).flip();
        GL20.glDrawBuffers(drawBuffers);

        assert GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) == GL30.GL_FRAMEBUFFER_COMPLETE
                : "Framebuffer initialization error.";

        lightingSystem = new LightingSystem(camera, this);
        // lights.add(mouseLight);
    }

    public void preFrame() {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fboScene);
        GL11.glClearColor(0, 0, 0, 0);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        GL11.glViewport(0, 0, Game.WIDTH, Game.HEIGHT);
        GL20.glUseProgram(shaderProgram.programId);

        textureBrick1.bind(0);
        textureBrick2.bind(1);
        textureBrick3.bind(2);
        textureCobble1.bind(3);
        shaderProgram.sendTextureUniform("textureSampler", GLTextureSlot.getMaxTextureSlotsNumberArray());

        GL30.glBindVertexArray(vao);

        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        GL20.glEnableVertexAttribArray(3);

        m4fModel.identity();
        GL20.glUniformMatrix4fv(uLocProjection, false, camera.getProjectionMatrix().get(fbProjectionMatrix));
        GL20.glUniformMatrix4fv(uLocView, false, camera.getViewMatrix().get(fbViewMatrix));
        GL20.glUniformMatrix4fv(uLocModel, false, m4fModel.get(fbModelMatrix));
    }

    private Vector2f mousePositionScreenSpace = new Vector2f();
    private Vector3fc mousePositionWorldSpace;

    public void postFrame() {
        combinedMVP.identity().mul(camera.getProjectionMatrix()).mul(camera.getViewMatrix()).mul(m4fModel);
        mousePositionScreenSpace.set((((float) Game.MOUSE_X / (float) Game.WIDTH)) * 2f - 1f,
                (((float) -Game.MOUSE_Y / (float) Game.HEIGHT)) * 2f + 1f);
        mousePositionWorldSpace = camera.screenPositionToWorldPosition(mousePositionScreenSpace);

        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL20.glDisableVertexAttribArray(3);

        textureBrick1.unbind();
        textureBrick2.unbind();
        textureBrick3.unbind();

        GL30.glBindVertexArray(0);
        GL20.glUseProgram(0);

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        GL11.glClearColor(1, 1, 0.9f, 1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fboLightingSystem);
        GL11.glViewport(0, 0, Game.WIDTH / LIGHTING_RESOLUTION_DIVISOR, Game.HEIGHT / LIGHTING_RESOLUTION_DIVISOR);

        checkAddLight();
        mouseLight.set(mousePositionWorldSpace, 0);
        lights.add(mouseLight);
        lightingSystem.invoke(lights);
        lights.remove(mouseLight);

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

        if (Game.DEBUG) {
            GL11.glViewport(
                    0, Game.HEIGHT / 2,
                    Game.WIDTH / 2, Game.HEIGHT / 2);
            renderSceneFromFb(0, sceneMapId);

            GL11.glViewport(
                    0, 0,
                    Game.WIDTH / 2, Game.HEIGHT / 2);
        } else {
            GL11.glViewport(0, 0, Game.WIDTH, Game.HEIGHT);
        }

        // renderSceneFromFb(2, textureCobble1.textureId, bgVao, bgVbo, -1f);

        GL33.glBlendEquation(GL33.GL_FUNC_ADD);
        GL33.glBlendFuncSeparate(
                GL33.GL_ZERO, GL33.GL_SRC_COLOR,
                GL33.GL_ZERO, GL33.GL_SRC_COLOR);
        renderSceneFromFb(1, lightingMapId, 0.9f);

        // GL33.glBlendEquation(GL33.GL_FUNC_ADD);
        // GL33.glBlendFuncSeparate(
        //         GL33.GL_SRC_COLOR, GL33.GL_DST_COLOR,
        //         GL33.GL_ONE, GL33.GL_SRC_ALPHA);
        // renderSceneFromFb(0, sceneMapId, 3.5f);

        GL33.glBlendEquation(GL33.GL_FUNC_ADD);
        GL33.glBlendFuncSeparate(
                GL33.GL_DST_COLOR, GL33.GL_ONE_MINUS_SRC_ALPHA,
                GL33.GL_ONE_MINUS_SRC_COLOR, GL33.GL_ONE_MINUS_SRC_ALPHA);
        renderSceneFromFb(0, sceneMapId);
    }

    public void renderSceneFromFb(int fbTexId, int drawMapId, float alpha) {
        renderSceneFromFb(fbTexId, drawMapId, fbVao, fbVbo, alpha);

    }

    public void renderSceneFromFb(int fbTexId, int drawMapId) {
        renderSceneFromFb(fbTexId, drawMapId, fbVao, fbVbo, -1f);
    }

    public void renderSceneFromFb(int fbTexId, int drawMapId, int vao, int vbo, float alpha) {
        GL30.glBindVertexArray(vao);
        GL20.glUseProgram(renderedSceneProgram.programId);

        GL13.glActiveTexture(GLTextureSlot.get(fbTexId).glTextureSlot);
        GL13.glBindTexture(GL13.GL_TEXTURE_2D, drawMapId);

        GL33.glUniform1i(uLocFbTex, fbTexId);
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);

        GL33.glUniform1f(renderedSceneProgram.uniform("alpha"), alpha);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);

        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);

        GL30.glBindVertexArray(0);
        GL20.glUseProgram(0);
    }

    private boolean willAddLight = false;
    private int lightCounter = 1;

    private void checkAddLight() {
        if (!willAddLight && Game.MOUSE_DOWN) {
            willAddLight = true;
        } else if (willAddLight && !Game.MOUSE_DOWN) {
            addLight(mousePositionWorldSpace.x(), mousePositionWorldSpace.y(), mousePositionWorldSpace.z());
            willAddLight = false;
        }
    }

    public void addLight(float x, float y, float z) {
        lights.add(new Vector4f().set(x, y, z, lightCounter++));
    }

    public void clearLights() {
        lights.clear();
    }

    private int batchElementOffset = 0;
    private static Vector4f colorWhite = new Vector4f(1, 1, 1, 1);

    public void render(io.github.davidmc971.java2dplatformer.framework.GameObject gameObject) {
        drawQuad(gameObject.getX(), gameObject.getY(), 0, gameObject.getWidth(), gameObject.getHeight(), colorWhite);
    }

    public void drawQuad(Vector3f position, Vector3f dimensions, Vector4f color) {
        drawQuad(position.x, position.y, position.z, dimensions.x, dimensions.y, color);
    }

    public void drawQuad(float x, float y, float z, float w, float h, Vector4f color) {
        drawQuad(x, y, z, w, h, color.x, color.y, color.z, color.w);
    }

    public void drawQuad(float x, float y, float z, float w, float h, float r, float g, float b, float a,
            boolean castsShadow) {
        drawQuadAny(x, y, z, w, h, r, g, b, a, -1, castsShadow);
    }

    public void drawQuad(float x, float y, float z, float w, float h, float r, float g, float b, float a) {
        drawQuadAny(x, y, z, w, h, r, g, b, a, -1, false);
    }

    public void drawTexturedQuad(float x, float y, float z, float w, float h, float texId) {
        drawQuadAny(x, y, z, w, h, 1, 1, 1, 1, texId, false);
    }

    public void drawTexturedQuad(float x, float y, float z, float w, float h, float r, float g, float b, float a,
            float texId, boolean castsShadow) {
        drawQuadAny(x, y, z, w, h, r, g, b, a, texId, castsShadow);
    }

    public void drawTexturedQuad(float x, float y, float z, float w, float h, float r, float g, float b, float a,
            float texId) {
        drawQuadAny(x, y, z, w, h, r, g, b, a, texId, false);
    }

    private void drawQuadAny(/* position */ float x, float y, float z, /* dimensions */ float w, float h,
            /* color */ float r, float g, float b, float a, /* texture id */ float texId, boolean castsShadow) {
        if (castsShadow && camera.coordsVisible2D(
                x - Game.WIDTH / 2,
                y - Game.HEIGHT / 2,
                w + Game.WIDTH,
                h + Game.HEIGHT)) {
            // We are adding the diagonals of the quad into the shadow region buffer as
            // quads themselves.
            lightingSystem.insertShadowDiagonals(x, y, x + w, y + h);
        }

        if (!camera.coordsVisible2D(x, y, w, h))
            return;

        // if (castsShadow) {
        //     // We are adding the diagonals of the quad into the shadow region buffer as
        //     // quads themselves.
        //     lightingSystem.insertShadowDiagonals(x, y, x + w, y + h);
        // }

        if (vertexBuffer.remaining() < 4 || elementBuffer.remaining() < 6)
            flush();

        vertexBuffer.put(x + w).put(y).put(z)
                .put(r).put(g).put(b).put(a)
                .put(1).put(0).put(texId);
        vertexBuffer.put(x).put(y + h).put(z)
                .put(r).put(g).put(b).put(a)
                .put(0).put(1).put(texId);
        vertexBuffer.put(x + w).put(y + h).put(z)
                .put(r).put(g).put(b).put(a)
                .put(1).put(1).put(texId);
        vertexBuffer.put(x).put(y).put(z)
                .put(r).put(g).put(b).put(a)
                .put(0).put(0).put(texId);

        elementBuffer.put(2 + batchElementOffset).put(1 + batchElementOffset).put(0 + batchElementOffset);
        elementBuffer.put(0 + batchElementOffset).put(1 + batchElementOffset).put(3 + batchElementOffset);
        batchElementOffset += 4;
    }

    private int currentBatchSize;

    public void flush() {
        Game.drawCalls++;
        currentBatchSize = elementBuffer.position();

        vertexBuffer.flip();
        elementBuffer.flip();

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, vertexBuffer);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
        GL15.glBufferSubData(GL15.GL_ELEMENT_ARRAY_BUFFER, 0, elementBuffer);

        GL11.glDrawElements(GL11.GL_TRIANGLES, currentBatchSize, GL11.GL_UNSIGNED_INT, 0);

        vertexBuffer.clear();
        elementBuffer.clear();
        batchElementOffset = 0;
    }

    @Override
    protected void finalize() {
        MemoryUtil.memFree(fbProjectionMatrix);
        MemoryUtil.memFree(fbViewMatrix);
        MemoryUtil.memFree(fbModelMatrix);
    }

    public int getSceneMapId() {
        return sceneMapId;
    }

    public int getLightingMapId() {
        return lightingMapId;
    }

    public int getFboLightingSystem() {
        return fboLightingSystem;
    }
}
