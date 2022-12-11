package ws.core.model.fields;

import org.springframework.data.mongodb.core.mapping.Field;

public class Config {
	@Field(value = "dagiaoview")
	public ConfigDagiao dagiaoview;
	
	@Field(value = "duocgiaoview")
	public ConfigDuocgiao duocgiaoview;
	
	@Field(value = "hotroview")
	public ConfigHotro hotroview;
	
	public Config() {
		this.dagiaoview=new ConfigDagiao();
		this.duocgiaoview=new ConfigDuocgiao();
		this.hotroview=new ConfigHotro();
	}
	
	public class ConfigDagiao{
		@Field(value = "display_assignee_org")
		public boolean display_assignee_org = true;
		
		@Field(value = "expand_task_info")
		public boolean expand_task_info = false;
	}
	
	public class ConfigDuocgiao{
		@Field(value = "display_owner_org")
		public boolean display_owner_org = true;
		
		@Field(value = "expand_task_info")
		public boolean expand_task_info = false;
	}
	
	public class ConfigHotro{
		@Field(value = "display_assignee_org")
		public boolean display_assignee_org = true;
		
		@Field(value = "display_owner_org")
		public boolean display_owner_org = true;
		
		@Field(value = "expand_task_info")
		public boolean expand_task_info = false;
	}
}
