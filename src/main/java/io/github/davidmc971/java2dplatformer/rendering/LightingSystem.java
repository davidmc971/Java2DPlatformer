package io.github.davidmc971.java2dplatformer.rendering;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector4fc;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL33;

import io.github.davidmc971.java2dplatformer.main.AssetManager;
import io.github.davidmc971.java2dplatformer.main.Camera;
import io.github.davidmc971.java2dplatformer.main.Game;
import io.github.davidmc971.java2dplatformer.rendering.ShaderType.CouldNotInferShaderTypeException;

public class LightingSystem {
    // ============
    // Light shader
    private ShaderProgram lightShaderProgram;
    private int lightVao, lightVbo;

    private FloatBuffer lightBoxBuffer;

    // ============
    // Shadow shader
    private ShaderProgram shadowShaderProgram;
    private int shadowVao, shadowVbo;

    private FloatBuffer shadowDiagonalsBuffer;
    private int currentBatchSize = 0;

    // ============
    private Camera camera;
    private FloatBuffer projectionMatrixBuffer;
    private Matrix4f projectionMatrix = new Matrix4f().identity();
    private FloatBuffer viewMatrixBuffer;
    private Matrix4f viewMatrix = new Matrix4f().identity();
    private FloatBuffer modelMatrixBuffer;
    private Matrix4f modelMatrix = new Matrix4f().identity();

    // TODO: ????
    private float[] lightQuad = {
            (float) Game.WIDTH * 4, (float) -Game.HEIGHT * 4, // br
            (float) -Game.WIDTH * 4, (float) -Game.HEIGHT * 4, // bl
            (float) -Game.WIDTH * 4, (float) Game.HEIGHT * 4, // tl
            (float) Game.WIDTH * 4, (float) -Game.HEIGHT * 4, // br
            (float) Game.WIDTH * 4, (float) Game.HEIGHT * 4, // tr
            (float) -Game.WIDTH * 4, (float) Game.HEIGHT * 4 // tl
    };
    // {
    // 1, -1, // br
    // -1, -1, // bl
    // -1, 1, // tl
    // 1, -1, // br
    // 1, 1, // tr
    // -1, 1 // tl
    // };
    private Renderer renderer;

