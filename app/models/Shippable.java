package models;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by raj on 30/9/16.
 */
@Entity
public class Shippable {

  @Id
  private int id;
  private String state;
  private String createdAt;
  private String updatedAt;
  private String closedAt;

  public String getState() { return state; }

  public void setState(String state) { this.state = state; }

  public String getCreatedAt() { return createdAt; }

  public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

  public String getUpdatedAt() { return updatedAt; }

  public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

  public String getClosedAt() { return closedAt; }

  public void setClosedAt(String closedAt) { this.closedAt = closedAt; }

  @Override
  public String toString() {
    return "Shippable{" +
        "state='" + state + '\'' +
        ", createdAt='" + createdAt + '\'' +
        ", updatedAt='" + updatedAt + '\'' +
        ", closedAt='" + closedAt + '\'' +
        '}';
  }
}
