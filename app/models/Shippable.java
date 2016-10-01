package models;

import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

/**
 * Created by raj on 30/9/16.
 */
@Entity
public class Shippable extends Model {

  @Id
  public int id;
  public String state;
  @JoinColumn(name = "created_at")
  public String createdAt;
  @JoinColumn(name = "updated_at")
  public String updatedAt;
  @JoinColumn(name = "closed_at")
  public String closedAt;
  public String getState() { return state; }

  public void setState(String state) { this.state = state; }

  public String getCreatedAt() { return createdAt; }

  public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

  public String getUpdatedAt() { return updatedAt; }

  public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

  public String getClosedAt() { return closedAt; }

  public void setClosedAt(String closedAt) { this.closedAt = closedAt; }

  public static Finder<Long, Shippable> find = new Finder<>(Long.class, Shippable.class);

  @Override
  public String toString() {
    return "Shippable{" +
        "closedAt='" + closedAt + '\'' +
        ", updatedAt='" + updatedAt + '\'' +
        ", createdAt='" + createdAt + '\'' +
        ", state='" + state + '\'' +
        '}';
  }
}
