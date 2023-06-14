package io.camunda.tasklist.dto;

import java.util.ArrayList;
import java.util.List;

import io.generated.tasklist.client.type.Sort;
import io.generated.tasklist.client.type.TaskOrderBy;
import io.generated.tasklist.client.type.TaskSortFields;

public class Pagination {

  private Integer pageSize;

  private List<String> search;

  private SearchType searchType;
  
  private List<TaskOrderBy> sort;

  public Integer getPageSize() {
    return pageSize;
  }

  public Pagination setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
    return this;
  }

  public List<String> getSearch() {
    return search;
  }

  public Pagination setSearch(List<String> search) {
    this.search = search;
    return this;
  }

  public SearchType getSearchType() {
    return searchType;
  }

  public Pagination setSearchType(SearchType searchType) {
    this.searchType = searchType;
    return this;
  }
  
  public List<TaskOrderBy> getSort() {
    return sort;
  }

  public Pagination setSort(List<TaskOrderBy> sort) {
    this.sort = sort;
    return this;
  }



  public static class Builder {

    private Integer pageSize;

    private List<String> search;

    private SearchType searchType;
    
    private List<TaskOrderBy> sort;

    public Builder pageSize(Integer pageSize) {
      this.pageSize = pageSize;
      return this;
    }

    public Builder after(List<String> search) {
      this.search = search;
      this.searchType = SearchType.AFTER;
      return this;
    }

    public Builder before(List<String> search) {
      this.search = search;
      this.searchType = SearchType.BEFORE;
      return this;
    }

    public Builder afterOrEqual(List<String> search) {
      this.search = search;
      this.searchType = SearchType.AFTER_OR_EQUAL;
      return this;
    }
    
    public Builder sortBy(TaskSortFields field, Sort order) {
      if (sort==null) {
        sort = new ArrayList<>();
      }
      sort.add(new TaskOrderBy(field, order));
      return this;
    }

    public Pagination build() {
      return new Pagination().setPageSize(pageSize).setSearch(search).setSearchType(searchType).setSort(sort);
    }
  }

}