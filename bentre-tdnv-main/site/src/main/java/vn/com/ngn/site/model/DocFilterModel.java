package vn.com.ngn.site.model;

public class DocFilterModel {
	private int skip;
	private int limit;
	private long formdate;
	private long todate;
	private String docCategory;
	private String accountDomino;
	private String keyword;
	private String docFroms;
	private String norNameBosses;
	private String norNameG3s;
	
	public String createQueryString() {
		String queryString = "skip="+skip
				+ "&limit="+limit
				+ "&fromDate="+formdate
				+ "&toDate="+todate
				+ "&accountDomino="+accountDomino;
				
		if(docCategory!=null && docCategory.isEmpty()==false) {
			queryString = queryString+ "&docCategory="+docCategory;
		}
				
				
				
		
		if(keyword!=null && !keyword.isEmpty()) {
			queryString+="&keyword="+keyword;
		}
		if(docFroms!=null) {
			queryString+="&findDocFroms="+docFroms;
		}
		if(norNameBosses!=null) {
			queryString+="&findNorNameBosses="+norNameBosses;
		}
		if(norNameG3s!=null) {
			queryString+="&findNorNameG3s="+norNameG3s;
		}
		
		return queryString;
	}
	
	public int getSkip() {
		return skip;
	}
	public void setSkip(int skip) {
		this.skip = skip;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public long getFormdate() {
		return formdate;
	}
	public void setFormdate(long formdate) {
		this.formdate = formdate;
	}
	public long getTodate() {
		return todate;
	}
	public void setTodate(long todate) {
		this.todate = todate;
	}
	public String getDocCategory() {
		return docCategory;
	}
	public void setDocCategory(String docCategory) {
		this.docCategory = docCategory;
	}
	public String getAccountDomino() {
		return accountDomino;
	}
	public void setAccountDomino(String accountDomino) {
		this.accountDomino = accountDomino;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
}
