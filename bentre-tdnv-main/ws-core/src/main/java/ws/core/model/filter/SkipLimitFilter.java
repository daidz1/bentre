package ws.core.model.filter;

public class SkipLimitFilter {
	public int skip=0;
	public int limit=0;
	
	public SkipLimitFilter() {

	}

	public SkipLimitFilter(int skip, int limit) {
		super();
		this.skip = skip;
		this.limit = limit;
	}
}
