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
		public boolean display_assignee_org = false;
	}
	
	public class ConfigDuocgiao{
		@Field(value = "display_owner_org")
		public boolean display_owner_org = false;
	}
	
	public class ConfigHotro{
		@Field(value = "display_assignee_org")
		public boolean display_assignee_org = false;
		
		@Field(value = "display_owner_org")
		public boolean display_owner_org = false;
	}
}
