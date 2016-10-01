package models;

import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by raj on 30/9/16.
 */
@Entity
public class Shippable extends Model {

  @Id
  private int id;
  private String state;
  private String created_at;
  private String updated_at;
  private String closed_at;

  public String getState() { return state; }

  public void setState(String state) { this.state = state; }

  public String getCreated_at() { return created_at; }

  public void setCreated_at(String created_at) { this.created_at = created_at; }

  public String getUpdated_at() { return updated_at; }

  public void setUpdated_at(String updated_at) { this.updated_at = updated_at; }

  public String getClosed_at() { return closed_at; }

  public void setClosed_at(String closed_at) { this.closed_at = closed_at; }

  @Override
  public String toString() {
    return "Shippable{" +
        "state='" + state + '\'' +
        ", created_at='" + created_at + '\'' +
        ", updated_at='" + updated_at + '\'' +
        ", closed_at='" + closed_at + '\'' +
        '}';
  }
}
