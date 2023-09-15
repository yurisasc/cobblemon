package com.cobblemon.mod.common.client.flywheel;

import com.jozufozu.flywheel.core.hardcoded.ModelPart;
import com.jozufozu.flywheel.core.hardcoded.PartBuilder;
import com.jozufozu.flywheel.core.vertex.PosTexNormalWriterUnsafe;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

//This is done in Java because it is mostly copied verbatim from Flywheel,
//but we use special CuboidBuilders that can use custom pivots for rotation
//Rewrite in Kotlin if u want
public class BetterPartBuilder {

    private final float sizeU;
    private final float sizeV;

    private Sprite sprite;

    private final List<BetterPartBuilder.CuboidBuilder> cuboids = new ArrayList<>();
    private final String name;

    public BetterPartBuilder(String name, int sizeU, int sizeV) {
        this.name = name;
        this.sizeU = (float) sizeU;
        this.sizeV = (float) sizeV;
    }

    public BetterPartBuilder sprite(Sprite sprite) {
        this.sprite = sprite;
        return this;
    }

    public BetterPartBuilder.CuboidBuilder cuboid() {
        return new BetterPartBuilder.CuboidBuilder(this);
    }

    public BetterModelPart build() {
        return new BetterModelPart(cuboids, name);
    }

    private BetterPartBuilder addCuboid(BetterPartBuilder.CuboidBuilder builder) {
        cuboids.add(builder);
        return this;
    }

    public static class CuboidBuilder {

        Sprite sprite;

        Set<Direction> visibleFaces = EnumSet.allOf(Direction.class);
        int textureOffsetU;
        int textureOffsetV;

        float posX1;
        float posY1;
        float posZ1;
        float posX2;
        float posY2;
        float posZ2;

        boolean invertYZ;

        boolean useRotation;
        float rotationX;
        float rotationY;
        float rotationZ;
        boolean usePivot;
        float pivotX;
        float pivotY;
        float pivotZ;

        final BetterPartBuilder partBuilder;

        CuboidBuilder(BetterPartBuilder partBuilder) {
            this.partBuilder = partBuilder;
            this.sprite = partBuilder.sprite;
        }

        public BetterPartBuilder.CuboidBuilder textureOffset(int u, int v) {
            this.textureOffsetU = u;
            this.textureOffsetV = v;
            return this;
        }

        public BetterPartBuilder.CuboidBuilder start(float x, float y, float z) {
            this.posX1 = x;
            this.posY1 = y;
            this.posZ1 = z;
            return this;
        }

        public BetterPartBuilder.CuboidBuilder end(float x, float y, float z) {
            this.posX2 = x;
            this.posY2 = y;
            this.posZ2 = z;
            return this;
        }

        public BetterPartBuilder.CuboidBuilder size(float x, float y, float z) {
            this.posX2 = posX1 + x;
            this.posY2 = posY1 + y;
            this.posZ2 = posZ1 + z;
            return this;
        }

        public BetterPartBuilder.CuboidBuilder shift(float x, float y, float z) {
            posX1 = posX1 - x;
            posY1 = posY1 - y;
            posZ1 = posZ1 - z;
            posX2 = posX2 - x;
            posY2 = posY2 - y;
            posZ2 = posZ2 - z;
            return this;
        }

        public BetterPartBuilder.CuboidBuilder rotate(float x, float y, float z) {
            useRotation = true;
            this.rotationX = x;
            this.rotationY = y;
            this.rotationZ = z;
            return this;
        }

        public BetterPartBuilder.CuboidBuilder rotateX(float x) {
            useRotation = true;
            this.rotationX = x;
            return this;
        }

        public BetterPartBuilder.CuboidBuilder rotateY(float y) {
            useRotation = true;
            this.rotationY = y;
            return this;
        }

        public BetterPartBuilder.CuboidBuilder rotateZ(float z) {
            useRotation = true;
            this.rotationZ = z;
            return this;
        }

        public BetterPartBuilder.CuboidBuilder sprite(Sprite sprite) {
            this.sprite = sprite;
            return this;
        }

        public BetterPartBuilder.CuboidBuilder pivot(float pivotX, float pivotY, float pivotZ) {
            usePivot = true;
            this.pivotX = pivotX;
            this.pivotY = pivotY;
            this.pivotZ = pivotZ;
            return this;
        }

        /**
         * Pulls the cuboid "inside out" through the Y and Z axes.
         */
        public BetterPartBuilder.CuboidBuilder invertYZ() {
            this.invertYZ = true;
            return this;
        }

        public BetterPartBuilder endCuboid() {
            return partBuilder.addCuboid(this);
        }

        public int vertices() {
            return visibleFaces.size() * 4;
        }

