package ws.core.model.filter;

public class DocFilter {
	public String _id=null;
	public String accountDomino=null;
	public String docCategory=null;
	
	public String findDocFroms=null;
	public String findNorNameBosses=null;
	public String findNorNameG3s=null;
	
	public String keySearch=null;
	public long fromDate=0;
	public long toDate=0;
	public String creatorId=null;
	public String active=null;
	
	public DocFilter() {
		
	}

	@Override
	public String toString() {
		return "DocFilter [_id=" + _id + ", accountDomino=" + accountDomino + ", docCategory=" + docCategory
				+ ", findDocFroms=" + findDocFroms + ", findNorNameBosses=" + findNorNameBosses + ", findNorNameG3s="
				+ findNorNameG3s + ", keySearch=" + keySearch + ", fromDate=" + fromDate + ", toDate=" + toDate
				+ ", creatorId=" + creatorId + ", active=" + active + "]";
	}
}
