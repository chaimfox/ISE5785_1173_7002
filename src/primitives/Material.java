package primitives;

/**
 * Material class represents the material of a geometry
 */
public class Material {

    /**
     * kA is the diffuse factor
     */
    public Double3 kA = Double3.ONE;
    /**
     * kS is the specular factor
     */
    public Double3 kS = Double3.ZERO;
    /**
     * kT is the transparency factor
     */
    public int nShininess = 0;

    /**
     * Constructor for Material
     * @param kA the diffuse factor
     * @return the material
     */
    public Material setkA(Double3 kA) {
        this.kA = kA;
        return this;
    }

    /**
     * Material setter
     * @param kA the diffuse factor
     * @return the material
     */
    public Material setkA(double kA) {
        this.kA = new Double3(kA);
        return this;
    }

    /**
     * Material setter
     * @param kS the specular factor
     * @return the material
     */
    public Material setKs(Double3 kS) {
        this.kS = kS;
        return this;
    }

    /**
     * Material setter
     * @param kS the specular factor
     * @return the material
     */
    public Material setKs(double kS) {
        this.kS = new Double3(kS);
        return this;
    }

    /**
     * Material setter
     * @param nShininess the shininess factor
     * @return the material
     */
    public Material setShininess(int nShininess) {
        this.nShininess = nShininess;
        return this;
    }
}