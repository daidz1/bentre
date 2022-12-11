package ws.core.module.ioffice;

public enum TrangthaiIOffice {
	CHUAGUI(0,1),
	DAGUIVANHANTHANHCONG(1,1),
	LOIKHINHAN(2,0);
	
	private int GET=0;
	private int UPDATE=0;
	
	private TrangthaiIOffice(int GET, int UPDATE){
		this.GET = GET;
		this.UPDATE = UPDATE;
	}

	public int getGET() {
		return GET;
	}

	public void setGET(int gET) {
		GET = gET;
	}

	public int getUPDATE() {
		return UPDATE;
	}

	public void setUPDATE(int uPDATE) {
		UPDATE = uPDATE;
	}
}
