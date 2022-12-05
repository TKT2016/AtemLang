package atem.compiler.analyzers;

public class SearchKinds {
    /** 是否搜索类型 */
    public boolean isSearchType;
    /** 是否搜索变量(包括字段) */
    public boolean isSearchVar;
    /** 是否搜索方法 */
  //  public boolean isSearchMethod;

    public SearchKinds(boolean isSearchType, boolean isSearchVar)//, boolean isSearchMethod)
    {
        this.isSearchType = isSearchType;
        this.isSearchVar = isSearchVar;
        //this.isSearchMethod = isSearchMethod;
    }

    public SearchKinds(   )
    {
        this.isSearchVar = true;
    }
}
