package models;

import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
 * Model file for getting the essential part from the json returned from the
 */
@Entity
public class Issue extends Model {

  @Id
  private int id;

  private String state;

  private String created_at;

  @OneToOne
  @JoinColumn(name = "pull_request")
  private PullRequest pull_request;

  public String getState() { return state; }

  public void setState(String state) { this.state = state; }

  public String getCreated_at() { return created_at; }

  public void setCreated_at(String created_at) { this.created_at = created_at;}

  public PullRequest getPull_request() { return pull_request; }

  public void setPull_request(PullRequest pull_request) { this.pull_request = pull_request; }
}
