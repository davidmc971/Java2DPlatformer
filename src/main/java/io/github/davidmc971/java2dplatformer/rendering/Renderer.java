package io.github.davidmc971.java2dplatformer.rendering;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
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

    private ShaderProgram renderedSceneProgram, lightProgram, shadowMapProgram;
    private int fboScene, fboShadow, sceneMapId, shadowMapId;
    private IntBuffer drawBuffers;
    private int fbVao, lightVao, shadowVao, fbVbo, lightVbo, shadowVbo, uLocFbTex, uLocLightPosition,
            uLocCombinedMVPShadowMap, uLocLightPositionShadow, uLocCombinedMVPLightMap;
    private List<Vector2f> lights = new ArrayList<>();

    private FloatBuffer fbCombinedMVP;
    private Matrix4f combinedMVP = new Matrix4f();

    // Should contain only vertex coords in 2D
    private FloatBuffer shadowRegionBuffer;

    private float[] lightQuad = {
            1, -1, // br
            -1, -1, // bl
            -1, 1, // tl
            1, -1, // br
            1, 1, // tr
            -1, 1 // tl
    };

    private float[] fbQuad = {
            1, -1, 1, 0, // br
            -1, -1, 0, 0, // bl
            -1, 1, 0, 1, // tl
            1, -1, 1, 0, // br
            1, 1, 1, 1, // tr
            -1, 1, 0, 1 // tl
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

    private static final int RENDER_BATCH_QUAD_AMOUNT = 8192;

    public void initialize(Camera camera) {
        this.camera = camera;
        shaderProgram = new ShaderProgram();
        renderedSceneProgram = new ShaderProgram();
        lightProgram = new ShaderProgram();
        shadowMapProgram = new ShaderProgram();

        try {
            shaderProgram.attachShader(AssetManager.getShaderInternal("/shaders/main.vert"));
            shaderProgram.attachShader(AssetManager.getShaderInternal("/shaders/main.frag"));
            renderedSceneProgram.attachShader(AssetManager.getShaderInternal("/shaders/renderedScene.vert"));
            renderedSceneProgram.attachShader(AssetManager.getShaderInternal("/shaders/renderedScene.frag"));
            lightProgram.attachShader(AssetManager.getShaderInternal("/shaders/lightShader.vert"));
            lightProgram.attachShader(AssetManager.getShaderInternal("/shaders/lightShader.frag"));
            shadowMapProgram.attachShader(AssetManager.getShaderInternal("/shaders/shadowMap.vert"));
            shadowMapProgram.attachShader(AssetManager.getShaderInternal("/shaders/shadowMap.frag"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (CouldNotInferShaderTypeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        shaderProgram.link();
        renderedSceneProgram.link();
        lightProgram.link();
        shadowMapProgram.link();

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        textureBrick1 = AssetManager.getTextureInternal("/img/textures/Brick-01.png");
        textureBrick2 = AssetManager.getTextureInternal("/img/textures/Brick-02.png");
        textureBrick3 = AssetManager.getTextureInternal("/img/textures/Brick-03.png");

        uLocModel = shaderProgram.getUniformLocation("model");
        uLocView = shaderProgram.getUniformLocation("view");
        uLocProjection = shaderProgram.getUniformLocation("projection");

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
        fbCombinedMVP = MemoryUtil.memAllocFloat(16);

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

        drawBuffers.put(GL30.GL_COLOR_ATTACHMENT0).flip();
        GL30.glDrawBuffers(drawBuffers);

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

        renderedSceneProgram.use();
        uLocFbTex = GL20.glGetUniformLocation(renderedSceneProgram.programId, "renderedTexture");

        lightVao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(lightVao);

        lightVbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, lightVbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, lightQuad, GL15.GL_STATIC_DRAW);

        GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 2 * Float.BYTES, 0);
        GL20.glEnableVertexAttribArray(0);

        lightProgram.use();
        // uLocCombinedMVPLightMap = lightProgram.getUniformLocation("MVPMatrix");
        uLocLightPosition = GL20.glGetUniformLocation(lightProgram.programId, "lightPosition");

        // TODO: figure out good size
        shadowRegionBuffer = BufferUtils.createFloatBuffer(65536);

        shadowMapProgram.use();
        uLocCombinedMVPShadowMap = shadowMapProgram.getUniformLocation("MVPMatrix");
        uLocLightPositionShadow = shadowMapProgram.getUniformLocation("lightPosition");

        shadowVao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(shadowVao);

        shadowVbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, shadowVbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, shadowRegionBuffer.capacity(), GL15.GL_STREAM_DRAW);

        // Shadow buffer contains diagonal boxes with flags on moveable edges

        GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 3 * Float.BYTES, 0);
        GL20.glEnableVertexAttribArray(0);

        GL20.glVertexAttribPointer(1, 1, GL11.GL_FLOAT, false, 3 * Float.BYTES, 2 * Float.BYTES);
        GL20.glEnableVertexAttribArray(1);

        fboShadow = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fboShadow);
        shadowMapId = GL11.glGenTextures();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, shadowMapId);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, Game.WIDTH, Game.HEIGHT, 0, GL11.GL_RGBA,
                GL11.GL_UNSIGNED_BYTE, 0);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);

        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT1, GL11.GL_TEXTURE_2D, shadowMapId, 0);

        drawBuffers.put(GL30.GL_COLOR_ATTACHMENT1).flip();
        GL30.glDrawBuffers(drawBuffers);

        assert GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) == GL30.GL_FRAMEBUFFER_COMPLETE
                : "Framebuffer initialization error.";
    }

    public void preFrame() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fboScene);
        GL11.glViewport(0, 0, Game.WIDTH, Game.HEIGHT);

        GL11.glColorMask(true, true, true, true);
        GL11.glClearColor(0, 0, 0, 0);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        GL20.glUseProgram(shaderProgram.programId);

        textureBrick1.bind(0);
        textureBrick2.bind(1);
        textureBrick3.bind(2);
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

        shadowRegionBuffer.clear();
    }

    private Vector2f mousePositionWorldSpace = new Vector2f();
    private Vector4f mousePositionScreenSpace = new Vector4f();

    public void postFrame() {
        combinedMVP.identity().mul(camera.getProjectionMatrix()).mul(camera.getViewMatrix()).mul(m4fModel);
        mousePositionScreenSpace.set((((float) Game.MOUSE_X / (float) Game.WIDTH)) * 2f - 1f,
                (((float) -Game.MOUSE_Y / (float) Game.HEIGHT)) * 2f + 1f, 0, 0);
        // TODO:
        // mousePositionScreenSpace.mul(combinedMVP);
        // mousePositionWorldSpace.set(mousePositionScreenSpace.x,
        // mousePositionScreenSpace.y);

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

        GL11.glColorMask(true, true, true, true);
        GL11.glClearColor(0, 0, 0, 1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        GL11.glViewport(0, 0, Game.WIDTH, Game.HEIGHT);

        renderLights();

        renderShadows();

        renderSceneFromFb();
    }

    private void renderSceneFromFb() {
        GL30.glBindVertexArray(fbVao);
        GL20.glUseProgram(renderedSceneProgram.programId);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL13.glBindTexture(GL13.GL_TEXTURE_2D, sceneMapId);

        GL33.glUniform1i(uLocFbTex, 0);
        GL20.glEnableVertexAttribArray(0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, fbVbo);

        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);

        GL20.glDisableVertexAttribArray(0);

        GL30.glBindVertexArray(0);
        GL20.glUseProgram(0);
    }

    private void renderShadows() {
        GL30.glBindVertexArray(shadowVao);
        GL20.glUseProgram(shadowMapProgram.programId);
        GL20.glUniform2f(uLocLightPositionShadow, mousePositionScreenSpace.x, mousePositionScreenSpace.y);

        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);

        GL33.glUniformMatrix4fv(uLocCombinedMVPShadowMap, false, combinedMVP.get(fbCombinedMVP));

        currentBatchSize = shadowRegionBuffer.position();
        shadowRegionBuffer.flip();

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, shadowVbo);
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, shadowRegionBuffer);

        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, currentBatchSize);

        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);
        GL20.glUseProgram(0);
    }

    private void renderLights() {
        GL30.glBindVertexArray(lightVao);
        GL20.glEnableVertexAttribArray(0);
        GL20.glUseProgram(lightProgram.programId);
        // TODO:
        // GL33.glUniformMatrix4fv(uLocCombinedMVPLightMap, false,
        // combinedMVP.get(fbCombinedMVP));
        // System.out.println(camera.getX() + ", " + camera.getY());
        // TODO: world space
        GL20.glUniform2f(uLocLightPosition, mousePositionScreenSpace.x, mousePositionScreenSpace.y);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);

        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        GL20.glUseProgram(0);
    }

    public void render(io.github.davidmc971.java2dplatformer.ecs.GameObject gameObject) {

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
        if (!camera.coordsVisible2D(x, y, w, h))
            return;

        if (castsShadow && shadowRegionBuffer.remaining() >= 4) {
            // We are adding the diagonals of the quad into the shadow region buffer as
            // quads themselves.
            putShadowQuad(shadowRegionBuffer, x, y, x + w, y + h);
            putShadowQuad(shadowRegionBuffer, x + h, y, x, y + h);
        }

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

    private void putShadowQuad(FloatBuffer target, float x1, float y1, float x2, float y2) {
        /**
         * 1, -1
         * -1, -1
         * -1, 1
         * 1, -1
         * 1, 1
         * -1, 1
         */
        target.put(x1).put(y2).put(1)
                .put(x1).put(y2).put(0)
                .put(x2).put(y1).put(1)
                .put(x2).put(y1).put(1)
                .put(x2).put(y1).put(0)
                .put(x1).put(y2).put(0);
    }

    private float[] vertexArray = {
            // position // color // uv
            740, 260, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1, 0, -1, // 0
            540, 460, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0, 1, -1, // 1
            740, 460, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1, 1, -1, // 2
            540, 260, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1, 0, -1 // 3
    };

    // counterclockwise
    private int[] elementArray = {
            2, 1, 0,
            0, 1, 3
    };

    private int[] elementArrayWithOffset = new int[elementArray.length];

    public void queueTestSquare() {
        vertexBuffer.put(vertexArray);
        for (int i = 0; i < elementArray.length; i++) {
            elementArrayWithOffset[i] = elementArray[i] + batchElementOffset;
        }
        elementBuffer.put(elementArray);
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
}
