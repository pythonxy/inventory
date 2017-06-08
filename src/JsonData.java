public class JsonData {
	private int id;
	private String name;
	private String part;
	private String material;
	private String batch;
	private int qty;

	public JsonData(int id, String name, String part, String material, String batch, int qty) {
		this.id = id;
		this.name = name;
		this.part = part;
		this.material = material;
		this.batch = batch;
		this.qty = qty;
	}
}
