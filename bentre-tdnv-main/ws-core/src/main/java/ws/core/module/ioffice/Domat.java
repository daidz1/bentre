package ws.core.module.ioffice;

public enum Domat {
	Tuyetmat("Tuyệt mật",3,"#c60000","prioriry-hoatoc"),
	Toimat("Tối mật",2,"#c60000","prioriry-thuongkhan"),
	Mat("Mật",1,"#DAA520","prioriry-khan"),
	Thuong("Thường",0,"#0F90E7","prioriry-thuong");
	
	private int type;
	private String name;
	private String color;
	private String className;
	
	private Domat(String name,int type,String color, String className){
		this.name=name;
		this.type=type;
		this.color=color;
		this.className=className;
	}

	public int getType(){
		return type;
	}
	
	public String getName(){
		return name;
	}
	
	public String getColor(){
		return color;
	}
	
	public String getClassName() {
		return className;
	}

	public static Domat getItem(int type){
		for(Domat item : values()) {
			if(item.type==type) return item;
		}
		return Thuong;
	}
}
