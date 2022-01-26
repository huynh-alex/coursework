public class Vector {
	
	private int dim;
	public float[] coordinates;
	
	public Vector(int dim) {
		dim = this.dim;
		coordinates = new float[dim];
		for(int i = 0; i < dim; i++)
			coordinates[i] = 0;
	}
		
	public Vector(float[] v) {
		dim = v.length;
		this.coordinates = new float[dim];
		for(int i = 0; i < dim; i++)
			coordinates[i] = v[i];
	}
	
	public Vector(Vector v){
		dim = v.getDim();
		for (int i = 0; i < dim; i++) {
			coordinates[i] = v.getIthCoor(i);
		}
	}
	
	public int getDim() {
		return dim;
	}
	
	public float getLength() {
		double l = 0;
		for(int i = 0; i < dim; i++) {
			l += (coordinates[i] * coordinates[i]);
		}
		return (float) Math.sqrt(l);
	}
	
	
	public float getIthCoor(int i) {
		return coordinates[i];
	}
	
	public float distance(Vector v) {
		double l = 0;
		for(int i = 0; i < dim; i++) {
			l = l + ((coordinates[i] - v.coordinates[i]) * (coordinates[i] - v.coordinates[i]));
		}
		return (float)Math.sqrt(l);
	}
}
