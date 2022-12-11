package ws.core.module.ioffice;

public enum TaskType {
	Vanbanphathanh(1,"Văn bản phát hành"),
	Chidao(2,"Chỉ đạo");
	
	private int id;
	private String name;
	
	private TaskType(int id,String name){
		this.id=id;
		this.name=name;
	}
	
	public int getId(){
		return id;
	}
	
	public String getName(){
		return this.name;
	}
	
	public TaskType getItem(int id){
		for(TaskType item : values()) {
			if(item.id==id) return item;
		}
		return null;
	}
	
	public String getName(int id){
		for(TaskType item : values()) {
			if(item.id==id) return item.getName();
		}
		return "UNKNOW";
	}
	
	public static boolean isVanbanphathanh(int typeId){
		if(Vanbanphathanh.getId()==typeId)
			return true;
		return false;
	}
	
	public static boolean isChidao(int typeId){
		if(Chidao.getId()==typeId)
			return true;
		return false;
	}
}