    public LightingSystem(Camera camera, Renderer renderer) {
        this.camera = camera;
        this.renderer = renderer;

        lightShaderProgram = new ShaderProgram();
        shadowShaderProgram = new ShaderProgram();
        try {
            lightShaderProgram.attachShader(AssetManager.getShaderInternal("/shaders/lightShader.vert"));
            lightShaderProgram.attachShader(AssetManager.getShaderInternal("/shaders/lightShader.frag"));
            shadowShaderProgram.attachShader(AssetManager.getShaderInternal("/shaders/shadowMap.vert"));
            shadowShaderProgram.attachShader(AssetManager.getShaderInternal("/shaders/shadowMap.geom"));
            shadowShaderProgram.attachShader(AssetManager.getShaderInternal("/shaders/shadowMap.frag"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CouldNotInferShaderTypeException e) {
            e.printStackTrace();
        }
        lightShaderProgram.link();
        shadowShaderProgram.link();

        projectionMatrixBuffer = BufferUtils.createFloatBuffer(16);
        viewMatrixBuffer = BufferUtils.createFloatBuffer(16);
        modelMatrixBuffer = BufferUtils.createFloatBuffer(16);
        shadowDiagonalsBuffer = BufferUtils.createFloatBuffer(512000);
        lightBoxBuffer = BufferUtils.createFloatBuffer(12);

        lightVao = GL33.glGenVertexArrays();
        GL33.glBindVertexArray(lightVao);

        lightVbo = GL33.glGenBuffers();
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, lightVbo);
        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, lightQuad, GL33.GL_DYNAMIC_DRAW);

        GL33.glVertexAttribPointer(0, 2, GL33.GL_FLOAT, false, 2 * Float.BYTES, 0);
        GL33.glEnableVertexAttribArray(0);

        shadowVao = GL33.glGenVertexArrays();
        GL33.glBindVertexArray(shadowVao);

        shadowVbo = GL33.glGenBuffers();
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, shadowVbo);
        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, shadowDiagonalsBuffer.capacity(), GL33.GL_DYNAMIC_DRAW);

        // Shadow buffer contains diagonal boxes with flags on moveable edges
        // Update, contains just the diagonal lines for processing in geometry shader

        GL33.glVertexAttribPointer(0, 2, GL33.GL_FLOAT, false, 2 * Float.BYTES, 0);
        GL33.glEnableVertexAttribArray(0);

        // GL33.glVertexAttribPointer(1, 1, GL33.GL_FLOAT, false, 4 * Float.BYTES, 3 * Float.BYTES);
        // GL33.glEnableVertexAttribArray(1);
    }

    public void insertShadowDiagonals(float x1, float y1, float x2, float y2) {
        // TODO: fix pls
        if (shadowDiagonalsBuffer.remaining() < 36) {
            System.out.println("full");
            return;
        }

        putShadowLine(shadowDiagonalsBuffer, x1, y1, x2, y2);
        putShadowLine(shadowDiagonalsBuffer, x2, y1, x1, y2);
    }

    private void putShadowLine(FloatBuffer target, float x1, float y1, float x2, float y2) {
        target.put(x1).put(y1).put(x2).put(y2);
    }

    public void invoke(List<? extends Vector4fc> lights) {
        preRender();
        for (int i = 0; i < lights.size(); i++) {
            render(lights.get(i));
        }
        // lights.sort((o1, o2) -> (int) (o2.w() - o1.w()));
        // lights.forEach((light) -> {
        // // System.out.println(light.w());
        // render(light);
        // });
        postRender();
    }

    private void preRender() {
        projectionMatrix.set(camera.getProjectionMatrix()).get(projectionMatrixBuffer.position(0));
        viewMatrix.set(camera.getViewMatrix()).get(viewMatrixBuffer.position(0));
        modelMatrix.identity().get(modelMatrixBuffer.position(0));

        shadowShaderProgram.use();
        shadowShaderProgram.sendUniformMatrix4f("projectionMatrix", projectionMatrixBuffer.position(0));
        shadowShaderProgram.sendUniformMatrix4f("viewMatrix", viewMatrixBuffer.position(0));
        // shadowShaderProgram.sendUniformMatrix4f("modelMatrix", modelMatrixBuffer.position(0));

        // modelMatrix.identity().translate(camera.getPosition()).get(modelMatrixBuffer.position(0));

        lightShaderProgram.use();
        lightShaderProgram.sendUniformMatrix4f("projectionMatrix", projectionMatrixBuffer.position(0));
        lightShaderProgram.sendUniformMatrix4f("viewMatrix", viewMatrixBuffer.position(0));
        // lightShaderProgram.sendUniformMatrix4f("modelMatrix", modelMatrixBuffer.position(0));
        GL33.glUseProgram(0);

        // Divide by two because two floats per line
        currentBatchSize = shadowDiagonalsBuffer.position() / 2;
        shadowDiagonalsBuffer.flip();

        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, shadowVbo);
        GL33.glBufferSubData(GL33.GL_ARRAY_BUFFER, 0, shadowDiagonalsBuffer);

        lightBoxBuffer.clear().position(0);
        for (int i = 0; i < lightQuad.length - 1; i += 2) {
            lightBoxBuffer.put(lightQuad[i] - camera.getX() + Game.WIDTH / 2)
                    .put(lightQuad[i + 1] - camera.getY() + Game.HEIGHT / 2);

        }
        lightBoxBuffer.flip();
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, lightVbo);
        GL33.glBufferSubData(GL33.GL_ARRAY_BUFFER, 0, lightBoxBuffer);

        GL33.glEnable(GL33.GL_BLEND);
        GL33.glClearColor(0, 0, 0, 0);
        GL33.glClear(GL33.GL_COLOR_BUFFER_BIT);
    }

    private void render(Vector4fc light) {
        GL33.glDepthMask(false);
        GL33.glBlendEquationSeparate(GL33.GL_FUNC_ADD, GL33.GL_FUNC_ADD);
        GL33.glBlendFuncSeparate(GL33.GL_ZERO, GL33.GL_ONE, GL33.GL_ONE, GL33.GL_ZERO);
        shadowRenderPass(light);
        if (Game.DEBUG) {
            debugRenderFramebuffer();
        }
        GL33.glBlendEquationSeparate(GL33.GL_FUNC_ADD, GL33.GL_FUNC_ADD);
        GL33.glBlendFuncSeparate(GL33.GL_ONE_MINUS_DST_ALPHA, GL33.GL_ONE, GL33.GL_ZERO, GL33.GL_ZERO);
        lightRenderPass(light);
        if (Game.DEBUG) {
            debugRenderFramebufferCustomBlend(GL33.GL_FUNC_ADD, GL33.GL_FUNC_ADD,
                    GL33.GL_ONE, GL33.GL_ONE, GL33.GL_DST_COLOR, GL33.GL_DST_COLOR);
        }
        GL33.glDepthMask(true);
    }

    private void shadowRenderPass(Vector4fc light) {
        GL33.glBindVertexArray(shadowVao);
        shadowShaderProgram.use();
        GL33.glUniform3f(shadowShaderProgram.uniform("lightPosition"), light.x(), light.y(), light.z());
        // GL33.glUniform1f(shadowShaderProgram.uniform("lightIndex"), light.w());

        GL33.glEnableVertexAttribArray(0);
        // GL33.glEnableVertexAttribArray(1);

        GL33.glDrawArrays(GL33.GL_LINES, 0, currentBatchSize);

        GL33.glDisableVertexAttribArray(0);
        // GL33.glDisableVertexAttribArray(1);

        GL33.glBindVertexArray(0);
        GL33.glUseProgram(0);
    }

    private void lightRenderPass(Vector4fc light) {
        GL33.glBindVertexArray(lightVao);
        lightShaderProgram.use();
        GL33.glUniform3f(lightShaderProgram.uniform("lightPosition"), light.x(), light.y(), light.z());
        // GL33.glUniform1f(lightShaderProgram.uniform("lightIndex"), light.w());

        GL33.glEnableVertexAttribArray(0);

        GL33.glDrawArrays(GL33.GL_TRIANGLES, 0, 6);

        GL33.glDisableVertexAttribArray(0);
        GL33.glBindVertexArray(0);
        GL33.glUseProgram(0);
    }

    private int debugFrameCount = 0;

    private void postRender() {
        shadowDiagonalsBuffer.clear();
        GL33.glBlendEquation(GL33.GL_FUNC_ADD);
        GL33.glBlendFunc(GL33.GL_ONE, GL33.GL_ZERO);
        debugFrameCount = 0;
    }

    private void debugRenderFramebuffer() {
        debugRenderFramebufferCustomBlend(GL33.GL_FUNC_ADD, GL33.GL_FUNC_ADD,
                GL33.GL_SRC_ALPHA, GL33.GL_ONE_MINUS_SRC_ALPHA, GL33.GL_SRC_ALPHA, GL33.GL_ONE_MINUS_SRC_ALPHA);
    }

    private void debugRenderFramebufferCustomBlend(int modeRGB, int modeAlpha,
            int sFactorRGB, int dFactorRGB, int sFactorAlpha, int dFactorAlpha) {
        if (debugFrameCount >= 8)
            return;
        int x = (Game.WIDTH / 2) + (Game.WIDTH / 4) * (debugFrameCount % 2);
        int y = (Game.HEIGHT / 4) * 3 - ((int) Math.floor(debugFrameCount / 2) * (Game.HEIGHT / 4));
        GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, 0);
        GL33.glViewport(
                x, y,
                Game.WIDTH / 4, Game.HEIGHT / 4);

        GL33.glBlendEquationSeparate(modeRGB, modeAlpha);
        GL33.glBlendFuncSeparate(sFactorRGB, dFactorRGB, sFactorAlpha, dFactorAlpha);
        renderer.renderSceneFromFb(1, renderer.getLightingMapId());
        GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, renderer.getFboLightingSystem());
        GL33.glViewport(0, 0, Game.WIDTH / Renderer.LIGHTING_RESOLUTION_DIVISOR,
                Game.HEIGHT / Renderer.LIGHTING_RESOLUTION_DIVISOR);
        debugFrameCount++;
    }
}
