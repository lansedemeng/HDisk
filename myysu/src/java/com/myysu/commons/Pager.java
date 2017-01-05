import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 公共分页方法
 * 
 * @author wangmengjie@jiuxian.com
 * @param <T>
 */
public class Pager<T> implements Serializable {
	private static final long serialVersionUID = -2520186053911450011L;
	/** 当前页码 */
	private int pageNum = 1;
	/** 每页显示记录条数 */
	private int pageSize = Constans.DEFAULT_PAGE_SIZE;
	/** 总记录数 */
	private int recordCount = 0;
	/** 记录list */
	private List<T> recordList = new ArrayList<T>();

	public Pager() {

	}

	public Pager(int recordCount) {
		this.recordCount = recordCount;
	}

	public Pager(int recordCount, int pageSize) {
		this.recordCount = recordCount;
		this.pageSize = pageSize;
	}

	public int getPageCount() {
		int pageCount = recordCount / pageSize;
		if (recordCount % pageSize > 0) {
			pageCount++;
		}
		if (pageCount == 0) {
			pageCount = 1;
		}
		return pageCount;
	}

	public int getStartRecord() {
		int startRecord = pageSize * (pageNum - 1);
		return startRecord;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		if (pageNum < 1) {
			this.pageNum = 1;
		} else if (pageNum > getPageCount()) {
			this.pageNum = getPageCount();
		} else {
			this.pageNum = pageNum;
		}
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(int recordCount) {
		this.recordCount = recordCount;
	}

	public List<T> getRecordList() {
		return recordList;
	}

	public void setRecordList(List<T> recordList) {
		this.recordList = recordList;
	}

}
