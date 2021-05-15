package me.travis.wurstplusthree.util.elements;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Matrix4f;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public final class GLUProjection {
    private static GLUProjection instance;
    private IntBuffer viewport;
    private FloatBuffer modelview;
    private FloatBuffer projection;
    private final FloatBuffer coords = BufferUtils.createFloatBuffer(3);
    private Vector3D frustumPos;
    private Vector3D[] frustum;
    private Vector3D[] invFrustum;
    private Vector3D viewVec;
    private double displayWidth;
    private double displayHeight;
    private double widthScale;
    private double heightScale;
    private double bra;
    private double bla;
    private double tra;
    private double tla;
    private Line tb;
    private Line bb;
    private Line lb;
    private Line rb;
    private float fovY;
    private float fovX;
    private Vector3D lookVec;

    private GLUProjection() {
    }

    public static GLUProjection getInstance() {
        if (instance == null) {
            instance = new GLUProjection();
        }
        return instance;
    }

    public void updateMatrices(IntBuffer viewport, FloatBuffer modelview, FloatBuffer projection, double widthScale, double heightScale) {
        float fov;
        this.viewport = viewport;
        this.modelview = modelview;
        this.projection = projection;
        this.widthScale = widthScale;
        this.heightScale = heightScale;
        this.fovY = fov = (float) Math.toDegrees(Math.atan(1.0 / (double) this.projection.get(5)) * 2.0);
        this.displayWidth = this.viewport.get(2);
        this.displayHeight = this.viewport.get(3);
        this.fovX = (float) Math.toDegrees(2.0 * Math.atan(this.displayWidth / this.displayHeight * Math.tan(Math.toRadians(this.fovY) / 2.0)));
        Vector3D lv = new Vector3D(this.modelview.get(0), this.modelview.get(1), this.modelview.get(2));
        Vector3D uv = new Vector3D(this.modelview.get(4), this.modelview.get(5), this.modelview.get(6));
        Vector3D fv = new Vector3D(this.modelview.get(8), this.modelview.get(9), this.modelview.get(10));
        Vector3D nuv = new Vector3D(0.0, 1.0, 0.0);
        Vector3D nlv = new Vector3D(1.0, 0.0, 0.0);
        double yaw = Math.toDegrees(Math.atan2(nlv.cross(lv).length(), nlv.dot(lv))) + 180.0;
        if (fv.x < 0.0) {
            yaw = 360.0 - yaw;
        }
        double pitch = 0.0;
        pitch = -fv.y > 0.0 && yaw >= 90.0 && yaw < 270.0 || fv.y > 0.0 && (!(yaw >= 90.0) || !(yaw < 270.0)) ? Math.toDegrees(Math.atan2(nuv.cross(uv).length(), nuv.dot(uv))) : -Math.toDegrees(Math.atan2(nuv.cross(uv).length(), nuv.dot(uv)));
        this.lookVec = this.getRotationVector(yaw, pitch);
        Matrix4f modelviewMatrix = new Matrix4f();
        modelviewMatrix.load(this.modelview.asReadOnlyBuffer());
        modelviewMatrix.invert();
        this.frustumPos = new Vector3D(modelviewMatrix.m30, modelviewMatrix.m31, modelviewMatrix.m32);
        this.frustum = this.getFrustum(this.frustumPos.x, this.frustumPos.y, this.frustumPos.z, yaw, pitch, fov, 1.0, this.displayWidth / this.displayHeight);
        this.invFrustum = this.getFrustum(this.frustumPos.x, this.frustumPos.y, this.frustumPos.z, yaw - 180.0, -pitch, fov, 1.0, this.displayWidth / this.displayHeight);
        this.viewVec = this.getRotationVector(yaw, pitch).normalized();
        this.bra = Math.toDegrees(Math.acos(this.displayHeight * heightScale / Math.sqrt(this.displayWidth * widthScale * this.displayWidth * widthScale + this.displayHeight * heightScale * this.displayHeight * heightScale)));
        this.bla = 360.0 - this.bra;
        this.tra = this.bla - 180.0;
        this.tla = this.bra + 180.0;
        this.rb = new Line(this.displayWidth * this.widthScale, 0.0, 0.0, 0.0, 1.0, 0.0);
        this.tb = new Line(0.0, 0.0, 0.0, 1.0, 0.0, 0.0);
        this.lb = new Line(0.0, 0.0, 0.0, 0.0, 1.0, 0.0);
        this.bb = new Line(0.0, this.displayHeight * this.heightScale, 0.0, 1.0, 0.0, 0.0);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public Projection project(double x, double y, double z, ClampMode clampModeOutside, boolean extrudeInverted) {
        boolean outsideFrustum;
        if (this.viewport == null || this.modelview == null || this.projection == null)
            return new Projection(0.0, 0.0, Projection.Type.FAIL);
        Vector3D posVec = new Vector3D(x, y, z);
        boolean[] frustum = this.doFrustumCheck(this.frustum, this.frustumPos, x, y, z);
        outsideFrustum = frustum[0] || frustum[1] || frustum[2] || frustum[3];
        if (outsideFrustum) {
            boolean outsideInvertedFrustum;
            boolean opposite = posVec.sub(this.frustumPos).dot(this.viewVec) <= 0.0;
            boolean[] invFrustum = this.doFrustumCheck(this.invFrustum, this.frustumPos, x, y, z);
            outsideInvertedFrustum = invFrustum[0] || invFrustum[1] || invFrustum[2] || invFrustum[3];
            if (extrudeInverted && !outsideInvertedFrustum || outsideInvertedFrustum && clampModeOutside != ClampMode.NONE) {
                if (extrudeInverted && !outsideInvertedFrustum || clampModeOutside == ClampMode.DIRECT && outsideInvertedFrustum) {
                    double vecX = 0.0;
                    double vecY = 0.0;
                    if (!GLU.gluProject((float) x, (float) y, (float) z, this.modelview, this.projection, this.viewport, this.coords))
                        return new Projection(0.0, 0.0, Projection.Type.FAIL);
                    if (opposite) {
                        vecX = this.displayWidth * this.widthScale - (double) this.coords.get(0) * this.widthScale - this.displayWidth * this.widthScale / 2.0;
                        vecY = this.displayHeight * this.heightScale - (this.displayHeight - (double) this.coords.get(1)) * this.heightScale - this.displayHeight * this.heightScale / 2.0;
                    } else {
                        vecX = (double) this.coords.get(0) * this.widthScale - this.displayWidth * this.widthScale / 2.0;
                        vecY = (this.displayHeight - (double) this.coords.get(1)) * this.heightScale - this.displayHeight * this.heightScale / 2.0;
                    }
                    Vector3D vec = new Vector3D(vecX, vecY, 0.0).snormalize();
                    vecX = vec.x;
                    vecY = vec.y;
                    Line vectorLine = new Line(this.displayWidth * this.widthScale / 2.0, this.displayHeight * this.heightScale / 2.0, 0.0, vecX, vecY, 0.0);
                    double angle = Math.toDegrees(Math.acos(vec.y / Math.sqrt(vec.x * vec.x + vec.y * vec.y)));
                    if (vecX < 0.0) {
                        angle = 360.0 - angle;
                    }
                    Vector3D intersect = new Vector3D(0.0, 0.0, 0.0);
                    intersect = angle >= this.bra && angle < this.tra ? this.rb.intersect(vectorLine) : (angle >= this.tra && angle < this.tla ? this.tb.intersect(vectorLine) : (angle >= this.tla && angle < this.bla ? this.lb.intersect(vectorLine) : this.bb.intersect(vectorLine)));
                    return new Projection(intersect.x, intersect.y, outsideInvertedFrustum ? Projection.Type.OUTSIDE : Projection.Type.INVERTED);
                }
                if (clampModeOutside != ClampMode.ORTHOGONAL || !outsideInvertedFrustum)
                    return new Projection(0.0, 0.0, Projection.Type.FAIL);
                if (!GLU.gluProject((float) x, (float) y, (float) z, this.modelview, this.projection, this.viewport, this.coords))
                    return new Projection(0.0, 0.0, Projection.Type.FAIL);
                double guiX = (double) this.coords.get(0) * this.widthScale;
                double guiY = (this.displayHeight - (double) this.coords.get(1)) * this.heightScale;
                if (opposite) {
                    guiX = this.displayWidth * this.widthScale - guiX;
                    guiY = this.displayHeight * this.heightScale - guiY;
                }
                if (guiX < 0.0) {
                    guiX = 0.0;
                } else if (guiX > this.displayWidth * this.widthScale) {
                    guiX = this.displayWidth * this.widthScale;
                }
                if (guiY < 0.0) {
                    guiY = 0.0;
                    return new Projection(guiX, guiY, outsideInvertedFrustum ? Projection.Type.OUTSIDE : Projection.Type.INVERTED);
                } else {
                    if (!(guiY > this.displayHeight * this.heightScale))
                        return new Projection(guiX, guiY, outsideInvertedFrustum ? Projection.Type.OUTSIDE : Projection.Type.INVERTED);
                    guiY = this.displayHeight * this.heightScale;
                }
                return new Projection(guiX, guiY, outsideInvertedFrustum ? Projection.Type.OUTSIDE : Projection.Type.INVERTED);
            }
            if (!GLU.gluProject((float) x, (float) y, (float) z, this.modelview, this.projection, this.viewport, this.coords))
                return new Projection(0.0, 0.0, Projection.Type.FAIL);
            double guiX = (double) this.coords.get(0) * this.widthScale;
            double guiY = (this.displayHeight - (double) this.coords.get(1)) * this.heightScale;
            if (!opposite)
                return new Projection(guiX, guiY, outsideInvertedFrustum ? Projection.Type.OUTSIDE : Projection.Type.INVERTED);
            guiX = this.displayWidth * this.widthScale - guiX;
            guiY = this.displayHeight * this.heightScale - guiY;
            return new Projection(guiX, guiY, outsideInvertedFrustum ? Projection.Type.OUTSIDE : Projection.Type.INVERTED);
        }
        if (!GLU.gluProject((float) x, (float) y, (float) z, this.modelview, this.projection, this.viewport, this.coords))
            return new Projection(0.0, 0.0, Projection.Type.FAIL);
        double guiX = (double) this.coords.get(0) * this.widthScale;
        double guiY = (this.displayHeight - (double) this.coords.get(1)) * this.heightScale;
        return new Projection(guiX, guiY, Projection.Type.INSIDE);
    }

    public boolean[] doFrustumCheck(Vector3D[] frustumCorners, Vector3D frustumPos, double x, double y, double z) {
        Vector3D point = new Vector3D(x, y, z);
        boolean c1 = this.crossPlane(new Vector3D[]{frustumPos, frustumCorners[3], frustumCorners[0]}, point);
        boolean c2 = this.crossPlane(new Vector3D[]{frustumPos, frustumCorners[0], frustumCorners[1]}, point);
        boolean c3 = this.crossPlane(new Vector3D[]{frustumPos, frustumCorners[1], frustumCorners[2]}, point);
        boolean c4 = this.crossPlane(new Vector3D[]{frustumPos, frustumCorners[2], frustumCorners[3]}, point);
        return new boolean[]{c1, c2, c3, c4};
    }

    public boolean crossPlane(Vector3D[] plane, Vector3D point) {
        Vector3D z = new Vector3D(0.0, 0.0, 0.0);
        Vector3D e0 = plane[1].sub(plane[0]);
        Vector3D e1 = plane[2].sub(plane[0]);
        Vector3D normal = e0.cross(e1).snormalize();
        double D = z.sub(normal).dot(plane[2]);
        double dist = normal.dot(point) + D;
        return dist >= 0.0;
    }

    public Vector3D[] getFrustum(double x, double y, double z, double rotationYaw, double rotationPitch, double fov, double farDistance, double aspectRatio) {
        double hFar = 2.0 * Math.tan(Math.toRadians(fov / 2.0)) * farDistance;
        double wFar = hFar * aspectRatio;
        Vector3D view = this.getRotationVector(rotationYaw, rotationPitch).snormalize();
        Vector3D up = this.getRotationVector(rotationYaw, rotationPitch - 90.0).snormalize();
        Vector3D right = this.getRotationVector(rotationYaw + 90.0, 0.0).snormalize();
        Vector3D camPos = new Vector3D(x, y, z);
        Vector3D view_camPos_product = view.add(camPos);
        Vector3D fc = new Vector3D(view_camPos_product.x * farDistance, view_camPos_product.y * farDistance, view_camPos_product.z * farDistance);
        Vector3D topLeftfrustum = new Vector3D(fc.x + up.x * hFar / 2.0 - right.x * wFar / 2.0, fc.y + up.y * hFar / 2.0 - right.y * wFar / 2.0, fc.z + up.z * hFar / 2.0 - right.z * wFar / 2.0);
        Vector3D downLeftfrustum = new Vector3D(fc.x - up.x * hFar / 2.0 - right.x * wFar / 2.0, fc.y - up.y * hFar / 2.0 - right.y * wFar / 2.0, fc.z - up.z * hFar / 2.0 - right.z * wFar / 2.0);
        Vector3D topRightfrustum = new Vector3D(fc.x + up.x * hFar / 2.0 + right.x * wFar / 2.0, fc.y + up.y * hFar / 2.0 + right.y * wFar / 2.0, fc.z + up.z * hFar / 2.0 + right.z * wFar / 2.0);
        Vector3D downRightfrustum = new Vector3D(fc.x - up.x * hFar / 2.0 + right.x * wFar / 2.0, fc.y - up.y * hFar / 2.0 + right.y * wFar / 2.0, fc.z - up.z * hFar / 2.0 + right.z * wFar / 2.0);
        return new Vector3D[]{topLeftfrustum, downLeftfrustum, downRightfrustum, topRightfrustum};
    }

    public Vector3D[] getFrustum() {
        return this.frustum;
    }

    public float getFovX() {
        return this.fovX;
    }

    public float getFovY() {
        return this.fovY;
    }

    public Vector3D getLookVector() {
        return this.lookVec;
    }

    public Vector3D getRotationVector(double rotYaw, double rotPitch) {
        double c = Math.cos(-rotYaw * 0.01745329238474369 - Math.PI);
        double s = Math.sin(-rotYaw * 0.01745329238474369 - Math.PI);
        double nc = -Math.cos(-rotPitch * 0.01745329238474369);
        double ns = Math.sin(-rotPitch * 0.01745329238474369);
        return new Vector3D(s * nc, ns, c * nc);
    }

    public enum ClampMode {
        ORTHOGONAL,
        DIRECT,
        NONE

    }

    public static class Projection {
        private final double x;
        private final double y;
        private final Type t;

        public Projection(double x, double y, Type t) {
            this.x = x;
            this.y = y;
            this.t = t;
        }

        public double getX() {
            return this.x;
        }

        public double getY() {
            return this.y;
        }

        public Type getType() {
            return this.t;
        }

        public boolean isType(Type type) {
            return this.t == type;
        }

        public enum Type {
            INSIDE,
            OUTSIDE,
            INVERTED,
            FAIL

        }
    }

    public static class Vector3D {
        public double x;
        public double y;
        public double z;

        public Vector3D(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Vector3D add(Vector3D v) {
            return new Vector3D(this.x + v.x, this.y + v.y, this.z + v.z);
        }

        public Vector3D add(double x, double y, double z) {
            return new Vector3D(this.x + x, this.y + y, this.z + z);
        }

        public Vector3D sub(Vector3D v) {
            return new Vector3D(this.x - v.x, this.y - v.y, this.z - v.z);
        }

        public Vector3D sub(double x, double y, double z) {
            return new Vector3D(this.x - x, this.y - y, this.z - z);
        }

        public Vector3D normalized() {
            double len = Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
            return new Vector3D(this.x / len, this.y / len, this.z / len);
        }

        public double dot(Vector3D v) {
            return this.x * v.x + this.y * v.y + this.z * v.z;
        }

        public Vector3D cross(Vector3D v) {
            return new Vector3D(this.y * v.z - this.z * v.y, this.z * v.x - this.x * v.z, this.x * v.y - this.y * v.x);
        }

        public Vector3D mul(double m) {
            return new Vector3D(this.x * m, this.y * m, this.z * m);
        }

        public Vector3D div(double d) {
            return new Vector3D(this.x / d, this.y / d, this.z / d);
        }

        public double length() {
            return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
        }

        public Vector3D sadd(Vector3D v) {
            this.x += v.x;
            this.y += v.y;
            this.z += v.z;
            return this;
        }

        public Vector3D sadd(double x, double y, double z) {
            this.x += x;
            this.y += y;
            this.z += z;
            return this;
        }

        public Vector3D ssub(Vector3D v) {
            this.x -= v.x;
            this.y -= v.y;
            this.z -= v.z;
            return this;
        }

        public Vector3D ssub(double x, double y, double z) {
            this.x -= x;
            this.y -= y;
            this.z -= z;
            return this;
        }

        public Vector3D snormalize() {
            double len = Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
            this.x /= len;
            this.y /= len;
            this.z /= len;
            return this;
        }

        public Vector3D scross(Vector3D v) {
            this.x = this.y * v.z - this.z * v.y;
            this.y = this.z * v.x - this.x * v.z;
            this.z = this.x * v.y - this.y * v.x;
            return this;
        }

        public Vector3D smul(double m) {
            this.x *= m;
            this.y *= m;
            this.z *= m;
            return this;
        }

        public Vector3D sdiv(double d) {
            this.x /= d;
            this.y /= d;
            this.z /= d;
            return this;
        }

        public String toString() {
            return "(X: " + this.x + " Y: " + this.y + " Z: " + this.z + ")";
        }
    }

    public static class Line {
        public Vector3D sourcePoint = new Vector3D(0.0, 0.0, 0.0);
        public Vector3D direction = new Vector3D(0.0, 0.0, 0.0);

        public Line(double sx, double sy, double sz, double dx, double dy, double dz) {
            this.sourcePoint.x = sx;
            this.sourcePoint.y = sy;
            this.sourcePoint.z = sz;
            this.direction.x = dx;
            this.direction.y = dy;
            this.direction.z = dz;
        }

        public Vector3D intersect(Line line) {
            double a = this.sourcePoint.x;
            double b = this.direction.x;
            double c = line.sourcePoint.x;
            double d = line.direction.x;
            double e = this.sourcePoint.y;
            double f = this.direction.y;
            double g = line.sourcePoint.y;
            double h = line.direction.y;
            double te = -(a * h - c * h - d * (e - g));
            double be = b * h - d * f;
            if (be == 0.0) {
                return this.intersectXZ(line);
            }
            double t = te / be;
            Vector3D result = new Vector3D(0.0, 0.0, 0.0);
            result.x = this.sourcePoint.x + this.direction.x * t;
            result.y = this.sourcePoint.y + this.direction.y * t;
            result.z = this.sourcePoint.z + this.direction.z * t;
            return result;
        }

        private Vector3D intersectXZ(Line line) {
            double a = this.sourcePoint.x;
            double b = this.direction.x;
            double c = line.sourcePoint.x;
            double d = line.direction.x;
            double e = this.sourcePoint.z;
            double f = this.direction.z;
            double g = line.sourcePoint.z;
            double h = line.direction.z;
            double te = -(a * h - c * h - d * (e - g));
            double be = b * h - d * f;
            if (be == 0.0) {
                return this.intersectYZ(line);
            }
            double t = te / be;
            Vector3D result = new Vector3D(0.0, 0.0, 0.0);
            result.x = this.sourcePoint.x + this.direction.x * t;
            result.y = this.sourcePoint.y + this.direction.y * t;
            result.z = this.sourcePoint.z + this.direction.z * t;
            return result;
        }

        private Vector3D intersectYZ(Line line) {
            double a = this.sourcePoint.y;
            double b = this.direction.y;
            double c = line.sourcePoint.y;
            double d = line.direction.y;
            double e = this.sourcePoint.z;
            double f = this.direction.z;
            double g = line.sourcePoint.z;
            double h = line.direction.z;
            double te = -(a * h - c * h - d * (e - g));
            double be = b * h - d * f;
            if (be == 0.0) {
                return null;
            }
            double t = te / be;
            Vector3D result = new Vector3D(0.0, 0.0, 0.0);
            result.x = this.sourcePoint.x + this.direction.x * t;
            result.y = this.sourcePoint.y + this.direction.y * t;
            result.z = this.sourcePoint.z + this.direction.z * t;
            return result;
        }

        public Vector3D intersectPlane(Vector3D pointOnPlane, Vector3D planeNormal) {
            Vector3D result = new Vector3D(this.sourcePoint.x, this.sourcePoint.y, this.sourcePoint.z);
            double d = pointOnPlane.sub(this.sourcePoint).dot(planeNormal) / this.direction.dot(planeNormal);
            result.sadd(this.direction.mul(d));
            if (this.direction.dot(planeNormal) == 0.0) {
                return null;
            }
            return result;
        }
    }
}

