package ws.core.enums;

public enum DocCategory {
	CVDEN("FrOfficialIn", "Công văn đến"),
	CVDI("FrOfficialOut", "Công văn đi");
	
	private String key;
	private String name;
	
	DocCategory(String key, String name){
		this.key=key;
		this.name=name;
	}

	public String getKey() {
		return key;
	}

	public String getName() {
		return name;
	}
}
