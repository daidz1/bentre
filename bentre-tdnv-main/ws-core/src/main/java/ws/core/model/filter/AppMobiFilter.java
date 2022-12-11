package ws.core.model.filter;

public class AppMobiFilter {
	public String _id=null;
	public String userId=null;
	public String deviceId=null;
	public String keySearch=null;
	public long fromDate=0;
	public long toDate=0;
	public String active=null;
	
	public AppMobiFilter() {
		
	}

	@Override
	public String toString() {
		return "AppMobiFilter [_id=" + _id + ", userId=" + userId + ", deviceId=" + deviceId + ", keySearch="
				+ keySearch + ", fromDate=" + fromDate + ", toDate=" + toDate + ", active=" + active + "]";
	}
}
