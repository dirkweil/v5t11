package de.gedoplan.v5t11.util.jsf;

import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import java.io.Serializable;

import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuItem.Builder;
import org.primefaces.model.menu.MenuItem;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NavigationItem implements Comparable<NavigationItem>, Serializable {

  @JsonbInclude
  private String name;
  @JsonbInclude
  private String category;
  @JsonbInclude
  private String url;
  @JsonbInclude
  private String icon;

  @JsonbInclude
  int order;

  public NavigationItem(String name, String category, String url, String icon, int order) {
    this.name = name;
    this.category = category;
    this.url = url;
    this.icon = icon != null ? icon : "fa fa-circle";
    this.order = order;
  }

  public MenuItem toMenuItem(boolean disabled) {
    Builder builder = DefaultMenuItem.builder()
        .value(this.name)
        .icon(this.icon)
        .disabled(disabled)
        .ajax(false);
    if (this.url != null) {
      builder.url(this.url);
    }
    return builder
        .build();
  }

  @Override
  public int compareTo(NavigationItem other) {
    int diff = Integer.compare(this.order, other.order);
    if (diff == 0) {
      diff = this.name.compareTo(other.name);
    }
    if (diff == 0) {
      diff = this.category.compareTo(other.category);
    }
    return diff;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.category == null) ? 0 : this.category.hashCode());
    result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    NavigationItem other = (NavigationItem) obj;
    if (this.category == null) {
      if (other.category != null) {
        return false;
      }
    } else if (!this.category.equals(other.category)) {
      return false;
    }
    if (this.name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!this.name.equals(other.name)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "NavigationItem{name=" + this.name + ", category=" + this.category + ", url=" + this.url + "}";
  }
}