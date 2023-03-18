package io.github.davidmc971.java2dplatformer.rendering;

import static org.lwjgl.opengl.GL33.*;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector4fc;
import org.lwjgl.BufferUtils;

import io.github.davidmc971.java2dplatformer.main.AssetManager;
import io.github.davidmc971.java2dplatformer.main.Camera;
import io.github.davidmc971.java2dplatformer.rendering.ShaderType.CouldNotInferShaderTypeException;

public class LightingSystem {
    // ============
    // Light shader
    private ShaderProgram lightShaderProgram;
    private int lightVao, lightVbo;

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
    private float[] lightQuad =
            // {
            // (float) Game.WIDTH / 2f, -(float) Game.HEIGHT / 2f, // br
            // -(float) Game.WIDTH / 2f, -(float) Game.HEIGHT / 2f, // bl
            // -(float) Game.WIDTH / 2f, (float) Game.HEIGHT / 2f, // tl
            // (float) Game.WIDTH / 2f, -(float) Game.HEIGHT / 2f, // br
            // (float) Game.WIDTH / 2f, (float) Game.HEIGHT / 2f, // tr
            // -(float) Game.WIDTH / 2f, (float) Game.HEIGHT / 2f // tl
            // };
            {
                    1, -1, // br
                    -1, -1, // bl
                    -1, 1, // tl
                    1, -1, // br
                    1, 1, // tr
                    -1, 1 // tl
            };

    public LightingSystem(Camera camera) {
        this.camera = camera;

        lightShaderProgram = new ShaderProgram();
        shadowShaderProgram = new ShaderProgram();
        try {
            lightShaderProgram.attachShader(AssetManager.getShaderInternal("/shaders/lightShader.vert"));
            lightShaderProgram.attachShader(AssetManager.getShaderInternal("/shaders/lightShader.frag"));
            shadowShaderProgram.attachShader(AssetManager.getShaderInternal("/shaders/shadowMap.vert"));
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
        shadowDiagonalsBuffer = BufferUtils.createFloatBuffer(65536);

        lightVao = glGenVertexArrays();
        glBindVertexArray(lightVao);

        lightVbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, lightVbo);
        glBufferData(GL_ARRAY_BUFFER, lightQuad, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 2, GL_FLOAT, false, 2 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        shadowVao = glGenVertexArrays();
        glBindVertexArray(shadowVao);

        shadowVbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, shadowVbo);
        glBufferData(GL_ARRAY_BUFFER, shadowDiagonalsBuffer.capacity(), GL_DYNAMIC_DRAW);

        // Shadow buffer contains diagonal boxes with flags on moveable edges

        glVertexAttribPointer(0, 2, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 1, GL_FLOAT, false, 3 * Float.BYTES, 2 * Float.BYTES);
        glEnableVertexAttribArray(1);
    }

    public void insertShadowDiagonals(float x1, float y1, float x2, float y2) {
        // TODO: fix pls
        if (shadowDiagonalsBuffer.remaining() < 36)
            return;

        putShadowQuad(shadowDiagonalsBuffer, x1, y1, x2, y2);
        putShadowQuad(shadowDiagonalsBuffer, x2, y1, x1, y2);
    }

    private void putShadowQuad(FloatBuffer target, float x1, float y1, float x2, float y2) {
        target.put(x1).put(y2).put(1)
                .put(x1).put(y2).put(0)
                .put(x2).put(y1).put(1)
                .put(x2).put(y1).put(1)
                .put(x2).put(y1).put(0)
                .put(x1).put(y2).put(0);
    }

    public void invoke(List<? extends Vector4fc> lights) {
        preRender();
        lights.forEach((light) -> {
            render(light);
        });
        postRender();
    }

    private void preRender() {
        projectionMatrix.set(camera.getProjectionMatrix()).get(projectionMatrixBuffer.position(0));
        viewMatrix.set(camera.getViewMatrix()).get(viewMatrixBuffer.position(0));
        modelMatrix.identity().get(modelMatrixBuffer.position(0));

        shadowShaderProgram.use();
        shadowShaderProgram.sendUniformMatrix4f("projectionMatrix", projectionMatrixBuffer.position(0));
        shadowShaderProgram.sendUniformMatrix4f("viewMatrix", viewMatrixBuffer.position(0));
        shadowShaderProgram.sendUniformMatrix4f("modelMatrix", modelMatrixBuffer.position(0));
        lightShaderProgram.use();
        lightShaderProgram.sendUniformMatrix4f("projectionMatrix", projectionMatrixBuffer.position(0));
        lightShaderProgram.sendUniformMatrix4f("viewMatrix", viewMatrixBuffer.position(0));
        lightShaderProgram.sendUniformMatrix4f("modelMatrix", modelMatrixBuffer.position(0));
        glUseProgram(0);

        currentBatchSize = shadowDiagonalsBuffer.position();
        shadowDiagonalsBuffer.flip();

        glBindBuffer(GL_ARRAY_BUFFER, shadowVbo);
        glBufferSubData(GL_ARRAY_BUFFER, 0, shadowDiagonalsBuffer);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_DEPTH_TEST);
        glDepthMask(true);
        glClearColor(0, 0, 0, 0);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    private void render(Vector4fc light) {
        shadowRenderPass(light);
        lightRenderPass(light);
        glClear(GL_DEPTH_BUFFER_BIT);
    }

    private void shadowRenderPass(Vector4fc light) {
        glBindVertexArray(shadowVao);
        shadowShaderProgram.use();
        glUniform2f(shadowShaderProgram.uniform("lightPosition"), light.x(), light.y());
        glUniform1f(shadowShaderProgram.uniform("lightIndex"), light.z());

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawArrays(GL_TRIANGLES, 0, currentBatchSize);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);
        glUseProgram(0);
    }

    private void lightRenderPass(Vector4fc light) {
        // glDepthMask(false);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE);

        glBindVertexArray(lightVao);
        lightShaderProgram.use();
        glUniform2f(lightShaderProgram.uniform("lightPosition"), light.x(), light.y());
        glUniform1f(lightShaderProgram.uniform("lightIndex"), light.z());

        glEnableVertexAttribArray(0);

        glDrawArrays(GL_TRIANGLES, 0, 6);

        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
        glUseProgram(0);

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        // glDepthMask(true);
    }

    private void postRender() {
        shadowDiagonalsBuffer.clear();
        glDepthMask(false);
        glDisable(GL_DEPTH_TEST);
    }
}