        public void buffer(PosTexNormalWriterUnsafe buffer) {

            float sizeX = posX2 - posX1;
            float sizeY = posY2 - posY1;
            float sizeZ = posZ2 - posZ1;

            float posX1 = this.posX1 / 16f;
            float posY1 = this.posY1 / 16f;
            float posZ1 = this.posZ1 / 16f;
            float posX2 = this.posX2 / 16f;
            float posY2 = this.posY2 / 16f;
            float posZ2 = this.posZ2 / 16f;


            Vector4f lll = new Vector4f(posX1, posY1, posZ1, 1);
            Vector4f hll = new Vector4f(posX2, posY1, posZ1, 1);
            Vector4f hhl = new Vector4f(posX2, posY2, posZ1, 1);
            Vector4f lhl = new Vector4f(posX1, posY2, posZ1, 1);
            Vector4f llh = new Vector4f(posX1, posY1, posZ2, 1);
            Vector4f hlh = new Vector4f(posX2, posY1, posZ2, 1);
            Vector4f hhh = new Vector4f(posX2, posY2, posZ2, 1);
            Vector4f lhh = new Vector4f(posX1, posY2, posZ2, 1);

            Vector4f down = new Vector4f(Direction.DOWN.getUnitVector(), 0f);
            Vector4f up = new Vector4f(Direction.UP.getUnitVector(), 0f);
            Vector4f west = new Vector4f(Direction.WEST.getUnitVector(), 0f);
            Vector4f north = new Vector4f(Direction.NORTH.getUnitVector(), 0f);
            Vector4f east = new Vector4f(Direction.EAST.getUnitVector(), 0f);
            Vector4f south = new Vector4f(Direction.SOUTH.getUnitVector(), 0f);

            //The tranformation matrix with pivot basically
            //moves the pivot to the origin, does the rotation, and moves the origin back
            //
            if (useRotation) {
                Matrix4f transformationMatrix = new Matrix4f();

                if (usePivot) {
                    transformationMatrix.translate(new Vector3f(pivotX, pivotY, pivotZ).mul(1f/16f));
                }

                transformationMatrix.rotateXYZ(
                    (float) Math.toRadians(rotationX),
                    (float) Math.toRadians(rotationY),
                    (float) Math.toRadians(rotationZ)
                );
                if (usePivot) {
                    transformationMatrix.translate(new Vector3f(pivotX, pivotY, pivotZ).mul(1f/16f).mul(-1));
                }

                lll.mul(transformationMatrix);
                hll.mul(transformationMatrix);
                hhl.mul(transformationMatrix);
                lhl.mul(transformationMatrix);
                llh.mul(transformationMatrix);
                hlh.mul(transformationMatrix);
                hhh.mul(transformationMatrix);
                lhh.mul(transformationMatrix);
                down.mul(transformationMatrix);
                up.mul(transformationMatrix);
                west.mul(transformationMatrix);
                north.mul(transformationMatrix);
                east.mul(transformationMatrix);
                south.mul(transformationMatrix);
            }

            float f4 = getU((float)textureOffsetU);
            float f5 = getU((float)textureOffsetU + sizeZ);
            float f6 = getU((float)textureOffsetU + sizeZ + sizeX);
            float f7 = getU((float)textureOffsetU + sizeZ + sizeX + sizeX);
            float f8 = getU((float)textureOffsetU + sizeZ + sizeX + sizeZ);
            float f9 = getU((float)textureOffsetU + sizeZ + sizeX + sizeZ + sizeX);
            float f10 = getV((float)textureOffsetV);
            float f11 = getV((float)textureOffsetV + sizeZ);
            float f12 = getV((float)textureOffsetV + sizeZ + sizeY);

            if (invertYZ) {
                quad(buffer, new Vector4f[]{hlh, llh, lll, hll}, f6, f11, f7, f10, down);
                quad(buffer, new Vector4f[]{hhl, lhl, lhh, hhh}, f5, f10, f6, f11, up);
                quad(buffer, new Vector4f[]{lll, llh, lhh, lhl}, f5, f12, f4, f11, west);
                quad(buffer, new Vector4f[]{hll, lll, lhl, hhl}, f9, f12, f8, f11, north);
                quad(buffer, new Vector4f[]{hlh, hll, hhl, hhh}, f8, f12, f6, f11, east);
                quad(buffer, new Vector4f[]{llh, hlh, hhh, lhh}, f6, f12, f5, f11, south);
            } else {
                quad(buffer, new Vector4f[]{hlh, llh, lll, hll}, f5, f10, f6, f11, down);
                quad(buffer, new Vector4f[]{hhl, lhl, lhh, hhh}, f6, f11, f7, f10, up);
                quad(buffer, new Vector4f[]{lll, llh, lhh, lhl}, f4, f11, f5, f12, west);
                quad(buffer, new Vector4f[]{hll, lll, lhl, hhl}, f5, f11, f6, f12, north);
                quad(buffer, new Vector4f[]{hlh, hll, hhl, hhh}, f6, f11, f8, f12, east);
                quad(buffer, new Vector4f[]{llh, hlh, hhh, lhh}, f8, f11, f9, f12, south);
            }
        }

        public void quad(PosTexNormalWriterUnsafe buffer, Vector4f[] vertices, float minU, float minV, float maxU, float maxV, Vector4f normal) {
            buffer.putVertex(vertices[0].x(), vertices[0].y(), vertices[0].z(), normal.x(), normal.y(), normal.z(), maxU, minV);
            buffer.putVertex(vertices[1].x(), vertices[1].y(), vertices[1].z(), normal.x(), normal.y(), normal.z(), minU, minV);
            buffer.putVertex(vertices[2].x(), vertices[2].y(), vertices[2].z(), normal.x(), normal.y(), normal.z(), minU, maxV);
            buffer.putVertex(vertices[3].x(), vertices[3].y(), vertices[3].z(), normal.x(), normal.y(), normal.z(), maxU, maxV);

        }

        public float getU(float u) {
            if (sprite != null)
                return sprite.getFrameU(u * 16 / partBuilder.sizeU);
            else
                return u / partBuilder.sizeU;
        }

        public float getV(float v) {
            if (sprite != null)
                return sprite.getFrameV(v * 16 / partBuilder.sizeV);
            else
                return v / partBuilder.sizeV;
        }
    }
}
